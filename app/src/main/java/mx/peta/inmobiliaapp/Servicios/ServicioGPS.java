package mx.peta.inmobiliaapp.Servicios;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import mx.peta.inmobiliaapp.login.FacebookLoginActivity;

public class ServicioGPS extends Service implements LocationListener {

    private static ServicioGPS instancia = null;

    public static ServicioGPS getInstancia(Context context) {
        if (instancia == null) {
            instancia = new ServicioGPS(context);
            System.out.println("Inmovilia inicializamos el gps");
        }
        return instancia;
    }

    public static ServicioGPS getInstancia() {
        if (instancia == null) {
            System.out.println("Inmobilia se trato de usar el gps antes de inicializarlo");
            return null;
        }
        return instancia;
    }

    private Context ctx;

    private double latitud;
    private double longitud;
    private Location location = null;
    private boolean gpsActivo = false;
    private LocationManager locationManager;


    private ServicioGPS(Context context) {
        super();
        ctx = context;
        getLocation();
    }

    public void stopServiceGPS() {
        locationManager.removeUpdates(this);
    }

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
        } catch (Exception e) {
        }

        int permissionCheck = ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (gpsActivo) {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,
                        50000,            // cada cuantos milisegundos se actualiza la posición
                        10,             // cada cuantos metros de actualiza la posición
                        this);
                location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                if (location == null) {
                    System.out.println("Inmobilia No se ha actualizado la localización");
                } else {
                    latitud = location.getLatitude();
                    longitud = location.getLongitude();
                    //locationManager.removeUpdates(this);
                }
            } else {
                Toast.makeText(ctx, "Se requiere usar el GPS, por favor habilitelo. \n" +
                        "Reinicie la aplicación.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(ctx, "Se requiere usar el GPS, por favor habilitelo. \n" +
                    "Reinicie la aplicación.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Inmobilia recibimos mensaje onLocationChanged");
        int permissionCheck = ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            if (location == null) {
                System.out.println("Inmobilia No se ha actualizado la localización");
            } else {
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                System.out.println("Inmobilia latitud:" + latitud + " longitud:" + longitud);
            }
        }
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
