package com.trimble.etiquetador;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

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
    protected InputMethodManager inputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_postes);
        final ListView listviewPoste = (ListView) findViewById(R.id.lista);
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            Log.w("Database",sqle.getMessage());
        }
        posteadapter = new PosteAdapter(this,postes);
        listviewPoste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                Intent intent = new Intent(ListadoPostes.this, InfoPoste.class);
                Poste tmpposte = postes.get(position);
                intent.putExtra("IdPoste", tmpposte.getId());
                intent.putExtra("CodigoPoste",tmpposte.getCodigo());
                intent.putExtra("Sector",tmpposte.getSector());
                intent.putExtra("NCables",tmpposte.getNcables());
                postes.clear();
                posteadapter.notifyDataSetChanged();
                startActivity(intent);
            }
        });
        listviewPoste.setAdapter(posteadapter);
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    public void checkLista(View view){
        String posteid = ((TextView) findViewById(R.id.posteid)).getText().toString();
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mySql = "SELECT * FROM postes WHERE posteid = '"+posteid+"';";
        Cursor c = db.rawQuery(mySql, null);
        try{
            c.moveToFirst();
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            posteadapter.notifyDataSetChanged();
            do{
                postes.add(new Poste(c.getString(c.getColumnIndex("posteid")), c.getString(c.getColumnIndex("sector")),c.getInt(c.getColumnIndex("_id")),c.getInt(c.getColumnIndex("ncables"))));
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
