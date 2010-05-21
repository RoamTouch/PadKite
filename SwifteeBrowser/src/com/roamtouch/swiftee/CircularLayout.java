package com.roamtouch.swiftee;

import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;


public class CircularLayout extends ViewGroup {

	//Temporary parameters
    private int mNumColumns = 2;
    private int mNumRows = 2;
    
    //Center of the circular menu
    private int a;
    private int b;
    
    //radius
    private int outR;
    private int inR;
    
    Paint circlePaint;
	
	
	public CircularLayout(Context context) {
		this(context, null);
	}

    public CircularLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        //This is so that we can create a stylable CircularLayout
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GridLayout, defStyle, 0);
//
//        mNumColumns = a.getInt(R.styleable.GridLayout_numColumns, 1);
//        mNumRows = a.getInt(R.styleable.GridLayout_numRows, 1);

//        a.recycle();
        
        
        
        init();
        
    }
    
	private void init() {
		
		circlePaint = new Paint();
		circlePaint.setARGB(100, 75, 75, 75);
		circlePaint.setAntiAlias(true);
		circlePaint.setStrokeWidth(2.0f);			
				
	}   
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);
        
        a = widthSpecSize /2;
        
        getChildAt(0).getWidth();

        Log.i("widthSpecSize:", ""+widthSpecSize);        

        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
        
        b = heightSpecSize /2;
        
        Log.i("heightSpecSize:", ""+heightSpecSize);        

        // for now we take the width of the view as the radius 
        outR = widthSpecSize/2;
        // we substracht the width of the icon to place the children with in the radius
        inR = outR - (getChildAt(0).getWidth()/2) - getPaddingLeft();
        
        if (widthSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            throw new RuntimeException("CircularLayout cannot have UNSPECIFIED dimensions");
        }

        final int width = widthSpecSize - getPaddingLeft() - getPaddingRight();
        final int height = heightSpecSize - getPaddingTop() - getPaddingBottom();

        final int columnWidth = width / mNumColumns;
        final int rowHeight = height / mNumRows;

        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            int childWidthSpec = MeasureSpec.makeMeasureSpec(columnWidth, MeasureSpec.AT_MOST);
            int childheightSpec = MeasureSpec.makeMeasureSpec(columnWidth, MeasureSpec.AT_MOST);

            child.measure(childWidthSpec, childheightSpec);
        }

        setMeasuredDimension(widthSpecSize, heightSpecSize);
	}
	
	
	@Override
	protected void dispatchDraw(Canvas canvas) {

		canvas.drawCircle(a, b, outR, circlePaint);

		super.dispatchDraw(canvas);
	}
	
	@Override
	protected void onLayout(boolean changed, int vl, int vt, int vr, int vb) {
		
		//center of the circle
		
        final int count = getChildCount();
        double t = 0;
        int x = 0, y = 0;

    	t = 360 / count;
        
    	Log.i("(a,b,r):" , "("+ a +","+ b +","+ outR +")");    	

    	//TODO:  TO FIX!  When a top an bottom icon line up in one column
    	//		         like with 4 icons, the bottom disappears.
    	
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {            
            	// Calc coordinates around the circle at the center of cart. system
            	
            	double angle = i*t;
            	x = (int) (inR * Math.cos(Math.toRadians(angle)));
            	y = (int) (inR * Math.sin(Math.toRadians(angle)));
            	
//            	Log.i("pos"+i+"@" + i*t + ":" , "("+ x +","+ y +")");
            	
            	// Sum the result to actual center, inverting y in Q1 & Q4
            	if (angle<=90 || angle >270) {
                	x = a + x;
                	y = b - y;			
            	} else {
                	x = a + x;
                	y = b + y;	
            	}

                final int measuredW = child.getMeasuredWidth();
                final int measuredH = child.getMeasuredHeight();

//            	Log.i("measured (W,H):" , "("+ measuredW +","+ measuredH +")");
 
                final int childLeft = x - (measuredW/2);
                final int childTop = y - (measuredH/2);
                final int lb = childLeft + measuredW;
                final int rb = childTop + measuredH;
               
                child.layout(childLeft, childTop, lb, rb);

//            	Log.i("fin pos"+i+"@" + i*t + ":" , "("+ childLeft +","+ childTop 
//            			+","+ lb +","+ rb +")");
                
            }
        }	
	}
}

