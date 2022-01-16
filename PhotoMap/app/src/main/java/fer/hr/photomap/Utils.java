package fer.hr.photomap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.location.LocationManagerCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

import fer.hr.photomap.data.model.EventData;

public class Utils {
    static final ArrayList<Float> hueList = new ArrayList<>(Arrays.asList(BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_MAGENTA
            ,BitmapDescriptorFactory.HUE_ORANGE,BitmapDescriptorFactory.HUE_RED,BitmapDescriptorFactory.HUE_ROSE,
            BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_VIOLET));

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    //Getting current location
    public static LatLng getCurrentLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider;
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) locationProvider = LocationManager.NETWORK_PROVIDER;
        else if( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) locationProvider = LocationManager.GPS_PROVIDER;
        else return null;
        @SuppressLint("MissingPermission") android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if(lastKnownLocation != null) return new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
        else return null;

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static String getBase64String(Uri uri, Context context) throws FileNotFoundException, IOException {

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // In case you want to compress your image, here it's at 40%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    public static void saveToInternalStorage(Context context, ArrayList<EventData> eventList) {
        try {
            FileOutputStream fos = context.openFileOutput("EventDataList", Context.MODE_PRIVATE);
            ObjectOutputStream of = new ObjectOutputStream(fos);
            of.writeObject(eventList);
            of.flush();
            of.close();
            fos.close();
        }
        catch (Exception e) {
            Log.e("InternalStorage", e.getMessage());
        }
    }
    public static ArrayList<EventData> readFromInternalStorage(Context context) {
        ArrayList<EventData> toReturn = new ArrayList<>();
        FileInputStream fis;
        try {
            fis = context.openFileInput("EventDataList");
            ObjectInputStream oi = new ObjectInputStream(fis);
            toReturn = (ArrayList<EventData>) oi.readObject();
            oi.close();
        } catch (FileNotFoundException e) {
            Log.e("InternalStorage", e.getMessage());
        } catch (IOException e) {
            Log.e("InternalStorage", e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void addMarkerToMap(GoogleMap mMap, EventData eventData) {
        StringJoiner joiner = new StringJoiner(";");
        joiner.add(eventData.getUser()).add(eventData.getType()).add(eventData.getDescription());
        String concatenatedData = joiner.toString();

        int typeIndex = eventData.getType().hashCode() % hueList.size();
        mMap.addMarker(new MarkerOptions().position(new LatLng(eventData.getLatitude(),eventData.getLongitude()))
                .title(concatenatedData)
                .snippet(eventData.getImage())
                .icon(BitmapDescriptorFactory.defaultMarker(hueList.get(typeIndex))));
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
