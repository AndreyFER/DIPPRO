package fer.hr.photomap;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import fer.hr.photomap.data.model.EventData;

public class DatabaseConnection extends AsyncTask<String, String, String> {
    GoogleMap mMap;
    URL databaseEndpoint = new URL("https://api.github.com/");

    // Create connection
    HttpsURLConnection myConnection = (HttpsURLConnection) databaseEndpoint.openConnection();

    public DatabaseConnection(GoogleMap mMap, EventData eventData) throws IOException {
        this.mMap = mMap;
    }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    if (myConnection.getResponseCode() == 200) {

                    InputStream in = myConnection.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);
                    int data = isw.read();
                    while (data != -1) {
                        result += (char) data;
                        data = isw.read();

                    }
                    Log.d("result", result);
                    // return the data to onPostExecute method
                    return result;

                    } else {
                        // Error handling code goes here
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return result;

        }

        @Override
        protected void onPostExecute(String s) {
            try {

                JSONObject jsonObject = new JSONObject(s);


                String currentUser =jsonObject.getString("current_user_authorizations_html_url");
                Log.d("result2", currentUser);
                LatLng sydneyLatLng = new LatLng(-36, 147);
                Marker mark = mMap.addMarker(new MarkerOptions().position(sydneyLatLng )
                        .title(currentUser)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }