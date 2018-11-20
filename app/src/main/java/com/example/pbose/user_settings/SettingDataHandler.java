package com.example.pbose.user_settings;

/**
 * Created by pbose on 4/9/16.
 */
public interface SettingDataHandler {

    public NotificationMode getNotificationMode();
    public void setNotificationMode(NotificationMode notificationMode);

    public NetworkMode getNetworkMode();
    public void setNetworkMode(NetworkMode networkMode);

    public boolean isNotificationEnabled();
    public void setNotificationEnabled(boolean status);
}
