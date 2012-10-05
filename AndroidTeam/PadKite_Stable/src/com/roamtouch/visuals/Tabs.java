package com.roamtouch.visuals;

import java.util.Vector;

import roamtouch.webkit.WebHitTestResult;

import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.utils.ColorUtils;
import com.roamtouch.visuals.SuggestionView;
import com.roamtouch.visuals.RingView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class Tabs extends Path{

	static int X;
	static int Y;
	static int width;
	static int height;
	static int edge;
	private int topId;
	static int extraHeight;
	static int corners;
	static int rotated;
	static int r;
	static int g;
	static int b;

	private Rect sugRect;

	
	private View vParent;

	private BrowserActivity mP;
	
	private int accumulator;

	private WebHitTestResult hitTestResult;

	private int module;
	
	private int spacer;
	
	private int originalId;

	Object[][] data;
	
	public Tabs() {
	}
	
	public void setTabs(final int[] coords, final int[] color, BrowserActivity bA) {

		X = coords[0];
		Y = coords[1];
		width = coords[2];
		height = coords[3];
		edge = coords[4];	
		module = edge/10;	
		
		if (vParent instanceof RingView){
			originalId = coords[5];
		}

		if (coords.length>5){			
			accumulator = coords[5];
		} else {
			accumulator = 0;
		}

		if (color!=null){
			r = color[0];
			g = color[1];
			b = color[2];
		}		
		sugRect = new Rect();
		mP = bA;	
	}
	
	public void setParent(View v){
		vParent = v;
	}

	public Path[] drawShape(Object[] params) { 	

		int type = (Integer) params[0];
		
		Path[] all = new Path[2];
		Path path = new Path();
		Path pathLine = new Path();

		switch (type) {

			case SwifteeApplication.TAB_ALL_ROUDED: 
				
				path.moveTo(X + edge, Y);
				path.lineTo(X + width - edge, Y);
				path.arcTo(GetRightUpper(edge), 270, 90);
				path.lineTo(X + width, Y + height - edge);
				path.arcTo(GetRightLower(edge), 0, 90);
				path.lineTo(X + edge, Y + height);
				path.arcTo(GetLeftLower(edge), 90, 90);
				path.lineTo(X, Y + edge);
				path.arcTo(new RectF(GetLeftUpper(edge)), 180, 90);				
				break;
	
			case SwifteeApplication.TAB_ROUNDED_ANGLE_UP:  

				int amount = (Integer) params[1];
				spacer = (Integer) params[3];				
				data = (Object[][]) ((RingView) vParent).getData();
				int orientationtab = (Integer) params[2];
				
				path = angleTabPath(false, orientationtab);				
				path.close();					
				pathLine = angleTabPath(true, orientationtab);
				
				setTabActions(data, originalId);		
				
				break;
				
			case SwifteeApplication.TAB_ROUNDED_ANGLE_DOWN: // Up Side Down
	
				/*path.moveTo(X, Y);
				path.lineTo(X + width, Y);
				path.lineTo(X + width, Y + (height - edge));
				path.arcTo(GetRightLower(edge), 0, 90);
				path.lineTo(X + edge, Y + height);
				path.arcTo(GetLeftLower(edge), 90, 90);
				path.lineTo(X, Y + height);*/
				
				break;
	
			case SwifteeApplication.SUGGESTION_FRAME_FOR_BUTTONS:			
				
				 boolean controlsStd = (Boolean) params[1];
				 boolean expandedStd = SwifteeApplication.getExpanded();
				 boolean hasDataStd = (Boolean) params[2];
				
				if (controlsStd){
					
					if (expandedStd && hasDataStd) {					
						
						path = input_frame_for_expanded();
						pathLine = input_frame_for_expanded();
						
					} else {
						
						path = suggestion_frame_for_futtons_fit_controls();
						pathLine = suggestion_frame_for_futtons_fit_controls();
						
					}		
										
				} else {
					
					path = suggestion_frame_for_buttons();
					pathLine = suggestion_frame_for_buttons();
				}
				
				break;
				
			case SwifteeApplication.TAB_SUGGESTION_FRAME:	
				
				int frameOrientUpDownm = (Integer) params[2];
				
				if (frameOrientUpDownm==SwifteeApplication.FRAME_ORIENT_DOWN){				
					
					path = flat_frame_down(path);
					pathLine = flat_frame_down(pathLine);
					
				} else if (frameOrientUpDownm==SwifteeApplication.FRAME_ORIENT_UP){
					
					path = flat_frame_up(path);
					pathLine = flat_frame_up(pathLine);					
				}
				
				break;
				
			case SwifteeApplication.TAB_SUGGESTION_FRAME_BIGGER_RIGHT:	
				
				int biggerFrameRightOrientUpDownm = (Integer) params[2];
				
				if (biggerFrameRightOrientUpDownm==SwifteeApplication.FRAME_ORIENT_DOWN){				
					
					path = bigger_frame_right(path, true);
					pathLine = bigger_frame_right(pathLine, false);
					
				} else if (biggerFrameRightOrientUpDownm==SwifteeApplication.FRAME_ORIENT_UP){
					
					//path = flat_frame_up(path);
					//pathLine = flat_frame_up(pathLine);					
				}
				
				break;	
					
			case SwifteeApplication.TAB_SUGGESTION_FRAME_BIGGER_CENTER:	
				
				int biggerFrameCenterOrientUpDownm = (Integer) params[2];
				
				if (biggerFrameCenterOrientUpDownm==SwifteeApplication.FRAME_ORIENT_DOWN){					
									
					path = bigger_frame_center(path, true);
					pathLine = bigger_frame_center(pathLine, false);
					
				} else if (biggerFrameCenterOrientUpDownm==SwifteeApplication.FRAME_ORIENT_UP){
					
					//path = flat_frame_up(path);
					//pathLine = flat_frame_up(pathLine);					
				}
				
				break;	
				
			case SwifteeApplication.TAB_SUGGESTION_FRAME_BIGGER_LEFT:	
				
				 boolean controlsRight = (Boolean) params[1];
				 boolean expandedRight = SwifteeApplication.getExpanded();
				 //boolean hasDataRight = (Boolean) params[2];
				 int biggerFrameLeftOrientUpDownm = (Integer) params[2];
				
				if (biggerFrameLeftOrientUpDownm==SwifteeApplication.FRAME_ORIENT_DOWN){
					
					
					if (controlsRight){
						
						path = bigger_frame_left(path, false, controlsRight, expandedRight);
						pathLine = bigger_frame_left(pathLine, true, controlsRight, expandedRight);
						
						/*if (expandedRight) { // && hasDataRight) {						
							path = bigger_frame_left(path, false, controlsRight, expandedRight);
							pathLine = bigger_frame_left(pathLine, true, controlsRight, expandedRight);
							
							//path = bigger_frame_left_frame_for_futtons_fit_controls(path, false, controlsRight);
							//pathLine = bigger_frame_left_frame_for_futtons_fit_controls(pathLine, true, controlsRight);							
						} else {						
							path = bigger_frame_left(path, false, controlsRight, expandedRight);
							pathLine = bigger_frame_left(pathLine, true, controlsRight, expandedRight);							
						}*/		
											
					} else {

						path = bigger_frame_left(path, false, false, false);
						pathLine = bigger_frame_left(pathLine, true, false, false);
						
						//path = suggestion_frame_for_buttons();
						//pathLine = suggestion_frame_for_buttons();
					}
					
					
					
					
				} else if (biggerFrameLeftOrientUpDownm==SwifteeApplication.FRAME_ORIENT_UP){
					
					//path = flat_frame_up(path);
					//pathLine = flat_frame_up(pathLine);					
				}
				
				break;	
				
			case SwifteeApplication.TAB_SUGGESTION_BUTTON:			
				
				path = suggestionButton();
				pathLine = suggestionButton();			
				break;	
				
			case SwifteeApplication.TIP_FOR_CONTENT_OBJECT:				
				
				int tipOrient = (Integer) params[1];	
				int vertical = (Integer) params[2];					
				boolean biggerThanComplete = SwifteeApplication.getTipMessageBigger();
				
				path = tipPath(path, tipOrient, vertical, biggerThanComplete);
				pathLine = tipPath(path, tipOrient, vertical,biggerThanComplete);			
							
				break;
				
			case SwifteeApplication.TAB_LINK:				
				
				int linkOrientUpDownm = (Integer) params[1];		
				int linkOrientULeftRight = (Integer) params[2];
				
				path = straightPath(path, linkOrientUpDownm, linkOrientULeftRight, false);
				pathLine = straightPath(path, linkOrientUpDownm, linkOrientULeftRight, true);			
				
				spacer = (Integer) params[3];
				data = (Object[][]) ((RingView) vParent).getData();					
				setTabActions(data, originalId);
							
				break;	
			
			case SwifteeApplication.TAB_TEXT:				
				
				int textOrientUpDownm = (Integer) params[1];	
				
				path = textPath(path, textOrientUpDownm);
				pathLine = textPath(path, textOrientUpDownm);		
				
				spacer = (Integer) params[3];
				data = (Object[][]) ((RingView) vParent).getData();
				
				setTabActions(data, originalId);
							
				break;	
				
			case SwifteeApplication.ANCHOR_SPINNER_BACKGROUND:
				
				int spinnerAnchorBackOrientlLeftRight = (Integer) params[1];	
				
				path = anchor_spinner_background_path(path, spinnerAnchorBackOrientlLeftRight, false);
				pathLine = anchor_spinner_background_path(path, spinnerAnchorBackOrientlLeftRight, true);
				
				break;

			case SwifteeApplication.PADKITE_INPUT_SPINNER_BACKGROUND:
				
				int spinnerPadKiteInputBackOrientlLeftRight = (Integer) params[1];	
				
				path = padkite_input_spinner_background_path(path, spinnerPadKiteInputBackOrientlLeftRight);
				pathLine = padkite_input_spinner_background_path(path, spinnerPadKiteInputBackOrientlLeftRight);
				
				break;
			
				
			}
		
		all[0] = path;
		all[1] = pathLine;
		return all;
	}
	
	private Path padkite_input_spinner_background_path(Path p, int orient){
		
		if ( orient == SwifteeApplication.PADKITE_INPUT_SPINNER_BACKGROUND_ORIENTED_RIGHT ){	
			
			p = padkite_input_spinner_background(p);		
			
		} else if ( orient == SwifteeApplication.PADKITE_INPUT_SPINNER_BACKGROUND_ORIENTED_LEFT ){				
		
			//p = anchor_spinner_background_left(p);				
		}
		
		return p;		
	}	
	
	private Path anchor_spinner_background_path(Path p, int orient, boolean line){
		
		if ( orient == SwifteeApplication.ANCHOR_SPINNER_BACKGROUND_ORIENTED_RIGHT ){	
			
			p = anchor_spinner_background_right(p, line);		
			
		} else if ( orient == SwifteeApplication.ANCHOR_SPINNER_BACKGROUND_ORIENTED_LEFT ){				
		
			p = anchor_spinner_background_left(p);				
		}
		
		return p;		
	}		
	
	private Path tipPath(Path p, int orient, int vertical, boolean biggerThanComplete){
		
		int cType = SwifteeApplication.getCType();
		
		if (cType == WebHitTestResult.SRC_ANCHOR_TYPE){			
		
			if ( orient == SwifteeApplication.TIP_CONTENT_ORIENT_UP ){	
				
				//p = tip_frame_down(p, biggerThanComplete);		
				
			} else if ( orient == SwifteeApplication.TIP_CONTENT_ORIENT_BOTTOM ){				
			
				if ( vertical == SwifteeApplication.VERTICAL_RIGHT_COLUMN ){				
					
					p = tip_frame_down_orient_left(p, biggerThanComplete);
					
					//p = flat_path_up_left(p, line);				
				} 
				
				/*else if ( orientRightLeft == SwifteeApplication.TAB_ORIENT_RIGHT ){
					
					p = flat_path_up_right(p, line);
					
				} 
				
				else if ( orientRightLeft == SwifteeApplication.TAB_ORIENT_CENTER ){
					
					p = flat_path_up_center(p);				
					
				}*/		
				
							
			}
		
		} else {
			
			
			if ( orient == SwifteeApplication.TIP_CONTENT_ORIENT_UP ){	
				
				p = flat_frame_up(p);		
				
			} else if ( orient == SwifteeApplication.TIP_CONTENT_ORIENT_BOTTOM ){				
			
				p = flat_frame_down(p);				
			}
			
		}
		
		
		return p;		
	}	
	
	private Path textPath(Path p, int orientUpDown){
		
		if ( orientUpDown == SwifteeApplication.TEXT_TAB_ORIENT_UP ){	
			
			p = text_tab_path_up(p);
			
			
		} else if ( orientUpDown == SwifteeApplication.TEXT_TAB_ORIENT_DOWN ){				
		
			p = flat_frame_down(p);	
			
		}
		
		return p;		
	}
	
	private Path text_tab_path_up(Path p){
		
		p.moveTo(X, Y + height);
		p.lineTo(X, Y + (edge/2));
		
		//upper left arc
		RectF A1 = new RectF();			
		A1.left = X;
		A1.top = Y;
		A1.right = X + edge;
		A1.bottom = Y + edge;							
		p.arcTo(A1, 180, 90, false);
		
		p.lineTo(X + width - (edge/2), Y);
		
		//up right arc
		RectF A2 = new RectF();
		A2.left = X + width - edge;
		A2.top = Y;
		A2.right = X + width;
		A2.bottom = Y + edge;										
		p.arcTo(A2, 270, 90, true);
				
		p.lineTo(X + width, Y + height);		
		p.lineTo(X+(edge/2), Y + height);		

		p.lineTo(X, Y + height);
		
		return p;		
	
	}
	
	private Path straightPath(Path p, int orientUpDown, int orientRightLeft, boolean line){
		
		if ( orientUpDown == SwifteeApplication.LINK_TAB_ORIENT_UP ){	
			
			if ( orientRightLeft == SwifteeApplication.TAB_ORIENT_LEFT ){				
			
				p = flat_path_up_left(p, line);				
			} 
			
			else if ( orientRightLeft == SwifteeApplication.TAB_ORIENT_RIGHT ){
				
				p = flat_path_up_right(p, line);
				
			} 
			
			else if ( orientRightLeft == SwifteeApplication.TAB_ORIENT_CENTER ){
				
				p = flat_path_up_center(p);				
				
			}
			
		} else if ( orientUpDown == SwifteeApplication.LINK_TAB_ORIENT_DOWN ){				
		
			p = flat_frame_down(p);	
			
		}
		
		return p;		
	}
	
	private Path flat_path_up_right(Path p, boolean line){		
		
		p.moveTo(X, Y + height - (edge/2));
		p.lineTo(X, Y + (edge/2));
		
		//upper left arc
		RectF A1 = new RectF();			
		A1.left = X;
		A1.top = Y;
		A1.right = X + edge;
		A1.bottom = Y + edge;							
		p.arcTo(A1, 180, 90, false);
		
		p.lineTo(X + width - (edge/2), Y);
		
		//up right arc
		RectF A2 = new RectF();
		A2.left = X + width - edge;
		A2.top = Y;
		A2.right = X + width;
		A2.bottom = Y + edge;										
		p.arcTo(A2, 270, 90, false);		
		
		p.lineTo(X + width, Y + height + (edge/2));	

		//down rigt arc
		RectF A3 = new RectF();
		A3.left = X + width - edge;
		A3.top = Y + height;
		A3.right = X + width;
		A3.bottom = Y + height + edge;										
		p.arcTo(A3, 0, -90, false);	
		
		p.lineTo(X-(edge/2), Y + height);
		
		//down left arc
		RectF A5 = new RectF();
		A5.left = X - edge;
		A5.top = Y + height - edge;
		A5.right = X;
		A5.bottom = Y + height;										
		p.arcTo(A5, 90, -90, false);	
		
		return p;		
	
	}
	
	private Path flat_path_up_center(Path p){		
		
		p.moveTo(X-(edge/2), Y + height);
				
		//upper left arc
		RectF A0 = new RectF();			
		A0.left = X-edge;
		A0.top = Y + height - edge;
		A0.right = X;
		A0.bottom = Y + height;							
		p.arcTo(A0, 90, -90, false);
		
		p.lineTo(X, Y + (edge/2));
		
		//upper left arc
		RectF A1 = new RectF();			
		A1.left = X;
		A1.top = Y;
		A1.right = X + edge;
		A1.bottom = Y + edge;							
		p.arcTo(A1, 180, 90, false);
		
		p.lineTo(X + width - (edge/2), Y);
		
		//up right arc
		RectF A2 = new RectF();
		A2.left = X + width - edge;
		A2.top = Y;
		A2.right = X + width;
		A2.bottom = Y + edge;										
		p.arcTo(A2, 270, 90, false);
				
		p.lineTo(X + width, Y + height - (edge/2));	
		
		//down right
		RectF A3 = new RectF();
		A3.left = X + width;
		A3.top = Y + height - edge;
		A3.right = X + width + edge;
		A3.bottom = Y + height;										
		p.arcTo(A3, 180, -90, false);
		
		p.lineTo(X, Y + height);
		
		return p;		
	
	}
	
	private Path flat_path_up_left(Path p, boolean line){
		
		p.moveTo(X, Y + height + (edge/2));
		p.lineTo(X, Y + (edge/2));
		
		//upper left arc
		RectF A1 = new RectF();			
		A1.left = X;
		A1.top = Y;
		A1.right = X + edge;
		A1.bottom = Y + edge;							
		p.arcTo(A1, 180, 90, false);
		
		p.lineTo(X + width - (edge/2), Y);
		
		//up right arc
		RectF A2 = new RectF();
		A2.left = X + width - edge;
		A2.top = Y;
		A2.right = X + width;
		A2.bottom = Y + edge;										
		p.arcTo(A2, 270, 90, true);	
				
		p.lineTo(X + width, Y + height - (edge/2));
		
		//down right arc
		RectF A3 = new RectF();
		A3.left = X + width;
		A3.top = Y + height - edge;
		A3.right = X + width + edge;
		A3.bottom = Y + height;										
		p.arcTo(A3, 180, -90, false);	
		
		p.lineTo(X+(edge/2), Y + height);		
	
		//down left arc
		RectF A4 = new RectF();
		A4.left = X;
		A4.top = Y + height;
		A4.right = X + edge;
		A4.bottom = Y + height + edge;										
		p.arcTo(A4, 270, -90, false);	
			
		return p;	
	
	}	
	
	private Path flat_frame_up(Path p){
		
		p.moveTo(X, Y + height + (edge/2));
		p.lineTo(X, Y + (edge/2));
		
		//upper left arc
		RectF A1 = new RectF();			
		A1.left = X;
		A1.top = Y;
		A1.right = X + edge;
		A1.bottom = Y - edge;							
		p.arcTo(A1, 180, 90, false);
		
		p.lineTo(X + width - (edge/2), Y);
		
		//up right arc
		RectF A2 = new RectF();
		A2.left = X + width - edge;
		A2.top = Y;
		A2.right = X + width;
		A2.bottom = Y + edge;										
		p.arcTo(A2, 270, 90, true);
				
		p.lineTo(X + width, Y + height);
				
		//down rigt arc
		RectF A3 = new RectF();
		A3.left = X + width - edge;
		A3.top = Y + height - edge;
		A3.right = X + width;
		A3.bottom = Y + height;										
		p.arcTo(A3, 0, -90, false);
				
		p.lineTo(X+width-(edge/2), Y + height -(edge/2));		

		//down rigt arc
		RectF A4 = new RectF();
		A4.left = X + width - edge;
		A4.top = Y + height - edge;
		A4.right = X + width;
		A4.bottom = Y + height;										
		p.arcTo(A4, 270, -90, false);
		
		return p;
	}
	
	private Path bigger_frame_left(Path p, boolean line, boolean controls, boolean expanded){	
		
		Rect biggerRect = SwifteeApplication.getBiggerRectResize();		
		int bX = biggerRect.left;		
		Rect spinnerRect = SwifteeApplication.getSpinnerBackgroundRect();	
		int spLeft = spinnerRect.left-(edge/2);		
				
		//RIGHT PATH		
		p.moveTo(X+(width/2), Y+(edge/2));
		p.lineTo(X+width-(edge/2), Y+(edge/2));
			
		//upper right arc
		RectF A0 = new RectF();			
		A0.left = X+width-edge;
		A0.top = Y - (edge/2);
		A0.right = X + width; 
		A0.bottom = Y + (edge/2);							
		p.arcTo(A0, 90, -90, false);
				
		if (line){
			p.moveTo(X+width, Y-(edge));
		}
		
		p.lineTo(X+width, Y+height-(edge/2));
		
		//down right arc
		RectF A1 = new RectF();			
		A1.left = X+width-edge;
		A1.top = Y+height-edge;
		A1.right = X + width; 
		A1.bottom = Y + height;							
		p.arcTo(A1, 0, 90, false);	
				
		if (controls){

			p.lineTo( X + width - (edge*2), Y + height);		
				
			//down middle arc first up
			RectF AC0 = new RectF();
			AC0.left = X + width - (edge*2);
			AC0.top = Y + height;
			AC0.right = X + width - edge;		
			AC0.bottom = Y + height + edge;							
			p.arcTo(AC0, 270, -90, false);	
			
			if (expanded){
				//bottom down line	
				p.lineTo( X + width - (edge*2), Y+height + (edge*2));
			} else {
				p.lineTo( X + width - (edge*2), Y+height + (edge + (edge/2)));
			}	
			
			//down right middle arc first
			RectF AC3 = new RectF();
			AC3.left = X + width - (edge*3);
			if (expanded){
				AC3.top = Y + height + edge;
				AC3.bottom = Y + height + (edge*2);
			} else {
				AC3.top = Y + height + (edge/2);
				AC3.bottom = Y + height + edge + (edge/2);			
			}
			AC3.right = X + width - (edge*2);										
			p.arcTo(AC3, 0, 90, false);			
						
			
			//down left middle arc second down
			RectF AC2 = new RectF();
			AC2.left = accumulator + (edge/2);		
			AC2.right = accumulator + (edge/2) + edge;
			if (expanded){
				AC2.top = Y + height + edge;
				AC2.bottom = Y + height + (edge*2);
			} else {
				AC2.top = Y + height + (edge/2);
				AC2.bottom = Y + height + edge + (edge/2);	
			}
			p.arcTo(AC2, 90, 90, false);		
				
			
			if (expanded){
				//left vertical center line	
				p.lineTo( accumulator + (edge/2), Y+height + edge);
			}		
			
			//down middle arc first up
			RectF AC1 = new RectF();
			AC1.left = accumulator - (edge/2);
			AC1.top = Y + height;
			AC1.right = accumulator + (edge/2);
			AC1.bottom = Y + height + edge;							
			p.arcTo(AC1, 0, -90, false);			
		}		
	
		//Line to bigger left down
		p.lineTo(bX+(edge/2), Y + height);
		
		//down left arc
		RectF A7 = new RectF();	
		A7.left = bX;
		A7.top = Y+height-edge;
		A7.right = bX + edge; 
		A7.bottom = Y + height;							
		p.arcTo(A7, 90, 90, false);		

		//Line to bigger bugger left up
		p.lineTo(bX, Y+(edge/2));		
		
		//up left arc
		RectF A6 = new RectF();			
		A6.left = bX;
		A6.top = Y+(edge/2);
		A6.right = bX + edge; 
		A6.bottom = Y+edge+(edge/2);							
		p.arcTo(A6, 180, 90, false);

		// Line to spinner left rect
		p.lineTo(spLeft-(edge/2), Y+(edge/2));	
		
		//up left-left arc 
		RectF A2 = new RectF();			
		A2.left = spLeft-edge;
		A2.top = Y - (edge/2);
		A2.right = spLeft; 
		A2.bottom = Y + (edge/2);					
		p.arcTo(A2, 90, -90, false);
		
		//Square below spinner left
		p.lineTo(spLeft, Y+(edge/2));			
		p.lineTo(X, Y+(edge/2));
		p.lineTo(X, Y-(edge/2));
			
		//up left-left arc 
		RectF A3 = new RectF();			
		A3.left = X+1;
		A3.top = Y - (edge/2);
		A3.right = X+1 + edge; 
		A3.bottom = Y + (edge/2);					
		p.arcTo(A3, 180, -90, false);		
		
		p.lineTo(X+(width/2), Y+(edge/2));
		
	return p;		
	
	}
	
	private Path bigger_frame_right(Path p, boolean line){	
		
		Rect biggerRect = SwifteeApplication.getBiggerRectResize();
		int bX = biggerRect.left;
		int bWidth = biggerRect.right;
		
		//Rect spinnerRect = SwifteeApplication.getSpinnerBackgroundRect();	
		//int spRight = spinnerRect.left  + spinnerRect.right;
		
		Rect master = SwifteeApplication.getMasterRect();
		int mRight = master.right + (edge/4);
					
		//LEFT PATH		
		p.moveTo(X+(width/2), Y+(edge/2));
		p.lineTo(X+(edge/2), Y+(edge/2));
		
		//upper left arc
		RectF A0 = new RectF();			
		A0.left = X+2;
		A0.top = Y - (edge/2);
		A0.right = X + edge; 
		A0.bottom = Y + (edge/2);							
		p.arcTo(A0, 90, 90, false);			
	
		p.lineTo(X+2, Y+height-(edge/2));
		
		//down left arc
		RectF A1 = new RectF();			
		A1.left = X+2;
		A1.top = Y+height-edge;
		A1.right = X + edge; 
		A1.bottom = Y + height;							
		p.arcTo(A1, 180, -90, false);	
		
		p.lineTo(X+(width/2), Y+height);	
		p.lineTo(X+(width/2), Y+(edge/2));
					
		//RIGHT PATH		
		p.moveTo(X+(width/2), Y+(edge/2));
		p.lineTo(mRight-(edge/2), Y+(edge/2));	
				
		RectF A2 = new RectF();			
		A2.left = mRight - edge;
		A2.top = Y - (edge/2);
		A2.right = mRight; 
		A2.bottom = Y + (edge/2);					
		p.arcTo(A2, 90, -90, false);
				
		RectF A4 = new RectF();			
		A4.left = mRight;
		A4.top = Y-(edge/2);
		A4.right = mRight + edge; 
		A4.bottom = Y + (edge/2);							
		p.arcTo(A4, 180, -90, false);
		
		p.lineTo(X + width-(edge/2), Y+(edge/2));		
		
			
		//up right-left arc 
		RectF A5 = new RectF();			
		A5.left = X + width - edge;
		A5.top = Y - (edge/2);
		A5.right = X + width; 
		A5.bottom = Y + (edge/2);					
		p.arcTo(A5, 90, -90, false);
		
		//up right-right arc 
		RectF A6 = new RectF();			
		A6.left = X + width;
		A6.top = Y-(edge/2);
		A6.right = X + width + edge; 
		A6.bottom = Y + (edge/2);							
		p.arcTo(A6, 180, -90, false);		
		
		p.lineTo(bWidth-(edge/2), Y+(edge/2));
		
		//up right border arc
		RectF A7 = new RectF();			
		A7.left = bWidth - edge;
		A7.top = Y+(edge/2);
		A7.right = bWidth; 
		A7.bottom = Y+edge+(edge/2);							
		p.arcTo(A7, 270, 90, false);	
		
		p.lineTo(bWidth, Y + height - (edge/2));
		
		//down right arc
		RectF A8 = new RectF();	
		A8.left = bWidth-edge;
		A8.top = Y+height-edge;
		A8.right = bWidth; 
		A8.bottom = Y + height;							
		p.arcTo(A8, 0, 90, false);		

		p.lineTo(X+(width/2), Y+height);	
		
		//if (line){			
		p.lineTo(X+(width/2), Y+(edge/2));
		//}
		
	return p;		
	
	}
	
	private Path bigger_frame_center(Path p, boolean line){	
		
		//ACA	
		
		Rect biggerRect = SwifteeApplication.getBiggerRectResize();
		int bX = biggerRect.left;
		int bWidth = biggerRect.right;		
		
		Rect master = SwifteeApplication.getMasterRect();
		int mRight = master.right + (edge/4);
		
		//RIGHT PATH		
		p.moveTo(X+(width/2), Y+(edge/2));	
		p.lineTo(mRight - (edge/2), Y+(edge/2));	
		
		//upper out right arc1
		RectF A0 = new RectF();			
		A0.left = mRight - edge;
		A0.top = Y - (edge/2);
		A0.right = mRight; 
		A0.bottom = Y + (edge/2);							
		p.arcTo(A0, 90, -90, false);
		
		RectF A1 = new RectF();			
		A1.left = mRight;
		A1.top = Y - (edge/2);
		A1.right = mRight + edge; 
		A1.bottom = Y + (edge/2);							
		p.arcTo(A1, 180, -90, false);
		
		p.lineTo(X+width-(edge/2), Y+(edge/2));	
		
		
		//upper out right arc1
		RectF A6 = new RectF();			
		A6.left = X + width - edge;
		A6.top = Y - (edge/2);
		A6.right = X + width; 
		A6.bottom = Y + (edge/2);							
		p.arcTo(A6, 90, -90, false);	
		
		//upper out right arc1
		RectF A7 = new RectF();			
		A7.left = X + width;
		A7.top = Y - (edge/2);
		A7.right = X + width + edge; 
		A7.bottom = Y + (edge/2);							
		p.arcTo(A7, 180, -90, false);	
		
		//p.lineTo(bWidth-(edge/2), Y+(edge/2));
			
		//up right arc
		RectF A4 = new RectF();			
		A4.left = bWidth - edge;
		A4.top = Y+(edge/2);
		A4.right = bWidth; 
		A4.bottom = Y+edge+(edge/2);							
		p.arcTo(A4, 270, 90, false);
		
		p.lineTo(bWidth, Y+height-(edge/2));		
		
		//down right arc
		RectF A5 = new RectF();			
		A5.left = bWidth - edge;
		A5.top = Y + height - edge;
		A5.right = bWidth; 
		A5.bottom = Y+height;							
		p.arcTo(A5, 0, 90, false);			
		
		p.lineTo(X+(width/2), Y+height);		
	
		//LEFT PATH		
		p.moveTo(X+(width/2), Y+(edge/2));
		p.lineTo(X+(edge/2), Y+(edge/2));	
		
		//upper out left-right
		RectF A7a = new RectF();			
		A7a.left = X;
		A7a.top = Y - (edge/2);
		A7a.right = X + edge; 
		A7a.bottom = Y + (edge/2);							
		p.arcTo(A7a, 90, 90, false);	
		
		//upper left out arc
		RectF A8 = new RectF();			
		A8.left = X-edge;
		A8.top = Y-(edge/2);
		A8.right = X; 
		A8.bottom = Y+(edge/2);							
		p.arcTo(A8, 0, 90, false);		
		
		p.lineTo(bX+(edge/2), Y+(edge/2));
		
		//up left arc
		RectF A2 = new RectF();			
		A2.left = bX;
		A2.top = Y + (edge/2);
		A2.right = bX+edge; 
		A2.bottom = Y + edge + (edge/2);							
		p.arcTo(A2, 270, -90, false);				
		
		p.lineTo(bX, Y + height-(edge/2));
				
		//down left arc 
		RectF A3 = new RectF();			
		A3.left = bX;
		A3.top = Y+height-edge;
		A3.right = bX+edge; 
		A3.bottom = Y+height;						
		p.arcTo(A3, 180, -90, false);
		
		p.lineTo(X+(width/2), Y+height);
		
		if (line){					
			p.lineTo(X+(width/2), Y+(edge/2));
		}	
		
	return p;		
	
	}	
	
	private Path flat_frame_down(Path p){	
		
		p.moveTo(X, Y);	
		p.lineTo(X, Y+height-(edge/2));
		
		//down left arc
		RectF A1 = new RectF();			
		A1.left = X;
		A1.top = Y+height-edge;
		A1.right = X + edge; 
		A1.bottom = Y + height;							
		p.arcTo(A1, 180, -90, false);
				
		p.lineTo(X+width-(edge/2), Y+height);
		
		//down right arc
		RectF A2 = new RectF();			
		A2.left = X+width-edge;
		A2.top = Y+height-edge;
		A2.right = X + width; 
		A2.bottom = Y + height;							
		p.arcTo(A2, 90, -90, false);

		p.lineTo(X+width, Y-(edge/2));
		
		//up right arc
		RectF A3 = new RectF();			
		A3.left = X+width-edge;
		A3.top = Y-(edge/2);
		A3.right = X + width; 
		A3.bottom = Y + (edge/2);							
		p.arcTo(A3, 0, 90, false);

		p.lineTo(X+(edge/2), Y+(edge/2));
		
		//up left arc
		RectF A4 = new RectF();			
		A4.left = X;
		A4.top = Y-(edge/2);
		A4.right = X + edge; 
		A4.bottom = Y + (edge/2);							
		p.arcTo(A4, 90, 90, false);
		
	return p;		
	
	}		
	
	private Path tip_frame_down_orient_left(Path p, boolean biggerThanComplete){	
		
		p.moveTo(X, Y);	
		p.lineTo(X, Y+height-(edge/2));
		
		//down left arc
		RectF A1 = new RectF();			
		A1.left = X;
		A1.top = Y+height-edge;
		A1.right = X + edge; 
		A1.bottom = Y + height;							
		p.arcTo(A1, 180, -90, false);		
		
		p.lineTo(X+width-(edge/2), Y+height);
		
		//down right arc
		RectF A2 = new RectF();			
		A2.left = X+width-edge;
		A2.top = Y+height-edge;
		A2.right = X + width; 
		A2.bottom = Y + height;							
		p.arcTo(A2, 90, -90, false);

		p.lineTo(X+width, Y-(edge/2));
		
		//up right arc
		RectF A3 = new RectF();			
		A3.left = X+width-edge;
		A3.top = Y-(edge/2);
		A3.right = X + width; 
		A3.bottom = Y + (edge/2);							
		p.arcTo(A3, 0, 90, false);	
		
		p.lineTo(X+(edge/2), Y+(edge/2));
		
		if (biggerThanComplete){
		
			//up left arc
			RectF A4 = new RectF();			
			A4.left = X;
			A4.top = Y+(edge/2);
			A4.right = X + edge; 
			A4.bottom = Y + edge + (edge/2);							
			p.arcTo(A4, 270, -90, false);
			
		} else {		
		
			//up left arc
			RectF A4 = new RectF();			
			A4.left = X;
			A4.top = Y-(edge/2);
			A4.right = X + edge; 
			A4.bottom = Y + (edge/2);							
			p.arcTo(A4, 90, 90, false);
		
		}
		
	return p;		
	
	}		
	
	private Path angleTabPath(boolean line, int orient){
		
		Path p = new Path();		
		
		if ( orient == SwifteeApplication.TAB_ORIENT_LEFT ){
		
			p = centerLeftTab(p);
			p.addPath(endTabLeft(p));			
			
		} else if ( orient == SwifteeApplication.TAB_ORIENT_CENTER ){
			
			p = centerTab(line, p);
			p.addPath(centerLeftTab(p));		
		
		} else if ( orient == SwifteeApplication.TAB_ORIENT_RIGHT ){
			
			p = centerTab(line, p);
			p.addPath(endTabRight(p));	
			
		}	
		
		return p;		
	}	
		
	private Path centerTab(boolean line, Path p){
				
		//left bottom line
		p.moveTo(X+(edge/2), Y+height);		
				
		//down left arc
		RectF A2 = new RectF();
		A2.left = X;
		A2.top = Y + (edge/2);
		A2.right = X + edge;;
		A2.bottom = Y + height;		
		boolean cont;
		if (originalId==4){ cont = false; 
		} else { cont = true; }
		p.arcTo(A2, 90, -80, cont);
		
		//gap between arcs line		
		p.lineTo(X + (edge+(module*3)+(module/2)), Y+(module*3));		
				
		if (line){			
			/**
			 * Drawing upper line and left upper
			 * counter clock wise due to bug on SDK.**/		
			//upper left arc
			p.moveTo(X + edge + (edge/2) + (module*3), Y);
			RectF A1_L = new RectF();
			A1_L.left = X + edge + (module*3);
			A1_L.top = Y;
			A1_L.right = X + (edge*2) + (module*3);
			A1_L.bottom = Y + edge;			
			p.arcTo(A1_L, 270, -80, false);		
			
			//upper central line
			p.moveTo(X + width - (edge + (module*8)), Y);
			p.lineTo(X + edge + (edge/2) + (module*3), Y);
			
		} else {
			
			//upper left arc
			RectF A1 = new RectF();
			A1.left = X + edge + (module*3);
			A1.top = Y;
			A1.right = X + (edge*2) + (module*3);
			A1.bottom = Y + edge;	
			boolean cont1;
			if (originalId==4){ cont1 = false; 
			} else { cont1 = true; }			
			p.arcTo(A1, 190, 80, cont1);		
			
			//upper central line
			//p.moveTo(X + width - (edge + (module*8)), Y);
			//p.lineTo(X + width - ((edge+(edge/2))+module*2), Y);
		}		
		return p;		
	}
	
	private Path centerLeftTab(Path p){	
		
		if (originalId==0){
			p.moveTo(X + (edge/2), Y);
			p.lineTo(X + width - ((edge+(edge/2))+module*2), Y);
		} else {
			p.lineTo(X + width - ((edge+(edge/2))+module*2), Y);
		}		
		
		//upper right arc
		RectF A3 = new RectF();			
		A3.left = X + width - ((edge*2)+(module*3));
		A3.top = Y;
		A3.right = X + width - (edge+(module*3));
		A3.bottom = Y + edge;							
		p.arcTo(A3, 270, 80, false);
		
		//gap between arcs line	
		p.lineTo(X + width - edge + (module/2), Y + height -(module*3));	
		
		//down rigt arc
		RectF A4 = new RectF();
		A4.left = X + width - edge;
		A4.top = Y + height - edge;
		A4.right = X + width;
		A4.bottom = Y + height;										
		p.arcTo(A4, 170, -80, false);	
		
		p.lineTo(X+(edge/2), Y+height);
		
		return p;
						
	}
	
	private Path endTabLeft(Path p){
		
		p.moveTo(X + width - (edge/2), Y + height);
		p.lineTo(X + (edge/2), Y + height);
		
		//down left arc
		RectF ARight2 = new RectF();
		ARight2.left = X;
		ARight2.top = Y + height;
		ARight2.right = X + edge;
		ARight2.bottom = Y + height  + edge;
		p.arcTo(ARight2, 270, -90, false);		
		
		//p.moveTo(X, Y + height + (edge/2));
		p.lineTo(X, Y+(edge/2));
		
		//up left arc
		RectF ALeft1 = new RectF();
		ALeft1.left = X;
		ALeft1.top = Y;
		ALeft1.right = X + edge;
		ALeft1.bottom = Y + edge;							
		//p.arcTo(A2, 10, 80, true);
		p.arcTo(ALeft1, 180, 90, false);	
		
		return p;
		
	}	
	
	private Path endTabRight(Path p){	
		
		p.lineTo(X + width - (edge/2), Y);
		
		//up right arc
		RectF ARight1 = new RectF();
		ARight1.left = X + width - edge;
		ARight1.top = Y;
		ARight1.right = X + width;
		ARight1.bottom = Y + edge;										
		p.arcTo(ARight1, 270, 90, true);
		
		//p.moveTo(X + width, Y + (edge/2));
		p.lineTo(X + width, Y + height + (edge/2));
				
		//down rigt arc
		RectF ARight2 = new RectF();
		ARight2.left = X + width - edge;
		ARight2.top = Y + height;
		ARight2.right = X + width;
		ARight2.bottom = Y + height + edge;										
		p.arcTo(ARight2, 0, -90, false);
				
		p.lineTo(X+(edge/2), Y + height);
		
		return p;	
	}	
	
	
	private Path input_frame_for_expanded() {
				
		Path p = new Path();	
		
		//left bottom line
		p.moveTo(X, Y);
		p.lineTo(X+(edge/2), Y);		
				
		//up left arc
		RectF A1 = new RectF();
		A1.left = X;
		A1.top = Y;
		A1.right = X + edge;
		A1.bottom = Y + edge;							
		p.arcTo(A1, 270, 90, false);		
		
		//left vertical line
		p.lineTo(X + edge, Y + height - (edge/2));
				
		//up left second arc
		RectF A2 = new RectF();
		A2.left = X + edge;
		A2.top = Y + height - edge;
		A2.right = X + (edge*2);
		A2.bottom = Y + height;							
		p.arcTo(A2, 180, -90, false);
		
		//left middle line
		p.lineTo(X + width - (edge+(edge/2)), Y + height);
		
		//up left second arc
		RectF A3 = new RectF();
		A3.left = X + width - (edge*2);
		A3.top = Y + height - edge;
		A3.right = X + width - edge;
		A3.bottom = Y + height;							
		p.arcTo(A3, 90, -90, false);				
		
		//right vertical line
		p.lineTo(X + width - edge, Y + (edge/2));
				
		//up right arc
		RectF A4 = new RectF();
		A4.left = X + width - edge;
		A4.top = Y;
		A4.right = X + width;
		A4.bottom = Y + edge;							
		p.arcTo(A4, 180, 90, false);
		
		//right bottom line		
		p.lineTo(X+width, Y);	

		return p;
	}
	
	private Path suggestion_frame_for_futtons_fit_controls(){
		
		boolean expanded = SwifteeApplication.getExpanded();
		
		Path p = new Path();	
		
		//left bottom line
		p.moveTo(X, Y);
		p.lineTo(X+(edge/2), Y);		
				
		//up left arc
		RectF A1 = new RectF();
		A1.left = X;
		A1.top = Y;
		A1.right = X + edge;
		A1.bottom = Y + edge;							
		p.arcTo(A1, 270, 90, false);
		
		//left middle line
		if (accumulator>0){
			p.lineTo(X + edge, Y + height - (edge+(edge/2)));
		}
		
		//up left second arc
		RectF A1_2 = new RectF();
		A1_2.left = X + edge;
		A1_2.top = Y + height - (edge*2);
		A1_2.right = X + (edge*2);
		A1_2.bottom = Y + height - edge;							
		p.arcTo(A1_2, 180, -90, false);
		
		//left middle line
		p.lineTo(accumulator, Y + height - edge);
				
		//down middle arc first up
		RectF A3 = new RectF();
		A3.left = accumulator - (edge/2);
		A3.top = Y + height -edge;
		A3.right = accumulator + (edge/2);
		A3.bottom = Y + height;							
		p.arcTo(A3, 270, 90, false);	
		
		if (expanded){
			//left vertical center line	
			p.lineTo( accumulator + (edge/2), Y+height+(edge/2));
		} 
	
		//down left middle arc second down
		RectF A3_2 = new RectF();
		A3_2.left = accumulator + (edge/2);		
		A3_2.right = accumulator + (edge/2) + edge;
		if (expanded){
			A3_2.top = Y + height;
			A3_2.bottom = Y + height + edge;
		} else {
			A3_2.top = Y + height - edge;
			A3_2.bottom = Y + height;	
		}
		p.arcTo(A3_2, 180, -90, false);		
		
		if (expanded){
			//bottom down line	
			p.lineTo( (X + width) - ((edge*3)+(edge/2)), Y+height + edge);
		} else {
			p.lineTo( (X + width) - ((edge*3)+(edge/2)), Y+height);
		}
				
		//down right middle arc first
		RectF A4 = new RectF();
		A4.left = X + width - (edge*4);
		if (expanded){
			A4.top = Y + height;
			A4.bottom = Y + height + edge;
		} else {
			A4.top = Y + height - edge;
			A4.bottom = Y + height;			
		}
		A4.right = X + width - (edge*3);
								
		p.arcTo(A4, 90, -90, false);
		
		if (expanded){
			//right vertical center line	
			p.lineTo( X + width - (edge*3), Y+height+(edge/2));
		}
	
		//down right middle arc second
		RectF A4_2 = new RectF();
		A4_2.left = X + width - (edge*3);
		A4_2.top = Y + height - edge;
		A4_2.right = X + width - (edge*2);
		A4_2.bottom = Y + height;							
		p.arcTo(A4_2, 180, 90, false);
				
		if (expanded){
			//	middle down right line	
			p.lineTo( X + width - (edge*2), Y+height-edge);
		} else {
			p.lineTo( (X + width) - (edge+(edge/2)), Y+height-edge);
		}
	
				
		//down right middle arc third
		RectF A4_3 = new RectF();
		A4_3.left = X + width - (edge*2);
		A4_3.top = Y + height - (edge*2);
		A4_3.right = X + width - edge;
		A4_3.bottom = Y + height - edge;							
		p.arcTo(A4_3, 90, -90, false);
	
		//right middle line
		if (accumulator>0){
			//p.moveTo(X + width - edge, Y + height - (edge+(edge/2)));
			p.lineTo(X + width - edge, Y+(edge/2));
		}
		
		//down right arc
		RectF A5 = new RectF();
		A5.left = X + width - edge;
		A5.top = Y;
		A5.right = X + width;
		A5.bottom = Y + edge;							
		p.arcTo(A5, 180, 90, false);
		
		//right bottom line		
		p.moveTo(X+width-(edge/2), Y);
		p.lineTo(X+width, Y);	

		return p;
	}
	
	private Path suggestion_frame_for_buttons(){
		
		Path p = new Path();	
		
		//left bottom line
		p.moveTo(X, Y);
		p.lineTo(X+(edge/2), Y);		
				
		//up left arc
		RectF A1 = new RectF();
		A1.left = X;
		A1.top = Y;
		A1.right = X + edge;
		A1.bottom = Y + edge;							
		p.arcTo(A1, 270, 90, false);		
		
		//up left second arc
		RectF A1_2 = new RectF();
		A1_2.left = X + edge;
		A1_2.top = Y + height - edge;
		A1_2.right = X + (edge*2);
		A1_2.bottom = Y + height;							
		p.arcTo(A1_2, 180, -90, false);
		
		//left middle line
		p.lineTo(X + width - (edge*2), Y + height);	
				
		//down right middle arc third
		RectF A4_3 = new RectF();
		A4_3.left = X + width - (edge*2);
		A4_3.top = Y + height - edge;
		A4_3.right = X + width - edge;
		A4_3.bottom = Y + height;							
		p.arcTo(A4_3, 90, -90, false);	
		
		//down right arc
		RectF A5 = new RectF();
		A5.left = X + width - edge;
		A5.top = Y;
		A5.right = X + width;
		A5.bottom = Y + edge;							
		p.arcTo(A5, 180, 90, false);
		
		//right bottom line		
		p.moveTo(X+width-(edge/2), Y);
		p.lineTo(X+width, Y);	

		return p;
	}
	
	private Path suggestionButton(){
		
		Path p = new Path();	
		
		//above central line
		p.moveTo(X+(edge/2), Y);
		p.lineTo(X + width - edge, Y);
		
		//up right left arc
		RectF A2 = new RectF();
		A2.left = X + width - edge;
		A2.top = Y;
		A2.right = X + width;
		A2.bottom = Y + edge;							
		p.arcTo(A2, 270, 90, false);
		
		//right line		
		p.lineTo(X+width, Y+height-edge);
		
		//down right arc
		RectF A3 = new RectF();
		A3.left = X + width - edge;
		A3.top = Y + height - edge;
		A3.right = X + width;
		A3.bottom = Y + height;							
		p.arcTo(A3, 0, 90, false);
		
		//bottom middle line
		p.lineTo(X + edge - (edge/2) - module, Y+height);
		
		//down left arc
		RectF A4 = new RectF();
		A4.left = X;
		A4.top = Y + height - edge;
		A4.right = X + edge;
		A4.bottom = Y + height;							
		p.arcTo(A4, 90, 90, true);
		
		//right bottom line		
		p.lineTo(X, Y+ (edge/2));	
		
		//up left arc
		RectF A1 = new RectF();
		A1.left = X;
		A1.top = Y;
		A1.right = X + edge;
		A1.bottom = Y + edge;							
		p.arcTo(A1, 180, 90, false);

		return p;
	}
	
	public Path[] drawTopRow(Rect re, int type) {

		Path[] all = new Path[2];
		Path path = new Path();
		Path pathLine = new Path();	
		
		switch (type) {
		
			case SwifteeApplication.DRAW_ROW_TOP:
				path = drawRow(re, SwifteeApplication.DRAW_ROW_TOP);
				pathLine = drawRow(re, SwifteeApplication.DRAW_ROW_TOP);
				break;
			
			case SwifteeApplication.DRAW_ROW_MIDDLE:
				path = drawRow(re, SwifteeApplication.DRAW_ROW_MIDDLE);
				pathLine = drawRow(re, SwifteeApplication.DRAW_ROW_MIDDLE);
				break;	
			
			case SwifteeApplication.DRAW_ROW_BOTTOM:
				path = drawRow(re, SwifteeApplication.DRAW_ROW_BOTTOM);
				pathLine = drawRow(re, SwifteeApplication.DRAW_ROW_BOTTOM);
				break;
			case SwifteeApplication.DRAW_ROW_ROUNDED:
				path = drawRow(re, SwifteeApplication.DRAW_ROW_ROUNDED);
				pathLine = drawRow(re, SwifteeApplication.DRAW_ROW_ROUNDED);
				break;
		}
		
		all[0] = path;
		all[1] = pathLine;
		return all;
	}
	
	public Path padkite_input_spinner_background(Path p){		
				
		//central line
		p.moveTo(X, Y);
		p.lineTo(X+width-(edge/2), Y);
				
		//up right arc
		RectF A1 = new RectF();
		A1.left = X + width - edge;
		A1.top = Y;
		A1.right = X + width;
		A1.bottom = Y + edge;
		p.arcTo(A1, 270, 90, true);	
		
		//right line		
		p.lineTo(X + width, Y+height-(edge/2));
		
		//up right2 arc
		RectF A2 = new RectF();
		A2.left = X + width - edge;
		A2.top = Y + height - edge;
		A2.right = X + width;
		A2.bottom = Y + height;
		p.arcTo(A2, 0, 90, false);
		
		//bottom line	
		p.lineTo(X+(edge/2), Y+height);
		
		//down left arc
		RectF A3 = new RectF();
		A3.left = X;
		A3.top = Y + height - edge;
		A3.right = X + edge;
		A3.bottom = Y + height;
		p.arcTo(A3, 90, -90, false);
		
		//vertical left line	
		p.lineTo(X+edge, Y+(edge/2));
		
		//up left arc
		RectF A4 = new RectF();
		A4.left = X;
		A4.top = Y;
		A4.right = X + edge;
		A4.bottom = Y + edge;
		p.arcTo(A4, 0, -90, false);	
		
		return p;
	}
	
	public Path drawRow(Rect re, int direction){
		
		Path p = new Path();	
		
		int X = re.left;
		int Y = re.top;
		int width = re.width();
		int height = re.height();
		
		switch (direction){
		
			case SwifteeApplication.DRAW_ROW_TOP:
				
				//left line
				p.moveTo(X, Y+(edge/2));			
						
				//up left arc
				RectF A1 = new RectF();
				A1.left = X;
				A1.top = Y;
				A1.right = X + edge;;
				A1.bottom = Y + edge;
				p.arcTo(A1, 180, 90, true);
						
				//central line	
				p.lineTo(X + width - (edge/2), Y);
						
				//up right arc
				RectF A2 = new RectF();
				A2.left = X + width - edge;
				A2.top = Y;
				A2.right = X + width;
				A2.bottom = Y + edge;
				p.arcTo(A2, 270, 90, true);	
				
				//bottom line			
				p.lineTo(X + width, Y + height);
				p.lineTo(X, Y + height);
				p.lineTo(X, Y+(edge/2));		
				
				break;
				
			case SwifteeApplication.DRAW_ROW_MIDDLE:
				
				p.moveTo(X, Y);	
				p.lineTo(X + width, Y);
				p.lineTo(X + width, Y + height);
				p.lineTo(X, Y+height);				
				p.lineTo(X, Y);				
				break;
				
			case SwifteeApplication.DRAW_ROW_BOTTOM:
				
				p.moveTo(X, Y);	
				p.lineTo(X + width, Y);		
						
				//right line		
				p.lineTo(X + width, Y+height-(edge/2));
				
				//up right2 arc
				RectF A3 = new RectF();
				A3.left = X + width - edge;
				A3.top = Y + height - edge;
				A3.right = X + width;
				A3.bottom = Y + height;
				p.arcTo(A3, 0, 90, false);
				
				//bottom line	
				p.lineTo(X + (edge/2), Y+height);
				
				//up right2 arc
				RectF A4 = new RectF();
				A4.left = X;
				A4.top = Y + height - edge;
				A4.right = X + edge;
				A4.bottom = Y + height;
				p.arcTo(A4, 90, 90, false);
				
				//left line
				p.lineTo(X, Y);					
				break;
				
			case SwifteeApplication.DRAW_ROW_ROUNDED:				
				
				//left line
				p.moveTo(X, Y+(edge/2));			
						
				//up left arc
				RectF A5 = new RectF();
				A5.left = X;
				A5.top = Y;
				A5.right = X + edge;;
				A5.bottom = Y + edge;
				p.arcTo(A5, 180, 90, true);
						
				//central line	
				p.lineTo(X + width - (edge/2), Y);
						
				//up right arc
				RectF A6 = new RectF();
				A6.left = X + width - edge;
				A6.top = Y;
				A6.right = X + width;
				A6.bottom = Y + edge;
				p.arcTo(A6, 270, 90, true);	
				
				//right line		
				p.lineTo(X + width, Y+height-(edge/2));
				
				//up right2 arc
				RectF A7 = new RectF();
				A7.left = X + width - edge;
				A7.top = Y + height - edge;
				A7.right = X + width;
				A7.bottom = Y + height;
				p.arcTo(A7, 0, 90, false);
				
				//bottom line	
				p.lineTo(X + (edge/2), Y+height);
				
				//down left arc
				RectF A8 = new RectF();
				A8.left = X;
				A8.top = Y + height - edge;
				A8.right = X + edge;
				A8.bottom = Y + height;
				p.arcTo(A8, 90, 90, false);
				
				//left line				
				p.lineTo(X, Y+(edge/2));		
				
				break;
		}		
		return p;
	}
	
	public Path drawRoundedSquare(int X, int Y, int width, int height, int edge){
	
		/*Path[] all = new Path[2];
		Path path = new Path();
		Path pathLine = new Path();*/	
		
		/*int X = re.left;
		int Y = re.top;
		int width = re.right - re.left;
		int height = re.bottom - re.top;*/		
		
		Path p = new Path();	
		
		//left line
		p.moveTo(X, Y+(edge/2));			
				
		//up left arc
		RectF A1 = new RectF();
		A1.left = X;
		A1.top = Y;
		A1.right = X + edge;
		A1.bottom = Y + edge;
		p.arcTo(A1, 180, 90, false);
				
		//central line	
		p.lineTo(X + width - (edge/2), Y);
				
		//up right arc
		RectF A2 = new RectF();
		A2.left = X + width - edge;
		A2.top = Y;
		A2.right = X + width;
		A2.bottom = Y + edge;
		p.arcTo(A2, 270, 90, false);		
		
		//right line		
		p.lineTo(X + width, Y+height-(edge/2));
		
		//up right2 arc
		RectF A3 = new RectF();
		A3.left = X + width - edge;
		A3.top = Y + height - edge;
		A3.right = X + width;
		A3.bottom = Y + height;
		p.arcTo(A3, 0, 90, false);
		
		//bottom line	
		p.lineTo(X + (edge/2), Y+height);
		
		//up right2 arc
		RectF A4 = new RectF();
		A4.left = X;
		A4.top = Y + height - edge;
		A4.right = X + edge;
		A4.bottom = Y + height;
		p.arcTo(A4, 90, 90, false);
		
		//left line
		p.lineTo(X, Y+(edge/2));		
		
		/*all[0] = p;
		all[1] = p;*/
		
		return p;
	}
	
	
	private Path anchor_spinner_background_right(Path p, boolean line){		
		
				
		p.moveTo(X, Y);	
		//Up line
		p.lineTo(X+width-(edge/2), Y);
		
		//up rigt arc
		RectF A1 = new RectF();
		A1.left = X + width - edge;
		A1.top = Y;
		A1.right = X + width;
		A1.bottom = Y + edge;										
		p.arcTo(A1, 270, -90, false);
		
		//vertical right line
		p.lineTo(X + width - edge, Y + height);
		
		//down rigt arc
		/*RectF A2 = new RectF();
		A2.left = X + width - (edge*2);
		A2.top = Y + height - edge;
		A2.right = X + width - edge;
		A2.bottom = Y + height;										
		p.arcTo(A2, 0, 90, false);*/
	
		//down line
		p.lineTo(X-(edge/2), Y + height);

		//down left arc
		/*RectF A3 = new RectF();
		A3.left = X-(edge/2);
		A3.top = Y + height - edge;
		A3.right = X + edge + (edge/2);
		A3.bottom = Y + height;										
		p.arcTo(A3, 90, 90, false);*/
			
		p.lineTo(X-(edge/2), Y + (edge/2));	
			
		//up left arc
		RectF A4 = new RectF();
		A4.left = X - (edge/2);
		A4.top = Y;
		A4.right = X + (edge/2);
		A4.bottom = Y + edge;										
		p.arcTo(A4, 180, 90, false);
		
		return p;
	}
	
	
	
	private Path anchor_spinner_background_left(Path p){		
		
		p.moveTo(X, Y);	
		//Up line
		p.lineTo(X+width-(edge/2), Y);
		
		//up rigt arc
		RectF A1 = new RectF();
		A1.left = X + width - edge;
		A1.top = Y;
		A1.right = X + width;
		A1.bottom = Y + edge;										
		p.arcTo(A1, 270, 90, false);
		
		//vertical right line
		p.lineTo(X+width, Y + height);
		
		//down rigt arc
		/*RectF A2 = new RectF();
		A2.left = X + width - edge;
		A2.top = Y + height - edge;
		A2.right = X + width;
		A2.bottom = Y + height;										
		p.arcTo(A2, 0, 90, false);*/
		
		//down line
		p.lineTo(X+(edge/2), Y + height);
	
		//down left arc
		/*RectF A3 = new RectF();
		A3.left = X - (edge/2);
		A3.top = Y + height - edge;
		A3.right = X + (edge/2);
		A3.bottom = Y + height;										
		p.arcTo(A3, 90, -90, false);*/
		
		//vertical right line
		p.lineTo(X+(edge/2), Y + (edge/2));
		
		//up left arc
		RectF A4 = new RectF();
		A4.left = X - (edge/2);
		A4.top = Y;
		A4.right = X + (edge/2);
		A4.bottom = Y + edge;										
		p.arcTo(A4, 0, -90, false);
		
		return p;
	}	
	
	
	/**CAUTION SEE setTabToTop() on RingController to match data**/
	
	private void setTabActions(Object[][] data, int originalId){	
		
		Rect re = new Rect();
		int id = originalId;
		
		if (mP.isTabsActivated()) {

			int cType = SwifteeApplication.getCType();
			
			if (cType == WebHitTestResult.TEXT_TYPE){ 
				re.top = Y;
				re.bottom = Y + height;
			} else if (cType == WebHitTestResult.TEXT_TYPE){
				re.top = Y;		
				Rect rA = SwifteeApplication.getAnchorRect();			
				re.bottom = rA.bottom;					
			} else {
				re.top = Y;		
				Rect rA = SwifteeApplication.getActiveRect();			
				re.bottom = rA.bottom;	
			}	
			
			switch (SwifteeApplication.getTabsAmountOf()){				
				case 2:
					adjustTwo(re, data);
					break;					
				case 3:
					adjustThree(re, data);
					break;					
				case 4:
					adjustFour(re, data);
					break;					
				case 5:
					adjustFive(re, data);
					break;				
			}		
		}
	}
	
	private void adjustTwo(Rect re, Object[][] data){
		
		switch (originalId) {
		
		case SwifteeApplication.TABINDEX_CERO:					
			if (topId == 0) {					
				re.left = X;
				re.right = X + width;		
				SwifteeApplication.setTabFirstRect(re);
			} 				
			if (topId == 1) {		
				int d_x = (Integer) data[1][0];							
				re.left = X; 
				re.right = d_x;
				SwifteeApplication.setTabCeroRect(re);
			}							
			SwifteeApplication.setTab_0_Rect(re);
			break;
			
		case SwifteeApplication.TABINDEX_FIRST:					
			if (topId == 0) {						
				int d_x = (Integer) data[0][0];							
				re.left = d_x; 
				re.right = d_x + width;
				SwifteeApplication.setTabCeroRect(re);
			} 					
			if (topId == 1) {						
				re.left = X;
				re.right = X + width;	
				SwifteeApplication.setTabFirstRect(re);
			} 						
			SwifteeApplication.setTab_1_Rect(re);
			break;			
		}	
	}
	
	private void adjustThree(Rect re, Object[][] data){
		
		switch (originalId) {
		
		case SwifteeApplication.TABINDEX_CERO:					
			if ( topId == 0 ) {					
				re.left = X;
				re.right = X + width;	
				SwifteeApplication.setTabSecondRect(re);
			}	
			if ( topId == 1 ) {				
				int d_x = (Integer) data[2][0];													
				re.left = X;
				re.right = d_x;
				SwifteeApplication.setTabSecondRect(re);
			}			
			if ( topId == 2 ) {				
				int d_x = (Integer) data[1][0];													
				re.left = X;
				re.right = d_x;
				SwifteeApplication.setTabCeroRect(re);
			}
			SwifteeApplication.setTab_0_Rect(re);
			break;
			
		case SwifteeApplication.TABINDEX_FIRST:					
			if (topId == 0) {						
				int d_x = (Integer) data[2][0];
				int d_w = (Integer) data[2][2];						
				re.left = d_x + d_w; 
				re.right = re.left + spacer;
				SwifteeApplication.setTabFirstRect(re);
			}
			if ( topId == 1 ) {					
				re.left = X;
				re.right = X + width;	
				SwifteeApplication.setTabCeroRect(re);
			}
			if ( topId == 2 ) {				
				int d_x = (Integer) data[1][0];			
				re.left = d_x;  
				re.right = d_x + spacer;
				SwifteeApplication.setTabFirstRect(re);
			}
			SwifteeApplication.setTab_1_Rect(re);
			break;
			
		case SwifteeApplication.TABINDEX_SECOND:					
			if (topId == 0) {					
				int d_x = (Integer) data[1][0];
				int d_w = (Integer) data[1][2];						
				re.left = d_x + d_w; 
				re.right = re.left + spacer;		
				SwifteeApplication.setTabCeroRect(re);
			}
			if (topId == 1) {					
				int d_x = (Integer) data[2][0];
				int d_w = (Integer) data[2][2];						
				re.left = d_x + d_w; 
				re.right = re.left + spacer;	
				SwifteeApplication.setTabFirstRect(re);
			}
			if ( topId == 2 ) {					
				re.left = X;
				re.right = X + width;			
				SwifteeApplication.setTabSecondRect(re);
			}
			SwifteeApplication.setTab_2_Rect(re);
			break;
		}	
			
	}
	
	private void adjustFour(Rect re, Object[][] data){
		
		switch (originalId) {
		
		case SwifteeApplication.TABINDEX_CERO:		
			
			if ( topId == 0 ) {					
				re.left = X;
				re.right = X + width;	
				SwifteeApplication.setTabThirdRect(re);
			}	
			if ( topId == 1 ) {				
				int d_x = (Integer) data[3][0];													
				re.left = X;
				re.right = d_x;
				SwifteeApplication.setTabThirdRect(re);
			}			
			if ( topId == 2 ) {				
				int d_x = (Integer) data[2][0];													
				re.left = X;
				re.right = d_x;	
				SwifteeApplication.setTabCeroRect(re);
			}
			
			if ( topId == 3 ) {
				int d_x = (Integer) data[0][0];			
				int d_x_1 = (Integer) data[1][0];
				re.left = d_x;
				re.right = d_x_1;		
				SwifteeApplication.setTabCeroRect(re);
			}
			
			SwifteeApplication.setTab_0_Rect(re);
			break;
			
		case SwifteeApplication.TABINDEX_FIRST:					
			if (topId == 0) {						
				int d_x = (Integer) data[3][0];
				int d_w = (Integer) data[3][2];						
				re.left = d_x + d_w; 
				re.right = re.left + spacer;		
				SwifteeApplication.setTabSecondRect(re);
			}
			if ( topId == 1 ) {					
				re.left = X;
				re.right = X + width;
				SwifteeApplication.setTabSecondRect(re);
			}
			if ( topId == 2 ) {				
				int d_x = (Integer) data[2][0];			
				re.left = d_x;  
				re.right = d_x + spacer;	
				SwifteeApplication.setTabThirdRect(re);
			}
			if ( topId == 3 ) {
				int d_x = (Integer) data[1][0];
				int d_x_2 = (Integer) data[2][0];		
				re.left = d_x;
				re.right = d_x_2;	
				SwifteeApplication.setTabFirstRect(re);
			}
			SwifteeApplication.setTab_1_Rect(re);
			break;
			
		case SwifteeApplication.TABINDEX_SECOND:					
			
			if (topId == 0) {					
				int d_x = (Integer) data[2][0];
				int d_w = (Integer) data[2][2];						
				re.left = d_x + d_w; 
				re.right = re.left + spacer;	
				SwifteeApplication.setTabFirstRect(re);
			}
			if (topId == 1) {					
				int d_x = (Integer) data[3][0];
				int d_w = (Integer) data[3][2];						
				re.left = d_x + d_w; 
				re.right = re.left + spacer;	
				SwifteeApplication.setTabCeroRect(re);
			}
			if ( topId == 2 ) {					
				re.left = X;
				re.right = X + width;	
				SwifteeApplication.setTabFirstRect(re);
			}
			if ( topId == 3 ) {
				int d_x = (Integer) data[2][0];			
				int d_x_3 = (Integer) data[3][0];
				re.left = d_x;
				re.right = d_x_3;		
				SwifteeApplication.setTabSecondRect(re);
			}
			SwifteeApplication.setTab_2_Rect(re);			
			break;
			
		case SwifteeApplication.TABINDEX_THIRD:					
			
			if ( topId == 0 || topId == 1 ) {					
				int d_x = (Integer) data[1][0];
				int d_w = (Integer) data[1][2];						
				re.left = d_x + d_w; 
				re.right = re.left + spacer;				
				if ( topId == 0 ){
					SwifteeApplication.setTabCeroRect(re);
				}				
				if ( topId == 1 ){
					SwifteeApplication.setTabFirstRect(re);
				}				
			}		
			
			if (topId == 2) {					
				int d_x = (Integer) data[3][0];
				int d_w = (Integer) data[3][2];						
				re.left = d_x + d_w; 
				re.right = re.left + spacer;	
				SwifteeApplication.setTabSecondRect(re);
			}
			
			if (topId == 3) {						
				re.left = X;
				re.right = X + width;			
				SwifteeApplication.setTabThirdRect(re);

			} 								
			SwifteeApplication.setTab_3_Rect(re);
			break;
		}	
			
	}
	
	//WITH ANN
	private void adjustFive(Rect re, Object[][] data) {
		
		switch (originalId) {
		
		case SwifteeApplication.TABINDEX_CERO:		
			
			if (topId == 0) {					
				re.left = X;
				re.right = X + width - edge;		
				SwifteeApplication.setTabFourthRect(re);
			}
			
			if (topId == 1  || topId == 2 || topId == 3 || topId == 4 ) {						
				re.left = X;
				re.right = X + spacer + (edge/2);	
				
				switch (topId) {
					case 1:
						SwifteeApplication.setTabFourthRect(re);
						break;
					case 2:
						SwifteeApplication.setTabCeroRect(re);						
						break;
					case 3:
						SwifteeApplication.setTabCeroRect(re);
						break;
					case 4:
						SwifteeApplication.setTabCeroRect(re);
						break;				
				}
			}
			Log.v("heavy", "TABINDEX_CERO re: "+re);
			SwifteeApplication.setTab_0_Rect(re);
			break;
			
		case SwifteeApplication.TABINDEX_FIRST:		
			
			if (topId == 0) {						
				int d_x = (Integer) data[4][0];
				int d_w = (Integer) data[4][2];						
				re.left = d_x + d_w - edge; 
				re.right = re.left + spacer;	
				SwifteeApplication.setTabThirdRect(re);
			} 					
			
			if (topId == 1) {						
				re.left = X + (edge/2);
				re.right = X + width - edge;	
				SwifteeApplication.setTabThirdRect(re);
			} 					
			
			if (topId == 2) {  				
				int d_x = (Integer) data[4][0];											
				re.left = X + (edge/2);  
				re.right = d_x + (edge/2);		
				SwifteeApplication.setTabFourthRect(re);				
			} 	
			
			if (topId == 3 || topId == 4) { 
				int d_x = (Integer) data[2][0];
				re.left = X + (edge/2);
				re.right = d_x + (edge/2);	
				
				if (topId == 3) { 
					SwifteeApplication.setTabFirstRect(re);
				}
				
				if (topId == 4) { 
					SwifteeApplication.setTabFirstRect(re);
				}
			} 			
			
			SwifteeApplication.setTab_1_Rect(re);
			Log.v("heavy", "TABINDEX_FIRST re: "+re);
			break;
			
		case SwifteeApplication.TABINDEX_SECOND:					
			
			if (topId == 0) {					
				int d_x = (Integer) data[3][0];
				int d_w = (Integer) data[3][2];						
				re.left = d_x + d_w - edge; 
				re.right = re.left + spacer;	
				SwifteeApplication.setTabSecondRect(re);
			} 		
			
			if (topId == 1) {						
				int d_x = (Integer) data[4][0];
				int d_w = (Integer) data[4][2];						
				re.left = d_x + d_w - edge; 
				re.right = re.left + spacer;		
				SwifteeApplication.setTabSecondRect(re);
			} 					
			
			if (topId == 2) {						
				re.left = X + (edge/2);
				re.right = X + width - edge;	
				SwifteeApplication.setTabThirdRect(re);				
			} 					
			
			if (topId == 3) {						
				int d_x = (Integer) data[4][0];					
				re.left = X + (edge/2);
				re.right = d_x + (edge/2);		
				SwifteeApplication.setTabSecondRect(re);
			}					
			
			if (topId == 4) {						
				int d_x = (Integer) data[3][0];						
				re.left = X + (edge/2);
				re.right = d_x + (edge/2);		
				SwifteeApplication.setTabSecondRect(re);
			}	
			
			SwifteeApplication.setTab_2_Rect(re);
			Log.v("heavy", "TABINDEX_SECOND re: "+re);
			break;
			
		case SwifteeApplication.TABINDEX_THIRD:		
			
			if (topId == 0 || topId == 1) {						
				int d_x = (Integer) data[2][0];
				int d_w = (Integer) data[2][2];						
				re.left = d_x + d_w - edge; 
				re.right = re.left + spacer;
				
				if (topId == 0) { 
					SwifteeApplication.setTabFirstRect(re);
				}
				
				if (topId == 1) { 
					SwifteeApplication.setTabCeroRect(re);
				}
			} 
			
			if (topId == 2 ) {						
				int d_x = (Integer) data[4][0];
				int d_w = (Integer) data[4][2];	
				re.left = d_x + d_w - edge; 
				re.right = re.left + spacer;
				SwifteeApplication.setTabFirstRect(re);				
			}	
			
			if (topId == 3) {						
				re.left = X + (edge/2);
				re.right = X + width - edge;	
				SwifteeApplication.setTabFourthRect(re);
			} 	
			
			if (topId == 4 ) {						
				int d_x_me = (Integer) data[3][0];				
				int d_x_next = (Integer) data[4][0];								
				re.left = d_x_me + (edge/2);
				re.right = d_x_next + (edge/2);	
				SwifteeApplication.setTabThirdRect(re);
			}		
			
			SwifteeApplication.setTab_3_Rect(re);
			Log.v("heavy", "TABINDEX_THIRD re: "+re);
			break;
			
		case SwifteeApplication.TABINDEX_FOURTH:
			
			if (topId == 0 || topId == 1) {						
				int d_x = (Integer) data[1][0];
				int d_w = (Integer) data[1][2];						
				re.left = d_x + d_w - edge; 
				re.right = X + width;	
				
				if (topId == 0) { 
					SwifteeApplication.setTabCeroRect(re);
				}
				
				if (topId == 1) { 
					SwifteeApplication.setTabFirstRect(re);
				}				
			} 	
			
			if (topId == 2 ) {						
				int d_x = (Integer) data[2][0];
				int d_w = (Integer) data[2][2];						
				re.left = d_x + d_w - edge; 
				re.right = X + width;
				SwifteeApplication.setTabSecondRect(re);				
			}	
			
			if (topId == 3) {						
				int d_x = (Integer) data[3][0];											
				re.left = d_x; 
				re.right = X + width;	
				SwifteeApplication.setTabThirdRect(re);
			} 
			
			if (topId == 4) {						
				re.left = X + (edge/2);
				re.right = X + width;					
				SwifteeApplication.setTabFourthRect(re);
			}		
			
			SwifteeApplication.setTab_4_Rect(re);
			Log.v("heavy", "TABINDEX_FOURTH re: "+re);
			break;
		}
		
		
		
	}
	
	private static RectF GetLeftUpper(int e) {
		RectF re = new RectF();
		re.left = X;
		re.top = Y;
		re.right = X + e;
		re.bottom = Y + e;
		return re;
	}

	private static RectF GetRightUpper(int e) {
		RectF re = new RectF();
		re.left = X + width - e;
		re.top = Y;
		re.right = X + width;
		re.bottom = Y + e;
		return re;
	}

	private static RectF GetRightLower(int e) {
		RectF re = new RectF();
		re.left = X + width - e;
		re.top = Y + height - e;
		re.right = X + width;
		re.bottom = Y + height;
		return re;
	}
	
	private static RectF GetRightLower_1(int e) {
		RectF re = new RectF();
		re.left = X;
		re.top = Y + height - e;
		re.right = e;
		re.bottom = Y + height;
		return re;
	}

	private static RectF GetLeftLower(int e) {
		RectF re = new RectF();
		re.left = X;
		re.top = Y + height - e;
		re.right = X + e;
		re.bottom = Y + height;
		return re;
	}
	
	 public Paint paintTab(boolean stroke) {    	
		 
		 	Paint p = new Paint();
			p.setAntiAlias(true);			
			
			if (stroke) {		
				
				p.setStyle(Style.STROKE);
				p.setStrokeWidth(1);	
				
				p.setColor(Color.rgb(r, g, b)); // fillColor
				
				/*int[] c = {r, b, g};  
				int[] colorArray = ColorUtils.D2_Color(c);
				int r = colorArray[0];
				int g = colorArray[1];
				int b = colorArray[2];   			 
				p.setColor(Color.rgb(r, g, b));*/
				
			} else {	
				
				p.setStyle(Paint.Style.FILL);	    		
	    		p.setColor(Color.rgb(r, g, b)); // fillColor    
	    		
			}
			
			return p;
		}
	 
	 public Paint paintText(int size) {
			
		 	Paint p = new Paint();
			p.setStyle(Paint.Style.FILL);
			p.setAntiAlias(true);
			
			/*int[] c = {r, b, g};  
			int[] colorArray = ColorUtils.L1_Color(c);
			int r = colorArray[0];
			int g = colorArray[1];
			int b = colorArray[2];   			 
			p.setColor(Color.rgb(r, g, b));*/		
			
			p.setColor(Color.WHITE);
			
			p.setTypeface(Typeface.DEFAULT_BOLD);
			p.setTextSize(size);
			
			return p;
	}
	
	public Paint paintButton(boolean stroke, int[] lineColor, int[] fillColor ){
		Paint p = new Paint();	
		if (stroke) {
			p.setStyle(Style.STROKE);			
			p.setStrokeWidth(2);
			int lL = lineColor[0];
			int gL = lineColor[1];
			int bL = lineColor[2];  
			p.setColor(Color.rgb(lL, gL, bL)); 	
		} else {
			p.setStyle(Paint.Style.FILL);
			p.setAntiAlias(true);		
			int rF = fillColor[0];
			int gF = fillColor[1];
			int bF = fillColor[2];  
			p.setColor(Color.rgb(rF, gF, bF)); 
		}
		return p;
	}	

	public int getTopId() {
		return topId;
	}

	public void setTopId(int topId) {
		this.topId = topId;
	}	
}
