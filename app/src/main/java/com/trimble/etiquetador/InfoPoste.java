package com.trimble.etiquetador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.trimble.etiquetador.adapters.CableAdapter;
import com.trimble.etiquetador.model.Cable;

import java.util.ArrayList;

public class InfoPoste extends Activity implements View.OnClickListener {
    private ImageButton boton;
    DataBaseHelper myDbHelper;
    private static int posteid;
    private static String codigoposte;
    private static String sector;
    private static int ncables;
    private static String ventana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_poste);
        boton = (ImageButton) findViewById(R.id.boton);
        boton.setOnClickListener(this);
        Intent intent = getIntent();
        if(intent.getIntExtra("IdPoste",0) != 0){
            posteid = intent.getIntExtra("IdPoste",0);
            codigoposte = intent.getStringExtra("CodigoPoste");
            sector = intent.getStringExtra("Sector");
            ncables = intent.getIntExtra("NCables",0);
            ventana = intent.getStringExtra("Ventana");
        }
        TextView viewcodigoPoste = (TextView) findViewById(R.id.viewcodigo);
        TextView viewsector = (TextView) findViewById(R.id.viewsector);
        EditText viewncable = (EditText) findViewById(R.id.viewncable);
        viewcodigoPoste.setText(codigoposte);
        viewsector.setText(sector);
        viewncable.setText(Integer.toString(ncables));
        viewncable.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "99")});
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            Log.w("Database",sqle.getMessage());
        }
        (findViewById(R.id.viewncable)).getBackground().mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        ((TextView) findViewById(R.id.viewncable)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String ncable = v.getText().toString();
                    if(!ncable.equals("")){
                        int ncabledata = Integer.parseInt(ncable);
                        if(ncabledata != ncables){
                            SQLiteDatabase db = myDbHelper.getReadableDatabase();
                            String mySql = "UPDATE postes SET ncables = "+ncabledata+" WHERE _id = "+posteid+";";
                            db.execSQL(mySql);
                            db.close();
                            Toast toast = Toast.makeText(InfoPoste.this,"Número de cables modificado",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP| Gravity.LEFT, 50, 130);
                            toast.show();
                            v.setCursorVisible(false);
                            ncables = ncabledata;
                        }
                        else{
                            Toast toast = Toast.makeText(InfoPoste.this,"Modifique el número de cables primero",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP| Gravity.LEFT, 20, 130);
                            toast.show();
                        }
                    }
                }
                return false;
            }
        });
    }

    public void openRfid(View view){
        Intent rfidIntent = new Intent(this,RfidActivity.class);
        rfidIntent.putExtra("posteId",posteid);
        startActivity(rfidIntent);
        finish();
    }

    public void escanear(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Escanea Codigo de Barra");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    @Override
    public void onClick(View v) {
        escanear();
    }

    public void registrarCable(String barCode){
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            Log.w("Database",sqle.getMessage());
        }
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mySql = "SELECT _id FROM cables WHERE _id = '"+barCode+"';";
        Cursor c = db.rawQuery(mySql, null);
        if(c.getCount() == 1){
            Toast toast = Toast.makeText(this,"Tag Repetido",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP| Gravity.LEFT, 80, 310);
            ViewGroup group = (ViewGroup) toast.getView();
            TextView messageTextView = (TextView) group.getChildAt(0);
            messageTextView.setTextSize(40);
            toast.show();
        }
        else{
            mySql = "INSERT INTO cables (_id,posteid,tipo,dimension,escable,operadora,usuario) VALUES ('"+barCode+"','"+posteid+"','','',"+0+",'','cnel');";
            db.execSQL(mySql);
            Toast toast = Toast.makeText(this,barCode,Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 80, 310);
            ViewGroup group = (ViewGroup) toast.getView();
            TextView messageTextView = (TextView) group.getChildAt(0);
            messageTextView.setTextSize(40);
            toast.show();
        }
        c.close();
        db.close();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Escaneo Terminado", Toast.LENGTH_SHORT).show();
            } else {
                String code=result.getContents();
                registrarCable(code);
                escanear();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void enableBlink(View view){
        EditText ncables = (EditText) view;
        ncables.setCursorVisible(true);
        InfoPoste.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void cablesFeatures(View view){
        Intent intent = new Intent(this,ListadoCables.class);
        intent.putExtra("posteid",posteid);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(ventana.equals("listado"))
            startActivity(new Intent(this, ListadoPostes.class));
        else if(ventana.equals("pendientes"))
            startActivity(new Intent(this, PostesPendientes.class));
        else if(ventana.equals("finalizados"))
            startActivity(new Intent(this, listaFinalizados.class));
        else if(ventana.equals("nuevo"))
            startActivity(new Intent(this, RegistrarPoste.class));
        else if(ventana.equals("repetidos"))
            startActivity(new Intent(this, ListadoRepetidos.class));
        finish();
    }

    public class InputFilterMinMax implements InputFilter {

        private int min, max;

        public InputFilterMinMax(int min, int max) {

            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}