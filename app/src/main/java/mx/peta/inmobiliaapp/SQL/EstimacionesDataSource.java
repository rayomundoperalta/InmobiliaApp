package mx.peta.inmobiliaapp.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rayo on 11/25/16.
 */

public class EstimacionesDataSource {
    private static SQLiteDatabase db;

    public EstimacionesDataSource(Context context) {
        EstimacionesSqLiteHelper helper = new EstimacionesSqLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public int cuantosRegistros(String tableName) {
        String count   = "SELECT count(*) FROM " + tableName;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        // icount contiene el numero de registrops en la tabla
        int icount = mcursor.getInt(0);
        mcursor.close();
        return icount;
    }

    public void deleteRegistro(EstimacionesModelItem modelItem) {
        db.delete(EstimacionesSqLiteHelper.APP_TABLE_NAME,PropiedadesSqLiteHelper.APP_COLUM_ID + " =? ",
                new String[]{String.valueOf(modelItem.id)});
    }

    public void writeRegistro(EstimacionesModelItem estimacionesModelItem) {
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();         // Se crea un conjunto
            values.put(EstimacionesSqLiteHelper.APP_COLUM_ESTIMACIONES, estimacionesModelItem.estimaciones);

            db.insert(EstimacionesSqLiteHelper.APP_TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /*
        Vamos a recuperar el password de un usuario que esta dado de alta en la base de datos
        si no se encuentra al usuario hay que regresar null
        El query debe ser: select password from table where User = user
     */
    final int REGISTRO_ESTIMACIONES = 1;
    public EstimacionesModelItem getRegistro() {
        String QUERY = "select " +
                EstimacionesSqLiteHelper.APP_COLUM_ID + ", " +
                EstimacionesSqLiteHelper.APP_COLUM_ESTIMACIONES +
                " from " + EstimacionesSqLiteHelper.APP_TABLE_NAME + " where " + EstimacionesSqLiteHelper.APP_COLUM_ID +
                " = ?";
        // ejemplo de instrucción db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery(QUERY, new String[] {Integer.toString(REGISTRO_ESTIMACIONES)});
        if (cursor.moveToFirst()) {
            EstimacionesModelItem modelItem = new EstimacionesModelItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow(EstimacionesSqLiteHelper.APP_COLUM_ESTIMACIONES)));
            modelItem.id = cursor.getInt(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_ID));
            cursor.close();
            return modelItem;
        } else {
            EstimacionesModelItem modelItem = new EstimacionesModelItem(0);
            modelItem.id = 0;
            return modelItem;
        }
    }

    public void updateEstimaciones(int estimaciones) {
        String QUERY = "update " + EstimacionesSqLiteHelper.APP_TABLE_NAME + " set " +
                EstimacionesSqLiteHelper.APP_COLUM_ESTIMACIONES + " = " + String.valueOf(estimaciones) +
                " where " + EstimacionesSqLiteHelper.APP_COLUM_ID + " = " + String.valueOf(REGISTRO_ESTIMACIONES);
        System.out.println("Inmobilia " + QUERY);
        db.execSQL(QUERY);
    }
}
