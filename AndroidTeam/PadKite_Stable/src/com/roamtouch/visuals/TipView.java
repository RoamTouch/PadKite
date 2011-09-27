package com.roamtouch.visuals;

import java.util.Timer;

import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;

import android.view.View;


public class TipView extends View {	

    private Bitmap bitmap;      
    
    public int draw;
    
    public int xPos;
    public int yPos;
    public int width;
    public int height;    
    
    public String[] tipText;
    
    int arc = 20;
    
    private Path cP;
    public int textLines;
    int lineHeight = 30;
    
    float xPoint;
	float yPoint;
    
    public TipView(Context context) {
        super(context);       
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.outer_circle);  
    }   

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (draw != SwifteeApplication.DRAW_NONE){
        	Paint pT = paintText();	 
        	 
        	
		    switch (textLines){
		    
	    		case 1:	    	   			
	    			width = (int) pT.measureText(tipText[0].toString());  
	    			comment(canvas, lineHeight);
	    	   		canvas.drawText(tipText[0].toString(), xPos+(width/2)+15, yPos-5, pT);	
	    	   		break;
	    	   		
	    	   	case 2:
	    	   		comment(canvas, lineHeight*2);
	    	   		canvas.drawText(tipText[0].toString(), xPos+(width/2)+15, yPos-5, pT);
	    	   		canvas.drawText(tipText[1].toString(), xPos+(width/2)+15, yPos+20, pT);
	    	   		break;
	    	   		
	    	   	case 3:
	    	   		comment(canvas, lineHeight*3);
	    	   		canvas.drawText(tipText[0].toString(), xPos+(width/2)+15, yPos-5, pT);
	    	   		canvas.drawText(tipText[1].toString(), xPos+(width/2)+15, yPos+20, pT);
	    	   		canvas.drawText(tipText[2].toString(), xPos+(width/2)+15, yPos+45, pT);
	    	   		break;   	   
		    }
		    
        }  	   
    }
    
    private void comment (Canvas canvas, int commentHeight){   
    	//Check for cuadrant
    	
    	 
       Path cP = drawComment(xPos,yPos-40, width+30, commentHeight+20, draw);  
	   Paint pF = getPaintFill();
	   canvas.drawPath(cP, pF);
	   Paint pS = getPaintStroke();	    	   
	   canvas.drawPath(cP, pS); 
	}
    
    private Paint getPaintFill(){
    	 Paint p = new Paint();
         p.setAntiAlias(true);
         p.setColor(0xffffffff);
         p.setStyle(Style.FILL);
         p.setShadowLayer(5.5f, 6.0f, 6.0f, Color.LTGRAY);
         return p;
    }
    
    public Paint getPaintStroke(){
    	Paint p = new Paint();    	
        p.setColor(0xff000000);
        p.setStyle(Style.STROKE);
        p.setStrokeWidth(2);     
        return p;
    }
    
    private Paint paintText(){
    	Paint pText = new Paint();
		pText.setStyle(Paint.Style.FILL);
		pText.setTextAlign(Paint.Align.CENTER);
		pText.setAntiAlias(false);		
		int [] textColor = SwifteeApplication.DARK_GRAY;		
		pText.setARGB(100, textColor[0], textColor[1], textColor[2]);
		//pText.setColor(Color.BLACK);
		pText.setTypeface(Typeface.DEFAULT_BOLD);
		pText.setTextSize(22);
		pText.setAntiAlias(true);
		return pText;
    }
    
    int Aax = 0;		        
	int Aay = 0;		        
	int Abx = 0;
	int Aby = 0;		        
	int Acx = 0;	
	int Acy = 0;
    
	int Fx = 0;
    int Fy = 0;		        
    int Gx = 0;
    int Gy = 0;		        
    int Hx = 0;		        
    int Hy = 0;
    
    
    
    public Path drawComment(int X, int Y, int W, int H, int type){
    	
    	cP = new Path();
    	
    	//UPPER left ARC    			
	 	int Ax = X + arc;
        int Ay = Y;		        
        cP.moveTo(Ax,Ay); //A    
        
        Aay = Ay;
        Aby = Ay - 12;
        Acy = Ay;
            	
        //UPPER TRIANGLES
        if ( type == SwifteeApplication.SET_TIP_TO_LEFT_UP ) {
        	Aax = X + 20;	    		        
      	    Abx = X + 30;      	    		        
      	    Acx = X + 40;      	    
      	    cP.lineTo(Aax, Aay); //A -Aa    
      	  	cP.lineTo(Abx, Aby); //Aa-Ab
      	  	//cP.lineTo(xPoint, yPoint); //Aa-Ab
      	  	cP.lineTo(Acx, Acy); //Ab-Ac 
        } else if ( type == SwifteeApplication.SET_TIP_TO_CENTER_UP ){
        	Aax = X + W/2 - 10;    	        
      	    Abx = X + W/2;      	            
      	    Acx = X + W/2 + 10;     	    
      	    cP.lineTo(Aax, Aay); //A -Aa    
    	  	cP.lineTo(Abx, Aby); //Aa-Ab
    	  	//cP.lineTo(xPoint, yPoint); //Aa-Ab
    	  	cP.lineTo(Acx, Acy); //Ab-Ac 
        } else if ( type == SwifteeApplication.SET_TIP_TO_CENTER_UP ){
        	Aax = W - 40;	    	        
      	    Abx = W - 30;      	    	        
      	    Acx = W - 20;	      	    
      	    cP.lineTo(Aax, Aay); //A -Aa    
    	  	cP.lineTo(Abx, Aby); //Aa-Ab
    	  	//cP.lineTo(xPoint, yPoint); //Aa-Ab
    	  	cP.lineTo(Acx, Acy); //Ab-Ac 
        }   
	    
        
//            Ab
//      A  Aa   Ac  B
  //  K               C
//  	Jc				Ca
  //Jb	     		  Cb
  //  Ja				Cc
  //  J                D
//      I   H    F   E
//             G
      		      
        
    	//UPPER RIGHT ARC
        int Bx = X + W - arc;
        int By = Y;    
        int Cx = X + W;
        int Cy = Y + arc;		        
        cP.lineTo(Bx,By);//B		        
        cP.arcTo(new RectF(Bx, By, Cx, Cy), 270, 90); //B-C arc
        
    	//RIGHT TRIANGLE
        int Cax = Cx;
        int Cay = Y + H/2 - 10;    
        int Cbx = Cx + 10;
        int Cby = Y + H/2;
        int Ccx = Cx;
        int Ccy = Cby + 10;        
        /*if ( type == SwifteeApplication.SET_TIP_TO_LEFT ) {                       
            cP.lineTo(Cax, Cay); //A -Aa    
	        cP.lineTo(Cbx, Cby); //Aa-Ab
	        cP.lineTo(Ccx, Ccy); //Ab-Ac	    
        }*/
        
        //BOTTON RIGHT ARC
        int Dx = Cx;
        int Dy = (Y + arc) + (H - arc*2);		        
        int Ex = Bx;		        
        int Ey = Y + H;
        cP.lineTo(Dx,Dy); //D
        cP.arcTo(new RectF(Dx - arc, Dy, Ex + arc, Ey),0,90); //D-E arc
           
        Fy = Ey;
        Gy = Y+H+12;	
        Hy = Ey;
        
        //BOTTOM TRIANGLE       
        if ( type == SwifteeApplication.SET_TIP_TO_LEFT_DOWN ) {
        	 Fx = X + 20;             		        
             Gx = X + 30;             	        
             Hx = X + 40;      
             cP.lineTo(Fx, Fy); //E-F    
             cP.lineTo(Gx, Gy); //F-G
             cP.lineTo(Hx, Hy); //G-H*/
        } else if ( type == SwifteeApplication.SET_TIP_TO_CENTER_DOWN ){
        	 Fx = X+W/2 +10;             	        
             Gx = X+W/2;             		        
             Hx = X+W/2-10;	       
             cP.lineTo(Fx, Fy); //E-F    
             cP.lineTo(Gx, Gy); //F-G
             cP.lineTo(Hx, Hy); //G-H
        } else if ( type == SwifteeApplication.SET_TIP_TO_RIGHT_DOWN ){
        	 Fx = X - 40;                     
             Gx = X - 30;             		        
             Hx = X - 20;            
             cP.lineTo(Fx, Fy); //E-F    
             cP.lineTo(Gx, Gy); //F-G
             cP.lineTo(Hx, Hy); //G-H
        }       
               
        //BOTTOM LEFT ARC
        int Ix = Ax;
        int Iy = Hy;
        int Jx = X;
        int Jy = Dy;
        cP.lineTo(Ix, Iy); //H - I
        cP.arcTo(new RectF(Jx, Jy, Ix, Iy),90,90);// I = J arc
        
        //LEFT TRIANGLE    
        int Jax = Jx;
        int Jay = Ccy;
        int Jbx = Jx - 10;
        int Jby = Cby;
        int Jcx = Jx;
        int Jcy = Cay;
        /*if ( type == SwifteeApplication.SET_TIP_TO_RIGHT ) {
        	cP.lineTo(Jax, Jay); //E-F    
		    cP.lineTo(Jbx, Jby); //F-G
		    cP.lineTo(Jcx, Jcy); //G-H
        }*/
        
        //END
        int Kx = X;
        int Ky = Cy;
        cP.lineTo(Kx, Ky); //K
        cP.arcTo(new RectF(Ax - arc, Ay, Kx + arc, Ky),180,90); //K - A arc
        cP.lineTo(Ax, Ay); //K   
	        

    	
		return cP;   	
 
     }

    public void setDraw(int draw) {
		this.draw = draw;
	}
    
    public void setXYPos(int x, int y){
    	xPos = x;
    	yPos = y;
    }
      
    public void setTipText(String[] tipText) {
		this.tipText = tipText;
	}
    
    public void setTextLines(int textLines) {    	
		this.textLines = textLines;
	}
} 



