package fer.hr.photomap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import fer.hr.photomap.data.AsyncResponse;
import fer.hr.photomap.data.SignInUser;
import fer.hr.photomap.data.model.User;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        getSupportActionBar().hide();

        TextView usernameL = (TextView) findViewById(R.id.usernameL);
        TextView passwordL = (TextView) findViewById(R.id.passwordL);
        TextView signupbtn = (TextView) findViewById(R.id.signupbtn);
        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(usernameL.getText().toString(), passwordL.getText().toString());
                SignInUser signinUser = null;

                signinUser = (SignInUser) new SignInUser(user, new AsyncResponse() {
                    @Override
                    public void processFinish(Integer output) {
                        if (output != 0) {
                            int returnFromMapsAc = getIntent().getIntExtra("AnonLogin", 0);
                            Toast.makeText(Login.this, "Logged in as user " + usernameL.getText().toString(), Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = getSharedPreferences("PrefFile", MODE_PRIVATE).edit();
                            editor.putString("username", usernameL.getText().toString());
                            editor.apply();
                            if (returnFromMapsAc != 30) {
                                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                            }
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Login failed", Toast.LENGTH_LONG).show();
                        }
                    }
                }).execute();
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int returnFromMapsAc = getIntent().getIntExtra("AnonLogin", 0);
                Intent intent = new Intent(getBaseContext(), Registration.class);
                if (returnFromMapsAc != 30) {
                    startActivity(intent);
                    finish();
                } else { //call Registration for AnonRegistration
                    intent.putExtra("AnonRegistration", 35);
                    startActivityForResult(intent, 35);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 35 &&
                resultCode == RESULT_OK) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }
}