package com.example.pbose.pageNavigator;

import com.example.pbose.types.Request;

import java.io.Serializable;

/**
 * Created by pbose on 4/10/16.
 */
public class PageNavigationData implements Serializable {
    Request request;
    PageType referrerPageType;

    public PageNavigationData(Request request){
        this.request = request;
    }

    public Request getRequest(){
        return this.request;
    }

    public PageType getReferrerPageType(){
        return this.referrerPageType;
    }

    public void setReferrerPageType(PageType referrerPageType){
        this.referrerPageType = referrerPageType;
    }
}
