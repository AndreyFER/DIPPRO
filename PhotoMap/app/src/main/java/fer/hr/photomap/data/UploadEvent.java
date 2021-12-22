package fer.hr.photomap.data;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import fer.hr.photomap.data.model.EventData;

public class UploadEvent extends AsyncTask<String, String, String> {
    EventData eventData;


    public UploadEvent(EventData eventData) throws IOException {
        this.eventData = eventData;
    }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                JSONObject postData = new JSONObject();
                postData.put("opis", eventData.getDescription());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(new Date());
                postData.put("datum", date);
                postData.put("geografskaSirina", eventData.getLatitude());
                postData.put("geografskaDuzina", eventData.getLongitude());
                postData.put("korisnik", eventData.getUser());
                postData.put("tipObjave", eventData.getType());
                postData.put("slika", eventData.getImage());
                Log.d("json", postData.toString());
                Log.d("image", eventData.getImage());
                URL databaseEndpoint = new URL("https://diplomski-projekt.herokuapp.com/api/objave");
                HttpsURLConnection myConnection = (HttpsURLConnection) databaseEndpoint.openConnection();
                try {
                    myConnection.setRequestProperty("Content-Type", "application/json");
                    myConnection.setRequestMethod("POST");
                    myConnection.setDoOutput(true);
                    myConnection.setDoInput(true);
                    myConnection.setChunkedStreamingMode(0);

                    OutputStream out = new BufferedOutputStream(myConnection.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                            out, "UTF-8"));
                    writer.write(postData.toString());
                    writer.flush();

                    int code = myConnection.getResponseCode();
                    if (code !=  200) {
                        throw new IOException("Invalid response from server: " + code);
                    }

                    BufferedReader rd = new BufferedReader(new InputStreamReader(
                            myConnection.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        Log.i("data", line);
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
        }

    }