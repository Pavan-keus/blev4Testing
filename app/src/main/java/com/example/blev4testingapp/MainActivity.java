package com.example.blev4testingapp;


import static android.content.Context.BLUETOOTH_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Intent;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.blev4testingapp.Myadapters.tabAdapater;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements ConnectionPair.connectionCommuniation, ConnectionLessPair.connectionLessCommuniation,
        OfflineCommissioning.offlineComissioningCommuniation {
    String Tag = "appOutput";
    private static final ParcelUuid SERVICE_UUID = ParcelUuid.fromString("00001190-0000-1000-8000-00805F9B34FB");
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ConnectionPair connectionPair;
    ConnectionLessPair connectionLessPair;
    OfflineCommissioning offlineCommissioning;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    BluetoothLeAdvertiser advertiser;
    private BluetoothLeScanner bluetoothLeScanner;
    private static final int REQUEST_ENABLE_BT = 1;
    private String[] labels = new String[]{"Connection Pairing", "Connection Less Pairing", "Offline Commissioning"};
    private int REQUEST_LOCATION_PERMISSION = 2;
    protected static final UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    String connectandpair = "0C:EC:80:95:AA:D8";
    List<BluetoothDevice> Devices = new ArrayList<BluetoothDevice>();
    BluetoothGatt connectedDevice = null;
    BluetoothGattCharacteristic NetworkState,sendBeacon,setChannel;
    String ntwkState = "f000a001-0451-4000-b000-000000000000";
    String sendbeacon = "f000a002-0451-4000-b000-000000000000";
    String channel = "f000a005-0451-4000-b000-000000000000";
    public boolean containsAddress(final List<BluetoothDevice> list, String address) {
        return list.stream().filter(o -> o.getAddress().equals(address)).findFirst().isPresent();
    }

    private void requestPermissions() {
        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission has been granted, perform the BLE scanning
            startScan();
        }

    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                connectedDevice = gatt;
                gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Object data[] = new Object[1];
                data[0] = new Integer(3);
                connectedDevice = null;
                connectionPair.ReceiveDataFromActivity(data);

            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    // Process characteristics (read, write, identify)
                    for(BluetoothGattCharacteristic characteristic:characteristics){
                        if(characteristic.getUuid().toString().equals(ntwkState) == true){
                            NetworkState = characteristic;
                        }
                        else if(characteristic.getUuid().toString().equals(sendbeacon) == true){
                            sendBeacon = characteristic;
                        }
                        else if(characteristic.getUuid().toString().equals(channel) == true){
                            setChannel = characteristic;
                        }
                    }
                }
                boolean returnvalue = connectedDevice.setCharacteristicNotification(NetworkState,true);
                BluetoothGattDescriptor discriptor = NetworkState.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
                discriptor.setValue(new byte[]{0x01, 0x00});
                connectedDevice.writeDescriptor(discriptor);
                Log.d(Tag,"Notify"+String.valueOf(returnvalue));
                Object data[] = new Object[1];
                data[0] = new Integer(2);
                connectionPair.ReceiveDataFromActivity(data);
            }
        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(Tag,"write operation"+String.valueOf(status));
            if(characteristic.getUuid().toString().equals(channel) == true){
                connectedDevice.writeCharacteristic(sendBeacon);
            }
        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
            Log.d(Tag,"updated value"+value[0]);
            if(value[0] == 0){
                Object data2[] = new Object[1];
                data2[0] = new Integer(4);
                connectionPair.ReceiveDataFromActivity(data2);
            }
            else if(value[0] == 7){
                Object data2[] = new Object[1];
                data2[0] = new Integer(5);
                connectionPair.ReceiveDataFromActivity(data2);
            }
        }

    };
    private ScanCallback blescanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            // Process scan results (e.g., get device name, address, RSSI)

            // You can access the BluetoothDevice object from the ScanResult
            BluetoothDevice device = result.getDevice();
            @SuppressLint("MissingPermission")
            String deviceName = device.getName();
            String deviceAddress = device.getAddress();
            int rssi = result.getRssi();

            // Update UI or perform actions based on scan results
            Log.i(Tag, "Device: " + deviceName + ", Address: " + deviceAddress + ", RSSI: " + rssi);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (final ScanResult result : results) {
                //result.getDevice() is scanned device
                BluetoothDevice device = result.getDevice();
                @SuppressLint("MissingPermission")
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                int rssi = result.getRssi();
                if (deviceAddress!=null && deviceAddress.equals(connectandpair) && deviceName != null) {
                    Object data[] = new Object[4];
                    data[0] = new Integer(1);
                    data[1] = new String(String.valueOf(rssi));
                    data[2] = new String(deviceName);
                    data[3] = new String(deviceAddress);
                    connectionPair.ReceiveDataFromActivity(data);
                    if (containsAddress(Devices, deviceAddress) == false) {
                        Log.d(Tag, "pushed");
                        Devices.add(device);
                    }
                }
                Log.i(Tag, "Device: " + deviceName + ", Address: " + deviceAddress + ", RSSI: " + rssi);

            }
        }

        // Implement methods for other scan events (e.g., scan started, scan failed)
    };

    void stopScan() {
        if (bluetoothLeScanner != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothLeScanner.stopScan(blescanCallback);
        }
    }

    @SuppressLint("MissingPermission")
    void startScan() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return;
        }
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner != null) {

            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                    .setReportDelay(200)
                    .build();
            ScanFilter filter = new ScanFilter.Builder()
                    .setServiceUuid(SERVICE_UUID)
                    .build();
            List<ScanFilter> scanFilters = new ArrayList<>();
            scanFilters.add(filter);
            bluetoothLeScanner.startScan(scanFilters, settings, blescanCallback);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager2);
        connectionPair = new ConnectionPair();
        connectionLessPair = new ConnectionLessPair();
        offlineCommissioning = new OfflineCommissioning();
        tabAdapater adapater = new tabAdapater(getSupportFragmentManager(), getLifecycle(), connectionPair, connectionLessPair, offlineCommissioning);
        viewPager.setAdapter(adapater);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(labels[position]);
        }).attach();


        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        requestPermissions();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void sendDataToActivityFromConnection(Object[] data) {
        if ((Integer) data[0] == 1) {
            for (BluetoothDevice device : Devices) {
                if (device.getAddress().equals(connectandpair) == true) {

                    device.connectGatt(this, false, gattCallback);
                    break;
                    }
                }
            }
        else if((Integer)data[0] == 2) {
            if (connectedDevice != null)
                connectedDevice.disconnect();
        }
        else if((Integer)data[0] == 3){
            if(connectedDevice!=null){
                Log.d(Tag,"paired started");
                byte[]channel = new byte[1];
                channel[0] = 13;
                byte[]senddata = new byte[1];
                senddata[0]=0x01;
                setChannel.setValue(channel);
                sendBeacon.setValue(senddata);
                connectedDevice.writeCharacteristic(setChannel);
            }
            else{
                Toast.makeText(this, "No device Connected",
                        Toast.LENGTH_SHORT).show();
                Object data2[] = new Object[1];
                data2[0] = new Integer(4);
                connectionPair.ReceiveDataFromActivity(data2);
            }
        }
    }

    @Override
    public void sendDataToActivityFromConnectionLess(String data) {

    }

    @Override
    public void sendDataToActivityFromOfflineCommunication(String data) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION)
        {
                startScan();

        }
    }


}