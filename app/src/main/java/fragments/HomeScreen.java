package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tritiumlabs.arthur.nucleus.MyService;
import com.tritiumlabs.arthur.nucleus.R;

/**
 * Created by Arthur on 10/4/2016.
 */

public class HomeScreen extends android.support.v4.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.homescreen_layout, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Nucleus");


        Button friendsList = (Button)view.findViewById(R.id.friendsListButton);
        Button inboxButton = (Button)view.findViewById(R.id.messagesButton);
        Button trackerButton = (Button)view.findViewById(R.id.trackerButton);
        Button notificationButton = (Button)view.findViewById(R.id.notificationButton);

        inboxButton.setEnabled(false);

        friendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Bundle args = new Bundle();
                //friend = friend.substring(0, friend.indexOf("@"));
                //args.putString("friendName", friend);
                //args.putString("friendID", friend);
                Fragment toFragment = new FriendsList();
                //toFragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragContainer, toFragment, "friendsList")
                        .addToBackStack("friendsList").commit();

            }
        });

        inboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment toFragment = new FriendsList();
                //toFragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragContainer, toFragment, "friendsList")
                        .addToBackStack("friendsList").commit();

            }
        });

        trackerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment toFragment = new Tracker();
                //toFragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragContainer, toFragment, "tracker")
                        .addToBackStack("tracker").commit();

            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment toFragment = new FriendRequest();
                //toFragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragContainer, toFragment, "notifications")
                        .addToBackStack("notifications").commit();

            }
        });




        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }




}


