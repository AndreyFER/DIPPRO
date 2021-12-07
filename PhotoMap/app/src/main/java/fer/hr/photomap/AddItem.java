package fer.hr.photomap;

import androidx.appcompat.app.AppCompatActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.constant.ImageProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AddItem extends AppCompatActivity {
    FloatingActionButton cameraButton;
    FloatingActionButton galleryButton;
    FloatingActionButton saveButton;
    Spinner spinner;
    private static String PROVIDER;
    Uri imageUri;
    EditText descriptionText;
    ImageView imageShow;
    boolean imageSet = false;
    String latitude;
    String longitude;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1000;
    private static final int CAMERA_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        latitude = Double.toString(getIntent().getDoubleExtra("latitude", 0));
        longitude = Double.toString(getIntent().getDoubleExtra("longitude", 0));

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
        saveButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("lat", latitude);
                intent.putExtra("lon", longitude);
                intent.putExtra("type", spinner.getSelectedItemPosition());
                intent.putExtra("description", descriptionText.getText().toString());
                try {
                    intent.putExtra("image", getBase64String(imageUri));
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
                        //.crop(1f, 1f) //omogucava cropanje po x:y formatu (1:1, 16:9, ...)
                        .compress(4096)//maksimalna velicina slike u KB
                        .maxResultSize(576,576)
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
                        .compress(4096)//maksimalna velicina slike u KB
                        .maxResultSize(576,576)
                        .start();
            }
        });

        imageShow = findViewById(R.id.imageShow);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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

    private String getBase64String(Uri uri) throws FileNotFoundException, IOException {

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // In case you want to compress your image, here it's at 40%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


}