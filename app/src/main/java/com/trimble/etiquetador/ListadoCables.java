package com.trimble.etiquetador;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.trimble.etiquetador.adapters.CableAdapter;
import com.trimble.etiquetador.model.Cable;

import java.util.ArrayList;

import static com.trimble.etiquetador.PostesPendientes.postes;

public class ListadoCables extends Activity {

    DataBaseHelper myDbHelper;
    static ArrayList<Cable> cables = new ArrayList<Cable>();
    static CableAdapter cableadapter;
    private int posteid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_cables);
        final ListView listviewCable = (ListView) findViewById(R.id.listadocables);
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            Log.w("Database",sqle.getMessage());
        }
        cableadapter = new CableAdapter(this,cables);
        /*
        listviewCable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListadoCables.this, RegistrarCable.class);
                Cable tmpcable = cables.get(position);
                intent.putExtra("modificar",1);
                intent.putExtra("barCode",tmpcable.getTagid());
                startActivity(intent);
            }
        });
        */
        posteid = getIntent().getIntExtra("posteid",0);
        listviewCable.setAdapter(cableadapter);
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mySql = "SELECT * FROM cables WHERE posteid = "+posteid+";";
        Cursor c = db.rawQuery(mySql, null);
        try{
            c.moveToFirst();
            cableadapter.notifyDataSetChanged();
            do{
                cables.add(new Cable(c.getString(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("tipo")),c.getString(c.getColumnIndex("uso")),c.getInt(c.getColumnIndex("escable"))!= 0,c.getString(c.getColumnIndex("operadora")),c.getString(c.getColumnIndex("dimension"))));
                c.moveToNext();
            }while(!c.isAfterLast());
        }
        catch (android.database.CursorIndexOutOfBoundsException e){
            cables.clear();
            cableadapter.notifyDataSetChanged();
        }
        c.close();
        db.close();

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        cables.clear();
        cableadapter.notifyDataSetChanged();
        startActivity(new Intent(this, InfoPoste.class));
        finish();
    }
}
