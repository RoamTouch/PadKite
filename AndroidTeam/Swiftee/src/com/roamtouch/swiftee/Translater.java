package com.roamtouch.swiftee;

import com.google.api.GoogleAPI;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class Translater {

	public static String text(String text, String fromLanguage, String toLanguage){
		
	    try {
	    	GoogleAPI.setHttpReferrer("http://code.google.com/p/google-api-translate-java/");
			String translatedText = Translate.execute(text, Language.fromString(fromLanguage), Language.fromString(toLanguage));
			return translatedText;	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
