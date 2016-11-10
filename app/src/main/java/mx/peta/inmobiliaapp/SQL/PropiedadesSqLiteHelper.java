package mx.peta.inmobiliaapp.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by rayo on 11/9/16.
 */
public class PropiedadesSqLiteHelper extends SQLiteOpenHelper {
    private final static String APP_DATABASE_NAME            = "inmobiliaapp_db";
    private final static int    APP_DATABASE_VERSION         = 1;
    public static final String  APP_TABLE_NAME               = "propiedades_table";
    public static final String  APP_COLUMN_ID                = BaseColumns._ID;
    public static final String  APP_COLUM_TELEFONO           = "telefono";
    public static final String  APP_COLUM_DIRECCION          = "direccion";
    public static final String  APP_COLUM_LATITUD            = "latitud";
    public static final String  APP_COLUM_LONGITUD           = "longitud";
    public static final String  APP_COLUM_PHOTOFILENAME      = "photo_file_name";
    public static final String  APP_COLUM_TIPOLOGIA          = "tipologia";
    public static final String  APP_COLUM_CP                 = "cp";
    public static final String  APP_COLUM_DELEGACION         = "municipio";
    public static final String  APP_COLUM_ENTIDAD            = "entidad";
    public static final String  APP_COLUM_PROXIMIDADURBANA   = "proximidad_urbana";
    public static final String  APP_COLUM_CLASEINMUEBLE      = "clase_inmueble";
    public static final String  APP_COLUM_VIDAUTIL           = "vida_util";
    public static final String  APP_COLUM_SUPERTERRENO       = "superficie_terreno";
    public static final String  APP_COLUM_SUPERCONSTRUIDO    = "superficie_construida";
    public static final String  APP_COLUM_VALCONST           = "valor_construccion";
    public static final String  APP_COLUM_VALCONCLUIDO       = "valor_concluido";
    public static final String  APP_COLUM_VALESTIMADO        = "valor_estimado";
    public static final String  APP_COLUM_VALDESSTN          = "valor_desstn";
    public static final String  APP_COLUM_REVISADOMANUALMENT = "revisado_manualmente";
    public static final String  APP_COLUM_SENSIBILIDAD       = "sensibilidad";

    private static final String CREATE_PROPIEDADES_TABLE   = "create table " + APP_TABLE_NAME + " (" +
            APP_COLUMN_ID +              " integer primary key autoincrement, " +
            APP_COLUM_TELEFONO +         " text no null, " +
            APP_COLUM_DIRECCION +        " text no null, " +
            APP_COLUM_LATITUD +          " double, " +
            APP_COLUM_LONGITUD +         " double, " +
            APP_COLUM_PHOTOFILENAME +    " text no null, " +
            APP_COLUM_TIPOLOGIA +        " double, " +
            APP_COLUM_CP +               " double, " +
            APP_COLUM_DELEGACION +       " double, " +
            APP_COLUM_ENTIDAD +          " double, " +
            APP_COLUM_PROXIMIDADURBANA + " double, " +
            APP_COLUM_CLASEINMUEBLE +    " double, " +
            APP_COLUM_VIDAUTIL +         " double, " +
            APP_COLUM_SUPERTERRENO +     " double, " +
            APP_COLUM_SUPERCONSTRUIDO +  " double, " +
            APP_COLUM_VALCONST +         " double, " +
            APP_COLUM_VALCONCLUIDO +     " double, " +
            APP_COLUM_VALESTIMADO +      " double, " +
            APP_COLUM_VALDESSTN +        " double, " +
            APP_COLUM_REVISADOMANUALMENT + " double, " +
            APP_COLUM_SENSIBILIDAD +     " int ) ";


    public PropiedadesSqLiteHelper(Context context) {
        super(context, APP_DATABASE_NAME, null, APP_DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROPIEDADES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

