package com.example.signintest;

import android.content.Intent;

/**
 * Interface for an individual identity provider 
 */
public interface Provider {
	public void setFragment(SignInFragment manager);
	public void detachFragment();
	public boolean handleOnActivityResult(int requestCode, int resultCode, Intent data);
	
	public String getId();
	public String getUserId(Object user);
	public boolean hasFeature(ProviderUtil.Feature feature);
	
	public void trySilentAuthentication();
	public void signIn();
	public void signOut(SignInUser user);
	public void disconnect(SignInUser user);
	
	
}
