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

package com.harsh.steelprofile.databases;

import android.os.Parcel;
import android.os.Parcelable;

public class Script implements Parcelable {
	public long mId;
	
	public String mName;

	public long get_id() {
		return mId;
	}

	public void set_id(long id) {
		this.mId = id;
	}

	public String get_name() {
		return mName;
	}

	public void set_name(String name) {
		this.mName = name;
	}
	
	@Override
	public String toString() {
		return mName;
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(mName);
	}
	
	public static final Parcelable.Creator<Script> CREATOR
			= new Parcelable.Creator<Script>() {
		public Script createFromParcel(Parcel in) {
			return new Script(in);
		}
		
		public Script[] newArray(int size) {
			return new Script[size];
		}
	};
	
	public Script() {}
	
	private Script(Parcel in) {
		mId = in.readLong();
		mName = in.readString();
	}
}
