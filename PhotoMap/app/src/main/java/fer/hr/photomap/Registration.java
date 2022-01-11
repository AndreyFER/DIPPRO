package fer.hr.photomap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;

import fer.hr.photomap.data.AsyncResponse;
import fer.hr.photomap.data.SignInUser;
import fer.hr.photomap.data.SignUpUser;
import fer.hr.photomap.data.UploadEvent;
import fer.hr.photomap.data.model.User;

public class Registration extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();

        TextView usernameR =(TextView) findViewById(R.id.usernameR);
        TextView passwordR =(TextView) findViewById(R.id.passwordR);
        TextView signinbtn = (TextView) findViewById(R.id.signinbtn);
        MaterialButton registrationbtn = (MaterialButton) findViewById(R.id.registrationbtn);

        registrationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(usernameR.getText().toString(), passwordR.getText().toString());
                SignUpUser signUpUser = null;
                try {
                    signUpUser= (SignUpUser) new SignUpUser(user, new AsyncResponse() {
                        @Override
                        public void processFinish(Integer output) {
                            if(output != 0) {
                                int returnFromMapsAc = getIntent().getIntExtra("AnonRegistration", 0);
                                Toast.makeText(Registration.this, "Successful registration with username " + usernameR.getText().toString(), Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = getSharedPreferences("PrefFile", MODE_PRIVATE).edit();
                                editor.putString("username", usernameR.getText().toString());
                                editor.apply();
                                if (returnFromMapsAc != 35) {
                                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                }
                                finish();
                            }else{
                                Toast.makeText(Registration.this, "Registration failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Login1.class);
                startActivity(intent);
            }
        });
    }

}