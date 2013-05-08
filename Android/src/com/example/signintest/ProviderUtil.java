package com.example.signintest;

import java.util.ArrayList;

public class ProviderUtil {
	/**
     * A list of the Providers we are using
     */
    private static ArrayList<Provider> mProviders;

    public static ArrayList<Provider> getProviders() {
    	if(mProviders == null) {
    		mProviders = new ArrayList<Provider>(2);
    		mProviders.add(new GooglePlusProvider());
    		mProviders.add(new FacebookProvider());
    	}
    	return mProviders;
    }
    
    public static enum Feature {
    	CALENDAR, INTERESTS, PROFILE, GRAPH
    }
}
