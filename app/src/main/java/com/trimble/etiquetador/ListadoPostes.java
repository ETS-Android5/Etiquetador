package com.trimble.etiquetador;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.trimble.etiquetador.adapters.PosteAdapter;
import com.trimble.etiquetador.model.Poste;
import com.trimble.etiquetador.DataBaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListadoPostes extends Activity {
    protected DataBaseHelper myDbHelper;
    protected ArrayList<Poste> postes = new ArrayList<Poste>();
    protected PosteAdapter posteadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_postes);
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }
        final ListView listviewPoste = (ListView) findViewById(R.id.lista);
        posteadapter = new PosteAdapter(this,postes);
        listviewPoste.setAdapter(posteadapter);
    }

    protected void checkLista(View view){
        String posteid = ((TextView) findViewById(R.id.posteid)).getText().toString();
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mySql = "SELECT * FROM postes WHERE posteid = '"+posteid+"';";
        Cursor c = db.rawQuery(mySql, null);
        try{
            c.moveToFirst();
            posteadapter.notifyDataSetChanged();
            do{
                postes.add(new Poste(c.getString(c.getColumnIndex("posteid")), c.getString(c.getColumnIndex("sector"))));
                c.moveToNext();
            }while(!c.isAfterLast());

        }
        catch (android.database.CursorIndexOutOfBoundsException e){
            Toast toast = Toast.makeText(this,"Código del poste no encontrado",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP| Gravity.LEFT, 50, 110);
            toast.show();
            postes.clear();
            posteadapter.notifyDataSetChanged();
        }
//        PostesAdapter adapter = new PostesAdapter(this, R.layout.activity_listado_postes, c, 1 );
//        ListView lista = (ListView) findViewById(R.id.lista);
//        lista.setAdapter(adapter);
        c.close();
    }



}
