package fer.hr.photomap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;

import fer.hr.photomap.data.AsyncResponse;
import fer.hr.photomap.data.SignInUser;
import fer.hr.photomap.data.SignUpUser;
import fer.hr.photomap.data.model.User;

public class Login1 extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        getSupportActionBar().hide();

        TextView usernameL =(TextView) findViewById(R.id.usernameL);
        TextView passwordL =(TextView) findViewById(R.id.passwordL);
        TextView signupbtn = (TextView) findViewById(R.id.signupbtn);
        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(usernameL.getText().toString(), passwordL.getText().toString());
                SignInUser signinUser = null;

                signinUser= (SignInUser) new SignInUser(user, new AsyncResponse() {
                    @Override
                    public void processFinish(Integer output) {
                        if(output != 0) {
                            int returnFromMapsAc = getIntent().getIntExtra("AnonLogin", 35);
                            Toast.makeText(Login1.this, "Prijavljen korisnik " + usernameL.getText().toString(), Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = getSharedPreferences("PrefFile", MODE_PRIVATE).edit();
                            editor.putString("username", usernameL.getText().toString());
                            editor.apply();
                            if(returnFromMapsAc != 30) {
                                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                            }
                            finish();
                        }else{
                            Toast.makeText(Login1.this, "Nije uspjela prijava", Toast.LENGTH_LONG).show();
                        }
                    }
                }).execute();
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Registration.class);
                startActivity(intent);
            }
        });
    }

    }