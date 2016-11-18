package com.tritiumlabs.arthur.nucleus.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tritiumlabs.arthur.nucleus.MainActivity;
import com.tritiumlabs.arthur.nucleus.MyService;
import com.tritiumlabs.arthur.nucleus.Notification;
import com.tritiumlabs.arthur.nucleus.MyXMPP;
import com.tritiumlabs.arthur.nucleus.R;

import java.util.ArrayList;

import fragments.FriendRequest;

/**
 * Created by Arthur on 10/8/2016.
 */

public class FriendRequestAdapter extends BaseAdapter {
    private static final String TAG = "FriendslistAdapter";
    private static LayoutInflater inflater = null;
    private ArrayList<Notification> notificationList;
    private MyXMPP xmpp;
    public FriendRequestAdapter(Activity activity, ArrayList<Notification> list)
    {
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        notificationList = list;
    }
    @Override
    public int getCount() {
        if (notificationList != null)
        {
            return notificationList.size();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        View vi = convertView;
        if (convertView == null)
        {
            vi = inflater.inflate(R.layout.notification_bubble,parent, false);
        }
        if(notificationList != null)
        {
            final Notification tempNote = notificationList.get(position);

            String notificationType = tempNote.getType();
            String notificationBody = tempNote.getBody();

            TextView noteTypeText = (TextView) vi.findViewById(R.id.notification_type);
            TextView noteBodyText = (TextView) vi.findViewById(R.id.notification_body);
            ImageView noteTypeIcon = (ImageView) vi.findViewById(R.id.notificationIcon);



            if(notificationType.equals("friendReq"))
            {
                noteTypeText.setText("FriendRequest");
                //TODO replace this with friend request icon
                noteTypeIcon.setImageResource(R.drawable.away);
            }
            else
            {
                noteTypeText.setText(notificationType);
                noteTypeIcon.setImageResource(R.drawable.offline);
            }

            noteBodyText.setText(notificationBody);

            final ImageButton removeButton = (ImageButton) vi.findViewById(R.id.removeButton);


            removeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    notificationList.remove(position);
                    MyService.xmpp.dbHandler.clearNotificationByID(tempNote.getNotificationID());
                    FriendRequest.friendRequestAdapter.notifyDataSetChanged();
                }

            });

            final LinearLayout clickableNotification = (LinearLayout) vi.findViewById(R.id.clickableNotification);
            clickableNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"clicked the notification!");
                    MyService.xmpp.confirmFriend(tempNote.getFrom());
                }
            });
        }


        return vi;
    }
}
