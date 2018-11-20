package com.example.pbose.uitype;

import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.pbose.movienotifier.R;
import com.example.pbose.types.Request;
import com.example.pbose.util.BulletTextUtil;
import com.example.pbose.util.TheaterUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pbose on 4/3/16.
 */
public class RequestDisplayUtil {

    public static void populateRequestData(View view,Request request){
        TextView locationTextView = (TextView) view.findViewById(R.id.locationConfirmationText);
        locationTextView.setText(request.getLocation().getDisplayName());

        TextView movieTextView = (TextView) view.findViewById(R.id.movieConfirmationText);
        movieTextView.setText(request.getMovie().getName());

        CharSequence bulletedList = new BulletTextUtil().makeBulletList(2, TheaterUtil.getTheaterNameList(request.getTheaterList()));
        TextView theaterTextView = (TextView) view.findViewById(R.id.theaterConfirmationText);
        theaterTextView.setText(bulletedList);

        TextView dateConfirmationTextView = (TextView) view.findViewById(R.id.dateConfirmationText);

        dateConfirmationTextView.setText(getDisplayDate(request.getSearchDate()));

        if(request.getCompletionDate() != null) {
            TableRow tableRow = (TableRow)view.findViewById(R.id.completionDateRow);
            tableRow.setVisibility(View.VISIBLE);
            TextView dateCompletionTextView = (TextView) view.findViewById(R.id.completionDate);
            dateCompletionTextView.setText(getDisplayDate(request.getCompletionDate()));
        }
    }

    private static String getDisplayDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d");
        String displayDate = simpleDateFormat.format(date);
        return displayDate;
    }

}
