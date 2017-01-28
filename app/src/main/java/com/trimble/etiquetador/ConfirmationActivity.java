package com.trimble.etiquetador;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ConfirmationActivity extends Activity implements View.OnClickListener {
    private TextView txtCode;
    private Button btnVolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        Intent intent = getIntent();
        String barCode = intent.getStringExtra("barCode");
        btnVolver= (Button) findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(this);
        txtCode = (TextView) findViewById(R.id.txtCode);
        txtCode.setText(barCode);
    }

    @Override
    public void onClick(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        //integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Escanea Codigo de Barra");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
            } else {
                String code=result.getContents();
                txtCode = (TextView) findViewById(R.id.txtCode);
                txtCode.setText(code);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
