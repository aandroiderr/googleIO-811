package com.example.signintest;

import com.example.signintest.SignInFragment.SignInStatusListener;
import com.example.signintest.providers.FacebookProvider;
import com.example.signintest.providers.GooglePlusProvider;
import com.example.signintest.providers.LinkedinProvider;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Switch;

public class SettingsActivity extends FragmentActivity
	implements SignInStatusListener {
	private SignInFragment mSignInFragment;
	
	// @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		mSignInFragment = SignInFragment.getSignInFragment(this);
		configureSettings(mSignInFragment.getUser());
		onNewIntent(getIntent());
	}

	@Override
	public void onStatusChange(SignInUser user) {
		configureSettings(user);
 	}
	
	public void configureSettings(SignInUser user) {
		if(user.isSignedIn()) {
			for(Provider p : user.listConnectedProviders()) {
				int toggle = -1;
				int disconnect = -1;
				if(p.getId() == GooglePlusProvider.ID) {
					toggle = R.id.toggle_google;
					disconnect = R.id.disconnect_google;
				} else if(p.getId() == FacebookProvider.ID) {
					toggle = R.id.toggle_facebook;
					disconnect = R.id.disconnect_facebook;
				} else if(p.getId() == LinkedinProvider.ID) {
					toggle = R.id.toggle_linkedin;
					disconnect = R.id.disconnect_linkedin;
				}
				
				if(toggle != -1) {
					((Switch)findViewById(toggle)).setChecked(true);
					findViewById(disconnect).setVisibility(View.VISIBLE);
				}
			}
		} else {
			
		}
	}
	
	public void onDisconnect(View v) { 
		String provider = null;
		
		switch (v.getId()) {
		case R.id.disconnect_google:
			provider = GooglePlusProvider.ID;
			break;
		case R.id.disconnect_facebook:
			provider = FacebookProvider.ID;
			break;
		case R.id.disconnect_linkedin:
			provider = LinkedinProvider.ID;
			break;
		}
		
		mSignInFragment.disconnect(provider);
	}
	
	public void onSwitched(View v) {
	    boolean on = ((Switch) v).isChecked();
	    String provider = null;
	    
	    switch (v.getId()) {
		case R.id.toggle_google:
			provider = GooglePlusProvider.ID;
			break;
		case R.id.toggle_facebook:
			provider = FacebookProvider.ID;
			break;
		case R.id.toggle_linkedin:
			provider = LinkedinProvider.ID;
			break;	
		}
	    
	    if (on) {
			mSignInFragment.signIn(provider);
		} else {
			mSignInFragment.signOut(provider);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSignInFragment.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onNewIntent(Intent intent) {
		mSignInFragment.onActivityResult(ProviderUtil.WEBCALLBACK, RESULT_OK, intent);
	}
}
