package com.example.alondra.dadm_u4_practica1_alondragonzalezcruz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatos extends SQLiteOpenHelper {

        //sqliteopenhelper equivalente php myadmin
        //se utiliza la version 7 y 8, no se puede decrementar la version
        //constructor
        //context recibe el this(activity que ejecutara esta clase, String nombre de la bd,curso que traia el contenido, version de la BD)
        public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //se ejecuta cuando la aplicación(ejercicio1) se ejecuta en el cel
            //sirve para construir en el SQLite que esta en el cel las tablas
            //que la APP requiere para funcionar
            //sqlitedatebase db construye objetos que son capaces de relizar transaccion
            db.execSQL("CREATE TABLE RECETA(ID INTEGER PRIMARY KEY NOT NULL,NOMBRE VARCHAR(200), INGREDIENTE VARCHAR(100),PREPARACION VARCHAR(100),OBSERVACIONES VARCHAR(200))");
            //navegar entre renglones de select
            //puede realizar cualquier transaccion insert,create table,delete,update
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
}
