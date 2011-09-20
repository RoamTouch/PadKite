 package com.roamtouch.visuals;


 import com.roamtouch.swiftee.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.BrowserActivity;


public class PointerHolder extends FrameLayout {
			
		// private Bitmap bitmap;
		PointerHolderView pH;		
		ImageView pointer;
     
		public PointerHolder(Context context, AttributeSet attrs) {
			super(context, attrs);
			init(context);
		}
		
		private void init(Context context) {
			pH = new PointerHolderView(getContext());		
			addView(pH);
		}

	    @Override
	    protected void onDraw(Canvas canvas) {
	        super.onDraw(canvas);       
	      
	    };   
	    
	    public ImageView getPointer() {
			return pointer;
		}
	
	
}
