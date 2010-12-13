package com.roamtouch.settings;


import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MiscListActivity extends Activity implements OnItemClickListener {


	private ListView mMiscList;
	private String phoneLanguage;

	float scale;
	private int item_height ; //Converting to Pixel
	private int padding;
	
	private ArrayList<View> views = new ArrayList<View>();
	OnCheckedChangeListener chk_lsnr;
	SharedPreferences sharedPreferences;
	private CheckBox tutorCB,eventViewerCB;
	private TextView lang;
	
	//Shared_Pref_MiscActivity
	//---language_from(String)
	//---language_to(String)
	//---enable_tutor(boolean)
	//---enable_event_viewer(boolean)
	//---selected_image_path(String)
	
	
	int SELECT_PICTURE = 1;
	String selectedImagePath;
	private static int thumbnailWidth = 200;
	private static int thumbnailHeight = 200;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		scale = getResources().getDisplayMetrics().density;
		item_height = (int) (60 * scale + 0.5f); //Converting to Pixel
		padding = (int) (10 * scale + 0.5f); //Converting to Pixel
		
		setContentView(R.layout.misc_list);
		mMiscList = (ListView)findViewById(R.id.miscList);
		mMiscList.setAdapter(new MiscListAdapter());
		mMiscList.setOnItemClickListener(this);

		Locale l=Locale.getDefault();
		phoneLanguage = l.getDisplayLanguage(Locale.ENGLISH);

	}

	@Override
	protected void onStart() {
		sharedPreferences = getApplicationContext().getSharedPreferences("Shared_Pref_AppSettings", MODE_WORLD_READABLE);
		super.onStart();
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		switch(position){
		case 1:
			final TextView tv2 = (TextView)view.findViewById(R.id.tv2);
			String langs[] = getResources().getStringArray(R.array.language_preference);
			final Dialog d = new Dialog(this);
			d.requestWindowFeature(Window.FEATURE_NO_TITLE);
			d.setContentView(R.layout.languages_dialog);
			ListView list = (ListView)d.findViewById(R.id.list);
			list.setOnItemClickListener(new OnItemClickListener(){

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TextView v = (TextView)view;
					tv2.setText(v.getText());
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("language_to", v.getText().toString());
					editor.commit();
					d.cancel();
				}

			});
			list.setAdapter(new ArrayAdapter<String>(this,R.layout.simple_list_item_1,langs));
			d.show();
			break;
		case 3:
			AlertDialog alertDialog = new AlertDialog.Builder(MiscListActivity.this).create();
			alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		    alertDialog.setMessage("Do you really want to restore the default settings?");
		    alertDialog.setTitle("Restore Defaults");
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		    	  	eventViewerCB.setChecked(true);
					tutorCB.setChecked(true);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("language_to", phoneLanguage);
					editor.commit();
					lang.setText(phoneLanguage);

		    } }); 
		    alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  return;
			    } });
		  	alertDialog.show();
		  	break;
		case 4:
			CheckBox cb = (CheckBox)view.findViewById(R.id.checkbox);
			if(cb.isChecked())
				cb.setChecked(false);
			else
				cb.setChecked(true);		
			break;
		case 5:
			cb = (CheckBox)view.findViewById(R.id.checkbox);
			if(cb.isChecked())
				cb.setChecked(false);
			else
				cb.setChecked(true);		
			break;
		case 6:
			Intent intent = new Intent(MiscListActivity.this,Contacts.class);
			startActivity(intent);
			break;
		case 7:
			alertDialog = new AlertDialog.Builder(MiscListActivity.this).create();
			alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		    alertDialog.setMessage("This feature is currently not yet available. We are working really hard on it and it'll be there in future versions. Stay tuned.");
		    alertDialog.setTitle("Coming soon ...");
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		        return;

		    } }); 
		  	alertDialog.show();
/*			intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
*/			break;
		case 9:
			alertDialog = new AlertDialog.Builder(MiscListActivity.this).create();
			alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		    alertDialog.setMessage("About Roaming Keyboards...");
		    alertDialog.setTitle("About Us ...");
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		        return;

		    } }); 
		  	alertDialog.show();
		  	break;
		case 10:
			alertDialog = new AlertDialog.Builder(MiscListActivity.this).create();
			alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		    alertDialog.setTitle("Version-Information");
		    alertDialog.setMessage("Version: " + BrowserActivity.version);
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		        return;

		    } }); 
		  	alertDialog.show();
		  	break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				selectedImagePath = getPath(selectedImageUri);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("selected_image_path", selectedImagePath);
				editor.commit();
				mMiscList.setAdapter(new MiscListAdapter());
			}
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
		.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public class TranslateView extends LinearLayout {
		String arr[] = {"Origin:","Translate to:"};
		public TranslateView(Context context,int position) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.translate_adapter, this);		
			TextView v1= (TextView) findViewById(R.id.tv1);
			v1.setText("Translate to:");

			TextView v2= (TextView) findViewById(R.id.tv2);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			String langto = sharedPreferences.getString("language_to", null);
			lang = v2;

//			if(position == 1){
			if(langto!=null){
				v2.setText(langto);				
			}
			else{
				v2.setText(phoneLanguage);
				editor.putString("language_to", phoneLanguage);
				editor.commit();
			}
				//ImageView image = (ImageView)findViewById(R.id.image);
				//image.setVisibility(INVISIBLE);
				
/*			}
			else
				v2.setText(sharedPreferences.getString("language_to", "Hindi"));
				editor.putString("language_from", phoneLanguage);
				editor.commit();
*/		}

	}
	
	public class MiscView extends LinearLayout{
		String arr[] = {"Restore Default", "Enable Tutor", "Enable Event Viewer", "Tell a contact"};
		public MiscView(Context context,int position) {
			super(context);

			LayoutInflater.from(context).inflate(R.layout.misc_adapter, this);

			TextView v1= (TextView) findViewById(R.id.tv1);
			v1.setText(arr[position-3]);

			CheckBox cb= (CheckBox) findViewById(R.id.checkbox);
			ImageView image = (ImageView)findViewById(R.id.image);

			if(position == 3){
				cb.setVisibility(GONE);
				image.setVisibility(VISIBLE);
			}
			else if(position == 4){
				if(sharedPreferences.getBoolean("enable_tutor", true)){
					cb.setChecked(true);
				}
				else{
					cb.setChecked(false);
				}
				cb.setTag("enable_tutor");
				cb.setOnCheckedChangeListener(chk_lsnr);
				tutorCB = cb;
			}
			else if(position == 5){
				if(sharedPreferences.getBoolean("enable_event_viewer", true)){
					cb.setChecked(true);
				}
				else{
					cb.setChecked(false);
				}
				cb.setTag("enable_event_viewer");
				cb.setOnCheckedChangeListener(chk_lsnr);
				eventViewerCB = cb;
			}
			else if(position == 6){
				cb.setVisibility(GONE);
				image.setVisibility(VISIBLE);
			}
			
		}
	}

	public class HotKeyView extends LinearLayout{

		public HotKeyView(Context context) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.hot_key_layout, this);		
			TextView v1= (TextView) findViewById(R.id.tv1_hotKey);
			v1.setText("Set HotKey");
			ImageView image = (ImageView)findViewById(R.id.image_hotkey);
			System.out.println("path : "+sharedPreferences.getString("selected_image_path", " "));
			if(!sharedPreferences.getString("selected_image_path", " ").equals(" ")){
				//Bitmap bit = getSmallImage(sharedPreferences.getString("selected_image_path", " "));
				//image.setImageBitmap(bit);
			}
			else{
				image.setVisibility(View.GONE);
			}
		}
	}
	
	public class AboutView extends LinearLayout{

		public AboutView(Context context) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.about_text, this);		
			TextView v1= (TextView) findViewById(R.id.version);
			v1.setText(BrowserActivity.version_code);
		}

	}


	public class MiscListAdapter extends BaseAdapter {

		public MiscListAdapter(){
			chk_lsnr = new OnCheckedChangeListener() {
				
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					SharedPreferences.Editor editor = sharedPreferences.edit();
					if(buttonView.getTag().equals("enable_tutor")){
						if(isChecked){
							editor.putBoolean("enable_tutor", true);
							editor.commit();
						}
						else{
							editor.putBoolean("enable_tutor", false);
							editor.commit();
						}
					}
					else if(buttonView.getTag().equals("enable_event_viewer")){
						if(isChecked){
							editor.putBoolean("enable_event_viewer", true);
							editor.commit();
						}
						else{
							editor.putBoolean("enable_event_viewer", false);
							editor.commit();
						}
					}
				}
			};
		}

		public int getCount() {
			return 11;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if(views.size()==11)
				return views.get(position);
			
			if(position == 0){
				TextView tv = new TextView(MiscListActivity.this);
				tv.setHeight(60);
				tv.setPadding(5, 0, 0, 0);
				tv.setText("Translate");
				tv.setTextColor(Color.WHITE);		
				tv.setBackgroundColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				views.add(0,tv);
				return tv; 
			}
			else if(position == 2){
				TextView tv = new TextView(MiscListActivity.this);
				tv.setHeight(60);
				tv.setPadding(5, 0, 0, 0);
				tv.setText("Miscellaneous");
				tv.setTextColor(Color.WHITE);		
				tv.setBackgroundColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				views.add(2,tv);
				return tv; 
			}
			else if(position == 8){
				TextView tv = new TextView(MiscListActivity.this);
				tv.setHeight(60);
				tv.setPadding(5, 0, 0, 0);
				tv.setText("About");
				tv.setTextColor(Color.WHITE);		
				tv.setBackgroundColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				views.add(8,tv);
				return tv; 
			}
			else if(position == 9){
				TextView tv = new TextView(MiscListActivity.this);
				tv.setHeight(item_height);
				tv.setPadding(padding, 0, 0, 0);
				tv.setText("About Us");
				tv.setTextSize(20);
				tv.setTextColor(Color.BLACK);		
				tv.setBackgroundColor(Color.WHITE);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				views.add(9,tv);
				return tv; 
			}
			else if(position == 10){
				AboutView about=new AboutView(MiscListActivity.this);
				views.add(10,about);
				return about;
			}
			else if(position == 1){
				TranslateView translateView=new TranslateView(MiscListActivity.this,position);
				views.add(1,translateView);
				return translateView;
			}
			else if(position == 7){
				HotKeyView hotKeyView = new HotKeyView(MiscListActivity.this);
				views.add(7,hotKeyView);
				return hotKeyView;
			}
			MiscView miscView = new MiscView(MiscListActivity.this,position);
			views.add(position,miscView);
			return miscView;
		}
	}

	private Bitmap getSmallImage(String source) {

		Bitmap temp_bitmap2 = null;
		try {
			File file = new File(source);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 16;
			options.inJustDecodeBounds = true;
			options.outWidth = 0;
			options.outHeight = 0;
			options.inSampleSize = 1;
			BitmapFactory.decodeFile(file.getPath(), options);

			if (options.outWidth > 0 && options.outHeight > 0) {
				int widthFactor = (options.outWidth + thumbnailWidth - 1) / thumbnailWidth;
				int heightFactor = (options.outHeight + thumbnailHeight - 1) / thumbnailHeight;

				widthFactor = Math.max(widthFactor, heightFactor);
				widthFactor = Math.max(widthFactor, 1);

				if (widthFactor > 1) {
					if ((widthFactor & (widthFactor-1)) != 0) {
						while ((widthFactor & (widthFactor-1)) != 0) {
							widthFactor &= widthFactor-1;
						}
						widthFactor <<= 1;
					}
				}
				options.inSampleSize = widthFactor;
				options.inJustDecodeBounds = false;

				temp_bitmap2 = BitmapFactory.decodeFile(file.getPath(), options);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		return temp_bitmap2;
	}
}
