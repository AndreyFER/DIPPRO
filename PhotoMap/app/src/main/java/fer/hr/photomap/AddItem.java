package fer.hr.photomap;

import androidx.appcompat.app.AppCompatActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.constant.ImageProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddItem extends AppCompatActivity {
    TextView latitudeText;
    TextView longitudeText;
    FloatingActionButton cameraButton;
    FloatingActionButton galleryButton;
    FloatingActionButton saveButton;
    private static String PROVIDER;
    Uri imageUri;
    EditText descriptionText;
    ImageView imageShow;
    boolean imageSet = false;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1000;
    private static final int CAMERA_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        latitudeText = findViewById(R.id.latitudeText);
        longitudeText = findViewById(R.id.longitudeText);
        String latitude = Double.toString(getIntent().getDoubleExtra("latitude", 0));
        String longitude = Double.toString(getIntent().getDoubleExtra("longitude", 0));
        latitudeText.setText("Latitude:" + latitude);
        longitudeText.setText("Longitude: " + longitude);

        descriptionText = (EditText) findViewById(R.id.descriptionText);
        descriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(imageSet && descriptionText.getText().length() > 0) saveButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
        saveButton.setEnabled(false);
        cameraButton = (FloatingActionButton) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PROVIDER="CAMERA";
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                ImagePicker.Companion.with(AddItem.this)
                        .provider(ImageProvider.CAMERA)
                        //.crop(1f, 1f) //omogucava cropanje po x:y formatu (1:1, 16:9, ...)
                        .compress(2048)//maksimalna velicina slike u KB
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
                        //.crop(1f, 1f) //omogucava cropanje po x:y formatu (1:1, 16:9, ...)
                        .compress(2048)//maksimalna velicina slike u KB
                        .start();
            }
        });

        imageShow = findViewById(R.id.imageShow);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.REQUEST_CODE) {
            if (resultCode == AddItem.RESULT_OK) {

                imageUri = data.getData();
                imageShow.setImageURI(imageUri);
                imageSet = true;

                if (descriptionText.getText().toString().length()>0 && !saveButton.isEnabled()) //if user has entered a description
                {
                    saveButton.setEnabled(true);
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