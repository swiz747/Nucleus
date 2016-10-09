package com.tritiumlabs.arthur.nucleus.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tritiumlabs.arthur.nucleus.Friend;
import com.tritiumlabs.arthur.nucleus.MyXMPP;
import com.tritiumlabs.arthur.nucleus.R;

import java.util.ArrayList;

/**
 * Created by Arthur on 7/15/2016.
 */
public class FriendAddAdapter extends BaseAdapter {

    private static final String TAG = "FriendslistAdapter";
    private static LayoutInflater inflater = null;
    private MyXMPP xmpp;
    ArrayList<Friend> UsersList;

    public FriendAddAdapter(Activity activity, ArrayList<Friend> list) {
        UsersList = list;
        xmpp = MyXMPP.getInstance(activity);

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


        View vi = convertView;
        if (convertView == null)
        {
            vi = inflater.inflate(R.layout.friend_add_bubble,parent, false);
        }
        if(UsersList != null)
        {
            final Friend user = UsersList.get(position);
            String onlineStatus = user.getOnlineStatus();
            TextView friendName = (TextView) vi.findViewById(R.id.friend_name);
            ImageView statusIcon = (ImageView) vi.findViewById(R.id.friendPic);

            friendName.setText(user.getUserName());

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

            final ImageButton addButton = (ImageButton) vi.findViewById(R.id.addFriendButton);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("click from", user.getName());
                    xmpp.addFriend(user.getJID());
                    addButton.setEnabled(false);
                }
            });

        }


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
