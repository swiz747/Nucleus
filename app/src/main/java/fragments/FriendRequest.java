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


import com.tritiumlabs.arthur.nucleus.Friend;
import com.tritiumlabs.arthur.nucleus.FriendAddAdapter;
import com.tritiumlabs.arthur.nucleus.MyService;
import com.tritiumlabs.arthur.nucleus.R;

import java.util.ArrayList;


public class FriendRequest extends Fragment {

    public static FriendAddAdapter friendaddAdapter;

    ListView lstView_Friends;
    ArrayList<Friend> userList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Friend Requests");
        View view = inflater.inflate(R.layout.friendrequest_layout, container, false);

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