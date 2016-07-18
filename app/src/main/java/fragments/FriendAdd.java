package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        final EditText usernameSearch = (EditText) view.findViewById(R.id.friendSearch);
        Button search = (Button) view.findViewById(R.id.btnAddFriend);
        lstView_Friends = (ListView) view.findViewById(R.id.newFriends);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    //TODO: need to sanitize input
                    searchName(v,usernameSearch);



            }
        });




        userList = new ArrayList<Friend>();
        friendaddAdapter = new FriendAddAdapter(getActivity(), userList);
        lstView_Friends.setAdapter(friendaddAdapter);
        friendaddAdapter.notifyDataSetChanged();


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {

    }
    private void searchName(View v, EditText username)
    {
        String userToSearch = username.getText().toString();
        if(userToSearch != null && !userToSearch.equals(""))
        {
            Log.d("FriendAdd","about to search for: " + userToSearch);

            userList = MyService.xmpp.searchUsers(userToSearch);
            friendaddAdapter.addMultiple(userList);

            friendaddAdapter.notifyDataSetChanged();
        }

    }



}