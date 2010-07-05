package com.roamtouch.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import roamtouch.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class TopBarArea extends LinearLayout implements OnClickListener{
	
	private final float BUTTON_DIP = 50; // 64dip=10mm, 96dip=15mm, 192dip=30mm expressed in DIP
	private final float ETEXT_DIP = 220; // 64dip=10mm, 96dip=15mm, 192dip=30mm expressed in DIP
	final float scale = getContext().getResources().getDisplayMetrics().density;
	final int buttonPX = (int) (BUTTON_DIP * scale + 0.5f); //Converting to Pixel
	final int etextPX = (int) (ETEXT_DIP * scale + 0.5f); //Converting to Pixel

	private Button refreshOrBackButton,goOrNextButton;
		
	private EditText mEditText;
	
	private int mode=ADDR_BAR_MODE;
	/*
	 * TopBarArea is used as both address bar and search bar
	 * these two variables decides whether it is addr bar or search bar 
	 */
	public static int ADDR_BAR_MODE=0;
	public static int SEARCH_BAR_MODE=1;
	
	private WebView mWebView;
	
	public TopBarArea(Context c,AttributeSet attrs){
		super(c, attrs);
		
		LayoutParams buttonParams = new LayoutParams(buttonPX,buttonPX);
		LayoutParams etextParams = new LayoutParams(etextPX,buttonPX);
		
		refreshOrBackButton=new Button(c);
		refreshOrBackButton.setId(0);
		refreshOrBackButton.setLayoutParams(buttonParams);
		refreshOrBackButton.setOnClickListener(this);
		
		mEditText=new EditText(c);
		mEditText.setLayoutParams(etextParams);
		mEditText.setLines(1);
		
		goOrNextButton=new Button(c);
		goOrNextButton.setId(1);
		goOrNextButton.setLayoutParams(buttonParams);
		goOrNextButton.setOnClickListener(this);

		
		addView(refreshOrBackButton);
		addView(mEditText);
		addView(goOrNextButton);
		
	}

	   public void setMode(int m){
		   mode=m;
		   if(mode==TopBarArea.SEARCH_BAR_MODE){
			   refreshOrBackButton.setText("<");
			   goOrNextButton.setText(">");
		   }
		   else if(mode==TopBarArea.ADDR_BAR_MODE){
			   refreshOrBackButton.setText("R");
			   goOrNextButton.setText("Go");
		   }
	   }
	public int getMode(){
		return mode;		
	}

	public void onClick(View v) {
		
		InputMethodManager imm = (InputMethodManager)((Activity)getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
		
		int id = v.getId();
		String str=mEditText.getText().toString();
		
		switch(id){
			case 0:{
				 if(mode==ADDR_BAR_MODE){
						mWebView.reload();						
				 }
				 else{
					
				 }
			     break;
			}
			case 1:{
				 if(mode==ADDR_BAR_MODE){
					 if(!str.startsWith("http://"))
							str="http://"+str;
					 mWebView.loadUrl(str);
				 }
				 else{
					 mWebView.findAll(str);
					 mWebView.findNext(true);
				 }
				 break;
			}
		}
		
	}

	public void setWebView(WebView mWebView) {
		this.mWebView = mWebView;
	}

}
