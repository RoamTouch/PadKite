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
import android.view.View;
import com.roamtouch.swiftee.SwifteeApplication;

public class RingView extends View {	

    private Bitmap bitmap;
    
    Rect tabRect;
    Rect ringRect;
    
    public int scrollX;
    public int scrollY;
     
    int ringArc;
    
    String text;   	
    
    int fillColor;    

    //int tabWidth;
    
    private static final int[] GREEN_COLOR = new int[]{49, 179, 110};
    private static final int[] BLUE_COLOR = new int[]{49, 179, 110};
    
    public int draw;   

    public int input;
    
    int xPosTab;
   	int yPosTab; 
   	
   	int x;
   	int y;
   	int W;
   	int H;
   	
   	int ringWidth;
   	
   	int TYPE;
    
    public RingView(Context context) {
        super(context);       
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.outer_circle);        
    }   

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);       
        
        if (tabRect!=null){ //Carefull, app crashes without
        	
        	xPosTab = tabRect.left - scrollX;        	
        	yPosTab = tabRect.top - scrollY;        	
        }
        
        if (ringRect!=null){ //Carefull, app crashes without
        	
        	ringRect.left 	= ringRect.left - scrollX;
        	ringRect.right 	= ringRect.right + scrollX;
        	ringRect.top 	= ringRect.top - scrollY;
        	ringRect.bottom = ringRect.bottom - scrollY;
        	
        	x = ringRect.left - 5; 
        	y = ringRect.top  - 4; 
        	
        	W = ringRect.width()  + 10;  
        	H = ringRect.height() + 10;       	
        	
        }
	   	
        switch (draw){       
        
	       case SwifteeApplication.DRAW_TAB: // && clear==false){    	   	
	        	finalDrawTab(canvas);
	        	break;    
	        	
	        case SwifteeApplication.DRAW_RING: // && clear==false){ 
	        	finalDrawRing(canvas);        	  	
	        	break;
	        	
	        case SwifteeApplication.DRAW_RING_AND_TAB: // && clear==false){
	        	finalDrawTab(canvas);
	        	finalDrawRing(canvas);     	
	        	
	        case SwifteeApplication.DRAW_NONE:
	        	//NOTHING
	        	break;
	        	
	        default:
	        	break;
        }        
    };  
    
    private void finalDrawTab(Canvas canvas){
    	
    	boolean close;    	
    	int tabWidth;    
    	int rectWidth;
    	int rectCenter;
    	int tabCenter;
    	int tabFinalXPos;
    	int textCenter;
    	int textFinalXPos;
    	
    	Paint pText = paintText();    
    	int tSize = (int) pText.measureText(text);    	
    	tabWidth = (int) (tSize+15);
    	
    	rectWidth = (tabRect.right - scrollX) - xPosTab;
    	rectCenter = (rectWidth/2)+xPosTab;
    	//Center Tab
    	tabCenter = tabWidth / 2;
    	tabFinalXPos = rectCenter - tabCenter;
    	
    	// Center Text
    	textCenter = tSize / 2;
    	textFinalXPos = rectCenter - textCenter;
    	
    	if ( tabWidth < rectWidth){
    		close = false;
    	} else {
    		close = true;
    	}    	
    	
    	Path pathTab = drawTab(tabFinalXPos, yPosTab-13, tabFinalXPos+tabWidth, close);  
    	Paint pSquare = paintTab();
    	canvas.drawPath(pathTab, pSquare);          	
    	
		canvas.drawText(text, textFinalXPos, yPosTab-9, pText);	        	
		pathTab.close();		
		
    }
    
    private void finalDrawRing(Canvas canvas){    	
    	//Path ring = drawRing(ringRect.left-5, ringRect.top-4, ringRect.right-2, ringRect.bottom-ringRect.top+7);
    	Path ring = drawRing(x, y, W, H);
    	Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(fillColor);
        p.setStrokeWidth(ringWidth);
        p.setStyle(Style.STROKE);        
        canvas.drawPath(ring, p);        
    }
      
    public Path drawTab(int x, int y, int right, boolean close){    	
    	
    	Path path = new Path();    	    	
    	path.moveTo(x, y+7);
    	path.lineTo(x, y-5);
    	path.arcTo(new RectF(x,y-12,x+20,y-5),180,90); //A 
    	path.lineTo(30, y-12);    	
    	path.arcTo(new RectF(right-20,y-12,right,y-5),270,90); //B
    	
    	if (close){    		
    		int Ex = right - 5; 
    		int Ey = y + 7;   		
    		path.arcTo(new RectF(right-5, y-5, Ex + 5, Ey),0,90); //D-E arc
            path.lineTo(x, y+7);
    	} else {
    		path.lineTo(right, y+7);       	
    		path.lineTo(x, y+7);
    	}      	
       	return path;  
    }
    
    public Path drawRing(int X, int Y, int W, int H){    	
    	int arc = ringArc;    	
    	int Ax = X + arc;
        int Ay = Y;
        int Bx = X + W - arc;
        int By = Y;
        int Cx = X + W;
        int Cy = Y + arc;
        int Dx = Cx;
        int Dy = (Y + arc) + (H - arc*2);
        int Ex = Bx;
        int Ey = Y + H;        
        int Hy = Ey;
        int Ix = Ax;
        int Iy = Hy;
        int Jx = X;
        int Jy = Dy;
        int Kx = X;
        int Ky = Cy;        
        Path pR = new Path();
        pR.moveTo(Ax,Ay); //A
        pR.lineTo(Bx,By);//B
        pR.arcTo(new RectF(Bx, By, Cx, Cy), 270, 90); //B-C arc
        pR.lineTo(Dx,Dy); //D
        pR.arcTo(new RectF(Dx - arc, Dy, Ex + arc, Ey),0,90); //D-E arc       
        pR.lineTo(Ix, Iy); //H - I        
        pR.arcTo(new RectF(Jx, Jy, Ix, Iy),90,90);// I = J arc
        pR.lineTo(Kx, Ky); //K
        pR.arcTo(new RectF(Ax - arc, Ay, Kx + arc, Ky),180,90); //K - A arc
        pR.lineTo(Ax, Ay); //K 
    	return pR;
    }
    
    private Paint paintTab(){
    	Paint pSpaint = new Paint();
    	pSpaint.setStyle(Paint.Style.FILL);
    	pSpaint.setAntiAlias(false);
    	pSpaint.setColor(fillColor); //fillColor
	    return pSpaint;
    }
    
    private Paint paintText(){
    	Paint pText = new Paint();
		pText.setStyle(Paint.Style.FILL);
		pText.setAntiAlias(false);
		pText.setColor(Color.WHITE);
		pText.setTypeface(Typeface.DEFAULT_BOLD);
		pText.setTextSize(16);
		return pText;
    }
          
    public void setDrawType(int draw) {
		this.draw = draw;
	}
    
    public void setRingWidth(int ringWidth) {
		this.ringWidth = ringWidth;
	}
    
}
 


