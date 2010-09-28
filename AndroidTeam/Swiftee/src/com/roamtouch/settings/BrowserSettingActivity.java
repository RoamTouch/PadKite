package com.roamtouch.settings;

import com.roamtouch.swiftee.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BrowserSettingActivity extends Activity implements OnItemClickListener {

	private ListView mList;

	/** Called when the activity is first created. */
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.list);
		mList = (ListView)findViewById(R.id.mList);
		mList.setAdapter(new Cust_ListAdapter());
		mList.setOnItemClickListener(this);

	}

	
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		System.out.println("position :::: "+pos);
		switch (pos) {
		case 1:
			final Dialog d = new Dialog(BrowserSettingActivity.this);
			d.setTitle("Text size");
			d.setCancelable(true);
			d.setContentView(R.layout.multi_select_list);
			d.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			Window w = d.getWindow();
			ListView list = (ListView) w.findViewById(R.id.multipleSelectList);
			String[] str_arr = {"Tiny","Small","Normal","Large","Huge"};
			//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,str_arr); 
			ImageAdapter adapter = new ImageAdapter(this,str_arr,1); 
			list.setAdapter(adapter);
			list.setItemsCanFocus(true);
			list.setOnItemClickListener(new OnItemClickListener() {
				
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					
				}
			});
			Button btn_cancel = (Button) w.findViewById(R.id.btn_Cancel_multi);
			btn_cancel.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					d.dismiss();
				}
			});
			d.show();
			break;
		case 2:
			final Dialog d1 = new Dialog(BrowserSettingActivity.this);
			d1.setTitle("Text size");
			d1.setCancelable(true);
			d1.setContentView(R.layout.multi_select_list);
			d1.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			Window w1 = d1.getWindow();
			ListView list1 = (ListView) w1.findViewById(R.id.multipleSelectList);
			String[] str_arr1 = {"Far","Medium","Close"};
			//ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,str_arr1); 
			ImageAdapter adapter1 = new ImageAdapter(this,str_arr1,1); 
			list1.setAdapter(adapter1);
			list1.setItemsCanFocus(true);
			list1.setOnItemClickListener(new OnItemClickListener() {
				
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					
				}
			});
			Button btn_cancel1 = (Button) w1.findViewById(R.id.btn_Cancel_multi);
			btn_cancel1.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					d1.dismiss();
				}
			});
			d1.show();
			break;
		case 4:
			final Dialog d2 = new Dialog(BrowserSettingActivity.this);
			d2.setTitle("Text size");
			d2.setCancelable(true);
			d2.setContentView(R.layout.multi_select_list);
			d2.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			Window w2 = d2.getWindow();
			ListView list2 = (ListView) w2.findViewById(R.id.multipleSelectList);
			String[] str_arr2 = {"Latin-1 (ISO-8859-1)","Unicode (UTF-8)","Japanese (ISO-2022-JP)",
					"Japanese (SHIFT-JIS)","Japanese (EUC-JP)"};
			//ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,str_arr2); 
			ImageAdapter adapter2 = new ImageAdapter(this,str_arr2,1); 
			list2.setAdapter(adapter2);
			list2.setItemsCanFocus(true);
			list2.setOnItemClickListener(new OnItemClickListener() {
				
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					
				}
			});
			Button btn_cancel2 = (Button) w2.findViewById(R.id.btn_Cancel_multi);
			btn_cancel2.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					d2.dismiss();
				}
			});
			d2.show();
			break;
		case 12:
			final Dialog d3 = new Dialog(BrowserSettingActivity.this);
			d3.setTitle("Set home page");
			d3.setCancelable(true);
			d3.setContentView(R.layout.enter_homepage);
			d3.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			Window w3 = d3.getWindow();
			EditText edt_mail = (EditText) w3.findViewById(R.id.edt_mail);
			Button btn_ok = (Button) w3.findViewById(R.id.btn_Ok);
			btn_ok.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					
				}
			});
			Button btn_cancel3 = (Button) w3.findViewById(R.id.btn_Cancel);
			btn_cancel3.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					d3.dismiss();
				}
			});
			
			d3.show();
			break;
		case 14:
			new AlertDialog.Builder(this).setTitle("Clear")
			.setMessage("Locally cached contents and databases will be deleted.")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.show();
			break;
		case 15:
			new AlertDialog.Builder(this).setTitle("Clear")
			.setMessage("Browser navigation history will be deleted.")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.show();
			break;
		case 17:
			new AlertDialog.Builder(this).setTitle("Clear")
			.setMessage("All cookies will be deleted.")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.show();
			break;
		case 19:
			new AlertDialog.Builder(this).setTitle("Clear")
			.setMessage("All saved form data will be deleted.")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.show();
			break;
		case 21:
			new AlertDialog.Builder(this).setTitle("Clear")
			.setMessage("Clear location access for all websites.")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.show();
			break;
		case 24:
			new AlertDialog.Builder(this).setTitle("Clear")
			.setMessage("All saved passwords will be deleted.")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
			})
			.show();
			break;
		case 28:
			new AlertDialog.Builder(this).setTitle("Reset to Default")
					.setMessage("All browser data will be deleted and settings will be revert to default values.")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									
								}
					})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									
								}
					})
					.show();
			break;
		default:
			break;
		}

	}

	public class HeadingView extends LinearLayout{

		public HeadingView(Context context) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.heading_text, this);		
		}

		public void setHeading(String str){
			TextView txt_view = (TextView) findViewById(R.id.heading_txt);
			txt_view.setText(str);
		}
	}

	
	
	public class Cust_ListAdapter extends BaseAdapter{

		public int getCount() {
			return 29;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if(position == 0){
				HeadingView head = new HeadingView(BrowserSettingActivity.this);
				head.setHeading("Page Content Settings");
				return head; 
			}
			else if(position == 13){
				HeadingView head = new HeadingView(BrowserSettingActivity.this);
				head.setHeading("Privacy Settings");
				return head; 
			}
			else if(position == 22){
				HeadingView head = new HeadingView(BrowserSettingActivity.this);
				head.setHeading("Security Settings");
				return head; 
			}
			else if(position == 26){
				HeadingView head = new HeadingView(BrowserSettingActivity.this);
				head.setHeading("Advanced Settings");
				return head; 
			}
			else if(position > 0 && position < 13){
				MiscView_1 miscView = new MiscView_1(BrowserSettingActivity.this,position);
				return miscView;
			}
			else if(position > 13 && position < 22){
				MiscView_2 miscView = new MiscView_2(BrowserSettingActivity.this,position);
				return miscView;
			}
			else if(position > 22 && position < 26){
				MiscView_3 miscView = new MiscView_3(BrowserSettingActivity.this,position);
				return miscView;
			}
			else if(position > 26 && position <= 28){
				MiscView_4 miscView = new MiscView_4(BrowserSettingActivity.this,position);
				return miscView;
			}

			return null;
		}

		
	}

	public class MiscView_1 extends LinearLayout{
		String arr_lrg[] = {"Text Size","Default Zoom","Open pages in overview",
				"Text Encoding","Block Popup Window","Load Images","Auto-fit pages",
				"Land-scape only Display","Enable JavaScript","Enable Plugins",
				"Open in Background","Set home page"};
		String arr_sml[] = {"Normal","Medium","Show overview of newly opened page",
				"aaa","bbb","Display images on webpages","Formate pages to fit the screen",
				"Display pages only in wider landscape screen orientation","aaa","aaa",
				"Open new windows behind current one","aaaaa"};

		public MiscView_1(Context context,int position) {
			super(context);

			LayoutInflater.from(context).inflate(R.layout.list_adpt, this);

			TextView tv_lrg= (TextView) findViewById(R.id.tv_lrg);
			TextView tv_sml= (TextView) findViewById(R.id.tv_sml);

			tv_lrg.setText(arr_lrg[position-1]);
			tv_sml.setText(arr_sml[position-1]);

			if(position == 1 || position == 2 ||position == 4 || position == 12){
				CheckBox cb= (CheckBox) findViewById(R.id.checkbox);
				cb.setVisibility(GONE);

				ImageView image = (ImageView)findViewById(R.id.image);
				image.setVisibility(VISIBLE);
			}
		}
	}

	public class MiscView_2 extends LinearLayout{
		String arr_lrg[] = {"Clear Cache","Clear history","Accept cookies",
				"Clear all cookie data","Remember from data","Clear from data",
				"Enable Location","Clear location access"};
		String arr_sml[] = {"Clear locally cached content and databases",
				"Clear browser navigation history",
				"Allow sites to save and read \"cookies\" data",
				"Clear all browser cookies",
				"Remember data I type in forms for later use",
				"Clear all the form data","Allow sites to request access to your location",
		"Clear location access for all websites"};

		public MiscView_2(Context context,int position) {
			super(context);

			LayoutInflater.from(context).inflate(R.layout.list_adpt, this);

			TextView tv_lrg= (TextView) findViewById(R.id.tv_lrg);
			TextView tv_sml= (TextView) findViewById(R.id.tv_sml);

			tv_lrg.setText(arr_lrg[position-14]);
			tv_sml.setText(arr_sml[position-14]);

			if(position == 14 || position == 15 ||position == 17 || position == 19
					|| position == 21){
				CheckBox cb= (CheckBox) findViewById(R.id.checkbox);
				cb.setVisibility(GONE);

				ImageView image = (ImageView)findViewById(R.id.image);
				image.setVisibility(VISIBLE);
			}

		}
	}

	public class MiscView_3 extends LinearLayout{
		String arr_lrg[] = {"Remember Passwords","Clear Passwords","Show Security Warnings"};
		String arr_sml[] = {"Save usernames and passwords for websites",
				"Clear all saved passwords",
		"Show warnings if there is a problem with a site's security"};

		public MiscView_3(Context context,int position) {
			super(context);

			LayoutInflater.from(context).inflate(R.layout.list_adpt, this);

			TextView tv_lrg= (TextView) findViewById(R.id.tv_lrg);
			TextView tv_sml= (TextView) findViewById(R.id.tv_sml);

			tv_lrg.setText(arr_lrg[position-23]);
			tv_sml.setText(arr_sml[position-23]);

			if(position == 24){
				CheckBox cb= (CheckBox) findViewById(R.id.checkbox);
				cb.setVisibility(GONE);

				ImageView image = (ImageView)findViewById(R.id.image);
				image.setVisibility(VISIBLE);
			}

		}
	}

	public class MiscView_4 extends LinearLayout{
		String arr_lrg[] = {"Website Settings","Reset to default"};
		String arr_sml[] = {"View advanced settings for individual websites",
		"Clear all browser data and reset all settings to default"};

		public MiscView_4(Context context,int position) {
			super(context);

			LayoutInflater.from(context).inflate(R.layout.list_adpt, this);

			TextView tv_lrg= (TextView) findViewById(R.id.tv_lrg);
			TextView tv_sml= (TextView) findViewById(R.id.tv_sml);

			tv_lrg.setText(arr_lrg[position-27]);
			tv_sml.setText(arr_sml[position-27]);

			if(position == 28){
				CheckBox cb= (CheckBox) findViewById(R.id.checkbox);
				cb.setVisibility(GONE);

				ImageView image = (ImageView)findViewById(R.id.image);
				image.setVisibility(VISIBLE);
			}

		}
	}


	public class ImageAdapter extends BaseAdapter {
		 
		private LayoutInflater mInflater;
	    
	     String []DATA ;
	     int []FLAG;
	     int selected;
	     Context context;

	     public ImageAdapter(Context context , String[] data, int selected) 
		 {
	        this.context =context;
	    	DATA =new String[data.length];
	    	DATA =data;
	    	this.selected = selected;
	    	mInflater = LayoutInflater.from(context);
	    }

	    public int getCount() 
	    {
	        return DATA.length;
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
	    		 convertView = mInflater.inflate(R.layout.text_icon, null);
	    		 holder = new ViewHolder();
	    		 holder.text = (TextView) convertView.findViewById(R.id.text);
	    		 holder.icon = (ImageView) convertView.findViewById(R.id.icon);
	    		 convertView.setTag(holder);
	    	 } 
	    	 else 
	    		 holder = (ViewHolder) convertView.getTag();

	    	 holder.text.setText(DATA[position]);
	    	 if(position == selected){
	    		 holder.icon.setImageResource(R.drawable.btn_radio_on);
	    	 }
	    	 else{
	    		 holder.icon.setImageResource(R.drawable.btn_radio_off);
	    	 }
	    	 return convertView;
	     }

	     class ViewHolder 
	     {
	    	 TextView text;
	    	 ImageView icon;
	     }
	}



}