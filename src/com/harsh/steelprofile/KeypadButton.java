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

public enum KeypadButton {
	PU("PU",KeypadButtonCategory.COMMANDS)
	, FD("FD",KeypadButtonCategory.DIRECTION)
	, PD("PD",KeypadButtonCategory.COMMANDS)
	, RPT("RPT",KeypadButtonCategory.COMMANDS)
	, SPC("SPC",KeypadButtonCategory.COMMANDS)
	, LT("LT",KeypadButtonCategory.DIRECTION)
	, BK("BK",KeypadButtonCategory.DIRECTION)
	, RT("RT",KeypadButtonCategory.DIRECTION)
	, MAC("MAC",KeypadButtonCategory.COMMANDS)
	, ZERO("0",KeypadButtonCategory.NUMBER)
	, ONE("1",KeypadButtonCategory.NUMBER)
	, TWO("2",KeypadButtonCategory.NUMBER)
	, THREE("3",KeypadButtonCategory.NUMBER)
	, FOUR("4",KeypadButtonCategory.NUMBER)
	, FIVE("5",KeypadButtonCategory.NUMBER)
	, SIX("6",KeypadButtonCategory.NUMBER)
	, SEVEN("7",KeypadButtonCategory.NUMBER)
	, EIGHT("8",KeypadButtonCategory.NUMBER)
	, NINE("9",KeypadButtonCategory.NUMBER)
	, PUR("PUR",KeypadButtonCategory.PURPLE)
	, GRN("GRN",KeypadButtonCategory.GREEN)
	, WHT("WHT",KeypadButtonCategory.WHITE)
	, BLU("BLU",KeypadButtonCategory.BLUE)
	, YLW("YLW",KeypadButtonCategory.YELLOW)
	, RED("RED",KeypadButtonCategory.RED)
	, CLR("CLR",KeypadButtonCategory.CLEAR)
	, BRKT("]",KeypadButtonCategory.COMMANDS)
	, POS("POS",KeypadButtonCategory.COMMANDS)
	, HOM("HOM",KeypadButtonCategory.COMMANDS)
	, WT("WT",KeypadButtonCategory.COMMANDS)
	, PT("PT",KeypadButtonCategory.COMMANDS)
	, DIR("DIR",KeypadButtonCategory.COMMANDS)
	, BKSP("BKSP", KeypadButtonCategory.BACKSPACE)
	, DUMMY("",KeypadButtonCategory.DUMMY);

	CharSequence mText; // Display Text
	KeypadButtonCategory mCategory;
	
	KeypadButton(CharSequence text,KeypadButtonCategory category) {
		mText = text;
		mCategory = category;
	}

	public CharSequence getText() {
		return mText;
	}
	
	public static KeypadButton fromString(String text) {
		if (text != null) {
			for(KeypadButton kb : KeypadButton.values()) {
				if (text.equalsIgnoreCase(kb.getText().toString())) {
					return kb;
				}
			}
		}
		return null;
	}
}

