package com.api.twitter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.roamtouch.swiftee.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterActivity extends Activity  {
	public static final String TAG = "TwitterActivity";

	private CheckBox mCB;
	private EditText mEditor;
	private Button mPostButton,mLoginButton;
	private TextView mUser;
	private TextView mLast;

	public static final String VERIFY_URL_STRING = "http://twitter.com/account/verify_credentials.json";
	public static final String PUBLIC_TIMELINE_URL_STRING = "http://twitter.com/statuses/public_timeline.json";
	public static final String USER_TIMELINE_URL_STRING = "http://twitter.com/statuses/user_timeline.json";
	public static final String HOME_TIMELINE_URL_STRING = "http://api.twitter.com/1/statuses/home_timeline.json";
	public static final String FRIENDS_TIMELINE_URL_STRING = "http://api.twitter.com/1/statuses/friends_timeline.json";
	public static final String STATUSES_URL_STRING = "http://twitter.com/statuses/update.json";

	ProgressDialog postDialog = null;

	public static final String TWITTER_REQUEST_TOKEN_URL = "http://twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_TOKEN_URL = "http://twitter.com/oauth/access_token";
	public static final String TWITTER_AUTHORIZE_URL = "http://twitter.com/oauth/authorize";

	private OAuthConsumer mConsumer = null;

	public String mToken;
	public String mSecret,mTweet;

	SharedPreferences mSettings;


	HttpClient mClient;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mTweet = getIntent().getStringExtra("Tweet");
		
		setContentView(R.layout.twitter);

		HttpParams parameters = new BasicHttpParams();
		HttpProtocolParams.setVersion(parameters, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(parameters,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(parameters, false);
		HttpConnectionParams.setTcpNoDelay(parameters, true);
		HttpConnectionParams.setSocketBufferSize(parameters, 8192);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		ClientConnectionManager tsccm = new ThreadSafeClientConnManager(
				parameters, schReg);
		mClient = new DefaultHttpClient(tsccm, parameters);

		mCB = new CheckBox(this);
		
//		mCB = (CheckBox) this.findViewById(R.id.enable);
//		mCB.setChecked(false);

		mEditor = (EditText) this.findViewById(R.id.editor);
		mEditor.setText(mTweet);
		mPostButton = (Button) this.findViewById(R.id.post);
		mLoginButton = (Button) this.findViewById(R.id.login);
		
		mUser = (TextView) this.findViewById(R.id.user);

		mLast = (TextView) this.findViewById(R.id.last);

		OnClickListener listener = new OnClickListener(){

			public void onClick(View v) {
				if(mPostButton.equals(v)){
					String postString = mEditor.getText().toString();
					if (postString.length() == 0) {
						Toast.makeText(TwitterActivity.this, getText(R.string.tweet_empty),
								Toast.LENGTH_SHORT).show();
					} else {
						 new PostTask().execute(postString);
					}
				}
				else{
					Intent i = new Intent(TwitterActivity.this, OAUTH.class);
					i.putExtra("Tweet", mTweet);
					startActivity(i);
					finish();
				}					
			}
			
		};
		mPostButton.setOnClickListener(listener);
		mLoginButton.setOnClickListener(listener);
		mSettings = getSharedPreferences(OAUTH.PREFS, Context.MODE_PRIVATE);
		mConsumer = new CommonsHttpOAuthConsumer(Keys.TWITTER_CONSUMER_KEY,
				Keys.TWITTER_CONSUMER_SECRET);

	}

	@Override
	public void onResume() {
		super.onResume();

		
		// We look for saved user keys
		if (mSettings.contains(OAUTH.USER_TOKEN)
				&& mSettings.contains(OAUTH.USER_SECRET)) {
			mToken = mSettings.getString(OAUTH.USER_TOKEN, null);
			mSecret = mSettings.getString(OAUTH.USER_SECRET, null);
			if (!(mToken == null || mSecret == null)) {
				mConsumer.setTokenWithSecret(mToken, mSecret);
			}
		}
		new GetCredentialsTask().execute();
	}

	protected void onFinish() {
		mClient.getConnectionManager().shutdown();
	}

	
	private String getUserName(JSONObject credentials) {
		return credentials.optString("name", getString(R.string.bad_value));
	}


	// These parameters are needed to talk to the messaging service
	public HttpParams getParams() {
		// Tweak further as needed for your app
		HttpParams params = new BasicHttpParams();
		// set this to false, or else you'll get an Expectation Failed: error
		HttpProtocolParams.setUseExpectContinue(params, false);
		return params;
	}

	// ----------------------------
	// This task is run on every onResume(), to make sure the current
	// credentials are valid.
	// This is probably overkill for a non-educational program
	private class GetCredentialsTask extends AsyncTask<Void, Void, JSONObject> {

		ProgressDialog authDialog;

		@Override
		protected void onPreExecute() {
			authDialog = ProgressDialog.show(TwitterActivity.this,
					getText(R.string.auth_progress_title),
					getText(R.string.auth_progress_text), true, // indeterminate
																// duration
					false); // not cancel-able
		}

		@Override
		protected JSONObject doInBackground(Void... arg0) {
			JSONObject jso = null;
			HttpGet get = new HttpGet(VERIFY_URL_STRING);
			try {
				mConsumer.sign(get);
				String response = mClient.execute(get,
						new BasicResponseHandler());
				jso = new JSONObject(response);
				Log.d(TAG, "authenticatedQuery: " + jso.toString(2));
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return jso;
		}

		// This is in the UI thread, so we can mess with the UI
		protected void onPostExecute(JSONObject jso) {
			authDialog.dismiss();
//			mCB.setChecked(jso != null);
			if(jso != null)
				mLoginButton.setText("Login as diffrent user");
			else
				mLoginButton.setText("Login");
			
			mPostButton.setEnabled(jso != null);
			mEditor.setEnabled(jso != null);
			mUser.setText(jso != null ? getUserName(jso)
					: getString(R.string.userhint));
		
		}
	}

	// ----------------------------
	// This task posts a message to your message queue on the service.
	private class PostTask extends AsyncTask<String, Void, JSONObject> {

		ProgressDialog postDialog;

		@Override
		protected void onPreExecute() {
			postDialog = ProgressDialog.show(TwitterActivity.this,
					getText(R.string.tweet_progress_title),
					getText(R.string.tweet_progress_text), true, // indeterminate
																	// duration
					false); // not cancel-able
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject jso = null;
			try {
				HttpPost post = new HttpPost(
						"http://twitter.com/statuses/update.json");
				LinkedList<BasicNameValuePair> out = new LinkedList<BasicNameValuePair>();
				out.add(new BasicNameValuePair("status", params[0]));
				post.setEntity(new UrlEncodedFormEntity(out, HTTP.UTF_8));
				post.setParams(getParams());
				// sign the request to authenticate
				mConsumer.sign(post);
				String response = mClient.execute(post,
						new BasicResponseHandler());
				jso = new JSONObject(response);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {

			}
			return jso;
		}

		// This is in the UI thread, so we can mess with the UI
		protected void onPostExecute(JSONObject jso) {
			postDialog.dismiss();
			if (jso != null) { // authorization succeeded, the json object
								// contains the user information
				mEditor.setText("");
				mLast.setText("Successfully posted");
				Intent i = new Intent();
				i.putExtra("Finish", "YES");
				TwitterActivity.this.setResult(1);
				TwitterActivity.this.finish();
				
			} else {
				mLast.setText(getText(R.string.tweet_error));
			}
		}
	}

}