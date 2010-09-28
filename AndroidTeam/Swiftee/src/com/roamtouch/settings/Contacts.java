package com.roamtouch.settings;


import com.roamtouch.swiftee.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;

public class Contacts extends Activity {

	String[] name_arr, num_arr, email_arr;
	ListView mMiscList;
	Context context;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_list);

		context = getApplicationContext();

		mMiscList = (ListView)findViewById(R.id.miscList);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getContactList(context);
		mMiscList.setAdapter(new ContactAdapter(Contacts.this, name_arr, email_arr, num_arr));
	}

	public void getContactList(Context context){
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
		name_arr = new String[cur.getCount()];
		num_arr = new String[cur.getCount()];
		email_arr = new String[cur.getCount()];

		if (cur.getCount() > 0) {

			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				String id = cur.getString(
						cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(
						cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				System.out.println("name : "+name);
				name_arr[cur.getPosition()] = name;

				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
							null, 
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
							new String[]{id}, null);
					while (pCur.moveToNext()) {
						String num = pCur.getString(
								pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
						String numType = pCur.getString(
								pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)); 
						if(numType.equals(""+ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)){
							num_arr[cur.getPosition()] = num;
						}
					} 
					pCur.close();
				}

				Cursor emailCur = cr.query(
						ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
						null,
						ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
						new String[]{id}, null);
				while (emailCur.moveToNext()) {
					String email = emailCur.getString(
							emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
					String emailType = emailCur.getString(
							emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)); 
					if(emailType.equals(""+ContactsContract.CommonDataKinds.Email.TYPE_HOME)){
						email_arr[cur.getPosition()] = email;
					}
				} 
				emailCur.close();

				cur.moveToNext();
			}
		}
		cur.close();

		for(int i=0;i<name_arr.length;i++){
			if(name_arr[i] == null){
				name_arr[i] = " ";
			}
			if(num_arr[i] == null){
				num_arr[i] = " ";
			}
			if(email_arr[i] == null){
				email_arr[i] = " ";
			}
		}

	}

}