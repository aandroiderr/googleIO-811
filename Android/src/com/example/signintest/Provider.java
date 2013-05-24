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

import android.content.Intent;

/**
 * Interface for an individual identity provider 
 */
public interface Provider {
	public void setFragment(SignInFragment manager);
	public void detachFragment();
	public boolean handleOnActivityResult(int requestCode, int resultCode, Intent data);
	
	public String getId();
	public String getUserId(Object user);
	public boolean hasFeature(ProviderUtil.Feature feature);
	
	public void trySilentAuthentication();
	public void signIn();
	public void signOut(SignInUser user);
	public void disconnect(SignInUser user);
	
	
}
