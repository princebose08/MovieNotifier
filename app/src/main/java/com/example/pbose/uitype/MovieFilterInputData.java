package com.example.pbose.uitype;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by pbose on 3/31/16.
 */
public class MovieFilterInputData implements Serializable{
    Set<String> totalLanguageList;

    public MovieFilterInputData(Set<String> totalLanguageList){
        this.totalLanguageList = totalLanguageList;
    }

    public Set<String> getTotalLanguageList(){
        return this.totalLanguageList;
    }

    @Override
    public String toString(){
        return this.totalLanguageList.toString();
    }
}
