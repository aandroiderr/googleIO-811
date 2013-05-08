package com.example.signintest;

import com.example.signintest.SignInFragment.SignInStatusListener;

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
		//configureSettings(mSignInFragment.getUser());
	}

	@Override
	public void onStatusChange(SignInUser user) {
		configureSettings(user);
 	}
	
	public void configureSettings(SignInUser user) {
		if(user.isSignedIn()) {
			for(Provider p : user.listConnectedProviders()) {
				int toggle = -1;
				if(p.getId() == GooglePlusProvider.ID) {
					toggle = R.id.toggle_google;
				} else if(p.getId() == FacebookProvider.ID) {
					toggle = R.id.toggle_facebook;
				}
				
				if(toggle != -1) {
					((Switch)findViewById(toggle)).setChecked(true);
				}
			}
		} else {
			
		}
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
}
