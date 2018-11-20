package com.example.pbose.movienotifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.pbose.background.MovieAvailabilityReceiver;
import com.example.pbose.persistence.MovieDataStorage;
import com.example.pbose.persistence.SQLiteMovieDataStorage;
import com.example.pbose.types.Request;
import com.example.pbose.uitype.IntentExtraType;

public class MainActivity extends AppCompatActivity {

    MovieDataStorage movieDataStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        movieDataStorage = SQLiteMovieDataStorage.getInstance(getApplicationContext());


        Button addNotifier = (Button) findViewById(R.id.add_notifier);

        addNotifier.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("here button clicked");
                /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();*/
                /*Movie movie = new Movie("/hyderabad/movies/batman-v-superman-dawn-of-justice-3d/ET00030143","batman_superman");
                Theater theater1 = new Theater("/cinemas/prasads-large-screen/PRHY","prasads large screen");
                Theater theater2 = new Theater("/cinemas/pvr-forum-sujana-mall-kukatpally/PVSF","forum");

                Calendar calendar = Calendar.getInstance();
                calendar.set(2016,03,1);

                Request req = new Request(movie, Arrays.asList(theater2),calendar.getTime(), Location.HYDERABAD);
                boolean status = movieDataStorage.addRequest(req);
                System.out.println("status of insertion is : " + status);
                System.out.println("Requests fetched from DB :");

                List<Request> requestList =  movieDataStorage.getRequests();
                for(Request request : requestList){
                    System.out.println(request);
                }*/

                //new FetchMovieListTask(getApplicationContext(),false).execute(Location.HYDERABAD);

                Intent countrySelectionIntent = new Intent(getApplicationContext(),LocationSelectionActivity.class);
                Request request = new Request();
                countrySelectionIntent.putExtra(IntentExtraType.REQUEST_TYPE,request);
                //countrySelectionIntent.p
                startActivity(countrySelectionIntent);

            }
        }));

        scheduleAlarm();


    }

    // Setup a recurring alarm every Minute
    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), MovieAvailabilityReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MovieAvailabilityReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                5*60 * 1000, pIntent);

        System.out.print("Alarm scheduled");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
