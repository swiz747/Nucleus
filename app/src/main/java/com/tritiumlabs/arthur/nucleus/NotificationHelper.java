package com.tritiumlabs.arthur.nucleus;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Arthur on 10/6/2016.
 */


public class NotificationHelper {
    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    public void generateNotification()
    {
        NotificationCompat.Builder notification;
        int uniqueID = 389234786;

        //notification stuff
        int color = 0xFF00FF00;

        notification = new NotificationCompat.Builder(context);
        notification.setAutoCancel(true);


        notification.setSmallIcon(R.drawable.ic_stat_testicon);
        //notification.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_stat_testicon));
        notification.setTicker("im a ticker");
        notification.setColor(color);
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Im The Title, Nigga");
        notification.setContentText("Im the body of the notification!");
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setContentIntent(pendIntent);

        // issues notification
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());

    }
}
