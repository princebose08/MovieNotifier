package com.example.pbose.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pbose on 3/19/16.
 */
public class Movie implements Serializable {
    String name;
    String id;
    List<String> languageList;
    Date lastRefreshDate;
    Location location;

    public Movie(String id,String name,Location location,List<String> languageList,Date lastRefreshDate){
        this(id,name);
        this.languageList = languageList;
        this.lastRefreshDate = lastRefreshDate;
        this.location = location;
    }

    public Movie(String id,String name){
        this.id = id;
        this.name = name;
        this.languageList = new ArrayList<String>();
    }
    public Movie(){

    }

    public String getName(){
        return this.name;
    }

    public String getId(){
        return this.id;
    }

    public List<String> getLanguageList() { return this.languageList; }

    public Date getLastRefreshDate(){ return this.lastRefreshDate; }

    public Location getLocation() { return this.location; }

    public void setId(String id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setLanguageList(List<String> languageList){
        this.languageList = languageList;
    }

    public void setLastRefreshDate(Date lastRefreshDate){
        this.lastRefreshDate = lastRefreshDate;
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public boolean isNull(){
        return id==null || name==null || languageList == null;
    }

    @Override
    public String toString(){
        return "{ name :"+name+
                " id :"+id+
                " language :"+languageList+
                " lastRefreshDate :"+lastRefreshDate+
                " location :"+location+" }";
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;
        if (object != null && object instanceof Movie)
        {
            String objectId = ((Movie) object).id;
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
