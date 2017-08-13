package com.example.prodigy.litevervelife;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArraySet;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.prodigy.litevervelife.EXTERNAL.EnableState;
import com.example.prodigy.litevervelife.HelpClasses.BroadcastReceivers;
import com.example.prodigy.litevervelife.HelpClasses.WorkWithServiceGatt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static com.example.prodigy.litevervelife.EXTERNAL.SettingBundle.SettingsBitFieldSet.keySettingEarDetect;
import static com.example.prodigy.litevervelife.EXTERNAL.SettingBundle.SettingsBitFieldSet.keySettingEqMode;
import static com.example.prodigy.litevervelife.EXTERNAL.SettingBundle.SettingsBitFieldSet.keySettingPassThroughAudio;
import static com.example.prodigy.litevervelife.EXTERNAL.SettingBundle.SettingsBitFieldSet.keySettingVideoEqMode;
import static com.example.prodigy.litevervelife.HelpClasses.BroadcastReceivers.connectionStatusMaster;
import static com.example.prodigy.litevervelife.HelpClasses.BroadcastReceivers.connectionStatusSlave;
import static com.example.prodigy.litevervelife.HelpClasses.BroadcastReceivers.firstChanceMaster;
import static com.example.prodigy.litevervelife.HelpClasses.BroadcastReceivers.firstChanceSlave;
import static com.example.prodigy.litevervelife.HelpClasses.BroadcastReceivers.setting;
import static com.example.prodigy.litevervelife.HelpClasses.WorkWithServiceGatt.mBluetoothLeServiceMaster;
import static com.example.prodigy.litevervelife.HelpClasses.WorkWithServiceGatt.mBluetoothLeServiceSlave;
import static com.example.prodigy.litevervelife.HelpClasses.WorkWithServiceGatt.mServiceConnectionMaster;
import static com.example.prodigy.litevervelife.HelpClasses.WorkWithServiceGatt.mServiceConnectionSlave;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    public static BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver discoverDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    public static List<BluetoothDevice> bluetoothDevicesList = new ArrayList<>(); //Список устройств
    public static int countSeachBT = 0;
    public static List<String> listIP = new ArrayList<>();
    private static final String sharedPreferencesName = "LiteVerveLife"; //файл с настройками
    private static final String sharedPreferencesStringSet = "addressesIP";
    static SharedPreferences mSettings;
    BroadcastReceivers broadcastReceivers = BroadcastReceivers.getInstance(this);


    // Элементы экрана
    public static ProgressBar progressBar;
    public TextView tv_batterySlave;
    public TextView tv_batteryMaster;
    public TextView tv_ipMaster;
    public TextView tv_ipSlave;
    public TextView tv_soundEqPreset;
    public TextView tv_videoEqPreset;

    public static Toolbar toolbar;
    public Switch switch_earDetect;
    public Switch switch_passThroughAudio;
    public Spinner spinner_soundEqPreset;
    public Spinner spinner_videoEqPreset;
    public TextView tv_statusSlave;
    public TextView tv_statusMaster;
    public Button btn_search;
    public Button btn_connectGat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //Убираем гамбургер
 /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        */

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this); //выключаем Listener пунктом меню

        //Получение ссылки на элемент экрана
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_batteryMaster = (TextView) findViewById(R.id.battery_master_tv);
        tv_batterySlave = (TextView) findViewById(R.id.battery_slave_tv);
        tv_ipMaster = (TextView) findViewById(R.id.ip_master_tv);
        tv_ipSlave = (TextView) findViewById(R.id.ip_slave_tv);
        switch_earDetect = (Switch) findViewById(R.id.ear_detect_switch);
        switch_passThroughAudio = (Switch) findViewById(R.id.pass_through_audio_switch);
        spinner_soundEqPreset = (Spinner) findViewById(R.id.sound_eq_preset_spinner);
        spinner_soundEqPreset.setEnabled(false);
        spinner_videoEqPreset = (Spinner) findViewById(R.id.video_eq_preset_spinner);
        spinner_videoEqPreset.setEnabled(false);
        switch_earDetect.setOnCheckedChangeListener(this);
        switch_passThroughAudio.setOnCheckedChangeListener(this);
        spinner_soundEqPreset.setOnItemSelectedListener(this);
        spinner_videoEqPreset.setOnItemSelectedListener(this);
        tv_soundEqPreset = (TextView) findViewById(R.id.sound_eq_preset_tv);
        tv_videoEqPreset = (TextView) findViewById(R.id.video_eq_preset_tv);
        tv_statusSlave = (TextView) findViewById(R.id.status_slave_tv);
        tv_statusMaster = (TextView) findViewById(R.id.status_master_tv);
        btn_search = (Button) findViewById(R.id.searchButton);
        btn_connectGat = (Button) findViewById(R.id.connectGat);

        //Запуск сервисов для общения с наушниками
        Intent gattServiceIntent = new Intent(this, BluetoothLeServiceSlave.class);
        bindService(gattServiceIntent, mServiceConnectionSlave, BIND_AUTO_CREATE); //BIND_AUTO_CREATE вкл сервиса, если он не запущен, а потом закрытие? unbindService(mServiceConnectionSlave); //выключение сервиса

        //Запуск сервисов для общения с наушниками
        Intent gattServiceIntentTwo = new Intent(this, BluetoothLeServiceMaster.class);
        bindService(gattServiceIntentTwo, mServiceConnectionMaster, BIND_AUTO_CREATE); //BIND_AUTO_CREATE вкл сервиса, если он не запущен, а потом закрытие? unbindService(mServiceConnectionSlave); //выключение сервиса

        mSettings = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);

        showIPDevice();


    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceivers.receiver_UpdateStatusBattery);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceivers.receiver_UpdateStatusBattery, BroadcastReceivers.intentFilter_UpdateBattery);

        registerReceiver(broadcastReceivers.receiver_UpdateSoundAndDeviceNameSettings, BroadcastReceivers.intentFilter_UpdateSoundAndDeviceName);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Выводим состояние о имеющихся устройствах и помещаем
     */
    public void showIPDevice() {
        Set<String> ip = mSettings.getStringSet(sharedPreferencesStringSet, null);

        if (ip != null) {
            if (ip.toArray().length > 1) {
                tv_ipMaster.setText(ip.toArray()[0].toString());
                tv_ipSlave.setText(ip.toArray()[1].toString());
                btn_search.setEnabled(false);
                btn_connectGat.setEnabled(true);
                //toolbar.setTitle(countMessage + " 50%. 1: " + ip.toArray()[0].toString() + ". 2: Отсутствует!");
            } else {
                tv_ipMaster.setText(ip.toArray()[0].toString());
            }
        }
    }


    /**
     * Глобальный Button Click Listener
     */
    public void GlobalButtonClickListener(View view) {
        try {
            switch (view.getId()) {
                case R.id.searchButton:
                    bluetoothDevicesList = new ArrayList<>(); //Сбрасываем счетчик
                    countSeachBT = 0; //Сбрасываем счетчик
                    discoverDevices();
                    break;
                case R.id.connectGat:
                    mBluetoothLeServiceSlave.disconnect(); // На случай глюков, когда подключается только одно из устройств
                    mBluetoothLeServiceMaster.disconnect();

                    connectionStatusSlave = false; //Сбрасываем счетчик
                    firstChanceSlave = true; //Сбрасываем счетчик
                    connectionStatusMaster = false; //Сбрасываем счетчик
                    firstChanceMaster = true; //Сбрасываем счетчик

                    registerReceiver(broadcastReceivers.receiver_ActionGattConnectedAndDisconnected, BroadcastReceivers.intentFilter_AGConnectedAndDisconnected);

                    Set<String> ip = mSettings.getStringSet(sharedPreferencesStringSet, new ArraySet<String>());


                    for (String getIp : ip) {
                        if (!listIP.contains(getIp)) {
                            listIP.add(getIp);
                        }
                    }
                    mBluetoothLeServiceMaster.connect(listIP.get(0));

                    mBluetoothLeServiceSlave.connect(listIP.get(1));


                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void discoverDevices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) || (checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_COARSE_LOCATION, BLUETOOTH_ADMIN}, 666);
            } else {
                registerReceiver(broadcastReceivers.receiver_ActionStarted_ActionDiscoveryAndFound, BroadcastReceivers.intentFilter_AFound_ADStartedAndFinished);
                mBluetoothLeServiceSlave.mBluetoothAdapter.startDiscovery();
                progressBar.setVisibility(ProgressBar.VISIBLE);
            }
        } else {
            registerReceiver(broadcastReceivers.receiver_ActionStarted_ActionDiscoveryAndFound, BroadcastReceivers.intentFilter_AFound_ADStartedAndFinished);
            mBluetoothLeServiceSlave.mBluetoothAdapter.startDiscovery();
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }
    }


    /**
     * Получение настроек (1? 2? IP)
     *
     * @param newIP
     */
    public static void saveSettings(String newIP) {
        Set<String> setListIP = new HashSet<String>();
        setListIP = mSettings.getStringSet(sharedPreferencesStringSet, new HashSet<String>());
        if (!setListIP.contains(newIP))
            setListIP.add(newIP);

        SharedPreferences.Editor editor = mSettings.edit();
        editor.clear().apply();//Иначе глюк с сохранением поэтому делаем clear и apply
        editor.putStringSet(sharedPreferencesStringSet, setListIP).apply();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.pass_through_audio_switch:
                setting.setEnableSetting(keySettingPassThroughAudio, EnableState.setBoolean(isChecked));
                WorkWithServiceGatt.setGattCharacteristic(WorkWithServiceGatt.soundEQ);
            case R.id.ear_detect_switch:
                setting.setEnableSetting(keySettingEarDetect, EnableState.setBoolean(isChecked));
                WorkWithServiceGatt.setGattCharacteristic(WorkWithServiceGatt.soundEQ);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sound_eq_preset_spinner:
                if (spinner_soundEqPreset.getTag() == null) //|| spinner_soundEqPreset.getTag().equals(position))
                {
                    spinner_soundEqPreset.setTag(position + 1);
                } else {
                    if (setting != null) {
                        setting.setIntSetting(keySettingEqMode, position + 1);
                        WorkWithServiceGatt.setGattCharacteristic(WorkWithServiceGatt.soundEQ);
                    }
                }
                break;
            case R.id.video_eq_preset_spinner:
                if (spinner_videoEqPreset.getTag() == null) {
                    spinner_videoEqPreset.setTag(position + 1);
                } else {
                    if (setting != null) {
                        setting.setIntSetting(keySettingVideoEqMode, position + 1);
                        WorkWithServiceGatt.setGattCharacteristic(WorkWithServiceGatt.soundEQ);
                    }
                }
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}





