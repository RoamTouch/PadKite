package com.roamtouch.utils;

import java.util.Vector;

import android.graphics.Rect;
import android.util.Log;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.BrowserActivity;

public class ScreenLocation {
	
	static BrowserActivity bA; 
	static FloatingCursor fC;
	private static Vector<Object> cuagrant34;
	
	public ScreenLocation(){		
	}
	
	public void setParent(BrowserActivity bA, FloatingCursor fC){
		this.bA = bA;
		this.fC = fC;
	}

	public static int getVerticalLocation(){
		
		Object[] obj = new Object[2];
		
		obj = (Object[]) getXY();
		int x = (Integer) obj[0];
		int y = (Integer) obj[1];
		
		cuagrant34 = bA.getCuadrant34(x, y);	
		
  	 	int vertical = (Integer) cuagrant34.get(0);
  	 	
  	 	Log.v("location","vertical: "+vertical);
  	 	
		return vertical;
	}
	
	public static int getHorizontalLocation(){

		Object[] obj = new Object[2];
		
		obj = (Object[]) getXY();
		int x = (Integer) obj[0];
		int y = (Integer) obj[1];
		
		cuagrant34 = bA.getCuadrant34(x, y);	
		
		int horizontal = (Integer) cuagrant34.get(1);	
		
		Log.v("location","horizontal: "+horizontal);
		
		return horizontal; 
	}
	
	public static Rect getRectCuardrant(){
		Rect xRect =  (Rect) cuagrant34.get(2);
		return xRect;
	}
	
	public static int getTipType(){
		int tipType =  (Integer) cuagrant34.get(3);
		return tipType;
	}
	
	private static Object getXY(){
		
		Object[] obj = new Object[2];
		
		int xCord = 0;
		int yCord = 0;
		
		if (fC.isFromFC()){			
			
			if (!fC.isAnchorTab()){
				
				xCord = fC.getAnX();
				yCord = fC.getAnY();
				
			} else {
				
				xCord = fC.getFcX();
				yCord = fC.getFcY();
				
			}
			
		} else {
			
			if (!fC.isAnchorTab()){
				
				xCord = fC.getAnX();
				yCord = fC.getAnY();
				
			} else {
				
				xCord = fC.getStX();
				yCord = fC.getStY();
				
			}
		}
		
		obj[0] = xCord;
		obj[1] = yCord;
		
		return obj;
	}
	
}
