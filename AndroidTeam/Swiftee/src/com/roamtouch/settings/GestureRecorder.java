package com.roamtouch.settings;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;

public class GestureRecorder extends Activity {

		private static final float LENGTH_THRESHOLD = 120.0f;

		private Gesture mGesture;
		private Button mDoneButton;
		private GestureOverlayView overlay;
		private String gestureName;
		private int gestureType;
		private boolean isNewBookmark = false;
		private boolean isStoredBookmark = false;
		private boolean isEditable = false;
		private boolean isNew = false;
		
		private String url;
		private EditText t,urlText;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			 getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			 
			gestureName = getIntent().getStringExtra("Gesture_Name");
			gestureType = getIntent().getIntExtra("Gesture_Type", -1);
			isNewBookmark = getIntent().getBooleanExtra("isNewBookmark", false);
			isStoredBookmark =getIntent().getBooleanExtra("isStoredBookmark", false);
			isEditable =getIntent().getBooleanExtra("isEditable", false);
			isNew =getIntent().getBooleanExtra("isNew", false);
			
			url = getIntent().getStringExtra("url");
			
			setContentView(R.layout.create_gesture);

			mDoneButton = (Button)findViewById(R.id.startRecording);

			t = (EditText) findViewById(R.id.gestureText);	
			urlText = (EditText) findViewById(R.id.gestureUrl);	
			if(!BrowserActivity.developerMode && gestureName.length()>12)
				gestureName = gestureName.substring(0, 11);
			t.setText(gestureName);
			if(isStoredBookmark){
				urlText.setVisibility(View.VISIBLE);
				urlText.setText(url);
			}
			if(isStoredBookmark || isEditable || isNewBookmark){
				t.setClickable(true);
				t.setEnabled(true);
				t.setFocusable(true);
			}
			else{
				t.setClickable(false);
				t.setEnabled(false);
				t.setFocusable(false);
				t.setText(BrowserActivity.convertGestureItem(gestureName));
			}
				
			overlay = (GestureOverlayView) findViewById(R.id.gestures_overlay);
			overlay.addOnGestureListener(new GesturesProcessor());

			mDoneButton.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					Button b = (Button) v;
					String s=gestureName;
					if(b.getText().toString().equals("Save Gesture")){
						
						if(isStoredBookmark || isEditable  || isNewBookmark) {
							s = t.getText().toString();
							if(!BrowserActivity.developerMode && s.length()>10)
								s = s.substring(0, 9);
						}
						SwifteeApplication appState = ((SwifteeApplication)getApplicationContext());
						GestureLibrary mLibrary = appState.getGestureLibrary(gestureType);
						
						if(mGesture!=null){
							if(isNewBookmark){
								mLibrary.addGesture(s, mGesture);
								appState.getDatabase().addBookmark(s,url);			
							}			
							else if (isNew)
							{
								mLibrary.addGesture(s, mGesture);								
							}
							else if(isStoredBookmark){								
								mLibrary.removeGesture(gestureName, mLibrary.getGestures(gestureName).get(0));
								mLibrary.addGesture(s, mGesture);
								appState.getDatabase().updateBookmark(s,urlText.getText().toString());								
							}
							else{
								mLibrary.removeGesture(gestureName, mLibrary.getGestures(gestureName).get(0));
								mLibrary.addGesture(s, mGesture);
							}
							
							mLibrary.save();							
						}
						finish();
					}
					else{
						mDoneButton.setEnabled(false);
						overlay.setVisibility(View.VISIBLE);
					}
				}

			});
		}

		@Override
		protected void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);

			if (mGesture != null) {
				outState.putParcelable("gesture", mGesture);
			}
		}

		@Override
		protected void onRestoreInstanceState(Bundle savedInstanceState) {
			super.onRestoreInstanceState(savedInstanceState);

			mGesture = savedInstanceState.getParcelable("gesture");
			if (mGesture != null) {
				final GestureOverlayView overlay =
					(GestureOverlayView) findViewById(R.id.gestures_overlay);
				overlay.post(new Runnable() {
					public void run() {
						overlay.setGesture(mGesture);
					}
				});

				mDoneButton.setEnabled(true);
				mDoneButton.setText("Save Gesture");
			}
		}


		public void cancelGesture(View v) {
			setResult(RESULT_CANCELED);
			finish();
		}

		private class GesturesProcessor implements GestureOverlayView.OnGestureListener {
			public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
				mGesture = null;
			}

			public void onGesture(GestureOverlayView overlay, MotionEvent event) {
			}

			public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
				mGesture = overlay.getGesture();
				if (mGesture.getLength() < LENGTH_THRESHOLD) {
					overlay.clear(false);
				}
				mDoneButton.setEnabled(true);
				mDoneButton.setText("Save Gesture");
			}

			public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
			}
		}
}
