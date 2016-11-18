package com.tritiumlabs.arthur.nucleus;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Arthur on 9/28/2016.
 */

public class LocationRetriever implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    public static LocationRetriever instance = null;
    private String longitude;
    private String latitude;
    private String combinedLatLong;
    private boolean isDataReady = false;


    LocationRetriever(Context context)
    {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

    }

    public static LocationRetriever getInstance(Context context)
    {
        if(instance == null)
        {
            instance = new LocationRetriever(context);
        }
        return instance;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null)
            {
                Log.d("nigga","just got the locations");
                latitude = Double.toString(mLastLocation.getLatitude());
                longitude = Double.toString(mLastLocation.getLongitude());
                isDataReady = true;


            }
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public String getLongitude() {

        return longitude;
    }

    public String getLatitude() {

        return latitude;
    }

    public String getCombinedLatLong() {
        combinedLatLong = getLatitude() +" : "+ getLongitude();
        return combinedLatLong;
    }
    public boolean getIsDataReady()
    {
        if (isDataReady)
        {
            isDataReady = false;
            return true;
        }
        else
        {
            return false;
        }
    }
}

