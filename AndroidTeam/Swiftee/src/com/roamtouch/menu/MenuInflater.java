package com.roamtouch.menu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.content.Context;
import android.view.ViewGroup;


public class MenuInflater {
	
	private static String PATH = "/sdcard/Swiftee/Default Theme/";

	
	public static void inflate(String xmlfile,Context context, ViewGroup view) {	
		try{
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		FileInputStream is = new FileInputStream(xmlfile); 

		parser.setInput(is,null);
		
		while(true){
			int eventType = parser.getEventType();
			if(eventType == XmlPullParser.END_DOCUMENT) {
				break;
			}
			if(eventType == XmlPullParser.START_TAG) {
				String tag = parser.getName();
				if(tag.equals("MenuButton")){
	//				System.out.println("attr count"+parser.getAttributeCount()+","+parser.getAttributeValue(0)+","+parser.getAttributeValue(1));
					MenuButton b = new MenuButton(context);	
					
					HashMap<String,String> attrs = new HashMap<String,String>();
					
					for (int i = 0; i < parser.getAttributeCount(); i++)
						attrs.put(parser.getAttributeName(i), parser.getAttributeValue(i));

					b.setDrawables(PATH+attrs.get("button_image"),PATH+attrs.get("button_highlightImage"));
					
					b.setFunction(attrs.get("button_function"));
					
					view.addView(b);	
				}	
			}
			parser.nextTag();
		}
		
	}
		
	catch(Exception e){
		
	}
	}
}
