package com.roamtouch.settings;

import java.io.File;
import com.roamtouch.swiftee.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BrowserSettingActivity extends Activity implements OnItemClickListener {

	private ListView mList;
	Context context;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		this.context = getApplicationContext();
		mList = (ListView)findViewById(R.id.mList);
		mList.setAdapter(new ImageAdapter(context, 
				new String[]{"Clear all Data" , "Restore to default"}));
		mList.setOnItemClickListener(this);

	}

	public class ImageAdapter extends BaseAdapter {
		 
		private LayoutInflater mInflater;
	    
	     String []DATA ;
	     int []FLAG;
	     Context context;

	     public ImageAdapter(Context context , String[] data) 
		 {
	        this.context =context;
	    	DATA =new String[data.length];
	    	DATA =data;
	    	
	    	mInflater = LayoutInflater.from(context);
	    }

	    public int getCount() 
	    {
	        return 1;
	    }


	     public Object getItem(int position) 
	  	 {
	         return position;
	     }

	     public long getItemId(int position) {
	         return position;
	     }
	     
	     public View getView(int position, View convertView, ViewGroup parent) {
	    	 ViewHolder holder;
	    	 if (convertView == null) 
	    	 {
	    		 convertView = mInflater.inflate(R.layout.text_icon_1, null);
	    		 holder = new ViewHolder();
	    		 holder.text = (TextView) convertView.findViewById(R.id.text);
	    		 holder.icon = (ImageView) convertView.findViewById(R.id.icon);
	    		 convertView.setTag(holder);
	    	 } 
	    	 else 
	    		 holder = (ViewHolder) convertView.getTag();

	    	 holder.text.setText(DATA[position]);
	    	 holder.icon.setImageResource(R.drawable.ic_menu_more);
	    	 return convertView;
	     }

	     class ViewHolder 
	     {
	    	 TextView text;
	    	 ImageView icon;
	     }
	}

	
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		System.out.println("Position ::: "+pos);
		switch (pos) {
		case 0:
			new AlertDialog.Builder(this).setTitle("Clear")
			.setMessage("Clear all data ?")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							File dir = context.getCacheDir();
							System.out.println(dir.getPath());
							if(dir!= null && dir.isDirectory()){
					            try{
					            File[] children = dir.listFiles();
					            if (children.length >0) {
					                for (int i = 0; i < children.length; i++) {
					                    File[] temp = children[i].listFiles();
					                    for(int x = 0; x<temp.length; x++)
					                    {
					                        temp[x].delete();
					                    }
					                }
					            }
					            }catch(Exception e)
					            {
					                Log.e("Cache", "failed cache clean");
					            }
					        }

							ContentResolver cr = context.getContentResolver();
							if(Browser.canClearHistory(cr))
								Browser.clearHistory(cr);
							
						    Browser.clearSearches(cr);
						    
//						    CookieManager.getInstance().removeAllCookie();

//						    mWebView.clearHistory();
//						    mWebView.clearFormData();
//						    mWebView.clearCache(true);
						    
						}
			})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.show();
			break;
		}
		
	}



}