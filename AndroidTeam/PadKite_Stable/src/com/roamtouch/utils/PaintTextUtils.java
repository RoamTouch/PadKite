	package com.roamtouch.utils;

import android.graphics.Paint;

public class PaintTextUtils {
	
	public static String CalculateStringFit(String text, int rowWidth, Paint textPaint){    	
    	String finalString = null;
    	int textSize = (int) textPaint.measureText(text);
    	int stringLength = ( rowWidth * text.length() ) / textSize;
    	if (textSize > rowWidth){    	
    		if (stringLength>5){  			
    			finalString = text.substring(0, (stringLength-5) );
        		finalString += " ...";	
    		} else if (stringLength<=5){
    			finalString = " ...";
    		}    		
    	} else {
    		finalString = text;
    	}
    	
    	return finalString;
    }

	public static int CalculateArrayMaxTexts(Paint textPaint, String[][] array){			
		int biggest = 0;		
		for (int i=0; i<array.length; i++){
			String title = (String) array[i][0];
			if (title!=null){
				int textSize = (int) textPaint.measureText(title);
				if (textSize > biggest){
					biggest = textSize;
				}
			}
		}		
		return biggest;
	}
	
}
