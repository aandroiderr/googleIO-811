package com.example.signintest;

import android.content.Intent;

public interface Provider {
	public String getId();
	public String getUserId(Object user);
	public void setFragment(SignInFragment manager);
	public void detachFragment();
	public void trySilentAuthentication();
	public void signIn();
	public void signOut(SignInUser user);
	public void disconnect(SignInUser user);
	public boolean handleOnActivityResult(int requestCode, int resultCode, Intent data);
}
