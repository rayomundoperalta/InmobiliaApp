package mx.peta.inmobiliaapp.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mx.peta.inmobiliaapp.Propiedad;

/**
 * Created by rayo on 11/9/16.
 */

public class PropiedadesDataSource {
    private static SQLiteDatabase db;

    public PropiedadesDataSource(Context context) {
        PropiedadesSqLiteHelper helper = new PropiedadesSqLiteHelper(context);
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

    public void deleteRegistro(PropiedadesModelItem modelItem) {
        db.delete(PropiedadesSqLiteHelper.APP_TABLE_NAME,PropiedadesSqLiteHelper.APP_COLUM_ID + " =? ",
                new String[]{String.valueOf(modelItem.id)});
    }

    public void writeRegistro(PropiedadesModelItem propiedad) {
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();         // Se crea un conjunto
            values.put(PropiedadesSqLiteHelper.APP_COLUM_TELEFONO, propiedad.telefono);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_DIRECCION, propiedad.direccion);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_LATITUD, propiedad.latitud);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_LONGITUD, propiedad.longitud);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_PHOTOFILENAME, propiedad.photoFileName);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_TIPOLOGIA, propiedad.tipologia);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_CP, propiedad.CP);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_DELEGACION, propiedad.delegacion);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_ENTIDAD, propiedad.entidad);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_PROXIMIDADURBANA, propiedad.proximidadUrbana);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_CLASEINMUEBLE, propiedad.claseInmueble);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_VIDAUTIL, propiedad.vidaUtil);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_SUPERTERRENO, propiedad.superTerreno);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_SUPERCONSTRUIDO, propiedad.superConstruido);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_VALCONST, propiedad.valConst);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_VALCONCLUIDO, propiedad.valConcluido);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_VALESTIMADO, propiedad.valEstimado);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_VALDESSTN, propiedad.valDesStn);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_REVISADOMANUALMENT, propiedad.revisadoManualment);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_SENSIBILIDAD, propiedad.sensibilidad);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_GROUPPOSITION, propiedad.groupPosition);
            values.put(PropiedadesSqLiteHelper.APP_COLUM_CHILDPOSITION, propiedad.childPosition);

            db.insert(PropiedadesSqLiteHelper.APP_TABLE_NAME, null, values);
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
    public PropiedadesModelItem getRegistro(int id) {
        String QUERY = "select " +
                PropiedadesSqLiteHelper.APP_COLUM_ID + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_TELEFONO + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_DIRECCION + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_LATITUD + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_LONGITUD + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_PHOTOFILENAME + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_TIPOLOGIA + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_CP + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_DELEGACION + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_ENTIDAD + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_PROXIMIDADURBANA + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_CLASEINMUEBLE + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_VIDAUTIL + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_SUPERTERRENO + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_SUPERCONSTRUIDO + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_VALCONST + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_VALCONCLUIDO + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_VALESTIMADO + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_VALDESSTN + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_REVISADOMANUALMENT + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_SENSIBILIDAD + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_GROUPPOSITION + ", " +
                PropiedadesSqLiteHelper.APP_COLUM_CHILDPOSITION +
                " from " + PropiedadesSqLiteHelper.APP_TABLE_NAME + " where " + PropiedadesSqLiteHelper.APP_COLUM_ID +
                " = ?";
        // ejemplo de instrucción db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery(QUERY, new String[] {Integer.toString(id)});
        if (cursor.moveToFirst()) {
            PropiedadesModelItem modelItem = new PropiedadesModelItem(
                    cursor.getString(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_TELEFONO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_DIRECCION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_LATITUD)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_LONGITUD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_PHOTOFILENAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_TIPOLOGIA)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_CP)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_DELEGACION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_ENTIDAD)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_PROXIMIDADURBANA)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_CLASEINMUEBLE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_VIDAUTIL)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_SUPERTERRENO)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_SUPERCONSTRUIDO)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_VALCONST)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_VALCONCLUIDO)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_VALESTIMADO)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_VALDESSTN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_REVISADOMANUALMENT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_SENSIBILIDAD)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_GROUPPOSITION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_CHILDPOSITION)));
            modelItem.id = cursor.getInt(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_ID));
            cursor.close();
            return modelItem;
        } else
            return null;
    }

    /*
        Esta subrutina hace un update sobre la base de datos

    public void updateEstado(ModelItem modelItem) {
        String QUERY = "update " + SqLiteHelper.APP_TABLE_NAME + " set " +
                SqLiteHelper.APP_COLUMN_ESTADO + " = " + String.valueOf(ModelItem.ACTUALIZADA) +
                " where " + SqLiteHelper.APP_COLUMN_ID + " = " + String.valueOf(modelItem.id);
        db.execSQL(QUERY);
    }
    */

    public void updateApp(PropiedadesModelItem modelItem) {
        String QUERY = "update " + PropiedadesSqLiteHelper.APP_TABLE_NAME + " set " +
                PropiedadesSqLiteHelper.APP_COLUM_TELEFONO              + " = '" + modelItem.telefono + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_DIRECCION             + " = '" + modelItem.direccion + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_LATITUD               + " = '" + modelItem.latitud + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_LONGITUD              + " = '" + modelItem.longitud + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_PHOTOFILENAME         + " = '" + modelItem.photoFileName + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_TIPOLOGIA             + " = '" + modelItem.tipologia + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_CP                    + " = '" + modelItem.CP + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_DELEGACION            + " = '" + modelItem.delegacion + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_ENTIDAD               + " = '" + modelItem.entidad + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_PROXIMIDADURBANA      + " = '" + modelItem.proximidadUrbana + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_CLASEINMUEBLE         + " = '" + modelItem.claseInmueble + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_VIDAUTIL              + " = '" + modelItem.vidaUtil + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_SUPERTERRENO          + " = '" + modelItem.superTerreno + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_SUPERCONSTRUIDO       + " = '" + modelItem.superConstruido + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_VALCONST              + " = '" + modelItem.valConst + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_VALCONCLUIDO          + " = '" + modelItem.valConcluido + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_VALESTIMADO           + " = '" + modelItem.valEstimado + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_VALDESSTN             + " = '" + modelItem.valDesStn + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_REVISADOMANUALMENT    + " = '" + modelItem.revisadoManualment + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_SENSIBILIDAD          + " = '" + modelItem.sensibilidad + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_GROUPPOSITION         + " = '" + modelItem.groupPosition + "'," +
                PropiedadesSqLiteHelper.APP_COLUM_CHILDPOSITION         + " = '" + modelItem.childPosition + "' " +
                " where " + PropiedadesSqLiteHelper.APP_COLUM_ID  + " = " + String.valueOf(modelItem.id);
        System.out.println("Inmovilia " + QUERY);
        db.execSQL(QUERY);
    }

    public List<PropiedadesModelItem> getAllItems()
    {
        List<PropiedadesModelItem> modelItemList = new ArrayList<>();
        Cursor cursor = db.query(PropiedadesSqLiteHelper.APP_TABLE_NAME,null,null,null,null,null,null);
        while (cursor.moveToNext())
        {
            PropiedadesModelItem modelItem = new PropiedadesModelItem(
                    cursor.getString(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_TELEFONO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_DIRECCION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_LATITUD)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_LONGITUD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_PHOTOFILENAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_TIPOLOGIA)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_CP)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_DELEGACION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_ENTIDAD)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_PROXIMIDADURBANA)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_CLASEINMUEBLE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_VIDAUTIL)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_SUPERTERRENO)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_SUPERCONSTRUIDO)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_VALCONST)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_VALCONCLUIDO)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_VALESTIMADO)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_VALDESSTN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_REVISADOMANUALMENT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_SENSIBILIDAD)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_GROUPPOSITION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_CHILDPOSITION)));
            modelItem.id = cursor.getInt(cursor.getColumnIndexOrThrow(PropiedadesSqLiteHelper.APP_COLUM_ID));
            modelItemList.add(modelItem);
        }
        cursor.close();
        return modelItemList;
    }
}
