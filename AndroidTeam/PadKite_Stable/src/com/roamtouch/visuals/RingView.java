package com.roamtouch.visuals;

import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import com.roamtouch.webhook.WebHitTestResult;

import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.R;
import com.roamtouch.utils.ColorUtils;
import com.roamtouch.utils.ImageCache;
import com.roamtouch.visuals.Tabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.roamtouch.swiftee.SwifteeApplication;

public class RingView extends View {

	private String text;

	int[] cords;

	public int draw;

	public int input;

	/*
	 * int x; int y; int W; int H;
	 */

	int r;
	int g;
	int b;

	int ringWidth = 1;

	int TYPE;

	int rotatedTab;

	int identifier = 0;

	private Rect rect;

	private Tabs t;

	int cX;
	int cY;

	private int extraHeight;

	private BrowserActivity mP;
	private FloatingCursor fC;

	private Rect hitRect;

	private String[] titles;
	private int[][] tabColors;
	private int[] ringColors;
	private Vector dataVector = new Vector();
	int topId;

	private Rect sugRect;
	private Rect initRect;	
	
	private RingController rC;

	ImageView im;

	private int edge;

	private int cType;

	private int textYPos;

	private int originalId;

	private Canvas canvasSpinner;
	private Bitmap bitmapSpinner;

	private ImageView spinnerImage;

	private Matrix matrix;
	private float mDegree;
	
	private String bitmaptabKey; 

	private Hashtable<Integer, int[]> colorSpinnerDots = new Hashtable();

	private Context context;
	
	private ImageCache imgCache = new ImageCache();

	private Object[][] data = new Object[SwifteeApplication.getTabsAmountOf()][11];

	public RingView(Context cont, RingController rc) {
		super(cont);
		sugRect = new Rect();
		im = new ImageView(cont);
		t = new Tabs();
		context = cont;
		rC = rc;
	}

	int _width;
	int _height;
		
  	private boolean isBitmapCache;
  	
  	private String bitmapTabKey;  
  	private boolean switchTabBitmap;
  	
  	private Canvas canvasTab;
  	private Bitmap bitmapTab;
	private int tabId = -1; 	
	private boolean bitmapTabCache;

	public void setBitmap(int x, int y, int width, int height) {
		
		_width = x + width;
		_height = y + height;	
		colorSpinnerDots = ColorUtils.setSpinnerColors(colorSpinnerDots); 		 		
  		
		this.bitmapTabKey = getBitmapKey();
  		switchTabBitmap = true;
  		
	}
	
	private String getBitmapKey(){		
		String bitmapKey = "r-" + SwifteeApplication.getIdentifier() + "-" + 
    			SwifteeApplication.getActiveTabIndex() + "-" + SwifteeApplication.getExpanded();
		return bitmapKey;		
	}

	public void destroy() {
		if (bitmapTab != null) {
			bitmapTab.recycle();
			bitmapTab = null;
			if (bitmapSpinner != null) {
				bitmapSpinner.recycle();
				bitmapSpinner = null;
			}
			System.gc();
		}
	}

	@Override
	protected void onDraw(Canvas c) {

		super.onDraw(c);

		switch (draw) {

		case SwifteeApplication.DRAW_TABS:
			draw_tabs(c);
			break;

		case SwifteeApplication.DRAW_RING_AND_TAB:
			draw_ring(c);
			draw_tabs(c);
			break;

		case SwifteeApplication.DRAW_NOTHING:
			mP.setTabsActivated(false);
			break;
		}
	};

	int xRing;
	int yRing;
	int wRing;
	int hRing;

	private int hitPKIndex;

	public void setXYWH(int _x, int _y, int _w, int _h) {
		xRing = _x;
		yRing = _y;
		wRing = _w;
		hRing = _h;
	}

	
	private void newTabBitmap(){
		bitmapTab = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
  		canvasTab = new Canvas(bitmapTab);
  		switchTabBitmap=true;
   	   	bitmapTabCache=false;
   	   	tabs();
   	   	//rC.persistRingColor();
	}
	
	private void draw_tabs(Canvas c) {
					
		 /**MEMORY MONITOR**/
		
 	   /*if(SwifteeApplication.getMemoryStatusEnabled()){
 		   int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
 		   String usedMegsString = String.format(" - Memory Used: %d MB", usedMegs);
 		   mP.getWindow().setTitle(usedMegsString);
 	   	}*/
 	   
 	   BrowserActivity.setTabsActivated(true);
 	   
 	   //rC.loopRingColor();
 	   
 		/**TAB**/
 	   Log.v("bitmapTab", ""+bitmapTabKey);
	   	if (imgCache.hasImage(bitmapTabKey)){
	   		
	   		if (switchTabBitmap)
	   			bitmapTab = imgCache.getFromCache(bitmapTabKey);
	   	
	   		if(bitmapTab==null){
	   			newTabBitmap();
	   		} else {
	   			bitmapTabCache=true;
	   			switchTabBitmap=false;
	   		}
	   		
	   	} else {	    	   		
	   		newTabBitmap();
	   	}		

	   	/**SPINNER**/
	   	spinner();
	   	
	   	//Draw Tabs
     	if (bitmapTab!=null){
	   		
    	   	int bitmapWidth = bitmapTab.getWidth(); 
	   		int bitmapHeight = bitmapTab.getHeight();
    	   	
			c.drawBitmap(bitmapTab, new Rect(0, 0, bitmapWidth, bitmapHeight),
					new Rect(0, 0, bitmapWidth, bitmapHeight), null);
    		
    		if(!bitmapTabCache){
    			imgCache.saveToCache(bitmapTabKey, bitmapTab);
    		}		
    		
	   	} else {	   		
	   		
	   		imgCache.deleteBitmap(bitmapTabKey);	
	   		bitmapTab = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);    	  	  		
  	  		canvasTab = new Canvas(bitmapTab);	
  	  		bitmapTabCache=false;
  	  		tabs();  	  		
	   	}	   	
		

		if (cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE) {
			c.drawBitmap(bitmapSpinner, matrix, null);
		}

		invalidate();

		 /*if (mP.isTabsActivated()){
		  
		 switch (originalId){ case SwifteeApplication.TABINDEX_CERO:
		 actionRect = SwifteeApplication.getTab_0_Rect();
		 checkPaint.setColor(Color.MAGENTA); break; case
		 SwifteeApplication.TABINDEX_FIRST: actionRect =
		 SwifteeApplication.getTab_1_Rect();
		 checkPaint.setColor(Color.YELLOW); break; case
		 SwifteeApplication.TABINDEX_SECOND: actionRect =
		 SwifteeApplication.getTab_2_Rect();
		 checkPaint.setColor(Color.BLACK); break; case
		 SwifteeApplication.TABINDEX_THIRD: actionRect =
		 SwifteeApplication.getTab_3_Rect();
		 checkPaint.setColor(Color.RED); break; case
		 SwifteeApplication.TABINDEX_FOURTH: actionRect =
		 SwifteeApplication.getTab_4_Rect();
		 checkPaint.setColor(Color.LTGRAY); break; }
		 c.drawRect(actionRect, checkPaint); 
		
		 }*/	
	}
	
	private int arrayAmountPerType;
	private Paint pStroke;
	private Paint pTab;
	private int[] color;
	
	private void tabs(){
		
		int length = SwifteeApplication.getTabsAmountOf();

		edge = SwifteeApplication.getEdge();

		for (int i = 0; i < length; i++) {

			int xTabPos = (Integer) data[i][0];
			int yTabPos = (Integer) data[i][1];
			int xWidth = (Integer) data[i][2];
			int xHeight = (Integer) data[i][3];
			String text = (String) data[i][4];
			originalId = (Integer) data[i][9];

			int[] col = (int[]) data[i][5];
			r = col[0];
			g = col[1];
			b = col[2];

			t = new Tabs();
			t.setParent(this);
			t.setTopId(topId);

			cType = SwifteeApplication.getCType();
			arrayAmountPerType = SwifteeApplication
					.getAmountOfObjectsPerType(cType);

			int amount = (Integer) data[i][11];

			Object[] params = new Object[arrayAmountPerType];

			/*if (cType == SwifteeApplication.TYPE_PADKITE_INPUT
					|| cType == SwifteeApplication.TYPE_PADKITE_PANEL
					|| cType == SwifteeApplication.TYPE_PADKITE_TAB
					|| cType == SwifteeApplication.TYPE_PADKITE_ROW
					|| cType == SwifteeApplication.TYPE_PADKITE_TIP_BUTTON
					|| cType == SwifteeApplication.TYPE_PADKITE_BUTTON) {*/

			params[0] = SwifteeApplication.TAB_ROUNDED_ANGLE_UP;
			params[1] = amount;

			textYPos = yTabPos + 25;

			//int tabHeight = SwifteeApplication.getTabHeight();
			
			if (cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE
					|| cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER
					|| cType == SwifteeApplication.TYPE_PADKITE_SERVER) {

				params[0] = SwifteeApplication.TAB_LINK;
				params[1] = SwifteeApplication.LINK_TAB_ORIENT_UP;// why
				
				int textYAverage = (xHeight - 18) / 2;
				
				textYPos = yTabPos + xHeight - textYAverage - (edge / 6);
				
				xTabPos -= 1;
			}

			else if (cType == WebHitTestResult.TYPE_TEXT_TYPE) {

				params[0] = SwifteeApplication.TAB_TEXT;
				params[1] = SwifteeApplication.TEXT_TAB_ORIENT_UP;
				
				int textYAverage = (xHeight - 18) / 2;
				
				textYPos = yTabPos + xHeight - textYAverage - (edge / 6);
				
				xTabPos -= 1;
			}

			if (originalId == 0) {
				params[2] = SwifteeApplication.TAB_ORIENT_LEFT;
			} else if (originalId == amount - 1) {
				params[2] = SwifteeApplication.TAB_ORIENT_RIGHT;
			} else {
				params[2] = SwifteeApplication.TAB_ORIENT_CENTER;
			}

			// if(amount>3){
			params[3] = data[i][10]; // spacer
			// }

			int[] coords = { xTabPos, yTabPos, xWidth, xHeight, edge, originalId };
			int[] tempCol = { r, g, b };
			color = tempCol;

			Rect actionRect = new Rect();
			Paint checkPaint = new Paint();

			// Load data
			t.setTabs(coords, color, mP);
			pTab = new Paint();
			pTab = t.paintTab(false);

			Path[] all = new Path[2];
			all = t.drawShape(params);
			
			//SHAPE
			canvasTab.drawPath(all[0], pTab);
			
			pStroke = new Paint();
			pStroke = t.paintTab(true);

			Paint pTe = new Paint();
			pTe = t.paintText(18);
			int textWidth = (int) pTe.measureText(text);
			int tabCenter = xWidth / 2;
			int textCenter = textWidth / 2;
			
			//STROKE
			canvasTab.drawPath(all[1], pStroke);
			
			//ACA Align Titles			
			int xTextPos = 0;
			
			int finalTextXPos;		
			
			int header = (Integer) data[i][13];
			
			switch (header) {
				
				case 0:
					
					Rect cero_rect = SwifteeApplication.getTabCeroRect();					
					if (cero_rect!=null && (cero_rect.left>0 && cero_rect.right>0)) {						
						Object[] callText = alignTabText(cero_rect, textWidth, text);						
						xTextPos = (Integer) callText[0];
						text = (String) callText[1];				
						Log.v("heavy", "TABINDEX_CERO "+cero_rect + " " + text + " xTextPos:" + xTextPos);
					}					
					break;
					
				case 1:
					
					Rect first_rect = SwifteeApplication.getTabFirstRect();
					if (first_rect!=null && (first_rect.left>0 && first_rect.right>0)){
						Object[] callText = alignTabText(first_rect, textWidth, text);						
						xTextPos = (Integer) callText[0];
						text = (String) callText[1];
						Log.v("heavy", "TABINDEX_FIRST "+first_rect + " " + text + " xTextPos:" + xTextPos);
					}						
					break;
					
				case 2:
					
					Rect second_rect = SwifteeApplication.getTabSecondRect();
					if (second_rect!=null && (second_rect.left>0 && second_rect.right>0)){
						Object[] callText = alignTabText(second_rect, textWidth, text);						
						xTextPos = (Integer) callText[0];
						text = (String) callText[1];				
						Log.v("heavy", "TABINDEX_SECOND "+second_rect + " " + text + " xTextPos:" + xTextPos);
					}						
					break;
					
				case 3:
					
					Rect third_rect = SwifteeApplication.getTabThirdRect();
					if (third_rect!=null && (third_rect.left>0 && third_rect.right>0)){
						Object[] callText = alignTabText(third_rect, textWidth, text);						
						xTextPos = (Integer) callText[0];
						text = (String) callText[1];						
						Log.v("heavy", "TABINDEX_THIRD "+third_rect + " " + text + " xTextPos:" + xTextPos);
					}						
					break;
					
				case 4:
					
					Rect fourth_rect = SwifteeApplication.getTabFourthRect();
					if (fourth_rect!=null && (fourth_rect.left>0 && fourth_rect.right>0)){						
						Object[] callText = alignTabText(fourth_rect, textWidth, text);						
						xTextPos = (Integer) callText[0];
						text = (String) callText[1];
						Log.v("heavy", "TABINDEX_FOURTH "+fourth_rect + " " + text + " xTextPos:" + xTextPos);
					}						
					break;				
					
			}
			
			canvasTab.drawText(text, xTextPos, textYPos, pTe);
		}
	}
	
	private void spinner(){
		
		/**SPINNER 
		 * BACKGROUND**/
		
		if (cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE
			|| cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER ) {

			Rect master = SwifteeApplication.getActiveRect();
			Rect anchorRect = SwifteeApplication.getAnchorRect();
			Rect rect = SwifteeApplication.getActiveRect();
			
			Rect spBottom = new Rect();
			
			int vertical = SwifteeApplication.getVerticalPosition();
			
			if ( vertical == SwifteeApplication.VERTICAL_RIGHT_COLUMN ){
				
				spBottom.left = anchorRect.left + (edge/3) -1;
				spBottom.top = master.top - (edge / 4);
				
				boolean expanded = SwifteeApplication.getExpanded();
				
				if (expanded) {				
							
					spBottom.right = (edge * SwifteeApplication.FINGER_EXPANDED) + (edge/3)  + 3;
					
				} else {
					
					spBottom.right = (edge * SwifteeApplication.PADKITE_EXPANDED) + (edge/3) + 3;
					
					if (cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE 
							&& SwifteeApplication.getTabsAmountOf()>2
							&& expanded) {
						
						//spBottom.right = 200;		
						spBottom.right = (edge * SwifteeApplication.PADKITE_EXPANDED) + (edge*6) + (edge/4) + 3;
						
					} else {						
						
						spBottom.right = (edge * SwifteeApplication.PADKITE_EXPANDED) + (edge/3) + 3;
					}
				}
				
			} else {
				
				spBottom.left = master.right - (edge / 4);
				spBottom.top = master.top - (edge / 4);
				
				if (SwifteeApplication.getExpanded()) {
					
					spBottom.right = (edge * SwifteeApplication.FINGER_EXPANDED) + (edge / 2);
					
				} else {
					
					spBottom.right = (edge * SwifteeApplication.PADKITE_EXPANDED) + (edge / 2);
					
				}				
			}	

			spBottom.bottom = master.bottom - master.top + (edge / 2);

			t = new Tabs();
			t.setParent(this);

			int[] coordsSp = { spBottom.left, spBottom.top, spBottom.right,
					spBottom.bottom, edge, originalId };
			
			SwifteeApplication.setSpinnerBackgroundRect(spBottom);
			
			int[] colorSp = { r, g, b };
			t.setTabs(coordsSp, colorSp, mP);

			Object[] paramsSp = new Object[arrayAmountPerType];

			paramsSp[0] = SwifteeApplication.ANCHOR_SPINNER_BACKGROUND;				
			
			if ( vertical == SwifteeApplication.VERTICAL_RIGHT_COLUMN ){

				paramsSp[1] = SwifteeApplication.ANCHOR_SPINNER_BACKGROUND_ORIENTED_RIGHT;
			
			} else {
				
				paramsSp[1] = SwifteeApplication.ANCHOR_SPINNER_BACKGROUND_ORIENTED_LEFT;		
				
			}
			
			//ACA IF DIRECTION

			Path[] spinnerAll = t.drawShape(paramsSp);

			canvasTab.drawPath(spinnerAll[0], pTab);
			canvasTab.drawPath(spinnerAll[1], pStroke);
			
			int _x_;
			int _y_;
			
			boolean expanded = SwifteeApplication.getExpanded();
			
			if (expanded) {
			
				_x_ = spBottom.left + edge + (edge / 2) + (edge * SwifteeApplication.PADKITE_EXPANDED);
				_y_ = spBottom.top + edge - (edge / 8);
				
			} else {
				
				if ( vertical == SwifteeApplication.VERTICAL_RIGHT_COLUMN ){
					
					_x_ = spBottom.left + edge*2;
					
					if (rect.height()>(edge+(edge/2))){
						
						_y_ = spBottom.top + (rect.height()/2);
						
					} else {
						
						_y_ = spBottom.top + edge;						
					}
					
				} else {
				
					_x_ = spBottom.left + edge + (edge / 2);
					
					if (rect.height()>(edge+(edge/2))){
						
						_y_ = spBottom.top + (rect.height()/2);
						
					} else {
						
						_y_ = spBottom.top + edge - (edge / 8);
						
					}						
				}					
			}			

			int width = _x_ + (edge * 4);
			int height = _y_ + (edge * 2);

			if (bitmapSpinner != null) {
				bitmapSpinner.recycle();
				bitmapSpinner = null;
				System.gc();
			}

			bitmapSpinner = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
			canvasSpinner = new Canvas(bitmapSpinner);

			if (cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE) {
			
				int linkStatus = mP.siteIdentifierLoaded(identifier);
	
				for (int q = 0; q < 10; q++) {
	
					Paint pF = new Paint();
					pF.setStyle(Paint.Style.FILL);
					pF.setAntiAlias(true);
					double t = 2 * Math.PI * q / 10;
					int x = (int) Math.round(_x_ + 10 * Math.cos(t));
					int y = (int) Math.round(_y_ + 10 * Math.sin(t));
					int[] colB = null;
	
					if (linkStatus == SwifteeApplication.LINK_DATA_PARSED) {						
						int[] b;
						b = colorSpinnerDots.get(q);
						colB = b;
					} else if (linkStatus == SwifteeApplication.LINK_DATA_CALLED
							|| linkStatus == SwifteeApplication.LINK_DATA_NOT_CALLED
							|| linkStatus == SwifteeApplication.LINK_DATA_LOADED) {
	
						int[] b;
						b = ColorUtils.L2_Color(color);   
						colB = b;
					}
					int rB = colB[0];
					int gB = colB[1];
					int bB = colB[2];
					pF.setColor(Color.rgb(rB, gB, bB));
					canvasSpinner.drawCircle(x, y, (float) 3, pF);
				}
	
				int minwidth = bitmapSpinner.getWidth();
				int minheight = bitmapSpinner.getHeight();
	
				int centrex = minwidth / 2;
				int centrey = minheight / 2;
	
				matrix = new Matrix();
				matrix.setRotate(mDegree, _x_, _y_);
	
				// if (SwifteeApplication.getAchorSpinnerOn()){
	
				if ((linkStatus == SwifteeApplication.LINK_DATA_CALLED)
						|| (linkStatus == SwifteeApplication.LINK_DATA_NOT_CALLED)) {
					mDegree += SwifteeApplication.getSpinnerSpeed();
				}
			}
		}
		
	}
	
	private Object[] alignTabText(Rect re, int textWidth, String text){
		
		Object[] textObj = new Object[2];
		
		int width = re.right - re.left;
		
		if (width > textWidth){		
			
			int xTextPos = re.left + ((width/2) - (textWidth/2));		
			
			textObj[0] = xTextPos;
			textObj[1] = text;
			
		} else if (re.width() <= textWidth) {			
					
			int ttlLength = text.length();       	    				
				
			int newLength =  ((width * ttlLength) / textWidth);
			text = text.substring(0, newLength-1);
			
			Paint p = new Paint();
			int newTextWidth = (int) p.measureText(text);
			
			int xTextPos = re.left + ((width/2) - (newTextWidth/2));		
						
			textObj[0] = xTextPos;
			textObj[1] = text;		
			
		}
		
		return textObj;
		
	}

	private void draw_ring(Canvas c) {
		Paint pRing = new Paint();
		pRing.setStyle(Style.STROKE);
		pRing.setStrokeWidth(2);
		// pRing.setColor(Color.rgb(r, g, b));
		pRing.setColor(Color.BLACK);
		Rect ring = new Rect();
		ring.left = xRing;
		ring.top = yRing;
		ring.right = xRing + wRing;
		ring.bottom = yRing + hRing;
		canvasTab.drawRect(ring, pRing);
	}

	public void setRotatedTab(int rotatedTab, int centerX, int centerY) {
		this.rotatedTab = rotatedTab;
		cX = centerX;
		cY = centerY;
	}

	public int getExtraHeight() {
		return extraHeight;
	}

	public void setExtraHeight(int extraHeight) {
		this.extraHeight = extraHeight;
	}

	private Paint paintText() {
		Paint p = new Paint();
		p.setStyle(Paint.Style.FILL);
		p.setAntiAlias(false);
		p.setColor(Color.WHITE);
		p.setTypeface(Typeface.DEFAULT_BOLD);
		p.setTextSize(20);
		return p;
	}

	public void setDrawType(int draw) {
		this.draw = draw;
	}

	public void setIdentifier(int id) {
		this.identifier = id;
	}

	public int getIdentifier() {
		return identifier;
	}

	public Rect getRect() {
		return rect;
	}

	public void setRect(Rect rect) {
		this.rect = rect;
	}

	public void setInitRect(Rect rect) {
		this.initRect = rect;
	}

	public void setGrandParent(BrowserActivity bA, FloatingCursor fcu) {
		mP = bA;
		fC = fcu;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String[] getTitles() {
		return titles;
	}

	public void setTitles(String[] titles) {
		this.titles = titles;
	}

	public int[][] getTabColors() {
		return tabColors;
	}

	public int[] getRingColors() {
		return ringColors;
	}

	public void setRingColors(int[] ringColors) {
		this.ringColors = ringColors;
	}

	public void setTabColors(int[][] colors) {
		this.tabColors = colors;
	}

	public int getTopId() {
		return topId;
	}

	public void setTopId(int topId) {
		this.bitmapTabKey = getBitmapKey();
		switchTabBitmap = true;
		this.topId = topId;
	}

	public Object[][] getData() {
		return data;
	}

	public void setData(Object[][] data) {
		this.data = data;
	}

	public void setEdge(int edge) {
		this.edge = edge;
	}

	public void setHitPKIndex(int hitPKIndex) {
		// TODO Auto-generated method stub
		this.hitPKIndex = hitPKIndex;
	}

}
