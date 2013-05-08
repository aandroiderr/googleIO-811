package com.example.signintest;

import com.example.signintest.SignInFragment.SignInStatusListener;

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
	}

	@Override
	public void onStatusChange(SignInUser user) {
		if (user.isSignedIn()) {
			
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
}
