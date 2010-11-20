package com.roamtouch.floatingcursor;

import com.roamtouch.swiftee.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
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

public class FloatingCursorView extends View{
	
	public static final String TAG = "FloatingCursorView";

    private static final float ROTATE_FROM = -360.0f;
    private static final float ROTATE_TO = 360.0f;

    private float x = 0;
    private float y = 0;
    private int r = 25;
    private int mProgress;

    private Bitmap bitmap;

    private Rect rect;

    private AnimationSet set;
    private RotateAnimation ra;
    private ScaleAnimation sa;
    private TranslateAnimation ta;

    private boolean isLoadingAnimationShown = false;

    public FloatingCursorView(Context context) {
        super(context);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.outer_circle);
    }


    protected void setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
        
        //Make sure animation follows new co-ordiantes, just restart animation.
        if (isLoadingAnimationShown) {
        	this.clearAnimation();
        	this.startScaleDownAndRotateAnimation();
        }
        
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
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, null, rect, null);
        }
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
    	sa = null; //Clear previous reference
    	sa = new ScaleAnimation(1.0f,0.3f,1.0f,0.3f,
    			Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	sa.setDuration((long) 1000);
    	sa.setInterpolator(new LinearInterpolator());
        this.startAnimation(sa);
    }
    
    protected void startScaleUpAnimation() {
    	sa = null; //Clear previous reference
    	sa = new ScaleAnimation(0.3f,1f,0.3f,1f,
    			Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	sa.setDuration((long) 1000);
    	sa.setInterpolator(new LinearInterpolator());
        this.startAnimation(sa);
    }
    
    // Combined scale-down and rotate operation
    protected void startScaleDownAndRotateAnimation() {
    	
    	this.clearAnimation();
    	isLoadingAnimationShown = true;
    	
    	ra = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	ra.setDuration((long) 1000); // 1 sec/rotation @ 0% loading
    	ra.setRepeatCount(Animation.INFINITE);
    	
    	sa = new ScaleAnimation(1.0f,0.3f,1.0f,0.3f,
    			Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	sa.setDuration((long) 1000);
    	
    	set = new AnimationSet(true);
    	set.setInterpolator(new LinearInterpolator());
    	set.addAnimation(ra);
        set.addAnimation(sa);
        this.startAnimation(set);
    }
    
    public void setProgress(int progress){
    	Log.v(TAG,"Progress is " + progress);
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
