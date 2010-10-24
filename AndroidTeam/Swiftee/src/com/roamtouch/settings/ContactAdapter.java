package com.roamtouch.settings;



import com.roamtouch.swiftee.R;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ContactAdapter extends BaseAdapter{


	private Context mContext;	
	private OnClickListener btn_lsnr;
	String[] name, mail, number;
	Button btn_mail, btn_call;
	TextView text_name, text_mail, text_number;
	Dialog dialog;
	String[] tagging = {"Mob: ", "email: "};

	public ContactAdapter(Context context, String[] name, String[] mail, String[] number){
		mContext=context;
		this.name = name;
		this.mail = mail;
		this.number = number;

		btn_lsnr = new OnClickListener(){
			public void onClick(View v) {

				String tag =  v.getTag().toString();
				//System.out.println("tag :: "+tag);
				if(tag.startsWith("call:")){
					//System.out.println("call : ");
					//if(tag==null)System.out.println("null");
					tag = tag.substring(tag.indexOf(":")+1, tag.length());
					//System.out.println("new Tag : "+tag);

					Uri uri = Uri.fromParts("tel", tag, null);
					Intent callIntent = new Intent(Intent.ACTION_CALL, uri);
					callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(callIntent);

					//					dialog = new Dialog(mContext);
					//					dialog.setTitle("Send SMS");
					//					dialog.setCancelable(true);
					//					dialog.setContentView(R.layout.call);
					//					Window w = dialog.getWindow();
					//					w.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					//					
					//					final EditText edt_no = (EditText) w.findViewById(R.id.mob_num);
					//					final EditText edt_msg = (EditText) w.findViewById(R.id.message);
					//					
					//					Button btn_snd = (Button) w.findViewById(R.id.msg_snd);
					//					btn_snd.setOnClickListener(new OnClickListener(){
					//						public void onClick(View v) {
					//							String num = edt_no.getText().toString();
					//							String msg = edt_msg.getText().toString();
					//							SmsManager sm = SmsManager.getDefault();
					//							sm.sendTextMessage(num, null, msg, null, null);
					//							dialog.dismiss();
					//						}
					//					});
					//					Button btn_cancel = (Button) w.findViewById(R.id.msg_cancel);
					//					btn_cancel.setOnClickListener(new OnClickListener(){
					//						public void onClick(View v) {
					//							dialog.dismiss();
					//						}
					//					});
					//					dialog.show();

				}
				else if(tag.startsWith("mail:")){
					Intent intent = new Intent(Intent.ACTION_SENDTO);
					tag = "mailto:" + tag.substring(tag.indexOf(":")+1, tag.length());
		
					//Log.v("Mailto: ", tag);
					intent.setData(Uri.parse(tag));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            mContext.startActivity(intent);
				}
			}
		};
	}

	public int getCount() {
		return name.length ;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.call_adapter, null);    
		}

		text_name = (TextView) v.findViewById(R.id.txt_name);
		text_name.setTextColor(Color.BLACK);
		text_name.setText(name[position]);

		text_number = (TextView) v.findViewById(R.id.txt_number);
		text_number.setTextColor(Color.BLACK);
		text_number.setText(tagging[0]+number[position]);
		
		text_mail = (TextView) v.findViewById(R.id.txt_mail);
		text_mail.setTextColor(Color.BLACK);
		text_mail.setText(tagging[1]+mail[position]);
		
		btn_call = (Button) v.findViewById(R.id.btn_call);
		if(!number[position].equals(" ")){
			btn_call.setTag("call:"+number[position]);
			btn_call.setOnClickListener(btn_lsnr);
			btn_call.setEnabled(true);
		}
		else{
//			btn_call.setClickable(false);
			btn_call.setEnabled(false);
		}

		btn_mail = (Button) v.findViewById(R.id.btn_mail);
		if(!mail[position].equals(" ")){
			btn_mail.setTag("mail:"+mail[position]);
			btn_mail.setOnClickListener(btn_lsnr);
			btn_mail.setEnabled(true);
		}
		else{
//			btn_mail.setClickable(false);
			btn_mail.setEnabled(false);		}

		return v;
	}

}

