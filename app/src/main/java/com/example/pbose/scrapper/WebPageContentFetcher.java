package com.example.pbose.scrapper;

import com.example.pbose.exceptions.ParsingException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by pbose on 3/19/16.
 */
public class WebPageContentFetcher {

    public Document getDocumentFromUrl(String url) throws ParsingException{
        System.out.println("Fetching url for :"+url);
        try {
            Connection.Response response = Jsoup.connect(url).followRedirects(true).execute();
            if(url.equals(response.url().toString())) {
                return response.parse();
            }else{
                throw new ParsingException(String.format("Crawling failure for url : %s as response url %s didn't match",url,response.url()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParsingException("Crawling failure for url :"+url,e);
        }
    }

    public static void main(String[] args) throws ParsingException {
        System.out.print("Hello");
        Document doc = new WebPageContentFetcher().getDocumentFromUrl("https://in.bookmyshow.com/buytickets/the-revenant-hyderabad/movie-hyd-ET00027851-MT/20160321");
        //System.out.println(doc.outerHtml());

    }
}
