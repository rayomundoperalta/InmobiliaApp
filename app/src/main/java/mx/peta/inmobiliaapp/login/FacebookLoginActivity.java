package mx.peta.inmobiliaapp.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mx.peta.inmobiliaapp.R;

import com.facebook.FacebookSdk;

public class FacebookLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
    }
}
