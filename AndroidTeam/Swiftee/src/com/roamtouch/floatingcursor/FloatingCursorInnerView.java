package com.roamtouch.floatingcursor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.View;

public class FloatingCursorInnerView extends View {

	private float x = 0;
	private float y = 0;
	private int r = 25;
	//private float radius_scale = 0.3f;
	private final Paint[] mPaints = new Paint[4];
	private final Paint mSmallPaint = new Paint();

	private int paintQuality = 0;
	
	private Bitmap buffer = null;
	private Canvas bufferCanvas = new Canvas();
	//private Drawable mDrawable;
	
	private void init() {
		
		mPaints[0] = new Paint();
		mPaints[0].setAntiAlias(true);
		mPaints[0].setColor(0xFF000000); // 0xFFFF0000
		mPaints[0].setStrokeWidth(6f);
		mPaints[0].setPathEffect(new DashPathEffect(new float[] { 6f, 20 }, 0) ); 
		mPaints[0].setStyle(Paint.Style.STROKE);
		
		mPaints[3] = new Paint();
		mPaints[3].setAntiAlias(true);
		mPaints[3].setColor(0xFFFFFFFF); // 0xFFFF0000
		mPaints[3].setStrokeWidth(6f);
		mPaints[3].setPathEffect(new DashPathEffect(new float[] { 6f, 20 }, 13) ); 
		mPaints[3].setStyle(Paint.Style.STROKE);
		
		mPaints[1] = new Paint();
		mPaints[1].setAntiAlias(true);
		mPaints[1].setColor(0xFFAAAAAA); // 0xFFFF0000
		mPaints[1].setStrokeWidth(2.0f);
		
		mPaints[2] = new Paint();
		mPaints[2].setAntiAlias(true);
		mPaints[2].setColor(0xAAAAAAAA); // 0xFFFF0000
		mPaints[2].setStrokeWidth(2.0f);
		
		mSmallPaint.setAntiAlias(true);
		mSmallPaint.setColor(0xFF000000);
		mSmallPaint.setStrokeWidth(0);
		mSmallPaint.setStyle(Paint.Style.STROKE);
	
		updateBuffer();
	}
	
	public FloatingCursorInnerView(Context context) {
		super(context);
		init();
	}
	
	protected void updateBuffer()
	{
		buffer=Bitmap.createBitmap(4*r, 4*r,Bitmap.Config.ARGB_8888);
		bufferCanvas.setBitmap(buffer);
        bufferCanvas.drawCircle(2*r, 2*r, r, mPaints[paintQuality]);
        bufferCanvas.drawCircle(2*r, 2*r, r, mPaints[3]);
        
        // Inner circle
        //final int r2 = (int) (r * radius_scale+0.5f);
        //bufferCanvas.drawCircle(r, r, r2, mSmallPaint);
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
			updateBuffer();
			invalidate();
		}
	}
	
	protected int getRadius()
	{
		return this.r;
	}
	
	protected void setQuality(int quality)
	{
		paintQuality = quality % 3;
		updateBuffer();
		invalidate();
	}
	
	 @Override
	 protected void onDraw(Canvas canvas) {
	     super.onDraw(canvas);
	     //Toast.makeText(getContext(), "Hello Draw", Toast.LENGTH_SHORT).show();
	     if (buffer != null)
	    	 canvas.drawBitmap(buffer, x-2*r, y-2*r, null);
        //canvas.drawCircle(x, y, r, mPaint);
	 }
}
