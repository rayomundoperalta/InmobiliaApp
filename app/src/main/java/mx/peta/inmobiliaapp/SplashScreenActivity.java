package mx.peta.inmobiliaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView splashScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // We are going to show the splash screen here
        splashScreen = (ImageView) findViewById(R.id.splashImage);
        splashScreen.setImageResource(R.drawable.plantaarq);
        // We transfer the control to the FBLoginActivity to control mx.peta.inmobiliaapp.login
        //Intent  intent = new Intent(getApplicationContext(), FBLoginActivity.class);
        //startActivity(intent);
        //finish();
    }
}
