package com.example.pbose.background;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.pbose.movienotifier.LandingPageActivity;
import com.example.pbose.movienotifier.MainActivity;
import com.example.pbose.movienotifier.R;
import com.example.pbose.persistence.MovieDataStorage;
import com.example.pbose.persistence.SQLiteMovieDataStorage;
import com.example.pbose.scrapper.WebsiteScrapperFactory;
import com.example.pbose.scrapper.WebsiteScrapperInterface;
import com.example.pbose.scrapper.bookmyshow.BookMyShowHelper;
import com.example.pbose.types.Movie;
import com.example.pbose.types.Request;
import com.example.pbose.types.Status;
import com.example.pbose.types.Theater;
import com.example.pbose.uitype.IntentExtraType;
import com.example.pbose.user_settings.NetworkMode;
import com.example.pbose.user_settings.NotificationMode;
import com.example.pbose.user_settings.SettingDataHandler;
import com.example.pbose.user_settings.SettingDataHandlerImpl;
import com.example.pbose.util.NetworkUtil;
import com.example.pbose.util.TheaterUtil;

import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by pbose on 3/19/16.
 */
public class MovieAvailabilityCheckerService extends IntentService {

    MovieDataStorage movieDataStorage;
    WebsiteScrapperInterface websiteScrapperInterface;
    WebsiteScrapperFactory websiteScrapperFactory;
    SettingDataHandler settingDataHandler;

    public MovieAvailabilityCheckerService(){
        super("MovieAvailabilityCheckerService");
        websiteScrapperFactory = WebsiteScrapperFactory.getInstance();
        websiteScrapperInterface = websiteScrapperFactory.getScrapper();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        movieDataStorage = SQLiteMovieDataStorage.getInstance(getApplicationContext());
        settingDataHandler = SettingDataHandlerImpl.getInstance(getApplicationContext());
        System.out.println("Executing periodic checking");
/*        Request dummy = new Request();
        dummy.setMovie(new Movie("aaa","bbbb"));
        handleSuccess(dummy);
        return;*/
        //Query Database to fetch all the scheduled requests
        List<Request> scheduledRequests = movieDataStorage.getRequestsByStatus(Status.SCHEDULED);
        List<Request> filteredRequests = removeAndNotifyInvalidRequests(scheduledRequests);

        if(isNetworkCallAllowed(getApplicationContext())) {
            for (Request scheduledRequest : filteredRequests) {
                //for each request call bookMyShow to know if movie is available
                boolean isMovieAvailable = websiteScrapperInterface.isMovieAvailable(scheduledRequest);
                if (isMovieAvailable) {
                    handleSuccess(scheduledRequest);
                } else {
                    System.out.println("Movie : " + scheduledRequest.getMovie() + " is still not available");
                }
            }
        }else{
            System.out.println("Skipping checking as network connectivity not found.");
        }
        movieDataStorage.updateRequests(scheduledRequests);
    }

    private void handleSuccess(Request request) {
        System.out.println("Yay ! Movie" + request.getMovie() + " is available");
        request.setStatus(Status.DONE);
        request.setCompletionDate(new Date());
        //Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        //Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification1);
        //r.play();
        //Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        long[] pattern = {0, 3000, 1000, 3000, 1000,3000, 1000,3000, 1000,3000, 1000,3000, 1000,3000, 1000,3000, 1000,3000, 1000,3000, 1000,3000, 1000};
        NotificationMode notificationMode = settingDataHandler.getNotificationMode();
        Uri notificationSoundUri = notificationMode.getUri();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo))
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle(request.getMovie().getName())
                .setContentText("Movie is Open. Book Now !!!")
                .setSound(notificationSoundUri, AudioManager.STREAM_RING)
                .setAutoCancel(true);

        if(notificationMode != NotificationMode.DEFAULT_NOTIFICATION){
            mBuilder.setVibrate(pattern);
        }
        Intent resultIntent = new Intent(this, LandingPageActivity.class);
        resultIntent.putExtra(IntentExtraType.SELECTED_NAV_ITEM, R.id.completed_notification);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LandingPageActivity.class);
        stackBuilder.addNextIntent(resultIntent);


        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 001;

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }



    private void handleFailure(List<Request> requestList) {
        for(Request request : requestList) {
            System.out.println("Movie Request Failed" + request.getMovie() + " is ** not ** available");
            request.setStatus(Status.FAILED);
            request.setCompletionDate(new Date());

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_logo)
                    .setContentTitle(request.getMovie().getName())
                    .setContentText("Oops !! Looks like movie is not available.")
                    .setSound(notification, AudioManager.STREAM_RING)
                    .setAutoCancel(true);

            Intent resultIntent = new Intent(this, LandingPageActivity.class);
            resultIntent.putExtra(IntentExtraType.SELECTED_NAV_ITEM, R.id.failed_notification);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(LandingPageActivity.class);
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            int mNotificationId = 002;

            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
    }

    private boolean isNetworkCallAllowed(Context context){
        if(!NetworkUtil.isConnected(getApplicationContext())){
            return false;
        }

        NetworkMode networkMode = settingDataHandler.getNetworkMode();

        switch(networkMode){
            case WIFI_ONLY: {
                return NetworkUtil.isConnectedWifi(context);
            }
            case WIFI_AND_MOBILE:{
                return NetworkUtil.isConnectedWifi(context)
                        || NetworkUtil.isConnectedMobile(context);
            }
            default: return false;
        }

    }

    private List<Request> removeAndNotifyInvalidRequests(List<Request> scheduledRequestList){
        List<Request> filteredRequestList = new ArrayList<Request>();
        List<Request> failedRequestList = new ArrayList<Request>();
        Calendar currentDate = Calendar.getInstance();
        for(Request request : scheduledRequestList){
            Calendar requestSearchDate = Calendar.getInstance();
            requestSearchDate.setTime(request.getSearchDate());
            if(DateUtils.isSameDay(currentDate,requestSearchDate) || requestSearchDate.after(currentDate)){
                filteredRequestList.add(request);
            }else{
                failedRequestList.add(request);
            }

        }
        handleFailure(failedRequestList);
        return filteredRequestList;
    }
}
