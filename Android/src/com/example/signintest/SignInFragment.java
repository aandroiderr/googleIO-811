package com.example.signintest;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class SignInFragment extends Fragment {
	
	/**
	 * Tags used to identify fragments of this type.
	 */
	private static final String TAG_SIGNIN_FRAGMENT = "signInFragment";
	private static final int TAG_DIALOG_REQ = 5739;
	private static final String TAG_DIALOG = "accountStatusChange";
	
    /**
     * The current user - declared static for simple sharing between activities.
     */
    private static SignInUser mUser;
   
    private static SignInUser mIncomingUser;
	
	/**
	 * HashMap used to store the providers registered.
	 */
	private HashMap<String,Provider> mProviders;
	
	private DBAdapter mDb;
	
	/**
	 * Local storage of a passed in intent, in case we are called before the providers
	 * are initialised.
	 */
	private Intent mPendingIntent;
	private int mPendingRequestCode;
	private int mPendingResultCode;
	
    private Handler mHandler;

	/**
     * Listener interface for sign in events.  Activities hosting a SignInFragment
     * must implement this.
     */
    public static interface SignInStatusListener {
        /**
         * Called when a provider has changed status.
         *
         * @param SignInUser user - the updated version of the user object.
         */
        void onStatusChange(SignInUser user);
    }
    
    /**
     * Local handler to send callbacks on sign in.
     */
    private static final class SignInClientFragmentHandler extends Handler {
        public static final int WHAT_STATUS_CHANGE = 1;
        private final WeakReference<SignInFragment> mFragment;

        public SignInClientFragmentHandler(SignInFragment parent) {
        	super(Looper.getMainLooper());
        	mFragment = new WeakReference<SignInFragment>(parent);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what != WHAT_STATUS_CHANGE || mFragment.get() == null) {
                return;
            }

            Activity activity = mFragment.get().getActivity();
            if ( activity instanceof SignInStatusListener ) {
                ((SignInStatusListener) activity).onStatusChange(mUser);
            }
        }
    }
    
	/**
	 * Attach a SignInFragment to manage authentication in your activity.
	 * 
	 * @param activity The activity to attach the fragment to
	 * @return
	 */
	public static SignInFragment getSignInFragment( 
			FragmentActivity activity) {
		// Check if the fragment is already attached.
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(TAG_SIGNIN_FRAGMENT);
		if(fragment instanceof SignInFragment) {
			return (SignInFragment) fragment;
		}
		
		SignInFragment signInFragment = new SignInFragment();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(signInFragment, TAG_SIGNIN_FRAGMENT);
		fragmentTransaction.commit();
		return signInFragment;
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
	}	
	
	public void onDestroy() {
		for(Provider provider : mProviders.values()) {
			provider.detachFragment();
		}	
		super.onDestroy();
	}		
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mHandler = new SignInClientFragmentHandler(this);
		mDb = new DBAdapter(getActivity().getApplicationContext());
		mDb.open();
		mProviders = new HashMap<String,Provider>();
		addProviders(ProviderUtil.getProviders());
	}		
	
	/**
	 * Return a sign in user built with the current database, 
	 * to avoid reinitialising the DB unnecessarily. 
	 * 
	 * @return SignInUser a new user.
	 */
	public SignInUser buildSignInUser() {
		return new SignInUser(mDb);
	}
	
	public void addProviders(ArrayList<Provider> providers) {
		for (Provider provider : providers) {
			String pId = provider.getId();
			if (null != mProviders.get(pId)) {
				assert(false); // Just blow up for now, but of course we should handle this!
			}
			mProviders.put(pId, provider);
			provider.setFragment(this);
		}
		testProviders();
	}
	
	public String getRoutingKey() {
		return getActivity().getLocalClassName().replace("Activity", "").toLowerCase(Locale.ENGLISH);
	}
	 	
	public void signIn(String provider) {
		mProviders.get(provider).signIn();
	}
	
	public void signOut(String provider) {
		Provider p = mProviders.get(provider);
		p.signOut(mUser);
	}
	
	public void disconnect(String provider) {
		Provider p = mProviders.get(provider);
		p.disconnect(mUser);
		mUser.removeProvider(p);
		mHandler.sendEmptyMessage(SignInClientFragmentHandler.WHAT_STATUS_CHANGE);
	}
	
	public SignInUser getUser() {
		return mUser;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(mProviders == null) {
			// If we're not ready, just hold on to the intent until we're done.
			mPendingIntent = data;
			mPendingRequestCode = requestCode;
			mPendingResultCode = resultCode;
			return;
		}
	    for(Provider provider : mProviders.values()) {
	    	if( provider.handleOnActivityResult(requestCode, resultCode, data) ) {
	    		return;
	    	}
	    }
	}
	
	/**
	 * Called to connect providers to establish provider state
	 * where possible.
	 */
	private void testProviders() {
		if (mPendingIntent != null) {
			onActivityResult(mPendingRequestCode, mPendingResultCode, mPendingIntent);
			mPendingIntent = null;
		}
		for (Provider provider : mProviders.values()) {
			provider.trySilentAuthentication();
		}
	}
	
	/**
	 * Called on sign in with a new user to ensure that we have the
	 * right providers automatically connected.
	 * 
	 */
	private void resolveUserStates() {
		for(String provider : mUser.listAdditionalProviders()) {
			signIn(provider);
		}
	}
	
	/**
	 * Called when the provider has signed in the user. 
	 * Merging users is always handled here. 
	 * 
	 * @param user
	 */
	public void onSignedIn(SignInUser user) {
		if (mUser == null) {
			// If we have no user, we just take the supplied user.
			mUser = user;
			mHandler.sendEmptyMessage(SignInClientFragmentHandler.WHAT_STATUS_CHANGE);
			resolveUserStates();
		} else if(mUser.getId() == user.getId() || user.isNew()) {
			// If we have a new provider for the same user, just merge.
			mUser.merge(user);
			mHandler.sendEmptyMessage(SignInClientFragmentHandler.WHAT_STATUS_CHANGE);
		} else {
			mIncomingUser = user;
			// If not we have two possible states: 
			DialogFragment f;
			if(mUser.canMerge(user)) {
				// We can automatically merge, but we should ask the user. 
				f = new MergeOrSwitchDialogFragment();					
			} else {
				// The accounts conflict, so tell the user they can only
				// switch or cancel. 
				f = new SwitchOrCancelDialogFragment();
			}
			f.setTargetFragment(this, TAG_DIALOG_REQ);
			f.show(getFragmentManager(), TAG_DIALOG);
		}
	}
	
	/*
	 * User Merging/Switching Logic
	 */
	
	public static final class MergeOrSwitchDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_merge_switch)
                   .setPositiveButton(R.string.do_merge, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   ((SignInFragment)getTargetFragment()).mergeUsers();
                       }
                   })
                   .setNegativeButton(R.string.do_switch, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   ((SignInFragment)getTargetFragment()).switchUsers();
                       }
                   });
            return builder.create();
        }
    }
    
    public static final class SwitchOrCancelDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_switch_cancel)
                   .setPositiveButton(R.string.do_switch, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   ((SignInFragment)getTargetFragment()).switchUsers();
                       }
                   })
                   .setNegativeButton(R.string.do_cancel, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   ((SignInFragment)getTargetFragment()).cancelSwitch();
                       }
                   });
            return builder.create();
        }
    }
	
	private void switchUsers() {
    	mUser = mIncomingUser;
        mIncomingUser = null;
        mHandler.sendEmptyMessage(SignInClientFragmentHandler.WHAT_STATUS_CHANGE);
    }
    
    private void mergeUsers() {
    	mUser.merge(mIncomingUser);
 	   	mIncomingUser = null;
 	   	mHandler.sendEmptyMessage(SignInClientFragmentHandler.WHAT_STATUS_CHANGE);
    }
    
    private void cancelSwitch() {
    	mIncomingUser = null;
    }
}
