package com.example.alondra.dadm_u4_practica1_alondragonzalezcruz;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button insertar, consultar, eliminar, actualizar;
    EditText identificador, nombre, ingrediente, preparacion, observacion;

    BaseDatos base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        identificador = findViewById(R.id.identificador);
        nombre = findViewById(R.id.nombre);
        ingrediente = findViewById(R.id.ingrediente);
        preparacion = findViewById(R.id.preparacion);
        observacion = findViewById(R.id.observacion);
        insertar = findViewById(R.id.insertar);
        consultar = findViewById(R.id.consultar);
        eliminar = findViewById(R.id.eliminar);
        actualizar = findViewById(R.id.actualizar);
        base = new BaseDatos(this, "recetas", null, 1);
        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoInsertar();
            }
        });
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirId(1); //metodo que tiene ek mensaje AlertDialog
            }
        });
       actualizar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(actualizar.getText().toString().startsWith("CONFIRMAR CAMBIOS")){
                  confirmacionActualizarDatos();
                  return;
               }
               pedirId(2);
           }
       });
       eliminar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               pedirId(3);
           }
       });
    }
    private void confirmacionActualizarDatos() {
        AlertDialog.Builder confir = new AlertDialog.Builder(this);
        confir.setTitle("IMPORTANTE").setMessage("¿Estas seguro que desea aplicar los cambios?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actualizarDatos();
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }
    private void actualizarDatos() {
        try{
            SQLiteDatabase tabla= base.getWritableDatabase();
            String SQL= "UPDATE RECETAS SET NOMBRE='"+nombre.getText().toString()+"', INGREDIENTES='"+ingrediente.getText().toString()+"', PREPARACION='"+preparacion.getText().toString()+"', OBSERVACIONES='"+observacion.getText().toString()
                    +"'WHERE ID=" +identificador.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "SE ACTUALIZO", Toast.LENGTH_LONG).show();
        }catch (SQLiteException e){
            Toast.makeText(this, "NO SE PUDO ACTUALIZAR", Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }


    private void pedirId(final int origen ) {
        final EditText pidoID= new EditText(this);
        String mensaje="INGRESE EL ID A BUSCAR";
        String botonAccion="BUSCAR...";
        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("VALOR ENTERO MAYOR DE 0");
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        if(origen ==2){
            mensaje="ESCRIBA ID A MODIFICAR";
            botonAccion="ACTUALIZAR";
        }
        if(origen ==3){
            mensaje="ESCRIBA ID QUE DESEA ELIMINAR";
            botonAccion="ELIMINAR";
        }
        alerta.setTitle("ATENCION").setMessage(mensaje)
                .setView(pidoID)
                .setPositiveButton(botonAccion, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(pidoID.getText().toString().isEmpty()){ //isEmpy si esta vacio
                            Toast.makeText(MainActivity.this, "DEBES ESCRIBIR UN ID", Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(pidoID.getText().toString(), origen);
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", null).show();
    }
    private void buscarDato(String idBuscar, int origen) { //row query
        try{
            SQLiteDatabase tabla= base.getReadableDatabase();
            String SQL ="SELECT * FROM RECETAS WHERE ID="+idBuscar;
            Cursor resultado =tabla.rawQuery(SQL, null); //cursor permite navegar enre los renglones d ela consultar
            if(resultado.moveToFirst()){
                if(origen==3){
                    //significa que se consulto para borrar
                    String datos=idBuscar+"&"+resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3)+"&"+resultado.getString(4);
                    confirmacionEliminarDatos(datos);
                    return;
                }
                //Si hay resultado
                identificador.setText(resultado.getString(0));
                nombre.setText(resultado.getString(1));
                ingrediente.setText(resultado.getString(2));
                preparacion.setText(resultado.getString(3));
                observacion.setText(resultado.getString(4));
                if(origen==2){
                    // consulta para modificar
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("CONFIRMAR CAMBIOS");
                    identificador.setEnabled(false);
                }
            }
            else{
                //no hay resultados
                Toast.makeText(this, "NO SE ECONTRARON RESULTADOS!", Toast.LENGTH_LONG).show();
            }
            tabla.close();
        }catch (SQLiteException e){
            Toast.makeText(this, "NO SE PUDO REALIZAR LA BUSQUEDA", Toast.LENGTH_LONG).show();
        }

    }
    private void confirmacionEliminarDatos(String datos) {
        final String []cadena = datos.split("&");
        AlertDialog.Builder confir = new AlertDialog.Builder(this);
        confir.setTitle("IMPORTANTE").setMessage("¿Estas seguro que desea eliminar la receta?: "+cadena[1])
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarDato(cadena[0]);
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }

        }).show();

    }
    private void eliminarDato(String idEliminar) {
        try{
            SQLiteDatabase tabla= base.getWritableDatabase();
            //String idEliminar = identificador.getText().toString();
            String SQL ="DELETE FROM RECETAS WHERE ID="+idEliminar;//identificador.getText().toString();
            tabla.execSQL(SQL);
            Toast.makeText(this, "SE ELIMINO CORRECTAMENTE EL REGISTRO", Toast.LENGTH_LONG).show();
            habilitarBotonesYLimpiarCampos();
            tabla.close();
        }catch (SQLiteException e){
            Toast.makeText(this, "NO SE PUDO ELIMINAR EL REGISTRO", Toast.LENGTH_LONG).show();
        }
    }




    private void habilitarBotonesYLimpiarCampos() {
        identificador.setText("");
        nombre.setText("");
        ingrediente.setText("");
        preparacion.setText("");
        observacion.setText("");
        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        identificador.setEnabled(true);
        actualizar.setText("ACTUALIZAR");
    }
    private void codigoInsertar(){
            try {
                if (identificador.getText().toString().isEmpty()) {
                    Toast.makeText(this, "INGRESE NUEVO ID", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!repetidoId(identificador.getText().toString())) {

                    SQLiteDatabase tabla = base.getWritableDatabase();

                    String SQL = "INSERT INTO RECETA VALUES(" + identificador.getText().toString() + ",'" + nombre.getText().toString() + "','" + ingrediente.getText().toString()
                            + "','" + preparacion.getText().toString()
                            + "','" + observacion.getText().toString() + "')";

                    tabla.execSQL(SQL);
                    tabla.close();
                    Toast.makeText(this, "SE GUARDO LA RECE CORRECTAMENTE", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "YA HAY UN REGISTRO CON ESE ID", Toast.LENGTH_LONG).show();
                    identificador.setText("");
                }
            } catch (SQLiteException e) {
                Toast.makeText(this, "ERROR EN LA APLICACION", Toast.LENGTH_LONG).show();
                habilitarBotonesYLimpiarCampos();
            }
        }//codigo insertar
    private boolean repetidoId(String idBuscar){
        SQLiteDatabase tabla= base.getReadableDatabase();
        String SQL ="SELECT * FROM RECETAS WHERE ID="+idBuscar;
        Cursor resultado =tabla.rawQuery(SQL, null);
        if(resultado.moveToFirst()){
            return true;
        }
        return false;
    }

}//class
