package com.example.pbose.asyncActivities;

import com.example.pbose.types.Location;

/**
 * Created by pbose on 3/30/16.
 */
public class FetchTaskParams {
    Location location;
    boolean forceRefresh;

    public FetchTaskParams(Location location, boolean forceRefresh){
        this.location = location;
        this.forceRefresh = forceRefresh;
    }

    public Location getLocation(){
        return this.location;
    }

    public boolean isForceRefresh(){
        return this.forceRefresh;
    }

    public void setForceRefresh(boolean forceRefresh){
        this.forceRefresh = forceRefresh;
    }
}
