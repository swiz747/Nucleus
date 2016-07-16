package com.tritiumlabs.arthur.servertest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import fragments.Chats;
import fragments.FriendAdd;
import fragments.FriendsList;


public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private boolean mBounded = false;

    private MyService mService;
    private NotificationCompat.Builder notification;
    private static final int uniqueID = 389234786;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "on create method");
        Intent i = new Intent(this, MyService.class);
        bindService( i, mConnection, Context.BIND_AUTO_CREATE);

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
            notification.setSmallIcon(R.drawable.message);
            notification.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.colored_message));
            notification.setTicker("im a ticker");
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle("Im The Title, Nigga");
            notification.setContentText("Im the body of the notification!");
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            notification.setContentIntent(pendIntent);

            // issues notification
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(uniqueID, notification.build());


            return true;
        }
        else if (id == R.id.fuck_everything)
        {
            //TODO for when you need to fuck everyhting -AB
            //LocalDBHandler handler = LocalDBHandler.getInstance(this);
            //handler.fuckeverything();
            return true;
        }
        else if (id == R.id.add_friend)
        {
            //TODO temporary add friend
            openFriendAdd();
            return true;
        }
        else if (id == R.id.add_friend)
        {
            //TODO temporary add friend

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

    public void openFriendslist()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, new FriendsList()).commit();
    }

    public void openChat()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, new Chats()).commit();
    }
    public void openFriendAdd()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragContainer, new FriendAdd()).addToBackStack("stillnotsure").commit();
    }


}

