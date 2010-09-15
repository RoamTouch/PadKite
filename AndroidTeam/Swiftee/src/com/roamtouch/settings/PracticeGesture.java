package com.roamtouch.settings;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.view.TutorArea;

public class PracticeGesture extends Activity {

		private static final float LENGTH_THRESHOLD = 120.0f;

		private Gesture mGesture;
		private Button mTypeButton;
		private GestureOverlayView overlay;
		private String gestureName;
		private int gestureType;
		private TutorArea tArea;
		private GestureLibrary mLibrary;
		private SwifteeApplication appState;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			gestureName = getIntent().getStringExtra("Gesture_Name");
			gestureType = getIntent().getIntExtra("Gesture_Type", -1);
			
			setContentView(R.layout.practice_gesture);

			mTypeButton = (Button)findViewById(R.id.selectType);

			mTypeButton.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					Intent i = new Intent(PracticeGesture.this,GestureEditor.class);
					i.putExtra("isForPracticeGesture", true);
					PracticeGesture.this.startActivityForResult(i, 1);
				}
				
			});
			TextView t = (TextView) findViewById(R.id.gestureTitle);
			t.setText("Cursor text gesture");
			
			overlay = (GestureOverlayView) findViewById(R.id.gestures_overlay);
			overlay.addOnGestureListener(new GesturesProcessor());

			appState = ((SwifteeApplication)getApplicationContext());
			mLibrary = appState.getGestureLibrary(SwifteeApplication.CURSOR_TEXT_GESTURE);
	    	
	    	HorizontalScrollView mTutor = (HorizontalScrollView) findViewById(R.id.gestureScrollView);
					
	    	tArea=(TutorArea)mTutor.getChildAt(0);
			tArea.setGestureLibrary(mLibrary);
			tArea.setParent(this);
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
		
			}

			public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
			}
		}
		
		@Override
		 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if(resultCode == 1){
				int i = data.getIntExtra("Gesture_Category", SwifteeApplication.CURSOR_TEXT_GESTURE);
				mLibrary = appState.getGestureLibrary(i);
				tArea.setGestureLibrary(mLibrary);
			}
		}
		
}
