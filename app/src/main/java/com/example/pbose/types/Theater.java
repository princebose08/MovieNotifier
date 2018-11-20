package com.example.pbose.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pbose on 3/19/16.
 */
public class Theater implements Serializable {
    String name;
    String id;
    Location location;
    Date lastRefreshDate;

    public Theater(String id,String name,Location location,Date lastRefreshDate){
        this(id,name);
        this.location = location;
        this.lastRefreshDate = lastRefreshDate;
    }

    public Theater(String id,String name){
        this.id = id;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String getId(){
        return this.id;
    }

    public Location getLocation() { return this.location; }

    public Date getLastRefreshDate(){ return this.lastRefreshDate; }

    public void setLocation(Location location){
        this.location = location;
    }

    public void setLastRefreshDate(Date lastRefreshDate){
        this.lastRefreshDate = lastRefreshDate;
    }

    @Override
    public String toString(){
        return "{ name :"+name+
                "id :"+id+
                "location :"+location+
                "lastRefresh :"+lastRefreshDate +" }";
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;
        if (object != null && object instanceof Theater)
        {
            String objectId = ((Theater) object).id;
            sameSame = this.id.equals(objectId);
        }
        return sameSame;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.id.hashCode();
        return hash;
    }
}
