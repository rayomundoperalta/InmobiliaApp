package mx.peta.inmobiliaapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mx.peta.inmobiliaapp.SQL.EstimacionesCompradasDataSource;
import mx.peta.inmobiliaapp.SQL.EstimacionesCompradasModelItem;
import mx.peta.inmobiliaapp.SQL.EstimacionesDataSource;
import mx.peta.inmobiliaapp.SQL.EstimacionesModelItem;
import mx.peta.inmobiliaapp.SQL.EstimacionesSqLiteHelper;
import mx.peta.inmobiliaapp.SQL.PropiedadesDataSource;
import mx.peta.inmobiliaapp.SQL.PropiedadesModelItem;
import mx.peta.inmobiliaapp.expandablelistview.CatalogoEstadoMunicipio;
import mx.peta.inmobiliaapp.expandablelistview.Municipio;
import mx.peta.inmobiliaapp.login.FacebookLoginActivity;
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

public class VerDetallePropiedad extends AppCompatActivity {

    private PropiedadesModelItem modelItem;
    private ImageButton btnEstima;
    private ImageButton btnRegresa;
    private ImageButton btnBorra;
    private ImageButton btnComparte;
    private PropiedadesDataSource ds = null;
    EstimacionesDataSource dsEstimadores = null;
    private Bitmap bitmap;

    private final String PENDING_ACTION_BUNDLE_KEY =
            "mx.peta.inmobiliaapp:PendingAction";
    private PendingAction pendingAction = PendingAction.NONE;

    final int RESULT_OF_PAYMENT = 1960;

    private static final String PERMISSION = "publish_actions";
    private boolean canPresentShareDialog;
    private boolean canPresentShareDialogWithPhotos;
    private ShareDialog shareDialog;
    private ProfileTracker profileTracker;

    private CallbackManager callbackManager;
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            showResult("Facebook callback", "onCancel");
        }

        @Override
        public void onError(FacebookException error) {
            String title = "Facebook exception";
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            if (result.getPostId() != null) {
                String title = "lo logró";
                String id = result.getPostId();
                String alertMessage = "Inmobilia share facebook successfully_posted_post " + id;
                showResult(title, alertMessage);
            }
        }

        private void showResult(String title, String alertMessage) {
            System.out.println("InmobiliaApp Ver detalle propiedad " + title + " " + alertMessage);
            /**
            new AlertDialog.Builder(VerDetallePropiedad.this)
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton("OK", null)
                    .show();
            /**/
        }
    };

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE,
        POST_LINK
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_detalle_propiedad);
        int propiedadI = 0;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                System.out.println("InmobiliaApp Ver detalle propiedad estamos pasando mal el id de la propiedad");
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
        btnRegresa = (ImageButton) findViewById(R.id.btnRegresa);

        ds = new PropiedadesDataSource(getApplicationContext());
        if (propiedadI > 0) {
            modelItem = ds.getRegistro(propiedadI);
        } else {
            System.out.println("InmobiliaApp Ver detalle propiedad error al aceder a la bd sqlite id index == 0");
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

        bitmap = BitmapFactory.decodeFile(modelItem.photoFileName, bmOptions);
        imageView.setImageBitmap(bitmap);

        DecimalFormat formateador = new DecimalFormat("###,###");
        DecimalFormat formatCP = new DecimalFormat("#");

        HashMap<String, List<Municipio>> catalogo = CatalogoEstadoMunicipio.getInstance();
        List<String> expandableListTitle = new ArrayList<String>(catalogo.keySet());
        Collections.sort(expandableListTitle);
        System.out.println("InmobiliaApp Ver detalle propiedad " + expandableListTitle.toString());
        String entidad = expandableListTitle.get(modelItem.groupPosition);
        String municipio = catalogo.get(entidad).get(modelItem.childPosition).getNombreMunicipio();
        textViewDireccion.setText(modelItem.direccion);
        textViewMunicipio.setText(municipio + ", ");
        textViewCP.setText("CP " + formatCP.format(modelItem.CP) + ", ");
        textViewEntidad.setText(entidad);
        textViewVidaUtil.setText(              "Vida util       " + formateador.format(modelItem.vidaUtil) + " meses");
        textViewSuperficieTerreno.setText(     "Terreno         " + formateador.format(modelItem.superTerreno) + " m2");
        textViewSuperficieConstruccion.setText("Construcción  " + formateador.format(modelItem.superConstruido) + " m2");
        textViewValorConstruccion.setText(     "Valor de la construcción   $" + formateador.format(modelItem.valConst));
        textViewValorEstimado.setText(         "Valor estimado $" + formateador.format(modelItem.valEstimado) + " +- " + formateador.format(ValorEstimado.porcentaje(modelItem.valEstimado, modelItem.valDesStn)) + "%");
        textViewPrecio.setText("Precio   $" + formateador.format(modelItem.valConcluido));
        textViewProximidadUrbana.setText("Proximidad urbana:  " + itemsProximidadUrbana[((int) modelItem.proximidadUrbana.doubleValue()) - 1]);
        textViewTipologia.setText(       "Tipologia:          " + itemsTipologia[((int) modelItem.tipologia.doubleValue()) - 1]);
        textViewClaseInmueble.setText(   "Clase Inmueble:     " + itemsClaseInmueble[((int) modelItem.claseInmueble.doubleValue()) - 1]);
        textViewTelefono.setText(modelItem.telefono);
        textViewLatitud.setText("Lat: " + Double.toString(modelItem.latitud));
        textViewLongitud.setText("    Lon: " + Double.toString(modelItem.longitud));

        btnEstima = (ImageButton) findViewById(R.id.btnEstima);
        btnEstima.setVisibility(View.INVISIBLE);

        if (modelItem.valEstimado == 0.0 ) {
            btnEstima.setVisibility(View.VISIBLE);
            btnEstima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Ya se ha terminado la captura de la información
                    // es necesario llamar al web service para conocer el valor del inmueble
                    // Lo cual solo se hace si tiene estimaciones compradas disponibles
                    dsEstimadores = new EstimacionesDataSource(getApplicationContext());
                    final int cuantasEstimaciones = dsEstimadores.getRegistro().estimaciones;
                    if ( cuantasEstimaciones > 0) {
                        //final String BASE_URL = "http://valjson.artica.com.mx/";
                        final String BASE_URL = "http://peta.mx/";
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
                                    AvaluoValidoJsonResult res = response.body().getAvaluoValidoJsonResult();
                                    modelItem.valEstimado = res.getValorEstimado();
                                    if (modelItem.valEstimado > 10) {
                                        btnEstima.setVisibility(View.INVISIBLE);
                                        dsEstimadores.updateEstimaciones(cuantasEstimaciones - 1);
                                    } else {
                                        new AlertDialog.Builder(VerDetallePropiedad.this, R.style.EstiloAlertas)
                                                .setTitle("Calculo de estimaciones")
                                                .setMessage("No tenemos información en esta zona.")
                                                .setPositiveButton("ok", null)
                                                .show();
                                    }
                                    dsEstimadores.close();
                                    modelItem.valDesStn = res.getDesStn();
                                    DecimalFormat formateador = new DecimalFormat("###,###");
                                    textViewValorEstimado.setText("Valor estimado $" + formateador.format(modelItem.valEstimado) + " +- " + formateador.format(ValorEstimado.porcentaje(modelItem.valEstimado, modelItem.valDesStn)) + "%");
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
                                Toast.makeText(getApplicationContext(), "Volver a intentar mas tarde calcular el valor estimado", Toast.LENGTH_LONG).show();
                                dsEstimadores.close();
                            }
                        });
                    } else {
                        dsEstimadores.close();
                        new AlertDialog.Builder(VerDetallePropiedad.this, R.style.EstiloAlertas)
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

                }
            });
        }

        btnBorra = (ImageButton) findViewById(R.id.btnBorra);
        btnBorra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fileToBedeleted = new File(modelItem.photoFileName);
                fileToBedeleted.delete();
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

        /*
            ---------------------------------------------------------------------------------------
            Aqui empieza el codigo para compartir información en Facebook
         */

        // FacebookSdk.sdkInitialize(this.getApplicationContext(), REQUESTCODE_FACEBOOK_OFFSET);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        System.out.println("InmobilaApp Ver detalle propiedad Login manager success");
                        /**
                        new AlertDialog.Builder(VerDetallePropiedad.this)
                                .setTitle("Login manager")
                                .setMessage("onSuccess")
                                .setPositiveButton("OK", null)
                                .show();
                         /**/
                        handlePendingAction();
                    }

                    @Override
                    public void onCancel() {
                        if (pendingAction != PendingAction.NONE) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (pendingAction != PendingAction.NONE
                                && exception instanceof FacebookAuthorizationException) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                    }

                    private void showAlert() {
                        System.out.println("InmobiliaApp Ver detalle propiedad " + "petición cancelada");
                        /**
                        new AlertDialog.Builder(VerDetallePropiedad.this)
                                .setTitle("Cancelada")
                                .setMessage("permission_not_granted")
                                .setPositiveButton("ok", null)
                                .show();
                         /**/
                    }
                });

        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                // It's possible that we were waiting for Profile to be populated in order to
                // post a status update.
                handlePendingAction();
            }
        };

        // Can we present the share dialog for regular links?
        canPresentShareDialog = ShareDialog.canShow(
                ShareLinkContent.class);

        // Can we present the share dialog for photos?
        canPresentShareDialogWithPhotos = ShareDialog.canShow(
                SharePhotoContent.class);

        btnComparte = (ImageButton) findViewById(R.id.btnComparte);
        btnComparte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Publicamos el status
                //performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);

                // Publicamos la foto
                //performPublish(PendingAction.POST_PHOTO, canPresentShareDialogWithPhotos);
                //finish();

                // Publicamos link
                performPublish(PendingAction.POST_LINK, canPresentShareDialog);
            }
        });
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_PHOTO:
                postPhoto();
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
            case POST_LINK:
                postLink();
                break;
        }
    }

    private void postPhoto() {
        //Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_email_black);
        //Uri myUri = Uri.parse("http://www.google.com");
        SharePhoto sharePhoto = new SharePhoto
                .Builder()
                .setBitmap(bitmap)
                .setCaption("Esta podria ser mi nueva casa")
                //.setImageUrl(Uri.parse("http://peta.mx/images/favicon/apple-icon-180x180.png"))
                .setUserGenerated(true)
                .build();
        ArrayList<SharePhoto> photos = new ArrayList<>();
        photos.add(sharePhoto);

        SharePhotoContent sharePhotoContent =
                new SharePhotoContent.Builder().addPhoto(sharePhoto).build();

                        //.setPhotos(photos).build();

        if (canPresentShareDialogWithPhotos) {
            shareDialog.show(sharePhotoContent);
        } else if (hasPublishPermission()) {
            ShareApi.share(sharePhotoContent, shareCallback);
        } else {
            pendingAction = PendingAction.POST_PHOTO;
            // We need to get new permissions, then complete the action when we get called back.

            LoginManager.getInstance().logInWithPublishPermissions(
                    this,
                    Arrays.asList(PERMISSION));
        }
    }

    private void postLink() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("(yyyy-MM-dd HH:mm:ss) ");
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://avaluos.peta.mx"))
                .setContentTitle("Está usando InmobiliaApp para lograr un buen trato para su nueva casa")
                .setContentDescription(df.format(c.getTime()) + " esta localización le gusta y está a buen precio " + "http://maps.google.com/maps?q=" + Double.toString(modelItem.latitud) + "," + Double.toString(modelItem.longitud))
                .setImageUrl(Uri.parse("http://peta.mx/images/favicon/apple-icon-180x180.png"))
                .build();

        if (canPresentShareDialogWithPhotos) {
            shareDialog.show(content);
        } else if (hasPublishPermission()) {
            ShareApi.share(content, shareCallback);
        } else {
            pendingAction = PendingAction.POST_PHOTO;
            // We need to get new permissions, then complete the action when we get called back.

            LoginManager.getInstance().logInWithPublishPermissions(
                    this,
                    Arrays.asList(PERMISSION));
        }
    }

    private void postStatusUpdate() {
        Profile profile = Profile.getCurrentProfile();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("InmobiliaApp")
                .setContentDescription(
                        "La nueva forma de buscar casa")
                .setContentUrl(Uri.parse("http://avaluos.peta.mx"))
                .build();
        if (canPresentShareDialog) {
            shareDialog.show(linkContent);
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    private void performPublish(PendingAction action, boolean allowNoToken) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null || allowNoToken) {
            pendingAction = action;
            handlePendingAction();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            System.out.println("InmobiliaApp Ver detalles propiedad resultado de Facebook");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        else {
            System.out.println("InmobiliaApp Vista Inicial onActivityResult vista inicial");
            if (requestCode == RESULT_OF_PAYMENT) {
                System.out.println("InmobiliaApp Ver detalles propiedad RESULT Of Payment recibido");
                if (resultCode == RESULT_OK) {
                    System.out.println("InmobiliaApp Payment result ok");
                    switch (data.getIntExtra(PaypalConstants.PAYPAL_RESULT, PaypalConstants.DEFAULT_VALUE)) {
                        case PaypalConstants.PAYPAL_SUCCEED:
                            String payPalPayKey = data.getStringExtra(PaypalConstants.PAY_KEY);
                            System.out.println("InmobiliaApp Ver detalles propiedad Pay Key: " + payPalPayKey);

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
                                    System.out.println("InmobiliaApp Ver detalles propiedad Se cargaron 3 estimaciones");
                                    break;
                                case 1: // siempre debe existir solo un registro
                                    estimacionesModelItem = estimacionesDS.getRegistro();
                                    estimacionesModelItem.estimaciones += 3;
                                    estimacionesDS.updateEstimaciones(estimacionesModelItem.estimaciones);
                                    estimacionesDS.close();
                                    System.out.println("InmobiliaApp Ver detalles propiedad Se adicionaron 3 estimaciones, total = " + estimacionesModelItem.estimaciones);
                                    break;
                                default: // Existe un error en la lógica
                                    Toast.makeText(getApplicationContext(), "Favor de reinstalar la aplicación", Toast.LENGTH_LONG).show();
                                    System.out.println("InmobiliaApp Vista Inicial --> E R R O R   en el manejo de la tabla de estimaciones <--");
                            }
                            EstimacionesModelItem estimacionesModelItem = new EstimacionesModelItem(0);
                            break;
                        case PaypalConstants.PAYPAL_CANCELED:
                            System.out.println("InmobiliaApp Ver detalles propiedad Pago Cancelado");
                            break;
                        case PaypalConstants.PAYPAL_ERROR:
                            String payPalErrorMsg = data.getStringExtra(PaypalConstants.PAYPAL_ERROR_MSG);
                            String payPalErrorID  = data.getStringExtra(PaypalConstants.PAYPAL_ERROR_ID);
                            Toast.makeText(getApplicationContext(),"Error en el pago " + payPalErrorMsg + " " + payPalErrorID, Toast.LENGTH_LONG).show();
                            System.out.println("InmobiliaApp Ver detalles propiedad Error en el pago " + payPalErrorMsg);
                            break;
                        case PaypalConstants.DEFAULT_VALUE:
                            System.out.println("InmobiliaApp Ver detalles propiedad Tenemos problemas con la API de PayPal");
                            break;
                    }
                } else {
                    System.out.println("InmobiliaApp Ver detalles propiedad Error en el resultcode");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
        ds.close();
    }
}
