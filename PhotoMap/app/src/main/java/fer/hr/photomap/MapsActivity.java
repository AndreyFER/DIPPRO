package fer.hr.photomap;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.metrics.Event;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;

import fer.hr.photomap.data.FetchEvents;
import fer.hr.photomap.data.UploadEvent;
import fer.hr.photomap.data.model.EventData;
import fer.hr.photomap.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    View mapView;
    Context context = this;
    FloatingActionButton customLocationButton;
    FloatingActionButton addItemButton;
    private View defaultlocationButton;
    TextView saveCounter;
    ImageView saveButton;
    TextView userInfo;
    TextView signOutBtn;
    ArrayList<EventData> eventDataList = new ArrayList<>();
    //String username = "ivan.baljkas";
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences prefs = getSharedPreferences("PrefFile", MODE_PRIVATE);
        username = prefs.getString("username", "No username defined");
        if (username.equals("No username defined")){
            username = "Anonymous user";
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        if(!Utils.isNetworkAvailable(context)){
            Toast.makeText(getApplicationContext(),
                    "Network connection unavailable.",
                    Toast.LENGTH_LONG).show();
        }

        userInfo = (TextView) findViewById(R.id.usernameM);
        signOutBtn = (TextView) findViewById(R.id.signoutbtn);

        userInfo.setText(username);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), Login1.class);
                SharedPreferences.Editor editor = getSharedPreferences("PrefFile", MODE_PRIVATE).edit();
                editor.putString("username", "No username defined");
                editor.apply();
                startActivity(intent);
                finish();
            }
        });
    }

    //@Override
    //public void onBackPressed() {
    //    ;
    //}

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ResourceType")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setInfoWindowAdapter(new MarkerInfoAdapter(context));

        saveButton = (ImageView) findViewById(R.id.saveImage);
        saveCounter = (TextView) findViewById(R.id.saveCounter);
        eventDataList = Utils.readFromInternalStorage(context);

        Log.d("list", eventDataList.toString());
        if(!eventDataList.isEmpty()){
            for(EventData eventData : eventDataList){
                Utils.addMarkerToMap(mMap, eventData);
            }
            saveCounter.setText(String.valueOf(eventDataList.size()));
        } else{
           toogleSaveButton(false, 0);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(Utils.isNetworkAvailable(context) && !eventDataList.isEmpty()){

                    ////ispod je dodatak u slucaju anonymousa da ga salje na login kad uhvati internet
                    if (username.equals("Anonymous user")) {
                        Intent intent = new Intent(getBaseContext(), Login1.class);
                        SharedPreferences.Editor editor = getSharedPreferences("PrefFile", MODE_PRIVATE).edit();
                        editor.putString("username", "No username defined");
                        editor.apply();
                        intent.putExtra("AnonLogin", 30);
                        startActivityForResult(intent, 30);
                    }else {
                        uploadEventsList();
                    }
                } else {
                    Toast.makeText(context,
                            saveCounter.getText().toString() + " events waiting for upload once connection is established.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        defaultlocationButton = mapFragment.getView().findViewById(0x2);
        // Change the visibility of my location button
        if(defaultlocationButton != null)  defaultlocationButton.setVisibility(View.GONE); //hide default map location button

        findViewById(R.id.customLocationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMap != null && defaultlocationButton != null) defaultlocationButton.callOnClick(); //custom location button activates default location button functionality
            }
        });

        findViewById(R.id.addItemButton).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent intentNextActivity = new Intent(getBaseContext(), AddItem.class);
                LatLng latlng = Utils.getCurrentLocation(context);
                double latitude;
                double longitude;
                if(latlng != null){
                    latitude = latlng.latitude;
                    longitude = latlng.longitude;
                } else{
                    latitude = 0;
                    longitude = 0;
                }
                intentNextActivity.putExtra("latitude", latitude );
                intentNextActivity.putExtra("longitude", longitude );;
                startActivityForResult(intentNextActivity, 40);
            }
        });

        try {
                FetchEvents fetchEvents = new FetchEvents(mMap, context);
                fetchEvents.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 40 &&
                resultCode == RESULT_OK) {
            Double lat = intent.getDoubleExtra("lat", 0);
            Double lon = intent.getDoubleExtra("lon", 0);
            String imageString = intent.getStringExtra("image");
            String description = intent.getStringExtra("description");
            String typeString = intent.getStringExtra("type");
            try {
                Uri imageUri = Uri.parse(imageString);
            }catch (RuntimeException e){
                imageString = "nekaslikaaa";
                Uri imageUri = Uri.parse(imageString);
            }
            EventData eventData = new EventData(description, lat, lon, imageString, typeString, username);
            if(Utils.isNetworkAvailable(context)){
                UploadEvent uploadData = null;
                try {
                    uploadData = new UploadEvent(eventData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadData.execute();
            }else{
                eventDataList.add(eventData);
                toogleSaveButton(true, eventDataList.size());
                Utils.saveToInternalStorage(context, eventDataList);
            }
            Utils.addMarkerToMap(mMap, eventData);
        }
        if (requestCode == 30 &&
                resultCode == RESULT_OK) {
            SharedPreferences prefs = getSharedPreferences("PrefFile", MODE_PRIVATE);
            username = prefs.getString("username", "No username defined");
            userInfo.setText(username);
            if(eventsGotUserAnonymous(eventDataList)) {
                for(EventData eData : eventDataList) {
                    eData.setUser(username);
                }
            }
            uploadEventsList();
        }
    }

    private void toogleSaveButton(boolean show, int size) {
        saveCounter.setText(String.valueOf(eventDataList.size()));
        saveCounter.setEnabled(show);
        saveButton.setEnabled(show);
        if(show){
            saveCounter.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
        } else{
            saveCounter.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
        }

    }

    private boolean eventsGotUserAnonymous(ArrayList<EventData> eventDataList) {
        for(EventData event : eventDataList){
            if(event.getUser().equals("Anonymous user")) {
                return true;
            }
        }
        return false;
    }

    private void uploadEventsList() {
        ArrayList<EventData> newEventDataList = new ArrayList<>(eventDataList);
        for(EventData eventData : eventDataList){
            try {
                for(int i = 0; i<3; i++){
                    UploadEvent uploadEvent = new UploadEvent(eventData);
                    Boolean success = uploadEvent.execute().get();
                    if(success) {
                        newEventDataList.remove(eventData); //remove successfully uploaded event (cannot remove from list being iterated)
                        break;
                    }
                }

            } catch (IOException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        eventDataList = newEventDataList;
        if(eventDataList.isEmpty()) toogleSaveButton(false,0);
        else toogleSaveButton(true, eventDataList.size());
        Utils.saveToInternalStorage(context, eventDataList);
        Toast.makeText(context,
                "Event data uploaded to server.",
                Toast.LENGTH_LONG).show();
    }

}