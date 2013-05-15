package com.example.signintest.providers;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;

import com.example.signintest.Provider;
import com.example.signintest.ProviderUtil;
import com.example.signintest.SignInFragment;
import com.example.signintest.SignInUser;
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
	private static final int GPLUS_REQUEST = 15124;
	public static final String ID = "googleplus";
	private SignInFragment mManager;
	private boolean mIsConnecting;
	private boolean mIsUserAction;
	
    private PlusClient mPlusClient;
    private ConnectionResult mLastConnectionResult;
    private int mRequestCode;
	
    public GooglePlusProvider() {
    	mRequestCode = INVALID_REQUEST_CODE;
    	mIsUserAction = false;
    }
    
	@Override
	public String getId() {
		return GooglePlusProvider.ID;
	}

	@Override
	public void setFragment(SignInFragment manager) {
		mManager = manager;
		PlusClient.Builder plusClientBuilder =
                new PlusClient.Builder(manager.getActivity().getApplicationContext(), this, this);
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
		mIsUserAction = true;
		if (mPlusClient.isConnected()) {
            refreshUser();
            return;
        }

        mRequestCode = GPLUS_REQUEST; 
        if (mLastConnectionResult == null) {
        	mPlusClient.connect();
            return;
        }

        resolveLastResult();
	}

	@Override
	public void signOut(SignInUser user) {
		if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
        }
	}

	@Override
	public void disconnect(SignInUser user) {
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
                mRequestCode = INVALID_REQUEST_CODE;
                break;
        }
        return true;
    }
	
	public boolean hasFeature(ProviderUtil.Feature feature) {
		// This is just an example! Obviously specific features will 
		// depend on the APIs implemented etc.
		switch(feature) {
		case GRAPH:
		case CALENDAR:
		case PROFILE:
		case FEATUREA:
			return true;
		default: 
			return false;
		}
	}
	
	@Override
	public String getUserId(Object user) {
		return ((Person)user).getId();
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
            mPlusClient.connect();
            mIsConnecting = true;
        }
    }
    
    private void refreshUser() {
    	if(mPlusClient.isConnected() && mIsUserAction) {
	    	Person person = mPlusClient.getCurrentPerson();
	    	SignInUser user = mManager.buildSignInUser();
	    	if(person == null) {
	    		// There was an error retrieving the Person object.
	    		return;
	    	}
	    	user.setName(person.getDisplayName());
	    	user.setProviderData(this, person);
	    	mManager.onSignedIn(user);
	    	mIsUserAction = false;
    	}
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
}
