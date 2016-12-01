package mx.peta.inmobiliaapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import mx.peta.inmobiliaapp.SQL.EstimacionesCompradasDataSource;
import mx.peta.inmobiliaapp.SQL.EstimacionesCompradasModelItem;
import mx.peta.inmobiliaapp.SQL.EstimacionesDataSource;
import mx.peta.inmobiliaapp.SQL.EstimacionesModelItem;
import mx.peta.inmobiliaapp.SQL.EstimacionesSqLiteHelper;
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

import static mx.peta.inmobiliaapp.CategoriasPropiedades.itemsClaseInmueble;
import static mx.peta.inmobiliaapp.CategoriasPropiedades.itemsProximidadUrbana;
import static mx.peta.inmobiliaapp.CategoriasPropiedades.itemsTipologia;

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

    private File photoFile = null;
    private String bitmapFileName = null;
    private String carpetaPropiedades;
    private Intent takePictureIntent = null;

    private EstimacionesDataSource dsEstimadores = null;
    private boolean hayInformacionNueva = false;
    boolean perderInformación = false;

    final int RESULT_OF_PAYMENT = 1960;
    final int RESULT_OF_PHOTO = 100;

    @Override
    protected void onResume() {
        super.onResume();
        if (propiedad.getValEstimado() > 0.0) {
            DecimalFormat formateador = new DecimalFormat("###,###");
            TextView textViewEstimacionValor = (TextView) findViewById(R.id.textViewEstimacionValor);
            textViewEstimacionValor.setText("Valor estimado $" + formateador.format(propiedad.getValEstimado()) + " +- " +
                    formateador.format(ValorEstimado.porcentaje(propiedad.getValEstimado(), propiedad.getValDesStn())) + "%");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura_categorias);

        propiedad.setValEstimado(0.0);
        propiedad.setValDesStn(0.0);
        System.out.println("Inmobilia CapturaCategorias onCreate");
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
                dsEstimadores = new EstimacionesDataSource(getApplicationContext());
                final int cuantasEstimaciones = dsEstimadores.getRegistro().estimaciones;
                final String BASE_URL = "http://valjson.artica.com.mx/";
                if (propiedad.getValEstimado() > 0.0) {
                    dsEstimadores.close();
                }
                else {
                    if (propiedad.getValEstimado() == 0.0 && cuantasEstimaciones > 0) {
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
                        propiedad.setSensibilidad(4.0);
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
                                propiedad.getSensibilidad());
                        callApiService.enqueue(new Callback<AvaluoValido>() {
                            @Override
                            public void onResponse(Call<AvaluoValido> call, Response<AvaluoValido> response) {
                                if (response == null) {
                                    Toast.makeText(getApplicationContext(), "Volver a intentar mas tarde calcular el valor estimado", Toast.LENGTH_LONG).show();
                                } else {
                                    AvaluoValidoJsonResult res = response.body().getAvaluoValidoJsonResult();
                                    propiedad.setValEstimado(res.getValorEstimado());
                                    propiedad.setValDesStn(res.getDesStn());
                                    if (propiedad.getValEstimado() > 10.0) {
                                        hayInformacionNueva = true;
                                        dsEstimadores.updateEstimaciones(cuantasEstimaciones - 1);
                                        btnEstimarValor.setVisibility(View.INVISIBLE);
                                    } else {
                                        new android.app.AlertDialog.Builder(CapturaCategorias.this, R.style.EstiloAlertas)
                                                .setTitle("Calculo de estimaciones")
                                                .setMessage("No tenemos información en esta zona.")
                                                .setPositiveButton("ok", null)
                                                .show();
                                    }
                                    DecimalFormat formateador = new DecimalFormat("###,###");
                                    TextView textViewEstimacionValor = (TextView) findViewById(R.id.textViewEstimacionValor);
                                    textViewEstimacionValor.setText("Valor estimado $" + formateador.format(res.getValorEstimado()) + " +- " +
                                            formateador.format(ValorEstimado.porcentaje(res.getValorEstimado(), res.getDesStn())) + "%");
                                }
                                dsEstimadores.close();
                            }

                            @Override
                            public void onFailure(Call<AvaluoValido> call, Throwable t) {
                                dsEstimadores.close();
                                Toast.makeText(getApplicationContext(), "Volver a intentar mas tarde calcular el valor estimado", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        dsEstimadores.close();
                        if (cuantasEstimaciones == 0) {
                            new android.app.AlertDialog.Builder(CapturaCategorias.this, R.style.EstiloAlertas)
                                    .setTitle("Compra de Estimaciones")
                                    .setMessage("No tiene estimaciones disponibles, ¿desea comprar?")
                                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        /* Se llama a pay pal para que cliente haga el pago por las estimaciones */
                                            Intent intentb = new Intent(getApplicationContext(), InmobiliaPayPalPayment.class);
                                            startActivityForResult(intentb, RESULT_OF_PAYMENT);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        /* No hay que hacer nada */
                                        }
                                    })
                                    .show();
                        }
                        DecimalFormat formateador = new DecimalFormat("###,###");
                        TextView textViewEstimacionValor = (TextView) findViewById(R.id.textViewEstimacionValor);
                        textViewEstimacionValor.setText("Valor estimado $" + formateador.format(propiedad.getValEstimado()) + " +- " +
                                formateador.format(ValorEstimado.porcentaje(propiedad.getValEstimado(), propiedad.getValDesStn())) + "%");
                    }
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
                        propiedad.getSensibilidad(),
                        propiedad.getGroupPosition(),
                        propiedad.getChildPosition());
                ds.writeRegistro(modelItem);
                ds.close();
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
                    System.out.println("InmobiliaApp Geting photo");
                    startActivityForResult(takePictureIntent, RESULT_OF_PHOTO);
                }
            }
        });

        /* se habilitan los sppiners */

        spinnerProximidadUrbana = (Spinner) findViewById(R.id.spinnerProximidadUrbana);

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
        propiedad.setSensibilidad(4.0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            System.out.println("InmobiliaApp Captura Categorias " + this.getClass().getName() + " - back button pressed <---------------------");
            if (hayInformacionNueva) {
                new android.app.AlertDialog.Builder(CapturaCategorias.this, R.style.EstiloAlertas)
                        .setTitle("Se calculó la estimacion de valor de esta propiedad.")
                        .setMessage("¿Desea perderla?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                perderInformación = true;
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                perderInformación = true;
                            }
                        })
                        .show();
                if (perderInformación) {
                    return super.onKeyDown(keyCode, event);
                } else {
                    return(false);
                }
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("InmobiliaApp Captura Categorias onActivity result");

        if (requestCode == RESULT_OF_PAYMENT) {
            System.out.println("InmobiliaApp Captura Categorias RESULT Of Payment recibido");
            if (resultCode == RESULT_OK) {
                System.out.println("InmobiliaApp Captura Categorias Payment result ok");
                switch (data.getIntExtra(PaypalConstants.PAYPAL_RESULT, PaypalConstants.DEFAULT_VALUE)) {
                    case PaypalConstants.PAYPAL_SUCCEED:
                        String payPalPayKey = data.getStringExtra(PaypalConstants.PAY_KEY);
                        System.out.println("InmobiliaApp Captura Categorias vista inicial Pay Key: " + payPalPayKey);

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
        } else

    //---------------------------------------
        if (requestCode == RESULT_OF_PHOTO && resultCode == RESULT_OK) {

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
            if (latLong == null || (latLong.getLatitud() == 0.0 && latLong.getLongitud() == 0.0)) {
                System.out.println("InmobiliaApp Captura Categorias no hemos podido recuperar la posición con el GPS");
                Toast.makeText(getApplicationContext(),"La pocisión obtenida del GPS no es correcta", Toast.LENGTH_LONG).show();
            }
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
