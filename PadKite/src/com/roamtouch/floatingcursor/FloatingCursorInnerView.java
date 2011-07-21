package com.roamtouch.floatingcursor;

import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.graphics.Rect;
import android.view.View;

public class FloatingCursorInnerView extends View {

	private float x = 0;
	private float y = 0;
	private int r = 25;
	
	private Bitmap bitmap;
	private Rect rect;

	private View pView;
	
	int vHeight;
	int vWidth;
	
	 Paint paint = new Paint();
	
	public FloatingCursorInnerView(Context context, View parentView) {
		super(context);
		pView = parentView;
		//bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.inner_circle); 
	}
	
	protected void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
		invalidate();
	}

	protected void setRadius(int r)
	{
		if (this.r != r) {
			this.r = r;
			invalidate();
		}
	}
	
	protected int getRadius()
	{
		return this.r;
	}
	
	 @Override
	 protected void onDraw(Canvas canvas) {
	     super.onDraw(canvas);
	     //Toast.makeText(getContext(), "Hello Draw", Toast.LENGTH_SHORT).show();
	     
	     //rect = new Rect(0,0,SwifteeApplication.getWidth(), SwifteeApplication.getHeight());
	     
	     rect = new Rect((int)x-r,(int)y-r,(int)x+r,(int)y+r);  
	     
	     //if (bitmap != null) 
	    	//paint.setAntiAlias(true);     
	     	//paint.setColor(Color.rgb(231, 231, 231));
            //canvas.drawCircle(x, y, r-1, paint);
	 }
}

