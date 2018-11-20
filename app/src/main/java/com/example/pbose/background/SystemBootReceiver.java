package com.example.pbose.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.pbose.user_settings.SettingDataHandler;
import com.example.pbose.user_settings.SettingDataHandlerImpl;

/**
 * Created by pbose on 4/9/16.
 */
public class SystemBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("****** System Boot received *******");
        SettingDataHandler settingDataHandler = SettingDataHandlerImpl.getInstance(context);
        if(settingDataHandler.isNotificationEnabled()){
            PeriodicCheckSchedulingHelper.startPeriodicCheck(context);
        }else{
            PeriodicCheckSchedulingHelper.stopPeriodicCheck(context);
        }
    }
}
