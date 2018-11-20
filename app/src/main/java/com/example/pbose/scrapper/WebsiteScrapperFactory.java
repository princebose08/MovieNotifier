package com.example.pbose.scrapper;

import com.example.pbose.scrapper.bookmyshow.BookMyShowHelper;

/**
 * Created by pbose on 3/20/16.
 */
public class WebsiteScrapperFactory {
    private static WebsiteScrapperFactory websiteScrapperFactory;

    private WebsiteScrapperFactory(){

    }

    public static WebsiteScrapperFactory getInstance(){
        if(websiteScrapperFactory == null){
            websiteScrapperFactory = new WebsiteScrapperFactory();
        }
        return websiteScrapperFactory;
    }

    public WebsiteScrapperInterface getScrapper(){
        return new BookMyShowHelper();
    }
}
