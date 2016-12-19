package com.example.android.sunshine.watchface;

import java.text.SimpleDateFormat;

/**
 * Created by Majeed on 10-12-2016.
 */

public class Utility {

    public static String getDayString(Long l){
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        return df.format(l);
    }

    public static String getDateString(Long l){
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy");

        return df.format(l);
    }
}
