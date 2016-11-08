package mx.peta.inmobiliaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import mx.peta.inmobiliaapp.Servicios.LatLong;
import mx.peta.inmobiliaapp.Servicios.ServicioGPS;

import static mx.peta.inmobiliaapp.R.id.btnFin;

public class CapturaCategorias extends AppCompatActivity {

    Propiedad propiedad = Propiedad.getInstance();
    private Spinner spinnerProximidadUrbana;
    private Spinner spinnerTipologia;
    private Spinner spinnerClaseInmueble;
    private Button btnFoto;
    private Button btnFin;
    private ImageView mImageView;
    private TextView textViewLatitud;
    private TextView textViewLonguitud;

    private LatLong latLong = new LatLong();
    ServicioGPS servicioGPS;

    File photoFile = null;
    String bitmapFileName = null;
    String carpetaPropiedades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura_categorias);

        System.out.println("Inmobilia onCreate CapturaCategorias");
        mImageView = (ImageView) findViewById(R.id.imageViewPropiedad);
        mImageView.setClickable(true);
        textViewLatitud = (TextView) findViewById(R.id.textViewLat);
        textViewLonguitud = (TextView) findViewById(R.id.textViewLong);

        btnFin = (Button) findViewById(R.id.btnFin);
        btnFin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "closing the app", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        });

        servicioGPS = new ServicioGPS(getApplicationContext());
        LatLong latLong = servicioGPS.getLatLong();
        textViewLatitud.setText(Double.toString(latLong.getLatitud()));
        textViewLonguitud.setText(Double.toString(latLong.getLongitud()));

        /* se habilitan los sppiners */
        spinnerProximidadUrbana = (Spinner) findViewById(R.id.spinnerProximidadUrbana);

        String[] itemsProximidadUrbana = new String[] { "Céntrica (1)", "Intermedia (2)", "Periférica (3)",
                "De expansión (4)", "Rural (5)" };

        ArrayAdapter<String> adapterProximidadUrbana = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, itemsProximidadUrbana);

        spinnerProximidadUrbana.setAdapter(adapterProximidadUrbana);

        spinnerProximidadUrbana.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                propiedad.setProximidadUrbana((double) (position + 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        spinnerTipologia = (Spinner) findViewById(R.id.spinnerTipologia);

        String[] itemsTipologia = new String[] { "Terreno (1)", "Casa habitación (2)", "Casa en condominio (3)",
                "Departamento en Condominio (4)",  "Casas multiples (5)", "Otros (6)"};

        ArrayAdapter<String> adapterTipologia = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, itemsTipologia);

        spinnerTipologia.setAdapter(adapterTipologia);

        spinnerTipologia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                propiedad.setTipologia((double) (position + 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        spinnerClaseInmueble = (Spinner) findViewById(R.id.spinnerClaseInmueble);

        String[] itemsClaseInmueble = new String[] { "Mínima (1)", "Económica (2)", "Intereés social (3)",
                "Medio (4)", "Semilujo (5)", "Residencial (6)", "Residencial plus (7)"};

        ArrayAdapter<String> adapterClaseInmueble = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, itemsClaseInmueble);

        spinnerClaseInmueble.setAdapter(adapterClaseInmueble);

        spinnerClaseInmueble.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                propiedad.setClaseInmueble((double) (position + 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }
}
