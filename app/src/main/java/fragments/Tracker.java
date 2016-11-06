package fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tritiumlabs.arthur.nucleus.Friend;
import com.tritiumlabs.arthur.nucleus.LocationInfo;
import com.tritiumlabs.arthur.nucleus.LocationRetriever;
import com.tritiumlabs.arthur.nucleus.MainActivity;
import com.tritiumlabs.arthur.nucleus.MyService;
import com.tritiumlabs.arthur.nucleus.R;
import com.tritiumlabs.arthur.nucleus.interfaces.ExternalDBInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.id.progress;
import static com.tritiumlabs.arthur.nucleus.MyXMPP.dbHandler;


/**
 * Created by Arthur on 10/6/2016.
 */

public class Tracker extends android.support.v4.app.Fragment {

    private static final String MAP_FRAGMENT_TAG = "map";
    private LocationRetriever locationRetriever;
    private ProgressDialog progress;
    private String myLatitudeText;
    private String myLongitudeText;
    private Double myLatitude;
    private Double myLongitude;
    private GoogleMap googleMap;
    MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Tritium Tracker");
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);



        progress = new ProgressDialog(getActivity());
        locationRetriever = LocationRetriever.getInstance(getActivity());
        AsyncTask<Void, Void, Boolean> retrieveLocation = new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setMessage("Getting Location");
                progress.show();
            }

            @Override
            protected Boolean doInBackground(Void... voids)
            {
                //TODO add timeout -AB
                //TODO there HAS to be a better way to do this but ive spent over 96 hours working on this fucker so this is how its working for now god damnit -AB
                while(!locationRetriever.getIsDataReady())
                {

                }
                Log.d("nigga","in background thread");
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(21,46)).title("me").snippet("holy shit, will this work?"));

                //addFriendMarkers();

                return null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                Log.d("nigga",locationRetriever.getCombinedLatLong());
                myLatitudeText = locationRetriever.getLatitude();
                myLongitudeText = locationRetriever.getLongitude();
                myLatitude = Double.valueOf(locationRetriever.getLatitude());
                myLongitude = Double.valueOf(locationRetriever.getLongitude());
                updateLocation(myLatitude, myLongitude);
                progress.dismiss();
                setMapCamera();
            }
        };
        retrieveLocation.execute();


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;

                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

            }
        });

        MapsInitializer.initialize(this.getActivity());



        return v;
    }
    public void setMapCamera()
    {

        Log.d("nigga", Double.toString(myLatitude) + " " + Double.toString(myLatitude));
        LatLng myLocation = new LatLng(myLatitude, myLongitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(14).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.addMarker(new MarkerOptions().position(myLocation).title("me").snippet("holy shit, will this work?"));

    }
    public void addFriendMarkers()
    {








        ExternalDBInterface dbInterface = ExternalDBInterface.retrofit.create(ExternalDBInterface.class);
        ArrayList<Friend> friendList = MyService.xmpp.getRoster();
        final ArrayList<LocationInfo> friendLocations = new ArrayList<>();

        String friendAggregation = "";
        for(int i = 0; i < friendList.size(); i++)
        {
            friendAggregation += friendList.get(i).getName();
            friendAggregation += ", ";
        }
        friendAggregation = friendAggregation.substring(0, friendAggregation.length() -2);
        final Call<List<LocationInfo>> call =
                dbInterface.getLocation(friendAggregation);


        call.enqueue(new Callback<List<LocationInfo>>() {
            @Override
            public void onResponse(Call<List<LocationInfo>> call, Response<List<LocationInfo>> response)
            {
                for(int i = 0; i < response.body().size(); i++)
                {
                    LocationInfo friendLocation = response.body().get(i);

                    LatLng derp = new LatLng(Double.valueOf(friendLocation.getLatitude()) ,Double.valueOf(friendLocation.getLongitude()));

                    friendLocation.setLatLng(derp);
                    friendLocations.add(friendLocation);
                }

            }
            @Override
            public void onFailure(Call<List<LocationInfo>> call, Throwable t) {

                Log.d("Tracker","Something went wrong: " + t.getMessage());
            }
        });

        for(int i = 0; i < friendLocations.size(); i++)
        {
            googleMap.addMarker(new MarkerOptions().position(friendLocations.get(i).getLatLng()).title(friendLocations.get(i).getUsername()).snippet("this is where statuses go"));
        }

    }


    public void updateLocation(double latitude, double longitude)
    {
        ExternalDBInterface dbInterface = ExternalDBInterface.retrofit.create(ExternalDBInterface.class);
        //TODO change username to be dynamic -AB
        final Call<List<LocationInfo>> call =
                dbInterface.setLocation(dbHandler.getUsername(), latitude, longitude);


        call.enqueue(new Callback<List<LocationInfo>>() {
            @Override
            public void onResponse(Call<List<LocationInfo>> call, Response<List<LocationInfo>> response) {


            }

            @Override
            public void onFailure(Call<List<LocationInfo>> call, Throwable t) {


                Log.d("Tracker", t.getMessage());
            }
        });
    }
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}

