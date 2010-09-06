package com.roamtouch.view;

import java.util.ArrayList;
import java.util.Set;

import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.gestures.Gesture;
import com.roamtouch.gestures.GestureLibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class TutorArea extends LinearLayout implements OnClickListener {

	private GestureLibrary mLibrary;
	private Context mContext;
	private BrowserActivity parent;
	private int gestureCount;
	private Object str[];
	
	public TutorArea(Context context, AttributeSet attrs) {
		super(context, attrs);	
		mContext = context;
	}
	public void setGestureLibrary(GestureLibrary l) {
		mLibrary = l;
		initView();
	}
	private void initView(){
		this.removeAllViews();
		Set<String> s=mLibrary.getGestureEntries();
		str = s.toArray();
		gestureCount = str.length;
		
		LayoutParams params=new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		for(int i=0;i<gestureCount;i++){
			Button b=new Button(mContext);
			b.setId(i);
			b.setLayoutParams(params);
			b.setText(str[i].toString());
			ArrayList<Gesture> list = mLibrary.getGestures(str[i].toString());
			Bitmap bit = list.get(0).toBitmap(50, 50, 0, Color.BLACK);
			BitmapDrawable d = new BitmapDrawable(bit);
			b.setCompoundDrawablesWithIntrinsicBounds(null,d, null, null);
			b.setOnClickListener(this);
			this.addView(b);
		}
	}
	public void onClick(View v) {
			ArrayList<Gesture> list = mLibrary.getGestures(str[v.getId()].toString());			
			parent.drawGestureToEducate(list);
			
	}
	public void setParent(BrowserActivity parent) {
		this.parent = parent;
	}
	
}
