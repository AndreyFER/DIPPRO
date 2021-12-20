package fer.hr.photomap;

import androidx.appcompat.app.AppCompatActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.constant.ImageProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;

import fer.hr.photomap.data.FetchTypes;

public class AddItem extends AppCompatActivity {
    Context context = this;
    FloatingActionButton cameraButton;
    FloatingActionButton galleryButton;
    FloatingActionButton saveButton;
    Spinner spinner;
    private static String PROVIDER;
    Uri imageUri;
    EditText descriptionText;
    EditText latitudeText;
    EditText longitudeText;
    ImageView imageShow;
    boolean imageSet = false;
    Double latitude;
    Double longitude;
    boolean locationEnabled;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1000;
    private static final int CAMERA_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        latitudeText = (EditText) findViewById(R.id.latitudeText);
        latitudeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(imageSet && descriptionText.getText().length() > 0){
                    if(!locationEnabled && latitudeText.getText().length() > 0 && longitudeText.getText().length() > 0)
                        saveButton.setEnabled(true);
                    else if (locationEnabled) saveButton.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        longitudeText = (EditText) findViewById(R.id.longitudeText);
        longitudeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(imageSet && descriptionText.getText().length() > 0){
                    if(!locationEnabled && latitudeText.getText().length() > 0 && longitudeText.getText().length() > 0)
                        saveButton.setEnabled(true);
                    else if (locationEnabled) saveButton.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        locationEnabled = latitude != 0 && longitude != 0;
        if(!locationEnabled){
            latitudeText.setEnabled(true);
            latitudeText.setVisibility(View.VISIBLE);
            longitudeText.setEnabled(true);
            longitudeText.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),
                    "Location unavailable. Please enter latitude/longitude manually.",
                    Toast.LENGTH_LONG).show();
        } else{
            latitudeText.setEnabled(false);
            latitudeText.setVisibility(View.GONE);
            longitudeText.setEnabled(false);
            longitudeText.setVisibility(View.GONE);
        }

        descriptionText = (EditText) findViewById(R.id.descriptionText);
        descriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(imageSet && descriptionText.getText().length() > 0){
                    if(!locationEnabled && latitudeText.getText().length() > 0 && longitudeText.getText().length() > 0)
                        saveButton.setEnabled(true);
                    else if (locationEnabled) saveButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
        saveButton.setEnabled(false);
        saveButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if(latitude == 0) latitude = Double.parseDouble(latitudeText.getText().toString());
                if(longitude == 0) longitude = Double.parseDouble(longitudeText.getText().toString());
                intent.putExtra("lat", latitude);
                intent.putExtra("lon", longitude);
                intent.putExtra("type", spinner.getSelectedItem().toString());
                intent.putExtra("description", descriptionText.getText().toString());
                try {
                    intent.putExtra("image", Utils.getBase64String(imageUri, context));
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                    setResult(RESULT_OK, intent);
                finish();
            }
        });

        cameraButton = (FloatingActionButton) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PROVIDER="CAMERA";
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                ImagePicker.Companion.with(AddItem.this)
                        .provider(ImageProvider.CAMERA)
                        .compress(1048)//maksimalna velicina slike u KB
                        .maxResultSize(650,650)
                        .start();
            }
        });

        galleryButton = (FloatingActionButton) findViewById(R.id.galleryButton);
        galleryButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PROVIDER="GALLERY";
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                ImagePicker.Companion.with(AddItem.this)
                        .provider(ImageProvider.GALLERY)
                        .compress(1048)//maksimalna velicina slike u KB
                        .maxResultSize(650,650)
                        .start();
            }
        });

        imageShow = findViewById(R.id.imageShow);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if(Utils.isNetworkAvailable(context)) {
            try {
                FetchTypes fetchTypes = new FetchTypes(spinner);
                fetchTypes.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.REQUEST_CODE) {
            if (resultCode == AddItem.RESULT_OK) {

                imageUri = data.getData();
                imageShow.setImageURI(imageUri);
                imageSet = true;

                if(imageSet && descriptionText.getText().length() > 0){
                    if(!locationEnabled && latitudeText.getText().length() > 0 && longitudeText.getText().length() > 0)
                        saveButton.setEnabled(true);
                    else if (locationEnabled) saveButton.setEnabled(true);
                }
                //You can get File object from intent
                //File file = ImagePicker.Companion.getFile(data);

                //You can also get File Path from intent
                //String filePath = ImagePicker.Companion.getFilePath(data);


            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ne bi trebao dobiti ovu gresku", Toast.LENGTH_SHORT).show();
        }
    }


}