package com.example.pbose.movienotifier;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pbose.asyncActivities.FetchMovieListTask;
import com.example.pbose.asyncActivities.FetchTaskParams;
import com.example.pbose.exceptions.IntentNotFoundException;
import com.example.pbose.pageNavigator.PageNavigationData;
import com.example.pbose.pageNavigator.PageNavigationFactory;
import com.example.pbose.pageNavigator.PageType;
import com.example.pbose.types.Movie;
import com.example.pbose.types.Request;
import com.example.pbose.uidata.MovieDataProvider;
import com.example.pbose.uitype.IntentExtraType;
import com.example.pbose.uitype.MovieFilter;
import com.example.pbose.uitype.MovieFilterInputData;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieSelectionActivity extends RequestSchedulingBaseActivity {
    Request request;
    PageNavigationData pageNavigationData;
    MovieDataProvider movieDataProvider;
    FetchMovieListTask fetchMovieListTask;
    ProgressBar loadingImage;
    MovieFilter movieFilter;
    EditText movieEditText;
    MovieArrayAdapter movieArrayAdapter;
    LanguageFilterArrayAdapter languageFilterArrayAdapter;
    boolean isMainPage = true;

    private void renderMainPage(final Request request){
        isMainPage = true;
        setContentView(R.layout.activity_movie_selection);

        if(null == movieFilter){
            movieFilter = new MovieFilter();
        }

        movieEditText = (EditText) findViewById(R.id.movieSelectionEditText);
        if(StringUtils.isNotBlank(movieFilter.getHintString())){
            movieEditText.setText(movieFilter.getHintString());
        }

        this.movieDataProvider = new MovieDataProvider(getApplicationContext());
        List<Movie> movieList = new ArrayList<Movie>();


        movieArrayAdapter = new MovieArrayAdapter(this,R.layout.content_individual_movie,movieList);
        final ListView movieListView = (ListView) findViewById(R.id.movieSelectionListView);

        movieListView.setAdapter(movieArrayAdapter);
        
        fetchMovieListTask = new FetchMovieListTask(getApplicationContext(),movieArrayAdapter);
        fetchMovieListTask.execute(new FetchTaskParams(request.getLocation(),false));
        loadingImage = (ProgressBar)findViewById(R.id.movieSelectionProgressBar);
        loadingImage.setVisibility(View.VISIBLE);

        setMainPageActions(request);
    }

    private void setMainPageActions(Request request){
        movieEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                movieFilter.setHintString(s.toString());
                movieArrayAdapter.updateMovieList(movieArrayAdapter.originalMovieList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.filterButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("filter button clicked");
                Set<String> languageSet = getLanguageSet(movieArrayAdapter.originalMovieList);
                MovieFilterInputData movieFilterInputData = new MovieFilterInputData(languageSet);

                renderFilterPage(movieFilterInputData);
            }
        });
    }

    private void renderFilterPage(MovieFilterInputData movieFilterInput){
        isMainPage = false;
        setContentView(R.layout.activity_movie_filter);

        System.out.println("movieFilterInput" + movieFilterInput);
        System.out.println("movieFilter" + movieFilter);

        languageFilterArrayAdapter = new LanguageFilterArrayAdapter(this,R.layout.content_individual_movie_filter,new ArrayList<String>(movieFilterInput.getTotalLanguageList()),movieFilter.getFilteredLanguageSet());
        final ListView languageFilterListView = (ListView) findViewById(R.id.languageFilterListView);

        languageFilterListView.setAdapter(languageFilterArrayAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title_layout);

        super.onCreate(savedInstanceState);
        pageNavigationData = (PageNavigationData) getIntent().getSerializableExtra(IntentExtraType.PAGE_NAVIGATION_DATA);
        request = pageNavigationData.getRequest();
        movieFilter = request.getMovieFilter();
        renderMainPage(request);
        populateData();
    }

    private Set<String> getLanguageSet(List<Movie> movieList){
        Set<String> languageSet = new HashSet<String>();
        for(Movie movie : movieList){
            languageSet.addAll(movie.getLanguageList());
        }

        return languageSet;
    }

    public class MovieArrayAdapter extends ArrayAdapter<Movie> {
        List<Movie> movieList;
        List<Movie> originalMovieList;
        Context context;
        int resource;

        public MovieArrayAdapter(Context context, int resource, List<Movie> movieList) {
            super(context, resource, movieList);
            this.movieList = movieList;
            this.originalMovieList = movieList;
            this.context = context;
            this.resource = resource;
        }

        @Override
        public int getCount() {
            return movieList.size();
        }

        @Override
        public Movie getItem(int position) {
            return movieList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(resource,parent,false);
            TextView movieNameView = (TextView)view.findViewById(R.id.movieName);
            TextView movieLanguages = (TextView)view.findViewById(R.id.movieLanguages);

            final Movie movie = movieList.get(position);
            String languagesString = StringUtil.join(movie.getLanguageList(),",");

            movieNameView.setText(movie.getName());
            movieLanguages.setText(languagesString);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    request.setMovie(movie);
                    request.setMovieFilter(movieFilter);
                    startNextIntent();
                }
            });
            return view;
        }

        public void refreshMovieList(List<Movie> movieList){
            this.originalMovieList = movieList;
            updateMovieList(movieList);
        }

        public void updateMovieList(List<Movie> movieList){
            this.movieList = getFilteredMovieList(movieList,movieFilter);
            notifyDataSetChanged();
            loadingImage.setVisibility(View.GONE);
        }

        private List<Movie> getFilteredMovieList(List<Movie> movieList,MovieFilter movieFilter) {
            // filter for hint String
            List<Movie> filteredMoviesForHint = getFilteredMoviesForHintString(movieList, movieFilter.getHintString());
            //filter movie for languages
            List<Movie> filteredMoviesForLanguage = getFilteredMoviesForLanguage(filteredMoviesForHint, movieFilter.getFilteredLanguageSet());

            return filteredMoviesForLanguage;

        }

        private List<Movie> getFilteredMoviesForHintString(List<Movie> movieList,String hintString){
            if(StringUtil.isBlank(hintString)){
                return movieList;
            }
            List<Movie> filteredMovieList = new ArrayList<Movie>();
            for(Movie movie : movieList){
                if(movie.getName().toLowerCase().contains(hintString.toLowerCase())){
                    filteredMovieList.add(movie);
                }
            }
            return filteredMovieList;
        }

        private List<Movie> getFilteredMoviesForLanguage(List<Movie> movieList,Set<String> filterLanguageList){
            if(filterLanguageList.isEmpty()){
                return movieList;
            }
            List<Movie> filteredMovieList = new ArrayList<Movie>();

            for(Movie movie : movieList){
                for(String language : filterLanguageList){
                    if(movie.getLanguageList().contains(language)){
                        filteredMovieList.add(movie);
                        break;
                    }
                }
            }
            return filteredMovieList;
        }
    }




    private class LanguageFilterArrayAdapter extends ArrayAdapter<String> {
        private Context context;
        List<String> languages;
        Set<String> selectedLanguages;
        int textViewResourceId;

        public LanguageFilterArrayAdapter(Context context, int textViewResourceId, List<String> languages,Set<String> selectedLanguages) {
            super(context, textViewResourceId,languages);
            this.context = context;
            this.languages = languages;
            this.selectedLanguages = selectedLanguages;
            this.textViewResourceId = textViewResourceId;
        }

        @Override
        public int getCount() {
            return languages.size();
        }

        @Override
        public String getItem(int position) {
            return languages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(textViewResourceId,parent,false);
            CheckBox languageFilterCheckBox = (CheckBox)view.findViewById(R.id.languageFilterCheckBox);
            String checkBoxText = languages.get(position);
            languageFilterCheckBox.setText(checkBoxText);
            if(selectedLanguages.contains(checkBoxText)){
                languageFilterCheckBox.setChecked(true);
            }

            languageFilterCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    if (checkBox.isChecked()) {
                        selectedLanguages.add(checkBox.getText().toString());
                    } else {
                        selectedLanguages.remove(checkBox.getText());
                    }

                    System.out.println(selectedLanguages);
                }
            });
            return view;
        }
    }

    @Override
    public void handleBackClick(View v){
        System.out.println("** Back Button clicked");
        if(isMainPage){
            navigateToBackPage();
        }else{
            // Simulate Done button action for filter
            Set<String> selectedLanguageSet = languageFilterArrayAdapter.selectedLanguages;
            movieFilter.setFilteredLanguageSet(selectedLanguageSet);
            renderMainPage(request);
        }
    }

    @Override
    public void populateData() {
        Movie movie = request.getMovie();
        if(null != movie) {
            movieEditText.setText(movie.getName());
            movieEditText.setSelection(movieEditText.getText().length());
        }
    }

    @Override
    public void handleNextIconClick(){
        // Do nothing as next is hidden
    }

    @Override
    public PageNavigationData getPageNavigationData() {
        return this.pageNavigationData;
    }

    @Override
    public PageType getPageType() {
        return PageType.MOVIE_SELECTION_PAGE;
    }

    @Override
    public void startupSetup(){
        hideNextButton();
        if(!isMainPage){
            hideHomeButton();
        }
    }

}
