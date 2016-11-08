package mx.peta.inmobiliaapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import mx.peta.inmobiliaapp.expandablelistview.CatalogoEstadoMunicipio;
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // We transfer the control to the FBLoginActivity to control mx.peta.inmobiliaapp.login
                Intent intent = new Intent(getApplicationContext(), FacebookLoginActivity.class);
                startActivity(intent);
                CatalogoEstadoMunicipio.getInstance(); // Solo queremos que se inicalize la instancia aquí
                System.out.println("Inmobilia Inicializamos el catalogo durante el splas");
                finish();
            }
        },1000*1);

    }
}
