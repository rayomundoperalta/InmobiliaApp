package mx.peta.inmobiliaapp.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by rayo on 11/25/16.
 */

public class EstimacionesCompradasSqLiteHelper extends SQLiteOpenHelper {
    private final static String APP_DATABASE_NAME            = "estimaciones_compradas";
    private final static int    APP_DATABASE_VERSION         = 1;

    public static final String  APP_TABLE_NAME               = "compra_paquetes_table";
    public static final String  APP_COLUM_ID                 = BaseColumns._ID;
    public static final String  APP_COLUM_FECHA_COMPRA       = "fecha_compra";
    public static final String  APP_COLUM_PAY_KEY            = "pay_key";

    private static final String CREATE_ESTIMACIONES_COMPRA_TABLE   = "create table " + APP_TABLE_NAME + " (" +
            APP_COLUM_ID +          " integer primary key autoincrement, " +
            APP_COLUM_FECHA_COMPRA +       " text no null, " +
            APP_COLUM_PAY_KEY +            " text no null ) ";

    public EstimacionesCompradasSqLiteHelper(Context context) {
        super(context, APP_DATABASE_NAME, null, APP_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ESTIMACIONES_COMPRA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
            Aqui se programa lo necesario para hacer un upgrate en la base de datos
         */

    }
}
