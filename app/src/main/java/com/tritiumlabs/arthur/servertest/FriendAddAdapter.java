package com.tritiumlabs.arthur.servertest;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Arthur on 7/15/2016.
 */
public class FriendAddAdapter extends BaseAdapter {

    private static final String TAG = "FriendslistAdapter";
    private static LayoutInflater inflater = null;
    ArrayList<Friend> UsersList;

    public FriendAddAdapter(Activity activity, ArrayList<Friend> list) {

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        return UsersList.size();
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

        Friend user = UsersList.get(position);
        View vi = convertView;
        if (convertView == null)
        {
            vi = inflater.inflate(R.layout.friend_add_bubble,parent, false);
        }

        TextView friendName = (TextView) vi.findViewById(R.id.friend_name);
        friendName.setText(user.getName());
        return vi;
    }

    public void add(Friend object) {
        UsersList.add(object);
    }


}
