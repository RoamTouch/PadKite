package com.roamtouch.view;

import com.roamtouch.swiftee.R;

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
	
	private final float BUTTON_DIP = 40; // 64dip=10mm, 96dip=15mm, 192dip=30mm expressed in DIP
	private final float ETEXT_DIP = 220; // 64dip=10mm, 96dip=15mm, 192dip=30mm expressed in DIP
	final float scale = getContext().getResources().getDisplayMetrics().density;
	final int buttonPX = (int) (BUTTON_DIP * scale + 0.5f); //Converting to Pixel
	final int etextPX = (int) (ETEXT_DIP * scale + 0.5f); //Converting to Pixel
	private int count = -1;
	
	private String prevStr;
	
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
		//buttonParams.set
		LayoutParams etextParams = new LayoutParams(etextPX,buttonPX);
		
		refreshOrBackButton=new Button(c);
		refreshOrBackButton.setId(0);
		refreshOrBackButton.setLayoutParams(buttonParams);
		refreshOrBackButton.setOnClickListener(this);
		refreshOrBackButton.setVisibility(GONE);
		refreshOrBackButton.setBackgroundResource(R.drawable.topbar_btn_active);
		
		mEditText=new EditText(c);
		mEditText.setLayoutParams(etextParams);
		mEditText.setLines(1);
		mEditText.setHint("");
		mEditText.setBackgroundResource(R.drawable.square_bg2);
		
		goOrNextButton=new Button(c);
		goOrNextButton.setId(1);
		goOrNextButton.setLayoutParams(buttonParams);
		goOrNextButton.setOnClickListener(this);
		goOrNextButton.setBackgroundResource(R.drawable.topbar_btn_active);
		
		addView(refreshOrBackButton);
		addView(mEditText);
		addView(goOrNextButton);
		
		setMode(TopBarArea.ADDR_BAR_MODE);
	}

	   public void setMode(int m){
		   mode=m;
		   if(mode==TopBarArea.SEARCH_BAR_MODE){
			   refreshOrBackButton.setText("<");
			   goOrNextButton.setText(">");
			   refreshOrBackButton.setVisibility(VISIBLE);
		   }
		   else if(mode==TopBarArea.ADDR_BAR_MODE){
			   refreshOrBackButton.setVisibility(GONE);
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
		if(!str.equals(prevStr))
			count = -1;
		prevStr = str;
		switch(id){
			case 0:{
				 if(mode==ADDR_BAR_MODE){
						mWebView.reload();	
						mWebView.findAll("");
				 }
				 else{
					 if(count != -1){
						 mWebView.findNext(false);
					 }
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
					 if(count == -1)
						 count = mWebView.findAll(str);
					 else{
						 mWebView.findNext(true);
					 }
				 }
				 break;
			}
		}
		
	}

	public void setWebView(WebView mWebView) {
		this.mWebView = mWebView;
	}
	
	public void setURL(String url)
	{
		mEditText.setText(url);	
	}
}
