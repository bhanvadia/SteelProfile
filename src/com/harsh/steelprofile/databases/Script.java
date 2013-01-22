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
