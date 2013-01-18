/*
 * Copyright (C) 2012 Xatik Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harsh.steelprofile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListActivity;

public class ShareImageWithXatikActivity extends SherlockListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String action = intent.getAction();


		if (Intent.ACTION_SEND.equals(action)) {
			Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
			
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"harsh.slade@gmail.com"});
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Profile Share");
			emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
	    	
	    	startActivity(Intent.createChooser(emailIntent, "Email to Harsh..."));
    	}
		finish();
	}

}
