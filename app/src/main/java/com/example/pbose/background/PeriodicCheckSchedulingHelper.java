package com.example.pbose.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by pbose on 4/4/16.
 */
public class PeriodicCheckSchedulingHelper {

    public static void startPeriodicCheck(Context context){
        PendingIntent pendingIntent = getPendingIntent(context);
        AlarmManager alarm = getAlarmManagerForPeriodicCheck(context);
        long firstMillis = System.currentTimeMillis(); // alarm is set right away

        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

       // alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
       //                 1*60*1000, pendingIntent);

        System.out.println("Alarm is activated");

    }

    public static void stopPeriodicCheck(Context context){
        PendingIntent pendingIntent = getPendingIntent(context);
        AlarmManager alarm = getAlarmManagerForPeriodicCheck(context);
        pendingIntent.cancel();
        alarm.cancel(pendingIntent);
        System.out.println("Alarm is cancelled");
    }

    public static boolean isPeriodicCheckActive(View view){
        Intent intent = new Intent(view.getContext(), MovieAvailabilityReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(view.getContext(), MovieAvailabilityReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_NO_CREATE);
        return pIntent != null;

    }

    private static PendingIntent getPendingIntent(Context context){
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, MovieAvailabilityReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, MovieAvailabilityReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pIntent;
    }

    private static AlarmManager getAlarmManagerForPeriodicCheck(Context context){
        // Setup periodic alarm every 5 seconds
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        return alarm;
    }
}
