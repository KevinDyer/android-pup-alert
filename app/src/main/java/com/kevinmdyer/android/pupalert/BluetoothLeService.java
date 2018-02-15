package com.kevinmdyer.android.pupalert;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class BluetoothLeService extends Service {
    private static final String TAG = BluetoothLeService.class.getSimpleName();

    private static final String ACTION_COMMAND = "command";
    private static final String EXTRA_COMMAND = "command";

    private static final long DELAY_REVISIT_DEVICE = 45 * 1000;

    private final Handler mHandler = new Handler();
    private Map<String, Runnable> addresses = new HashMap<>();
    private BluetoothAdapter mBluetoothAdapter;
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            final String address = device.getAddress();
            if (!addresses.containsKey(address)) {
                Log.d(TAG, "New Bluetooth LE device(" + addresses.size() + "," + rssi + "): '" + address + "' " + device.getName());
                device.connectGatt(BluetoothLeService.this, false, mGattCallback);
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
//                        Log.d(TAG, "Removing " + address + " from addresses");
                        addresses.remove(address);
                    }
                };
                mHandler.postDelayed(runnable, DELAY_REVISIT_DEVICE);
                addresses.put(address, runnable);
            }
        }
    };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String address = gatt.getDevice().getAddress();
            Runnable runnable = addresses.get(address);
            mHandler.removeCallbacks(runnable);
            mHandler.postDelayed(runnable, DELAY_REVISIT_DEVICE);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
                dispatchDeviceConnected(gatt);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                dispatchDeviceDisconnected(gatt);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return;
            }
            String address = gatt.getDevice().getAddress();
            Runnable runnable = addresses.get(address);
            mHandler.removeCallbacks(runnable);
            mHandler.postDelayed(runnable, DELAY_REVISIT_DEVICE);

            boolean disconnect = true;
            Log.d(TAG, "--------" + gatt.getDevice().getAddress() + " " + gatt.getDevice().getName());
            for (BluetoothGattService service : gatt.getServices()) {
                Log.d(TAG, "  svc[" + service.getUuid() + "]: " + Lookup.getServiceFromUUID(service.getUuid()));
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    Log.d(TAG, "    char[" + characteristic.getUuid() + "]");
                    if ((BluetoothGattCharacteristic.PROPERTY_READ & characteristic.getProperties()) != 0) {
                        if ((BluetoothGattCharacteristic.PROPERTY_WRITE & characteristic.getProperties()) != 0) {
                            Log.d(TAG, "      PROPERTY_WRITE");
                        }
                        if ((BluetoothGattCharacteristic.PROPERTY_NOTIFY & characteristic.getProperties()) != 0) {
                            Log.d(TAG, "      PROPERTY_NOTIFY");
                        }
                        Log.d(TAG, "      PROPERTY_READ");
                        final byte[] data = characteristic.getValue();
                        if (data != null && data.length > 0) {
                            final StringBuilder stringBuilder = new StringBuilder(data.length);
                            for(byte byteChar : data) {
                                stringBuilder.append(String.format("%02X ", byteChar));
                            }
                            Log.d(TAG, "    - " + stringBuilder.toString());
                        } else {
                            Log.d(TAG, "    - reading value");
                            if (!disconnect) {
                                gatt.readCharacteristic(characteristic);
                            }
                            disconnect = false;
                        }
                    }

                }
            }
            if (disconnect) {
                gatt.disconnect();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "-------- onCharacteristicRead" + gatt.getDevice().getAddress() + " " + gatt.getDevice().getName());
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "Read characteristic["+ characteristic.getUuid() + "], but not success");
                return;
            }
            Log.i(TAG, "onCharacteristicRead: " + characteristic.getUuid() + " " + characteristic.getPermissions() + " " + characteristic.getProperties());
            String address = gatt.getDevice().getAddress();
            Runnable runnable = addresses.get(address);
            mHandler.removeCallbacks(runnable);
            mHandler.postDelayed(runnable, DELAY_REVISIT_DEVICE);
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                Log.d(TAG, "Got value from char["+ characteristic.getUuid() + "]: " + stringBuilder.toString());
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicChanged: " + characteristic.getValue() + " " + characteristic.getUuid());
        }
    };

    private void dispatchDeviceConnected(BluetoothGatt gatt) {
        MainActivity.sendDeviceConnected(this, gatt);
    }

    private void dispatchDeviceDisconnected(BluetoothGatt gatt) {
        MainActivity.sendDeviceDisconnected(this, gatt);
    }

    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        buildNotification();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(commandReceiver, new IntentFilter(ACTION_COMMAND));

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        boolean startLeScan = mBluetoothAdapter.startLeScan(mLeScanCallback);
        Log.d(TAG, "Started LE scan: " + startLeScan);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(commandReceiver);
    }

    private void buildNotification() {
        final String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        final PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Pup Alert")
                .setContentText("Scanning, tap to cancel")
                .setSmallIcon(R.drawable.ic_scanning)
                .setOngoing(true)
                .setContentIntent(broadcastIntent);
        final Notification noti = builder.build();
        startForeground(1, noti);
    }

    private final BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    private final BroadcastReceiver commandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String command = intent.getStringExtra(EXTRA_COMMAND);
            Log.d(TAG, "Received command: " + command);
        }
    };

    static boolean sendCommand(Context context, String command) {
        Intent intent = new Intent(ACTION_COMMAND);
        intent.putExtra(EXTRA_COMMAND, command);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        return localBroadcastManager.sendBroadcast(intent);
    }
}
