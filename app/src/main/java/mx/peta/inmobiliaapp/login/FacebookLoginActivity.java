package mx.peta.inmobiliaapp.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mx.peta.inmobiliaapp.InternetConnection;
import mx.peta.inmobiliaapp.Servicios.ServicioGPS;
import mx.peta.inmobiliaapp.VistaInicial;
import mx.peta.inmobiliaapp.R;

import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FacebookLoginActivity extends AppCompatActivity implements FacebookCallback<LoginResult> {
    LoginButton loginButton;

    private CallbackManager callbackManager;
    private AlertDialog alert = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "mx.peta.inmobiliaapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                System.out.println("Inmobilia KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        // antes que nada se verifica si tenemos conexion de internet
        if (InternetConnection.isOnLine()) {
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                new AlertDialog.Builder(FacebookLoginActivity.this)
                        .setTitle("GPS desactivado")
                        .setMessage("El GPS es indispensable para continuar ¿Desea activarlo?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                                finishAffinity();
                            }
                        })
                        .show();
            } else {
                ServicioGPS.getInstancia(getApplicationContext());  // Inicializamon el gps
                loginButton = (LoginButton) findViewById(R.id.fb_login_button);
            /*
                Vamos a hacer el login usando a Facebook
            */
                loginButton.setPublishPermissions("publish_actions");

                FacebookSdk.sdkInitialize(this.getApplicationContext());
                callbackManager = CallbackManager.Factory.create();

                loginButton.setPublishPermissions("publish_actions");
                loginButton.registerCallback(callbackManager, this);

                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (AccessToken.getCurrentAccessToken() != null) {
                    System.out.println("Inmobilia tenemos una sesión abierta");
                    startActivity(new Intent(this, VistaInicial.class));
                }
                System.out.println("Inmobilia Inicializamos el catalogo durante el splas");
            }
        } else {
            new AlertDialog.Builder(FacebookLoginActivity.this)
                    .setTitle("Error")
                    .setMessage("No tenemos conexion a Internet")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        System.out.println("Inmobilia entramos por onSuccess");
        startActivity(new Intent(this, VistaInicial.class));
    }

    @Override
    public void onCancel() {
        Snackbar.make(findViewById(android.R.id.content), "Se cancelo la operación", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onError(FacebookException error) {
        Snackbar.make(findViewById(android.R.id.content), "Rebisar la conexión a Internet", Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
