package com.roamtouch.swiftee;

import com.google.api.GoogleAPI;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class Translater {

	public static String text(String text, String fromLanguage, String toLanguage){
		
	    try {
	    	GoogleAPI.setHttpReferrer("http://code.google.com/p/google-api-translate-java/");

	    	Language to = Language.fromString(toLanguage);
	    	
	    	if (to == null)
	    	{
	    		to = Language.ENGLISH;

	    		if(toLanguage.equals("RUSSIAN"))
	    			to = Language.RUSSIAN;
	    		else if(toLanguage.equals("FRENCH"))
	    			to = Language.FRENCH;
	    		else if(toLanguage.equals("GERMAN"))
	    			to = Language.GERMAN;
	    		else if(toLanguage.equals("SPANISH"))
	    			to = Language.SPANISH;
	    		else if(toLanguage.equals("HINDI"))
	    			to = Language.HINDI;
	    		else if(toLanguage.equals("CHINESE"))
	    			to = Language.CHINESE;
	    	}
	 
	    	
			String translatedText = Translate.execute(text, Language.ENGLISH, to);
			return translatedText;	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
