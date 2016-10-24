package mx.peta.inmobiliaapp.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mx.peta.inmobiliaapp.InternetConnection;
import mx.peta.inmobiliaapp.MapaActivity;
import mx.peta.inmobiliaapp.R;

import android.support.design.widget.Snackbar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.R.attr.host;

public class FacebookLoginActivity extends AppCompatActivity implements FacebookCallback<LoginResult> {
    LoginButton loginButton;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        loginButton = (LoginButton) findViewById(R.id.fb_login_button);

        /*
         Vamos a hacer el login usando a Facebook
        */

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, this);
        if (AccessToken.getCurrentAccessToken() != null) {
            Snackbar.make(findViewById(android.R.id.content), "access token", Snackbar.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MapaActivity.class);
            startActivity(new Intent(this, MapaActivity.class));
        }

    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Snackbar.make(findViewById(android.R.id.content), "Ya entramos", Snackbar.LENGTH_LONG).show();
        Intent intent = new Intent(this, MapaActivity.class);
        startActivity(new Intent(this, MapaActivity.class));
    }

    @Override
    public void onCancel() {
        Snackbar.make(findViewById(android.R.id.content), "Se cancelo la operación", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onError(FacebookException error) {
        Snackbar.make(findViewById(android.R.id.content), "Rebisar la conexión a Internet", Snackbar.LENGTH_LONG).show();
    }
}
