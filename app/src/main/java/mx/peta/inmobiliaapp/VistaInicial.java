package mx.peta.inmobiliaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import mx.peta.inmobiliaapp.SQL.PropiedadesDataSource;
import mx.peta.inmobiliaapp.SQL.PropiedadesModelItem;
import mx.peta.inmobiliaapp.SQL.PropiedadesSqLiteHelper;
import mx.peta.inmobiliaapp.Servicios.LatLong;
import mx.peta.inmobiliaapp.Servicios.ServicioGPS;

public class VistaInicial extends AppCompatActivity implements OnMapReadyCallback {

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Propiedad propiedad = Propiedad.getInstance();
    private GoogleMap mMap = null;

    private CameraPosition cameraPosition;
    private LatLngBounds bounds;

    SupportMapFragment mapFragment;

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Inmobilia Vista Inicial onResume()");

        // Se dibuja el mapa
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Inicializamos el registro propiedad
        propiedad.setTakingPhotoState(false);
        if (mMap != null) mMap.clear();

        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_inicial);
        System.out.println("Inmobilia Vista Inicial onCreate()");

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);



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
                    /**
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
                    /**/
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

        System.out.println("Inmobilia Vista inicial onMapReady()");
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if ((int) marker.getTag() > 0) {
                    System.out.println("Inmobilia click del info window " + marker.getId() + " " + marker.getTag());
                    Intent intent = new Intent(getApplicationContext(), VerDetallePropiedad.class);
                    intent.putExtra("MARKER_TAG", (int) marker.getTag());
                    startActivity(intent);
                }
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                System.out.println("Inmobilia getInfoWindow");
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                System.out.println("Inmobilia getInfoContents" + marker.getTag());
                if ((int) marker.getTag()  > 0) {
                    PropiedadesDataSource ds = new PropiedadesDataSource(getApplicationContext());
                    PropiedadesModelItem modelItem = ds.getRegistro((int) marker.getTag());
                    ds.close();
                    View view = getLayoutInflater().inflate(R.layout.info_window_propiedad, null);
                    ImageView imageView = (ImageView) view.findViewById(R.id.infoWindowImage);
                    TextView textViewTitle = (TextView) view.findViewById(R.id.infoWindowsTitle);
                    TextView textViewSnippet = (TextView) view.findViewById(R.id.infoWindowSnippet);

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(modelItem.photoFileName, bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW / 100, photoH / 100);

                    // Decode the image file into a Bitmap sized to fill the View
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;

                    Bitmap bitmap = BitmapFactory.decodeFile(modelItem.photoFileName, bmOptions);
                    imageView.setImageBitmap(bitmap);

                    textViewTitle.setText(modelItem.direccion);
                    textViewSnippet.setText(modelItem.telefono);
                    return view;
                } else {
                    return null;
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                System.out.println("Inmobilia detectamos el click en el marcador de mapa: " + marker.getId() + " " + marker.getTag());
                return true;
                /*
                // just delay a little bit in orden to let read the info window
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), VerDetallePropiedad.class);
                        startActivity(intent);
                    }
                },1000*3);
                return true;
                */
            }
        });

        PropiedadesDataSource ds = new PropiedadesDataSource(getApplicationContext());
        int cuantosRegistros = ds.cuantosRegistros(PropiedadesSqLiteHelper.APP_TABLE_NAME);
        System.out.println("Inmobilia registros en la base de datos -> > " + cuantosRegistros);
        if (cuantosRegistros == 0) {
            ds.close();
            // si no hay registros en la base de datos mostramos la posición del celular
            LatLong latLong = ServicioGPS.getInstancia().getLatLong();
            if (latLong == null)
                System.out.println("Inmobilia no hemos podido recuperar la posición");
            LatLng posicionActual = new LatLng(latLong.getLatitud(), latLong.getLongitud());
            // Add a marker in Mexico and move the camera
            // coordenadas de la Ciudad de México 19.3424545,-99.1843678
            mMap.addMarker(new MarkerOptions().position(posicionActual).title("Usd. está aqui").snippet("¿Qué vamos a hacer?")).setTag(0);
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
            System.out.println("Inmobilia tenemos registros en la base de datos");
            List<PropiedadesModelItem> listaPropiedades = ds.getAllItems();
            ds.close();
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            LatLng pos = null;
            for (PropiedadesModelItem item:listaPropiedades) {
                pos = new LatLng(item.latitud, item.longitud);
                System.out.println(pos.toString());
                mMap.addMarker(new MarkerOptions().position(pos).title(item.direccion).snippet(item.telefono)).setTag(item.id);
                boundsBuilder.include(pos);
            }
            bounds = boundsBuilder.build();
            if (cuantosRegistros == 1) {
                cameraPosition = CameraPosition.builder()
                        .target(pos)
                        .zoom(15)
                        .bearing(0)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                cameraPosition = CameraPosition.builder()
                        .target(bounds.getCenter())
                        .bearing(0)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
            }
        }
    }
}

/*
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                CameraPosition cameraPosition = CameraPosition.builder()
                        .target(pos)
                        .zoom(15)
                        .bearing(0)
                        .build();

                // Animate the change in camera view over 2 seconds
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                        2000, null);
*/

/*
            LatLngBounds bounds = new LatLngBounds.Builder()
                 .include(Leicester_Square)
                 .include(Covent_Garden)
                 .include(Piccadilly_Circus)
                 .include(Embankment)
                 .include(Charing_Cross)
                 .build();

                 if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                  mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                 } else {
                  mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                 }
                 myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
 */