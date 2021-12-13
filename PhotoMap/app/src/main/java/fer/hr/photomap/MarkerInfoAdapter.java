package fer.hr.photomap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

class MarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;
    private Context context;

    public MarkerInfoAdapter(Context context){
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    // Defines the contents of the InfoWindow
    @Override
    public View getInfoContents(Marker arg0) {

        // Getting view from the layout file infowindowlayout.xml
        View v = inflater.inflate(R.layout.marker_info_layout, null);

        LatLng latLng = arg0.getPosition();

        TextView userView = (TextView) v.findViewById(R.id.user);
        TextView typeView = (TextView) v.findViewById(R.id.type);
        TextView description = (TextView) v.findViewById(R.id.description);
        String unparsedTitle = arg0.getTitle();
        String snippetData=arg0.getSnippet();

        String[] titleParts = unparsedTitle.split(";");

        userView.setText(titleParts[0]);
        typeView.setText(titleParts[1]);
        description.setText(titleParts[2]);
        if(snippetData != null) {
            ImageView im = (ImageView) v.findViewById(R.id.markerImage);
            // Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.grom);
            Bitmap image = decodeBase64(snippetData);
            im.setImageBitmap(image);
        }
        //im.setImageURI(Uri.parse(snippetData));

        return v;

    }

    private Bitmap decodeBase64(String completeImageData) {

        InputStream stream = new ByteArrayInputStream(Base64.decode(completeImageData.getBytes(), Base64.DEFAULT));

        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        return bitmap;
    }
}