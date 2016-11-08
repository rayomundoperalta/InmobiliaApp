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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
    Intent takePictureIntent;

    @Override
    protected void onResume() {
        super.onResume();

    }

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

        /* Código que se necesita para tomar la foto */

        carpetaPropiedades = verificarCrearCarpeta("propiedades");
        mImageView = (ImageView) findViewById(R.id.imageViewPropiedad);
        mImageView.setClickable(true);

        if (propiedad.getTakingPhotoState()) {
            setPic(propiedad.getPhotoFileName(), mImageView);
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
