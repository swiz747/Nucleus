package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;


import com.tritiumlabs.arthur.servertest.Friend;
import com.tritiumlabs.arthur.servertest.FriendAddAdapter;
import com.tritiumlabs.arthur.servertest.MyService;
import com.tritiumlabs.arthur.servertest.R;

import java.util.ArrayList;


public class FriendAdd extends Fragment {

    public static FriendAddAdapter friendaddAdapter;

    ListView lstView_Friends;
    ArrayList<Friend> userList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Find Friend");
        View view = inflater.inflate(R.layout.friendadd_layout, container, false);
        EditText usernameSearch = (EditText) view.findViewById(R.id.friendSearch);
        lstView_Friends = (ListView) view.findViewById(R.id.lstView_Friends);

        //TODO: need to sanitize input
        String userToSearch = usernameSearch.getText().toString();
        userList = MyService.xmpp.searchUsers(userToSearch);


        Log.d("FriendAdd","about to search for: " + userToSearch);
        friendaddAdapter = new FriendAddAdapter(getActivity(), userList);
        lstView_Friends.setAdapter(friendaddAdapter);
        friendaddAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {

    }




}