package fer.hr.photomap;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.metrics.Event;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;

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
    ImageView saveImage;
    ArrayList<Float> hueList = new ArrayList<Float>();
    ArrayList<EventData> eventDataList = new ArrayList<>();
    String username = "guest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        Utils.addHuesToList(hueList);
        if(!Utils.isNetworkAvailable(context)){
            Toast.makeText(getApplicationContext(),
                    "Network connection unavailable.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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

        // Add a marker in Sydney and move the camera
        LatLng sydneyLatLng = new LatLng(-34, 151);
        Marker mark1 = mMap.addMarker(new MarkerOptions().position(new LatLng(-33.87365, 151.20689))
                .title("Andrej Gregic;River;Description of a river")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        Marker mark2 = mMap.addMarker(new MarkerOptions().position(new LatLng(-35.87365, 161.20689))
                .title("Vinko Benkovic;Mountain;Description of a mountain")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydneyLatLng));

        saveImage = (ImageView) findViewById(R.id.saveImage);
        saveCounter = (TextView) findViewById(R.id.saveCounter);
        eventDataList = Utils.readFromInternalStorage(context);
        Log.d("list", eventDataList.toString());
        if(!eventDataList.isEmpty()){
            saveCounter.setText(String.valueOf(eventDataList.size()));
        } else{
           toogleSaveButton(false, 0);
        }

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isNetworkAvailable(context) && !eventDataList.isEmpty()){
                    //upload event data
                    toogleSaveButton(false,0);
                    eventDataList.clear();
                    Utils.saveToInternalStorage(context, new ArrayList<>());
                    Toast.makeText(context,
                            "Event data uploaded to server.",
                            Toast.LENGTH_LONG).show();
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
            int type = intent.getIntExtra("typeIndex", 0) % 9;
            String imageString = intent.getStringExtra("image");
            String description = intent.getStringExtra("description");
            String typeString = intent.getStringExtra("typeString");
            Uri imageUri = Uri.parse(imageString);
            EventData eventData = new EventData(description, lat, lon, imageString, type, username);
            if(Utils.isNetworkAvailable(context)){
                DatabaseConnection uploadData = null;
                try {
                    uploadData = new DatabaseConnection(mMap, eventData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadData.execute();
            }else{
                eventDataList.add(eventData);
                toogleSaveButton(true, eventDataList.size());
                Utils.saveToInternalStorage(context, eventDataList);
            }
            StringJoiner joiner = new StringJoiner(";");
            joiner.add(username).add(typeString).add(description);
            String concatenatedData = joiner.toString();


            mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon))
                    .title(concatenatedData)
                    .snippet(imageString)
                    .icon(BitmapDescriptorFactory.defaultMarker(hueList.get(type))));
        }
    }

    private void toogleSaveButton(boolean show, int size) {
        saveCounter.setText(String.valueOf(eventDataList.size()));
        saveCounter.setEnabled(show);
        saveImage.setEnabled(show);
        if(show){
            saveCounter.setVisibility(View.VISIBLE);
            saveImage.setVisibility(View.VISIBLE);
        } else{
            saveCounter.setVisibility(View.GONE);
            saveImage.setVisibility(View.GONE);
        }

    }

}