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

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class KeypadAdapter extends BaseAdapter {
	private Context mContext;
	private OnClickListener mOnButtonClick;
	public final static String SCREEN_ORIENTATION = "Orientation";
	
	public KeypadAdapter(Context c) {
		mContext = c;
	}
	
	public void setOnButtonClickListener(OnClickListener listener) {
		mOnButtonClick = listener;
	}

	public int getCount() {
		int orientation = KeypadActivity.getScreenOrientation();
		
		if(orientation ==  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
			return mButtons.length;
		else
			return mButtons_land.length;
	}

	public Object getItem(int position) {
		int orientation = KeypadActivity.getScreenOrientation();
		
		if(orientation ==  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
			return mButtons[position];
		else
			return mButtons_land[position];
	}

	public long getItemId(int position) {
		return 0;
	}

	/* create a new ButtonView for each item referenced by the Adapter */
	public View getView(int position, View convertView, ViewGroup parent) {
		Button btn;
		KeypadButton[] buttons;
		int orientation = KeypadActivity.getScreenOrientation();
		
		if(orientation ==  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
			buttons = mButtons;
		else
			buttons = mButtons_land;
		
		if (convertView == null) {
			btn = new Button(mContext);
			KeypadButton keypadButton = buttons[position];
			
			switch(keypadButton.mCategory)
			{
			case COMMANDS:
				btn.setBackgroundResource(R.drawable.cmdbuttons);
				break;	
			case DIRECTION:
				btn.setBackgroundResource(R.drawable.directionbuttons);
				break;	
			case CLEAR:
				btn.setBackgroundResource(R.drawable.cmdbuttons);
				break;	
			case NUMBER:
				btn.setBackgroundResource(R.drawable.numbuttons);
				break;
			case WHITE:
				btn.setBackgroundResource(R.drawable.whtbutton);
				break;
			case RED:
				btn.setBackgroundResource(R.drawable.redbutton);
				break;
			case GREEN:
				btn.setBackgroundResource(R.drawable.greenbutton);
				break;
			case BLUE:
				btn.setBackgroundResource(R.drawable.bluebutton);
				break;
			case PURPLE:
				btn.setBackgroundResource(R.drawable.purplebutton);
				break;
			case YELLOW:
				btn.setBackgroundResource(R.drawable.yellowbutton);
				break;
			case OTHER:
				btn.setBackgroundResource(R.drawable.cmdbuttons);
				break;
			case DUMMY:
				btn.setBackgroundResource(R.drawable.dummybuttons);
				break;
			default:
				btn.setBackgroundResource(R.drawable.cmdbuttons);
				break;
			}
			
			/* if dummy, don't make clickable */
			if(keypadButton != KeypadButton.DUMMY)
				btn.setOnClickListener(mOnButtonClick);
			else
				btn.setClickable(false);
			
			btn.setTag(keypadButton);
		} else {
			btn = (Button) convertView;
		}
        
		
		switch( buttons[position] ){
		case BKSP:
			btn.setBackgroundResource(R.drawable.bkspacebutton);
			btn.setText("");
			break;
		case WHT:
		case RED:
		case GRN:
		case BLU:
		case PUR:
		case YLW:
			btn.setText("");
			break;
		default:
			btn.setText(buttons[position].getText());
			btn.setTextSize(14);
			btn.setTextColor(Color.WHITE);
		}
		
		return btn;
	}

	/* Create and populate keypad buttons array for portrait with KeypadButton enum values, laid out how it will be viewed */
	private KeypadButton[] mButtons = 
	{       KeypadButton.DUMMY, KeypadButton.DUMMY, KeypadButton.HOM,   KeypadButton.DIR,   KeypadButton.DUMMY,
			KeypadButton.PU,    KeypadButton.FD,    KeypadButton.PD,    KeypadButton.RPT,   KeypadButton.BRKT, 
			KeypadButton.LT,    KeypadButton.BK,    KeypadButton.RT,    KeypadButton.PT,    KeypadButton.SPC, 
			KeypadButton.SEVEN, KeypadButton.EIGHT, KeypadButton.NINE,  KeypadButton.WHT,   KeypadButton.RED, 
			KeypadButton.FOUR,  KeypadButton.FIVE,  KeypadButton.SIX,   KeypadButton.GRN,   KeypadButton.BLU,
			KeypadButton.ONE,   KeypadButton.TWO,   KeypadButton.THREE, KeypadButton.PUR,   KeypadButton.YLW, 
			KeypadButton.DUMMY, KeypadButton.ZERO,  KeypadButton.DUMMY, KeypadButton.BKSP,  KeypadButton.CLR };
	
	/* Create and populate keypad buttons array for landscape with KeypadButton enum values, laid out how it will be viewed */
	private KeypadButton[] mButtons_land = 
	{       KeypadButton.SEVEN, KeypadButton.EIGHT, KeypadButton.NINE,   KeypadButton.RPT,  KeypadButton.BRKT, KeypadButton.PT,  KeypadButton.SPC,   KeypadButton.WHT,  KeypadButton.RED,
			KeypadButton.FOUR,  KeypadButton.FIVE,  KeypadButton.SIX,    KeypadButton.HOM,  KeypadButton.DIR,  KeypadButton.DUMMY, KeypadButton.DUMMY,    KeypadButton.GRN,  KeypadButton.BLU, 
			KeypadButton.ONE,   KeypadButton.TWO,   KeypadButton.THREE,  KeypadButton.PU,   KeypadButton.FD,   KeypadButton.PD,  KeypadButton.DUMMY,    KeypadButton.PUR,  KeypadButton.YLW, 
			KeypadButton.DUMMY, KeypadButton.ZERO,  KeypadButton.DUMMY,  KeypadButton.LT,   KeypadButton.BK,   KeypadButton.RT,  KeypadButton.DUMMY, KeypadButton.BKSP, KeypadButton.CLR };

}
