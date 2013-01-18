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

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.harsh.steelprofile.TouchListView;
import com.harsh.steelprofile.databases.Command;
import com.harsh.steelprofile.databases.CommandsDbAdapter;
import com.harsh.steelprofile.databases.Script;
import com.harsh.steelprofile.databases.ScriptsDbAdapter;
import com.harsh.steelprofile.common.ImportExport;


public class CommandsActivity extends SherlockListActivity {
	private static final int INSERT_ABOVE = 0;
	private static final int INSERT_BELOW = 1;
	private static final int INSERT = 2;
	private static final int EDIT = 3;
	
	private ScriptsDbAdapter mScriptsDbAdapter;
	private CommandsDbAdapter mCommandsDbAdapter;
	private Script mScript;
	
	private ArrayList<Command> mCommands;
	
	private boolean mAutoInsertBrkt;
	private boolean mAutoInsertBelow;
	private boolean mAnimate;
	
	private boolean mFirstLoad = true;
	
	private IconicAdapter mAdapter = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        
        final FrameLayout frame = new FrameLayout(this);
        frame.addView(LayoutInflater.from(getBaseContext()).inflate(R.layout.commands, null));
        
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean showTutorial = sp.getBoolean(getResources().getString(R.string.pref_command_tut), true);
		
		final View tutView = LayoutInflater.from(getBaseContext()).inflate(R.layout.commands_tut, null);
		
        if (showTutorial) {
			frame.addView(tutView);
		}
        
        setContentView(frame);
        
        // Set up the button event for the tutorial view
        if(showTutorial) {
        	
        	Button button = (Button) findViewById(R.id.completeButton);
			
			button.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(final View v) {
	        		frame.removeView(tutView);
	        		
	        		Editor edit = sp.edit();
	    			edit.putBoolean(getResources().getString(R.string.pref_command_tut), false);
	    			edit.commit();
	            }
			});
        }
        
        mScriptsDbAdapter = new ScriptsDbAdapter(this);
        mCommandsDbAdapter = new CommandsDbAdapter(this);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        registerForContextMenu(getListView());
        
        mScript = (savedInstanceState == null) ? null :
        	(Script) savedInstanceState.getSerializable(ScriptsDbAdapter.SCRIPT);
        
        if (mScript == null) {
        	Bundle extras = getIntent().getExtras();
        	mScript = extras != null ? (Script) extras.getParcelable(ScriptsDbAdapter.SCRIPT) : null;
        }
        
        setViewTitle();
        fillCommands();
        saveState();
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if(mAutoInsertBelow) {
			startKeypadActivity(position + 1, INSERT_BELOW);
		} else {
			editCommandString((Command) getListAdapter().getItem(position));
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.commands, menu);
        return true;
    }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		if(mFirstLoad) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	        
	        MenuItem autoInsertBelowCB = menu.findItem(R.id.menu_commands_auto_insert_below);
	        MenuItem autoInsertBrktCB = menu.findItem(R.id.menu_commands_auto_insert_brkt);
	        MenuItem animate = menu.findItem(R.id.menu_animate);
	        
	        mAutoInsertBelow = sp.getBoolean(getResources().getString(R.string.pref_auto_insert_below), true);
	        mAutoInsertBrkt = sp.getBoolean(getResources().getString(R.string.pref_auto_insert_brkt), true);
	        mAnimate = sp.getBoolean(getResources().getString(R.string.pref_animate), true);
	        
	        autoInsertBelowCB.setChecked(mAutoInsertBelow);
	        autoInsertBrktCB.setChecked(mAutoInsertBrkt);
	        animate.setChecked(mAnimate);
	        
	        mFirstLoad = false;
		}
        
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case android.R.id.home:
	        	setResult(RESULT_CANCELED);
	        	finish();
	        	return true;
	        case R.id.menu_commands_insert:
        		startKeypadActivity(mCommands.size(), INSERT);
        		return true;
	        case R.id.menu_commands_rename:
	        	AlertDialog.Builder alert = new AlertDialog.Builder(this);

				alert.setTitle(getResources().getString(R.string.menu_rename));

				// Set an EditText view to get user input 
				final EditText input = new EditText(this);
				alert.setView(input);

				alert.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int whichButton) {
					    // Canceled.
					  }
					});
				
				alert.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					  String value = input.getText().toString();
					  if(mScript != null) {
						  mScriptsDbAdapter.open();
						  if(mScriptsDbAdapter.scriptNameIsValid(value)) {
							  if(!mScriptsDbAdapter.scriptExist(value)) {
								  mScript.set_name(value);
								  mScriptsDbAdapter.renameScript(mScript.get_id(), value);
								  mScriptsDbAdapter.close();
								  setTitle(value);
							  } else {
								  mScriptsDbAdapter.close();
								  Toast.makeText(getApplicationContext(), "Script '" + value + "' already exists.  Names must be unique", Toast.LENGTH_LONG).show();
							  }
						  } else {
							  	mScriptsDbAdapter.close();
								Toast.makeText(getApplicationContext(), "Script '" + value + "' contains illegal characters. Only enter 0-9, a-z, A-Z, -, and _", Toast.LENGTH_LONG).show();
						  }
					  }
				  }
				});

				alert.show();
				return true;
	        case R.id.menu_commands_delete_script:
	        	if(mScript != null) {
	        		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	        		 
	                // Setting Dialog Title
	                alertDialog.setTitle("Confirm Delete...");
	         
	                // Setting Dialog Message
	                alertDialog.setMessage("Are you sure you want delete this script?");
	              
	                alertDialog.setNegativeButton(getResources().getString(R.string.button_no), new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                    	dialog.cancel();
	                    }
	                });
	         
	                // Setting Positive "Yes" Button
	                alertDialog.setPositiveButton(getResources().getString(R.string.button_yes), new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog,int which) {
	                    	deleteScriptAndCommands(mScript);
	                    	setResult(RESULT_OK);
	        	        	finish();
	                    }
	                });
	                // Showing Alert Message
	                alertDialog.show();
	        	}
	        	
	        	return true;
	        case R.id.menu_commands_play:
	        	if (CommandsActivity.validCommandList(getApplicationContext(), mCommands)) {
		        	Intent drawViewIntent = new Intent(this, DrawActivity.class);
		        	drawViewIntent.putExtra(ScriptsDbAdapter.SCRIPT, mScript);
		        	drawViewIntent.putParcelableArrayListExtra(CommandsDbAdapter.DATABASE_TABLE, mCommands);
		        	startActivity(drawViewIntent);
	        	}
	        	return true;
	        case R.id.menu_commands_auto_insert_below:
	        	if(item.isChecked()) {
	        		mAutoInsertBelow = false;
	        	} else {
	        		mAutoInsertBelow = true;
	        	}
	        	item.setChecked(mAutoInsertBelow);
	        	return true;
	        case R.id.menu_commands_auto_insert_brkt:
	        	if(item.isChecked()) {
	        		mAutoInsertBrkt = false;
	        	} else {
	        		mAutoInsertBrkt = true;
	        	}
	        	item.setChecked(mAutoInsertBrkt);
	        	return true;
	        case R.id.menu_animate:
	        	if(item.isChecked()) {
	        		mAnimate = false;
	        	} else {
	        		mAnimate = true;
	        	}
	        	item.setChecked(mAnimate);
	        	return true;
	        case R.id.menu_help:
				Intent help = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.uni-due.de"));
				startActivity(help);
				return true;
	        case R.id.menu_commands_export:
	        	ImportExport.ExportDialog(this, getResources().getInteger(R.integer.database_version), mScript, mCommands);
	        	return true;
			default:
				return true;
	    }
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final Command command = (Command) getListAdapter().getItem(info.position);
		
		if (item.toString().equals(getResources().getString(R.string.menu_edit))) {
			editCommandString(command);
		} else if (item.toString().equals(getResources().getString(R.string.menu_insert_above))) {
			startKeypadActivity(command.get_order(), INSERT_ABOVE);
		} else if (item.toString().equals(getResources().getString(R.string.menu_insert_below))) {
			startKeypadActivity(command.get_order() + 1, INSERT_BELOW);
		} else if (item.toString().equals(getResources().getString(R.string.menu_delete))) {
			deleteCommand(command);
			fillCommands();
		}
		
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(getResources().getString(R.string.menu_edit));
		menu.add(getResources().getString(R.string.menu_insert_above));
		menu.add(getResources().getString(R.string.menu_insert_below));
		menu.add(getResources().getString(R.string.menu_delete));
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			long order = bundle.getLong(CommandsDbAdapter.COLUMN_ORDER);
			long scriptId = bundle.getLong(CommandsDbAdapter.COLUMN_SCRIPT_ID);
			String command = bundle.getString(CommandsDbAdapter.COLUMN_COMMAND);
			boolean foundRpt = bundle.getBoolean(KeypadActivity.FoundRepeat);
			
			switch (requestCode) {
			case INSERT_ABOVE:
			case INSERT_BELOW:
				incrementCommandPositions(scriptId, order, mCommands.size());
				insertCommand(scriptId, order, command);
				break;
			case INSERT:
				insertCommand(scriptId, order, command);
				break;
			case EDIT:
				updateCommandString(scriptId, order, command);
				break;
			}
			
			// Auto add a ]
			if (mAutoInsertBrkt) {
				if (foundRpt) {
					if (requestCode == INSERT_ABOVE ||
							requestCode == INSERT_BELOW) {
						incrementCommandPositions(scriptId, order + 1, mCommands.size() + 1);
					}
					
					insertCommand(scriptId, order + 1, KeypadButton.BRKT.getText().toString());
				}
			}
			
			getListView().smoothScrollToPosition((int)order);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
        
        edit.putBoolean(getResources().getString(R.string.pref_auto_insert_below), mAutoInsertBelow);
        edit.putBoolean(getResources().getString(R.string.pref_auto_insert_brkt), mAutoInsertBrkt);
        edit.putBoolean(getResources().getString(R.string.pref_animate), mAnimate);
		edit.commit();
		
		saveState();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mFirstLoad = true;
		setViewTitle();
		fillCommands();
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(ScriptsDbAdapter.SCRIPT, mScript);
	}

	private void saveState() {
		String title = (String) getTitle();
		
		if (mScript == null) {
			mScriptsDbAdapter.open();
			mScript = mScriptsDbAdapter.createScript(title);
			mScriptsDbAdapter.close();
		}		
	}
	
	private void setViewTitle() {
		if (mScript != null) {
			setTitle(mScript.get_name());
		}
		else {
			setTitle("Script");
		}
	}
	
	private void fillCommands() {
		if (mScript != null) {
			mCommandsDbAdapter.open();
			mCommands = (ArrayList<Command>) mCommandsDbAdapter.fetchCommandsForScript(mScript.get_id());
			mCommandsDbAdapter.close();
			
			mAdapter = new IconicAdapter();
			TouchListView tlv = (TouchListView) getListView();
			setListAdapter(mAdapter);
			
			tlv.setDropListener(onDrop);
			tlv.setRemoveListener(onRemove);
		}
	}
	
	private TouchListView.DropListener onDrop=new TouchListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if(from == to) {
				return;
			}
			
			mCommandsDbAdapter.open();
			if (from < to) {
				mCommandsDbAdapter.updateCommandPosition(mCommands.get(from).get_id(), to);
				for(int i=from+1; i<=to; i++) {
					mCommandsDbAdapter.updateCommandPosition(mCommands.get(i).get_id(), i-1);
				}
			} else {
				mCommandsDbAdapter.updateCommandPosition(mCommands.get(from).get_id(), to);
				for(int i=to; i<from; i++) {
					mCommandsDbAdapter.updateCommandPosition(mCommands.get(i).get_id(), i+1);
				}
			}
			mCommandsDbAdapter.close();
			
			fillCommands();
		}
	};

	private TouchListView.RemoveListener onRemove=new TouchListView.RemoveListener() {
		@Override
		public void remove(int which) {
			mAdapter.remove(mAdapter.getItem(which));
		}
	};
	
	class IconicAdapter extends ArrayAdapter<Command> {
		IconicAdapter() {
			super(CommandsActivity.this, R.layout.commands_row, mCommands);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			View row=convertView;
			
			if(row==null) {
				LayoutInflater inflater = getLayoutInflater();
				row=inflater.inflate(R.layout.commands_row, parent, false);
			}
			
			TextView label = (TextView) row.findViewById(R.id.label);
			
			label.setText(mCommands.get(position).get_commandString());
			
			return(row);
		}
	}
	
	private void cleanUp() {
		mScriptsDbAdapter.close();
		mCommandsDbAdapter.close();
	}
	
	private void deleteScriptAndCommands(Script script) {
		mScriptsDbAdapter.open();
		mCommandsDbAdapter.open();
		mCommandsDbAdapter.deleteCommandsForScript(script.get_id());
		mScriptsDbAdapter.deleteScript(script.get_id());		
		cleanUp();
	}
	
	private void startKeypadActivity(long order, int requestCode) {
		Intent keypad_intent = new Intent(this, KeypadActivity.class);
        keypad_intent.putExtra(ScriptsDbAdapter.SCRIPT, mScript);
        keypad_intent.putExtra(CommandsDbAdapter.COLUMN_ORDER, order);
        startActivityForResult(keypad_intent, requestCode);
	}
	
	private void deleteCommand(Command command) {
		mCommandsDbAdapter.open();
		mCommandsDbAdapter.deleteCommand(command.get_id());
		
		for (int i = (int) command.get_order() + 1; i < mCommands.size(); i++) {
			mCommandsDbAdapter.updateCommandPosition(command.get_scriptId(),
					i, i-1);
		}
		
		mCommandsDbAdapter.close();
	}
	
	private void incrementCommandPositions(long scriptId, long startOrder, int length) {
		mCommandsDbAdapter.open();
		for (int i = length - 1; i >= (int) startOrder; i--) {
			mCommandsDbAdapter.updateCommandPosition(scriptId, i, i+1);
		}
		mCommandsDbAdapter.close();
	}
	
	private void insertCommand(long scriptId, long order, String fullCommand) {
		mCommandsDbAdapter = new CommandsDbAdapter(this);
		mCommandsDbAdapter.open();
		mCommandsDbAdapter.insertCommand(scriptId, order, fullCommand);
		mCommandsDbAdapter.close();
	}
	
	private void updateCommandString(long scriptId, long order, String fullCommand) {
		mCommandsDbAdapter = new CommandsDbAdapter(this);
		mCommandsDbAdapter.open();
		mCommandsDbAdapter.updateCommandString(scriptId, order, fullCommand);
		mCommandsDbAdapter.close();
	}
	
	private void editCommandString(Command command) {
		Intent keypad_intent = new Intent(this, KeypadActivity.class);
        keypad_intent.putExtra(ScriptsDbAdapter.SCRIPT, mScript);
        keypad_intent.putExtra(CommandsDbAdapter.COLUMN_ORDER, command.get_order());
        keypad_intent.putExtra(CommandsDbAdapter.COLUMN_COMMAND, command.get_commandString());
        startActivityForResult(keypad_intent, EDIT);
	}
	
	public static boolean validCommandList(Context context, List<Command> commands) {
		int rptCount = 0;
		int brktCount = 0;
		
		boolean valid = true;
		
		for (Command command : commands) {
			if(command.get_commandString().contains(KeypadButton.RPT.getText().toString())) {
				rptCount++;
			}
			
			if(command.get_commandString().contains(KeypadButton.BRKT.getText().toString())) {
				brktCount++;
				
				if(brktCount > rptCount) {
					valid = false;
					Toast.makeText(context, "RPTs must come before ]'s.  Please fix this.", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}
		
		if (rptCount != brktCount) {
			valid = false;
			Toast.makeText(context, "There is a mismatch between RPTs and ]'s.  Please fix this.", Toast.LENGTH_SHORT).show();
		}
		
		return valid;
	}
}
