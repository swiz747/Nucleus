package com.tritiumlabs.arthur.nucleus;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.jivesoftware.smack.chat.Chat;

/**
 * Created by Arthur on 6/8/2016.
 */
public class MyService extends Service {


    public static ConnectivityManager cm;
    public static MyXMPP xmpp;
    private final IBinder myBinder = new LocalBinder();
    private boolean mainActivityAlive;
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
        mainActivityAlive = true;
        //xmpp.connect("onCreate");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId)
    {
        return Service.START_STICKY;
    }

    @Override
    public boolean onUnbind(final Intent intent)
    {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        xmpp.disconnect();
        mainActivityAlive = false;
    }

    public void messageNotification(ChatMessage chat)
    {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("from", chat.getSender());
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), i, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true)
                .setContentTitle("New message from " + chat.getSender())
                .setContentText("Subject")
                .setSmallIcon(R.drawable.ic_stat_notify_msg)
                .setContentIntent(pIntent)
                .setAutoCancel(true);


        NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        nm.notify(0, notification.build());
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
