/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.signintest;

import com.example.signintest.SignInFragment.SignInStatusListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity 
	implements SignInStatusListener, OnClickListener {
	
	private SignInFragment mSignInFragment;
	private static final int MAIN_REQUEST = 352;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mSignInFragment = SignInFragment.getSignInFragment(this);
	}

	/**
	 * onStatusChange is called whenever we have a new user object or a 
	 * change to the user object from the sign in fragment. In this case 
	 * we're just going to test for different features avialable, and
	 * display the user's name.
	 * 
	 * @param SignInUser user the current user object
	 */
	@Override
	public void onStatusChange(SignInUser user) {
		CheckBox graph = (CheckBox)findViewById(R.id.checkbox_graph);
		CheckBox calendar = (CheckBox)findViewById(R.id.checkbox_calendar);
		CheckBox profile = (CheckBox)findViewById(R.id.checkbox_profile);
		CheckBox featurea = (CheckBox)findViewById(R.id.checkbox_featurea);
		CheckBox featureb = (CheckBox)findViewById(R.id.checkbox_featureb);
		TextView maintext = (TextView)findViewById(R.id.maintext);
		Button signin = (Button)findViewById(R.id.mainbutton);
		if (user.isSignedIn()) {
			signin.setText(R.string.accounts);
			maintext.setText(String.format(getString(R.string.welcome), user.getName()));
			
			graph.setChecked(user.hasFeature(ProviderUtil.Feature.GRAPH));
			calendar.setChecked(user.hasFeature(ProviderUtil.Feature.CALENDAR));
			profile.setChecked(user.hasFeature(ProviderUtil.Feature.PROFILE));
			featurea.setChecked(user.hasFeature(ProviderUtil.Feature.FEATUREA));
			featureb.setChecked(user.hasFeature(ProviderUtil.Feature.FEATUREB));
			
			if(user.isNew()) {
				// For new users, give them an extra hello!
				Toast.makeText(this, getString(R.string.firstrun), Toast.LENGTH_LONG).show();
			}
		} else {
			signin.setText(R.string.signin);
			maintext.setText(R.string.signinmessage);
			graph.setChecked(false);
			calendar.setChecked(false);
			profile.setChecked(false);
			featurea.setChecked(false);
			featureb.setChecked(false);
		}
 	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.mainbutton:
			if (((Button)v).getText() == getString(R.string.signin)) {
				// Show Sign In screen.
				Intent intent = new Intent(this, AccountChooserActivity.class);
				startActivityForResult(intent, MAIN_REQUEST);	
			} else {
				// Show settings screen.
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivityForResult(intent, MAIN_REQUEST);
			}
			break;
		}
	}
	
	/**
	 * onActivityResult may get either responses from the account or settings 
	 * activities, or from callbacks from the various providers.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MAIN_REQUEST) {
			onStatusChange(mSignInFragment.getUser());
		} else {
			mSignInFragment.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	public void onNewIntent(Intent intent) {
		mSignInFragment.onActivityResult(ProviderUtil.WEBCALLBACK, RESULT_OK, intent);
	}

}
