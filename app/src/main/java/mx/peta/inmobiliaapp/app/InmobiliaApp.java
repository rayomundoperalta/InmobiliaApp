package mx.peta.inmobiliaapp.app;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Created by rayo on 10/24/16.
 */

public class InmobiliaApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
    }
}
