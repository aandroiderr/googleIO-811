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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mSignInFragment = SignInFragment.getSignInFragment(this);
	}

	@Override
	public void onStatusChange(SignInUser user) {
		CheckBox graph = (CheckBox)findViewById(R.id.checkbox_graph);
		CheckBox calendar = (CheckBox)findViewById(R.id.checkbox_calendar);
		CheckBox profile = (CheckBox)findViewById(R.id.checkbox_profile);
		CheckBox interests = (CheckBox)findViewById(R.id.checkbox_interests);
		TextView maintext = (TextView)findViewById(R.id.maintext);
		Button signin = (Button)findViewById(R.id.mainbutton);
		if (user.isSignedIn()) {
			signin.setText(R.string.accounts);
			maintext.setText(String.format(getString(R.string.welcome), user.getName()));
			
			graph.setChecked(user.hasFeature(ProviderUtil.Feature.GRAPH));
			calendar.setChecked(user.hasFeature(ProviderUtil.Feature.CALENDAR));
			profile.setChecked(user.hasFeature(ProviderUtil.Feature.PROFILE));
			interests.setChecked(user.hasFeature(ProviderUtil.Feature.INTERESTS));
			
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
			interests.setChecked(false);
		}
 	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.mainbutton:
			if (((Button)v).getText() == getString(R.string.signin)) {
				// Show Sign In screen.
				Intent intent = new Intent(this, AccountChooserActivity.class);
				startActivity(intent);	
			} else {
				// Show settings screen.
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
			}
			break;
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSignInFragment.onActivityResult(requestCode, resultCode, data);
		onStatusChange(mSignInFragment.getUser());
	}

}
