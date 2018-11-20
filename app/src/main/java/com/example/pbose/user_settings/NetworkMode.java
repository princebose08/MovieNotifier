package com.example.pbose.user_settings;

import java.io.Serializable;

/**
 * Created by pbose on 4/8/16.
 */
public enum NetworkMode implements Serializable{
    WIFI_ONLY("Wifi"),
    WIFI_AND_MOBILE("Wifi and Mobile");

    private final String networkMode;

    private NetworkMode(String networkMode){
        this.networkMode = networkMode;
    }

    @Override
    public String toString(){
        return networkMode;
    }

    public static String[] getDisplayNames(NetworkMode[] networkModes) {
        String[] names = new String[networkModes.length];

        for (int i = 0; i < networkModes.length; i++) {
            names[i] = networkModes[i].toString();
        }

        return names;
    }

    public static NetworkMode getDefault(){
        return NetworkMode.WIFI_AND_MOBILE;
    }

}

