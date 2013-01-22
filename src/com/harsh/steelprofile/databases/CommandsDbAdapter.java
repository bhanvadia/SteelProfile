/*
 * Modified by Harsh Bhanvadia <bhanvadia@gmail.com>
 */

package com.harsh.steelprofile.databases;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.harsh.steelprofile.R;

public class CommandsDbAdapter {
	public static final String DATABASE_TABLE = "Commands";
	
	public static final String COLUMN_SCRIPT_ID = "ScriptId";
	public static final String COLUMN_ORDER = "Position";
	public static final String COLUMN_COMMAND = "Command";
	
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
	
	public CommandsDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}
	
	public CommandsDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDb.close();
		mDbHelper.close();
	}
	
	public void insertCommand(long scriptId, long order, String command) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COLUMN_SCRIPT_ID, scriptId);
		initialValues.put(COLUMN_ORDER, order);
		initialValues.put(COLUMN_COMMAND, command);

		@SuppressWarnings("unused")
		long insertId = mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public List<Command> fetchCommandsForScript(long scriptId) {
		List<Command> commands = new ArrayList<Command>();
		
		Cursor cursor = mDb.query(DATABASE_TABLE, new String[] {DbAdapter.COLUMN_ID, COLUMN_SCRIPT_ID, COLUMN_ORDER, COLUMN_COMMAND}, 
				COLUMN_SCRIPT_ID + " = " + scriptId, null, null, null, COLUMN_ORDER);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			Command command = cursorToCommand(cursor);
			commands.add(command);
			cursor.moveToNext();
		}
		cursor.close();
		return commands;
	}
	
	public boolean deleteCommandsForScript(long scriptId) {
		return mDb.delete(DATABASE_TABLE, COLUMN_SCRIPT_ID + "=" + scriptId, null) > 0;
	}
	
	public boolean deleteCommand(long commandId) {
		return mDb.delete(DATABASE_TABLE, DbAdapter.COLUMN_ID + "=" + commandId, null) > 0;
	}
	
	public boolean updateCommandPosition(long id, long newOrder) {
		ContentValues args = new ContentValues();
		args.put(COLUMN_ORDER, newOrder);
		return mDb.update(DATABASE_TABLE, args, 
				DbAdapter.COLUMN_ID + " = " + id, null) > 0;
	}
	
	public boolean updateCommandPosition(long scriptId, long oldOrder, long newOrder) {
		ContentValues args = new ContentValues();
		args.put(COLUMN_ORDER, newOrder);
		return mDb.update(DATABASE_TABLE, args, 
				COLUMN_SCRIPT_ID + " = " + scriptId + " AND " +
				COLUMN_ORDER + " = " + oldOrder, null) > 0;
	}
	
	public boolean updateCommandString(long scriptId, long order, String commandString) {
		ContentValues args = new ContentValues();
		args.put(COLUMN_COMMAND, commandString);
		return mDb.update(DATABASE_TABLE, args, 
				COLUMN_SCRIPT_ID + " = " + scriptId + " AND " +
				COLUMN_ORDER + " = " + order, null) > 0;
	}
	
	private Command cursorToCommand(Cursor cursor) {
		Command command = new Command();
		command.set_id(cursor.getLong(0));
		command.set_scriptId(cursor.getLong(1));
		command.set_order(cursor.getInt(2));
		command.set_commandString(cursor.getString(3));
		return command;
	}
}
