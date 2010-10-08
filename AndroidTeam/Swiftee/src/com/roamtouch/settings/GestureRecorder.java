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
		private String url;
		private EditText t;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			 getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			 
			gestureName = getIntent().getStringExtra("Gesture_Name");
			gestureType = getIntent().getIntExtra("Gesture_Type", -1);
			isNewBookmark = getIntent().getBooleanExtra("isNewBookmark", false);
			url = getIntent().getStringExtra("url");
			
			setContentView(R.layout.create_gesture);

			mDoneButton = (Button)findViewById(R.id.startRecording);

			t = (EditText) findViewById(R.id.gestureText);		
			t.setText(gestureName);
			if(gestureName.equals(""))
				t.setHint("Enter name for this bookmark");
			else{
				t.setClickable(false);
				t.setEnabled(false);
				t.setFocusable(false);
			}
			overlay = (GestureOverlayView) findViewById(R.id.gestures_overlay);
			overlay.addOnGestureListener(new GesturesProcessor());

			mDoneButton.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					Button b = (Button) v;
					if(b.getText().toString().equals("Save Gesture")){
						if(gestureName.equals(""))
							gestureName = t.getText().toString();
						SwifteeApplication appState = ((SwifteeApplication)getApplicationContext());
						GestureLibrary mLibrary = appState.getGestureLibrary(gestureType);
						
						if(mGesture!=null){
							if(!isNewBookmark)
								mLibrary.removeGesture(gestureName, mLibrary.getGestures(gestureName).get(0));
							else{
								appState.getDatabase().addBookmark(gestureName,url);
							}
							String s = t.getText().toString();
							mLibrary.addGesture(s, mGesture);
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
