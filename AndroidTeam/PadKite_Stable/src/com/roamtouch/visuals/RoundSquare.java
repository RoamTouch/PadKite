package com.roamtouch.visuals;

import android.graphics.Path;
import android.graphics.RectF;

public class RoundSquare extends Path {
	
	static int X;
	static int Y;
	static int width;
    static int height;
    static int edge;
    static int corners;
    static int stretch;
	
	public static Path getRoundedPath(int[] coords, int[] layout){		
		
		X = coords[0];
		Y = coords[1];           
		width = coords[2];
		height = coords[3];
		edge = coords[4];

		corners = layout[0];
		stretch = layout[1];
		
		Path p = drawTabRect();
		return p;
	}

	private static Path drawTabRect(){
		
    	Path path = new Path();   	
    	
    	switch (corners){
    	
    		case 1: //Top corners + stretch
    			
    			path.moveTo(X, Y+edge+stretch);
    			
    			path.moveTo(X, Y+edge);
    			path.arcTo(new RectF(GetLeftUpper(edge)),180,90);   
    			
    			path.lineTo(X+width-edge, Y);
    	    	path.arcTo(GetRightUpper(edge), 270, 90);
    	    	
    	    	path.lineTo(X+width, Y+edge+stretch);
    	    	path.lineTo(X, Y+edge+stretch);
    	    	    			
    			break;
    			
    		case 2:
    			break;
    		case 3:
    			break;
    		case 4:
    		
    			path.moveTo( X+edge, Y);    			
    			
    	    	path.lineTo( X+width-edge, Y);
    	    	path.arcTo(GetRightUpper(edge), 270, 90);  		
    	    
    	    	path.lineTo( X+width, Y+height-edge);
    	    	path.arcTo(GetRightLower(edge), 0, 90);    	
    	    
    	    	path.lineTo(X+edge, Y+height);   	   	
    	    	path.arcTo(GetLeftLower(edge), 90, 90);	
    	    
    	    	path.lineTo(X, Y + edge);
    	    	path.arcTo(new RectF(GetLeftUpper(edge)),180,90);   
    	    
    			break;    	
    	}
    	
	return path;
    }
    
    private static RectF GetLeftUpper(int e) {
    	RectF re = new RectF();
    	re.left=X; 	re.top=Y; re.right= X+e; re.bottom= Y+e;    	
    	return re;
    }
    
    private static RectF GetRightUpper(int e) {
    	RectF re = new RectF();
    	re.left=X+width-e;	re.top=Y; re.right=X+width; re.bottom=Y+e;   	
    	return re;
    }
 
    private static RectF GetRightLower(int e) {
    	RectF re = new RectF();
    	re.left=X+width-e; re.top=Y+height-e; re.right=X+width; re.bottom=Y+height;
    	return re;
    }
    
    private static RectF GetLeftLower(int e) {
    	RectF re = new RectF();
    	re.left=X ;  re.top=Y+height-e;	re.right=X+e; re.bottom=Y+height;
    	return re;
    }
    
    
    
   

}
