package com.tritiumlabs.arthur.nucleus;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Arthur on 9/22/2016.
 */
public class LocationInfo {



    String username;
    String longitude;
    String latitude;
    LatLng latLng;


    LocationInfo()
    {

    }

    LocationInfo(String username)
    {
        this.username = username;
    }


    @Override
    public String toString() {
        return username + " is at " + latitude + " by "+ longitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public String getLatitude() {
        return latitude;
    }
    public String getUsername() {
        return username ;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;

        this.latitude = String.valueOf(latLng.latitude);
        this.longitude = String.valueOf(latLng.longitude);
    }
}
