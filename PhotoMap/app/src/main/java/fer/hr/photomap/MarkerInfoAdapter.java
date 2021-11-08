package fer.hr.photomap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

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

        TextView tv1 = (TextView) v.findViewById(R.id.textView1);
        TextView tv2 = (TextView) v.findViewById(R.id.textView2);
        String title=arg0.getTitle();
        String informations=arg0.getSnippet();

        tv1.setText(title);
        tv2.setText(informations);
        ImageView im = (ImageView) v.findViewById(R.id.imageView1);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.grom);
        im.setImageBitmap(bm);

        return v;

    }
}