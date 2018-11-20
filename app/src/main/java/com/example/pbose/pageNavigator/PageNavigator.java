package com.example.pbose.pageNavigator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.pbose.movienotifier.LandingPageActivity;
import com.example.pbose.movienotifier.R;
import com.example.pbose.uitype.IntentExtraType;

/**
 * Created by pbose on 4/12/16.
 */
public class PageNavigator {

    public static void goToHomePage(Context context,Activity activity){
        goToLandingPageItem(context, activity, R.id.home);
    }

    public static void goToScheduledNotificationPage(Context context,Activity activity){
        goToLandingPageItem(context,activity,R.id.scheduled_notification);
    }

    public static void goToCompletedNotificationPage(Context context,Activity activity){
        goToLandingPageItem(context, activity, R.id.completed_notification);
    }

    public static void goToFailedNotificationPage(Context context,Activity activity){
        goToLandingPageItem(context, activity, R.id.failed_notification);
    }

    private static void goToLandingPageItem(Context context,Activity activity,int itemId){
        Intent requestDisplayIntent = new Intent(context,LandingPageActivity.class);
        requestDisplayIntent.putExtra(IntentExtraType.SELECTED_NAV_ITEM, itemId);
        activity.startActivity(requestDisplayIntent);
    }
}
