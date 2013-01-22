/*
 * Copyright (C) 2013 Harsh Bhanvadia <bhanvadia@gmail.com>
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.harsh.steelprofile.R;

public class ScriptsDbAdapter {
	public static final String DATABASE_TABLE = "Scripts";
	public static final String SCRIPT = "Script";
	public static final String COLUMN_NAME = "Name";
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private final Context mCtx;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DbAdapter.DATABASE_NAME, null, 
					context.getResources().getInteger(R.integer.database_version));
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) { }
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
	}
	
	public ScriptsDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}
	
	public ScriptsDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDb.close();
		mDbHelper.close();
	}
	
	public Script createScript() {
		return createScript("Script");
	}
	
	public Script createScript(String name) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COLUMN_NAME, name);
		
		long insertId = mDb.insert(DATABASE_TABLE, null, initialValues);
		
		return fetchScript(insertId);
	}
	
	public boolean deleteScript(long rowId) {
		return mDb.delete(DATABASE_TABLE, DbAdapter.COLUMN_ID + "=" + rowId, null) > 0;
	}
	
	public List<Script> fetchAllScripts() {
		List<Script> scripts = new ArrayList<Script>();
		
		Cursor cursor = mDb.query(DATABASE_TABLE, new String[] {DbAdapter.COLUMN_ID, COLUMN_NAME }, 
				null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			Script script = cursorToScript(cursor);
			scripts.add(script);
			cursor.moveToNext();
		}
		cursor.close();
		return scripts;
	}
	
	public Script fetchScript(long rowId) throws SQLException {
		Script script = null;
		
		Cursor cursor = mDb.query(true, DATABASE_TABLE, new String[] {DbAdapter.COLUMN_ID, COLUMN_NAME },
				DbAdapter.COLUMN_ID + "=" + rowId, null,
				null, null, null, null);
		
		if(cursor != null) {
			cursor.moveToFirst();
			script = cursorToScript(cursor);
		}
		
		cursor.close();
		
		return script;
	}
	
	public boolean renameScript(long rowId, String newName) throws SQLException {
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, newName);
		
		return mDb.update(DATABASE_TABLE, values, "_id=?", new String[] { rowId + "" }) > 0;
	}
	
	private Script cursorToScript(Cursor cursor) {
		Script script = new Script();
		script.set_id(cursor.getLong(0));
		script.set_name(cursor.getString(1));
		return script;
	}
	
	public boolean scriptExist(String name) {
		boolean result = false;
		
		Cursor cursor = mDb.query(true, DATABASE_TABLE, new String[] {DbAdapter.COLUMN_ID, COLUMN_NAME },
				COLUMN_NAME + "='" + name + "'", null,
				null, null, null, null);
		
		if(cursor != null) {
			result = cursor.moveToFirst();
		}
		
		cursor.close();
		
		return result;
	}
	
	public boolean scriptNameIsValid(String name) {
		if(name == null || name.equals("")) {
			return false;
		}
		Pattern pat = Pattern.compile("[^a-zA-Z0-9-_]");
		Matcher mat = pat.matcher(name);
		
		return !mat.find();
	}
}
