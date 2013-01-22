/*
 * Modified by Harsh Bhanvadia <bhanvadia@gmail.com>
 */

package com.harsh.steelprofile.databases;

import android.os.Parcel;
import android.os.Parcelable;

import com.harsh.steelprofile.*;

public class Command implements Parcelable {

	public long mId;
	public long mScriptId;
	public long mOrder;
	public String mCommandString;
	
	public long get_id() {
		return mId;
	}
	public void set_id(long id) {
		this.mId = id;
	}
	public long get_scriptId() {
		return mScriptId;
	}
	public void set_scriptId(long scriptId) {
		this.mScriptId = scriptId;
	}
	public long get_order() {
		return mOrder;
	}
	public void set_order(long order) {
		this.mOrder = order;
	}
	public String get_commandString() {
		return mCommandString;
	}
	public void set_commandString(String commandString) {
		this.mCommandString = commandString;
	}
	
	@Override
	public String toString() {
		return mCommandString;
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeLong(mScriptId);
		dest.writeLong(mOrder);
		dest.writeString(mCommandString);
	}
	
	public static final Parcelable.Creator<Command> CREATOR
			= new Parcelable.Creator<Command>() {
		public Command createFromParcel(Parcel in) {
			return new Command(in);
		}
		
		public Command[] newArray(int size) {
			return new Command[size];
		}
	};
	
	public Command() {}
	
	public Command(String command) {
		this.mCommandString = command;
	}
	
	private Command(Parcel in) {
		mId = in.readLong();
		mScriptId = in.readLong();
		mOrder = in.readLong();
		mCommandString = in.readString();
	}
	
	@SuppressWarnings("incomplete-switch")
	public static boolean Validate(String fullCommand) {
		boolean valid = false;
		String[] split = fullCommand.split(" ");
		
		KeypadButton kb = null;
		
		if (split.length == 1 || split.length == 2 || split.length == 3) {
			kb = KeypadButton.fromString(split[0]);
		}
		
		if (kb != null) {
			if(split.length == 1) {
				switch (kb) {
					case PU:
					case PD:
					case BRKT:
					case HOM:
						valid = true;
						break;
				}
				
			} else if (split.length == 2) {
				switch (kb) {
					case FD:
					case RPT:
					case LT:
					case BK:
					case RT:
					case PT:
					case DIR:
						try {
							Float.parseFloat(split[1]);
						/*	Integer.parseInt(split[1]); */
							valid = true;
						}
						catch (NumberFormatException ex) {
							// Command is invalid
						}
						break;
						
					case SPC:
						KeypadButton temp = KeypadButton.fromString(split[1]);
						if(temp != null) {
							switch(temp) {
							case WHT:
							case RED:
							case GRN:
							case BLU:
							case PUR:
							case YLW:
								valid = true;
								break;
							}
						}
				}
				if(split[1].length() > 5){
					valid = false;					
				}
			} else if (split.length == 3){
				switch (kb) {
				case FD:
				case RPT:
				case LT:
				case BK:
				case RT:
				case PT:
				case DIR:
					KeypadButton temp = KeypadButton.fromString(split[1]);
					if(temp != null) {
						switch(temp) {
						
						}
					}
					break;
				}
			}
		}
		
		return valid;
	}
}
