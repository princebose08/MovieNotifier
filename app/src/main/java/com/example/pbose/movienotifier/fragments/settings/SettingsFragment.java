package com.example.pbose.movienotifier.fragments.settings;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.pbose.background.PeriodicCheckSchedulingHelper;
import com.example.pbose.movienotifier.R;
import com.example.pbose.user_settings.NetworkMode;
import com.example.pbose.user_settings.NotificationMode;
import com.example.pbose.user_settings.SettingDataHandler;
import com.example.pbose.user_settings.SettingDataHandlerImpl;

public class SettingsFragment extends Fragment {

    public static final int NOTIFICATION_MODE_DIALOG = 1;
    public static final int NETWORK_MODE_DIALOG = 2;

    LinearLayout notification_mode_linear_layout;
    LinearLayout network_mode_linear_layout;
    View view;
    SettingDataHandler settingDataHandler;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        //Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);
        settingDataHandler = SettingDataHandlerImpl.getInstance(view.getContext());
        landingSetup();

        Switch notificationSwitch = (Switch) view.findViewById(R.id.notification_enabler_switch);
        notification_mode_linear_layout = (LinearLayout) view.findViewById(R.id.notification_mode_linear_layout);
        network_mode_linear_layout = (LinearLayout) view.findViewById(R.id.network_mode_linear_layout);

        boolean isPeriodicCheckEnabled = PeriodicCheckSchedulingHelper.isPeriodicCheckActive(view);
        notificationSwitch.setChecked(isPeriodicCheckEnabled);
        settingDataHandler.setNotificationEnabled(isPeriodicCheckEnabled);
        enableDisableNotificationRelatedViews(isPeriodicCheckEnabled);

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableDisableNotificationRelatedViews(isChecked);
                settingDataHandler.setNotificationEnabled(isChecked);
                if (!isChecked) {
                    PeriodicCheckSchedulingHelper.stopPeriodicCheck(view.getContext());
                } else {
                    PeriodicCheckSchedulingHelper.startPeriodicCheck(view.getContext());
                }
            }
        });


        notification_mode_linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotificationModeFragment();
            }
        });

        network_mode_linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNetworkModeFragment();
            }
        });

        return view;
    }

    private void landingSetup(){
        TextView notificationModeText = (TextView)view.findViewById(R.id.notification_mode_text);
        notificationModeText.setText(settingDataHandler.getNotificationMode().toString());

        TextView networkModeText = (TextView)view.findViewById(R.id.network_mode_text);
        networkModeText.setText(settingDataHandler.getNetworkMode().toString());

    }

    private void enableDisableNotificationRelatedViews(boolean status){
        enableDisableView(notification_mode_linear_layout, status);
        enableDisableView(network_mode_linear_layout,status);
    }

    private void openNotificationModeFragment(){
        NotificationMode selectedNotificationMode = settingDataHandler.getNotificationMode();
        DialogFragment notificationModeFragment = NotificationModeFragment.getInstance(selectedNotificationMode);
        notificationModeFragment.setTargetFragment(this,NOTIFICATION_MODE_DIALOG);
        notificationModeFragment.show(getFragmentManager(), "Notification Mode");
    }

    private void openNetworkModeFragment(){
        NetworkMode selectedNetworkMode = settingDataHandler.getNetworkMode();
        DialogFragment networkModeFragment = NetworkModeFragment.getInstance(selectedNetworkMode);
        networkModeFragment.setTargetFragment(this,NETWORK_MODE_DIALOG);
        networkModeFragment.show(getFragmentManager(), "Network Mode");
    }

    private void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);

        if ( view instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode){
            case NOTIFICATION_MODE_DIALOG :{
                NotificationMode choosenNotificationMode = (NotificationMode)data.getSerializableExtra(NotificationModeFragment.NOTIFICATION_MODE);
                TextView notificationModeText = (TextView)view.findViewById(R.id.notification_mode_text);
                notificationModeText.setText(choosenNotificationMode.toString());
                settingDataHandler.setNotificationMode(choosenNotificationMode);
                break;
            }
            case NETWORK_MODE_DIALOG :{
                NetworkMode choosenNetworkMode = (NetworkMode)data.getSerializableExtra(NetworkModeFragment.NETWORK_MODE);
                TextView networkModeText = (TextView)view.findViewById(R.id.network_mode_text);
                networkModeText.setText(choosenNetworkMode.toString());
                settingDataHandler.setNetworkMode(choosenNetworkMode);
                break;
            }
        }
    }
}
