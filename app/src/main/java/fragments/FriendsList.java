package fragments;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tritiumlabs.arthur.servertest.FriendslistAdapter;
import com.tritiumlabs.arthur.servertest.MainActivity;
import com.tritiumlabs.arthur.servertest.MyService;
import com.tritiumlabs.arthur.servertest.R;





public class FriendsList extends Fragment {


    String friendString = "";
    public static FriendslistAdapter friendslistAdapter;
    ListView lstView_Friends;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendslist_layout, container, false);
        lstView_Friends = (ListView) view.findViewById(R.id.lstView_Friends);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Friend List");


        // ----Set autoscroll of listview when a new message arrives----//
        lstView_Friends.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lstView_Friends.setStackFromBottom(true);


        lstView_Friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView friendView  = (TextView)view.findViewById(R.id.friend_name);
                String friend = friendView.getText().toString();

                Log.d("item click:",position+" " + id +" item:" + friend);
                //TODO Create chat fragment here pass in unique chat ID -AB
                //MainActivity activity = ((MainActivity) getActivity());
                //activity.openChat();
                Bundle args = new Bundle();
                friend = friend.substring(0, friend.indexOf("@"));
                args.putString("friendName", friend);
                //args.putString("friendID", friend);
                Fragment toFragment = new Chats();
                toFragment.setArguments(args);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragContainer, toFragment, "where is this")
                        .addToBackStack("where is this").commit();




            }


        });
        lstView_Friends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView friendView  = (TextView)view.findViewById(R.id.friend_status);
                String friend = friendView.getText().toString();

                Log.d("item click:",position+" " + id +" item:" + friend.toString());

                return true;

            }


        });

        Log.d("Friendslist","about to get roster");
        friendString = MyService.xmpp.getRoster();
        Log.d("Friendslist","got roster: " + friendString);
        friendslistAdapter = new FriendslistAdapter(getActivity(), friendString);
        lstView_Friends.setAdapter(friendslistAdapter);
        friendslistAdapter.notifyDataSetChanged();
        Log.d("Friendslist","Trust me the friendslist was created and in theory the layout was inflated");



        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {

    }









    }