package com.example.pbose.user_settings;

import android.media.RingtoneManager;
import android.net.Uri;

import com.example.pbose.movienotifier.R;

import java.io.Serializable;

/**
 * Created by pbose on 4/8/16.
 */
public enum NotificationMode implements Serializable{
    RINGTONE("Ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)),
    DEFAULT_NOTIFICATION("Default Notification",RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)),
    REGULAR_ALARM("Tring Tring",Uri.parse("android.resource://com.example.pbose.movienotifier/" + R.raw.regular_alarm)),
    BIRDS("BIRDS",Uri.parse("android.resource://com.example.pbose.movienotifier/" + R.raw.birds)),
    BUZZER("Buzzer",Uri.parse("android.resource://com.example.pbose.movienotifier/" + R.raw.buzzer)),
    CLASSICAL("Classical",Uri.parse("android.resource://com.example.pbose.movienotifier/" + R.raw.classical));

    private final String notificationMode;
    Uri uri;

    private NotificationMode(String notificationMode,Uri uri){
        this.notificationMode = notificationMode;
        this.uri = uri;
    }

    @Override
    public String toString(){
        return notificationMode;
    }

    public Uri getUri(){
        return uri;
    }

    public static String[] getDisplayNames(NotificationMode[] notificationModes) {
        String[] names = new String[notificationModes.length];

        for (int i = 0; i < notificationModes.length; i++) {
            names[i] = notificationModes[i].toString();
        }

        return names;
    }

    public static NotificationMode getDefault(){
        return NotificationMode.DEFAULT_NOTIFICATION;
    }
}
