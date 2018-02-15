package com.kevinmdyer.android.pupalert;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String ACTION_DEVICE_CONNECTED = "deviceConnected";
    private static final String ACTION_DEVICE_DISCONNECTED = "deviceDisconnected";
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_ADDRESS = "address";

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int REQUEST_ENABLE_BT = 1337;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DEVICE_CONNECTED);
        intentFilter.addAction(ACTION_DEVICE_DISCONNECTED);
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(statusReceiver, intentFilter);

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }

        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }

        startLeService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(statusReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_ENABLE_BT == requestCode) {
            if (RESULT_OK == resultCode) {
                startLeService();
            } else {
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLeService();
        } else {
            finish();
        }
    }

    private void startLeService() {
        startService(new Intent(this, BluetoothLeService.class));
    }

    private final BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra(EXTRA_NAME);
            String address = intent.getStringExtra(EXTRA_ADDRESS);
            Log.d(TAG, "onReceive: " + intent.getAction() + " " + address + " " + name);
        }
    };

    static boolean sendDeviceConnected(Context context, BluetoothGatt gatt) {
        BluetoothDevice device = gatt.getDevice();
        Intent intent = new Intent(ACTION_DEVICE_CONNECTED);
        intent.putExtra(EXTRA_NAME, device.getName());
        intent.putExtra(EXTRA_ADDRESS, device.getAddress());
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        return lbm.sendBroadcast(intent);
    }

    static boolean sendDeviceDisconnected(Context context, BluetoothGatt gatt) {
        BluetoothDevice device = gatt.getDevice();
        Intent intent = new Intent(ACTION_DEVICE_DISCONNECTED);
        intent.putExtra(EXTRA_NAME, device.getName());
        intent.putExtra(EXTRA_ADDRESS, device.getAddress());
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        return lbm.sendBroadcast(intent);
    }
}
