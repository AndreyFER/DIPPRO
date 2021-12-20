package fer.hr.photomap.data;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import fer.hr.photomap.data.model.EventData;

public class FetchTypes extends AsyncTask<String, String, String> {
    Spinner spinner;
    URL databaseEndpoint = new URL("https://diplomski-projekt.herokuapp.com/api/tipObjave");

    // Create connection
    HttpsURLConnection myConnection = (HttpsURLConnection) databaseEndpoint.openConnection();

    public FetchTypes(Spinner spinner) throws IOException {
        this.spinner = spinner;
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
                    if (myConnection != null) {
                        myConnection.disconnect();
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

                JSONArray typesArray = new JSONArray(s);
                List<String> typesList = new ArrayList<>();

                for(int i = 0; i<typesArray.length(); i++){
                    JSONObject typeObject = typesArray.getJSONObject(i);
                    String type = typeObject.getString("naziv");
                    typesList.add(type);
                }

                Log.d("typesList", typesList.toString());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(), android.R.layout.simple_spinner_dropdown_item, typesList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }