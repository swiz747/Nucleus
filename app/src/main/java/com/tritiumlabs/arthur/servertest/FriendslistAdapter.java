package com.tritiumlabs.arthur.servertest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Arthur on 6/27/2016.
 */
public class FriendslistAdapter extends BaseAdapter {

    private static final String TAG = "FriendslistAdapter";
    private static LayoutInflater inflater = null;
    ArrayList<String> friendlist;

    public FriendslistAdapter(Activity activity, String friendString) {

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (friendString.equals("") || friendString.equals(","))
        {
            friendString = "NFPH";
        }
        else
        {
            friendlist = new ArrayList<String>(Arrays.asList(friendString.split(",")));
        }


        Log.d(TAG, "uh idk maybe: " + friendString);

    }

    @Override
    public int getCount() {
        return friendlist.size();
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

        String strFriendName = friendlist.get(position);
        View vi = convertView;
        if (convertView == null)
        {
            vi = inflater.inflate(R.layout.friend_bubble,parent, false);
        }

        TextView friendName = (TextView) vi.findViewById(R.id.friend_name);
        friendName.setText(strFriendName);
        Log.d(TAG, "inside getView method: " + strFriendName);
        return vi;
    }


}
