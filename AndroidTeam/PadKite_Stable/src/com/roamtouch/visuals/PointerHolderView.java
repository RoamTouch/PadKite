package com.roamtouch.visuals;

import com.roamtouch.swiftee.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import com.roamtouch.swiftee.SwifteeApplication;

public class PointerHolderView extends View {	

    private Bitmap bitmap;
       
    public PointerHolderView(Context context) {
        super(context);       
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.outer_circle);        
    }   

    @Override
    protected void onDraw(Canvas canvas) {    	
        super.onDraw(canvas);           
    };  
    
    
}
 