package com.roamtouch.floatingcursor;

import com.roamtouch.swiftee.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

public class FloatingCursorView extends View {

	private float x = 0;
	private float y = 0;
	private int r = 25;
	
	private Bitmap bitmap;
	
	private Rect rect;
	
	public FloatingCursorView(Context context) {
		super(context);
		
		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.outer_circle); 

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

			rect = new Rect((int)x-r,(int)y-r,(int)x+r,(int)y+r);
	     if (bitmap != null)
	    //	 canvas.drawBitmap(bitmap, x-r, y-r, null);
	     canvas.drawBitmap(bitmap, null, rect, null);
        //canvas.drawCircle(x, y, r, mPaint);
	 }
}
