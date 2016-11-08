package mx.peta.inmobiliaapp.Servicios;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

public class ServicioGPS extends Service implements LocationListener {

    private Context ctx;

    private double latitud;
    private double longitud;
    Location location;
    boolean gpsActivo = false;
    LocationManager locationManager;

    public ServicioGPS() {
        super();
        this.ctx = this.getApplicationContext();
    }

    public ServicioGPS(Context c) {
        super();
        this.ctx = c;
        getLocation();
    }

    /*
    public void setView(LatLong latLong) {
        latLong.setLatitud(this.latitud);
        latLong.setLongitud(this.longitud);
    }
    */

    public LatLong getLatLong() {
        LatLong latLong = new LatLong();

        latLong.setLatitud(this.latitud);
        latLong.setLongitud(this.longitud);
        return latLong;
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) this.ctx.getSystemService(LOCATION_SERVICE);
            gpsActivo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception e) {}

        int permissionCheck = ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (gpsActivo) {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,
                        500,            // cada cuantos milisegundos se actualiza la posición
                        10,             // cada cuantos metros de actualiza la posición
                        this);
                location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                //locationManager.removeUpdates(this);
            }
        } else {
            Toast.makeText(ctx, "Se requiere usar el GPS",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
