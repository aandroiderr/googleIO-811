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
