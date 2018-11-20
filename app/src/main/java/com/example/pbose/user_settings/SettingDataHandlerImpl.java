package com.example.pbose.user_settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.pbose.movienotifier.R;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by pbose on 4/9/16.
 */
public class SettingDataHandlerImpl implements SettingDataHandler {

    private static SettingDataHandler settingDataHandler;
    private static String PREFERENCE_FILE = "SETTING_HANDLER";
    private static String NOTIFICATION_ENABLED = "NOTIFICATION_ENABLED";
    private static String NOTIFICATION_MODE = "NOTIFICATION_MODE";
    private static String NETWORK_MODE = "NETWORK_MODE";

    private Context context;
    SharedPreferences sharedPref;


    private SettingDataHandlerImpl(Context context){
        this.context = context;
        this.sharedPref = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
    }

    public static SettingDataHandler getInstance(Context context){
        if(settingDataHandler == null){
            settingDataHandler = new SettingDataHandlerImpl(context);
        }
        return settingDataHandler;
    }

    @Override
    public NotificationMode getNotificationMode() {
        String notificationModeString = sharedPref.getString(NOTIFICATION_MODE,NotificationMode.getDefault().name());
        return NotificationMode.valueOf(notificationModeString);
    }

    @Override
    public void setNotificationMode(NotificationMode notificationMode) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(NOTIFICATION_MODE, notificationMode.name());
        editor.commit();
    }

    @Override
    public NetworkMode getNetworkMode() {
        String networkModeString = sharedPref.getString(NETWORK_MODE,NetworkMode.getDefault().name());
        return NetworkMode.valueOf(networkModeString);
    }

    @Override
    public void setNetworkMode(NetworkMode networkMode) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(NETWORK_MODE, networkMode.name());
        editor.commit();
    }

    @Override
    public boolean isNotificationEnabled() {
        return sharedPref.getBoolean(NOTIFICATION_ENABLED,false);
    }

    @Override
    public void setNotificationEnabled(boolean status) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(NOTIFICATION_ENABLED, status);
        editor.commit();
    }
}
