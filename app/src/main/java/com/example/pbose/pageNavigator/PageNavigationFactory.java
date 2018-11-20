package com.example.pbose.pageNavigator;

import android.content.Context;
import android.content.Intent;

import com.example.pbose.exceptions.IntentNotFoundException;
import com.example.pbose.movienotifier.ConfirmationPageActivity;
import com.example.pbose.movienotifier.DateSelectionActivity;
import com.example.pbose.movienotifier.LandingPageActivity;
import com.example.pbose.movienotifier.LocationSelectionActivity;
import com.example.pbose.movienotifier.MovieSelectionActivity;
import com.example.pbose.movienotifier.TheaterSelectionActivity;
import com.example.pbose.uitype.IntentExtraType;

import java.util.LinkedList;

/**
 * Created by pbose on 4/10/16.
 */
public class PageNavigationFactory {

    public static LinkedList<PageType> REQUEST_NAVIGATION_CLASS_LIST = new LinkedList<PageType>();
    static {
        REQUEST_NAVIGATION_CLASS_LIST.push(PageType.CONFIRMATION_PAGE);
        REQUEST_NAVIGATION_CLASS_LIST.push(PageType.DATE_SELECTION_PAGE);
        REQUEST_NAVIGATION_CLASS_LIST.push(PageType.THEATER_SELECTION_PAGE);
        REQUEST_NAVIGATION_CLASS_LIST.push(PageType.MOVIE_SELECTION_PAGE);
        REQUEST_NAVIGATION_CLASS_LIST.push(PageType.LOCATION_SELECTION_PAGE);
        REQUEST_NAVIGATION_CLASS_LIST.push(PageType.LANDING_PAGE);

    }

    /**
     * 1. From Location Page always go to the next page.
     * 2. From Movie selection page go to referrer page if present else to the next page
     * 3. From Theater selection page go to referrer page if present else to the next page
     * 4. From Date selection page,  go to referrer page if present else to the next page
     * @param pageNavigationData
     * @return
     */
    public static Intent getNextIntent(PageNavigationData pageNavigationData,PageType requestPageType,Context context) throws IntentNotFoundException{

        PageType nextPageType = getNextPageType(pageNavigationData,requestPageType);
        System.out.println("Next page = "+nextPageType);
        Intent intent = new Intent(context,nextPageType.getPageClass());
        intent.putExtra(IntentExtraType.PAGE_NAVIGATION_DATA, pageNavigationData);
        return intent;

    }

    public static Intent getPreviousIntent(PageNavigationData pageNavigationData,PageType requestPageType,Context context) throws IntentNotFoundException{

        PageType prevPageType = getImmediatePrevPageType(requestPageType);
        pageNavigationData.setReferrerPageType(null);
        System.out.println("Prev page = "+prevPageType);
        Intent intent = new Intent(context,prevPageType.getPageClass());
        intent.putExtra(IntentExtraType.PAGE_NAVIGATION_DATA, pageNavigationData);
        return intent;

    }

    public static Intent getIntent(PageNavigationData pageNavigationData,PageType targetPageType,PageType referrerPageType,Context context){
        return getIntent(pageNavigationData,targetPageType,referrerPageType,context,false);
    }

    public static Intent getIntent(PageNavigationData pageNavigationData,PageType targetPageType,PageType referrerPageType,Context context,boolean setReferrer){
        Intent intent = new Intent(context,targetPageType.getPageClass());
        if(setReferrer) {
            pageNavigationData.setReferrerPageType(referrerPageType);
        }
        intent.putExtra(IntentExtraType.PAGE_NAVIGATION_DATA, pageNavigationData);
        return intent;
    }

    private static PageType getNextPageType(PageNavigationData pageNavigationData,PageType requestPageType) throws IntentNotFoundException{
        PageType referrerPageType = pageNavigationData.getReferrerPageType();
        if(null == referrerPageType){
            return getImmediateNextPageType(requestPageType);
        }else{
            pageNavigationData.setReferrerPageType(null); // removing referrer from data
            if(requestPageType == PageType.LOCATION_SELECTION_PAGE ||
                    requestPageType == PageType.LANDING_PAGE){
                return getImmediateNextPageType(requestPageType);
            }else{
                return referrerPageType;
            }
        }
    }

    private static PageType getImmediateNextPageType(PageType requestPageType) throws IntentNotFoundException {
        int currentPageIndex = REQUEST_NAVIGATION_CLASS_LIST.indexOf(requestPageType);
        int nextPageIndex = currentPageIndex + 1;
        if(nextPageIndex < REQUEST_NAVIGATION_CLASS_LIST.size()){
            return REQUEST_NAVIGATION_CLASS_LIST.get(nextPageIndex);
        }
        throw new IntentNotFoundException("No Next page type found for request page type : "+requestPageType);
    }

    private static PageType getImmediatePrevPageType(PageType requestPageType) throws IntentNotFoundException {
        int currentPageIndex = REQUEST_NAVIGATION_CLASS_LIST.indexOf(requestPageType);
        int nextPageIndex = currentPageIndex - 1;
        if(nextPageIndex >= 0){
            return REQUEST_NAVIGATION_CLASS_LIST.get(nextPageIndex);
        }
        throw new IntentNotFoundException("No Previous page type found for request page type : "+requestPageType);
    }
}
