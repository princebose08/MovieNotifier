package com.example.pbose.uitype;

import com.example.pbose.types.Location;

/**
 * Created by pbose on 3/30/16.
 */
public class LocationData {
    String displayName;
    String enumName;

    public LocationData(String displayName,String enumName){
        this.displayName = displayName;
        this.enumName = enumName;
    }

    public String getDisplayName(){
        return this.displayName;
    }

    public String getEnumName(){
        return this.enumName;
    }
}
