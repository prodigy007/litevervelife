package com.example.prodigy.litevervelife.HelpClasses;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.example.prodigy.litevervelife.BluetoothLeServiceMaster;
import com.example.prodigy.litevervelife.BluetoothLeServiceSlave;
import com.example.prodigy.litevervelife.EXTERNAL.SettingBundle;

import static com.example.prodigy.litevervelife.HelpClasses.BroadcastReceivers.connectionStatusMaster;
import static com.example.prodigy.litevervelife.HelpClasses.BroadcastReceivers.connectionStatusSlave;
import static com.example.prodigy.litevervelife.HelpClasses.BroadcastReceivers.setting;

/**
 * Created by Prodigy on 13.07.2017.
 */

public class WorkWithServiceGatt {

    public static String soundEQ = "80000e32-3c4f-44be-b5f3-e302e1f59da9"; //Настройки звука
    public static String battery = "00002a19-0000-1000-8000-00805f9b34fb"; //батарея устройства
    public static String nameDevice = "00002a24-0000-1000-8000-00805f9b34fb"; // Название AppVerveOnes+



    public static BluetoothLeServiceMaster mBluetoothLeServiceMaster;
    public static BluetoothLeServiceSlave mBluetoothLeServiceSlave;

    public static final ServiceConnection mServiceConnectionMaster = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeServiceMaster = ((BluetoothLeServiceMaster.LocalBinder) service).getService();
            if (!mBluetoothLeServiceMaster.initialize()) {
                Log.e("", "Unable to initialize Bluetooth");
                //finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    /**
     * Ссылка на подключенный сервис (в данном случае класс BluetoothLeServiceSlave.java)
     */
    public static final ServiceConnection mServiceConnectionSlave = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeServiceSlave = ((BluetoothLeServiceSlave.LocalBinder) service).getService();
            if (!mBluetoothLeServiceSlave.initialize()) {
                Log.e("", "Unable to initialize Bluetooth");
                //finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    public void sendGattCharacteristic() {

    }

  /*
    public static void getGattCharacteristic(String UUIDCharacteristic, IService service) {

        service.getGatt(UUIDCharacteristic);
    }
    */
    /**
     * Получение настроек
     * @param uuidCharacteristic
     */
    public static void getGattCharacteristic(String uuidCharacteristic) {
        if (connectionStatusMaster && connectionStatusSlave) {
            IService serviceSlave = (IService) WorkWithServiceGatt.mBluetoothLeServiceSlave;
            serviceSlave.getGatt(uuidCharacteristic);
            IService serviceMaster = (IService) WorkWithServiceGatt.mBluetoothLeServiceMaster;
            serviceMaster.getGatt(uuidCharacteristic);
        }
    }


    /**
     * Отправка настроек
     * @param uuidCharacteristic
     */
    public static void setGattCharacteristic(String uuidCharacteristic) {
        SettingBundle.SettingsBitFieldSet settingsBitFieldSetMaster = new SettingBundle.SettingsBitFieldSet();
        byte[] byteSettings = settingsBitFieldSetMaster.getArray(setting);

        if (connectionStatusMaster && connectionStatusSlave) {
            IService service = (IService) WorkWithServiceGatt.mBluetoothLeServiceSlave;
            service.setGatt(uuidCharacteristic, byteSettings);
            service = (IService) WorkWithServiceGatt.mBluetoothLeServiceMaster;
            service.setGatt(uuidCharacteristic, byteSettings);
        }
    }






}
