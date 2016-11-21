package mx.peta.inmobiliaapp;

import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookBroadcastReceiver;

/**
 * Created by rayo on 11/17/16.
 */

public class InmobiliaBroadcastReceiver extends FacebookBroadcastReceiver {
    @Override
    protected void onSuccessfulAppCall(String appCallId, String action, Bundle extras) {
        // A real app could update UI or notify the user that their photo was uploaded.
        Log.d("HelloFacebook", String.format("Photo uploaded by call " + appCallId + " succeeded."));
    }

    @Override
    protected void onFailedAppCall(String appCallId, String action, Bundle extras) {
        // A real app could update UI or notify the user that their photo was not uploaded.
        Log.d("HelloFacebook", String.format("Photo uploaded by call " + appCallId + " failed."));
    }
}
