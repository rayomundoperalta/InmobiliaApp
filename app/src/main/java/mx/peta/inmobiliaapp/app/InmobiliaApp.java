package mx.peta.inmobiliaapp.app;

import android.app.Application;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import mx.peta.inmobiliaapp.Servicios.ServicioGPS;

/**
 * Created by rayo on 10/24/16.
 */

public class InmobiliaApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        /*
            Se puede invocar el metodo getInstance de un singleton con el argumento  Context.getApplicationContext() as a Context argument
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        // ServicioGPS.getInstancia(getApplicationContext());  // Inicializamon el gps
    }
}
