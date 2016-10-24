package mx.peta.inmobiliaapp;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import mx.peta.inmobiliaapp.login.FacebookLoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView splashScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // We are going to show the splash screen here
        // and transfer control to Facebook login
        splashScreen = (ImageView) findViewById(R.id.splashImage);
        splashScreen.setImageResource(R.drawable.plantaarq);
        /*
            Verificamos si tenemos Internet haciendo un ping al DNS de Google,
            suponemos que es mas probable que el celular no tenga conexión a internet
            a que este caido el DNS de Google.
         */
        int exitValue = 5;
        try {
            Process ipProcess = Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8");
            exitValue = ipProcess.waitFor();

        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        if (exitValue == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // We transfer the control to the FBLoginActivity to control mx.peta.inmobiliaapp.login
                    Intent intent = new Intent(getApplicationContext(), FacebookLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000 * 3);
        }
        else {
            Snackbar.make(findViewById(android.R.id.content), "No tenemos acceso a Internet...", Snackbar.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), "No tenemos acceso a Internet...", Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000 * 5);
        }
    }
}
