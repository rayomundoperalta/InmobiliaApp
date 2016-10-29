package mx.peta.inmobiliaapp;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by rayo on 10/28/16.
 */

public class MapHelper implements OnMapReadyCallback {

    private static MapHelper mapHelper = new MapHelper();

    public static MapHelper getInstance() { return mapHelper; }

    private MapHelper() {}

    private GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // coordenadas de la Ciudad de México 19.3424545,-99.1843678
        LatLng mexico = new LatLng(19.3424545,-99.1843678);
        //LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(mexico).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mexico));
    }
}

