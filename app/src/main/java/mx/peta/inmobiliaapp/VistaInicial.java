package mx.peta.inmobiliaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import mx.peta.inmobiliaapp.SQL.EstimacionesCompradasDataSource;
import mx.peta.inmobiliaapp.SQL.EstimacionesCompradasModelItem;
import mx.peta.inmobiliaapp.SQL.EstimacionesCompradasSqLiteHelper;
import mx.peta.inmobiliaapp.SQL.EstimacionesDataSource;
import mx.peta.inmobiliaapp.SQL.EstimacionesModelItem;
import mx.peta.inmobiliaapp.SQL.EstimacionesSqLiteHelper;
import mx.peta.inmobiliaapp.SQL.PropiedadesDataSource;
import mx.peta.inmobiliaapp.SQL.PropiedadesModelItem;
import mx.peta.inmobiliaapp.SQL.PropiedadesSqLiteHelper;
import mx.peta.inmobiliaapp.Servicios.LatLong;
import mx.peta.inmobiliaapp.Servicios.ServicioGPS;

import static mx.peta.inmobiliaapp.PaypalConstants.PAYPAL_SUCCEED;

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

    private int cuantosRegistros;
    private LatLng pos;

    final int RESULT_OF_PAYMENT = 1960;

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
        System.out.println("InmoviliaApp Vista Inicial --->" + this.getClass().getName() + "<---");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_inicial);
        System.out.println("InmobiliaApp Vista Inicial onCreate()");

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
                    case R.id.comprarEstimaciones:
                        Intent intentb = new Intent(getApplicationContext(), InmobiliaPayPalPayment.class);
                        startActivityForResult(intentb, RESULT_OF_PAYMENT);
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
                System.out.println("InmobiliaApp Vista Inicial cerre la hamburgesa");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
                System.out.println("InmobiliaApp Vista Inicial abri la hamburgesa");
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        System.out.println("InmobiliaApp Vista Inicial Salimos de onCreate en Vista Inicial");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            System.out.println("InmobiliaApp Vista Inicial " + this.getClass().getName() + " - back button pressed <---------------------");
            finish();
            System.out.println("InmobiliaApp Vista Inicial despues del finish del onKeyDown en vista inicial");
            return(false);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("InmobiliaApp Vista Inicial onActivityResult vista inicial");
        if (requestCode == RESULT_OF_PAYMENT) {
            System.out.println("InmobiliaApp Vsita Inicial RESULT Of Payment recibido");
            if (resultCode == RESULT_OK) {
                System.out.println("InmobiliaApp Payment result ok");
                switch (data.getIntExtra(PaypalConstants.PAYPAL_RESULT, PaypalConstants.DEFAULT_VALUE)) {
                    case PaypalConstants.PAYPAL_SUCCEED:
                        String payPalPayKey = data.getStringExtra(PaypalConstants.PAY_KEY);
                        System.out.println("InmobiliaApp vista inicial Pay Key: " + payPalPayKey);

                        EstimacionesCompradasDataSource ds = new EstimacionesCompradasDataSource(getApplicationContext());
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        EstimacionesCompradasModelItem modelItem = new EstimacionesCompradasModelItem(df.format(c.getTime()), payPalPayKey);
                        ds.writeRegistro(modelItem);
                        ds.close();
                        EstimacionesDataSource estimacionesDS = new EstimacionesDataSource(getApplicationContext());
                        switch (estimacionesDS.cuantosRegistros(EstimacionesSqLiteHelper.APP_TABLE_NAME)) {
                            case 0: // Es la primera vez que se va a escribir un registro a esta tabla
                                EstimacionesModelItem estimacionesModelItem = new EstimacionesModelItem(3);
                                estimacionesDS.writeRegistro(estimacionesModelItem);
                                estimacionesDS.close();
                                System.out.println("InmobiliaApp Vista Inicial Se cargaron 3 estimaciones");
                                break;
                            case 1: // siempre debe existir solo un registro
                                estimacionesModelItem = estimacionesDS.getRegistro();
                                estimacionesModelItem.estimaciones += 3;
                                estimacionesDS.updateEstimaciones(estimacionesModelItem.estimaciones);
                                estimacionesDS.close();
                                System.out.println("InmobiliaApp Vista Inicial Se adicionaron 3 estimaciones, total = " + estimacionesModelItem.estimaciones);
                                break;
                            default: // Existe un error en la lógica
                                Toast.makeText(getApplicationContext(), "Favor de reinstalar la aplicación", Toast.LENGTH_LONG).show();
                                System.out.println("InmobiliaApp Vista Inicial --> E R R O R   en el manejo de la tabla de estimaciones <--");
                        }
                        EstimacionesModelItem estimacionesModelItem = new EstimacionesModelItem(0);
                        break;
                    case PaypalConstants.PAYPAL_CANCELED:
                        System.out.println("InmobiliaApp Vista Inicial Pago Cancelado");
                        break;
                    case PaypalConstants.PAYPAL_ERROR:
                        String payPalErrorMsg = data.getStringExtra(PaypalConstants.PAYPAL_ERROR_MSG);
                        String payPalErrorID  = data.getStringExtra(PaypalConstants.PAYPAL_ERROR_ID);
                        Toast.makeText(getApplicationContext(),"Error en el pago " + payPalErrorMsg + " " + payPalErrorID, Toast.LENGTH_LONG).show();
                        System.out.println("InmobiliaApp Vista Inicial Error en el pago " + payPalErrorMsg);
                        break;
                    case PaypalConstants.DEFAULT_VALUE:
                        System.out.println("InmobiliaApp Vista Inicial Tenemos problemas con la API de PayPal");
                        break;
                }
            } else {
                System.out.println("InmobiliaApp Vista Inicial Error en el resultcode");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        System.out.println("Inmmobilia Vista Inicial creamos el menu de opciones");
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
                System.out.println("Inmobilia Vista Inicial seleccionamos configuracion");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("InmobiliaApp Vista Inicial se recibe onMapReady");
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                System.out.println("InmoviliaApp Vista Inicial entramos al callback del mapa");
                /*
                    Esta rutina es llamada cuando se ha cargado el mapa y es entonces cuando
                    se pinta el conenido del mapa
                 */

                PropiedadesDataSource ds = new PropiedadesDataSource(getApplicationContext());
                cuantosRegistros = ds.cuantosRegistros(PropiedadesSqLiteHelper.APP_TABLE_NAME);
                System.out.println("InmobiliaApp Vista Inicial registros en la base de datos -> > " + cuantosRegistros);
                if (cuantosRegistros == 0) {
                    ds.close();
                    System.out.println("InmobiliaApp Vista Inicial cuantos registros == 0");
                    // si no hay registros en la base de datos mostramos la posición del celular
                    LatLong latLong = ServicioGPS.getInstancia().getLatLong();
                    if (latLong == null || (latLong.getLatitud() == 0.0 && latLong.getLongitud() == 0.0)) {
                        System.out.println("InmobiliaApp Vista Inicial no hemos podido recuperar la posición con el GPS");
                        Toast.makeText(getApplicationContext(),"La pocisión obtenida del GPS no es correcta", Toast.LENGTH_LONG).show();
                    }
                    LatLng posicionActual = new LatLng(latLong.getLatitud(), latLong.getLongitud());
                    // Add a marker in Mexico and move the camera
                    // coordenadas de la Ciudad de México 19.3424545,-99.1843678
                    mMap.addMarker(new MarkerOptions().position(posicionActual).title("Usd. está aqui").snippet("¿Qué desea hacer?")).setTag(0);
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
                    System.out.println("InmobiliaApp Vista Inicial tenemos registros en la base de datos");
                    List<PropiedadesModelItem> listaPropiedades = ds.getAllItems();
                    ds.close();
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    pos = null;
                    for (PropiedadesModelItem item:listaPropiedades) {
                        pos = new LatLng(item.latitud, item.longitud);
                        System.out.println("InmobiliaApp Vista Inicial pos == " + pos.toString());
                        mMap.addMarker(new MarkerOptions().position(pos).title(item.direccion).snippet(item.telefono)).setTag(item.id);
                        boundsBuilder.include(pos);
                    }
                    bounds = boundsBuilder.build();
                    if (cuantosRegistros == 1) {
                        System.out.println("InmobiliaApp Vista Inicial cuantos registros == 1");
                        cameraPosition = CameraPosition.builder()
                                .target(pos)
                                .zoom(15)
                                .bearing(0)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } else {
                        System.out.println("InmobiliaApp Vista Inicial cuantos registros > 1");
                        cameraPosition = CameraPosition.builder()
                                .target(bounds.getCenter())
                                .bearing(0)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                    }
                }
                //---------------------------------------
                System.out.println("InmobiliaApp Vista Inicial salimos del callback de onmap");
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if ((int) marker.getTag() > 0) {
                    System.out.println("InmobiliaApp Vista Inicial click del info window " + marker.getId() + " " + marker.getTag());
                    Intent intent = new Intent(getApplicationContext(), VerDetallePropiedad.class);
                    intent.putExtra("MARKER_TAG", (int) marker.getTag());
                    startActivity(intent);
                }
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                System.out.println("InmobiliaApp Vista Inicial getInfoWindow");
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                System.out.println("InmobiliaApp Vista Inicial getInfoContents marker.getTag == " + marker.getTag());
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
                System.out.println("Inmobilia Vista Inicial detectamos el click en el marcador de mapa: " + marker.getId() + " " + marker.getTag());
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


    }

    @Override
    public void onDestroy() {
        System.out.println("InmobiliaApp Vista Inicial se llamo onDestroy");
        super.onDestroy();
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