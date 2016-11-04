package mx.peta.inmobiliaapp.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mx.peta.inmobiliaapp.VistaInicial;
import mx.peta.inmobiliaapp.R;

import android.support.design.widget.Snackbar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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
            startActivity(new Intent(this, VistaInicial.class));
        }

    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Snackbar.make(findViewById(android.R.id.content), "Ya entramos", Snackbar.LENGTH_LONG).show();
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
