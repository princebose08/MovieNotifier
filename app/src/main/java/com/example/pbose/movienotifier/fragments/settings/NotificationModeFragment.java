package com.example.pbose.movienotifier.fragments.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.pbose.movienotifier.R;
import com.example.pbose.user_settings.NotificationMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pbose on 4/8/16.
 */
public class NotificationModeFragment extends DialogFragment {

    public static String NOTIFICATION_MODE = "NOTIFICATION_MODE";
    private NotificationMode currentSelection;
    private Ringtone ringtone;


    public static NotificationModeFragment getInstance(NotificationMode notificationMode){
        NotificationModeFragment notificationModeFragment = new NotificationModeFragment();
        Bundle bundleArg = new Bundle();
        bundleArg.putSerializable(NOTIFICATION_MODE, notificationMode);
        notificationModeFragment.setArguments(bundleArg);


        return notificationModeFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        System.out.println("*********"+getActivity().getPackageName()+"*********");
        //default
        ringtone = RingtoneManager.getRingtone(getActivity().getApplicationContext(), NotificationMode.getDefault().getUri());

        final NotificationMode[] notificationModeArr = NotificationMode.values();
        final String[] displayNotificationModeArr = NotificationMode.getDisplayNames(notificationModeArr);
        NotificationMode selectedNotificationMode = (NotificationMode)getArguments().getSerializable(NOTIFICATION_MODE);
        this.currentSelection = selectedNotificationMode;

        int selectedPosition = findPosition(notificationModeArr,selectedNotificationMode);
        builder.setTitle("Choose Notification Mode")
                .setSingleChoiceItems(displayNotificationModeArr,selectedPosition,new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("item clicked");
                        currentSelection = notificationModeArr[which];
                        playSound(currentSelection);

                    }
                })
        .setPositiveButton("Set", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Ok clicked" + which);
                ringtone.stop();
                Intent intent = getActivity().getIntent();
                intent.putExtra(NOTIFICATION_MODE,currentSelection);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("cancel clicked");
                ringtone.stop();
            }
        });

        return builder.create();
    }

    private void playSound(NotificationMode notificationMode){
        ringtone.stop();
        ringtone = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notificationMode.getUri());
        ringtone.play();

    }

    private int findPosition(NotificationMode[] notificationList,NotificationMode selectedNotification){
        for(int i=0;i<notificationList.length;i++){
            if(notificationList[i].equals(selectedNotification)){
                return i;
            }
        }

        return 0;
    }

}
