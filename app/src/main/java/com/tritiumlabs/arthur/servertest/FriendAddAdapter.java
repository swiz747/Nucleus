package com.tritiumlabs.arthur.servertest;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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
        UsersList = list;

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

        final Friend user = UsersList.get(position);
        View vi = convertView;
        if (convertView == null)
        {
            vi = inflater.inflate(R.layout.friend_add_bubble,parent, false);
        }

        TextView friendName = (TextView) vi.findViewById(R.id.friend_name);
        ImageButton addButton = (ImageButton) vi.findViewById(R.id.addFriendButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click from", user.getName());
            }
        });

        friendName.setText(user.getName());
        return vi;
    }

    public void add(Friend object) {
        UsersList.add(object);
    }


    public void addMultiple(ArrayList<Friend> userList) {
        for(int i = 0; i < userList.size(); i++)
        {
            add(userList.get(i));
        }

    }
}
