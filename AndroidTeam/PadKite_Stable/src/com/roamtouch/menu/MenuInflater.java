package com.roamtouch.menu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.roamtouch.swiftee.BrowserActivity;

import android.content.Context;
import android.view.ViewGroup;


public class MenuInflater {
	
	public static String PATH = BrowserActivity.THEME_PATH + "/";
	
	//public static String hotkey_function,hotkey_image,hotkey_highlight;	
	
	public static String hotkey_function_main,hotkey_image_main,hotkey_highlight_main;
	
	public static String hotkey_function_settings,hotkey_image_settings,hotkey_highlight_settings;
	
	public static void inflate(String xmlfile,Context context, ViewGroup view) {
		
		try {
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
	
			FileInputStream is = new FileInputStream(xmlfile); 
	
			parser.setInput(is,null);
		    final int depth = parser.getDepth();
			int eventType; 
			while (((eventType = parser.next()) != XmlPullParser.END_TAG ||
	                parser.getDepth() > depth) && eventType != XmlPullParser.END_DOCUMENT) {
				
				if(eventType == XmlPullParser.START_TAG) {
					String tag = parser.getName();
					if(tag.equals("MenuButton")){
						MenuButton b = new MenuButton(context);	
						
						HashMap<String,String> attrs = new HashMap<String,String>();
						
						for (int i = 0; i < parser.getAttributeCount(); i++)
							attrs.put(parser.getAttributeName(i), parser.getAttributeValue(i));
	
						b.setDrawables(PATH+attrs.get("button_image"),PATH+attrs.get("button_highlightImage"));
						
						b.setFunction(attrs.get("button_function"));
						b.setPolicy(attrs.get("button_policy"));
						
						String s = attrs.get("button_disabled");
						if(s != null){
							b.setDisabled(PATH+s);
						}
						
						s = attrs.get("hotkey");
						
						if(s != null){
							if(s.equals("true")){
								b.setHotkey(true);
								
								if (xmlfile.equals(PATH + "main_menu.xml")){
									
									hotkey_function_main 	= attrs.get("button_function");
									hotkey_image_main 		= PATH+attrs.get("button_image");
									hotkey_highlight_main 	= PATH+attrs.get("button_highlightImage");
									
								} else if (xmlfile.equals( PATH + "settings_menu.xml")){
									
									hotkey_function_settings 	= attrs.get("button_function");
									hotkey_image_settings 		= PATH+attrs.get("button_image");
									hotkey_highlight_settings 	= PATH+attrs.get("button_highlightImage");
									
								}					
								
							}
							else
								view.addView(b);
						}
						else
							view.addView(b);	
						
					}	
				}
				//	parser.nextTag();
			
			}

	}
		
	catch(XmlPullParserException e){
		e.printStackTrace();
	} 
	catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	catch (IOException e) {
		e.printStackTrace();
	}
	}
	
}
