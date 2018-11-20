package com.example.pbose.types;

import com.example.pbose.uitype.MovieFilter;

import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by pbose on 3/19/16.
 */
public class Request implements Serializable {
    private int id;
    private Movie movie;
    private MovieFilter movieFilter;
    private List<Theater> theaterList;
    private Date searchDate;
    private Date completionDate;
    private Location location;
    private Status status;
    private String scrapperUrl;

    public Request(){

    }

    public Request(int id,Movie movie,List<Theater> theaterList,Date searchDate,Location location){
        this(movie,theaterList,searchDate,location);
        this.id = id;
    }

    public Request(Movie movie,List<Theater> theaterList,Date searchDate,Location location){
        this.movie = movie;
        this.theaterList = theaterList;
        this.searchDate = searchDate;
        this.location = location;
    }

    public int getId(){
        return id;
    }
    public Movie getMovie(){
        return this.movie;
    }

    public List<Theater> getTheaterList(){
        return this.theaterList;
    }

    public Date getSearchDate(){
        return this.searchDate;
    }
    public Date getCompletionDate(){
        return this.completionDate;
    }

    public Location getLocation(){
        return this.location;
    }

    public Status getStatus(){
        return this.status;
    }
    public String getScrapperUrl(){ return this.scrapperUrl;}
    public MovieFilter getMovieFilter(){ return this.movieFilter;}

    public void setId(int id){
        this.id = id;
    }
    public void setMovie(Movie movie){
        this.movie = movie;
    }

    public void setTheater(List<Theater> theaterList){
        this.theaterList = theaterList;
    }

    public void setSearchDate(Date searchDate){
        this.searchDate = searchDate;
    }
    public void setCompletionDate(Date completionDate){
        this.completionDate = completionDate;
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public void setStatus(Status status){
        this.status = status;
    }
    public void setScrapperUrl(String scrapperUrl){ this.scrapperUrl = scrapperUrl;}
    public void setMovieFilter(MovieFilter movieFilter){
        this.movieFilter = movieFilter;
    }

    @Override
    public String toString(){
        return "{ id : "+id+
        "movie : "+movie+
        "theater :"+theaterList+
        "searchDate :"+searchDate+
        "Location :"+location+
        "ScrapperUrl :"+scrapperUrl+
        "Status :" +status+" }";
    }


    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;
        if (object != null && object instanceof Request)
        {
            Request objectRequest = ((Request) object);
            Movie objectMovie = objectRequest.movie;
            sameSame = this.movie.equals(objectMovie);

            List<Theater> objectTheaterList = objectRequest.theaterList;
            sameSame = sameSame && CollectionUtils.isEqualCollection(this.theaterList,objectTheaterList);

            sameSame = sameSame && (this.searchDate.getTime() == objectRequest.getSearchDate().getTime());

            sameSame = sameSame && (this.location == objectRequest.getLocation());
        }
        return sameSame;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.movie.hashCode();
        hash = 7 * hash + this.searchDate.hashCode();
        return hash;
    }

    public static Comparator<Request> CompletionDateComparator = new Comparator<Request>() {
        @Override
        public int compare(Request lhs, Request rhs) {
            Date lhsCompletionDate = lhs.getCompletionDate();
            Date rhsCompletionDate = rhs.getCompletionDate();
            if(lhsCompletionDate != null && rhsCompletionDate != null) {
                return (int)(rhs.getCompletionDate().getTime() - lhs.getCompletionDate().getTime());
            }

            return -1000000000;
        }
    };

    public static void main(String[] args){
        System.out.println("#####");
        Movie movie1 = new Movie("aaa1","bbb1");
        Movie movie2 = new Movie("aaa2","bbb2");
        Movie movie3 = new Movie("aaa3","bbb3");

        Theater theater1 = new Theater("theater1","theaterId1");
        Theater theater2 = new Theater("theater2","theaterId2");
        Theater theater3 = new Theater("theater3","theaterId3");


        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2016,04,02);

        Calendar calendar2 = Calendar.getInstance();
        calendar1.set(2016,04,01);

        Calendar calendar3 = Calendar.getInstance();
        calendar1.set(2016,04,16);

        Request req1 = new Request(movie1, Arrays.asList(theater2),calendar1.getTime(),Location.Hyderabad);
        req1.setCompletionDate(calendar1.getTime());

        Request req2 = new Request(movie2, Arrays.asList(theater2,theater3),calendar1.getTime(),Location.Hyderabad);
        req2.setCompletionDate(calendar2.getTime());

        Request req3 = new Request(movie3, Arrays.asList(theater1,theater2,theater3),calendar1.getTime(),Location.Hyderabad);
        req2.setCompletionDate(calendar3.getTime());

        List<Request> requestList = new ArrayList<Request>();
        requestList.add(req1);
        requestList.add(req2);
        requestList.add(req3);

        Collections.sort(requestList,Request.CompletionDateComparator);

        System.out.println(requestList);
    }
}
