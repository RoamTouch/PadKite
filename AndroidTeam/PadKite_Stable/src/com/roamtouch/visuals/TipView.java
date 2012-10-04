package com.roamtouch.visuals;

import java.util.StringTokenizer;
import java.util.Timer;
import java.util.Vector;

import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.utils.ColorUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;

import android.util.Log;
import android.view.View;


public class TipView extends View {     
    
    public int draw;
    public int isFor;	
    public int tipFor;
    
    public int xPos;
    public int yPos;
    public int width;
    public int height;   
    
    public int xCenter;
    
    public String[] tipText;
    
    int arc = 20;
    
    private Path cP;
    public int textLines;
    int lineHeight = 30;
    
    float xPoint;
	float yPoint;
	
	private int vertical;

	public void setVertical(int vertical) {
		this.vertical = vertical;
	}

	private Rect rect;
	
	private int[] tipColor;   
	
	private FloatingCursor fC;
	private BrowserActivity mP;
	
	private Tabs t;
	
	private int objOrientation;
	
	private int edge;
	
	private String buttonText;
	
	public int buttonOver = -1;
	
	private Canvas canvas;
	private Bitmap bitmap;

	public TipView(Context context) {
        super(context);     
    }   
	
	public void setGrandParent(BrowserActivity b, FloatingCursor f){
		mP = b;
		fC = f;		
	}

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);   
        
        switch(draw){		

			case SwifteeApplication.IS_FOR_CIRCULAR_MENU_TIPS:
				
				Paint pT = new Paint();   		
		       	
			    switch (textLines){
			    
		    		case 1:	    	   		
		    			
		    			Vector paintText11 = new Vector();
		    			paintText11 =  getPaintText(tipText[0], pT);
		    			Paint resPaint = (Paint)paintText11.get(0);	    			
		    			String title = (String)paintText11.get(1);	 			
		    			width = (int) resPaint.measureText(title);  
		    			comment(c, lineHeight);
		    			int _x = checkCMTextWidth();
		    			//Paint pT1 = paintText(1);
		    	   		c.drawText(title, _x, yPos-5, resPaint);
		    			
		    	   		break;
		    	   		
		    	   	case 2:	     	   		
		    	   			       	   		
		    	   		Vector paintText21 =  getPaintText(tipText[0], pT);	    	   		
		    	   		Paint onePaint = (Paint)paintText21.get(0);    			
		    			String oneLine = (String)paintText21.get(1);		
		    			int width21 = (int) onePaint.measureText(oneLine); 	   		
		    	   		
		    			Vector paintText22 =  getPaintText(tipText[1], pT);
		    			Paint twoPaint = (Paint)paintText22.get(0);       			
		    			String twoLine = (String)paintText22.get(1);		
		    			int width22 = (int) twoPaint.measureText(twoLine);    			
		    	   		
		    	   		if ( width21 > width22 ) {	    	   			
		    	   			width = width21;	    	   			
		    	   		} else {	    	   			
		    	   			width = width22;	    	   			
		    	   		}  				
		    	   		
		    	   		int xText;
		    	   		
		    	   		if (BrowserActivity.checkWindowsTabOpened() && textLines == 2 ){
		    	   			xText = xPos + (width/3)-20;
		    	   		} else {
		    	   			width += 10;
		    	   			xText = xPos + (width/3) + 10;
		    	   		}
		    	   		
		    	   		comment(c, lineHeight*2);  
		    	   		
		    	   		//canvas.drawText((String)paintText21[1], xPos+(width/2)+15, yPos+10, onePaint);	    	   		
		    	   		//canvas.drawText((String)paintText22[1], xPos+(width/2)+15, yPos+45, twoPaint);
		    	   		
		    	   		c.drawText(oneLine, xText, yPos+10, onePaint);	    	   		
		    	   		c.drawText(twoLine, xText, yPos+45, twoPaint);
		    	   		
		    	   		break;
		    	   		
		    	   	case 3:
		    	   		
		    	   		comment(c, lineHeight*3);   	       	   		
		    	   		Vector paintText31 =  getPaintText(tipText[0], pT);	    	   		       
		    	   		c.drawText((String)paintText31.get(1), xPos+(width/2)+15, yPos-10, (Paint)paintText31.get(0));
		    	   		
		    	   		Vector paintText32 =  getPaintText(tipText[1], pT);
		    	   		c.drawText((String)paintText32.get(1), xPos+(width/2)+15, yPos+25, (Paint)paintText32.get(0));
		    	   		
		    	   		Vector paintText33 =  getPaintText(tipText[2], pT);
		    	  		c.drawText((String)paintText33.get(1), xPos+(width/2)+15, yPos+45, (Paint)paintText33.get(0));	   	
		    	   		
		    	   		break;   	
		    	   		
			    }
				
				break;
				
			case SwifteeApplication.IS_FOR_CONTENT_OBJECT:	
				
				mP.setTipsActivated(true);
   	
				edge = SwifteeApplication.getEdge();
    	    	
    	    	int variableHeight;  	
    	    	
    	    	int xTabPos = rect.left - (edge/4) - 1;
    	    	int yTabPos = rect.bottom - 5;
    	    	
    	    	int xText = xTabPos + edge + (edge/2);
    	    	int yText = yTabPos + edge;
    	    	
    	    	String text;
    	    	String text1 = null;    	    	
    	    	int yText1 = 0;   	    	
    	    	if (tipText.length==2){ 	    		    	    		
    	    		text1 = (String) tipText[1];    	    		
    	    		yText1 = yTabPos + (edge*2);
    	    	} 
    	    	
    	    	yText = yTabPos + edge+(edge/2);
    	    	text = (String) tipText[0];
    	    	
    	    	int xWidth = rect.right - rect.left + edge - 10;
    	    	int xHeight = edge*4;   	    	
    	    	
    			int r = tipColor[0]; 
    			int g = tipColor[1]; 
    			int b = tipColor[2];
    	    	
    	    	int[] coords = {xTabPos, yTabPos, xWidth, xHeight, edge};    
    	    	int[] color = { r, g, b };		
    	    	t = new Tabs();
    	    	t.setTabs(coords, color, mP);
    	    	
    	    	Rect back = new Rect();
    	    	back.left = xTabPos;
    	    	back.top = yTabPos;
    	    	back.right = rect.left + xWidth;
    	    	back.bottom = yTabPos + xHeight;   	
    	    	SwifteeApplication.setBottomFormButton_0_Rect(back);
    	    	
    	    	//Canvas 			
    	        bitmap = Bitmap.createBitmap(xTabPos + xWidth, yTabPos + xHeight, Bitmap.Config.ARGB_8888);
    	        canvas = new Canvas(bitmap);   
    	    	
    	    	Paint pTipFill = new Paint();
    	    	pTipFill = t.paintTab(false); 
    	    	
    	    	Object[] params = new Object[4];
    	    	params[0] = SwifteeApplication.TIP_FOR_CONTENT_OBJECT; //objOrientation;
    	    	params[1] = SwifteeApplication.TIP_CONTENT_ORIENT_BOTTOM; //objOrientation;  
    	    	params[2] = vertical;     	    	
    	    	
    	    	Path[] all = new Path[2];	    	    	
    	    	all = t.drawShape(params);
    	    	canvas.drawPath(all[0], pTipFill);
    	    	
    	    	Paint pTipStroke = new Paint();
    	    	pTipStroke = t.paintTab(true);
    	    	canvas.drawPath(all[1], pTipStroke);    
    	    	
    	    	Paint mPaint = new Paint();
    	    	int textSize = (int) mPaint.measureText(buttonText);
    	    	mPaint = t.paintText(18);
    	    	
    	    	Rect rButton 	= new Rect();
    	    	rButton.right 	= rect.right - edge; //rButton.left + 25 + (edge*2);
    	    	int buttonWidth = (edge*2) + textSize;   	    	
    	    	rButton.left 	= rButton.right - buttonWidth;
    	    	rButton.top 	= yTabPos + 20;    	    	    	    	
    	    	rButton.bottom 	= yTabPos + xHeight - 10;
    	    	
       	    	Rect messageRect	= new Rect();
    	    	messageRect.left 		= rect.left + (edge/2);     	    	
    	    	messageRect.right 		= rButton.left - edge;
    	    	messageRect.top 		= yTabPos + 20;    	    	    	    	
    	    	messageRect.bottom 		= yTabPos + xHeight - 10;
    	    	
    	    	SwifteeApplication.setTipMessageRect(messageRect);
    	    	    	
    	    	Path[] paBack	= new Path[2];	    	    	
    	    	paBack = t.drawTopRow(messageRect, SwifteeApplication.DRAW_ROW_ROUNDED);
    	    	
    	    	Paint baFill = new Paint();   	    	
    	    	baFill.setStyle(Paint.Style.FILL);	
    	    	baFill.setColor(Color.WHITE);
    	    	baFill.setAntiAlias(true);	
    	    	
    	    	canvas.drawPath(paBack[0], baFill);   
    	    	
    	    	Paint pTe = new Paint();
    	    	pTe.setStyle(Paint.Style.FILL);
    	    	pTe.setAntiAlias(true);
    	    
    	    	int[] textColor = ColorUtils.checkDarkColor(tipColor); 
    	    	int rT = ColorUtils.checkVeryDarkColor(tipColor)[0];
    	    	int gT = ColorUtils.checkVeryDarkColor(tipColor)[1];
    	    	int bT = ColorUtils.checkVeryDarkColor(tipColor)[2];	    	
    	    	pTe.setColor(Color.rgb(rT, gT, bT));
    	    	pTe.setTypeface(Typeface.DEFAULT_BOLD);
    	    	pTe.setTextSize(21);
    	    	
    	    	if (tipText.length==1){
    	    		canvas.drawText(text, xText - (edge/4), yText + (edge/2), pTe);
    	    	} if (tipText.length==2){
    	    		canvas.drawText(text, xText - (edge/4), yText + (edge/2), pTe);
    	    		
    	    		if (text1.contains("http://www")){
    	    			text1 = text1.replace("http://www", "");
    	    			int textWidth = (int) pTe.measureText(text1);	    
    	    			int messageRectWidth = SwifteeApplication.getTipMessageRect().width();  			
    	    			int text1Length = text1.length();    	    			
    	    			if (textWidth > messageRectWidth){
    	    				int newLength =  ((messageRectWidth * textWidth) / text1Length) -5;    
    	    				text1 = text1.substring(0, newLength) + "...";
    	    			}    			
    	    		} 		
    	    		
    	    		canvas.drawText(text1, xText - (edge/4), yText1 + (edge+(edge/4)), pTe);
    	    	}   
    	    	  	
   	    	
    	    	Path[] paBut	= new Path[2];	    	    	
    	    	paBut = t.drawTopRow(rButton, SwifteeApplication.DRAW_ROW_ROUNDED);
    	    	
    	    	SwifteeApplication.setTipButton_Rect(rButton); 
    	    	
    	    	Paint paBFill = new Paint();   	    	
    	    	paBFill.setStyle(Paint.Style.FILL);	
    	    	paBFill.setAntiAlias(true);	
    	    	
    	    	if (buttonText.equals("STOP")){
    	    		pTe.setColor(Color.RED);
    	    	} else {
    	    		pTe.setColor(Color.rgb(rT, gT, bT));
    	    	}
    	    	
    	    	int[] colorArray;  
    	    	
    	    	if ( buttonOver == 40 ){
    	    		colorArray = ColorUtils.checkVeryDarkColor(tipColor);
    	    	} else {
    	    		colorArray = tipColor;
    	    	}   	    	
    	    	
				/*int R = tipColor[0];
				int G = tipColor[1];
				int B = tipColor[2];
				
				int dR = ColorUtils.checkDarkColor(tipColor)[0];
				int dG = ColorUtils.checkDarkColor(tipColor)[1];
				int dB = ColorUtils.checkDarkColor(tipColor)[2];*/	
				        
	            /*Shader sha = new LinearGradient(0, 0, rButton.width(), rButton.height(), 
	            		Color.rgb(R, G, B), 
	            		Color.rgb(dR, dG, dB), 
	            		Shader.TileMode.MIRROR);
	            
				paBFill.setShader(sha);*/
				
				canvas.drawPath(paBut[0], paBFill);
				
    	    	Paint paStroke = new Paint();
    	    	paStroke.setStrokeWidth(1);	
    	    	paStroke.setStyle(Paint.Style.STROKE);
    	    	paStroke.setColor(Color.WHITE);   	 
    	    	
    	    	canvas.drawPath(paBut[1], paStroke); 
    	    	
    	    	int textXPos = (buttonWidth/2) - (textSize/2);  	    	
    	    	int textYPos = (xHeight - 18) / 2; 
    	    	canvas.drawText(buttonText, rButton.left + (textXPos/2), rButton.top + textYPos , mPaint);  	
    	    	
    	    	/*Paint b0 = new Paint();
    	    	b0.setColor(Color.BLUE);
    	    	Rect bB0 = SwifteeApplication.getBottomFormButton_0_Rect();
    	    	canvas.drawRect(bB0, b0);
    	    	
    	    	Paint b1 = new Paint();
    	    	b1.setColor(Color.CYAN);
    	    	Rect bB1 = SwifteeApplication.getTipButton_Rect();
    	    	canvas.drawRect(bB1, b1);*/
    	    	
    	    	int bitmapWidth = bitmap.getWidth(); 
    	   		int bitmapHeight = bitmap.getHeight();
	    	   	
	    		c.drawBitmap(bitmap, 
	    		new Rect(0,0,bitmapWidth,bitmapHeight), 
	    		new Rect(0,0,bitmapWidth,bitmapHeight), null);
    	    	
				break;
				
			case SwifteeApplication.DRAW_NOTHING:    
				mP.setTipsActivated(false);
				buttonOver = -1;
    			break;
				
        }  	
		    
       	   
    }
    
    
    
    private int checkCMTextWidth(){ 
    	
    	int _x = 0;
    	
    	if (isFor == SwifteeApplication.IS_FOR_CIRCULAR_MENU_TIPS){	
	
			_x =  xPos+(width/2);
			
			if (tipText[0].toString().equals("Add New Window")){
				_x -= 25;
			} else if (tipText[0].toString().equals("Tabs Manager")){
				_x -= 10; 
			} else if (tipText[0].toString().equals("Go Forward")){
				_x += 10;
			} else if (tipText[0].toString().equals("Refresh Page")){
				_x -= 5;			
			} else if (tipText[0].toString().equals("Bookmarks")){
				_x += 5;
			} else if (tipText[0].toString().equals("Home Page")){
				_x += 60;
			} else if (tipText[0].toString().equals("Share Page")){
				_x += 90;
			} else if (tipText[0].toString().equals("Finger Model")){
				_x -= 80;					
			} else if (tipText[0].toString().equals("Settings")){
				_x += 20;
			}
		
			if (tipText[0].toString().equals("Landing Page")){
				_x -= 8; 
			} else if (tipText[0].toString().equals("Back")){
				_x += 40; 
			}
			
			if (tipText[0].toString().equals("Gesture Editor")){
				_x -= 10; 
			} else if (tipText[0].toString().equals("Add Bookmark")){
				_x -= 10; 
			} else if (tipText[0].toString().equals("Browser Settings")){
				_x -= 25;  
			} else if (tipText[0].toString().equals("Miscellaneous")){
				_x -= 10; 
			} else if (tipText[0].toString().equals("Set Homepage")){
				_x -= 70; 
			}  else if (tipText[0].toString().equals("History")){
				_x += 25; 
			} else if (tipText[0].toString().equals("Download")){
				_x += 10; 
			} else if (tipText[0].toString().equals("Back to Main Menu")){
				_x -= 35; 
			}		
			
		} else {
			_x = xPos+(width/2)+15;
		}      
		
		return _x;
	}
    
    private Vector getPaintText(String text, Paint pT){    	
    	
    	
		Throwable te = new Throwable(); StackTraceElement[] elements =
		te.getStackTrace();				 
		String calleeMethod = elements[0].getMethodName(); String
		callerMethodName = elements[1].getMethodName(); String
		callerClassName = elements[1].getClassName();				
		Log.v("call", "callerMethodName: "+callerMethodName+ " callerClassName: "+callerClassName );
		   	
    	Vector paintTextVector = new Vector();
    	
    	if (text.contains("<b>")){   	
   			
   			pT = paintText(0);	 
   			text = text.replaceFirst("<b>", "");
   			text = text.replaceFirst("</b>", "");   			 			
   			paintTextVector.add(pT);
   			paintTextVector.add(text);   			
   		
   		} else if (text.contains("<g>")){
   			
   			pT = paintText(2);	 
   			String t = null; 
   			t =	text.replaceFirst("<g>", "");  	
   			paintTextVector.add(pT);
   			paintTextVector.add(t);	
   			
   		} else {  			
   			pT = paintText(1); 			
   			
   			paintTextVector.add(pT);
   			paintTextVector.add(text);   		
   		}	
   		
   		return paintTextVector;
    }
    
    private void comment (Canvas canvas, int commentHeight){   
    
    	int _x;
    	int _y;
    	
    	if (isFor == SwifteeApplication.IS_FOR_CIRCULAR_MENU_TIPS){
    		
    		if (BrowserActivity.checkWindowsTabOpened() && textLines == 2 ){
    			
    			_x = xPos - (width/2) + 35;
    			_y = yPos-20;
    			
    		} else {    		
    			
    			_x = xPos - (width/2) + 50;
    			_y = yPos-40;
    			
    		}
    		
    	} else {
    		
    		_x = xPos - 30;
    		_y = yPos - 20;
    		
    	}  	
    	  
       Path cP = drawComment(_x, _y, width+30, commentHeight+20, tipFor);  
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
    
    private Paint paintText(int what){
    	Paint pText = new Paint();
		pText.setStyle(Paint.Style.FILL);
		pText.setTextAlign(Paint.Align.CENTER);
		pText.setAntiAlias(false);
		//int [] textColor = null;
		Color color = null;
		switch (what){
			case 0:
				pText.setColor(color.BLACK);
				//textColor = SwifteeApplication.BLACK;
				pText.setTextSize(25);
				break;
			case 1:
				//textColor = SwifteeApplication.DARK_GRAY;
				pText.setColor(color.GRAY);
				pText.setTextSize(22);
				break;
			case 2:							
				
				/*int r = SwifteeApplication.LIGHT_GRAY[0];
				int g = SwifteeApplication.LIGHT_GRAY[1];
				int b = SwifteeApplication.LIGHT_GRAY[2];	
				int col = Color.rgb(r, g, b);		
				pText.setColor(col);*/
				
				pText.setColor(color.LTGRAY);
				pText.setTextSize(22);				
				break;
		}
		//pText.setARGB(100, textColor[0], textColor[1], textColor[2]);	
		pText.setTypeface(Typeface.DEFAULT_BOLD);		
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
    
    public void setIsFor(int isFor) {
		this.isFor = isFor;
	}
    
    public void setTipFor(int tipFor) {
		this.tipFor = tipFor;
	}
    
    public void setXYPos(int x, int y){
    	xPos = x;
    	yPos = y;
    }
      
    public void setTipText(String[] tipText) {
		this.tipText = tipText;
	}
    
    public void setTipButtonText(String tipText) {
		this.buttonText = tipText;
	}
    
    public void setTextLines(int textLines) {    	
		this.textLines = textLines;
	}
    
    public void setRect(Rect rect) {
		this.rect = rect;
	}
    
    public int[] getTipColor() {
		return tipColor;
	}

	public void setTipColor(int[] tipColor) {
		this.tipColor = tipColor;
	}
	
	public int getObjOrientation() {
		return objOrientation;
	}

	public void setObjOrientation(int objOrientation) {
		this.objOrientation = objOrientation;
	}
	
	public void setButtonOver(int buttonOver) {
		this.buttonOver = buttonOver;
	}
} 



