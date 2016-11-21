package mx.peta.inmobiliaapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mx.peta.inmobiliaapp.expandablelistview.CatalogoEstadoMunicipio;
import mx.peta.inmobiliaapp.login.FacebookLoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView splashScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent intent = new Intent(getApplicationContext(), FacebookLoginActivity.class);
        startActivity(intent);

    }
}
