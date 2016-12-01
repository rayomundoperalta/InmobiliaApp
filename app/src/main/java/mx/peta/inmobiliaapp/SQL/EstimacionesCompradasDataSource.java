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

public class EstimacionesCompradasDataSource {
    private static SQLiteDatabase db;

    public EstimacionesCompradasDataSource(Context context) {
        EstimacionesCompradasSqLiteHelper helper = new EstimacionesCompradasSqLiteHelper(context);
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

    public void deleteRegistro(EstimacionesCompradasModelItem modelItem) {
        db.delete(EstimacionesCompradasSqLiteHelper.APP_TABLE_NAME,EstimacionesCompradasSqLiteHelper.APP_COLUM_ID + " =? ",
                new String[]{String.valueOf(modelItem.id)});
    }

    public void writeRegistro(EstimacionesCompradasModelItem modelItem) {
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();         // Se crea un conjunto
            values.put(EstimacionesCompradasSqLiteHelper.APP_COLUM_FECHA_COMPRA, modelItem.fechaCompra);
            values.put(EstimacionesCompradasSqLiteHelper.APP_COLUM_PAY_KEY, modelItem.payKey);

            db.insert(EstimacionesCompradasSqLiteHelper.APP_TABLE_NAME, null, values);
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
    public EstimacionesCompradasModelItem getRegistro(int id) {
        String QUERY = "select " +
                EstimacionesCompradasSqLiteHelper.APP_COLUM_ID + ", " +
                EstimacionesCompradasSqLiteHelper.APP_COLUM_FECHA_COMPRA + ", " +
                EstimacionesCompradasSqLiteHelper.APP_COLUM_PAY_KEY +
                " from " + EstimacionesCompradasSqLiteHelper.APP_TABLE_NAME + " where " + EstimacionesCompradasSqLiteHelper.APP_COLUM_ID +
                " = ?";
        // ejemplo de instrucción db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery(QUERY, new String[] {Integer.toString(id)});
        if (cursor.moveToFirst()) {
            EstimacionesCompradasModelItem modelItem = new EstimacionesCompradasModelItem(
                    cursor.getString(cursor.getColumnIndexOrThrow(EstimacionesCompradasSqLiteHelper.APP_COLUM_FECHA_COMPRA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EstimacionesCompradasSqLiteHelper.APP_COLUM_PAY_KEY)));
            modelItem.id = cursor.getInt(cursor.getColumnIndexOrThrow(EstimacionesCompradasSqLiteHelper.APP_COLUM_ID));
            cursor.close();
            return modelItem;
        } else
            return null;
    }

    public List<EstimacionesCompradasModelItem> getAllItems()
    {
        List<EstimacionesCompradasModelItem> modelItemList = new ArrayList<>();
        Cursor cursor = db.query(EstimacionesCompradasSqLiteHelper.APP_TABLE_NAME,null,null,null,null,null,null);
        while (cursor.moveToNext())
        {
            EstimacionesCompradasModelItem modelItem = new EstimacionesCompradasModelItem(
                    cursor.getString(cursor.getColumnIndexOrThrow(EstimacionesCompradasSqLiteHelper.APP_COLUM_FECHA_COMPRA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EstimacionesCompradasSqLiteHelper.APP_COLUM_PAY_KEY)));
            modelItem.id = cursor.getInt(cursor.getColumnIndexOrThrow(EstimacionesCompradasSqLiteHelper.APP_COLUM_ID));
            modelItemList.add(modelItem);
        }
        cursor.close();
        return modelItemList;
    }
}
