package com.roamtouch.menu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

public class MenuBGView extends View {

	private float x = 0;
	private float y = 0;
	private int r = 25;
	
	private Bitmap bitmap;
	
	private Rect rect;
	
	public MenuBGView(Context context) {
		super(context);
		
		bitmap = BitmapFactory.decodeFile("/sdcard/Swiftee/Default Theme/circle.png");
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

			rect = new Rect((int)0,(int)0,(int)2*r,(int)2*r);
	     if (bitmap != null)
	    //	 canvas.drawBitmap(bitmap, x-r, y-r, null);
	     canvas.drawBitmap(bitmap, null, rect, null);
        //canvas.drawCircle(x, y, r, mPaint);
	 }

}
