package com.tritiumlabs.arthur.nucleus;


import android.util.Log;

import com.tritiumlabs.arthur.nucleus.interfaces.ExternalDBInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arthur on 9/19/2016.
 */


public class ExternalDBHandler {

    public LocationInfo getLocation(String username)
    {
        final LocationInfo returnInfo = new LocationInfo(username);
        String latText = "";
        String longText = "";
        ExternalDBInterface dbInterface = ExternalDBInterface.retrofit.create(ExternalDBInterface.class);
        final Call<List<LocationInfo>> call =
                dbInterface.getLocation(username);


        call.enqueue(new Callback<List<LocationInfo>>() {
            @Override
            public void onResponse(Call<List<LocationInfo>> call, Response<List<LocationInfo>> response) {

                returnInfo.setLatitude(response.body().get(0).getLatitude());
                returnInfo.setLongitude(response.body().get(0).getLongitude());
                Log.d("externalDB handler", returnInfo.toString());
            }
            @Override
            public void onFailure(Call<List<LocationInfo>> call, Throwable t) {

                returnInfo.setLatitude("0");
                returnInfo.setLongitude("0");
                Log.d("externalDB handler", t.getMessage());
            }
        });
        return returnInfo;
    }

    public void setLocation(String username, double latitude, double longitude)
    {
        final String returnValue = "";

        ExternalDBInterface dbInterface = ExternalDBInterface.retrofit.create(ExternalDBInterface.class);
        final Call<List<LocationInfo>> call =
                dbInterface.setLocation(username, latitude, longitude);


        call.enqueue(new Callback<List<LocationInfo>>() {
            @Override
            public void onResponse(Call<List<LocationInfo>> call, Response<List<LocationInfo>> response) {
                Log.d("externalDB handler", "Success!");

            }
            @Override
            public void onFailure(Call<List<LocationInfo>> call, Throwable t) {

                Log.d("externalDB handler", t.getMessage());

            }
        });

    }

}





