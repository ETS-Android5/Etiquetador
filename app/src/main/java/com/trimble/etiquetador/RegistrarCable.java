package com.trimble.etiquetador;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.trimble.etiquetador.GpsWorker;
import com.trimble.etiquetador.model.Position;

public class RegistrarCable extends Activity {
    private static String codigoposte;
    private static int posteid;
    private static String sector;
    private static String tagid;
    private String[] uso;
    private String[] tipo;
    private String[] operadora;
    private DataBaseHelper myDbHelper;
    private Spinner spinneruso;
    private Spinner spinnertipo;
    private Spinner spinneroperadora;

    //    private GpsWorker gpsWorker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_cable);
        Intent intent = getIntent();
        if(intent.getIntExtra("posteId",0) != 0) {
            posteid = intent.getIntExtra("posteId",0);
            codigoposte = intent.getStringExtra("codigoPoste");
            sector = intent.getStringExtra("sector");
            tagid = intent.getStringExtra("barCode");
        }
        TextView viewcodigoPoste = (TextView) findViewById(R.id.viewposteid);
        TextView viewsector = (TextView) findViewById(R.id.viewsectorcable);
        TextView viewtagid = (TextView) findViewById(R.id.viewtagidcable);
        viewcodigoPoste.setText(codigoposte);
        viewsector.setText(sector);
        viewtagid.setText(tagid);
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
//        gpsWorker = new GpsWorker(this);
//        gpsWorker.init();
//        Position currentPosition = gpsWorker.getCurrentPosition();
//        Log.w("GPS",Double.toString(currentPosition.getX()));
//        Log.w("GPS2",Double.toString(currentPosition.getY()));
    }

    public void regresarListadoCables(View view){
        Intent intent = new Intent(this, InfoPoste.class);
        startActivity(intent);
    }

    public void registrarCableBase(View view){
        String tipoCable = spinnertipo.getSelectedItem().toString();
        String usoCable = spinneruso.getSelectedItem().toString();
        String operadoraCable = spinneroperadora.getSelectedItem().toString();
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            Log.w("Database",sqle.getMessage());
        }
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mySql = "INSERT INTO cables (_id,posteid,tipo,uso,escable,operadora,usuario) VALUES ('"+tagid+"','"+posteid+"','"+tipoCable+"','"+usoCable+"',"+1+",'"+operadoraCable+"','cnel');";
        try{
            db.execSQL(mySql);
            Intent intent = new Intent(this, InfoPoste.class);
            startActivity(intent);
        }
        catch (SQLException e){
            Toast toast = Toast.makeText(this,"Ya existe un Tag con ese ID",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP| Gravity.LEFT, 100, 310);
            toast.show();
            Log.w("errorInsert",e.getMessage());
        }
    }
}
