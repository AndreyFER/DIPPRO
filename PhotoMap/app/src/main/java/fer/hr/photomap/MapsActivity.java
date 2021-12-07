package fer.hr.photomap;

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
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.io.InputStream;
import java.util.ArrayList;

import fer.hr.photomap.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    View mapView;
    FloatingActionButton customLocationButton;
    FloatingActionButton addItemButton;
    private View defaultlocationButton;
    ArrayList<Float> hueList = new ArrayList<Float>();


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

        addHuesToList();

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
        mMap.setInfoWindowAdapter(new MarkerInfoAdapter(this));

        // Add a marker in Sydney and move the camera
        LatLng sydneyLatLng = new LatLng(-34, 151);
        Marker mark1 = mMap.addMarker(new MarkerOptions().position(new LatLng(-33.87365, 151.20689))
                .title("Marker1")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        Marker mark2 = mMap.addMarker(new MarkerOptions().position(new LatLng(-35.87365, 161.20689))
                .title("Marker2")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydneyLatLng));

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
                LatLng latlng = getCurrentLocation();
                intentNextActivity.putExtra("latitude", latlng.latitude );
                intentNextActivity.putExtra("longitude", latlng.longitude );
                startActivityForResult(intentNextActivity, 40);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 40 &&
                resultCode == RESULT_OK) {
            String lat = intent.getStringExtra("lat");
            String lon = intent.getStringExtra("lon");
            int type = intent.getIntExtra("type", 0) % 9;
            String imageUriString = intent.getStringExtra("image");
            String description = intent.getStringExtra("description");
            Uri imageUri = Uri.parse(imageUriString);

            Marker mark = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat) , Double.parseDouble(lon) ))
                    .title(description)
                    .snippet(imageUriString)
                    .icon(BitmapDescriptorFactory.defaultMarker(hueList.get(type))));
        }
    }
    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    //Getting current location
    private LatLng getCurrentLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider;
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) locationProvider = LocationManager.NETWORK_PROVIDER;
        else if( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) locationProvider = LocationManager.GPS_PROVIDER;
        else return null;
        @SuppressLint("MissingPermission") android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        return new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
    }
    private void addHuesToList() {
        hueList.add(BitmapDescriptorFactory.HUE_AZURE);
        hueList.add(BitmapDescriptorFactory.HUE_BLUE);
        hueList.add(BitmapDescriptorFactory.HUE_CYAN);
        hueList.add(BitmapDescriptorFactory.HUE_GREEN);
        hueList.add(BitmapDescriptorFactory.HUE_MAGENTA);
        hueList.add(BitmapDescriptorFactory.HUE_ORANGE);
        hueList.add(BitmapDescriptorFactory.HUE_RED);
        hueList.add(BitmapDescriptorFactory.HUE_ROSE);
        hueList.add(BitmapDescriptorFactory.HUE_VIOLET);
        hueList.add(BitmapDescriptorFactory.HUE_YELLOW 	);
    }
}