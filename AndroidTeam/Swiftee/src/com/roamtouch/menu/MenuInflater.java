package com.roamtouch.menu;

import java.io.FileInputStream;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import android.content.Context;
import android.view.ViewGroup;


public class MenuInflater {
	
	public static String PATH = "/sdcard/Swiftee/Default Theme/";
	public static String hotkey_function,hotkey_image,hotkey_highlight;
	public static void inflate(String xmlfile,Context context, ViewGroup view) {
		
		try{
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		FileInputStream is = new FileInputStream(xmlfile); 

		parser.setInput(is,null);
		
		while(true){
			int eventType = parser.getEventType();
			
			if(eventType == XmlPullParser.START_TAG) {
				String tag = parser.getName();
				if(tag.equals("MenuButton")){
					MenuButton b = new MenuButton(context);	
					
					HashMap<String,String> attrs = new HashMap<String,String>();
					
					for (int i = 0; i < parser.getAttributeCount(); i++)
						attrs.put(parser.getAttributeName(i), parser.getAttributeValue(i));

					b.setDrawables(PATH+attrs.get("button_image"),PATH+attrs.get("button_highlightImage"));
					
					b.setFunction(attrs.get("button_function"));
					
					String s = attrs.get("hotkey");
					
					if(s != null){
						if(s.equals("true")){
							b.setHotkey(true);
							hotkey_function = attrs.get("button_function");
							hotkey_image = PATH+attrs.get("button_image");
							hotkey_highlight = PATH+attrs.get("button_highlightImage");
						}
						else
							view.addView(b);
					}
					else
						view.addView(b);	
					
				}	
			}
			parser.nextTag();
			
		}

	}
		
	catch(Exception e){
		e.printStackTrace();
	}
	}
	
}
