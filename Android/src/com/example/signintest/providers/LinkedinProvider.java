package com.example.signintest.providers;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.signintest.Provider;
import com.example.signintest.ProviderUtil;
import com.example.signintest.ProviderUtil.Feature;
import com.example.signintest.SignInFragment;
import com.example.signintest.SignInUser;

public class LinkedinProvider implements Provider {
	public static final String ID = "linkedin";
	private final static String TAG = "LINKEDIN_PROVIDER";
	private final static String PREFS_NAME = "LINKEDIN_PROVIDER_PREFS";
	private final static String LINKEDIN_PROFILE_URL = "http://api.linkedin.com/v1/people/~:(id,first-name,last-name)";
	private final static String CALLBACK = "signintest://%s/%s";
	private final static String LINKEDIN_ACCESS_TOKEN = "linkedin_access_token";
	private final static String LINKEDIN_ACCESS_SECRET = "linkedin_access_secret";
	private SignInFragment mManager;
	private Token mAccessToken;
	private Token mRequestToken;
	private boolean mIsUserAction;
	private OAuthService mService;
	private SharedPreferences mPrefs;
	private Document mUser;
	private ProgressDialog mPd;

	/**
	 * Retrieve the auth URL.
	 */
	private class GetAuthURLTask extends AsyncTask<Void, Void, String> {
		protected String doInBackground(Void... arg0) {  
			try {  
		        mRequestToken = mService.getRequestToken();  
		        return mService.getAuthorizationUrl(mRequestToken);  
			} catch ( OAuthException e ) {  
				e.printStackTrace();  
				return null;  
			}	  
		}
		
		protected void onPostExecute(String authURL) {   
			mPd.dismiss();
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authURL))
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
			mManager.getActivity().getApplicationContext().startActivity(intent);
		}  
	}
	
	/**
	 * Exchange a code for a token.
	 */
	private class GetTokenTask extends AsyncTask<String, Void, Void> {
		protected Void doInBackground(String... params) {  
			try {
				Verifier v = new Verifier(params[0]);  
	            mAccessToken = mService.getAccessToken(mRequestToken, v); 
	            SharedPreferences.Editor editor = mPrefs.edit();
	    		editor.putString(LINKEDIN_ACCESS_TOKEN, mAccessToken.getToken());
	    		editor.putString(LINKEDIN_ACCESS_SECRET, mAccessToken.getSecret());
	    		editor.commit();
	            refreshUser();
			} catch ( OAuthException e ) {  
				e.printStackTrace();  
			}
			return null;
		} 
	}
	
	/**
	 * Retrieve user profile information from the LinkedIn API.
	 */
	private class GetUserTask extends AsyncTask<String, Void, Void> {
		protected Void doInBackground(String... params) {  
			try {
				OAuthRequest request = new OAuthRequest(Verb.GET, LINKEDIN_PROFILE_URL);
				mService.signRequest(mAccessToken, request); 
				Response response = request.send();
				//Log.i("LinkedIN", response.getBody());
				StringReader sr = new StringReader(response.getBody());
				InputSource is = new InputSource(sr);
				try {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					mUser = builder.parse(is);
				}
				catch(Exception e) {
					Log.i(TAG, "Could not parse response");
				}
				
				if(mIsUserAction) {
					returnUser();
				}
			} catch ( OAuthException e ) {  
				e.printStackTrace();  
			}
			return null;
		} 
	}
	
	@Override
	public void setFragment(SignInFragment manager) {
		mManager = manager;
		mIsUserAction = false;
		// TODO: These should clearly live somewhere else!
		String apiKey = "yzwlgpm1ijqj";
		String apiSecret = "IvJRIk57latNMxRy";
		// Initialise the Service from Scribe.
		mService = new ServiceBuilder()  
    	.provider(LinkedInApi.class)  
    	.apiKey(apiKey)  
    	.apiSecret(apiSecret)  
    	.callback(String.format(CALLBACK, mManager.getRoutingKey(), ID))  
        .scope("r_basicprofile")    
        .build();  
		mPrefs = mManager.getActivity().getSharedPreferences(PREFS_NAME, 0);
	}

	@Override
	public void detachFragment() {
		return;
	}

	@Override
	public boolean handleOnActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (requestCode != ProviderUtil.WEBCALLBACK) {
			return false;
		}
		final Uri uri = data.getData();
		if (null == uri) {
			return false;
		}
		String path = uri.getPath();
		String localPath = "/" + ID;
		if (path.equals(localPath)) {
			String verifier = uri.getQueryParameter("oauth_verifier");  
			GetTokenTask task = new GetTokenTask();
			mIsUserAction = true;
			task.execute(verifier);
    		return true;
		}
		return false;
	}

	private void refreshUser() {
		if(mUser == null) {
			GetUserTask task = new GetUserTask();
			task.execute();
		}
		
		if(mIsUserAction && mUser != null) {
			returnUser();
		}
		
		mIsUserAction = false;
	}
	
	private void returnUser() {
		SignInUser user = mManager.buildSignInUser();
		user.setName(mUser.getElementsByTagName("first-name").item(0).getTextContent());
		user.setProviderData(this, mUser);
		mManager.onSignedIn(user);
	}

	@Override
	public String getId() {
		return LinkedinProvider.ID;
	}

	@Override
	public String getUserId(Object user) {
		return ((Document)user).getElementsByTagName("id").item(0).getTextContent();
	}

	@Override
	public boolean hasFeature(Feature feature) {
		// This is just an example! Obviously specific features will 
		// depend on the APIs implemented etc.
		switch(feature) {
		case GRAPH:
		case PROFILE:
		case FEATUREB:
			return true;
		default: 
			return false;
		}
	}

	@Override
	public void trySilentAuthentication() {
		if (mPrefs.getString(LINKEDIN_ACCESS_TOKEN, null) != null && mUser == null) {
			mAccessToken = new Token(mPrefs.getString(LINKEDIN_ACCESS_TOKEN, null), mPrefs.getString(LINKEDIN_ACCESS_SECRET, null));
			refreshUser();
		}
	}

	@Override
	public void signIn() {
		mIsUserAction = true;
		if(mUser == null) {
			mPd = new ProgressDialog(mManager.getActivity());
			mPd.setMessage("Authenticating with LinkedIn");
			mPd.show();
			GetAuthURLTask task = new GetAuthURLTask();
			task.execute();
		} else {
			returnUser();
		}
	}

	@Override
	public void signOut(SignInUser user) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.remove(LINKEDIN_ACCESS_TOKEN);
		editor.remove(LINKEDIN_ACCESS_SECRET);
		editor.commit();
	}

	@Override
	public void disconnect(SignInUser user) {
		signOut(user);
	}

}
