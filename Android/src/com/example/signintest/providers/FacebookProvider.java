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

package com.example.signintest.providers;

import android.content.Intent;

import com.example.signintest.Provider;
import com.example.signintest.ProviderUtil;
import com.example.signintest.SignInFragment;
import com.example.signintest.SignInUser;
import com.facebook.*;
import com.facebook.model.*;

public class FacebookProvider implements 
	Provider, Session.StatusCallback, Request.GraphUserCallback {
	public static final String ID = "facebook";
	
	private SignInFragment mManager;
	private GraphUser mGraphUser;
	private boolean mIsSigningIn;
	
	@Override
	public String getId() {
		return FacebookProvider.ID;
	}

	@Override
	public void setFragment(SignInFragment manager) {
		mManager = manager;
		mIsSigningIn = false;
	}

	@Override
	public void trySilentAuthentication() {
		if(mGraphUser == null) {
			// Test login status - set allowLoginUI to false
			Session.openActiveSession(mManager.getActivity(), false, this);
		}
	}

	@Override
	public void signIn() {
		mIsSigningIn = true;
		if (mGraphUser != null) {
			returnUser();
			return;
		}
		// start Facebook Login - allowLoginUI is true
		Session.openActiveSession(mManager.getActivity(), true, this);
	}

	@Override
	public void signOut(SignInUser user) {
		Session.getActiveSession().closeAndClearTokenInformation();
		mGraphUser = null;
	}

	@Override
	public void disconnect(SignInUser user) {
		signOut(user);
	}
	
	public boolean hasFeature(ProviderUtil.Feature feature) {
		// This is just an example! Obviously specific features will 
		// depend on the APIs implemented etc.
		switch(feature) {
		case GRAPH:
		case FEATUREA:
		case PROFILE:
			return true;
		default: 
			return false;
		}
	}
	
	@Override
	public void detachFragment() {
		return;
	}

	@Override
	public boolean handleOnActivityResult(int requestCode, int resultCode,
			Intent data) {
		if(Session.getActiveSession() == null) {
			return false;
		}
		return Session.getActiveSession().onActivityResult(mManager.getActivity(), requestCode, resultCode, data);
	}

	@Override
	public String getUserId(Object user) {
		return ((GraphUser)user).getId();
	}

	/**
	 * Callbacks from API calls.
	 */
	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if (session.isOpened()) {
			Request.executeMeRequestAsync(session, this);
		}
	}
	
	/**
	 * Callback after Graph API response with user object
	 */
	@Override
	public void onCompleted(GraphUser graphUser, Response response) {
		if (graphUser != null) {
			mGraphUser = graphUser;
			returnUser();
		}
	}
	
	/**
	 * Return the user to the manager.
	 */
	private void returnUser() {
		if(mIsSigningIn) {
			SignInUser user = mManager.buildSignInUser();
			user.setName(mGraphUser.getName());
			user.setProviderData(this, mGraphUser);
			mManager.onSignedIn(user);
			mIsSigningIn = false;
		}
	}
}
