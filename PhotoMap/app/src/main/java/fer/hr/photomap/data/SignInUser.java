package fer.hr.photomap.data;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import fer.hr.photomap.Registration;
import fer.hr.photomap.data.model.User;

public class SignInUser extends AsyncTask<String, String, Integer> {
    public AsyncResponse delegate = null;
    User user;

    public SignInUser(User user, AsyncResponse delegate) {
        this.user = user;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... params) {
        Integer result;
        try {
            JSONObject postData = new JSONObject();
            postData.put("korisnickoIme", user.getUsername());
            postData.put("lozinka", user.getPassword());
            URL databaseEndpoint = new URL("https://diplomski-projekt.herokuapp.com/api/korisnici/login");
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
                line = rd.readLine();
                result = Integer.parseInt(line);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            } finally {
                if (myConnection != null) {
                    myConnection.disconnect();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return result;

    }

    @Override
    protected void onPostExecute(Integer s) {
        delegate.processFinish(s);
    }
}
