package com.example.pbose.movienotifier.fragments.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.pbose.user_settings.NetworkMode;

/**
 * Created by pbose on 4/9/16.
 */
public class NetworkModeFragment extends DialogFragment {

    public static String NETWORK_MODE = "NETWORK_MODE";
    private NetworkMode currentSelection;


    public static NetworkModeFragment getInstance(NetworkMode networkMode){
        NetworkModeFragment networkModeFragment = new NetworkModeFragment();
        Bundle bundleArg = new Bundle();
        bundleArg.putSerializable(NETWORK_MODE, networkMode);
        networkModeFragment.setArguments(bundleArg);

        return networkModeFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final NetworkMode[] networkModeArr = NetworkMode.values();
        final String[] displayNetworkModeArr = NetworkMode.getDisplayNames(networkModeArr);
        final NetworkMode selectedNetworkMode = (NetworkMode)getArguments().getSerializable(NETWORK_MODE);
        this.currentSelection = selectedNetworkMode;
        System.out.println(networkModeArr);
        System.out.println(selectedNetworkMode);

        int selectedPosition = findPosition(networkModeArr,selectedNetworkMode);
        System.out.println(selectedPosition);
        builder.setTitle("Choose Network Mode")
                .setSingleChoiceItems(displayNetworkModeArr,selectedPosition,new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("item clicked");
                        currentSelection = networkModeArr[which];

                    }
                })
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Ok clicked" + which);
                        Intent intent = getActivity().getIntent();
                        intent.putExtra(NETWORK_MODE,currentSelection);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("cancel clicked");
            }
        });

        return builder.create();
    }

    private int findPosition(NetworkMode[] networkModes,NetworkMode selectedNetworkMode){
        for(int i=0;i<networkModes.length;i++){
            if(networkModes[i].equals(selectedNetworkMode)){
                return i;
            }
        }
        return 0;
    }

}
