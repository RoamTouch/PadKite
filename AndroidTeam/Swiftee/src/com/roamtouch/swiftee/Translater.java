package com.roamtouch.swiftee;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class Translater {

	public static String text(String text, String fromLanguage, String toLanguage){
		// Set the HTTP referrer to your website address.
	    //Translate.setHttpReferrer(/* Enter the URL of your site here */);

		//Language.fromString(pLanguage)
	    try {
			String translatedText = Translate.execute("Bonjour le monde", Language.ENGLISH, Language.ITALIAN);
			return translatedText;	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
