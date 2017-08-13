package com.example.prodigy.litevervelife.HelpClasses;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.prodigy.litevervelife.BluetoothLeServiceMaster;
import com.example.prodigy.litevervelife.BluetoothLeServiceSlave;
import com.example.prodigy.litevervelife.EXTERNAL.EnableState;
import com.example.prodigy.litevervelife.EXTERNAL.Setting;
import com.example.prodigy.litevervelife.HelpClasses.Timer.GlobalTimerForGatt;
import com.example.prodigy.litevervelife.HelpClasses.Timer.TimerTaskGlobal;
import com.example.prodigy.litevervelife.MainActivity;
import com.example.prodigy.litevervelife.R;

import java.util.Timer;

import static com.example.prodigy.litevervelife.BluetoothLeServiceMaster.ACTION_GATT_CONNECTED_MASTER;
import static com.example.prodigy.litevervelife.BluetoothLeServiceMaster.ACTION_GATT_DISCONNECTED_MASTER;
import static com.example.prodigy.litevervelife.BluetoothLeServiceSlave.ACTION_GATT_CONNECTED_SLAVE;
import static com.example.prodigy.litevervelife.BluetoothLeServiceSlave.ACTION_GATT_DISCONNECTED_SLAVE;
import static com.example.prodigy.litevervelife.EXTERNAL.SettingBundle.SettingsBitFieldSet.keySettingEarDetect;
import static com.example.prodigy.litevervelife.EXTERNAL.SettingBundle.SettingsBitFieldSet.keySettingEqMode;
import static com.example.prodigy.litevervelife.EXTERNAL.SettingBundle.SettingsBitFieldSet.keySettingPassThroughAudio;
import static com.example.prodigy.litevervelife.EXTERNAL.SettingBundle.SettingsBitFieldSet.keySettingVideoEqMode;
import static com.example.prodigy.litevervelife.HelpClasses.Timer.TimerTaskGlobal.ITimerActions;
import static com.example.prodigy.litevervelife.HelpClasses.WorkWithServiceGatt.mBluetoothLeServiceMaster;
import static com.example.prodigy.litevervelife.HelpClasses.WorkWithServiceGatt.mBluetoothLeServiceSlave;
import static com.example.prodigy.litevervelife.MainActivity.bluetoothDevicesList;
import static com.example.prodigy.litevervelife.MainActivity.countSeachBT;
import static com.example.prodigy.litevervelife.MainActivity.listIP;
import static com.example.prodigy.litevervelife.MainActivity.progressBar;
import static com.example.prodigy.litevervelife.MainActivity.saveSettings;

/**
 * Created by Prodigy on 06.06.2017.
 */

public class BroadcastReceivers {

    public static Setting setting;
    private static BroadcastReceivers instance;
    private static MainActivity activity;
    public static Timer gattTimer;

    private BroadcastReceivers() {

    }

    public static BroadcastReceivers getInstance(MainActivity activity){
        instance = new BroadcastReceivers();
        BroadcastReceivers.activity = activity;
        return instance;
    }

    public static boolean connectionStatusSlave = false;
    public static boolean firstChanceSlave = true;

    public static boolean connectionStatusMaster = false;
    public static boolean firstChanceMaster = true;
    public static IntentFilter intentFilter_AFound_ADStartedAndFinished = new IntentFilter();
    public static IntentFilter intentFilter_AGConnectedAndDisconnected = new IntentFilter();
    public static IntentFilter intentFilter_UpdateBattery = new IntentFilter();
    public static IntentFilter intentFilter_UpdateSoundAndDeviceName = new IntentFilter();


    //Для Intent ключи
    public static String intentKeyBattery = "battery";
    public static String intentKeyDeviceName = "deviceName";
    public static String intentKeySound = "sound";


    static {
        try {
            intentFilter_AFound_ADStartedAndFinished.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter_AFound_ADStartedAndFinished.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter_AFound_ADStartedAndFinished.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            intentFilter_AGConnectedAndDisconnected.addAction(ACTION_GATT_DISCONNECTED_SLAVE);
            intentFilter_AGConnectedAndDisconnected.addAction(ACTION_GATT_CONNECTED_SLAVE);
            intentFilter_AGConnectedAndDisconnected.addAction(ACTION_GATT_DISCONNECTED_MASTER);
            intentFilter_AGConnectedAndDisconnected.addAction(ACTION_GATT_CONNECTED_MASTER);
            intentFilter_UpdateBattery.addAction(BluetoothLeServiceMaster.UPDATE_BATTERY);
            intentFilter_UpdateBattery.addAction(BluetoothLeServiceSlave.UPDATE_BATTERY);
            intentFilter_UpdateSoundAndDeviceName.addAction(BluetoothLeServiceMaster.UPDATE_SOUND);
            intentFilter_UpdateSoundAndDeviceName.addAction(BluetoothLeServiceMaster.UPDATE_DEVICE_NAME);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Подключение/отключение Gatt
     */
    public BroadcastReceiver receiver_ActionGattConnectedAndDisconnected = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            synchronized (this) {
                String action = intent.getAction();
                //SLAVE
                if (action.equals(ACTION_GATT_DISCONNECTED_SLAVE)) {
                    if (!connectionStatusSlave && firstChanceSlave == true) { //Если 1-ая попытка не успешная и ещё не было коннектов, то...
                        new Timer().schedule(new TimerTaskGlobal(activity, new ITimerActions() { //На тот случай, т.к. иногда приходит DISCONNECTED, а потом резко CONNECTED
                            @Override
                            public void runActions() {
                                firstChanceSlave = false;
                                mBluetoothLeServiceSlave.connect(listIP.get(1)); //Пробуем второй РАЗ
                            }
                        }), 3000);
                    } else if (connectionStatusSlave) {
                        connectionStatusSlave = false;
                        Toast.makeText(context, context.getString(R.string.gatt_disconnected_timeout), Toast.LENGTH_LONG).show();
                        activity.tv_statusSlave.setText(context.getString(R.string.status_gatt_dis));
                    } else if (!connectionStatusSlave && firstChanceSlave == false) { //Последняя попытка произведена, то...
                        new Timer().schedule(new TimerTaskGlobal(activity, new ITimerActions() { //На тот случай, т.к. иногда приходит DISCONNECTED, а потом резко CONNECTED
                            @Override
                            public void runActions() {
                                if (connectionStatusSlave == false) {
                                    Toast.makeText(activity, context.getString(R.string.turn_off_and_repeat), Toast.LENGTH_LONG).show();
                                }
                            }
                        }), 3000);
                    }
                } else if (action.equals(ACTION_GATT_CONNECTED_SLAVE)) {
/*
                    mBluetoothLeServiceSlave.sendKALL();
                    mBluetoothLeServiceSlave.setCharacteristicNotification("00002a19-0000-1000-8000-00805f9b34fb");
                    mBluetoothLeServiceSlave.setCharacteristicNotification("80000e12-3c4f-44be-b5f3-e302e1f59da9");
                    mBluetoothLeServiceSlave.setCharacteristicNotification("80000e21-3c4f-44be-b5f3-e302e1f59da9");
                    mBluetoothLeServiceSlave.setCharacteristicNotification("80000e24-3c4f-44be-b5f3-e302e1f59da9");
                    mBluetoothLeServiceSlave.setCharacteristicNotification("80000e32-3c4f-44be-b5f3-e302e1f59da9");
                    mBluetoothLeServiceSlave.setCharacteristicNotification("80000e41-3c4f-44be-b5f3-e302e1f59da9");
*/
                    activity.tv_statusSlave.setText(context.getString(R.string.status_gatt_con));
                    connectionStatusSlave = true;
                    Toast.makeText(context, context.getString(R.string.gatt_successful_connection), Toast.LENGTH_LONG).show();
                    getNameDevice();
                    getSoundSettings();
                    getBatteryGattTimer();
                }

                //MASTER
                else if (action.equals(ACTION_GATT_DISCONNECTED_MASTER)) {
                    if (!connectionStatusMaster && firstChanceMaster == true) { //Если 1-ая попытка не успешная и ещё не было коннектов, то...
                        new Timer().schedule(new TimerTaskGlobal(activity, new ITimerActions() { //На тот случай, т.к. иногда приходит DISCONNECTED, а потом резко CONNECTED
                            @Override
                            public void runActions() {
                                firstChanceMaster = false;
                                mBluetoothLeServiceMaster.connect(listIP.get(0)); //Пробуем второй РАЗ
                            }
                        }), 3000);
                    } else if (connectionStatusMaster) {
                        connectionStatusMaster = false;
                        Toast.makeText(context, context.getString(R.string.gatt_disconnected_timeout), Toast.LENGTH_LONG).show();
                        activity.tv_statusMaster.setText(context.getString(R.string.status_gatt_dis));
                    } else if (!connectionStatusMaster && firstChanceMaster == false) { //Последняя попытка произведена, то...
                        new Timer().schedule(new TimerTaskGlobal(activity, new ITimerActions() { //На тот случай, т.к. иногда приходит DISCONNECTED, а потом резко CONNECTED
                            @Override
                            public void runActions() {
                                if (connectionStatusMaster == false) {
                                    Toast.makeText(activity, context.getString(R.string.turn_off_and_repeat), Toast.LENGTH_LONG).show();
                                }
                            }
                        }), 3000);
                    }
                } else if (action.equals(ACTION_GATT_CONNECTED_MASTER)) {
                    mBluetoothLeServiceMaster.sendKALL();
                    mBluetoothLeServiceMaster.setCharacteristicNotification("00002a19-0000-1000-8000-00805f9b34fb");
                    mBluetoothLeServiceMaster.setCharacteristicNotification("80000e12-3c4f-44be-b5f3-e302e1f59da9");
                    mBluetoothLeServiceMaster.setCharacteristicNotification("80000e21-3c4f-44be-b5f3-e302e1f59da9");
                    mBluetoothLeServiceMaster.setCharacteristicNotification("80000e24-3c4f-44be-b5f3-e302e1f59da9");
                    mBluetoothLeServiceMaster.setCharacteristicNotification("80000e32-3c4f-44be-b5f3-e302e1f59da9");
                    mBluetoothLeServiceMaster.setCharacteristicNotification("80000e41-3c4f-44be-b5f3-e302e1f59da9");
                    activity.tv_statusMaster.setText(context.getString(R.string.status_gatt_con));
                    connectionStatusMaster = true;
                    Toast.makeText(context, context.getString(R.string.gatt_successful_connection), Toast.LENGTH_LONG).show();
                    getNameDevice();
                    getSoundSettings();
                    getBatteryGattTimer();
                }
            }
        }
    };

    /**
     * Поиск Bluetooth устройств - Делаем 5 поиска до получения 2 устройств!
     */
    public BroadcastReceiver receiver_ActionStarted_ActionDiscoveryAndFound = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(context, context.getString(R.string.start_search_bt), Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (bluetoothDevicesList.size() == 2) {
                    activity.unregisterReceiver(this);
                    Toast.makeText(context, context.getString(R.string.successful_both_devices_found), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                } else {
                    countSeachBT++;

                    if (countSeachBT > 5) { // 4 попытку поиска прерываем
                        if (bluetoothDevicesList.size() == 1) {
                            Toast.makeText(context, context.getString(R.string.searched_only_one_bt), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(context, context.getString(R.string.turn_off_and_repeat), Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        activity.unregisterReceiver(this);
                    } else {
                        BluetoothLeServiceSlave.mBluetoothAdapter.startDiscovery();
                    }
                }
            } else
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (bluetoothDevice.getName().toLowerCase().contains(context.getString(R.string.name_headphones).toLowerCase())) {
                    if (!bluetoothDevicesList.contains(bluetoothDevice)) {
                        bluetoothDevicesList.add(bluetoothDevice);
                        saveSettings(bluetoothDevice.getAddress());
                        activity.showIPDevice();
                        Toast.makeText(activity, context.getString(R.string.searched_successful_bt), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };


    /**
     * Постоянный траймер - запрос каждую секунду, чтобы устройство не засыпало и получение процента заряда
     */
    public static void getBatteryGattTimer() {
        //gattTimer =
                GlobalTimerForGatt.createNewGattTaskSchedule(new TimerTaskGlobal.ITimerActions() {
            @Override
            public void runActions() {
                WorkWithServiceGatt.getGattCharacteristic(WorkWithServiceGatt.battery);
            }
        }, 1000); //    }}, 4000, 1000);
    }

    /**
     * Получение настроек звука
     */
    private void getSoundSettings() {
            //gattTimer =
                    GlobalTimerForGatt.createNewGattTaskSchedule(new TimerTaskGlobal.ITimerActions() {
                @Override
                public void runActions() {
                    WorkWithServiceGatt.getGattCharacteristic(WorkWithServiceGatt.soundEQ);
                }
            }, 2000 );
    }

    /**
     * Получение названия устройства
     */
    private void getNameDevice() {
        WorkWithServiceGatt.getGattCharacteristic(WorkWithServiceGatt.nameDevice);
    }

    /**
     * Получение заряда батареи
     */
    public BroadcastReceiver receiver_UpdateStatusBattery = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothLeServiceMaster.UPDATE_BATTERY.equals(action)) {
                if (intent.getStringExtra(intentKeyBattery) != null) {
                    activity.tv_batteryMaster.setText(intent.getStringExtra(intentKeyBattery) + "%");
                }
            }
            if (BluetoothLeServiceSlave.UPDATE_BATTERY.equals(action)) {
                if (intent.getStringExtra(intentKeyBattery) != null) {
                    activity.tv_batterySlave.setText(intent.getStringExtra(intentKeyBattery) + "%");
                }
            }
        }
    };

    /**
     * ПОлучение настроек звука
     */
    public BroadcastReceiver receiver_UpdateSoundAndDeviceNameSettings = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothLeServiceMaster.UPDATE_SOUND.equals(action)) {
                activity.switch_earDetect.setEnabled(true);
                activity.switch_passThroughAudio.setEnabled(true);
                activity.spinner_soundEqPreset.setEnabled(true);
                activity.spinner_videoEqPreset.setEnabled(true);
                activity.tv_soundEqPreset.setEnabled(true);
                activity.tv_videoEqPreset.setEnabled(true);

                setting =  (Setting) intent.getSerializableExtra(intentKeySound);

                EnableState ear_detect = setting.getEnableSetting(keySettingEarDetect);
                EnableState passThroughAudio = setting.getEnableSetting(keySettingPassThroughAudio);
                activity.switch_earDetect.setChecked(ear_detect.getBoolean());
                activity.switch_passThroughAudio.setChecked(passThroughAudio.getBoolean());

                int sound_eq_preset = setting.getIntSetting(keySettingEqMode);
                int video_eq_preset = setting.getIntSetting(keySettingVideoEqMode);
                activity.spinner_soundEqPreset.setSelection(sound_eq_preset - 1);
                activity.spinner_videoEqPreset.setSelection(video_eq_preset - 1);
            }

            if (BluetoothLeServiceMaster.UPDATE_DEVICE_NAME.equals(action)) {

                activity.toolbar.setTitle(intent.getStringExtra(intentKeyDeviceName));
            }
        }
    };
}

