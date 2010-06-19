package com.roamtouch.swiftee;

import java.util.ArrayList;
import java.util.Set;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

public class TutorArea extends LinearLayout{

	private ArrayList<Gesture> gestureList;
	public TutorArea(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutParams params=new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		for(int i=0;i<9;i++){
			Button b=new Button(context);
			b.setLayoutParams(params);
			b.setText("Search");
			b.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.icon, 0, 0);
			this.addView(b);
		}
		
	}
	public void setGestureList(ArrayList<Gesture> list) {
		this.gestureList = list;
	}
	public ArrayList<Gesture> getGestureList() {
		return gestureList;
	}

}
