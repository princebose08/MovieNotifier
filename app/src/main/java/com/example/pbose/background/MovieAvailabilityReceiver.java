package com.example.pbose.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by pbose on 3/19/16.
 */
public class MovieAvailabilityReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context,MovieAvailabilityCheckerService.class);
        context.startService(i);
    }
}
