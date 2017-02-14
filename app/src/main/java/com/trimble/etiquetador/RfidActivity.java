package com.trimble.etiquetador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.trimble.etiquetador.adapters.CodeBarAdapter;
import com.trimble.etiquetador.model.Cable;
import com.trimble.etiquetador.model.CodeBar;
import com.trimble.mcs.rfid.v1.RfidConstants;
import com.trimble.mcs.rfid.v1.RfidException;
import com.trimble.mcs.rfid.v1.RfidManager;
import com.trimble.mcs.rfid.v1.RfidParameters;
import com.trimble.mcs.rfid.v1.RfidStatusCallback;
import java.util.Map;
import java.util.HashMap;
import android.widget.Toast;

public class RfidActivity extends Activity implements Observer {
    private final static String LOG_TAG = "RfidDemo";

    private BroadcastReceiver mRecvr;
    private IntentFilter mFilter;
    private boolean mScanning = false;
    private ImageButton mBtn;
    private TextView rfidState;
    private int power;
    private SeekBar seekBar;
    private int maxPower;
    private int minPower;
    private TextView txtdB;
    private ArrayList<CodeBar> codeBars=  new ArrayList<CodeBar>();
    private int numberTags;
    private ListView elements;
    private CodeBarAdapter adapter;
    private Map<String, String> codeBarRfid = new HashMap<String, String>();
    private DataBaseHelper myDbHelper;
    private int posteId;

    @Override
    public void update(final Object objeto){
        //eliminar el cable
        new AlertDialog.Builder(RfidActivity.this)
                .setTitle("Confirmación de eliminar")
                .setMessage("¿Está seguro de eliminar el cable "+objeto+"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SQLiteDatabase db = myDbHelper.getReadableDatabase();
                        String mySql = "DELETE FROM cables WHERE _id = "+objeto+";";
                        db.execSQL(mySql);
                        db.close();
                        for(int i=0;i<codeBars.size();i++) {
                            CodeBar c=codeBars.get(i);
                            if(c.getCode()==objeto){
                                codeBars.remove(i);
                                if(c.getEstado()==1){
                                    numberTags--;
                                }
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                        ((TextView)findViewById(R.id.etData)).setText("Tags Detectados: " + numberTags +"/"+codeBars.size());
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfid);
        posteId = getIntent().getIntExtra("posteId",0);
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            Log.w("Database",sqle.getMessage());
        }
        maxPower=30;
        minPower=10;
        power=maxPower;
        rfidState = (TextView) findViewById(R.id.rfidState);
        mBtn = (ImageButton)findViewById(R.id.btn_scan);
        txtdB = (TextView)findViewById(R.id.txtdB);
        configureSeekBar();
        configureListCodeBar();
        numberTags=0;
        ((TextView)findViewById(R.id.etData)).setText("Tags Detectados: " + numberTags +"/"+codeBars.size());

        mRecvr = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                onScanComplete(context, intent);
            }
        };

        mFilter = new IntentFilter();
        mFilter.addAction(RfidConstants.ACTION_RFID_TAG_SCANNED);
        mFilter.addAction(RfidConstants.ACTION_RFID_STOP_SCAN_NOTIFICATION);

        RfidStatusCallback cb = new RfidStatusCallback() {
            @Override
            public void onAPIReady() {
                // Called when RfidManager API is fully initialized.
                // Perform initial RFID configuration here.
                onRfidReady();
            }
        };

        try {
            RfidManager.init(this, RfidConstants.SESSION_SCOPE_PRIVATE, cb);
        } catch (RfidException e) {
            Log.e(LOG_TAG, "Error initializing RFID Manager.", e);
        }
    }

    private void configureListCodeBar(){
        elements = (ListView) findViewById(R.id.listRfid);
        adapter = new CodeBarAdapter(this,codeBars,this);
        elements.setAdapter(adapter);
        codeBarRfid.put("57864259","E280116060000207BF501B86" );
        codeBarRfid.put("87923568", "E280116060000207BF51AB76");
        codeBarRfid.put("95050003", "E280116060000207BF51CD46");
        codeBarRfid.put("90311017","E280116060000207BF51CDB6");
        //codeBars.add(new CodeBar("57864259", "No Detectado", "E280116060000207BF501B86"));
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mySql = "SELECT * FROM cables WHERE posteid="+posteId+";";
        Cursor c = db.rawQuery(mySql, null);
        codeBars.clear();
        try{
            c.moveToFirst();
            do{
                String codeNumber=c.getString(c.getColumnIndex("_id"));
                String rfid=codeBarRfid.get(codeNumber);
                if(rfid==null)
                    rfid="";
                codeBars.add(new CodeBar(c.getString(c.getColumnIndex("_id")),0,rfid));
                c.moveToNext();
            }while(!c.isAfterLast());
        }
        catch (android.database.CursorIndexOutOfBoundsException e){
            codeBars.clear();
            adapter.notifyDataSetChanged();
        }
        c.close();
        db.close();

    }

    private void configureSeekBar(){
        seekBar = (SeekBar) findViewById(R.id.powerBar);
        seekBar.setProgress(maxPower);
        seekBar.setMax(maxPower - minPower);
        txtdB.setText("Potencia: " + power + " dB");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
                power = progress + minPower;

                try {
                    // Set output mode to 'Intent' mode so that broadcast
                    // intents will be fired tags are scanned
                    RfidParameters parms = RfidManager.getParameters();
                    parms.setReadPower(power);
                    RfidManager.setParameters(parms);
                    txtdB.setText("Potencia: " + power + " dB");
                } catch (RfidException e) {
                    Log.e(LOG_TAG, "Error setting RFID parameters.", e);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //textView.setText("Covered: " + progress + "/" + seekBar.getMax());

            }
        });
    }

    public void finalizarPoste(View view){
        new AlertDialog.Builder(this)
                .setTitle("Confirmación de finalizar")
                .setMessage("¿Está seguro de haber terminado de editar el poste?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (numberTags == codeBars.size()) {
                            SQLiteDatabase db = myDbHelper.getReadableDatabase();
                            String mySql = "UPDATE postes SET estado = 2 WHERE _id = " + posteId + ";";
                            db.execSQL(mySql);
                            db.close();
                            try {
                                if (mScanning) {
                                    RfidManager.stopScan();
                                    mScanning = false;
                                    //seekBar.setEnabled(true);
                                    rfidState.setText("Escanear");
                                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) rfidState
                                            .getLayoutParams();
                                    mlp.setMargins(53, 8, 0, 0);
                                    mBtn.setBackgroundResource(R.drawable.rfidsignal80);
                                }
                                onDestroy();
                            } catch (Exception e) {
                                Log.e(LOG_TAG, "Error al finalizar ", e);
                            }
                            Intent intent = new Intent(RfidActivity.this, ListadoPostes.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast toast = Toast.makeText(RfidActivity.this, "Es necesario que se verifiquen todos los TAGS antes de finalizar", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 520);
                            toast.show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void onRfidReady() {
        try {
            // Set output mode to 'Intent' mode so that broadcast
            // intents will be fired tags are scanned
            RfidParameters parms = RfidManager.getParameters();
            parms.setOutputMode(RfidConstants.OUTPUT_MODE_INTENT);
            parms.setReadPower(power);
            RfidManager.setParameters(parms);
        } catch (RfidException e) {
            Log.e(LOG_TAG, "Error setting RFID parameters.", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            RfidManager.deinit();
        } catch (RfidException e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mRecvr, mFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mRecvr);
        if (mScanning) {
            try {
                RfidManager.stopScan();
            } catch (RfidException e) {
            }
            mScanning = false;
            rfidState.setText("Escanear");
            mBtn.setBackgroundResource(R.drawable.rfidsignal80);
        }
    }

    public void startScan(View view) {
        try {
            if (!mScanning) {
                numberTags=0;
                for(int i=0;i<codeBars.size();i++) {
                    codeBars.get(i).setEstado(0);
                }
                adapter.notifyDataSetChanged();
                RfidManager.startScan();
                mScanning = true;
                rfidState.setText("Detener");
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) rfidState
                        .getLayoutParams();
                mlp.setMargins(57, 8, 10, 0);
                mBtn.setBackgroundResource(R.drawable.rfidsignal100);
                seekBar.setEnabled(false);
            }
            else {
                RfidManager.stopScan();
                mScanning = false;
                //seekBar.setEnabled(true);
                rfidState.setText("Escanear");
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) rfidState
                        .getLayoutParams();
                mlp.setMargins(53, 8, 0, 0);
                mBtn.setBackgroundResource(R.drawable.rfidsignal80);
            }
        } catch (RfidException e) {
            Log.e(LOG_TAG, "Error attempting to start/stop scan.", e);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        try {
            if (mScanning) {
                RfidManager.stopScan();
                mScanning = false;
                //seekBar.setEnabled(true);
                rfidState.setText("Escanear");
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) rfidState
                        .getLayoutParams();
                mlp.setMargins(53, 8, 0, 0);
                mBtn.setBackgroundResource(R.drawable.rfidsignal80);
            }
            //onDestroy();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error on back pressed", e);
        }
        startActivity(new Intent(this, InfoPoste.class));
        finish();
    }

    private void onScanComplete(Context context, Intent intent) {
        String act = intent.getAction();
        if (act.equals(RfidConstants.ACTION_RFID_TAG_SCANNED)) {
            String tagId = intent.getStringExtra(RfidConstants.RFID_FIELD_ID);
            Log.d(LOG_TAG, "Tag: " + tagId);
            Log.d(LOG_TAG, tagId);
            for(int i=0;i<codeBars.size();i++){
                String currentCode=codeBars.get(i).getRfid();
                if(currentCode.equals(tagId)){
                    numberTags++;
                    codeBars.get(i).setEstado(1);
                    adapter.notifyDataSetChanged();
                    ((TextView)findViewById(R.id.etData)).setText("Tags Detectados: " + numberTags + "/" + codeBars.size());
                    break;
                }
            }
        } else if (act.equals(RfidConstants.ACTION_RFID_STOP_SCAN_NOTIFICATION)) {
            seekBar.setEnabled(true);
        }
    }
}
