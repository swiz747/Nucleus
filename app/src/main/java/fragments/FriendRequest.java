package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.tritiumlabs.arthur.nucleus.MyService;
import com.tritiumlabs.arthur.nucleus.R;
import com.tritiumlabs.arthur.nucleus.adapters.FriendRequestAdapter;

import java.util.ArrayList;

import static fragments.FriendsList.friendslistAdapter;


public class FriendRequest extends Fragment {


    String friendString = "";
    public static FriendRequestAdapter friendRequestAdapter;
    ListView lstView_Reqs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendrequest_layout, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Friend Requests");
        lstView_Reqs= (ListView) view.findViewById(R.id.friendRequestList);


        // ----Set autoscroll of listview when a new message arrives----//
        lstView_Reqs.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lstView_Reqs.setStackFromBottom(false);




        lstView_Reqs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView noteView  = (TextView)view.findViewById(R.id.notification_type);
                String noteType = noteView.getText().toString();

                Log.d("item click:",position+" " + id +" item:" + noteType);
                //TODO Create chat fragment here pass in unique chat ID -AB
                //MainActivity activity = ((MainActivity) getActivity());
                //activity.openChat();
                //Bundle args = new Bundle();
                //friend = friend.substring(0, friend.indexOf("@"));
                //args.putString("friendName", friend);
                //args.putString("friendID", friend);
                //Fragment toFragment = new Chats();
                //toFragment.setArguments(args);
                //getFragmentManager()
                       // .beginTransaction()
                      //  .replace(R.id.fragContainer, toFragment, "where is this")
                       // .addToBackStack("where is this").commit();




            }


        });
        lstView_Reqs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView friendView  = (TextView)view.findViewById(R.id.friend_status);
                String friend = friendView.getText().toString();

                Log.d("item click:",position+" " + id +" item:" + friend);

                return true;

            }


        });

        Log.d("Friendslist","about to get roster");
        //MyService.xmpp.dbHandler.getNotificationsByType("friendReq")
        friendRequestAdapter = new FriendRequestAdapter(getActivity(), MyService.xmpp.dbHandler.getAllNotifications());

        lstView_Reqs.setAdapter(friendRequestAdapter);
        friendRequestAdapter.notifyDataSetChanged();
        Log.d("Friendslist","Trust me the friendslist was created and in theory the layout was inflated");



        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {

    }



}