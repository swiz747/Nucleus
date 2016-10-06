package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tritiumlabs.arthur.servertest.R;

/**
 * Created by Arthur on 10/4/2016.
 */

public class HomeScreen extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.homescreen_layout, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Friend List");
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }
}


