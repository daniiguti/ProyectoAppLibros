package com.example.pruebarecviewbasedatos.Controlador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ComprobarLoginRegister extends SQLiteOpenHelper {

    //Nombre de la conexion
    private static final String DB_NAME = "db_usuarios";
    //Nombre de la tabla
    private static final String DB_TABLE_NAME = "db_usuarios";
    private static final int DB_VERSION = 1;
    //Columnas
    private static final String usuario = "usuario";
    private static final String password = "password";
    //Contexto
    private Context mContext;

    //Constructor, se encarga de crear la conexion o de conectarse a ella
    public ComprobarLoginRegister(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    //onCreate crea las tablas
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USER_TABLE = "CREATE TABLE " + DB_TABLE_NAME + "("
                + usuario + " TEXT PRIMARY KEY,"
                + password + " TEXT)";

        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
    }

    //onUpgrade para cuando actualicemos la base de datos, como es la primera versión, no hace nada
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //Metodo para insertar
    public long insertar(String user, String pass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(this.usuario, user);
        values.put(this.password, pass);
        long codError = db.insert(DB_TABLE_NAME,null,values);
        db.close();
        return codError;
    }

    //Método consultar
    public boolean consultar(String user, String pass){
        boolean contraCorrecta = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols = new String[]{this.usuario, this.password};
        //Cursos para recorrer los registros que nos devuelve la consulta
        Cursor c = db.query(DB_TABLE_NAME,cols,this.usuario+"='"+user+"'",null,null,null,null);
        if(c.moveToFirst()) {
            //Recorremos el cursor
            do {
                String contrasenia = c.getString(1);
                //Compruebo que la contraseña es la correcta
                if (contrasenia.equals(pass)) {
                    contraCorrecta = true;
                }
            } while (c.moveToNext());
        }
        return contraCorrecta;
    }
}
