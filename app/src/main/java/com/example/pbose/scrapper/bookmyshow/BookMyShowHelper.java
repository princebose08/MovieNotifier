package com.example.pbose.scrapper.bookmyshow;

import com.example.pbose.exceptions.ParsingException;
import com.example.pbose.scrapper.WebPageContentFetcher;
import com.example.pbose.scrapper.WebsiteScrapperInterface;
import com.example.pbose.types.Location;
import com.example.pbose.types.Movie;
import com.example.pbose.types.Request;
import com.example.pbose.types.Theater;
import com.example.pbose.util.TheaterUtil;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by pbose on 3/19/16.
 */
public class BookMyShowHelper implements WebsiteScrapperInterface{

    WebPageContentFetcher webPageContentFetcher;
    private final String BOOK_MY_SHOW_DOMAIN = "https://in.bookmyshow.com";

    public BookMyShowHelper(){
        webPageContentFetcher = new WebPageContentFetcher();
    }

    /**
     * 1. With movie url get contents of its detail page content. Search for class showtimes in div with class more-showtimes
     *     1.1. If div don't exist then return false // It means there are no shows running
     *     1.2. If div exists fetch the show timing url
     * 2. Replace the time with the search time
     * 3. Find the content of the formed url
     *     3.1. If the content is present and theaterUrl is NULL, return true
     *     3.2. If the content is present and theaterUrl is not NULL, find theatreUrl in the page. If present return true else false
     * 4. If content is not found, return false
     * @return
     */
    @Override
    public boolean isMovieAvailable(Request request){
        try {
            List<Theater> theaterList = request.getTheaterList();
            List<String> theaterUrlList = TheaterUtil.getTheaterIdList(theaterList);

            List<String> absoluteTheaterUrlList = getAbsoluteTheaterUrlList(theaterUrlList);

            String showPageLinkWithDate = getAndPopulateShowPageLink(request);
            System.out.println("Show page link with date : "+showPageLinkWithDate);
            if(!StringUtil.isBlank(showPageLinkWithDate)){
                Document showPageDoc = webPageContentFetcher.getDocumentFromUrl(showPageLinkWithDate);
                return isTheaterPresent(showPageDoc,absoluteTheaterUrlList);
            }

        } catch (ParsingException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Movie> getAllMovies(Location location){
        List<Movie> movieList = new ArrayList<Movie>();
        try {
            String pageUrl = BOOK_MY_SHOW_DOMAIN+"/"+location.toString()+"/movies"; // https://in.bookmyshow.com/hyderabad/movies
            Document allMoviePageDoc = webPageContentFetcher.getDocumentFromUrl(pageUrl);
            Elements movieCards = allMoviePageDoc.getElementsByClass("movie-card-container");
            Iterator<Element> movieCardIterator = movieCards.iterator();
            while(movieCardIterator.hasNext()){

                Movie movie = new Movie();
                movie.setLocation(location);

                Element movieCard = movieCardIterator.next();
                populateNameAndIdInMovieData(movieCard, movie);
                populateLanguageInMovieData(movieCard,movie);
                /*Elements movieDetails = movieCard.getElementsByClass("detail");
                if(movieDetails.size() > 0) {
                    Element movieDetailDiv = movieDetails.get(0);
                    populateNameAndIdInMovieData(movieDetailDiv, movie);
                    populateLanguageInMovieData(movieDetailDiv,movie);
                }*/
                movieList.add(movie);

            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        return filterMovieData(movieList);
    }

    @Override
    public List<Theater> getAllTheaters(Location location){
        List<Theater> theaterList = new ArrayList<Theater>();
        try {
            String theaterPageUrl = BOOK_MY_SHOW_DOMAIN + "/" + location.toString() + "/cinemas"; // https://in.bookmyshow.com/hyderabad/cinemas
            Document allMoviePageDoc = webPageContentFetcher.getDocumentFromUrl(theaterPageUrl);
            Elements cinemaDivElements = allMoviePageDoc.getElementsByClass("__cinema-text");
            Iterator<Element> cinemaDivIterator = cinemaDivElements.iterator();

            while(cinemaDivIterator.hasNext()){
                Element cinemaDiv = cinemaDivIterator.next();
                Elements anchorTagElements = cinemaDiv.getElementsByTag("a");
                if(anchorTagElements.size() > 0){
                    Element anchorTag = anchorTagElements.get(0);
                    String theaterUrl = anchorTag.attr("href");
                    String theaterName = anchorTag.text();
                    Theater theater = new Theater(theaterUrl,theaterName);
                    theater.setLocation(location);
                    theaterList.add(theater);
                }
            }
        }catch (ParsingException e){
            e.printStackTrace();
        }

        return theaterList;
    }

    private String getAndPopulateShowPageLink(Request request) throws ParsingException {
        if(!StringUtil.isBlank(request.getScrapperUrl())){
            System.out.println("Fetching url from request");
            return request.getScrapperUrl();
        }

        String movieUrl = request.getMovie().getId();
        String absoluteMovieUrl = BOOK_MY_SHOW_DOMAIN+movieUrl;
        Date searchDate = request.getSearchDate();
        /* get movie page content */
        Document movieUrlDoc = webPageContentFetcher.getDocumentFromUrl(absoluteMovieUrl);
        String showPageLink = getShowPageLink(movieUrlDoc);
        System.out.println("Show page link : "+showPageLink);
        if(StringUtil.isBlank(showPageLink)){
            return null; // 1.1 no active show is running
        }
        /* replace time tag */
        String showPageLinkWithDate = addSearchTimeInShowPageUrl(showPageLink, searchDate);
        /* populate data in request */
        request.setScrapperUrl(showPageLinkWithDate);
        return showPageLinkWithDate;
    }

    private void populateLanguageInMovieData(Element movieCard,Movie movie){
        List<String> languageList = new ArrayList<String>();
        Elements languages = movieCard.getElementsByClass("__language");
        Iterator<Element> languageIterator = languages.iterator();
        while (languageIterator.hasNext()){
            Element language = languageIterator.next();
            languageList.addAll(beautifyLanguageList(Arrays.asList(language.text().split("/"))));
        }

        //hacks where language-list ul has language info
        if(languageList.isEmpty()){
            Elements languageListUL = movieCard.getElementsByClass("language-list");
            if(languageListUL.size() > 0){
                String languageStrings = languageListUL.get(0).text();
                String[] splittedLanguageStrings = languageStrings.split(",");
                for(String splittedLanguageString : splittedLanguageStrings){
                    languageList.addAll(beautifyLanguageList(Arrays.asList(splittedLanguageString.split("/")))); // Languages can be like Hindi/English
                }

            }
        }
        movie.setLanguageList(languageList);
    }

    private List<String> beautifyLanguageList(List<String> languageList){
        List<String> beautifiedLanguageList = new ArrayList<String>();
        for(String language : languageList){
            String beautifiedLang = language.replaceAll("[^a-zA-Z]+", " ").trim();
            if(!StringUtil.isBlank(beautifiedLang)) {
                beautifiedLanguageList.add(StringUtils.capitalize(beautifiedLang.toLowerCase()));
            }
        }
        return beautifiedLanguageList;
    }

    private void populateNameAndIdInMovieData(Element movieCard,Movie movie){
            Elements movieNameAnchors =  movieCard.getElementsByTag("a");
            if(movieNameAnchors.size() > 0){
                Element movieNameAnchor = movieNameAnchors.get(0);
                movie.setId(movieNameAnchor.attr("href"));
                movie.setName(movieNameAnchor.attr("title"));
            }
    }

    private List<Movie> filterMovieData(List<Movie> movieList){
        List<Movie> filteredMovieData = new ArrayList<Movie>();
        Set<String> movieIdSet = new HashSet<String>();
        for(Movie movie : movieList){
            if(!movie.isNull() && !movieIdSet.contains(movie.getId())){
                filteredMovieData.add(movie);
                movieIdSet.add(movie.getId());
            }
        }
        return filteredMovieData;
    }

    private String addSearchTimeInShowPageUrl(String showPageUrl,Date searchDate){
        /* Removing today from url like /buytickets/the-revenant-hyd/movie-hyd-ET00027851-MT/today */
        String stripUrl = showPageUrl.substring(0, showPageUrl.lastIndexOf('/'));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(searchDate);
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = calendar.get(Calendar.MONTH) < 10 ? "0"+String.valueOf(calendar.get(Calendar.MONTH)+1) : String.valueOf(calendar.get(Calendar.MONTH)+1);
        String day = calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) : String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        String dateToAppend = year + month + day;
        return stripUrl +"/"+ dateToAppend;
    }

    private String getShowPageLink(Document movieDoc){
        if(null == movieDoc){
            return null;
        }
        Elements moreShowtimeDivElemets = movieDoc.getElementsByClass("more-showtimes");
        if(moreShowtimeDivElemets.size() > 0){
            Element moreShowtimeDiv = moreShowtimeDivElemets.get(0);
            Elements showTimeElemets = moreShowtimeDiv.getElementsByClass("showtimes");
            if(showTimeElemets.size() > 0){
                Element showTimeAnchor =  showTimeElemets.get(0);
                String relativeShowPageLink = showTimeAnchor.attr("href");
                if(!StringUtil.isBlank(relativeShowPageLink)) {
                    return BOOK_MY_SHOW_DOMAIN+relativeShowPageLink;
                }

            }
        }

        return null;
    }

    private boolean isTheaterPresent(Document showPageDoc,List<String> absoluteTheaterUrlList){
        if(isShowPageDocPresent(showPageDoc)){
            if(null == absoluteTheaterUrlList || absoluteTheaterUrlList.size() <= 0){
                return true; // 3.1
            }else{
                return isTheaterUrlPresent(showPageDoc,absoluteTheaterUrlList);
            }
        }
        return false;
    }

    private boolean isShowPageDocPresent(Document showPageDoc){
        //logic to see if show page present
        return null != showPageDoc;
    }

    private boolean isTheaterUrlPresent(Document showPageDoc,List<String> absoluteTheaterUrlList){
        // extract theaters from page content
        List<String> theaterUrlList = getTheaterUrlFromShowPageDoc(showPageDoc);
        //System.out.println("Theater urls"+theaterUrlList);

        // extract theaterIds from theater list
        List<String> theaterIdList = extractTheaterIds(absoluteTheaterUrlList);
        //System.out.println("Theater Ids"+theaterIdList);

        // check if any of the theaterId is present in theater extracted from page
        for(String theaterId : theaterIdList){
            for(String theaterUrl : theaterUrlList){
                if(theaterUrl.contains(theaterId)){
                    System.out.println(String.format("matched theater Id %s with theater url %s",theaterId,theaterUrl));
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> getTheaterUrlFromShowPageDoc(Document showPageDoc){
        List<String> theaterUrlList = new ArrayList<String>();

        Elements listingInfoElements = showPageDoc.getElementsByClass("listing-info");
        Iterator<Element> listingInfoIterator = listingInfoElements.iterator();
        while(listingInfoIterator.hasNext()){
            Element listingInfo = listingInfoIterator.next();
            Elements venueNameElements = listingInfo.getElementsByClass("__venue-name");
            if(venueNameElements.size() > 0){
                theaterUrlList.add(venueNameElements.get(0).attr("href"));
            }
        }

        return theaterUrlList;
    }

    private List<String> extractTheaterIds(List<String> theaterUrlList){
        List<String> theaterIdList = new ArrayList<String>();
        for(String theaterUrl : theaterUrlList){
            int lastSlashIndex = theaterUrl.lastIndexOf('/');
            theaterIdList.add(theaterUrl.substring(lastSlashIndex+1));
        }

        return theaterIdList;

    }

    private List<String> getAbsoluteTheaterUrlList(List<String> theaterUrlList){
        List<String> absoluteTheaterUrlList = new ArrayList<String>();
        for(String theaterUrl : theaterUrlList){
            absoluteTheaterUrlList.add(BOOK_MY_SHOW_DOMAIN+theaterUrl);
        }
        return absoluteTheaterUrlList;
    }

    public static void main(String[] args) throws ParsingException {
        BookMyShowHelper bookMyShowHelper = new BookMyShowHelper();
        /*Calendar calendar = Calendar.getInstance();
        calendar.set(2016,02,25);
        System.out.println("Creating object of BMS");
        System.out.println(bookMyShowHelper.isMovieAvailable("/hyderabad/movies/the-revenant/ET00027851", null, calendar.getTime()));*/

        /*List<Movie> movieDataList = bookMyShowHelper.getAllMovies(Location.Hyderabad);

        for(Movie movieData : movieDataList){
            System.out.println(movieData);
        }*/

        /*Document doc = bookMyShowHelper.webPageContentFetcher.getDocumentFromUrl("https://in.bookmyshow.com/buytickets/batman-v-superman-dawn-of-justice-3d-hyderabad/movie-hyd-ET00030143-MT/20160330");
        List<String> theaterList = Arrays.asList("/cinemas/prasads-large-screen/PRHY");
        System.out.println(bookMyShowHelper.isTheaterPresent(doc,theaterList));*/

        /*List<Theater> theaters = bookMyShowHelper.getAllTheaters(Location.Hyderabad);
        System.out.println(theaters);*/
        /*for(Theater theater : theaters){
            System.out.
        }*/

    }

}
