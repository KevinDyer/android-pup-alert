package com.kevinmdyer.android.pupalert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by kevin on 2/14/18.
 */

public class Lookup {
    private static final Map<UUID, String> sServices = new HashMap<>();

    static {
        sServices.put(UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.generic_access Generic Access");
        sServices.put(UUID.fromString("00001811-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.alert_notification Alert Notification Service");
        sServices.put(UUID.fromString("00001815-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.automation_io Automation IO");
        sServices.put(UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.battery_service Battery Service");
        sServices.put(UUID.fromString("00001810-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.blood_pressure Blood Pressure");
        sServices.put(UUID.fromString("0000181B-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.body_composition Body Composition");
        sServices.put(UUID.fromString("0000181E-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.bond_management Bond Management Service");
        sServices.put(UUID.fromString("0000181F-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.continuous_glucose_monitoring Continuous Glucose Monitoring");
        sServices.put(UUID.fromString("00001805-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.current_time Current Time Service");
        sServices.put(UUID.fromString("00001818-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.cycling_power Cycling Power");
        sServices.put(UUID.fromString("00001816-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.cycling_speed_and_cadence Cycling Speed and Cadence");
        sServices.put(UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.device_information Device Information");
        sServices.put(UUID.fromString("0000181A-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.environmental_sensing Environmental Sensing");
        sServices.put(UUID.fromString("00001826-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.fitness_machine Fitness Machine");
        sServices.put(UUID.fromString("00001801-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.generic_attribute Generic Attribute");
        sServices.put(UUID.fromString("00001808-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.glucose Glucose");
        sServices.put(UUID.fromString("00001809-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.health_thermometer Health Thermometer");
        sServices.put(UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.heart_rate Heart Rate");
        sServices.put(UUID.fromString("00001823-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.http_proxy HTTP Proxy");
        sServices.put(UUID.fromString("00001812-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.human_interface_device Human Interface Device");
        sServices.put(UUID.fromString("00001802-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.immediate_alert Immediate Alert");
        sServices.put(UUID.fromString("00001821-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.indoor_positioning Indoor Positioning");
        sServices.put(UUID.fromString("00001820-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.internet_protocol_support Internet Protocol Support Service");
        sServices.put(UUID.fromString("00001803-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.link_loss Link Loss");
        sServices.put(UUID.fromString("00001819-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.location_and_navigation Location and Navigation");
        sServices.put(UUID.fromString("00001827-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.mesh_provisioning Mesh Provisioning Service");
        sServices.put(UUID.fromString("00001828-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.mesh_proxy Mesh Proxy Service");
        sServices.put(UUID.fromString("00001807-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.next_dst_change Next DST Change Service");
        sServices.put(UUID.fromString("00001825-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.object_transfer Object Transfer Service");
        sServices.put(UUID.fromString("0000180E-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.phone_alert_status Phone Alert Status Service");
        sServices.put(UUID.fromString("00001822-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.pulse_oximeter Pulse Oximeter Service");
        sServices.put(UUID.fromString("00001829-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.reconnection_configuration Reconnection Configuration");
        sServices.put(UUID.fromString("00001806-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.reference_time_update Reference Time Update Service");
        sServices.put(UUID.fromString("00001814-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.running_speed_and_cadence Running Speed and Cadence");
        sServices.put(UUID.fromString("00001813-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.scan_parameters Scan Parameters");
        sServices.put(UUID.fromString("00001824-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.transport_discovery Transport Discovery");
        sServices.put(UUID.fromString("00001804-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.tx_power Tx Power");
        sServices.put(UUID.fromString("0000181C-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.user_data User Data");
        sServices.put(UUID.fromString("0000181D-0000-1000-8000-00805f9b34fb"), "org.bluetooth.service.weight_scale Weight Scale");
    }

    static String getServiceFromUUID(UUID uuid) {
        return sServices.get(uuid);
    }
}
