package com.roamtouch.settings;

import com.roamtouch.database.DBConnector;
import com.roamtouch.menu.CircularMenu;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity{

	    private EditText email,username,password;
	    private Button register;
	    private TextView errorText;
	    private DBConnector database;
	    
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);

	    	setContentView(R.layout.register);

	    	SwifteeApplication appState = ((SwifteeApplication)getApplicationContext());
	    	database = appState.getDatabase();



	    	email = (EditText)findViewById(R.id.email);
	    	username = (EditText)findViewById(R.id.username);
	    	password = (EditText)findViewById(R.id.password);

	    	errorText = (TextView)findViewById(R.id.errorText);
	    	
	    	register = (Button)findViewById(R.id.registerButton);
	    	register.setOnClickListener(new OnClickListener(){

	    		public void onClick(View v) {
	    			String emailStr = email.getText().toString();
	    			String usrStr = username.getText().toString();
	    			String passStr = password.getText().toString();
	    			if(emailStr.equalsIgnoreCase("") || emailStr == null){
	    				errorText.setVisibility(View.VISIBLE);
	    				errorText.setText("Enter valid email address");
	    				return;
	    			}
	    			else if(usrStr.equalsIgnoreCase("") || usrStr == null){
	    				errorText.setVisibility(View.VISIBLE);
	    				errorText.setText("You must enter username");
	    				return;
	    			}
	    			else if(passStr.equalsIgnoreCase("") || passStr == null){
	    				errorText.setVisibility(View.VISIBLE);
	    				errorText.setText("You must enter password");
	    				return;
	    			}
	    			database.registerUser(emailStr, usrStr, passStr);
	    			CircularMenu.USER_REGISTERED = true;
	    		}

	    	});
	    }
}
