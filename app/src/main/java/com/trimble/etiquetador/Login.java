package com.trimble.etiquetador;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Login extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	protected void verifyUser(View view) {
		String user = ((TextView) findViewById(R.id.user)).getText().toString();
		String password = ((TextView) findViewById(R.id.password)).getText().toString();
		if(user.equals("cnel") && password.equals("123")){
			Intent intent = new Intent(this, Menu.class);
			startActivity(intent);
		}
	}
}
