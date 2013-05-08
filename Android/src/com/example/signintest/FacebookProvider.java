package com.example.signintest;

import android.content.Intent;

import com.facebook.*;
import com.facebook.model.*;

public class FacebookProvider implements 
	Provider, Session.StatusCallback, Request.GraphUserCallback {
	public static final String ID = "facebook";
	
	private SignInFragment mManager;
	
	@Override
	public String getId() {
		return FacebookProvider.ID;
	}

	@Override
	public void setFragment(SignInFragment manager) {
		mManager = manager;
	}

	@Override
	public void trySilentAuthentication() {
		// Test login status - set allowLoginUI to false
		Session.openActiveSession(mManager.getActivity(), false, this);
		
	}

	@Override
	public void signIn() {
		// start Facebook Login - allowLoginUI is true
		Session.openActiveSession(mManager.getActivity(), true, this);
	}

	@Override
	public void signOut(SignInUser user) {
		Session.getActiveSession().closeAndClearTokenInformation();
	}

	@Override
	public void disconnect(SignInUser user) {
		signOut(user);
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if (session.isOpened()) {
			Request.executeMeRequestAsync(session, this);
		}
	}
	
	// callback after Graph API response with user object
	// TODO: Cache this.
	@Override
	public void onCompleted(GraphUser graphUser, Response response) {
		SignInUser user = mManager.buildSignInUser();
		user.setName(graphUser.getName());
		user.setProviderData(this, graphUser);
		mManager.onSignedIn(user);
	}

	@Override
	public void detachFragment() {
		return;
	}

	@Override
	public boolean handleOnActivityResult(int requestCode, int resultCode,
			Intent data) {
		return Session.getActiveSession().onActivityResult(mManager.getActivity(), requestCode, resultCode, data);
	}

	@Override
	public String getUserId(Object user) {
		return ((GraphUser)user).getId();
	}
}
