 package com.roamtouch.visuals;

import java.util.Vector;

import com.roamtouch.webhook.WebHitTestResult;

import android.webkit.WebView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.utils.PaintTextUtils;
import com.roamtouch.utils.ScreenLocation;
import com.roamtouch.visuals.RingView;

public class SuggestionController extends FrameLayout {
	
	private SuggestionView sV = null;	
	
	private WebView cW;
	private BrowserActivity mP;
	private FloatingCursor fC;
	
	Rect re;    		
	int[] c;
	int r;
	int g;
	int b;
	private String t;
	int w;
	int l;
	int TYPE;

	int scrollX;
	int scrollY;
	
	private Spinner iS; 
	
	int edge = SwifteeApplication.getEdge();
	
	private int identifier;
	private int hitPKIndex;
	
	private String[] buttons;
	
	private int storeType;
	
	private int accumulator;
	private int[] buttonsWidth;
	private int[] buttonXPos;
	
	private int cType;
	private int lastKnownWebHitType;
	
	boolean isSmallPhone;

	public SuggestionController(Context context, AttributeSet attrs) {
		super(context.getApplicationContext(), attrs);
		init(context.getApplicationContext());	
	}
	
	private void init(Context context) {		
		sV = new SuggestionView(context.getApplicationContext());		
		addView(sV);	
	}
	
	int x; 
	int y;
	int W;
	int H;
	
	public void setParent(BrowserActivity p, FloatingCursor f, WebView w) {
		mP = p;
		cW = w;	
		fC = f;			
		sV.setGrandParent(mP);
		sV.setFloatingCursror(f);
	}
	
	/**
	 * Object[]
	 * Rect 
	 * Color
	 * Text 
	 * Extra Tab Height 
	 * Optional Array to set the suggestion listy list.
	 */
	public void setDrawStyle(int type, Object[] param, int id){	
		
		int hitId = (Integer) param[4];
		
		Log.v("vamos","this.identifier : " + this.identifier + " id: " + id + " this.hitPKIndex: " + this.hitPKIndex + " hitId: " + hitId );
		
		if (	( this.identifier != id && this.hitPKIndex == hitId )
			|| 	( this.identifier == id && this.hitPKIndex != hitId )
			|| 	( this.identifier != id && this.hitPKIndex != hitId ) 
			){	
			
			renderAssets(type, param, true);			
			
		}  else if ( this.identifier == id ) {			
			renderAssets(type, param, false);
		}	
		
		this.identifier = id;
		this.hitPKIndex = hitId;
	} 
	
	String[][] array;
	Object[][] data;
	Object[][] buttonsData;
	Object[][] ImagesData;
	int[] color;
	
	int biggerTextSize;
	int masterRectWidth;
	Rect biggerRect;
	
	boolean enlarge;
	int vertical;
	boolean expanded;
	boolean hasButtons;
	boolean hasImages;
	boolean hasListData;
	
	private void renderAssets(int type, Object[] param, boolean redrawRect){
		
		sV.setDrawType(type);	
		sV.setIdentifier(identifier);		
		
		cType = SwifteeApplication.getCType(); 	
		lastKnownWebHitType = SwifteeApplication.getLastKnownHitType();
		edge = SwifteeApplication.getEdge();		
		re = SwifteeApplication.getMasterRect();
		expanded = SwifteeApplication.getExpanded();
		
		int[] screenSize = mP.getDeviceWidthHeight();
    	int screenWidth = screenSize[0];
    	if( screenWidth < 500 ){
    		isSmallPhone = true;
    	} else {
    		isSmallPhone = false;
    	}
		
		/** No need to set rect again 
		 * if identifier is the same**/
		if (redrawRect) {
			
			storeType = type;
			color = (int[]) param[0];
			array = (String[][]) param[1];
			buttons = (String[]) param[2];
			hasButtons = (Boolean) param[3];	
			sV.setHasButtons(hasButtons);	
			int hitIndex = (Integer) param[4];
			sV.setHitPKIndex(hitIndex);
			
		}
		
	 	if (re!=null){ 	
	 		
	 		if (!redrawRect && param!=null){
	 			color = (int[]) param[0];
	 		}		
        	
        	sV.setRect(re);      	    	
        	sV.setEdge(edge);   	
        	
			sV.setSuggestionColors(color);
			SwifteeApplication.setSuggestionColor(color);
					
			if (mP.getHasWiki()) {		    		
	    		sV.setHasWiki(true);			    		
	    	} else {
	    		sV.setHasWiki(false);	
	    	}

			if (mP.hasVideoBitmaps()) {		    		
	    		sV.setHasVideos(true);			    		
	    	} else {
	    		sV.setHasVideos(false);		
	    	}
			
			if (array!=null){	    		
	    		if (mP.hasImageBitmaps()) {	    			
		    		sV.setHasImages(true);
	    		} else {
	    			sV.setHasImages(false);
	    		}		    		
			} 
			
        	if (array!=null){        		
				if (array.length>0){	
					hasListData = true;
					int textSize = SwifteeApplication.getSuggestionRowTextSize();
					Paint textPaint = sV.paintText(textSize, color);			
					biggerTextSize = PaintTextUtils.CalculateArrayMaxTexts(textPaint, array);				
					SwifteeApplication.setArrayTextBiggerRow(biggerTextSize);   
					sV.setArraySuggestion(array);				
				} else {
					hasListData = false;
					sV.cleanArray();
				}
			} else {
				hasListData = false;
				sV.cleanArray();				
			}       	
        	     	
        	if (hasButtons){ 	
        		
        		SwifteeApplication.setSuggestionButtonsArray(buttons);
        		
        		int buttonlight;
        		
        		if (!expanded){
        			
        			buttonlight = edge-(edge/4);
        			accumulator = re.right - (edge*2) - buttonlight;
        			
        		} else {
        			
        			if (!hasListData){
        			
        				buttonlight = edge;
        				accumulator = re.right - (edge*2) - buttonlight;
        				
        			} else {				
        				
        				buttonlight = edge-(edge/3);
        				accumulator = re.right - edge - buttonlight;
        				
        				//buttonlight = (edge/4);
        				//accumulator = re.right - (edge/2) - buttonlight;        				
        				
        			}
        		}
        		int lightBtween;
        		if (isSmallPhone){
        			lightBtween = (edge/3);
        		} else {
        			lightBtween = (edge/2);
        		}
	        	  	
	        	buttonsWidth = new int[buttons.length];
	        	buttonXPos = new int[buttons.length];   	
				
				sV.setButtons(buttons);		
				buttonsData = new Object[buttons.length][7];
				
	        	for (int i=0; i<buttons.length; i++){   
	        		
	        		Paint p = new Paint();        		
	        		p.setTypeface(Typeface.DEFAULT_BOLD);
	        		if (isSmallPhone){
	        			p.setTextSize(12);
	        		} else {
	        			p.setTextSize(15);
	        		}
	        		if (i==0){ accumulator -= 10; };
	        		int textWidth = (int) p.measureText(buttons[i]);	        		
		      	    int buttonWidth = textWidth + (buttonlight*2);
		      	    buttonsWidth[i] = buttonWidth; 
		      	    int bPos = accumulator - buttonWidth;
		      	    buttonXPos[i] = bPos;
		      	    accumulator -= buttonWidth;
		      	    accumulator -= lightBtween;	      	    
		      	    if (i==3){ accumulator -= 10;}   
		      	    if (i==buttons.length-1){ accumulator -= 20;}
		      	    
		      	}	        		
	        	accumulator -= lightBtween;    	
        	} else {
	 			sV.setData(null);
	 		}	
        	
        	if ( cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE 
        			|| cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER 
      	    		|| cType == SwifteeApplication.TYPE_PADKITE_SERVER 
      	    		|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && lastKnownWebHitType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE)
      	    	){
	        	
        		masterRectWidth = re.right - re.left; 
        		
        		if (( biggerTextSize + edge ) > masterRectWidth ){     			
        			
        			enlarge = true;        			
					
        			/*Vector cuagrant34 = new Vector();
					
					int xCord = 0;
					int yCord = 0;				
					
					if (fC.isFromFC()){
						
						xCord = fC.getFcX();
						yCord = fC.getFcY();
						
					} else {
						
						if (fC.isAnchorTab()){
							
							xCord = fC.getAnX();
							yCord = fC.getAnY();
							
						} else {
							
							xCord = fC.getStX();
							yCord = fC.getStY();
							
						}
					}
					
					cuagrant34 = mP.getCuadrant34(xCord, yCord);			      	 	
		      	 	vertical = (Integer) cuagrant34.get(0);
		      	 	
					int horizontal = (Integer) cuagrant34.get(1);		
					Rect xRect =  (Rect) cuagrant34.get(2);*/
        			
        		
        			biggerRect = new Rect();	
					Rect anchorRect = SwifteeApplication.getAnchorRect();
					
        			int vertical = ScreenLocation.getVerticalLocation();
        			SwifteeApplication.setVerticalPosition(vertical);
        			
        			int horizontal = ScreenLocation.getHorizontalLocation();				
					
					switch (vertical){
						
						case SwifteeApplication.VERTICAL_LEFT_COLUMN:
							biggerRect.left = re.left - (edge/2);						
							biggerRect.right = re.left + biggerTextSize + (2*edge);
							SwifteeApplication.setTextIsBigger(SwifteeApplication.TEXT_IS_BIGGER_LEFT);						
							sV.setFrameHorizontalOrientation(SwifteeApplication.TAB_SUGGESTION_FRAME_BIGGER_RIGHT);
							
							if (cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER) {			
								re = anchorRect;						
							}
							
							break;
						
						case SwifteeApplication.VERTICAL_CENTER_COLUMN:
							
							int biggerRectWidth = biggerTextSize + (2*edge);
							int reHalfWidth;
							int bHalfWidth;
							
							if (cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER) {								

								reHalfWidth = anchorRect.left + ((anchorRect.right - anchorRect.left)/2);
								
								if ( biggerRectWidth < (anchorRect.width()+(2*edge)) ){														
									biggerRectWidth = anchorRect.width() + (3*edge);									
								}								
								re = anchorRect;
								
							} else {					
								
								reHalfWidth = re.left + ((re.right - re.left)/2);	
								
							}		
							
							bHalfWidth = (biggerRectWidth/2);					
							biggerRect.left = reHalfWidth -  bHalfWidth;
							biggerRect.right = biggerRect.left + biggerRectWidth;							
														
							SwifteeApplication.setTextIsBigger(SwifteeApplication.TEXT_IS_BIGGER_CENTER);						
							sV.setFrameHorizontalOrientation(SwifteeApplication.TAB_SUGGESTION_FRAME_BIGGER_CENTER);			
														
							re.left = SwifteeApplication.getMasterRect().left + 2;
							
							break;
							
						case SwifteeApplication.VERTICAL_RIGHT_COLUMN:
							biggerRect.right = re.right + (edge/2);
							biggerRect.left = re.right - (biggerTextSize + (2*edge));					
							SwifteeApplication.setTextIsBigger(SwifteeApplication.TEXT_IS_BIGGER_RIGHT);						
							sV.setFrameHorizontalOrientation(SwifteeApplication.TAB_SUGGESTION_FRAME_BIGGER_LEFT);
							if (cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER) {								
								re = anchorRect;
							}
							break;					
					}		
					
				} else {
					
					SwifteeApplication.setTextIsBigger(SwifteeApplication.TEXT_IS_NOT_BIGGER);				
					sV.setFrameHorizontalOrientation(SwifteeApplication.TAB_SUGGESTION_FRAME);
					
				}
        	}
        	
        	int[] cords = new int[5];
        	x = re.left;  	
      	    y = re.top - (re.bottom - re.top);
      	    W = re.width();
      	    H = re.height();     	    
	         	    
      	    cords[0] = x;      	    	
      	    cords[2] = W;
	    	cords[1] = y;      	    	
      	    
      	    /*if ( (cType == SwifteeApplication.TYPE_PADKITE_INPUT)
      	    	|| (cType == SwifteeApplication.TYPE_PADKITE_PANEL)
      	    	|| ((cType == SwifteeApplication.TYPE_PADKITE_TAB) 
				&& (lastKnownWebHitType == WebHitTestResult.EDIT_TEXT_TYPE))
				){
      	    	
      	    	cords[0] = x;      	    	cords[2] = W;
      	    	cords[1] = y;      	    	
      	    	
      	    } else*/ 
      	    	
      	    if ( cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE
      	    		|| cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER 
      	    		|| cType == SwifteeApplication.TYPE_PADKITE_SERVER 
      	    		|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && lastKnownWebHitType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE)){      	    	
      	    	
      	    	x -= (edge/3) + 1;      	    
      	    	cords[0] = x;      	    	
      	    	y -= (edge/3);
      	    	cords[1] = y;      	    	
      	    	W += (edge/2) + 2;
      	    	cords[2] =  W;      	     	    	    	
      	    	
      	    }      	    
      	    
			int variableHeight;
			int top = 0;
			
			if(sV.isListArmed()){
				
				if ( (cType == SwifteeApplication.TYPE_PADKITE_INPUT)
		      	    	|| (cType == SwifteeApplication.TYPE_PADKITE_PANEL)
		      	    	|| ((cType == SwifteeApplication.TYPE_PADKITE_TAB) 
						&& (lastKnownWebHitType == WebHitTestResult.TYPE_EDIT_TEXT_TYPE)) ) {
					
					variableHeight = H + (
	    	   				SwifteeApplication.getSuggestionRowHeight()
    	   					*SwifteeApplication.getRowAmount());   
					
				} else {
					
					variableHeight = (edge+(edge/6)) + ( 
	    	   				SwifteeApplication.getSuggestionRowHeight()
    	   					*SwifteeApplication.getRowAmount());
					
				}				   
	    	   		
			} else {
				
				variableHeight = H; //(edge+(edge/6)); //H;
 				
  	  		}
			
   			/*if ( ( cType == SwifteeApplication.TYPE_PADKITE_INPUT )
   				|| ((cType == SwifteeApplication.TYPE_PADKITE_TAB) 
   				&& (lastKnownWebHitType == WebHitTestResult.EDIT_TEXT_TYPE)) ){
   				
   				top = y + H*2 + 4;
   				
   				if (array!=null) {  					
   					
   					variableHeight += edge;
   					
   				} else {
   					
   					variableHeight -= (edge/3);
   					
	   			}	    	   		
	   		} */
   			
   			top = y + H*2 + 4;
				
			if (array!=null) {  					
				
				variableHeight += edge;
				
			} else {
				
				variableHeight -= (edge/3);
				
			}	
   			
   			//else 
   			
   			if ( cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE
   					|| cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER 
   					|| cType == SwifteeApplication.TYPE_PADKITE_SERVER ){
   				
	   			top = y;
	   			variableHeight -= edge;		
	   			W += (edge/2);
	   		}
	   		
	   		else if ( cType == SwifteeApplication.TYPE_PADKITE_PANEL ){
	   			
	   			top = y;
	   			if (!hasButtons){
	   				variableHeight -= (edge*2);
	   			} else {
	   				variableHeight -= (edge/2);
	   			}	    	   					
	   			W += (edge/2);
	   			
	   		}		    
			
			cords[3] = H; 
      		cords[4] = accumulator;
      		
      	    sV.setCords(cords);
      	    
			sV.setVariableHeight(variableHeight);
			
			if (enlarge){			
				biggerRect.top = re.top;
				biggerRect.bottom = top + variableHeight;
				W = biggerRect.left + biggerRect.right;
				SwifteeApplication.setBiggerRectResize(biggerRect);
				enlarge = false;
			}
			int finalHeight = top + variableHeight + edge;
			
			if (finalHeight==0 || W==0){
				Log.v("fuck", "fuck");
			}		
			
			String frameBitmapKey = "s-" + SwifteeApplication.getIdentifier() + "-" + 
        			SwifteeApplication.getActiveTabIndex()+ "-" + expanded + "-" + SwifteeApplication.getTabsAmountOf();		
 						
			int width = (W-x)+(edge*12);
			int height = (finalHeight-y)+(edge*8);
			
			sV.setBitmap(x, y, width, height, frameBitmapKey);
			
			if (hasButtons){
				for (int j=0; j<buttons.length; j++){     		
	        		buttonsData[j][0] = buttonXPos[j];
	        		buttonsData[j][1] = buttonsWidth[j];	        		
	        		if (expanded){
	        			buttonsData[j][3] = (edge*2); //button height        			
	        		} else {
	        			buttonsData[j][3] = edge + (edge/4);	        				
	        		}
	        		buttonsData[j][2] = y;
	        		buttonsData[j][4] = buttons[j];	        		
	        		buttonsData[j][5] = color; //SwifteeApplication.GRAY;	 		  	        			
	        	}	        		        	
	        	sV.setData(buttonsData);
	        	data = buttonsData;	  
			} else {
				sV.setData(null);
				sV.setButtonText(null);  	
			}	
			
			
	    	if (array!=null){
	    		
	    		if (mP.hasImageBitmaps()) {	    			
		    		sV.setHasImages(true);		    		
		    		int length = mP.getImageArraySize();	    		
					Object[][] imagesParams = new Object[length][4]; //soze and position.							   				
	   				int imgSpace = W/5;
	   				int startleftPosition = x + (edge*2);				
					for (int t=0; t<length/2; t++){				
						imagesParams [t][0] = startleftPosition;					
						imagesParams [t][1] = re.bottom + edge;
						imagesParams [t][2] = imgSpace;
						startleftPosition += imgSpace;	 		  	        			
		        	}
					startleftPosition = x + (edge*2);		
					for (int t=length/2; t<length; t++){				
						imagesParams [t][0] = startleftPosition;					
						imagesParams [t][1] = re.bottom + edge;
						imagesParams [t][2] = imgSpace;
						startleftPosition += imgSpace;	 		  	        			
		        	}	
					
		        	sV.setImagesLocationData(imagesParams);	   		
		        	
	    		} else {
	    			
	    			sV.setHasImages(false);		
	    		}		    	
	    	}					
	 	}	
    }		 	
		
	
	
	public void refresh(Rect r, Object obj){
		re = r;		
		if (obj instanceof int[]){			
			Object[] param = new Object[1];
			param[0] = (int[]) obj;
			renderAssets(storeType, param, false);
		} else {
			renderAssets(storeType, null, false);
		}
		
	}
	
	/**Draw Nothing**/
	public void drawNothing(){		
		if (data!=null){
			data = null;
		}
		mP.setSuggestionListActivated(false);	   
    	mP.setSuggestionActivated(false);    	
		sV.setDrawType(SwifteeApplication.DRAW_NOTHING);	
	}	
	
	//Paint Row Over
	public void setRowOver(int id){		
		
		sV.setRowOver(id);	
		invalidate();
	}
	
	//Paint button Over
	public void setButtonOver(int id){		
		sV.setButtonOver(id);
		invalidate();
	}
	
	public void cleanOvers(){
		sV.cleanOvers();
	}
	

	public void cleanButtonsOver(){		
		sV.cleanButtonsOver();
	}
	
	public void cleanRowsOver(){
		sV.cleanRowsOver();
	}
	
	public void setSuggestionData(String[][] array){		
		sV.setRect(re);
		sV.setArraySuggestion(array);			
	}
	
	public void invalidateTips(){
		sV.postInvalidate();
	}
	
	public void cleanArray(){
		sV.cleanArray();
	}
	
	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}
	
}
