package com.trimble.etiquetador;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Menu extends Activity {
    protected DataBaseHelper myDbHelper;
    PostTask posttask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            Log.w("Database",sqle.getMessage());
        }
    }

    public void iniciarMedicion(View view){
        Intent intent = new Intent(this, ListadoPostes.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    public void postesPendientes(View view){
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mySql = "SELECT * FROM postes WHERE estado = 1;";
        Cursor c = db.rawQuery(mySql, null);
        if(c.getCount() == 0) {
            Toast toast = Toast.makeText(this, "No existen postes pendientes", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 65, 230);
            toast.show();
        }
        else{
            Intent intent = new Intent(this, PostesPendientes.class);
            startActivity(intent);
            finish();
        }
    }

    public void postesFinalizados(View view){
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mySql = "SELECT * FROM postes WHERE estado = 2;";
        Cursor c = db.rawQuery(mySql, null);
        if(c.getCount() == 0) {
            Toast toast = Toast.makeText(this, "No existen postes finalizados", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 65, 230);
            toast.show();
        }
        else{
            Intent intent = new Intent(this, listaFinalizados.class);
            startActivity(intent);
            finish();
        }
    }

    public void transferirDatos(View view) {
        posttask = new PostTask();
        posttask.execute();
    }

    public class PostTask extends AsyncTask<Void, String, Boolean> {

        public PostTask(){
            //set context variables if required
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                HttpPost post = new HttpPost("http://192.168.1.131:8081/poste/extras/");
                json.put("spid",12);
                json.put("objectid",854587);
                json.put("ncables", 2);
                String fotoposte = "data:image/jpeg;base64,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";
                Log.w("longitud",fotoposte.length()+"");
                json.put("fotoposte", fotoposte);
                json.put("fotocables", "url2");
                StringEntity se = new StringEntity( json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                    /*Checking response */
                if(response!=null){
                    InputStream in = response.getEntity().getContent(); //Get the data in the entity
                }

            } catch(Exception e) {
                Log.w("Error",e.getMessage());
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }
}
