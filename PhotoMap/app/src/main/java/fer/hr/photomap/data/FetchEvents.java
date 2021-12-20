package fer.hr.photomap.data;

import android.media.metrics.Event;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javax.net.ssl.HttpsURLConnection;

import fer.hr.photomap.Utils;
import fer.hr.photomap.data.model.EventData;

public class FetchEvents extends AsyncTask<String, String, String> {
    GoogleMap mMap;
    URL databaseEndpoint = new URL("https://diplomski-projekt.herokuapp.com/api/objave");

    // Create connection

    public FetchEvents(GoogleMap mMap) throws IOException {
        this.mMap = mMap;
    }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String inputLine;
            try {
                Log.d("fetch", "fetching");
                HttpsURLConnection myConnection = (HttpsURLConnection) databaseEndpoint.openConnection();
                try {
                    if (myConnection.getResponseCode() == 200) {
                        //Create a new InputStreamReader
                        InputStreamReader streamReader = new
                                InputStreamReader(myConnection.getInputStream());         //Create a new buffered reader and String Builder
                        BufferedReader reader = new BufferedReader(streamReader);
                        StringBuilder stringBuilder = new StringBuilder();         //Check if the line we are reading is not null
                        while((inputLine = reader.readLine()) != null){
                            stringBuilder.append(inputLine);
                        }         //Close our InputStream and Buffered reader
                        reader.close();
                        streamReader.close();         //Set our result equal to our stringBuilder
                        result = stringBuilder.toString();
                        Log.d("ovojeerror", result);
                        return result;
                    } else {
                        Log.e("error", "error in fetching events" + myConnection.getResponseCode());
                    }
                } catch (Exception e) {
                    Log.d("ovojeerror", e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (myConnection != null) {
                        myConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ovojeerror", e.getMessage());
                return "Exception: " + e.getMessage();
            }
            return result;

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String s) {
            try {

                JSONArray eventsArray = new JSONArray(s);

                for(int i = 0; i<eventsArray.length(); i++) {
                    JSONObject event = eventsArray.getJSONObject(i);
                    String description = event.getString("opis");
                    Double latitude = event.getDouble("geografskaSirina");
                    Double longitude = event.getDouble("geografskaDuzina");
                    String username = event.getString("korisnik");
                    String type = event.getString("tipObjave");
                    String image = event.getString("slika");

                    EventData eventData = new EventData(description, latitude, longitude, image, type, username);
                    Log.d("event", eventData.toString());
                    Utils.addMarkerToMap(mMap, eventData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }