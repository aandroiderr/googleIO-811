package com.example.signintest;

import java.util.ArrayList;

import com.example.signintest.providers.FacebookProvider;
import com.example.signintest.providers.GooglePlusProvider;
import com.example.signintest.providers.LinkedinProvider;

public class ProviderUtil {
	
	/**
	 * Tag for external callbacks.
	 */
	public final static int WEBCALLBACK = 38910;
	
	/**
     * A list of the Providers we are using
     */
    private static ArrayList<Provider> mProviders;

    public static ArrayList<Provider> getProviders() {
    	if(mProviders == null) {
    		mProviders = new ArrayList<Provider>();
    		mProviders.add(new GooglePlusProvider());
    		mProviders.add(new FacebookProvider());
    		mProviders.add(new LinkedinProvider());
    	}
    	return mProviders;
    }
    
    /**
     * Application specific features, which may be provided by
     * individual IDPs.
     */
    public static enum Feature {
    	CALENDAR, PROFILE, GRAPH, FEATUREA, FEATUREB
    }
}
