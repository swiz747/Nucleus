package com.tritiumlabs.arthur.servertest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.jivesoftware.smack.chat.Chat;

/**
 * Created by Arthur on 6/8/2016.
 */
public class MyService extends Service {
    //TODO: there are now 3 places that have this hard coded, we need a global constant or something, maybe pop it in an sqlite table? - AB
    private static final String DOMAIN = "tritium";
    private static final String HOST = "45.35.4.171";
    private static final int PORT = 5222;

    public static ConnectivityManager cm;
    public static MyXMPP xmpp;
    private final IBinder myBinder = new LocalBinder();
    public static boolean ServerchatCreated = false;
    String text = "";


    @Override
    public IBinder onBind(final Intent intent)
    {
        return myBinder;
    }

    public Chat chat;

    @Override
    public void onCreate() {
        Log.d("Myservice", "Attempting to create service");
        super.onCreate();
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        xmpp = MyXMPP.getInstance(MyService.this);
        //xmpp.connect("onCreate");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId)
    {
        return Service.START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(final Intent intent)
    {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        xmpp.connection.disconnect();
    }

    public static boolean isNetworkConnected()
    {
        return cm.getActiveNetworkInfo() != null;
    }

    public class LocalBinder extends Binder {



        MyService getService()
        {
            return MyService.this;
        }

    }
}
