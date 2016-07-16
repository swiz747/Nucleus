package com.tritiumlabs.arthur.servertest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Arthur on 6/7/2016.
 */
public class CommonMethods {
    private static DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
    private static DateFormat timeFormat = new SimpleDateFormat("K:mma");
    private static DateFormat dateTimeFormat = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS.SSS");


    public static String getCurrentTime() {

        Date today = Calendar.getInstance().getTime();
        return timeFormat.format(today);
    }

    public static String getCurrentDate() {

        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    public static String getCurrentDateTime() {

        Date today = Calendar.getInstance().getTime();
        return dateTimeFormat.format(today);
    }

}
