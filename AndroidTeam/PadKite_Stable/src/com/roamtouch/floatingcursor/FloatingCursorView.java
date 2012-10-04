package com.roamtouch.floatingcursor;

import java.util.Hashtable;

import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;

import com.roamtouch.floatingcursor.FloatingCursorInnerView;;

public class FloatingCursorView extends View {
	
	public static final String TAG = "FloatingCursorView";

    private static final float ROTATE_FROM = -360.0f;
    private static final float ROTATE_TO = 360.0f;

    private float x = 0;
    private float y = 0;
    private int rad = 25;
    
    private static final float smallFact = 0.5f;
    
    private int mProgress;

    private Bitmap bitmap;

    private Rect rect;

    private AnimationSet set;
    private RotateAnimation ra;
    private ScaleAnimation sa;
    private TranslateAnimation ta;

    private boolean isLoadingAnimationShown = false;
    
    private boolean isSmall = false;
    
    private FloatingCursorInnerView innerCircle;
    
    public double dD;
    
    private Hashtable<Integer, int[]> colorTable = new Hashtable();
    private Hashtable<Integer, int[]> colorTableBorder = new Hashtable();
    
    //int n = 40;

    public FloatingCursorView(Context context) {
        super(context);
        //bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.outer_circle);        
        innerCircle = new FloatingCursorInnerView(context);
        
        colorTable.put(1, SwifteeApplication.CIRCLE_PANTONE_801C);
        colorTable.put(2, SwifteeApplication.CIRCLE_PANTONE_802C);
        colorTable.put(3, SwifteeApplication.CIRCLE_PANTONE_803C);
        colorTable.put(4, SwifteeApplication.CIRCLE_PANTONE_804C);
        colorTable.put(5, SwifteeApplication.CIRCLE_PANTONE_805C);
        colorTable.put(6, SwifteeApplication.CIRCLE_PANTONE_806C);
        colorTable.put(7, SwifteeApplication.CIRCLE_PANTONE_807C);
        colorTable.put(8, SwifteeApplication.CIRCLE_PANTONE_808C);
        colorTable.put(9, SwifteeApplication.CIRCLE_PANTONE_809C);
        colorTable.put(10, SwifteeApplication.CIRCLE_PANTONE_810C);     
        colorTable.put(11, SwifteeApplication.CIRCLE_PANTONE_811C);
        
        colorTableBorder.put(1, SwifteeApplication.CIRCLE_PANTONE_173C);
        colorTableBorder.put(2, SwifteeApplication.CIRCLE_PANTONE_362C);
        colorTableBorder.put(3, SwifteeApplication.CIRCLE_PANTONE_456C);
        colorTableBorder.put(4, SwifteeApplication.CIRCLE_PANTONE_154C);
        colorTableBorder.put(5, SwifteeApplication.CIRCLE_PANTONE_429C);
        colorTableBorder.put(6, SwifteeApplication.CIRCLE_PANTONE_235C);
        colorTableBorder.put(7, SwifteeApplication.CIRCLE_PANTONE_2425C);
        colorTableBorder.put(8, SwifteeApplication.CIRCLE_PANTONE_343C);
        colorTableBorder.put(9, SwifteeApplication.CIRCLE_PANTONE_392C);
        colorTableBorder.put(10, SwifteeApplication.CIRCLE_PANTONE_1535C);
        colorTableBorder.put(11, SwifteeApplication.CIRCLE_PANTONE_7526C);     
    
        
    }

    protected void setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
        
        //Make sure animation follows new co-ordiantes, just restart animation.
        if (isLoadingAnimationShown) {
        	this.clearAnimation();
        	// Force isSmall to be false otherwise we may 
        	// fail to call startScaleDownAndRotateAnimation.
        	// See ticket #506, in which animations are cleared but
        	// startScaleDownAndRotateAnimation is not really invoked.
        	this.isSmall = false;
        	this.startScaleDownAndRotateAnimation(1000);
        }
        
        invalidate();
    }

    protected void setRadius(int r)
    {
        if (this.rad != r) {
            this.rad = r;

            invalidate();
        }
    }

    public int getRadius()
    {
        return this.rad;
    }  
    
    public void setInvalidate(){
    	invalidate();
    }
    
    int countColor=1;
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);       
        
        if (SwifteeApplication.getFCVisible()){        	
        
	        dD = SwifteeApplication.getFCDotDiam();       
	        rect = new Rect((int)x-rad,(int)y-rad,(int)x+rad,(int)y+rad);
	        
	        Paint pF = new Paint();   
	        pF.setStyle(Paint.Style.FILL);
	        pF.setAntiAlias(true);
	        
	        Paint lF = new Paint();   
	        lF.setStyle(Paint.Style.STROKE);        
	        lF.setStrokeWidth(1);
	        lF.setColor(0xFFF5CD31);	          
	        
	        for (int i = 0; i < SwifteeApplication.getFCAmountOfDots(); i++) {
	        	
	            double t = 2 * Math.PI * i / SwifteeApplication.getFCAmountOfDots();
	            int x = (int) Math.round(rect.exactCenterX() + rad * Math.cos(t));
	            int y = (int) Math.round(rect.exactCenterY() + rad * Math.sin(t));
	            
	            if (isOdd(i)){
	            	
	            	if (SwifteeApplication.getFCDotDiam()==4){
	            	
	            		pF.setColor(0xFF000000);
		            	canvas.drawCircle(x, y, (float) dD, pF);
		            	pF.setColor(0xFF616161);
		            	canvas.drawCircle(x, y, (float) (dD-1), pF);
		            	pF.setColor(0xFFB3B3B3);
		            	canvas.drawCircle(x, y, (float) (dD-2), pF);
	            	
	            	} else {
	            	        	
		            	if (countColor <= 11){       		          
			            			            	
			            	int[] colB = colorTableBorder.get(countColor);
			            	int rB = colB[0];
			        		int gB = colB[1];
			        		int bB = colB[2];    		
			            	
			            	pF.setColor(Color.rgb(rB, gB, bB));	            	
			            	canvas.drawCircle(x, y, (float) dD, pF);
			            	
			          		int[] colT = colorTable.get(countColor);
		            		int rT = colT[0];
			        		int gT = colT[1];
			        		int bT = colT[2];    		
			        		
			            	pF.setColor(Color.rgb(rT, gT, bT));
			            	canvas.drawCircle(x, y, (float) (dD-1), pF);	            	
			            	
			            	countColor++;
			            	
		            	} else {
		            		countColor=1;
		            		pF.setColor(0xFFB3B3B3);
			            	canvas.drawCircle(x, y, (float) (dD-2), pF);
		            	}      	
		            	
		            	countColor++;
		            	
		            	if (i==SwifteeApplication.getFCAmountOfDots()-1){
		            		countColor=1;
		            	}		            
	            	}       	
	            	
	            } else {	
	            	
	            	lF.setColor(0xFF000000);
	            	Log.v("out", "i: "+i);	            	
	            	canvas.drawCircle(x, y, (float) (dD-1.5), lF);
	            	pF.setColor(0xFFFFFFFF);
	            	canvas.drawCircle(x, y, (float) (dD-2), pF);        
	            	
	            }
	                    
	        }  
	    }  
        
    }   
    
    private boolean isOdd( int val ) 
    {
    	return (val & 0x01) != 0; 
    }
   

    protected void startRotateAnimation() {
    	ra = null;
    	this.clearAnimation();
    	ra = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	ra.setDuration((long) 1000); // 1 sec/rotation @ 0%
    	ra.setRepeatCount(Animation.INFINITE);
    	ra.setInterpolator(new LinearInterpolator());
        this.startAnimation(ra);
    }

    protected void startScaleDownAnimation() {
    	if (isSmall)
    		return;
    	
    	sa = null; //Clear previous reference
    	sa = new ScaleAnimation(1.0f,smallFact,1.0f,smallFact,
    			Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	sa.setDuration((long) 1000);
    	sa.setInterpolator(new LinearInterpolator());
        this.startAnimation(sa);
        isSmall = true;
    }
    
    protected void startScaleUpAnimation(long duration) {
    	if (!isSmall)
    		return;
    	
    	sa = null; //Clear previous reference
    	sa = new ScaleAnimation(smallFact,1f,smallFact,1f,
    			Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	sa.setDuration(duration);
    	sa.setInterpolator(new LinearInterpolator());
        this.startAnimation(sa);
        isSmall = false;
    }
    
    // Combined scale-down and rotate operation
    protected void startScaleDownAndRotateAnimation(long duration) {
    	
    	if (isSmall)
    		return;
    	
    	this.clearAnimation();
    	isLoadingAnimationShown = true;
    	
    	ra = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	ra.setDuration((long) 1000); // 1 sec/rotation @ 0% loading
    	ra.setRepeatCount(Animation.INFINITE);
    	
    	sa = new ScaleAnimation(1.0f,smallFact,1.0f,smallFact,
    			Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	sa.setDuration((long) duration);
    	
    	set = new AnimationSet(true);
    	set.setInterpolator(new LinearInterpolator());
    	set.addAnimation(ra);
    	if (isSmall) {
    		sa = null; // trigger gc
    	}
    	else {
    		set.addAnimation(sa);
    	}
        this.startAnimation(set);
        isSmall = true;
    }

    protected void startScaleUpAndRotateAnimation(long duration) {
 
       	if (!isSmall)
    		return;
 
    	this.clearAnimation();
    	isLoadingAnimationShown = true;
    	
    	ra = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	ra.setDuration((long) 1000); // 1 sec/rotation @ 0% loading
    	ra.setRepeatCount(Animation.INFINITE);
    	
    	sa = new ScaleAnimation(smallFact,1f,smallFact,1f,
    			Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	sa.setDuration(duration);
    	
    	set = new AnimationSet(true);
    	set.setInterpolator(new LinearInterpolator());
    	set.addAnimation(ra);
    	if (isSmall) {
    		sa = null; // trigger gc
    	}
    	else {
    		set.addAnimation(sa);
    	}
        this.startAnimation(set);
        isSmall = false;
    }

    
    
    public void setProgress(int progress){
    	Log.v("progress","Progress is " + progress);
    	mProgress = progress;
    	//Commented out for now for performance issues.
    	// ra = null;
    	// this.clearAnimation();
    	// ra = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF,
        //         0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	// //Speed of animationdepends on % loaded  - 0% fastest (1sec/rotation), every 10% adds 1 sec. 
    	// ra.setDuration((long) (mProgress/10 + 1) * 1000);
    	// ra.setRepeatCount(Animation.INFINITE);
    	// ra.setInterpolator(new LinearInterpolator());
    	// this.startAnimation(ra);
	}
    
    public Handler handler = new Handler();
 
    public Runnable runnable = new Runnable() {
    	public void run() {
            FloatingCursorView.this.startKiteAnimation();

    	}
    };
    
    
    public void startKiteAnimation() {
    	
    	if (isLoadingAnimationShown)
    		return;

        this.clearAnimation();
        //Drift to be a random number between -20 and 20
        float driftX = -20 + (float) Math.random() * 40;
        float driftY = -20 + (float) Math.random() * 40;

        if (driftX < 0 && driftX > -5)
        	driftX = -5;

        if (driftX >= 0 && driftX < 5)
        	driftX = -5;

        if (driftY < 0 && driftY > -5)
        	driftY = -5;

        if (driftY >= 0 && driftY < 5)
        	driftY = -5;
        
        ta = new TranslateAnimation(0, driftX, 0, driftY);
        ta.setDuration((long) 5000);
        ta.setInterpolator(new AccelerateDecelerateInterpolator());
        
        final float driftXC = driftX;
        final float driftYC = driftY;
        
        ta.setAnimationListener(new AnimationListener(){

            public void onAnimationEnd(Animation arg0) {
            	   TranslateAnimation ta2 = new TranslateAnimation(driftXC, 0, driftYC, 0);
                   ta2.setDuration((long) 5000);
                   ta2.setInterpolator(new AccelerateDecelerateInterpolator());      
                   ta2.setAnimationListener(new AnimationListener() {

                	   public void onAnimationEnd(Animation arg0) {

                		   handler.postDelayed(runnable, (int) (1000+Math.random() * 5000));
                	   }
                	   
                	   public void onAnimationRepeat(Animation arg0) {

                       }

                       public void onAnimationStart(Animation arg0) {

                       }
                	   
                   });
                   FloatingCursorView.this.startAnimation(ta2);
            }

            public void onAnimationRepeat(Animation arg0) {

            }

            public void onAnimationStart(Animation arg0) {

            }

        });
        this.startAnimation(ta);
    }
    	
	public void stopAllAnimation() {
		this.clearAnimation();
		isLoadingAnimationShown = false;
	}	

 
}
