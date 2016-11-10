package mx.peta.inmobiliaapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by rayo on 10/23/16.
 */
import android.content.Context;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by rayo on 9/17/16.
 */
public class InternetConnection {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
        Las rutinas Executer y isOnLine trabajan correctamente
        No se puede usar el comando ping desde eestas rutinas porque no
        logra hacer la noneccion a internet
     */
    public static String Executer(String command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + " ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        return response;
    }

    public static boolean isOnLine() {

        //Runtime runtime = Runtime.getRuntime();
        try {

            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 www.google.com");
            Process ipProcess = Runtime.getRuntime().exec("/system/bin/ping -c 1 www.google.com");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public static int estoyEnInternet() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName("8.8.8.8");
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            return 1;
        }
        try {
            if(addr.isReachable(5000)) {
                return 2;
            } else {
                return 3;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return 4;
        }
    }

}