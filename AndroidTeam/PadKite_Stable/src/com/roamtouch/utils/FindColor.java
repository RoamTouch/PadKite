package com.roamtouch.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

public class FindColor {

	/**
     * Find components of color of the bitmap at x, y. 
     * @param x Distance from left border of the View
     * @param y Distance from top of the View
     * @param view Touched surface on screen
     */
	public static int Find(View view, int x, int y) 
	
	  throws NullPointerException {
	    
	    int red = 0;
	      int green = 0;
	      int blue = 0;
	      int color = 0;
	      
	      int offset = 1; // 3x3 Matrix
	      int pixelsNumber = 0;
	      
	      int xImage = 0;
	      int yImage = 0;
	      
	      // Get the bitmap from the view.
	      ImageView imageView = (ImageView)view;
	      BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
	      Bitmap imageBitmap = bitmapDrawable.getBitmap();
	
	        // Calculate the target in the bitmap.
	      xImage = (int)(x * ((double)imageBitmap.getWidth() / (double)imageView.getWidth()));
	      yImage = (int)(y * ((double)imageBitmap.getHeight() / (double)imageView.getHeight()));
	      
	        // Average of pixels color around the center of the touch.
	      for (int i = xImage - offset; i <= xImage + offset; i++) {
	        for (int j = yImage - offset; j <= yImage + offset; j++) {
	          try {
	              color = imageBitmap.getPixel(i, j);
	              red += Color.red(color);
	              green += Color.green(color);
	              blue += Color.blue(color);
	              pixelsNumber += 1;
	            } catch(Exception e) {
	              //Log.w(TAG, "Error picking color!");
	            }  
	        }
	      }
	      red = red / pixelsNumber;
	      green = green / pixelsNumber;
	      blue = blue / pixelsNumber;
	      
	      return Color.rgb(red, green, blue); 
	  }
	  
	
}
