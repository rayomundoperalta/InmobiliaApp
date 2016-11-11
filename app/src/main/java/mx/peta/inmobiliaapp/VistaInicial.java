package mx.peta.inmobiliaapp;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import mx.peta.inmobiliaapp.SQL.PropiedadesDataSource;
import mx.peta.inmobiliaapp.SQL.PropiedadesModelItem;
import mx.peta.inmobiliaapp.SQL.PropiedadesSqLiteHelper;
import mx.peta.inmobiliaapp.Servicios.LatLong;
import mx.peta.inmobiliaapp.Servicios.ServicioGPS;

import static mx.peta.inmobiliaapp.SQL.PropiedadesDataSource.getAllItems;

public class VistaInicial extends AppCompatActivity implements OnMapReadyCallback {

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Propiedad propiedad = Propiedad.getInstance();
    private GoogleMap mMap;

    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_inicial);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializamos el registro propiedad
        propiedad.setTakingPhotoState(false);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.capturaPropiedad:
                        Intent intent = new Intent(getApplicationContext(), CapturaEstadoMunicipio.class);
                        startActivity(intent);
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.starred:
                        Toast.makeText(getApplicationContext(),"Stared Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.sent_mail:
                        Toast.makeText(getApplicationContext(),"Send Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.drafts:
                        Toast.makeText(getApplicationContext(),"Drafts Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.allmail:
                        Toast.makeText(getApplicationContext(),"All Mail Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.trash:
                        Toast.makeText(getApplicationContext(),"Trash Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.fin:
                        finishAffinity();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle, this is the hamburger
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                System.out.println("Inmobilia cerre la hamburgesa");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
                System.out.println("Inmobilia abri la hamburgesa");
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();



        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        System.out.println("Inmmobilia creamos el menu de opciones");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                System.out.println("Inmobilia seleccionamos configuracion");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PropiedadesDataSource ds = new PropiedadesDataSource(getApplicationContext());
        int cuantosRegistros = ds.cuantosRegistros(PropiedadesSqLiteHelper.APP_TABLE_NAME);
        if (cuantosRegistros == 0) {
            // si no hay registros en la base de datos mostramos la posición del celular
            ServicioGPS gps = ServicioGPS.getInstancia();
            LatLong latLong = gps.getLatLong();
            if (latLong == null)
                System.out.println("Inmobilia no hemos podido recuperar la posición");
            LatLng posicionActual = new LatLng(latLong.getLatitud(), latLong.getLongitud());
            // Add a marker in Mexico and move the camera
            // coordenadas de la Ciudad de México 19.3424545,-99.1843678
            mMap.addMarker(new MarkerOptions().position(posicionActual).title("Tuera 55").snippet("R$35,000 V$8,000,000"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(posicionActual));
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(posicionActual)
                    .zoom(15)
                    .bearing(0)
                    .build();

            // Animate the change in camera view over 2 seconds
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    2000, null);
        } else {
            List<PropiedadesModelItem> listaPropiedades = getAllItems();
            for (PropiedadesModelItem item:listaPropiedades) {
                LatLng pos = new LatLng(item.latitud, item.longitud);
                mMap.addMarker(new MarkerOptions().position(pos).title(item.direccion).snippet(item.telefono));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                CameraPosition cameraPosition = CameraPosition.builder()
                        .target(pos)
                        .zoom(15)
                        .bearing(0)
                        .build();

                // Animate the change in camera view over 2 seconds
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                        2000, null);
            }
        }
    }
}
