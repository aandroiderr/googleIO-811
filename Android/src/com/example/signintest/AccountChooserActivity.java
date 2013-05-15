package com.example.signintest;

import com.example.signintest.SignInFragment.SignInStatusListener;
import com.example.signintest.providers.FacebookProvider;
import com.example.signintest.providers.GooglePlusProvider;
import com.example.signintest.providers.LinkedinProvider;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class AccountChooserActivity extends FragmentActivity 
	implements OnClickListener, SignInStatusListener {
	
	private SignInFragment mSignInFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accountchooser);
		
		mSignInFragment = SignInFragment.getSignInFragment(this);
        
        findViewById(R.id.google_plus_signin).setOnClickListener(this);
        findViewById(R.id.facebook_signin).setOnClickListener(this);
        findViewById(R.id.linkedin_signin).setOnClickListener(this);
        onNewIntent(getIntent());
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.google_plus_signin:
			mSignInFragment.signIn(GooglePlusProvider.ID);
			break;
		case R.id.facebook_signin:
			mSignInFragment.signIn(FacebookProvider.ID);
			break;	
		case R.id.linkedin_signin:
			mSignInFragment.signIn(LinkedinProvider.ID);
			break;	
		}
	}

	@Override
	public void onStatusChange(SignInUser user) {
		if (user.isSignedIn()) {
			Intent intent = this.getIntent();
			this.setResult(RESULT_OK, intent);
			finish();
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
