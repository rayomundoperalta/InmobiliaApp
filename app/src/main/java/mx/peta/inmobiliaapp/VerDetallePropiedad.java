package mx.peta.inmobiliaapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mx.peta.inmobiliaapp.SQL.PropiedadesDataSource;
import mx.peta.inmobiliaapp.SQL.PropiedadesModelItem;
import mx.peta.inmobiliaapp.expandablelistview.CatalogoEstadoMunicipio;
import mx.peta.inmobiliaapp.expandablelistview.Municipio;
import mx.peta.inmobiliaapp.validadorWebService.AvaluoValido;
import mx.peta.inmobiliaapp.validadorWebService.AvaluoValidoJsonResult;
import mx.peta.inmobiliaapp.validadorWebService.EndPointsInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static mx.peta.inmobiliaapp.CategoriasPropiedades.itemsClaseInmueble;
import static mx.peta.inmobiliaapp.CategoriasPropiedades.itemsProximidadUrbana;
import static mx.peta.inmobiliaapp.CategoriasPropiedades.itemsTipologia;

public class    VerDetallePropiedad extends AppCompatActivity {

    private PropiedadesModelItem modelItem;
    private Button btnEstima;
    private Button btnRegresa;
    private Button btnBorra;
    private PropiedadesDataSource ds = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_detalle_propiedad);
        int propiedadI = 0;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                System.out.println("Inmobilia estamos pasando mal el id de la propiedad");
            } else {
                propiedadI = extras.getInt("MARKER_TAG");
            }
        } else {
            propiedadI = (int) savedInstanceState.getSerializable("MARKER_TAG");
        }

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView textViewEntidad = (TextView) findViewById(R.id.textViewEntidad);
        TextView textViewMunicipio = (TextView) findViewById(R.id.textViewMunicipio);
        TextView textViewCP = (TextView) findViewById(R.id.textViewCP);
        TextView textViewVidaUtil = (TextView) findViewById(R.id.textViewVidaUtil);
        TextView textViewSuperficieTerreno = (TextView) findViewById(R.id.textViewSuperficieTerreno);
        TextView textViewSuperficieConstruccion = (TextView) findViewById(R.id.textViewSuperficieConstruccion);
        TextView textViewValorConstruccion = (TextView) findViewById(R.id.textViewValorConstruccion);
        TextView textViewPrecio = (TextView) findViewById(R.id.textViewPrecio);
        TextView textViewProximidadUrbana = (TextView) findViewById(R.id.textViewProximidadUrbana);
        TextView textViewTipologia = (TextView) findViewById(R.id.textViewTipologia);
        TextView textViewClaseInmueble = (TextView) findViewById(R.id.textViewClaseInmueble);
        final TextView textViewValorEstimado = (TextView) findViewById(R.id.textViewValorEstimado);
        TextView textViewDireccion = (TextView) findViewById(R.id.textViewDireccion);
        TextView textViewTelefono = (TextView) findViewById(R.id.textViewTelefono);
        TextView textViewLatitud = (TextView) findViewById(R.id.textViewLatitud);
        TextView textViewLongitud = (TextView) findViewById(R.id.textViewLongitud);
        btnRegresa = (Button) findViewById(R.id.btnRegresa);

        ds = new PropiedadesDataSource(getApplicationContext());
        if (propiedadI > 0) {
            modelItem = ds.getRegistro(propiedadI);
        } else {
            System.out.println("Inmobilia error al aceder a la bd sqlite id index == 0");
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(modelItem.photoFileName, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/400, photoH/400);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(modelItem.photoFileName, bmOptions);
        imageView.setImageBitmap(bitmap);

        DecimalFormat formateador = new DecimalFormat("###,###");
        DecimalFormat formatCP = new DecimalFormat("#");

        HashMap<String, List<Municipio>> catalogo = CatalogoEstadoMunicipio.getInstance();
        List<String> expandableListTitle = new ArrayList<String>(catalogo.keySet());
        Collections.sort(expandableListTitle);
        System.out.println("Inmobilia " + expandableListTitle.toString());
        String entidad = expandableListTitle.get(modelItem.groupPosition);
        String municipio = catalogo.get(entidad).get(modelItem.childPosition).getNombreMunicipio();
        textViewDireccion.setText(modelItem.direccion);
        textViewMunicipio.setText(municipio + ", ");
        textViewCP.setText("CP " + formatCP.format(modelItem.CP) + ", ");
        textViewEntidad.setText(entidad);
        textViewVidaUtil.setText("Vida util " + formateador.format(modelItem.vidaUtil) + " meses");
        textViewSuperficieTerreno.setText("Terreno " + formateador.format(modelItem.superTerreno) + " m2");
        textViewSuperficieConstruccion.setText("Construcción " + formateador.format(modelItem.superConstruido) + " m2");
        textViewValorConstruccion.setText("Valor de la construcción $" + formateador.format(modelItem.valConst));
        textViewPrecio.setText("Precio $" + formateador.format(modelItem.valConcluido));
        textViewProximidadUrbana.setText("Proximidad urbana: " + itemsProximidadUrbana[((int) modelItem.proximidadUrbana.doubleValue()) - 1]);
        textViewTipologia.setText("Tipologia: " + itemsTipologia[((int) modelItem.tipologia.doubleValue()) - 1]);
        textViewClaseInmueble.setText("Clase Inmueble: " + itemsClaseInmueble[((int) modelItem.claseInmueble.doubleValue()) - 1]);
        textViewValorEstimado.setText("Valor estimado $" + formateador.format(modelItem.valEstimado) + " +- $" + formateador.format(modelItem.valDesStn));
        textViewTelefono.setText(modelItem.telefono);
        textViewLatitud.setText("Lat: " + Double.toString(modelItem.latitud));
        textViewLongitud.setText("    Lon: " + Double.toString(modelItem.longitud));

        btnEstima = (Button) findViewById(R.id.btnEstima);
        btnEstima.setVisibility(View.INVISIBLE);

        if (modelItem.valEstimado == 0.0 ) {
            btnEstima.setVisibility(View.VISIBLE);
            btnEstima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Ya se ha terminado la captura de la información
                    // es necesario llamar al web service para conocer el valor del inmueble
                    final String BASE_URL = "http://valjson.artica.com.mx/";
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
                    modelItem.sensibilidad = 4.0;
                    Call<AvaluoValido> callApiService = apiService.getAvaluo(
                            modelItem.tipologia,
                            modelItem.CP,
                            modelItem.delegacion + modelItem.entidad * 1000.0,
                            modelItem.entidad,
                            modelItem.proximidadUrbana,
                            modelItem.claseInmueble,
                            modelItem.vidaUtil,
                            modelItem.superTerreno,
                            modelItem.superConstruido,
                            modelItem.valConst,
                            modelItem.valConcluido,
                            modelItem.revisadoManualment,
                            "rayo",
                            "rayo",
                            modelItem.sensibilidad);
                    callApiService.enqueue(new Callback<AvaluoValido>() {
                        @Override
                        public void onResponse(Call<AvaluoValido> call, Response<AvaluoValido> response) {
                            if (response == null) {
                                Toast.makeText(getApplicationContext(), "Volver a intentar mas tarde calcular el valor estimado", Toast.LENGTH_LONG).show();
                            } else {
                                btnEstima.setVisibility(View.INVISIBLE);
                                AvaluoValidoJsonResult res = response.body().getAvaluoValidoJsonResult();
                                modelItem.valEstimado = res.getValorEstimado();
                                modelItem.valDesStn = res.getDesStn();
                                DecimalFormat formateador = new DecimalFormat("###,###");
                                textViewValorEstimado.setText("Valor estimado $" + formateador.format(modelItem.valEstimado) + " +- $" + formateador.format(modelItem.valDesStn));
                                // Es necesario actualizar la base de datos para que no sea necesario recalcular el valor estimado
                                ds.updateApp(modelItem);
                            }
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
                }
            });
        }

        btnBorra = (Button) findViewById(R.id.btnBorra);
        btnBorra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ds.deleteRegistro(modelItem);
                finish();
            }
        });
        btnRegresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
