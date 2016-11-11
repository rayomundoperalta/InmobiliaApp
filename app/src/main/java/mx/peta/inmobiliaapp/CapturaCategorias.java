package mx.peta.inmobiliaapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import mx.peta.inmobiliaapp.SQL.PropiedadesDataSource;
import mx.peta.inmobiliaapp.SQL.PropiedadesModelItem;
import mx.peta.inmobiliaapp.Servicios.LatLong;
import mx.peta.inmobiliaapp.Servicios.ServicioGPS;
import mx.peta.inmobiliaapp.validadorWebService.AvaluoValido;
import mx.peta.inmobiliaapp.validadorWebService.AvaluoValidoJsonResult;
import mx.peta.inmobiliaapp.validadorWebService.EndPointsInterface;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CapturaCategorias extends AppCompatActivity {

    Propiedad propiedad = Propiedad.getInstance();
    private Spinner spinnerProximidadUrbana;
    private Spinner spinnerTipologia;
    private Spinner spinnerClaseInmueble;
    private Button btnEstimarValor;
    private Button btnGuardar;
    private ImageView mImageView;
    private TextView textViewLatitud;
    private TextView textViewLonguitud;
    private EditText editTextDireccion;
    private EditText editTextTelefono;

    private LatLong latLong = new LatLong();

    File photoFile = null;
    String bitmapFileName = null;
    String carpetaPropiedades;
    Intent takePictureIntent;

    @Override
    protected void onResume() {
        super.onResume();
        if (propiedad.getValEstimado() > 0.0) {
            DecimalFormat formateador = new DecimalFormat("###,###");
            TextView textViewEstimacionValor = (TextView) findViewById(R.id.textViewEstimacionValor);
            textViewEstimacionValor.setText("Valor estimado $" + formateador.format(propiedad.getValEstimado()) + " +- $" +
                    formateador.format(propiedad.getValDesStn()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura_categorias);

        propiedad.setValEstimado(0.0);
        propiedad.setValDesStn(0.0);
        System.out.println("Inmobilia onCreate CapturaCategorias");
        mImageView = (ImageView) findViewById(R.id.imageViewPropiedad);
        mImageView.setClickable(true);
        textViewLatitud = (TextView) findViewById(R.id.textViewLat);
        textViewLonguitud = (TextView) findViewById(R.id.textViewLong);

        editTextDireccion = (EditText) findViewById(R.id.editTextDireccion);

        editTextTelefono  = (EditText) findViewById(R.id.editTextTelefono);

        btnEstimarValor = (Button) findViewById(R.id.btnEstimarValor);
        btnEstimarValor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // Ya se ha terminado la captura de la información
            // es necesario llamar al web service para conocer el valor del inmueble
            final String BASE_URL = "http://valjson.artica.com.mx/";
                if (propiedad.getValEstimado() == 0.0) {
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                            .create();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                    EndPointsInterface apiService = retrofit.create(EndPointsInterface.class);
                    //tipologia=2
                    //&CP=4318&delegacion=9003&entidad=9&proximidadUrbana=1&claseInmueble=5&vidautil=720&superTerreno=400&superConstruido=400
                    // &valConst=4000000&valConcluido=8000000&revisadoManualmente=0&USER=rayo&PASSWORD=rayo&sensibilidad=3.5
                    Call<AvaluoValido> callApiService = apiService.getAvaluo(
                            propiedad.getTipologia(),
                            propiedad.getCP(),
                            propiedad.getDelegacion() + propiedad.getEntidad() * 1000.0,
                            propiedad.getEntidad(),
                            propiedad.getProximidadUrbana(),
                            propiedad.getClaseInmueble(),
                            propiedad.getVidaUtil(),
                            propiedad.getSuperTerreno(),
                            propiedad.getSuperConstruido(),
                            propiedad.getValConst(),
                            propiedad.getValConcluido(),
                            propiedad.getRevisadoManualment(),
                            "rayo",
                            "rayo",
                            3.5);
                    callApiService.enqueue(new Callback<AvaluoValido>() {
                        @Override
                        public void onResponse(Call<AvaluoValido> call, Response<AvaluoValido> response) {
                            AvaluoValidoJsonResult res = response.body().getAvaluoValidoJsonResult();
                            propiedad.setValEstimado(res.getValorEstimado());
                            propiedad.setValDesStn(res.getDesStn());
                            DecimalFormat formateador = new DecimalFormat("###,###");
                            TextView textViewEstimacionValor = (TextView) findViewById(R.id.textViewEstimacionValor);
                            textViewEstimacionValor.setText("Valor estimado $" + formateador.format(res.getValorEstimado()) + " +- $" +
                                    formateador.format(res.getDesStn()));
                            //Toast.makeText(getApplicationContext(),"Avaluo válido " + res.getAvaluoValido().toString() + "\n " +
                            //        "Valor estimado " + res.getValorEstimado().toString() + "\n " +
                            //        "DS " + res.getDesStn().toString() + "\n " +
                            //        res.getTipoModelo() + "\n " +
                            //        res.getErrorStatus(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<AvaluoValido> call, Throwable t) {

                        }
                    });
                } else {
                    DecimalFormat formateador = new DecimalFormat("###,###");
                    TextView textViewEstimacionValor = (TextView) findViewById(R.id.textViewEstimacionValor);
                    textViewEstimacionValor.setText("Valor estimado $" + formateador.format(propiedad.getValEstimado()) + " +- $" +
                            formateador.format(propiedad.getValDesStn()));
                }

            }
        });

        btnGuardar = (Button) findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PropiedadesDataSource ds = new PropiedadesDataSource(getApplicationContext());
                propiedad.setDireccion(editTextDireccion.getText().toString());
                propiedad.setTelefono(editTextTelefono.getText().toString());
                // Ya se capturo la información y hay que almacenarla en la base de datos
                // los datos que hay que almacenar estan en la singleton propiedad
                // al final hay que brincar a la actividad con el mapa
                PropiedadesModelItem modelItem = new PropiedadesModelItem(
                        propiedad.getTelefono(),
                        propiedad.getDireccion(),
                        propiedad.getLatitud(),
                        propiedad.getLongitud(),
                        propiedad.getPhotoFileName(),
                        propiedad.getTipologia(),
                        propiedad.getCP(),
                        propiedad.getDelegacion(),
                        propiedad.getEntidad(),
                        propiedad.getProximidadUrbana(),
                        propiedad.getClaseInmueble(),
                        propiedad.getVidaUtil(),
                        propiedad.getSuperTerreno(),
                        propiedad.getSuperConstruido(),
                        propiedad.getValConst(),
                        propiedad.getValConcluido(),
                        propiedad.getValEstimado(),
                        propiedad.getValDesStn(),
                        propiedad.getRevisadoManualment(),
                        propiedad.getSensibilidad());
                ds.writeRegistro(modelItem);
                finish();
            }
        });

        /* Código que se necesita para tomar la foto */

        carpetaPropiedades = verificarCrearCarpeta("propiedades");
        mImageView = (ImageView) findViewById(R.id.imageViewPropiedad);
        mImageView.setClickable(true);

        if (propiedad.getTakingPhotoState()) {
            setPic(propiedad.getPhotoFileName(), mImageView);
            textViewLatitud.setText(Double.toString(propiedad.getLatitud()));
            textViewLonguitud.setText(Double.toString(propiedad.getLongitud()));
        }

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Image Touch", Toast.LENGTH_LONG).show();
                takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Se verifica si hay una aplicacion que pueda tomar la foto
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    propiedad.setTakingPhotoState(true);
                    System.out.println("Inmobilia Geting photo");
                    startActivityForResult(takePictureIntent, 100);
                }
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Inmobilia onActivity result");
        if (requestCode == 100 && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Hay que salvar el bitmap a disco
            File savedBitmap = createImageFile(carpetaPropiedades);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(savedBitmap);
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                System.out.println("Inmobilia error while writing a image to disk");
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ServicioGPS gps = ServicioGPS.getInstancia();
            LatLong latLong = gps.getLatLong();
            propiedad.setLatitud(latLong.getLatitud());
            propiedad.setLongitud(latLong.getLongitud());
            propiedad.setPhotoFileName(savedBitmap.toString());

            // Hay que almacenar el nombre del archivo en SQLite junto con los datos del inmueble
        }
    }

    private File createImageFile(String filePath) {  // Crea el path unico para almacenar una imagen
        // Create an image file name
        // the image will be stored in the DCIM directory
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getDefault());
        long dateTaken = calendar.getTimeInMillis();

        File dir = new File(filePath + "/");
        final File photoFile = new File(dir, DateFormat.format("yyyyMMdd_kkmmss", dateTaken).toString() + ".jpg");

        return photoFile;
    }

    private String verificarCrearCarpeta(String dirname) {
        String dir = "unknown";
        PackageManager m = getPackageManager();
        String packageName = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(packageName, 0);
            dir = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println(packageName + " Error Package name not found ");
            return null;
        }

        System.out.println("Inmobilia app directory " + dir);
        dir = dir + "/" + dirname;
        System.out.println("Inmobilia property directory " + dir);
        File f = new File(dir);

        // Comprobamos si la carpeta está ya creada
        // Si la carpeta no está creada, la creamos.
        if(!f.isDirectory()) {
            System.out.println("Inmobilia creating directory");
            File myNewFolder = new File(dir);
            myNewFolder.mkdir(); //creamos la carpeta
        }
        if (f.isDirectory()) {
            System.out.println("Inmobilia directory exist");
        }
        return dir;
    }

    private void setPic(String mCurrentPhotoFilePath, ImageView mImageView) {
        // Get the dimensions of the View
        Display display = getWindowManager().getDefaultDisplay();  // Recuperamos las características del display
        Point size = new Point();;
        display.getSize(size); // size contiene el tamaño del display en pixels
        int imageSize = (int) (Math.min(size.x,size.y) / 3); // el tamaño de la imagen sera una tercera parte del lado mas corto

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoFilePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/imageSize, photoH/imageSize);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoFilePath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }
}
