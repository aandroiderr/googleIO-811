package com.example.signintest;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
	 * Tag used to identify fragments of this type.
	 */
	private static final String TAG_SIGNIN_FRAGMENT = "signInFragment";
	
	private static final String TAG_DIALOG = "accountStatusChange";
	
    // The current user. 
    // Declared static for reuse between activities.
    private static SignInUser mUser;
	
	/**
	 * HashMap used to store the providers registered.
	 */
	private HashMap<String,Provider> mProviders;
	
	private DBAdapter mDb;
	
    // A handler to post callbacks (rather than call them in a potentially reentrant way.)
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
    private final class SignInClientFragmentHandler extends Handler {
        public static final int WHAT_STATUS_CHANGE = 1;

        public SignInClientFragmentHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what != WHAT_STATUS_CHANGE) {
                return;
            }

            Activity activity = getActivity();
            if ( activity instanceof SignInStatusListener ) {
                ((SignInStatusListener) activity).onStatusChange(mUser);
            }
        }
    }
    
    public static final class MergeOrSwitchDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_merge_switch)
                   .setPositiveButton(R.string.do_merge, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // TODO: Merge the users
                       }
                   })
                   .setNegativeButton(R.string.do_switch, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // TODO: Switch user.
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
                           // TODO: Switch user.
                       }
                   })
                   .setNegativeButton(R.string.do_cancel, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // TODO: Drop the newly signed in user.
                       }
                   });
            return builder.create();
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
        // Retain instance to avoid reconnecting on rotate.  This means that onDestroy and onCreate
        // will not be called on configuration changes.
        setRetainInstance(true);
	}
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(mHandler == null) {
			mHandler = new SignInClientFragmentHandler();
			mDb = new DBAdapter(getActivity().getApplicationContext());
			mProviders = new HashMap<String,Provider>();
			addProviders(ProviderUtil.getProviders());
		}
		testProviders();
	}
	
	public void onDestroy() {
		for(Provider provider : mProviders.values()) {
			provider.detachFragment();
		}
		super.onDestroy();
	}
	
	public SignInUser buildSignInUser() {
		return new SignInUser(mDb);
	}
	
	public void addProviders(ArrayList<Provider> providers) {
		for (Provider provider : providers) {
			String pId = provider.getId();
			if (null != mProviders.get(pId)) {
				assert(false); // TODO: Just blow up for now, I'll think about this later.
			}
			mProviders.put(pId, provider);
			provider.setFragment(this);
		}
	}
	
	public void testProviders() {
		for (Provider provider : mProviders.values()) {
			provider.trySilentAuthentication();
		}
	}
	
	public void signIn(String provider) {
		mProviders.get(provider).signIn();
	}
	
	public void signOut(String provider) {
		Provider p = mProviders.get(provider);
		p.signOut(mUser.getProviderData(p));
	}
	
	public void disconnect(String provider) {
		Provider p = mProviders.get(provider);
		p.disconnect(mUser.getProviderData(p));
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
		} else if(mUser.getId() == user.getId()) {
			// If we have a new provider for the same user, just merge.
			for(Provider provider : user.listConnectedProviders()) {
				mUser.setProviderData(provider, user.getProviderData(provider));
			}
		} else {
			// If not we have two possible states: 
			if(mUser.canMerge(user)) {
				// We can automatically merge, but we should ask the user. 
				new MergeOrSwitchDialogFragment().show(getFragmentManager(), TAG_DIALOG);				
			} else {
				// The accounts conflict, so tell the user they can only
				// switch or cancel. 
				new SwitchOrCancelDialogFragment().show(getFragmentManager(), TAG_DIALOG);
			}
		}
		mHandler.sendEmptyMessage(SignInClientFragmentHandler.WHAT_STATUS_CHANGE);
	}
}
