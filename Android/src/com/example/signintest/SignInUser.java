package com.example.signintest;

import java.util.HashMap;
import java.util.Set;

public class SignInUser {

	/**
	 * HashMap used to store the providers registered.
	 */
	private HashMap<Provider,Object> mProviderData;
	
	private String mName;
	private Integer mId;
	private boolean mIsNew;
	private DBAdapter mDb;
	
	public SignInUser(DBAdapter db) {
		mDb = db;
		mDb.open();
		mProviderData = new HashMap<Provider,Object>();
	}
	
    protected void finalize( ) throws Throwable {
    	mDb.close();
	}
	
	public void setProviderData(Provider provider, Object user) {
		mProviderData.put(provider, user);
		if (mId == null) {
			mId = mDb.getUserId(provider, getProviderUserId(provider));
			if(mId == null) {
				// There is no user in the DB, so create one.
				mDb.createUser(provider, this);
				mIsNew = true;
			} else {
				mIsNew = false;
			}
		}	
	}
	
	public void removeProvider(Provider provider) {
		mDb.deleteProviderUser(provider, this);
		mProviderData.remove(provider);
	}
	
	public Object getProviderData(Provider provider) {
		return mProviderData.get(provider);
	}
	
	public boolean isSignedIn() {
		return !mProviderData.isEmpty();
	}
	
	public boolean isNew() {
		return mIsNew;
	}

	public void setName(String name) {
		mName = name;
	}
	
	public String getName() {
		return mName;
	}
	
	public Integer getId() {
		// return the local identifier. 
		return mId;
	}
	
	public String getProviderUserId(Provider provider) {
		return provider.getUserId(getProviderData(provider));
	}
	
	public boolean hasFeature(ProviderUtil.Feature feature) {
		for(Provider p : mProviderData.keySet()) {
			if (p.hasFeature(feature)) {
				return true;
			}
		}
		return false;
	}

	public boolean canMerge(SignInUser user) {
		// TODO: We should look for overlap rather than just single provider
		// id Match.
		return mId == user.getId();
	}

	public Set<Provider> listConnectedProviders() {
		return mProviderData.keySet();
	}

}
