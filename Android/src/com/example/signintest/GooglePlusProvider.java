package com.example.signintest;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
import com.google.android.gms.plus.model.people.Person;

public class GooglePlusProvider implements 
	Provider, ConnectionCallbacks, OnConnectionFailedListener, OnAccessRevokedListener {

	private static final int INVALID_REQUEST_CODE = -1;
	public static final String ID = "googleplus";
	private SignInFragment mManager;
	private boolean mIsConnecting;
	
	// The PlusClient to connect.
    private PlusClient mPlusClient;

    // The last result from onConnectionFailed.
    private ConnectionResult mLastConnectionResult;

    // The request specified in signIn or INVALID_REQUEST_CODE if not signing in.
    private int mRequestCode;
	
	@Override
	public String getId() {
		return GooglePlusProvider.ID;
	}

	@Override
	public void setFragment(SignInFragment manager) {
		mManager = manager;
		PlusClient.Builder plusClientBuilder =
                new PlusClient.Builder(manager.getActivity().getApplicationContext(), this, this);
        // TODO: How do we configure visible activities. Seems to be passing them as an argument
		// in the fragment: plusClientBuilder.setVisibleActivities(visibleActivities);
        mPlusClient = plusClientBuilder.build();
	}
	
	public void detachFragment() {
		if (mIsConnecting || mPlusClient.isConnected()) {
            mPlusClient.disconnect();
        }
	}

	@Override
	public void trySilentAuthentication() {
		mPlusClient.connect();
	}

	@Override
	public void signIn() {
		if (mPlusClient.isConnected()) {
            refreshUser();
            return;
        }

        mRequestCode = 12345; // TODO: move to a static number somewhere
        if (mLastConnectionResult == null) {
            // We're starting up, show progress.
            // showProgressDialog();
            return;
        }

        resolveLastResult();
	}

	@Override
	public void signOut(Object user) {
		if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
        }
		// TODO: Handle the during connection issues.
	}

	@Override
	public void disconnect(Object user) {
		if (mPlusClient.isConnected()) {
            mPlusClient.revokeAccessAndDisconnect(this);
        }
	}
	
	public boolean handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != mRequestCode) {
            return false;
        }

        switch (resultCode) {
            case Activity.RESULT_OK:
                mLastConnectionResult = null;
                mPlusClient.connect();
                mIsConnecting = true;
                break;
            case Activity.RESULT_CANCELED:
                // User canceled sign in, clear the request code.
                mRequestCode = INVALID_REQUEST_CODE;
                break;
        }
        return true;
    }
	
    /**
     * Perform resolution given a non-null result.
     */
    private void resolveLastResult() {
        if (GooglePlayServicesUtil.isUserRecoverableError(mLastConnectionResult.getErrorCode())) {
            // No Google Play services.
            return;
        }

        if (mLastConnectionResult.hasResolution()) {
            startResolution();
        }
    }
    
    private void startResolution() {
        try {
            mLastConnectionResult.startResolutionForResult(mManager.getActivity(), mRequestCode);
        } catch (SendIntentException e) {
            // The intent we had is not valid right now, perhaps the remote process died.
            // Try to reconnect to get a new resolution intent.
            mLastConnectionResult = null;
            // TODO: Show progress dialog.
            mPlusClient.connect();
            mIsConnecting = true;
        }
    }
    
    private void refreshUser() {
    	Person person = mPlusClient.getCurrentPerson();
    	SignInUser user = mManager.buildSignInUser();
    	user.setName(person.getDisplayName());
    	user.setProviderData(this, person);
    	mManager.onSignedIn(user);
    }
	
	/* Callbacks for the Google+ Client */

	@Override
	public void onAccessRevoked(ConnectionResult connectionResult) {
		 // Reconnect to get a new mPlusClient.
        mLastConnectionResult = null;
        // Cancel sign in.
        mRequestCode = INVALID_REQUEST_CODE;

        // Reconnect to fetch the sign-in (account chooser) intent from the plus client.
        mPlusClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		mLastConnectionResult = connectionResult;
        mIsConnecting = false;
        // On a failed connection try again.
        if (mManager.isResumed() && mRequestCode != INVALID_REQUEST_CODE) {
            resolveLastResult();
        }
		
	}

	@Override
	public void onConnected() {
		//mLastConnectionResult = CONNECTION_RESULT_SUCCESS;
        mRequestCode = INVALID_REQUEST_CODE;
        mIsConnecting = false;
        // hideProgressDialog();
        refreshUser();
	}

	@Override
	public void onDisconnected() {
		mIsConnecting = false;
	}

	@Override
	public String getUserId(Object user) {
		return ((Person)user).getId();
	}
}
