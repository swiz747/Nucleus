package com.tritiumlabs.arthur.nucleus;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tritiumlabs.arthur.nucleus.interfaces.ExternalDBInterface;

import java.util.List;

import fragments.Chats;
import fragments.FriendAdd;
import fragments.FriendsList;
import fragments.HomeScreen;
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
    private MyService mService;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Checking extras in intents for more dynamic responses
       if(getIntent().hasExtra("notification"))
       {
           //TODO add notification filtering here
       }



        Log.d(TAG, "on create method");
        Intent i = new Intent(this, MyService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        externalDB = new ExternalDBHandler();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        openHomeScreen();



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

            //TODO send to settings fragment.


            return true;
        } else if (id == R.id.fuck_everything) {
            //for when you need to fuck everything -AB
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
        transaction.replace(R.id.fragContainer, new FriendsList()).addToBackStack("friendsList").commit();
    }

    public void openChat() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, new Chats()).commit();
    }

    public void openFriendAdd() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, new FriendAdd()).addToBackStack("addFriend").commit();
    }
    public void openHomeScreen() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, new HomeScreen()).addToBackStack("homeScreen").commit();
    }

    public void openMapFragment() {

    }




}

