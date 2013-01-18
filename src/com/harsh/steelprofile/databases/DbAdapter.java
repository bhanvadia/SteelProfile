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

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.harsh.steelprofile.R;

public class DbAdapter {

	public static final String DATABASE_NAME = "droiddraw.db";
	
	public static final String COLUMN_ID = "_id";
	
	private final Context mContext;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static final String CREATE_TABLE_SCRIPTS =
			"CREATE TABLE IF NOT EXISTS Scripts(" +
					COLUMN_ID + " INTEGER PRIMARY KEY autoincrement, " +
					ScriptsDbAdapter.COLUMN_NAME + " text not null default 'Script');";
	
	private static final String CREATE_TABLE_COMMANDS =
			"CREATE TABLE IF NOT EXISTS Commands(" +
					COLUMN_ID + " INTEGER PRIMARY KEY autoincrement, " +
					CommandsDbAdapter.COLUMN_SCRIPT_ID + " INTEGER not null, " +
					CommandsDbAdapter.COLUMN_ORDER + " INTEGER not null, " +
					CommandsDbAdapter.COLUMN_COMMAND + " text not null);";
					
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, 
					context.getResources().getInteger(R.integer.database_version));
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_SCRIPTS);
			db.execSQL(CREATE_TABLE_COMMANDS);
			InsertHExample(db);
			InsertUExample(db);
			InsertCExample(db);
			InsertTExample(db);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS Scripts");
			db.execSQL("DROP TABLE IF EXISTS Commands");
			onCreate(db);
		}
	}
	
	public DbAdapter(Context ctx) {
		this.mContext = ctx;
	}
	
	public DbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mContext);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDb.close();
		mDbHelper.close();
	}
	
	private static void InsertHExample(SQLiteDatabase db) {
		db.execSQL("INSERT INTO Scripts (" + ScriptsDbAdapter.COLUMN_NAME + ") VALUES ('Ex_H_Profile')");
		InsertCommand(db, 1, 0, "DIR 90");
		InsertCommand(db, 1, 1, "FD 100");
		InsertCommand(db, 1, 2, "PU");
		InsertCommand(db, 1, 3, "BK 50");
		InsertCommand(db, 1, 4, "DIR 0");
		InsertCommand(db, 1, 5, "PD");
		InsertCommand(db, 1, 6, "FD 150");
		InsertCommand(db, 1, 7, "DIR 90");
		InsertCommand(db, 1, 8, "PU");
		InsertCommand(db, 1, 9, "BK 50");
		InsertCommand(db, 1,10, "PD");
		InsertCommand(db, 1,11, "FD 100");
	}
	
	private static void InsertUExample(SQLiteDatabase db) {
		db.execSQL("INSERT INTO Scripts (" + ScriptsDbAdapter.COLUMN_NAME + ") VALUES ('Ex_U_Profile')");
		InsertCommand(db, 2, 0, "BK 100");
		InsertCommand(db, 2, 1, "RT 90");
		InsertCommand(db, 2, 2, "FD 50");
		InsertCommand(db, 2, 3, "LT 90");
		InsertCommand(db, 2, 4, "FD 100");
	}
	
	private static void InsertCExample(SQLiteDatabase db) {
		db.execSQL("INSERT INTO Scripts (" + ScriptsDbAdapter.COLUMN_NAME + ") VALUES ('Ex_C_Profile')");
		InsertCommand(db, 3, 0, "LT 90");
		InsertCommand(db, 3, 1, "FD 50");
		InsertCommand(db, 3, 2, "RT 90");
		InsertCommand(db, 3, 3, "FD 100");
		InsertCommand(db, 3, 4, "RT 90");
		InsertCommand(db, 3, 5, "FD 50");
	}
	
	private static void InsertTExample(SQLiteDatabase db) {
		db.execSQL("INSERT INTO Scripts (" + ScriptsDbAdapter.COLUMN_NAME + ") VALUES ('Ex_T_Profile')");
		InsertCommand(db, 4, 0, "FD 150");
		InsertCommand(db, 4, 1, "RT 90");
		InsertCommand(db, 4, 2, "FD 35");
		InsertCommand(db, 4, 3, "PU");
		InsertCommand(db, 4, 4, "BK 35");
		InsertCommand(db, 4, 5, "PD");
		InsertCommand(db, 4, 6, "BK 35");
	}
	
	private static void InsertCommand(SQLiteDatabase db, int scriptId, int order, String command) {
		db.execSQL("INSERT INTO Commands (" +
				CommandsDbAdapter.COLUMN_SCRIPT_ID + "," +
				CommandsDbAdapter.COLUMN_ORDER + "," +
				CommandsDbAdapter.COLUMN_COMMAND + ") VALUES (" +
				scriptId + "," + order + ",'" + command + "')");
	}
}
