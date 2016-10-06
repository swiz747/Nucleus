package com.tritiumlabs.arthur.servertest;

import android.*;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tritiumlabs.arthur.servertest.interfaces.ExternalDBInterface;

import java.util.List;

import fragments.Chats;
import fragments.FriendAdd;
import fragments.FriendsList;
import fragments.MapHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean mBounded = false;
    private static final String MAP_FRAGMENT_TAG = "map";
    protected GoogleApiClient mGoogleApiClient = null;
    protected Location mLastLocation;
    private  String myLongitudeText;
    private String myLatitudeText;
    private ExternalDBHandler externalDB;
    private LocationRetriever locationRetriever;
    private ProgressDialog progress;
    private GoogleMap googleMap;
    private MyService mService;
    private NotificationCompat.Builder notification;
    private static final int uniqueID = 389234786;
    private Double myLatitude;
    private Double myLongitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "on create method");
        Intent i = new Intent(this, MyService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        externalDB = new ExternalDBHandler();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);


    }

    public MyService getmService() {
        return mService;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalDBHandler handler = LocalDBHandler.getInstance(this);
        handler.close();

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
            // LocalDBHandler handler = LocalDBHandler.getInstance(this);
            //handler.getLocalSettings();

            //notification stuff
            int color = 0xFF00FF00;

            notification.setSmallIcon(R.drawable.ic_stat_testicon);
            //notification.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_stat_testicon));
            notification.setTicker("im a ticker");
            notification.setColor(color);
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle("Im The Title, Nigga");
            notification.setContentText("Im the body of the notification!");
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            notification.setContentIntent(pendIntent);

            // issues notification
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(uniqueID, notification.build());


            return true;
        } else if (id == R.id.fuck_everything) {
            //TODO for when you need to fuck everyhting -AB
            LocalDBHandler handler = LocalDBHandler.getInstance(this);
            handler.fuckeverything();
            return true;
        } else if (id == R.id.add_friend) {
            //TODO temporary add friend
            openFriendAdd();
            return true;
        } else if (id == R.id.mystery) {
            openMapFragment();
            return true;
        } else if (id == R.id.refresh) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
            mBounded = true;
            Log.d(TAG, "Connected the service");

            //TODO this connects the friendslist fragment, probably -AB
            openFriendslist();

        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            mBounded = false;
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    public void openFriendslist() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, new FriendsList()).commit();
    }

    public void openChat() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, new Chats()).commit();
    }

    public void openFriendAdd() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, new FriendAdd()).addToBackStack("stillnotsure").commit();
    }

    public void openMapFragment() {
        ((AppCompatActivity) MainActivity.this).getSupportActionBar().setTitle("Tritium Tracker");

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);

        // We only create a fragment if it doesn't already exist.
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            Log.d("nigga","inserting fragment");
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragContainer, mapFragment, MAP_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        progress = new ProgressDialog(this);
        locationRetriever = LocationRetriever.getInstance(this);
        AsyncTask<Void, Void, Boolean> retrieveLocation = new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setMessage("Getting Location");
                progress.show();
            }

            @Override
            protected Boolean doInBackground(Void... voids)
            {
                //TODO there HAS to be a better way to do this but ive spent over 96 hours working on this fucker so this is how its working for now god damnit -AB
                while(!locationRetriever.getIsDataReady())
                {

                }
                Log.d("nigga","in background thread");

                return null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                Log.d("nigga",locationRetriever.getCombinedLatLong());
                myLatitudeText = locationRetriever.getLatitude();
                myLongitudeText = locationRetriever.getLongitude();
                myLatitude = Double.valueOf(locationRetriever.getLatitude());
                myLongitude = Double.valueOf(locationRetriever.getLongitude());
                updateLocation(myLatitude, myLongitude);
                progress.dismiss();
                setMapCamera();

            }
        };
        retrieveLocation.execute();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;



                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }



            }
        });


    }
    public void getLocation() {
        Log.d("nigga","running get loocation");

    }
    public void setMapCamera()
    {

        Log.d("nigga", Double.toString(myLatitude) + " " + Double.toString(myLatitude));
        LatLng myLocation = new LatLng(myLatitude, myLongitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(14).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.addMarker(new MarkerOptions().position(myLocation).title("me").snippet("holy shit, will this work?"));

    }
    public void addFriendMarkers()
    {
        //googleMap.addMarker(new MarkerOptions().position(warwick).title("dildo").snippet("fucking gorton pond or some shit"));
    }
    public void updateLocation(double latitude, double longitude)
    {
        ExternalDBInterface dbInterface = ExternalDBInterface.retrofit.create(ExternalDBInterface.class);
        //TODO change username to be dynamic -AB
        final Call<List<LocationInfo>> call =
                dbInterface.setLocation("phoneapp", latitude, longitude);


        call.enqueue(new Callback<List<LocationInfo>>() {
            @Override
            public void onResponse(Call<List<LocationInfo>> call, Response<List<LocationInfo>> response) {


            }

            @Override
            public void onFailure(Call<List<LocationInfo>> call, Throwable t) {


                Log.d(TAG, t.getMessage());
            }
        });
    }




}

