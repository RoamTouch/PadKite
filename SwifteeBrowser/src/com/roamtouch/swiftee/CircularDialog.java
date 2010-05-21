package com.roamtouch.swiftee;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CircularDialog extends Dialog implements OnClickListener {

	public CircularDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		setContentView(R.layout.circular_menu);
	}

	public void onClick(View view) {
		
//		switch (view.getId()) {
//		case R.id.okButton:
//		break;
//		case R.id.cancelButton:
//			cancel();
//		break;
		}

	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
		Log.i("Clicked:", ""+which);
		
		Button whichButton = (Button) findViewById(which);
        Toast.makeText(this.getContext(), "You Clicked " + whichButton.getTag(), Toast.LENGTH_SHORT).show();
		
		dismiss();
		
	}

}