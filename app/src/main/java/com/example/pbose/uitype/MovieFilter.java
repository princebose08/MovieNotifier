package com.example.pbose.uitype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pbose on 3/31/16.
 */
public class MovieFilter implements Serializable{
    String hintString;
    Set<String> filteredLanguageSet;

    public MovieFilter(){
        filteredLanguageSet = new HashSet<String>();
    }
    public String getHintString(){
        return this.hintString;
    }

    public Set<String> getFilteredLanguageSet(){
        return this.filteredLanguageSet;
    }

    public void setHintString(String hintString){
        this.hintString = hintString;
    }

    public void setFilteredLanguageSet(Set<String> filteredLanguageSet){
        this.filteredLanguageSet = filteredLanguageSet;
    }

    public void addFilteredLanguage(String filteredLanguage){
        this.filteredLanguageSet.add(filteredLanguage);
    }

    @Override
    public String toString(){
        return "{ hintString : "+hintString +
                " filteredLanguageSet : "+this.filteredLanguageSet +"}";

    }

}
