/*
 * Modified by Harsh Bhanvadia <bhanvadia@gmail.com>
 */

package com.harsh.steelprofile.databases;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.harsh.steelprofile.R;

public class DbAdapter {

	public static final String DATABASE_NAME = "steelprofile.db";
	
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
		db.execSQL("INSERT INTO Scripts (" + ScriptsDbAdapter.COLUMN_NAME + ") VALUES ('Ex_HEA200')");
		InsertCommand(db, 1, 0, "DIR 90");
		InsertCommand(db, 1, 1, "PT 10");
		InsertCommand(db, 1, 2, "FD 200");
		InsertCommand(db, 1, 3, "PU");
		InsertCommand(db, 1, 4, "BK 100");
		InsertCommand(db, 1, 5, "DIR 0");
		InsertCommand(db, 1, 6, "PD");
		InsertCommand(db, 1, 7, "PT 6.5");
		InsertCommand(db, 1, 8, "FD 190");
		InsertCommand(db, 1, 9, "DIR 90");
		InsertCommand(db, 1,10, "PU");
		InsertCommand(db, 1,11, "BK 100");
		InsertCommand(db, 1,12, "PD");
		InsertCommand(db, 1,13, "PT 10");
		InsertCommand(db, 1,14, "FD 200");
	}
	
	private static void InsertUExample(SQLiteDatabase db) {
		db.execSQL("INSERT INTO Scripts (" + ScriptsDbAdapter.COLUMN_NAME + ") VALUES ('Ex_U_Profile')");
		InsertCommand(db, 2, 0, "PT 5");
		InsertCommand(db, 2, 1, "BK 100");
		InsertCommand(db, 2, 2, "RT 90");
		InsertCommand(db, 2, 3, "PT 8");
		InsertCommand(db, 2, 4, "FD 50");
		InsertCommand(db, 2, 5, "LT 90");
		InsertCommand(db, 2, 6, "PT 5");
		InsertCommand(db, 2, 7, "FD 100");
	}
	
	private static void InsertCExample(SQLiteDatabase db) {
		db.execSQL("INSERT INTO Scripts (" + ScriptsDbAdapter.COLUMN_NAME + ") VALUES ('Ex_C180_Purlin')");
		InsertCommand(db, 3, 0, "PT 3");
		InsertCommand(db, 3, 1, "FD 20");
		InsertCommand(db, 3, 2, "LT 90");
		InsertCommand(db, 3, 3, "FD 60");
		InsertCommand(db, 3, 4, "LT 90");
		InsertCommand(db, 3, 5, "FD 180");
		InsertCommand(db, 3, 6, "LT 90");
		InsertCommand(db, 3, 7, "FD 60");
		InsertCommand(db, 3, 8, "LT 90");
		InsertCommand(db, 3, 9, "FD 20");
	}
	
	private static void InsertTExample(SQLiteDatabase db) {
		db.execSQL("INSERT INTO Scripts (" + ScriptsDbAdapter.COLUMN_NAME + ") VALUES ('Ex_Z140_Purlin')");
		InsertCommand(db, 4, 0, "DIR 45");
		InsertCommand(db, 4, 1, "PT 3");
		InsertCommand(db, 4, 2, "FD 20");
		InsertCommand(db, 4, 3, "RT 45");
		InsertCommand(db, 4, 4, "FD 50");
		InsertCommand(db, 4, 5, "RT 90");
		InsertCommand(db, 4, 6, "FD 140");
		InsertCommand(db, 4, 7, "LT 90");
		InsertCommand(db, 4, 8, "FD 54");
		InsertCommand(db, 4, 9, "DIR 45");
		InsertCommand(db, 4,10, "FD 20");
	}
	
	private static void InsertCommand(SQLiteDatabase db, int scriptId, int order, String command) {
		db.execSQL("INSERT INTO Commands (" +
				CommandsDbAdapter.COLUMN_SCRIPT_ID + "," +
				CommandsDbAdapter.COLUMN_ORDER + "," +
				CommandsDbAdapter.COLUMN_COMMAND + ") VALUES (" +
				scriptId + "," + order + ",'" + command + "')");
	}
}
