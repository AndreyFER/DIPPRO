package fer.hr.photomap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class splashScreen extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        Context context = this;
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        //String usernameReturn = new String();
        //if(checkSignInUser(usernameReturn)) {
        //    Intent intent = new Intent(this, MapsActivity.class);
        //    intent.putExtra("username", usernameReturn);
        //    startActivity(intent);
        //    finish();
        //}else {
        //    Intent intent = new Intent(this, Registration.class);
        //    startActivity(intent);
        //    finish();
        //}
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                String usernameReturn = new String();
                if(checkSignInUser(usernameReturn) || !Utils.isNetworkAvailable(context)) {
                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(getBaseContext(), Registration.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public Boolean checkSignInUser(String usernameReturn) {
        SharedPreferences prefs = getSharedPreferences("PrefFile", MODE_PRIVATE);
        String username = prefs.getString("username", "No username defined");
        if (!username.equals("No username defined")){
            usernameReturn = username;
            return true;
        }
        return false;
    }
}