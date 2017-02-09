package com.trimble.etiquetador;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.trimble.etiquetador.adapters.CodeBarAdapter;
import com.trimble.etiquetador.model.CodeBar;
import com.trimble.mcs.rfid.v1.RfidConstants;
import com.trimble.mcs.rfid.v1.RfidException;
import com.trimble.mcs.rfid.v1.RfidManager;
import com.trimble.mcs.rfid.v1.RfidParameters;
import com.trimble.mcs.rfid.v1.RfidStatusCallback;


public class RfidActivity extends Activity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfid);
        maxPower=30;
        minPower=10;
        power=maxPower;
        rfidState = (TextView) findViewById(R.id.rfidState);
        mBtn = (ImageButton)findViewById(R.id.btn_scan);
        txtdB = (TextView)findViewById(R.id.txtdB);
        configureSeekBar();
        configureListCodeBar();
        numberTags=0;
        ((TextView)findViewById(R.id.etData)).setText("Tags Detectados: " + numberTags);

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
        adapter = new CodeBarAdapter(this,codeBars,null);
        elements.setAdapter(adapter);
        codeBars.add(new CodeBar("12345678", "No Detectado"));
        codeBars.add(new CodeBar("00002716","No Detectado"));
        codeBars.add(new CodeBar("12345673","No Detectado"));
    }

    private void configureSeekBar(){
        seekBar = (SeekBar) findViewById(R.id.powerBar);
        seekBar.setProgress(maxPower);
        seekBar.setMax(maxPower-minPower);
        txtdB.setText("Potencia: " + power + " dB");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
                power=progress+minPower;

                try {
                    // Set output mode to 'Intent' mode so that broadcast
                    // intents will be fired tags are scanned
                    RfidParameters parms = RfidManager.getParameters();
                    parms.setReadPower(power);
                    RfidManager.setParameters(parms);
                    txtdB.setText("Potencia: "+power+" dB");
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
                RfidManager.startScan();
                mScanning = true;
                rfidState.setText("Detener");
                mBtn.setBackgroundResource(R.drawable.rfidsignal100);
                seekBar.setEnabled(false);
            }
            else {
                RfidManager.stopScan();
                mScanning = false;
                //seekBar.setEnabled(true);
                rfidState.setText("Escanear");
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
        startActivity(new Intent(this, InfoPoste.class));
        finish();
    }

    private void onScanComplete(Context context, Intent intent) {
        String act = intent.getAction();

        if (act.equals(RfidConstants.ACTION_RFID_TAG_SCANNED)) {
            String tagId = intent.getStringExtra(RfidConstants.RFID_FIELD_ID);
            Log.d(LOG_TAG, "Tag: "+tagId);
            numberTags++;
            ((TextView)findViewById(R.id.etData)).setText("Tags Detectados: " + numberTags);
            //codeBars.add(new CodeBar(tagId));
            for(int i=0;i<codeBars.size();i++){
                String currentCode=codeBars.get(i).getCode();
                if(currentCode.equals(tagId.substring(16))){
                    //codeBars.add(new CodeBar("jajaja","jajajaj"));
                    codeBars.get(i).setEstado("Si Detectado");
                    Log.d(LOG_TAG, "Si");
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        } else if (act.equals(RfidConstants.ACTION_RFID_STOP_SCAN_NOTIFICATION)) {
            seekBar.setEnabled(true);
        }
    }
}
