package com.tritiumlabs.arthur.nucleus.interfaces;

import com.tritiumlabs.arthur.nucleus.LocationInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Arthur on 9/22/2016.
 */
public interface ExternalDBInterface {
    @FormUrlEncoded
    @POST("nucleus/db/requestlocation.php")
    Call<List<LocationInfo>> getLocation(
            @Field("username") String username);

    @FormUrlEncoded
    @POST("nucleus/db/requestMultilocation.php")
    Call<List<String>> getMultiLocation(
            @Field("usernames") String usernames);

    @FormUrlEncoded
    @POST("nucleus/db/updatelocation.php")
    Call<List<LocationInfo>> setLocation(
            @Field("username") String username,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude);
    //TODO: add timestamp collection here -AB

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://45.35.4.171/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


}
