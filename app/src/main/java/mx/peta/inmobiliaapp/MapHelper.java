package mx.peta.inmobiliaapp;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
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

        // Add a marker in Mexico and move the camera
        // coordenadas de la Ciudad de México 19.3424545,-99.1843678
        LatLng mexico = new LatLng(19.341822116645, -99.183682);
        mMap.addMarker(new MarkerOptions().position(mexico).title("Tuera 55").snippet("R$35,000 V$8,000,000"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mexico));
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mexico)
                .zoom(15)
                .bearing(0)
                .build();

        // Animate the change in camera view over 2 seconds
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);
    }
}

