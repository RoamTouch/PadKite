package com.roamtouch.visuals;

/*
 * 
 * 

▲
â–²

▼
â–¼

□
â–¡

■
â– 


 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.roamtouch.webhook.WebHitTestResult;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.utils.COLOR;
import com.roamtouch.utils.ColorUtils;
import com.roamtouch.utils.PaintTextUtils;
import com.roamtouch.utils.StringUtils;
import com.roamtouch.utils.ImageCache;

public class SuggestionView extends View implements Runnable {	
   
    private BrowserActivity mP;
    private FloatingCursor fC;
   
    //Rect tabRect;
    //Rect ringRect;
    
    public int scrollX;
    public int scrollY;    
     
    int ringArc;    
    

	int r;
   	int g;
   	int b;
    
   	int[] fillColor;  

    int[] cords;
    
    private Tabs t;
    
    public int draw;   

    public int input;
    	
   	int x;
   	int y;
   	int W;
   	int H;
   	
   	int ringWidth = 1;
   	
   	public int rectRight;
   	public int rectBottom;
   	
   	int TYPE;
   	
   	int rotatedTab = 0;
   	
   	private Rect rect;    
   	private String[][] arraySuggestion;  	
   	
	int identifier = 0;
	
	private Rect recentRect;
	
	private int cX;
	private int cY;
	
	private boolean open;   
	
    int textFinalXPos;
    
    private int accumulator;
    
    private static int edge;  

	private int[] suggestionColors;    
    private String[] buttons;  
 
	int countHeight;
    int rowHeight = 0;
    int suggestionHeight;
    
    private int rowOver = -1;   
    public int buttonOver = -1;
    public int buttonDown = -1;	
    public int realButtonId = -1;    
    public int activeSearch;
    
    private String buttonText;  

	private int variableHeight;
	
	private Canvas canvasFrame;
	private Bitmap bitmapFrame;
	
	private Canvas canvasButtons;
	private Bitmap bitmapButtons;
	
	private Canvas canvasRows;
	private Bitmap bitmapRows;
	
	private Bitmap cacheBitmap;
	
	private Canvas canvasSpinner;
	private Canvas canvasImages;
	private Canvas canvasVideos;
	private Canvas canvasWiki;
	
	private Bitmap bitmapSpinner;
	private Bitmap bitmapImages;
	private Bitmap bitmapVideos;
	private Bitmap bitmapWiki;
	
	private int[] butColor;
	private int[] tabColor;	
	
	int sum;
	
	int k;
	int m;
	
	private int upId; //scroll id	
	private int downId; //scroll id
	private boolean downFlag = false;
	private boolean upFlag = false;
	private int currentIndex;
	private boolean scroll;

	int addScroll;
	long mStartTime;	
	
	private Object[][] objectList;
	List<Object[][]> list = new ArrayList<Object[][]>();
	String[] tempObjectText = new String[SwifteeApplication.getRowAmount()];
	
	private Handler handler = new Handler();
	
	private Hashtable<Integer, int[]> colorSpinnerDots = new Hashtable();
	private RotateAnimation rotateAnim;
	
	private int cType;
	private int lastKnownWebHitType;
	
	int top;
	int yRow;	
	
	private Context context;
	
	int pKIndex;
	
	private Matrix matrixSpinner;
	private Matrix matrixImage;	
	
	private float mDegree;
	private boolean drawSpinner;
	private boolean drawVideoBitmap;
	
	private RelativeLayout relativeVideoView;
	
	private ImageView[] videoImageArray;
	private Matrix[] videoMatrixArray;
	private Bitmap[] videoBitmapArray;
	
	private Rect[] reRowArray;	
 	
	final AlphaAnimation alphaAnimation = new AlphaAnimation(0.5F, 0.5F); // Creating new alpha animation, that makes the view half transparent
	
	private int frameHorizontalOrientation;
	private int frameVerticalOrientation;
	
	private int hitPKIndex;
	private boolean expanded;

	private boolean hasImages;
	private static boolean hasVideos; 
	
	private ImageView searchImage;	
	private int topUp; 
	private int topDown;	
	private boolean isForImageArray;
	
	private int addFrameId;
	private int addButtonId;
	private int addRowId;

	private boolean hasButtons;
	
	public void setHasButtons(boolean hasButtons) {
		this.hasButtons = hasButtons;
	}

	private ImageCache imgCache = new ImageCache();

	public void setHasImages(boolean hasImages) {
		this.hasImages = hasImages;		
	}
	
	public void setHasVideos(boolean hasVideos) {				
		this.hasVideos = hasVideos;
		if (hasVideos){		
			relativeVideoView 	= new RelativeLayout(context);	
			int amount = SwifteeApplication.getRowAmount();
			videoImageArray 	= new ImageView[amount];			  
	        videoMatrixArray 	= new Matrix[amount];
	        videoBitmapArray 	= new Bitmap[amount];
	        canvasVideos = new Canvas();       
		}
	}
	
	private boolean hasWiki;
	public void setHasWiki( boolean hasWiki ){
		this.hasWiki = hasWiki;		
		if (hasWiki){			
			int amount = SwifteeApplication.getRowAmount();		
	        canvasWiki = new Canvas();		       
		}
	}
	
	public SuggestionView(Context cont) {
        super(cont);
        context = cont.getApplicationContext();
        recentRect = new Rect();      
        t = new Tabs();
    }	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //if (bitmap != null) {
        //    bitmap .recycle();
        //}        
    }
	
   	public void setArraySuggestion(String[][] arraySuggestion) {  
   		  				
   		this.arraySuggestion = arraySuggestion; 		
   		
   		if (arraySuggestion.length>0){			
   			
   			topUp = SwifteeApplication.getRowAmount();
   			
   			if (hasVideos){
   				objectList = new Object[topUp][4];   				
   			} else if (hasWiki){
   				objectList = new Object[topUp][5];
   			} else {
   				objectList = new Object[topUp][2];
   			}
   			
   			upId = topUp-1;
   			downId = 0;
   			topDown = 0;
   			
   			downTask = null;
   			upTask = null;
   			
   			limitUp = upId;  
   			limitDown = 0; 
   			
   			int rowAmount = SwifteeApplication.getRowAmount();
   			for (int i=0; i<rowAmount; i++){  	   			
   				String data = (String) arraySuggestion[i][0];  				
   				objectList[i][0] = i;
   				objectList[i][1] = data;
   				if (hasVideos){
   					String videoUrl = (String) arraySuggestion[i][1];
   					String extra 	= (String) arraySuggestion[i][2];   					
   					objectList[i][2] = videoUrl;  	
   					objectList[i][3] = extra;   					
   				} else if (hasWiki) {
   					String url = (String) arraySuggestion[i][1];
   					String abs 	= (String) arraySuggestion[i][2];   					
   					String wikipedia_id = (String) arraySuggestion[i][3];
   					objectList[i][2] = url;  	
   					objectList[i][3] = abs;
   					objectList[i][4] = wikipedia_id;   	
   				}
   			}  			
   			list.add(objectList);   			
   			//runOverAsyncOnce = true;
   			//searchTask = new SearchTask();
   		}
   		else {
   			this.arraySuggestion = null;
   		} 	  		
   	}
	   	
    int _width;
  	int _height;
  	
  	private String bitmapFrameKey;  	
  	private String bitmapButtonKey;
  	private String bitmapRowKey;
  	
  	private boolean switchFrameBitmap;
  	private boolean switchButtonBitmap;
  	private boolean switchRowBitmap;
      
      public void setBitmap(int x, int y, int width, int height, String bitmapFrameKey){		
  		_width = x+width;
  		_height = y+height;
  		//destroy();
  		data=null;
  		colorSpinnerDots = ColorUtils.setSpinnerColors(colorSpinnerDots);  		
  		//http://www.curious-creature.org/2010/12/08/bitmap-quality-banding-and-dithering/ 		
  		
  		addFrameId++;
  		this.bitmapFrameKey = bitmapFrameKey + "-" + addFrameId;
  		switchFrameBitmap = true;
  		addButtonId++;
  		bitmapButtonKey =  bitmapFrameKey + "-" + "b-" + "none" + "-" + SwifteeApplication.getActiveTabIndex() + "-" + addButtonId;	
  		switchButtonBitmap = true;
  		addRowId++;
  		bitmapRowKey =  bitmapFrameKey + "-" + "r-" + "none" + "-" + SwifteeApplication.getActiveTabIndex() + "-" + addRowId;;	 
  		switchRowBitmap = true;  	
  	}
	
	Object[][] data;   
	
	private int textYLess;
	
	private int isBigger;
	private int arrayAmountPerType;
	private Paint pTab;
	private Paint pStroke;
	private int[] color;
	
	private boolean bitmapFrameCache;
	private boolean bitmapButtonCache;
	private boolean bitmapRowCache;
	
	private boolean ceroButtonMarkUpdated;
	private boolean ceroRowMarkUpdated;
	
	private void newButtonBitmap(){
		bitmapButtons = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
	  	canvasButtons = new Canvas(bitmapButtons);	    		  	
	  	switchButtonBitmap=true;	
   		bitmapButtonCache=false;
   		buttons(); 
	}
	
	private void newFrameBitmap(){
		bitmapFrame = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
  		canvasFrame = new Canvas(bitmapFrame);
  		switchFrameBitmap=true;
   	   	bitmapFrameCache=false;
   	   	frame();
	}
	
	private void newRowBitmap(){
  	  	bitmapRows = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
  	  	canvasRows = new Canvas(bitmapRows);   	   	  	  	
  	  	switchRowBitmap = true;	    	   	  	  	
		bitmapRowCache	= false;
		rows();
	}
	
	@Override
    protected void onDraw(Canvas c) {
        
    	super.onDraw(c);   
        
    	Paint paint = new Paint();
    	
        switch (draw){     
                 
	       case SwifteeApplication.TAB_SUGGESTIONS: // && clear==false){	

	    	   /**MEMORY MONITOR - CHECK APP SETTINGS**/
	    	   if(SwifteeApplication.getMemoryStatusEnabled()){	    		   
	    		   int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
	    		   String usedMegsString = String.format("PadKite | Memory Used: %d MB", usedMegs);	    		   
	    		   mP.getWindow().setTitle(usedMegsString);	    		   
	    		   //mP.getWindow().setTitle(bitmapRowKey);    		   
	    	   	}
	    	   
	    	   /**GLOBAL VARS**/
	    	   BrowserActivity.setSuggestionActivated(true);
			   cType = SwifteeApplication.getCType();
			   lastKnownWebHitType = SwifteeApplication.getLastKnownHitType();
			   arrayAmountPerType = SwifteeApplication.getAmountOfObjectsPerType(cType);		    	
			   expanded = SwifteeApplication.getExpanded();	   
	    	   
			   boolean isArmed = isListArmed();
			   
			   	/**FRAME**/
			   Log.v("bitmapFrameKey", ""+bitmapFrameKey);
			   boolean has = imgCache.hasImage(bitmapFrameKey);
	    	   if ( has ){
	    	   		
	    	   		if (switchFrameBitmap && ceroRowMarkUpdated){
	    	   			bitmapFrame = imgCache.getFromCache(bitmapFrameKey);
	    	   			ceroRowMarkUpdated = false;
	    	   		}
	    	   	
	    	   		if(bitmapFrame==null){
	    	   			newFrameBitmap();
	    	   		} else {
	    	   			bitmapFrameCache=true;
	    	   			switchFrameBitmap=false;
	    	   		}
	    	   		
	    	   	} else {	    	   		
	    	   		newFrameBitmap();
	    	   	}

	    	   	/**BUTTONS**/
	    	   Log.v("bitmapButtonKey","has : "+hasButtons);
	    	   if (hasButtons){	
	    		   
	    		   Log.v("bitmapButtonKey",""+bitmapButtonKey);
		    	   	boolean hasCache = imgCache.hasImage(bitmapButtonKey);
		    	   	if ( hasCache && data!=null && ceroButtonMarkUpdated ){    	   		
		    	   		
		    	   		if ( switchButtonBitmap ){
		    	   			bitmapButtons = imgCache.getFromCache(bitmapButtonKey);
		    	   			ceroButtonMarkUpdated = false;
		    	   		}
		    	   		
		    	   		if(bitmapButtons==null) {
		    	   			newButtonBitmap();
		    	   		} else {	    	   		
		    	   			bitmapButtonCache=true;
		    	   			switchButtonBitmap=false;
		    	   		}
	    			   
		    	   	} else {	    	   		
		    	   		newButtonBitmap();	  	   	    	
		    	   	} 
	    	   }   
		    	
	    	   	/**ROWS**/
	    	   	if (isArmed){
	    	   		
	    	   		if (scroll){
	    	   			
	    	   			rows();
	    	   			
	    	   		} else {
	    	   			
	    	   			Log.v("bitmapRowKey",""+bitmapRowKey);
			    	   	if ( !hasImages && imgCache.hasImage(bitmapRowKey) && !hasVideos && !hasWiki){	    	   		   	   			    	   			
			    	   			
			    	   			if (switchRowBitmap){
			    	   				bitmapRows = imgCache.getFromCache(bitmapRowKey);
			    	   			}
			    	   			
			    	   			if(bitmapRows==null) {
			    	   				newRowBitmap();
				    	   		} else {	    	   		
				    	   			bitmapRowCache=true;
				    	   			switchRowBitmap=false;
				    	   		}    	   			
			    	   		
		    	   		} else {
		    	   			Log.v("bitmapRowKey","NEW: "+bitmapRowKey);
		    	   			newRowBitmap();
		    	   		}   	
	    	   		}
	    	   	}	    	
	    	   	
	    	   	/**SPINNER**/
	    	   	spinner();   	  	
		    	
    	    	//Draw Frame
		     	if (bitmapFrame!=null){
	    	   		
		    	   	int bitmapWidth = bitmapFrame.getWidth(); 
			   		int bitmapHeight = bitmapFrame.getHeight();
		    	   	
		    		c.drawBitmap(bitmapFrame, 
		    		new Rect(0,0,bitmapWidth,bitmapHeight), 
		    		new Rect(0,0,bitmapWidth,bitmapHeight), null);
		    		
		    		if(!bitmapFrameCache){
		    			imgCache.saveToCache(bitmapFrameKey, bitmapFrame);
		    		}
		     				    		
	    	   	} else {
	    	   		
	    	   		imgCache.deleteBitmap(bitmapFrameKey);	
    		   		bitmapFrame = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);    	  	  		
    	  	  		canvasFrame = new Canvas(bitmapFrame);	
    	  	  		bitmapFrameCache=false;
    	  	  		frame();
	    	   		
	    	   	}
		     	
		     	//Draw Buttons
		     	if ( bitmapButtons!=null && hasButtons ){
	    	   		
		    	   	int bitmapWidth = bitmapButtons.getWidth(); 
			   		int bitmapHeight = bitmapButtons.getHeight();
		    	   	
		    		c.drawBitmap(bitmapButtons, 
		    		new Rect(0,0,bitmapWidth,bitmapHeight), 
		    		new Rect(0,0,bitmapWidth,bitmapHeight), null); 
		    		
		    		if(!bitmapButtonCache){
		    			imgCache.saveToCache(bitmapButtonKey, bitmapButtons);
		    		}		
		    		
	    	   	} else {
	    	   		
	    	   		imgCache.deleteBitmap(bitmapButtonKey);	
    		   		bitmapButtons = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);    	  	  		
    	  	  		canvasFrame = new Canvas(bitmapButtons);	
    	  	  		bitmapButtonCache=false;
    	  	  		buttons();	    	   		
	    	   	}
		     	
		     	//Draw Rows
		     	if ( isArmed && !hasImages){
		     			     	
			     	if (bitmapRows!=null){
			     					     		
			     		/*Paint b1 = new Paint();
        		    	b1.setColor(Color.CYAN);
        		    	Rect bB1 = SwifteeApplication.getBottomFormButton_2_Rect();
        		    	c.drawRect(bB1, b1);*/
			     		
			    	   	int bitmapWidth = bitmapRows.getWidth(); 
				   		int bitmapHeight = bitmapRows.getHeight();		    	   	
				   		
			    		c.drawBitmap(bitmapRows, 
			    		new Rect(0,0,bitmapWidth,bitmapHeight), 
			    		new Rect(0,0,bitmapWidth,bitmapHeight), null);    	
			    		 		
			    		if(!bitmapRowCache){
			    			imgCache.saveToCache(bitmapRowKey, bitmapRows);
			    		}
			    		
		    	   	}  else {   	   		
		    	   		
			    	   	imgCache.deleteBitmap(bitmapRowKey);	
		    		   	bitmapRows = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);    	  	  		
		    	  	  	canvasFrame = new Canvas(bitmapRows);	
		    	  	  	bitmapRowCache=false;
		    	  	  	rows(); 	   		
		    	   	} 
		     	}
		     	
		     	/** IMAGES **/
    	    	if (hasImages){ 		    	    		
    	    		images(c);     	    		  
    	    	}
		     	
		     	/**VIDEOS**/    	    	
    	    	if ( isArmed && hasVideos){ 		   			
    	    		videos(c);		        
	    		} else {	    			
	    			drawVideoBitmap = false;
	    		}
		     	
		     	//Draw Spinner
		     	if (drawSpinner){
	    			c.drawBitmap(bitmapSpinner, matrixSpinner, null);
	    		}
		     	
		     	/*Log.v("bitmap","frame: "+bitmapFrameKey);
		     	Log.v("bitmap","button: "+bitmapButtonKey);
		     	Log.v("bitmap","row: "+bitmapRowKey);
		     	Log.v("bitmap","------------------");*/
			     	
	    		invalidate();
		    	
	    		break;  
	    	
		    	    	
	        case SwifteeApplication.DRAW_NOTHING:
	        default:
	        	BrowserActivity.setSuggestionListActivated(false);
	        	BrowserActivity.setSuggestionActivated(false);
	        	arraySuggestion=null;	
	        	upId		= 0; 
	        	limitDown	= 0;
	        	limitUp		= topUp-1;
	        	cleanOvers();
	        	reachedTop 		= false;
	        	reachedBottom 	= true;	        	
	        	//destroy();
	        	
	        	/**MEMORY MONITOR**/
		    	//if(SwifteeApplication.getMemoryStatusEnabled() && context!=null){	    		   
		    	//	 mP.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		    	// }
	        	break;        	
    	    }      
		};	

		private void spinner(){
			
    	   	int status = SwifteeApplication.getPadKiteInputSpinnerStatus();	 
		   	
		   	if (cType == SwifteeApplication.TYPE_PADKITE_INPUT
		   	    &&	( status == SwifteeApplication.SUGGESTION_DATA_CALLED 
		   	    || status == SwifteeApplication.SUGGESTION_DATA_LOADED )
		   	    || (status == SwifteeApplication.SUGGESTION_DATA_LOADED 
		   	    && isListArmed())    	   	    
		   		) { 				   		
		   		
	   			Rect spBackground = new Rect();	    	   			
	   			spBackground.left = rect.right - (edge*4);
	   			spBackground.top = rect.top -2;
	   			spBackground.right = (edge*4) + 2;
	   			spBackground.bottom = rect.bottom - rect.top + 3;    	
	   			
	   			int _x_ = spBackground.left + (edge*2) + (edge/2) + (edge/5);
		    	int _y_ = spBackground.top + edge + (edge/3);
		   		
		   		int width 	= _x_ 	+ spBackground.right;
		   		int height 	= _y_ 	+ spBackground.bottom; 	  	
		   		
		   		if (bitmapSpinner!=null) {
	        		bitmapSpinner.recycle();
	        		bitmapSpinner = null;
	        		System.gc();
	        	}     		   		
		   		
		        bitmapSpinner = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		        canvasSpinner = new Canvas(bitmapSpinner);         	        
	    		
	    		t = new Tabs();
		    	t.setParent(this);		    			
	    			    		 
		    	int[] coordsSp = { spBackground.left, spBackground.top, spBackground.right, spBackground.bottom, edge };    
		    	int[] colorSp = { r, g, b };		    	
		    	t.setTabs(coordsSp, colorSp, mP);
		    	
		    	Object[] paramsSp = new Object[arrayAmountPerType];
		    	
		    	paramsSp[0] = SwifteeApplication.PADKITE_INPUT_SPINNER_BACKGROUND;	   
		    	paramsSp[1] = SwifteeApplication.PADKITE_INPUT_SPINNER_BACKGROUND_ORIENTED_RIGHT;
		    	
		    	Path[] spinnerAll = t.drawShape(paramsSp);
		    	
		    	canvasFrame.drawPath(spinnerAll[0], pTab);
		    	canvasFrame.drawPath(spinnerAll[1], pStroke);   		    		   		
		   
		   		for (int q = 0;  q < 17; q ++) {	    	   			
		   			
		   			Paint pF = new Paint();   
	    	   	 	pF.setStyle(Paint.Style.FILL);
	    	   	 	pF.setAntiAlias(true);		    	   	 	
		            
	    	   	 	double t = 2 * Math.PI * q / 10;
		            
		            int x = (int) Math.round(_x_ + 17 * Math.cos(t));
		            int y = (int) Math.round(_y_ + 17 * Math.sin(t));
		            
		            int[] colB = ColorUtils.L2_Color(color);       
		            
		            if (status == SwifteeApplication.SUGGESTION_DATA_LOADED){        	            	 
		            	colB = colorSpinnerDots.get(q);        	            	
		            } else {
		            	Log.v("status", "status :" + status);
		            }
		            
	            	int rB = colB[0];
	        		int gB = colB[1];
	        		int bB = colB[2];		
	        		
	            	pF.setColor(Color.rgb(rB, gB, bB));
	            	canvasSpinner.drawCircle(x, y, (float) 4, pF); 	            	       	
		   		}
		   		
	    	 	int minwidth = bitmapSpinner.getWidth();  
	    	   	int minheight = bitmapSpinner.getHeight();
	    	   	
	    	   	int centrex = minwidth/2;
	    	   	int centrey = minheight/2;
		   		
	    	   	matrixSpinner = new Matrix(); 		    	   		
		   		matrixSpinner.setRotate(mDegree, _x_, _y_);   	          	   			
	   			
		   		if (status == SwifteeApplication.SUGGESTION_DATA_CALLED) {
		   			mDegree += SwifteeApplication.getSpinnerSpeed();
		   		}
	   			
	   			drawSpinner = true;
	    	   		
	   		} else {	
	   			
	   			drawSpinner = false;		   			
	   		}
		}
		
        private void frame(){    	
		   	       	        	
	   		int[] col = (int[]) suggestionColors;
			int rF = col[0];
			int gF = col[1];
			int bF = col[2];   
			int c[] = { rF, gF, bF };
			color = c;
			    			
			Object[] params = new Object[3];	
			
			params[0] = SwifteeApplication.SUGGESTION_FRAME_FOR_BUTTONS;
			params[2] = isListArmed(); 
			top = y + H*2 + (edge/5); 
			
			///**PADKITE INPUT**/    			
			/*if ( 
				(cType == SwifteeApplication.TYPE_PADKITE_INPUT)
				|| ((cType == SwifteeApplication.TYPE_PADKITE_TAB) 
				&& (lastKnownWebHitType == WebHitTestResult.EDIT_TEXT_TYPE))
				|| ((cType == SwifteeApplication.TYPE_PADKITE_ROW) 
				&& (lastKnownWebHitType == SwifteeApplication.TYPE_PADKITE_INPUT))    				
				){   					    	
				
				params[0] = SwifteeApplication.SUGGESTION_FRAME_FOR_BUTTONS;    				
				params[2] = isListArmed();   
				top = y + H*2 + (edge/5);   			
			
			/**PADKITE PANEL**/
			/*else if (cType == SwifteeApplication.TYPE_PADKITE_PANEL ) {
				
				params[0] = SwifteeApplication.SUGGESTION_FRAME_FOR_BUTTONS;
				params[2] = isListArmed(); 
				top = y + H*2 + (edge/5); 
				
			}*/
			
			/** ANCHOR
			 * WINDOWS MANAGER
			 * SERVER 
			 * **/   			
			if ( cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE // ){
					|| cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER 
					|| cType == SwifteeApplication.TYPE_PADKITE_SERVER ){   				
				   				
				params[0] = frameHorizontalOrientation;   				
				params[2] = SwifteeApplication.FRAME_ORIENT_DOWN;	    				
				top = y + H*2;	
					
				
			} 
			
			int[] coords = {x, top, W, variableHeight, edge, accumulator};   					
			   			
	    	t = new Tabs(); 
	    	t.setParent(this);
	    	t.setTabs(coords, color, mP);	    	    	
	    	pTab = t.paintTab(false);	   	
	   		
	   		isBigger = SwifteeApplication.getTextIsBigger();    	   		
		
			if (data==null) { 
				
				params[1] = false;
				Rect back_1 	= new Rect();   				
				
				if ( cType == SwifteeApplication.TYPE_PADKITE_INPUT 
						|| cType == SwifteeApplication.TYPE_PADKITE_PANEL 
						|| cType == SwifteeApplication.TYPE_PADKITE_TAB ){	   
				    					
					back_1.left 	= x + edge;
	    			back_1.top  	= top - (edge/2);
	    			back_1.right	= x + W - edge;     
	    			
	    			if (expanded){
	    				back_1.bottom 	=  top + variableHeight;
	    			} else {
	    				back_1.bottom 	=  top + variableHeight - edge;
	    			}
					
				} else if ( cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE 
						|| cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER
						|| cType == SwifteeApplication.TYPE_PADKITE_SERVER){   	
					
					if (isBigger!=-1){
						
						Rect bigger = SwifteeApplication.getBiggerRectResize();	   
						
		   				back_1.left 	= bigger.left;
	        			back_1.top  	= top;
	        			back_1.right	= bigger.right;
	        			back_1.bottom 	= top + variableHeight;
	        			
					} else {
						
						back_1.left 	= x;					
	        			back_1.top  	= top;
	        			back_1.right	= x + W;
	        			back_1.bottom 	=  top + variableHeight;	            			
					}	        				 					
				}    				
				SwifteeApplication.setBottomFormButton_0_Rect(back_1);
				
			} else {
				
				params[1] = true; 			
				
				Rect back_1 	= new Rect();
				back_1.left 	= x + edge;
				back_1.top  	= top - (edge/2);
				back_1.right	= x + W - edge;
				back_1.bottom 	=  top + variableHeight - edge;
				SwifteeApplication.setBottomFormButton_0_Rect(back_1);
				    	    	    	
				Rect back_2 	= new Rect();
				back_2.left 	= accumulator + (edge/2);
				back_2.top  	= back_1.bottom;
				back_2.right	= x + W - (3*edge);
				if (expanded){
					back_2.bottom 	=  back_1.bottom + (2*edge); //extra below not to cut interaction.
				} else {
					back_2.bottom 	=  back_1.bottom + edge + (edge/2);
				}
				SwifteeApplication.setBottomFormButton_1_Rect(back_2);    				
			}   	
			
			Path[] allFrame = t.drawShape(params);	     	   		
	   		
	   		canvasFrame.drawPath(allFrame[0], pTab);    	
	   		
	    	pStroke = t.paintTab(true);    	    	
	    	canvasFrame.drawPath(allFrame[1], pStroke);					
		
	    	/*Paint b1 = new Paint();
	    	b1.setColor(Color.CYAN);
	    	Rect bB1 = SwifteeApplication.getBottomFormButton_0_Rect();
	    	canvas.drawRect(bB1, b1);
	    	
	    	Paint b2 = new Paint();
	    	b2.setColor(Color.LTGRAY);
	    	Rect bB2 = SwifteeApplication.getBottomFormButton_1_Rect();
	    	canvas.drawRect(bB2, b2);*/	  	
	    	
	    	pKIndex = SwifteeApplication.getPKIndex();
	    	
	    	if (scroll==true){
	    		
	    		if (currentIndex!=pKIndex) {
	    			
					handler.removeCallbacks(downTask);
					handler.removeCallbacks(upTask);	    
					
					downTask	= null;
					upTask		= null;    		    					
					scroll 		= false;
					upFlag 		= false;
					downFlag 	= false;	
	    		}
			} 
        }
        
        /**
    	 * BUTTONS
    	 **/ 
        private void buttons(){        	    	
		    	    		
			   	if (buttons!=null && data!=null){
			   		
		    	   	for (int i=0; i<buttons.length; i++){    	   		
		    	   		  	   		
		    	   		int xButPos = (Integer) data[i][0];
		    	   		int xWidth = (Integer) data[i][1];
		    	   		int yButPos = (Integer) data[i][2];
		    	   		int yHeight = (Integer) data[i][3];
		    	   		buttonText = (String) data[i][4];	   			    	   		
		    	   		butColor = (int[]) data[i][5];
		    	   		
		    	   		if (expanded){
		    	   			
		    	   			if (isListArmed()){
		    	   				
		    	   				yButPos = top + yHeight - (edge*2+(edge/2));
		    	   				
		    	   			} else {
		    	   				
		    	   				yButPos = top + yHeight - (edge+(edge/2));
		    	   				
		    	   			}
		    	   			
		    	   		} else {
		    	   			yButPos = top + yHeight; 
		    	   		}
		    	   		
		    	   		int varYButton = 0;
		    	   		
		    	   		if (isListArmed()) {
		    	   			
		    	   			varYButton = yButPos + (
		    	   					SwifteeApplication.getSuggestionRowHeight()
		    	   					*SwifteeApplication.getRowAmount())
		    	   					+ edge;			    	   				
		    			} else {
		    				
		    				varYButton = yButPos;
		    				
			      	  	}
		    	   		
		    	   		if ( (cType == SwifteeApplication.TYPE_PADKITE_INPUT)
		    	   			|| (cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE)	
		        			|| ((cType == SwifteeApplication.TYPE_PADKITE_TAB) 
		        			&& (lastKnownWebHitType == WebHitTestResult.TYPE_EDIT_TEXT_TYPE))
		        			){ 
		    	   			
		    	   			if (expanded){
		    	   				
		    	   				if(!SwifteeApplication.getLandingShrinked()){
		    	   					
		    	   					varYButton += (edge/3);
		    	   					
		    	   				} else {
		    	   					
		    	   					if (isListArmed()) {
		    	   						varYButton += (edge/2);
		    	   					}
		    	   					
		    	   				}
		    	   				
		    	   			} else {		    	   				
		    	   				
		    	   				if (cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE){		    	   					
		    	   					yHeight -= edge;
		    	   					varYButton -= (edge/2);
		    	   				} else {
		    	   					varYButton -= (edge/2);
		    	   				}
		    	   				
		    	   			}	   			
		    	   			
		    	   		} else if ( cType == SwifteeApplication.TYPE_PADKITE_PANEL ){
		    	   			varYButton -= (edge+(edge/3));
		    	   		}
		    	   		
		    			int[] coordsB = {xButPos, varYButton, xWidth, yHeight, (edge-5)};    	   			
		    			t = new Tabs();
		    			t.setTabs(coordsB, null, mP); 			
		    	    	Paint pTB = new Paint();	  
		    	    	
		    	    	Object[] paramsB = new Object[1];
		    	    	paramsB[0] = SwifteeApplication.TAB_SUGGESTION_BUTTON;
		    			Path[] allButtons = t.drawShape(paramsB);
		    			
		    			realButtonId = (20-buttonOver)*(-1);		
		    			activeSearch = SwifteeApplication.getActiveSearchCheck();
		    			
		    			Paint pFill = new Paint();
		    			Paint pStrokeB = new Paint();
		    			Paint pText = new Paint();
		    			boolean fill = false;
		    			boolean stroke = false;
		    			boolean text = false;
		    			boolean overText = false; 
		    			int testSize = 0;
		    			int lowerText = 0;	    			
		    			
		    			int rowAmount = SwifteeApplication.getRowAmount();
		    			int tabIndex = SwifteeApplication.getPKTabIndex();
		    			int arrayAmount  = mP.getArrayAmountByIndex(tabIndex);
		    			
		    			if ( buttonText.equals("▼") || buttonText.equals("▲")) {
		    				
		    				if (expanded){
		    					
		    					testSize =  2*edge;
		    					varYButton += edge;
		    					
		    				} else {
		    				
		    					testSize =  edge+(edge/2);
		    					varYButton -= (edge/5);
		    					
		    				}
		    				
		    				varYButton += (edge/2);		    				
		    			
		    			} else if ( buttonText.equals("□") || buttonText.equals("■") ) {		
		    				
		    				if (expanded){
		    					
		    					testSize = 2*edge;
		    					varYButton += (edge+(edge/3));
		    					
		    				} else {
		    					
		    					testSize = edge+(edge/4);
		    					varYButton += (edge/2);
		    					
		    				}	
		    				
		    			} else {  			
		    				
		    				if (expanded){
		    					
		    					testSize = edge;
		    					varYButton += edge;
		    					
		    				} else {
		    					
		    					testSize = edge-3;
			    				varYButton += (edge/2);
			    				
		    				}
		    			}		    			 
		    			
		    			/**
		    			 * BUTTON OVER.
		    			 */				
		    			if ( (i + 20) == buttonOver) {	    				
		    				
		    				if (!(buttonText.equals("□")
								|| buttonText.equals("■"))) {
								//&& (BrowserActivity.isSuggestionListActivated()==false))) {
			    								    						
		    						int[] fillColor = new int[3];
		    						int[] strokeColor = new int[3];	   						
			    		    		
		    						if ((buttonText.equals("▲") && limitDown <= 0)
		    							|| 	(buttonText.equals("▼") && arrayAmount <= limitUp + 1 )) {
		    							
		    							fillColor 	= butColor;
		    							strokeColor = butColor;
		    							
		    						} else {
		    							
		    							fillColor 	= ColorUtils.D2_Color(butColor);
		    							strokeColor = COLOR.WHITE;
		    									
		    						}			    		    		
			    					
			    					pFill = t.paintButton(false, null,
			    							fillColor);
		    						
		    						pStrokeB = t.paintButton(true, 
		    								strokeColor, null);
		    						
		    						switch(realButtonId){
					    				case 3:
					    					realButtonId = SwifteeApplication.WIKI_BUTTON;
					    					break;					    				    					
					    				case 2:
					    					realButtonId = SwifteeApplication.VIDEO_BUTTON;
					    					break;	
					    				case 1:
					    					realButtonId = SwifteeApplication.IMAGE_BUTTON;
					    					break;					    					
		    						}	
			    				
		    						SwifteeApplication.setActiveSearch(realButtonId);	
		    				
			    					if ((!reachedTop) && (buttonText.equals("▼")) && (downTask==null) )  { 
			    						
			    						scroll = true;
			    						handler.postDelayed(downTask = new ArrowDownTask(),20);
			    						if(!downFlag){
			    							currentIndex = SwifteeApplication.getPKIndex();
			    							downFlag = true;
			    						}					
			    								    						
			    						  					
			    					} else if ((!reachedBottom) && buttonText.equals("▲") && (upTask==null)){					    					
				    						
		    							scroll = true;
		    							handler.postDelayed(upTask = new ArrowUpTask(),20);
		    							if(!upFlag){
		    								currentIndex = SwifteeApplication.getPKIndex();
		    								upFlag = true;
		    							}  			        	
					    									    				
			    					} 
			    					
			    					fill=true; stroke=true; text=true; overText=true;					
			    					
		    				} else {	 		
		    					
		    					buttonText = "■";	
		    					
		    					pText = paintButtonText(testSize, 
			    						buttonText, butColor, arrayAmount, rowAmount, false);		  
		    					
		    					SwifteeApplication.setActiveSearch(SwifteeApplication.WEB_SEARCH);	
		    					
		    					fill=false; text=true; stroke=false;	
		    					
		    				}		
		    				
		    			} else {	    					
		    				
		    				if (!buttonText.equals("□")
									|| !buttonText.equals("■")) {
			    				
			    				pFill = t.paintButton(false,	    						
			    						null, butColor);
			    				
			    				pStrokeB = t.paintButton(true, 
			    	    				butColor, null);
		    			
		    				}
		    				
		    				pText = paintButtonText(testSize, 
		    						buttonText, butColor, arrayAmount, rowAmount, false);		    				
		    				
		    				fill=true; text=true; stroke=false;		    				
		    			}   	
		    			
		    			if (i==0 && buttons.length>2) {
		    				int act = SwifteeApplication.getActiveSearchCheck();
							if ( act != -1 ){			    	    	
			    				buttonText = "□";
			    				ceroButtonMarkUpdated = true;
			    	    	} else {
			    	    		buttonText = "■";				    	    		
			    	    	}
							//Log.v("act",""+act+" "+buttonText);					
		    	    	}	
		    			
		    			if (text)
			    			pText = paintButtonText(testSize, 
		    						buttonText, butColor, arrayAmount, rowAmount, overText);	
			    			
		    	    	int textWidth = (int) pText.measureText(buttonText);	    	    	
		    	    	int butCenter = xWidth / 2;
		    	    	int textCenter = textWidth / 2;
		    	    	int xTextPos = xButPos + (butCenter - textCenter);	    	    
		    	    	int yTextPos = varYButton + (edge/2);	 
		    	    			    	    	
		    	    	if (stroke)
		    	    		canvasButtons.drawPath(allButtons[1], pStrokeB);
		    	    			    	    	
		    	    	if (fill)
		    	    		canvasButtons.drawPath(allButtons[0], pFill);	  
			    		
			    		if ( cType == SwifteeApplication.TYPE_PADKITE_PANEL ) { 
			    			yTextPos += (edge/2);		    			
			    		}			    		
			    		
			    		if (text)	    			
			    			canvasButtons.drawText(buttonText, xTextPos, yTextPos, pText);  
			    		
			    		if (data!=null){
			    			
			    	    	if (data.length>0){  	    		
			    	   			
			    	   			Rect action = new Rect();
				    	   		action.left = xButPos;
				    	   		if (expanded){
				    	   			action.top = varYButton - edge;
				    	   		} else {
				    	   			action.top = varYButton - (edge/2); 
				    	   		}
				    	   		action.right = xButPos + xWidth;
				    	   		action.bottom = action.top + yHeight;
				    	   		
				    	    	switch (i){
					    			case 0:
					    				SwifteeApplication.setSuggestionButton_0_Rect(action);
					    				break;
					    			case 1:
					    				SwifteeApplication.setSuggestionButton_1_Rect(action);
					    				break;
					    			case 2:
					    				SwifteeApplication.setSuggestionButton_2_Rect(action);
					    			case 3:
					    				SwifteeApplication.setSuggestionButton_3_Rect(action);
					    			case 4:
					    				SwifteeApplication.setSuggestionButton_4_Rect(action);
					    			case 5:
					    				SwifteeApplication.setSuggestionButton_5_Rect(action);
					    				break;				    							    			  
						    	}   		
				    	    	//drawButtonsActionsOnScreen(canvas, i); 
			    	    	}
			    		}
		    	    	
		    	   	}
				} 
           }
        
        
        private void rows(){        
    	   		
 	   			BrowserActivity.setSuggestionListActivated(true);
	   					    	   		
    	   		Rect re = new Rect();
    	   		
    	   		/*if ((cType == SwifteeApplication.TYPE_PADKITE_INPUT
    	   			|| cType == SwifteeApplication.TYPE_PADKITE_PANEL)
    	   			
    	   			|| ((cType == SwifteeApplication.TYPE_PADKITE_TAB)		    	   					
    	   			&& (lastKnownWebHitType == WebHitTestResult.EDIT_TEXT_TYPE))
    	   			
    	   			|| ( (cType == SwifteeApplication.TYPE_PADKITE_TAB)		    	   					
    	   			&& (lastKnownWebHitType == SwifteeApplication.TYPE_PADKITE_INPUT))
    	   			
    	   			|| ( (cType == SwifteeApplication.TYPE_PADKITE_ROW)		    	   					
    	   			&& (lastKnownWebHitType == SwifteeApplication.TYPE_PADKITE_INPUT)))
    	   			
    	   		{	    	   		
	    	   		
        			re.left = rect.left + (edge + (edge/2));
        			re.right = rect.right - (edge + (edge/2));        			
        			re.top = rect.bottom;
        			yRow = rect.bottom + 3; */
        			
    	   		if ( cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE //){
    	   			||	cType ==  SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER 
    	   			||	cType ==  SwifteeApplication.TYPE_PADKITE_SERVER ){
    	   			
    	   			re.top = rect.bottom - (edge/3);
    	   			re.left = rect.left;  	  	    	   			
    	   			
    	   			if (isBigger!=-1){
	    	   			
    	   				Rect bigger = SwifteeApplication.getBiggerRectResize();
    	   				re.left = bigger.left + (edge/2);   	   				 
    	   				
    	   				switch(isBigger){
    	   					case SwifteeApplication.TEXT_IS_BIGGER_LEFT:
    	   						re.left = re.left - (edge/8);
		    	   				re.right = bigger.right - (edge/4); 
    	   						break;
    	   					case SwifteeApplication.TEXT_IS_BIGGER_CENTER:
    	   						re.left = re.left - (edge/4);
		    	   				re.right = bigger.right - (edge/4);
    	   						break;
    	   					case SwifteeApplication.TEXT_IS_BIGGER_RIGHT:
    	   						re.left = re.left - (edge/4);
    	   						re.right = bigger.right - (edge/2);
    	   						break;		    	   						
    	   				} 			
	    	   			
	    	   			//Paint pa = new Paint();
	    	   			//canvas.drawRect(bigger, pa);
	    	   			
    	   			} else {
    	   				
    	   				re.left = rect.left - (edge/8);  	
    	   				re.right = rect.right;
    	   				
    	   			}	   			
        			
        			textYLess = (edge/6);    
        			
        			yRow = rect.bottom - (edge/6);
        			
    	   		}  else {
    	   					    	   			
    	   			if ((cType == SwifteeApplication.TYPE_PADKITE_INPUT
    	   			|| cType == SwifteeApplication.TYPE_PADKITE_PANEL
    	   			|| cType == SwifteeApplication.TYPE_PADKITE_ROW
    	   			|| cType == SwifteeApplication.TYPE_PADKITE_TAB)){
    	   				
    	   				re.left = rect.left + (edge + (edge/2));
	        			re.right = rect.right - (edge + (edge/2));
	        			
    	   			} else {
    	   				
    	   				re.left = rect.left + edge;
	        			re.right = rect.right - edge; 
	        			
    	   			}	    	   			
    	   			        			
        			re.top = rect.bottom;
        			yRow = rect.bottom + 3;
    	   			
    	   		}
    	   		
    			re.bottom = re.top 
    					+ (SwifteeApplication.getSuggestionRowHeight()*SwifteeApplication.getRowAmount())
    					- edge; 
    	   		
    	   		//Path[] rounded = t.drawRoundedSquare(re);
    	   		//Paint rBorderLine = paintRow(false);
	    		//canvas.drawPath(rounded[0], rBorderLine);	
	    		
	    		//Paint rBorderFill = paintRow(false);
	    		//canvas.drawPath(rounded[1], rBorderFill);        			
	    	   	
    			  	
	        	int j = 0;
	        	
	        	Rect lastRect = new Rect();  
	        	
	        	Rect belowRect = new Rect();
	        	
	        	reRowArray = new Rect[topUp];
	        	
        		for (k=0; k<topUp; k++){     			
        			
        			if (k==0){ 
        				rowHeight=0;	
        			} else {
        				rowHeight=SwifteeApplication.getSuggestionRowHeight();
        			} 
        			
        			Path[] row = new Path[2];
        			
        			Rect reRow = new Rect();
        			reRow.left = re.left + 2;			        				  
        			reRow.top = yRow + (SwifteeApplication.getSuggestionRowHeight()*k)+(edge/2)+(edge/5);
        			reRow.right = re.right - 2;
        			reRow.bottom = reRow.top + SwifteeApplication.getSuggestionRowHeight()-(edge/5);
        			
        			if (k==0){
        				belowRect.top = reRow.top;
        				belowRect.left = reRow.left;
        				belowRect.right = reRow.right;        				
        			} else if (k==topUp-1){
        				belowRect.bottom = reRow.bottom;
        				SwifteeApplication.setBottomFormButton_2_Rect(belowRect);      		    	
        			}
        			
        			reRowArray[k] = reRow; 
        			
        			//Set listener action
        			switch(k){
	        			case 0:
	        				SwifteeApplication.setSuggestionRow_0_Rect(reRow);
	        				break;
	        			case 1:
	        				SwifteeApplication.setSuggestionRow_1_Rect(reRow);
	        				break;
	        			case 2:
	        				SwifteeApplication.setSuggestionRow_2_Rect(reRow);
	        				break;
	        			case 3:
	        				SwifteeApplication.setSuggestionRow_3_Rect(reRow);
	        				break;
	        			case 4:
	        				SwifteeApplication.setSuggestionRow_4_Rect(reRow);
	        				break;	
	        			case 5:
	        				SwifteeApplication.setSuggestionRow_5_Rect(reRow);
	        				break;	
        			}
        			
        			if (arraySuggestion.length==1){
        				row = t.drawTopRow(reRow, SwifteeApplication.DRAW_ROW_ROUNDED);
        			} else {
        				if (k==0){			        			
	        	    		row = t.drawTopRow(reRow, SwifteeApplication.DRAW_ROW_TOP);			        	    		
	        	    	} else if (k>=topUp-1){
	        	    		row = t.drawTopRow(reRow, SwifteeApplication.DRAW_ROW_BOTTOM);
	        	    	} else {
	        	    		row = t.drawTopRow(reRow, SwifteeApplication.DRAW_ROW_MIDDLE);			        	    	
	        	    	} 	
        			}	        			
        			
        			Paint rRowLine = new Paint();
        			Paint rRow = new Paint();
        			Paint tPaint = new Paint();
        			int textSize = SwifteeApplication.getSuggestionRowTextSize();
        			
        			if ( k+10 == rowOver ){
        				rRowLine = paintRow(false, COLOR.WHITE);	        				
        				rRow = paintRow(true, ColorUtils.D2_Color(tabColor));
        				tPaint = paintText(textSize, COLOR.WHITE);
        			} else {
        				//rRowLine = paintRow(true, ColorUtils.checkDarkColor(tabColor));		        				
        				/*if (StringUtils.isOdd(k)){
        					rRow = paintRow(true, ColorUtils.checkLightColor(tabColor));
        				} else {
        					rRow = paintRow(true, SwifteeApplication.WHITE);		        					
        				}*/		        				
        				rRow = paintRow(true, COLOR.WHITE);
        				tPaint = paintText(textSize, COLOR.GRAY);
        			}		  	 
        			
        			canvasRows.drawPath(row[0], rRowLine);   			        	    		
        			canvasRows.drawPath(row[1], rRow);        	    			
    	    		
        	    	//String fitText = PaintTextUtils.CalculateStringFit(text, reRow.width(),tPaint);
        	    	
        	    	int bottomLight = (int) ((SwifteeApplication.getSuggestionRowTextSize() - edge)*1.5);
        	    	
        	    	int textYPos; 
        	    	
        	    	if(fC.isExpandToFinger()){		        	    		
        	    		textYPos = reRow.bottom - (edge/2);		        	    				
        	    	} else {
        	    		textYPos = reRow.bottom - bottomLight - textYLess;
        	    	}
        	    	int leftText = 0;
        	    	
        	    	if (hasVideos){
        	    		leftText = reRow.left + (edge*5);
        	    	} else {
        	    		leftText = reRow.left + (edge/2);		        	    		
        	    	}
        	    	
        	    	
        	    	
        	    	if (hasWiki){        	    		
        	    	    
        	    		String text = text = (String) objectList[k][1];
        	    		
        	    		String line_two_abs =  (String) objectList[k][3];
        	    		
        	    		if (isNullOrBlank(line_two_abs)){
        	    			
        	    			Paint sPaint = paintText(30, COLOR.GRAY);
        	    			 	    			
        	    			String line_one_title =  (String) objectList[k][1];	
        	    			
        	    			int ttlLength = line_one_title.length();        	    				
    	    				int rowWidth = reRow.width();        	    				
    	    				int ttltextwidth = (int) sPaint.measureText(line_one_title);    
    	    				
    	    				if (ttltextwidth>=rowWidth){
    	    					int newLength =  ((rowWidth * ttltextwidth) / ttlLength) -5;        	    				
    	    					line_one_title = line_two_abs.substring(0, newLength) + "...";
    	    				}   				
        	    			
        	    			canvasRows.drawText(line_one_title, leftText, textYPos-(edge/2), sPaint);        	    			
        	    			
        	    		} else {
        	    			
        	    			Paint sPaint = paintText(25, COLOR.GRAY);
        	    			
        	    			String line_one_title =  (String) objectList[k][1];	    			
        	    			canvasRows.drawText(line_one_title, leftText, textYPos-(edge+(edge/3)), sPaint);
        	    			
        	    			Paint aPaint = paintText(15, COLOR.BLACK);        	    			 
        	    			
        	    			if (!isNullOrBlank(line_two_abs)){
        	    				
        	    				int absLength = line_two_abs.length();        	    				
        	    				int rowWidth = reRow.width();        	    				
        	    				int absTextwidth = (int) aPaint.measureText(line_two_abs);        	    				
        	    				int newLength =  ((rowWidth * absLength) / absTextwidth) -5;        	    				
        	    				String shorten = line_two_abs.substring(0, newLength);
        	    				
        	    				canvasRows.drawText(shorten+"...", leftText, textYPos-(edge/5), aPaint);
        	    			}
        	    		}     	    		
        	    		
        	    		
        	    	}
        	    	
        	    	else if (hasVideos){	
        	    		
        	    		String text = text = (String) objectList[k][1];
        	    		
        	    		Paint vPaint = paintText(20, COLOR.GRAY);
        	    		
        	    		int videoTextSize = (int) tPaint.measureText(text);
        	    		int module = reRowArray[k].height()-(edge/2);
        	    		int rowWidth = reRowArray[k].width() - module; // - Image
        	    		
        	    		if (videoTextSize>rowWidth){
        	    			
        	    			int charsInRow =  ((rowWidth * text.length()) / videoTextSize);
        	    			
        	    			String line_one = text.substring(0, charsInRow);	        	    			
        	    			canvasRows.drawText(line_one, leftText, textYPos-(edge*3+(edge/4)), vPaint);
        	    			
        	    			String line_two = text.substring(charsInRow, text.length());
        	    			canvasRows.drawText(line_two, leftText, textYPos-(edge*2), vPaint);
	        	    		
        	    		} else {
        	    			
        	    			canvasRows.drawText(text, leftText, textYPos-(edge*2+(edge/2)), tPaint);
        	    			
        	    		}
        	    		
        	    		String extra = text = (String) objectList[k][3];   	    		
        	    		
        	    		if (extra.contains("by")){	
        	    			
        	    			String[] extraLikes = extra.split("by"); 
        	    			String likes = extraLikes[0];  
        	    		
	        	    		int leftFrom = reRowArray[k].left + module + edge;		        	    		
	        	    		Paint extraPaint = paintText(15, COLOR.BLACK);
	        	    		canvasRows.drawText(likes, leftText, textYPos-edge, extraPaint);
	        	    		
	        	    		Paint paintLikes = paintText(17, COLOR.BLACK);
	        	    		String by = "by " + extraLikes[1];
	        	    		canvasRows.drawText(by, leftText, textYPos, paintLikes);
        	    		}
	        	    		
        	    	} else {
        	    		
        	    		String text = text = (String) objectList[k][1];
        	    		
        	    		canvasRows.drawText(text, leftText, textYPos+(edge/6), tPaint);
        	    	}
        	    	
        	    	// http://developer.android.com/guide/topics/ui/themes.html#DefiningStyles
        			lastRect = re;
        			ceroRowMarkUpdated=true;
        		}       		     	   	
    	   	
   			}	  
        
        private void videos(Canvas c) {
        	
    		//VIDEO OVERLAY
    		//http://stackoverflow.com/questions/5433525/videoview-on-top-of-surfaceview
    		//http://stackoverflow.com/questions/3466473/android-cannot-see-video-on-htc-hero-1-5
        	
			for (r=0; r<topUp; r++){     
				
				canvasVideos = new Canvas();
				
				if (bitmapVideos!=null) {
					bitmapVideos.recycle();
					bitmapVideos = null;
	        		System.gc();
	        	}  	    			
	
				int go = 0;
				int length = arraySuggestion.length;    
				
				
				if (scroll==true){
					
						if (SwifteeApplication.getScrollingDirection()
							== SwifteeApplication.SCROLLING_DIRECTION_DOWN 
							&& ( (upId+r)  <= length-1 )
						){ 
							
							go = r + upId;
							
						} else if (SwifteeApplication.getScrollingDirection()
								== SwifteeApplication.SCROLLING_DIRECTION_UP
							&& ((downId+r) >= 0 )){
							
							go = downId + r;									
						} 
						
						else {			
							go = r;	  
						} 
						
						
					 
				} else {			
					go = r;	  
				}
				
				int aSize = mP.getVideoBitmapsArraySize();
				Log.v("go", "go: " + go);
				
				bitmapVideos = mP.getVideoBitmaps(go);	
	
				int module = reRowArray[r].height()-(edge/2);
				    	    			
				bitmapVideos = Bitmap.createScaledBitmap(bitmapVideos, module, module, false);    	    			
				bitmapVideos = getRoundedCornerBitmap(bitmapVideos);
				   		
		   		int imgBitmapWidth = bitmapVideos.getWidth(); 
		   		int imgBitmapHeight = bitmapVideos.getHeight();   	
		   		
		   		int left = reRowArray[r].left + (edge/3);       			   		
		   		int top =  reRowArray[r].top + (edge/3);       			   		
		   		
		   		Rect imageRect = new Rect(left, top, imgBitmapWidth, imgBitmapHeight);    			   		
		   		   			   		
		   		ImageView searchVideo = new ImageView(context);
		   		Matrix matrixVideo = new Matrix();
		        
		        matrixVideo.setTranslate(left,top);
	
		        Paint pI = new Paint();
		        canvasVideos.drawBitmap(bitmapVideos, matrixVideo, pI);    			        
		        searchVideo.setImageBitmap(bitmapVideos);      		           	    
		  	        			        
		       	c.drawBitmap(bitmapVideos, matrixVideo, null);    

		}
        
	} 
        
        private void images(Canvas c){ 
        
			canvasImages = new Canvas();   			
			
			int length = arraySuggestion.length;
			
			for (int s=0; s<length; s++){ 			
				
				RelativeLayout relativeView = new RelativeLayout(context);
				
				bitmapImages = null;
				bitmapImages = mP.getImageBitmaps(s);   	    				
	
				int module = (Integer) imagesLocationData[s][2];    	    			
				bitmapImages = Bitmap.createScaledBitmap(bitmapImages, module-(edge/2), module-(edge/2), false);    	    			
				bitmapImages = getRoundedCornerBitmap(bitmapImages);
				   		
		   		int imgBitmapWidth = bitmapImages.getWidth(); 
		   		int imgBitmapHeight = bitmapImages.getHeight();   	
		   		
		   		int left = (Integer) imagesLocationData[s][0];       			   		
		   		int top = (Integer) imagesLocationData[s][1];  		
		   		
		   		if (s >= 4){ //TWO ROWS
		   			top += module + edge;
				} 
		   		
		   		Rect imageRect = new Rect(left, top, imgBitmapWidth, imgBitmapHeight);    			   		
		   		   			   		
		   		searchImage = new ImageView(context);
		        matrixImage = new Matrix();    			        
		        
		        matrixImage.setTranslate(left,top);
	
		        Paint pI = new Paint();
		        canvasImages.drawBitmap(bitmapImages, matrixImage, pI);    			        
		        searchImage.setImageBitmap(bitmapImages); 	        
		        
		        relativeView.addView(searchImage);   	
		        
		        c.drawBitmap(bitmapImages, matrixImage, null);
		        
	    		switch(s){
	    			case 0:
	    				
	    				SwifteeApplication.set_Image_Button_0(imageRect);
	    				break;
	    			case 1:
	    				SwifteeApplication.set_Image_Button_1(imageRect);
	    				break;
	    			case 2:
	    				SwifteeApplication.set_Image_Button_2(imageRect);
	    				break;
	    			case 3:
	    				SwifteeApplication.set_Image_Button_3(imageRect);
	    				break;
	    			case 4:
	    				SwifteeApplication.set_Image_Button_4(imageRect);
	    				break;	
	    			case 5:
	    				SwifteeApplication.set_Image_Button_5(imageRect);
	    				break;
	    			case 6:
	    				SwifteeApplication.set_Image_Button_6(imageRect);
	    				break;	
	    			case 7:
	    				SwifteeApplication.set_Image_Button_7(imageRect);
	    				break;	
	    			case 8:
	    				SwifteeApplication.set_Image_Button_8(imageRect);
	    				break;			        			
				}
				drawButtonsImagesOnScreen(canvasImages, s);     	    			
			}
        }  
              
		
        public Paint paintButtonText(int size, 
        		String text, int[] color, int arrayAmount, 
        		int rowAmount, boolean over) {		
    		
    		Paint p = new Paint();
    		p.setStyle(Paint.Style.FILL);
    		p.setAntiAlias(true);  		
    			
    		if (!over) {    		 		 
	    		
	    		//ARROW DOWN			
	    		if ((text.equals("▼") && arrayAmount >= limitUp)) {				
	    			p.setColor(Color.WHITE);			
	    		} else if ((text.equals("▼") && arrayAmount <= limitUp+1 )){
	    			p = auxiliarPaint(color);	
	    		} 
	    		
	    		//ARROW UP
	    		else if ((text.equals("▲") && limitDown > 0)) {				
	    			p.setColor(Color.WHITE);    			    						
	    		} else if ((text.equals("▲") && limitDown <= 0)) {
	    			p = auxiliarPaint(color);
	    		} 
	    		
	    		//SQUARE
	    		else if (text.equals("□")){	
	    			p.setColor(Color.WHITE);				
	    		} else if (text.equals("■")){				
	    			p = auxiliarPaint(color);	    			
	    		} else {
	    			//TEXT
	    			p.setColor(Color.WHITE);
	    		}
	    		
    		} else {     
    			
    			if (text.equals("▼")) {    				
	    			
    				if (!reachedTop){
	    				p.setColor(Color.WHITE);
	    			} else if (reachedTop) {
	    				p = auxiliarPaint(color);    				  
	    			}
	    			
    			} else if (text.equals("▲")) {    				
    				
    				if (!reachedBottom){
	    				p.setColor(Color.WHITE);
	    			} else if (reachedBottom) {
	    				p = auxiliarPaint(color);    				  
	    			}
    				
    			} else {    				
    				
    				p.setColor(Color.WHITE);
    				
    			}
    			
    		}
    		
    		p.setTypeface(Typeface.DEFAULT_BOLD);
    		p.setTextSize(size);
    		
    		return p;
        }
        
        public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) { 
        	
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), 
                bitmap.getHeight(), Config.ARGB_8888); 
            Canvas canvas = new Canvas(output);
            final int color = 0xff424242; 
            final Paint paint = new Paint();           
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
            final RectF rectF = new RectF(rect);
            final float roundPx;
            if (hasVideos){
            	roundPx = (edge/3);
            } else {
            	roundPx = (edge/2);
            }
            paint.setAntiAlias(true); 
            canvas.drawARGB(0, 0, 0, 0); 
            paint.setColor(color); 
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
            canvas.drawBitmap(bitmap, rect, rect, paint); 

            return output; 
          } 
         
        
        private boolean runOverAsyncOnce;       

        private AsyncTask searchTask = new SearchTask();
        
        private class SearchTask extends AsyncTask  {    
	          
			@Override
			protected Object doInBackground(Object... arg) {	
				String inputText = (String) arg[0];
				mP.launchSuggestionSearch(inputText);   			
				return false;
			}		
		};   
        
        public void setVariableHeight(int variableHeight) {
        	this.variableHeight = variableHeight;
        }
       
        private ArrowDownTask downTask;     
        private int limitUp;
        private int limitDown;        
        private boolean reachedTop;   
        private boolean direction;
        
        
        public class ArrowDownTask implements Runnable {
         	
     	    public void run() {  	      
     	    	
     	    	if (arraySuggestion!=null){      	    		
     	    	
	     	    	int length = arraySuggestion.length;    
	     	    	int listLength = objectList.length;
	     	    	
	     	    	if ( upId  < length-1 ){		
	     	    			     	    		
	     	    		upId++;
	     	    		SwifteeApplication.setScrollingDirection(SwifteeApplication.SCROLLING_DIRECTION_DOWN);
	     	    		String newText = arraySuggestion[upId][0];
	     	    	
	     	    		String newVideoUrl = null;
	     	    		String newExtra = null;
	     	    		
	     	    		if (hasVideos){ 		    					
	     	    			newVideoUrl = (String) arraySuggestion[upId][1];
	     	    			newExtra = (String) arraySuggestion[upId][2];  					
	   					} 	
	     	    		
	     	    		for (int i=0; i<listLength; i++){
	     	    			int id;
	     	    			
	     	    			String text; 
	     	    			String videoUrl = null;
	     	    			String extra 	= null;
	     	    			
	 		    			if (i==listLength-1){		    				 
	 		    				id = upId;		    				
	 		    				text = newText;
	 		    				videoUrl = newVideoUrl;
	 		    				extra = newExtra;
	 		    				downId = id - listLength + 1;
	 		    			} else {		    						    				
	 		    				text = (String) objectList[i+1][1];
	 		    				if (hasVideos){
	 		    					videoUrl = (String) objectList[i+1][2];
	 		     	    			extra = (String) objectList[i+1][3];  
	 		    				}
	 		    				id = i+1; 		    				
	 		    			}	
	 		    			
	 		    			limitUp = upId;
	 		    			limitDown = limitUp - listLength;
	 		    			
	 		    			objectList[i][0] = id;
	 		    			objectList[i][1] = text;
	 		    			if (hasVideos){
	 		    				objectList[i][2] = videoUrl;
	 		    				objectList[i][3] = extra;
	 		    			}
	 		    			
	     	    		}
	     	    		
	     	    		if (upId > 0) {
	 	 	    		   
		 	    		    reachedBottom = false;
		 	    	   }
	     	    		
	     	    		handler.postDelayed(this, 150);   	    			
	     	    		invalidate();
	     	    		
	 	    	   }  else if ( upId  == length-1 ){  		   
	 	    		   
	 	    		   	handler.removeCallbacks(this);

	 	    		   	reachedTop		= true;
		    		   	scroll			= false;    
		     	    	downTask		= null;		    			    	   	
		     	    	currentIndex 	= -1; 	   
	 	    	  
	 	    	   } else {	 	    		
	 	    		  
	 	    		   reachedTop		= false;
	 	    		 
	 	    	  }
	     	    	
     	    	}
     	    }   
     	    
        }  

       
       private ArrowUpTask upTask;
       private boolean reachedBottom = true;
       
       public class ArrowUpTask implements Runnable {
       	
   	    public void run() {  	      
   	    	
   	    	if (arraySuggestion!=null){ 	    		
   	    	
	   	    	int length = arraySuggestion.length;    
	   	    	int listLength = objectList.length;
	   	    	
	   	    	if ( downId  > 0 ){		
	   	    		
	   	    		downId--;    	    
	   	    		SwifteeApplication.setScrollingDirection(SwifteeApplication.SCROLLING_DIRECTION_UP);
	   	    		int adjust = downId; 
	   	    		String newText = arraySuggestion[adjust][0];  
	   	    		
	   	    		String newVideoUrl = null;
     	    		String newExtra = null;
     	    		
     	    		if (hasVideos){ 		    					
     	    			newVideoUrl = (String) arraySuggestion[adjust][1];
     	    			newExtra = (String) arraySuggestion[adjust][2];  					
   					} 
	   	    		
	   	    		for (int i=topUp-1; i>=0; i--){
	   	    			int id;
	   	    			
	   	    			String text;   	    			 
     	    			String videoUrl = null;
     	    			String extra 	= null;
     	    			
		    			if (i==0){
		    				id = downId; 
		    				text = newText;		    				
 		    				videoUrl = newVideoUrl;
 		    				extra = newExtra;				
		    				upId = id + listLength;	    				
		    			} else {		    						    				
		    				text = (String) objectList[i-1][1];
		    				if (hasVideos){
 		    					videoUrl = (String) objectList[i-1][2];
 		     	    			extra = (String) objectList[i-1][3];  
 		    				}
		    				id = i-1;   				
		    			}
		    			
		    			Log.v("limitDown", "limitDown :" + limitDown );
		    			
		    			//TAKES AWAY FROM TOP
		    			reachedTop = false;  		    	 
		    			
		    			limitDown = downId;		    
		    			limitUp = downId + listLength-1;
		    			
		    			objectList[i][0] = id;
		    			objectList[i][1] = text;
		    			
	   	    		}   	    		
	   	    		handler.postDelayed(this, 150);   	    			
	   	    		invalidate(); 
	   	    		
		    	   } else if (downId==0){		    		   
		    		   
		   	    		handler.removeCallbacks(this); 	    	
		   	    		reachedBottom 	= true;
		   	    		scroll			= false;   	
		   	    		upTask			= null;
		   			    currentIndex 	= -1;
		   			    
		    	   }   
   	    	}
   	    }   
   	           	
   }    
   
   	
	private Paint auxiliarPaint(int[] color){
		Paint p = new Paint();
		int[] c = ColorUtils.checkDarkColor(color); 
		int r = c[0];
		int g = c[1];
		int b = c[2];   		
		p.setColor(Color.rgb(r, g, b));
		return p;
	} 
    
	private Paint lightPaint(int[] color){
		Paint p = new Paint();
		int[] c = ColorUtils.checkLightColor(color); 
		int r = c[0];
		int g = c[1];
		int b = c[2];   		
		p.setColor(Color.rgb(r, g, b));
		return p;
	} 

	private Paint paintRoundedRect(boolean fill){
    	Paint p = new Paint();
    	p.setAntiAlias(false);    	
    	if(fill){
    		p.setStyle(Paint.Style.FILL);
    		p.setColor(Color.WHITE); //fillColor    		
    	} else{
    		p.setStyle(Paint.Style.STROKE);
    		p.setStrokeWidth(1);    		
    		p.setColor(Color.BLACK);    		
    	}
    	return p;
    }
    
   public boolean isListArmed(){
	   if( arraySuggestion!=null){    	    	   		
   	   		if (arraySuggestion.length>0){
   	   			return true;
   	   		}
	   }
	   return false;
   }
  
    
    public Tabs getActiveTab(){
    	return t;
    }
    
    int v = 60;

	private int rectTop;
	private Object[][] imagesLocationData;    

    private void finalArrow(Canvas canvas, int x, int y){    	
    	
    	Paint pBig = paintArrow(3);
    	Path sBig = new Path();    	
    	sBig.moveTo(x-20, y);
    	sBig.lineTo(x, y-20);
    	sBig.lineTo(x+20, y);    	
    	canvas.drawPath(sBig, pBig);   	    
    }       
    
    private Paint paintRow(boolean fill, int[] rowColor){
    	Paint pS = new Paint();
    	pS.setAntiAlias(true);
    	int[] c = (int[]) rowColor;
		int r = c[0];
		int g = c[1];
		int b = c[2];
    	if(fill){
    		pS.setStyle(Paint.Style.FILL);     		   	
			pS.setColor(Color.rgb(r, g, b));
    	} else{
    		pS.setStyle(Paint.Style.STROKE);
    		pS.setStrokeWidth(2);        		
    		pS.setColor(Color.rgb(r, g, b));
    	}    	  	
	    return pS;
    }
    
    private Paint paintArrow(int width){
    	Paint pSpaint = new Paint();
    	pSpaint.setStyle(Paint.Style.STROKE);
    	pSpaint.setStrokeWidth(width);
    	pSpaint.setAntiAlias(false);
    	pSpaint.setColor(Color.rgb(r, g, b)); //fillColor
	    return pSpaint;
    }
    
    public Paint paintText(int size, int[] color){
    	Paint pText = new Paint();
		pText.setStyle(Paint.Style.FILL);		
		pText.setAntiAlias(true);	
		int r = color[0];
		int g = color[1];
		int b = color[2];
		pText.setColor(Color.rgb(r, g, b));		
		pText.setTypeface(Typeface.DEFAULT_BOLD);
		pText.setTextSize(size);
		return pText;
    }
    
    
    public String[][] getArraySuggestion() {
		return arraySuggestion;
	}
    
	public void cleanArray(){
		this.arraySuggestion = null;
	}


	public Rect getRect() {
		return rect;
	}

	public void setRect(Rect rect) {
		this.rect = rect;
	}

	public Rect getRecentRect(){
		return recentRect;
	}    
	
	public void setDrawType(int draw) {
		this.draw = draw;
	} 	

   public void setCords(int[] cords) {
	    x = (Integer)cords[0]; 
	    y = (Integer)cords[1];
	    W = (Integer)cords[2];
	    H = (Integer)cords[3];	
	    accumulator = (Integer)cords[4];
	    this.cords = cords;	
  }
   
   
   public void setColors(int[] colors){
	   
   }
   
	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
		
	public void setGrandParent(BrowserActivity bA){
		mP = bA;		
	}
		
	public void setFloatingCursror(FloatingCursor f){
		fC = f;		
	}
	
	public int[] getSuggestionColors() {
		return suggestionColors;
	}

	public void setSuggestionColors(int[] suggestionColors) {	 
 		this.suggestionColors = suggestionColors;
		this.tabColor = suggestionColors;
	}
	
	public String[] getButtons() {
		return buttons;
	}

	public void setButtons(String[] buttons) {
		this.buttons = buttons;
	}
	
	public int getEdge() {
		return edge;
	}

	public void setEdge(int edge) {
		this.edge = edge;
	}
	
	public int getSuggestionHeight() {
		return suggestionHeight;
	}

	public void setSuggestionHeight(int sH) {
		this.suggestionHeight = sH;
	}
 
	public int getRowOver() {
		return rowOver;
	}

	int lastRow = -1;
	
	public void setRowOver(int rowOver) {		
		if (lastRow != rowOver){	
			switchRowBitmap = true;
			addRowId++;
			bitmapRowKey =  bitmapFrameKey + "-" + "r-" + rowOver + "-" + SwifteeApplication.getActiveTabIndex() + "-" + addRowId;		
			this.rowOver = rowOver;	
			lastRow = rowOver;
		}		
	}
	
	public void cleanOvers(){
		
		if (!isNullOrBlank(bitmapRowKey)) {
			addRowId++;
			String rowString = bitmapFrameKey + "-" + "r-" + "none" + "-" + SwifteeApplication.getActiveTabIndex() + "-" + addRowId;
			if (!bitmapRowKey.equals(rowString)){
				switchRowBitmap = true;			
				bitmapRowKey =  rowString;			
				this.rowOver = -1;			
			}
		}
		
		if (!isNullOrBlank(bitmapButtonKey)) {
			addButtonId++;
			String buttonString = bitmapFrameKey + "-" + "b-" + "none" + "-" + SwifteeApplication.getActiveTabIndex() + "-" + addButtonId;
			if (!bitmapButtonKey.equals(buttonString)){
				bitmapButtonKey = buttonString;
				switchButtonBitmap = true;
				this.buttonOver = -1;			
			}
		}
	}
	

	private static boolean isNullOrBlank(String s){
	  return (s==null || s.trim().equals(""));
	}

	public void cleanButtonsOver(){	
		switchButtonBitmap = true;
		addButtonId++;
		bitmapButtonKey =  bitmapFrameKey + "-" + "r-" + "none" + "-" + SwifteeApplication.getActiveTabIndex() + "-" + addButtonId;
		this.buttonOver = -1;
	}
	
	public void cleanRowsOver(){
		addRowId++;
		String rowString = bitmapFrameKey + "-" + "r-" + "none" + "-" + SwifteeApplication.getActiveTabIndex() + "-" + addRowId;
		if (!bitmapRowKey.equals(rowString)){
			switchRowBitmap = true;
			bitmapRowKey =  bitmapFrameKey + "-" + "r-" + "none" + "-" + SwifteeApplication.getActiveTabIndex() + "-" + addRowId;;
			this.rowOver = -1;		
		}
	}
	
	public int getButtonOver() {
		return buttonOver;
	}

	int lastButton = -1;
	
	public void setButtonOver(int buttonOver) {
		if (lastButton != buttonOver){	
			switchButtonBitmap = true;
			addButtonId++;
			bitmapButtonKey =  bitmapFrameKey + "-" + "b-" + buttonOver + "-" + SwifteeApplication.getActiveTabIndex() + "-" + addButtonId; 
			this.buttonOver = buttonOver;
			lastButton = buttonOver;
		}
	}
	
	public int getButtonDown() {
		return buttonDown;
	}

	public void setButtonDown(int buttonDown) {
		this.buttonDown = buttonDown;
	}
	
	 public Object[][] getData() {
		return data;
	}

	public void setData(Object[][] data) {
		this.data = data;
	}
	
	public void setImagesLocationData(Object[][] data) {
		this.imagesLocationData = data;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	 public String getButtonText() {
			return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}
	
	public int getFrameHorizontalOrientation() {
		return frameHorizontalOrientation;
	}

	public void setFrameHorizontalOrientation(int frameHorizontalOrientation) {
		this.frameHorizontalOrientation = frameHorizontalOrientation;
	}
	
	public int getFrameVerticalOrientation() {
		return frameVerticalOrientation;
	}

	public void setFrameVerticalOrientation(int frameVerticalOrientation) {
		this.frameVerticalOrientation = frameVerticalOrientation;
	}

	private void drawButtonsActionsOnScreen(Canvas canvas, int i){
		
		Paint bP = new Paint();
		Rect bR = new Rect();
		
		switch (i){
			case 0:
				bP.setColor(Color.BLACK);
				bR = SwifteeApplication.getSuggestionButton_0_Rect();
				break;
			case 1:
				bP.setColor(Color.GREEN);
				bR = SwifteeApplication.getSuggestionButton_1_Rect();
				break;
			case 2:
				bP.setColor(Color.BLUE);
				bR = SwifteeApplication.getSuggestionButton_2_Rect();
				break;				    							    			  
			case 3:
				bP.setColor(Color.YELLOW);
				bR = SwifteeApplication.getSuggestionButton_3_Rect();
				break;
			case 4:
				bP.setColor(Color.RED);
				bR = SwifteeApplication.getSuggestionButton_4_Rect();
				break;
			case 5:
				bP.setColor(Color.YELLOW);
				bR = SwifteeApplication.getSuggestionButton_5_Rect();
				break;			    					
		}
		canvas.drawRect(bR, bP);
	}	
	
	private void drawButtonsImagesOnScreen(Canvas canvas, int i){
		
		Paint bP = new Paint();
		Rect bR = new Rect();
		
		switch (i){
			case 0:
				bP.setColor(Color.BLACK);
				bR = SwifteeApplication.get_Image_Button_0();
				break;
			case 1:
				bP.setColor(Color.GREEN);
				bR = SwifteeApplication.get_Image_Button_1();
				break;
			case 2:
				bP.setColor(Color.BLUE);
				bR = SwifteeApplication.get_Image_Button_2();
				break;				    							    			  
			case 3:
				bP.setColor(Color.YELLOW);
				bR = SwifteeApplication.get_Image_Button_3();
				break;
			case 4:
				bP.setColor(Color.RED);
				bR = SwifteeApplication.get_Image_Button_4();
				break;
			case 5:
				bP.setColor(Color.YELLOW);
				bR = SwifteeApplication.get_Image_Button_5();
				break;			    					
			case 6:
				bP.setColor(Color.GRAY);
				bR = SwifteeApplication.get_Image_Button_6();
				break;
			case 7:
				bP.setColor(Color.DKGRAY);
				bR = SwifteeApplication.get_Image_Button_7();
				break;
			case 8:
				bP.setColor(Color.LTGRAY);
				bR = SwifteeApplication.get_Image_Button_8();
				break;			
			
		}
		canvas.drawRect(bR, bP);
	}	
	
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}
	
	public void setHitPKIndex(int hitPKIndex) {
		this.hitPKIndex = hitPKIndex;
	}
	
	private void destroy(){
		destroyBitmap();
		destroySpinnerBitmap();
		destroyImagesBitmap();
		destroyVideosBitmap();
	}
	
	public void destroyBitmap() {
        if (bitmapFrame != null) {
        	bitmapFrame.recycle();           
        	bitmapFrame=null;      
        }
        System.gc();
	}
	
	public void destroySpinnerBitmap() {    
        if (bitmapSpinner != null) {
        	bitmapSpinner.recycle();           
        	bitmapSpinner=null;
        }
        System.gc();
	}
	
	public void destroyImagesBitmap() {
        if (bitmapImages != null) {
        	bitmapImages.recycle();           
        	bitmapImages=null;  
        }
        System.gc();
    }
	
	public void destroyVideosBitmap() {
        if (bitmapVideos != null) {
        	bitmapVideos.recycle();           
        	bitmapVideos = null;        	
        }   
        System.gc();
	}    
  
	
}
 



