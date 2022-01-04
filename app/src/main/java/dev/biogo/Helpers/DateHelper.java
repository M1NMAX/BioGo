package dev.biogo.Helpers;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        Date date = new Date();
        long now = date.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static long convertToLong(String dateStr, String pattern) {
        long startDate = 0;
        //pattern = "yyyy-MM-dd|HH:mm"
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.UK);
            Date date = sdf.parse(dateStr);
            Log.d("DateH", "convertToLong: "+date.toString());
            startDate = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate;
    }

    public static Date convertToDate(String dateStr, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.UK);
        Date date = null;
        try {
             date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
