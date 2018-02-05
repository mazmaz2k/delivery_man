package axeleration.com.finalproject;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String address="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        address = getIntent().getStringExtra("receiver_address");
        Log.d("temp",""+address);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            Log.d("temp", geocoder.getFromLocationName("Israel Jerusalem Gilo ", 5) + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Address> x;
        try {
            x= geocoder.getFromLocationName("Israel Jerusalem Gilo tirosh 9 ", 1);
            //  x.get(0).getLatitude();
            LatLng sydney = new LatLng(x.get(0).getLatitude(),x.get(0).getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            LatLngBounds AUSTRALIA = new LatLngBounds(
                    new LatLng(x.get(0).getLatitude(),x.get(0).getLongitude()), new LatLng(x.get(0).getLatitude(),x.get(0).getLongitude()));
            mMap.setMinZoomPreference(1);
            mMap.setMaxZoomPreference(100);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AUSTRALIA.getCenter(), 15));



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
