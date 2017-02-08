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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RegistrarCable extends Activity {
    private int modificarVal;
    private String codigoposte;
    private int posteid;
    private String sector;
    private String tagid;
    private String[] uso;
    private String[] tipo;
    private String[] operadora;
    private EditText viewdimension;
    private DataBaseHelper myDbHelper;
    private TextView viewuso;
    private Spinner spinneruso;
    private Spinner spinnertipo;
    private Spinner spinneroperadora;
    private CheckBox checkboxCable;

    //    private GpsWorker gpsWorker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_cable);
        Intent intent = getIntent();
        posteid = intent.getIntExtra("posteId",0);
        codigoposte = intent.getStringExtra("codigoPoste");
        sector = intent.getStringExtra("sector");
        tagid = intent.getStringExtra("barCode");
        modificarVal = intent.getIntExtra("modificar",0);
        TextView viewcodigoPoste = (TextView) findViewById(R.id.viewposteid);
        TextView viewsector = (TextView) findViewById(R.id.viewsectorcable);
        TextView viewtagid = (TextView) findViewById(R.id.viewtagidcable);
        viewcodigoPoste.setText(codigoposte);
        viewsector.setText(sector);
        viewtagid.setText(tagid);
        viewuso = (TextView) findViewById(R.id.textView13);
        viewdimension = (EditText) findViewById(R.id.viewDimension);
        this.uso = new String[] {"Distribución","Acometida"};
        this.tipo = new String[] {"Fibra","Cobre multipar"};
        this.operadora = new String[] {"Claro","Netlife","TVCable"};
        spinneruso = (Spinner) findViewById(R.id.spinnerUso);
        spinnertipo = (Spinner) findViewById(R.id.spinnerTipo);
        spinneroperadora = (Spinner) findViewById(R.id.spinnerOperadora);
        ArrayAdapter<String> adapterUso = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, uso);
        spinneruso.setAdapter(adapterUso);
        ArrayAdapter<String> adaptertipo = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, tipo);
        spinnertipo.setAdapter(adaptertipo);
        ArrayAdapter<String> adapteroperadora = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, operadora);
        spinneroperadora.setAdapter(adapteroperadora);
        Button eliminar = (Button) findViewById(R.id.eliminar);
        Button modificar = (Button) findViewById(R.id.modificar);
        if(modificarVal == 0){
            eliminar.setVisibility(View.GONE);
            modificar.setVisibility(View.GONE);
        }
        else{
            eliminar.setVisibility(View.VISIBLE);
            modificar.setVisibility(View.VISIBLE);
            Button registrar = (Button) findViewById(R.id.registrar);
            registrar.setVisibility(View.GONE);
        }
        checkboxCable = (CheckBox) findViewById(R.id.checkBoxCable);
        checkboxCable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if(checkboxCable.isChecked()){
                        tipo = new String[] {"Activo","Pasivo"};
                        ArrayAdapter<String> adaptertipo = new ArrayAdapter<String>(RegistrarCable.this,android.R.layout.simple_spinner_item, tipo);
                        spinnertipo.setAdapter(adaptertipo);
                        viewuso.setText("Dim:");
                        spinneruso.setVisibility(View.INVISIBLE);
                        viewdimension.setVisibility(View.VISIBLE);
                    }
                    else{
                        tipo = new String[] {"Fibra","Cobre multipar"};
                        ArrayAdapter<String> adaptertipo = new ArrayAdapter<String>(RegistrarCable.this,android.R.layout.simple_spinner_item, tipo);
                        spinnertipo.setAdapter(adaptertipo);
                        viewuso.setText("Uso:");
                        spinneruso.setVisibility(View.VISIBLE);
                        viewdimension.setVisibility(View.INVISIBLE);
                    }
               }
           }
        );
//        gpsWorker = new GpsWorker(this);
//        gpsWorker.init();
//        Position currentPosition = gpsWorker.getCurrentPosition();
//        Log.w("GPS",Double.toString(currentPosition.getX()));
//        Log.w("GPS2",Double.toString(currentPosition.getY()));
    }

    public void regresarListadoCables(View view){
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            Log.w("Database",sqle.getMessage());
        }
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT ncables FROM postes WHERE _id = '"+posteid+"';", null);
        c.moveToFirst();
        int ncables = c.getInt(c.getColumnIndex("ncables"));
        db.close();
        c.close();
        Intent intent = new Intent(this, InfoPoste.class);
        intent.putExtra("NCables", ncables);
        startActivity(intent);
    }

    public void eliminarCable(View view){
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            Log.w("Database",sqle.getMessage());
        }
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mySql = "DELETE FROM cables WHERE _id = '"+tagid+"'";
        db.execSQL(mySql);
        mySql = "UPDATE postes SET ncables = ncables - 1 WHERE _id = '"+posteid+"';";
        db.execSQL(mySql);
        Cursor c = db.rawQuery("SELECT ncables FROM postes WHERE _id = '"+posteid+"';", null);
        c.moveToFirst();
        int ncables = c.getInt(c.getColumnIndex("ncables"));
        db.close();
        Intent intent = new Intent(this, InfoPoste.class);
        intent.putExtra("NCables", ncables);
        startActivity(intent);
    }

    public void modificarCable(View view){
        String tipoCable = spinnertipo.getSelectedItem().toString();
        String operadoraCable = spinneroperadora.getSelectedItem().toString();
        CheckBox checkboxCable = (CheckBox) findViewById(R.id.checkBoxCable);
        String mySql = "";
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            Log.w("Database",sqle.getMessage());
        }
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        if(checkboxCable.isChecked()){
            String dimension = viewdimension.getText().toString();
            mySql = "UPDATE cables SET tipo ='"+tipoCable+"',uso ='',escable ="+0+",operadora ='"+operadoraCable+"',usuario = 'cnel',dimension='"+dimension+"' WHERE _id = '"+tagid+"';";
        }
        else{
            String usoCable = spinneruso.getSelectedItem().toString();
            mySql = "UPDATE cables SET tipo ='"+tipoCable+"',uso ='"+usoCable+"',escable ="+1+",operadora ='"+operadoraCable+"',usuario = 'cnel',dimension='' WHERE _id = '"+tagid+"';";
        }
        db.execSQL(mySql);
        Cursor c = db.rawQuery("SELECT ncables FROM postes WHERE _id = '"+posteid+"';", null);
        c.moveToFirst();
        int ncables = c.getInt(c.getColumnIndex("ncables"));
        db.close();
        Intent intent = new Intent(this, InfoPoste.class);
        intent.putExtra("NCables", ncables);
        startActivity(intent);
    }

    public void registrarCableBase(View view){
        String tipoCable = spinnertipo.getSelectedItem().toString();
        String operadoraCable = spinneroperadora.getSelectedItem().toString();
        String usoCable = "";
        String dimension = "";
        String mySql = "";
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            Log.w("Database",sqle.getMessage());
        }
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        if(checkboxCable.isChecked()){
            dimension = viewdimension.getText().toString();
            mySql = "INSERT INTO cables (_id,posteid,tipo,dimension,escable,operadora,usuario) VALUES ('"+tagid+"','"+posteid+"','"+tipoCable+"','"+dimension+"',"+0+",'"+operadoraCable+"','cnel');";
            }
        else{
            usoCable = spinneruso.getSelectedItem().toString();
            mySql = "INSERT INTO cables (_id,posteid,tipo,uso,escable,operadora,usuario) VALUES ('"+tagid+"','"+posteid+"','"+tipoCable+"','"+usoCable+"',"+1+",'"+operadoraCable+"','cnel');";
            }
        db.execSQL(mySql);
        mySql = "UPDATE postes SET ncables = ncables + 1 WHERE _id = '"+posteid+"';";
        db.execSQL(mySql);
        Cursor c = db.rawQuery("SELECT ncables FROM postes WHERE _id = '"+posteid+"';", null);
        c.moveToFirst();
        Intent intent = new Intent(this, InfoPoste.class);
        int ncables = c.getInt(c.getColumnIndex("ncables"));
        intent.putExtra("NCables", ncables);
        c.close();
        db.close();
        startActivity(intent);
    }
}
