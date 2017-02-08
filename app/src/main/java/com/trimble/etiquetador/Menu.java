package com.trimble.etiquetador;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class Menu extends Activity {
    protected DataBaseHelper myDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            Log.w("Database",sqle.getMessage());
        }
    }

    public void iniciarMedicion(View view){
        Intent intent = new Intent(this, ListadoPostes.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    public void postesPendientes(View view){
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mySql = "SELECT * FROM postes WHERE estado = 1;";
        Cursor c = db.rawQuery(mySql, null);
        if(c.getCount() == 0) {
            Toast toast = Toast.makeText(this, "No existen postes pendientes", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 65, 230);
            toast.show();
        }
        else{
            Intent intent = new Intent(this, PostesPendientes.class);
            startActivity(intent);
        }
    }

    public void postesFinalizados(View view){
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mySql = "SELECT * FROM postes WHERE estado = 2;";
        Cursor c = db.rawQuery(mySql, null);
        if(c.getCount() == 0) {
            Toast toast = Toast.makeText(this, "No existen postes finalizados", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 65, 230);
            toast.show();
        }
        else{
            Intent intent = new Intent(this, listaFinalizados.class);
            startActivity(intent);
        }
    }
}
