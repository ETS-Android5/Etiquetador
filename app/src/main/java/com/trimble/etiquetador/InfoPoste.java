package com.trimble.etiquetador;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class InfoPoste extends Activity implements View.OnClickListener {
    private Button boton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_poste);
        boton = (Button) findViewById(R.id.button);
        boton.setOnClickListener(this);
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
                //txtCode.setText(code);
                //Toast.makeText(this, "Scanned: " + code, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ConfirmationActivity.class);
                intent.putExtra("barCode",code);
                startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}