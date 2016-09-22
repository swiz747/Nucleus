package com.tritiumlabs.arthur.servertest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Arthur on 9/19/2016.
 */


public class ExternalDBHandler {

    private static LocalDBHandler instance = null;

    private static final String DATABASE_NAME = "RoundAbout";
    // table names
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_SETTINGS = "settings";
    private Context currentContext;

    // Progress Dialog
    private ProgressDialog pDialog;


    // urls
    private static String url_get_locations = "awdawd";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    // products JSONArray
    JSONArray products = null;



    public static synchronized LocalDBHandler getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new LocalDBHandler(context.getApplicationContext());
        }
        return instance;
    }

    public ExternalDBHandler(Context context) {

        // Loading products in Background Thread
        new GetLocations().execute();

    }




    /**
     * Background Async Task  making HTTP Request
     * */
    class GetLocations extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(currentContext);
            pDialog.setMessage("Stalking friends...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All locations from url
         * */
        protected String doInBackground(String... args)
        {
            return null;
        }


    }



        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String result) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();


        }

    }





