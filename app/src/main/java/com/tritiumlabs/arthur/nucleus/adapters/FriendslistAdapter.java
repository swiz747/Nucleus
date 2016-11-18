package com.tritiumlabs.arthur.nucleus.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tritiumlabs.arthur.nucleus.Friend;
import com.tritiumlabs.arthur.nucleus.R;

import java.util.ArrayList;

/**
 * Created by Arthur on 6/27/2016.
 */
public class FriendslistAdapter extends BaseAdapter {

    private static final String TAG = "FriendslistAdapter";
    private static LayoutInflater inflater = null;
    ArrayList<Friend> friendlist;

    public FriendslistAdapter(Activity activity, ArrayList<Friend> friendArray) {

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        friendlist = friendArray;





    }

    @Override
    public int getCount() {
        if (friendlist != null)
        {
            return friendlist.size();
        }
        else
        {
            return 0;
        }

    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {


        View vi = convertView;
        if (convertView == null)
        {
            vi = inflater.inflate(R.layout.friend_bubble,parent, false);
        }
        if(friendlist != null)
        {
            Friend tempFriend = friendlist.get(position);
            String onlineStatus = tempFriend.getOnlineStatus();
            TextView friendName = (TextView) vi.findViewById(R.id.friend_name);
            TextView friendStatus = (TextView) vi.findViewById(R.id.friend_status);
            ImageView statusIcon = (ImageView) vi.findViewById(R.id.statusIcon);

            friendName.setText(tempFriend.getUserName());
            friendStatus.setText(tempFriend.getEmoStatus());

            if(onlineStatus.equals("available"))
            {
                statusIcon.setImageResource(R.drawable.online);
            }
            else if(onlineStatus.equals("away"))
            {
                statusIcon.setImageResource(R.drawable.away);
            }
            else
            {
                statusIcon.setImageResource(R.drawable.offline);
            }
        }



        return vi;
    }


}
