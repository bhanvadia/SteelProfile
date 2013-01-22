package com.harsh.steelprofile;

import java.io.File;
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
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.harsh.steelprofile.databases.Command;
import com.harsh.steelprofile.databases.CommandsDbAdapter;
import com.harsh.steelprofile.databases.DbAdapter;
import com.harsh.steelprofile.databases.Script;
import com.harsh.steelprofile.databases.ScriptsDbAdapter;
import com.harsh.steelprofile.common.ImportExport;

public class DroidDrawClientActivity extends SherlockListActivity {
	
	private static final int NEW_SCRIPT = 0;
	private static final int EDIT_SCRIPT = 1;
	
	private static final String SCRIPT_COUNTER = "Script_Counter";
	
	private ScriptsDbAdapter mScriptsDbAdapter;
	CommandsDbAdapter mCommandsDbAdapter;
	private ImageButton mInsertNewScriptButton;
	private EditText mEditTextScriptName;
	
	SimpleCursorAdapter mAdapter;
	
	private final Context mContext = this;
	FrameLayout mFrame;
	boolean mOnCreateDone = false;
	boolean mHidePressed = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        mFrame = new FrameLayout(this);
        
        mFrame.addView(LayoutInflater.from(getBaseContext()).inflate(R.layout.scripts, null));
        setContentView(mFrame);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        DbAdapter dbAdapter = new DbAdapter(this);
        dbAdapter.open();
        dbAdapter.close();
        
        mScriptsDbAdapter = new ScriptsDbAdapter(this);
        mCommandsDbAdapter = new CommandsDbAdapter(this);
        fillData();
        
        registerForContextMenu(getListView());
        
        mEditTextScriptName = (EditText) findViewById(R.id.editTextScriptName);
        mEditTextScriptName.setText("");
        
        mInsertNewScriptButton = (ImageButton) findViewById(R.id.buttonInsert);
        mInsertNewScriptButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String scriptName = mEditTextScriptName.getText().toString();
				
				if (scriptName.equals("")) {
					int currentCount = fetchScriptCount();
					if(currentCount == 0) {
						scriptName = "Script";
					}
					else {
						scriptName = "Script_" + currentCount;
					}
					incrementScriptCount(currentCount);
				}
				
				createScript(scriptName);
			}
		});
        
        mOnCreateDone = true;
    }
    
    private void InitializeTutFrame()
    {        
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean showTutorial = sp.getBoolean(getResources().getString(R.string.pref_script_tut), true);
		
		final View tutView = LayoutInflater.from(getBaseContext()).inflate(R.layout.scripts_tut, null);
		
        if (showTutorial) {
			mFrame.addView(tutView);
			mHidePressed = false;
		}
        
        // Set up the button event for the tutorial view
        if(showTutorial) {
        	
        	Button button = (Button) findViewById(R.id.completeButton);
			
			button.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(final View v) {
	        		mFrame.removeView(tutView);
        			Editor edit = sp.edit();
        			edit.putBoolean(getResources().getString(R.string.pref_script_tut), false);
        			edit.commit();
        			mHidePressed = true;
	            }
			});
        }
    }
    
    @Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		mEditTextScriptName.setText("");
		
		// Only try to re add the tutorial when the oncreate method has finished AND the hide button has been pressed
		if (mFrame.getChildCount() == 1)
			InitializeTutFrame();
		
		
		super.onResume();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.scripts, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_help:
				Intent help = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.uni-due.de"));
				startActivity(help);
	   			return true;
			case R.id.menu_import:
				importScript();
	   			return true;
			default:
				return true;
				//return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final Script script = (Script) getListAdapter().getItem(info.position);
		
		if(item.toString().equals(getResources().getString(R.string.menu_run))) {
			ArrayList<Command> commands = (ArrayList<Command>) fetchCommandsForScript(script);
			if (CommandsActivity.validCommandList(this, commands)) {
	        	Intent drawViewIntent = new Intent(this, DrawActivity.class);
	        	drawViewIntent.putExtra(ScriptsDbAdapter.SCRIPT, script);
	        	drawViewIntent.putParcelableArrayListExtra(CommandsDbAdapter.DATABASE_TABLE, commands);
	        	startActivity(drawViewIntent);
        	}
        	return true;
		} else if (item.toString().equals(getResources().getString(R.string.menu_rename))) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle(getResources().getString(R.string.menu_rename));

			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				  String value = input.getText().toString();
				  mScriptsDbAdapter.open();
				  if(mScriptsDbAdapter.scriptNameIsValid(value)) {
					  if(!mScriptsDbAdapter.scriptExist(value)) {
						  mScriptsDbAdapter.renameScript(script.get_id(), value);
						  mScriptsDbAdapter.close();
						  fillData();
					  } else {
						  mScriptsDbAdapter.close();
						  Toast.makeText(mContext, "Script '" + value + "' already exists.  Names must be unique", Toast.LENGTH_LONG).show();
					  }
				  } else {
					  	mScriptsDbAdapter.close();
						Toast.makeText(mContext, "Script '" + value + "' contains illegal characters. Only enter 0-9, a-z, A-Z, -, and _", Toast.LENGTH_LONG).show();
				  }
			  }
			});
			
			alert.show();
		} else if (item.toString().equals(getResources().getString(R.string.menu_delete))) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
   		 
            // Setting Dialog Title
            alertDialog.setTitle("Confirm Delete...");
     
            // Setting Dialog Message
            alertDialog.setMessage("Are you sure you want delete this script?");

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton(getResources().getString(R.string.button_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                	deleteScript(script);
        			fillData();
                }
            });
            // Showing Alert Message
            alertDialog.show();
			
		} else if (item.toString().equals(getResources().getString(R.string.menu_copy))) {
			copyScript(script);
		} else if (item.toString().equals(getResources().getString(R.string.menu_export))) {
			exportScript(script);
		}
		else {
			Toast.makeText(this, "Got click: " + item.toString(), Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(getResources().getString(R.string.menu_run));
		menu.add(getResources().getString(R.string.menu_rename));
		menu.add(getResources().getString(R.string.menu_copy));
		menu.add(getResources().getString(R.string.menu_delete));
		menu.add(getResources().getString(R.string.menu_export));
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		editScript((Script) getListAdapter().getItem(position));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}
	
	private void fillData() {
		mScriptsDbAdapter.open();
		
		List<Script> scripts = mScriptsDbAdapter.fetchAllScripts();
		
		ArrayAdapter<Script> adapter = new ArrayAdapter<Script>(this,
				android.R.layout.simple_list_item_1, scripts);
		setListAdapter(adapter);
		
		mScriptsDbAdapter.close();
	}
	
	private void createScript(String scriptName) {
		mScriptsDbAdapter.open();
		
		if(mScriptsDbAdapter.scriptNameIsValid(scriptName)) {
			if(!mScriptsDbAdapter.scriptExist(scriptName)) {
				Script script = mScriptsDbAdapter.createScript(scriptName);
				mScriptsDbAdapter.close();
				
				Intent i = new Intent(this, CommandsActivity.class);
				i.putExtra(ScriptsDbAdapter.SCRIPT, script);
				startActivityForResult(i, NEW_SCRIPT);
			} else {
				mScriptsDbAdapter.close();
				Toast.makeText(this, "Script '" + scriptName + "' already exists.  Names must be unique", Toast.LENGTH_LONG).show();
			}
		} else {
			mScriptsDbAdapter.close();
			Toast.makeText(this, "Script '" + scriptName + "' contains illegal characters. Only enter 0-9, a-z, A-Z, -, and _", Toast.LENGTH_LONG).show();
		}
	}
	
	private boolean scriptNameExists(String scriptName) {
		ScriptsDbAdapter scriptsDbAdapter = new ScriptsDbAdapter(this);
		scriptsDbAdapter.open();
		boolean scriptExists = scriptsDbAdapter.scriptExist(scriptName);
		scriptsDbAdapter.close();
		
		return scriptExists;
	}
	
	private void copyScript(Script originalScipt) {
		final Script temp = originalScipt;		
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getResources().getString(R.string.menu_copy) + " - New Script Name");

		
		mScriptsDbAdapter.open();
        int counter = 1;
        String suggestedName = originalScipt.get_name() + "_" + counter;
        
        while (mScriptsDbAdapter.scriptExist(suggestedName)) {
                counter++;
                suggestedName = originalScipt.get_name() + "_" + counter;
        }
        mScriptsDbAdapter.close();
		
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setText(suggestedName);
		alert.setView(input);

		alert.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			  String value = input.getText().toString();
			  mScriptsDbAdapter.open();
			  if(mScriptsDbAdapter.scriptNameIsValid(value)) {
				  if(!mScriptsDbAdapter.scriptExist(value)) {
					  Script script = mScriptsDbAdapter.createScript(value);
					  mScriptsDbAdapter.close();
					  List<Command> commands = fetchCommandsForScript(temp);
						
						mCommandsDbAdapter.open();
						for (Command command : commands) {
							mCommandsDbAdapter.insertCommand(script.get_id(), command.get_order(), command.get_commandString());
						}
						mCommandsDbAdapter.close();
						
						fillData();
				  } else {
					  mScriptsDbAdapter.close();
					  Toast.makeText(mContext, "Script '" + value + "' already exists.  Names must be unique", Toast.LENGTH_LONG).show();
				  }
			  } else {
				  	mScriptsDbAdapter.close();
					Toast.makeText(mContext, "Script '" + value + "' contains illegal characters. Only enter 0-9, a-z, A-Z, -, and _", Toast.LENGTH_LONG).show();
			  }
		  }
		});
		
		alert.show();
	}
	
	private void deleteScript(Script script) {
		mCommandsDbAdapter.open();
		mCommandsDbAdapter.deleteCommandsForScript(script.get_id());
		mCommandsDbAdapter.close();
		
		mScriptsDbAdapter.open();
		mScriptsDbAdapter.deleteScript(script.get_id());
		mScriptsDbAdapter.close();
	}
	
	private void editScript(Script script) {
		Intent i = new Intent(this, CommandsActivity.class);
		i.putExtra(ScriptsDbAdapter.SCRIPT, script);
		startActivityForResult(i, EDIT_SCRIPT);
	}
	
	private int fetchScriptCount() {
		int scriptCount;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		scriptCount = sp.getInt(SCRIPT_COUNTER, 0);
		
		return scriptCount;
	}
	
	private void incrementScriptCount(int current) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		Editor edit = sp.edit();
		edit.putInt(SCRIPT_COUNTER, current + 1);
		edit.commit();
	}
	
	private List<Command> fetchCommandsForScript(Script script) {
		mCommandsDbAdapter.open();
		List<Command> commands = (ArrayList<Command>) mCommandsDbAdapter.fetchCommandsForScript(script.get_id());		
		mCommandsDbAdapter.close();
		
		return commands;
	}
	
	private void importScript() {
		final File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
				  this.getResources().getString(R.string.app_sdcard_path));
		
		File[] files = directory.listFiles();
		if (files == null || files.length == 0) {
			Toast.makeText(mContext, "To Import scripts, please place scripts in folder: " +
					directory.getAbsolutePath(),
					Toast.LENGTH_LONG).show();
			return;
		}
		
		final List<String> temp = new ArrayList<String>();
		
		for (int i=0; i<files.length; i++) {
			if(files[i].getName().endsWith(getResources().getString(R.string.script_extension))) {
				temp.add(files[i].getName());
			}
		}
		
		if (temp == null || temp.size() == 0) {
			Toast.makeText(mContext, "To Import scripts, please place scripts in folder: " +
					directory.getAbsolutePath(),
					Toast.LENGTH_LONG).show();
			return;
		}
		
		final String[] scripts = new String[temp.size()];
		for (int i=0; i<temp.size(); i++) {
			scripts[i] = temp.get(i);
		}
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Select script to import");
		alert.setItems(scripts, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (scriptNameExists(scripts[which].replace(getResources().getString(R.string.script_extension), ""))) {
					Toast.makeText(mContext, "Script '" + scripts[which].replace(getResources().getString(R.string.script_extension), "") +
							"' already exists.  Please enter a new name.", Toast.LENGTH_LONG).show();
					promptImportScriptAs(scripts[which]);
				} else {
					ImportExport.Import(mContext, directory.getAbsolutePath() +
							"/" + scripts[which],
							scripts[which].replace(getResources().getString(R.string.script_extension), ""));
					fillData();
				}
			}
		});
		
		alert.show();
	}
	
	private void promptImportScriptAs(final String fileName) {
		final File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
				  this.getResources().getString(R.string.app_sdcard_path));
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Import Script As...");

		// Set an EditText view to get user input 
		final EditText inputExport = new EditText(this);
		alert.setView(inputExport);
		
		alert.setPositiveButton(this.getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			    String value = inputExport.getText().toString();
		    	if (scriptNameExists(value)) {
		    		Toast.makeText(mContext, "Script '" + value + "' already exists.  Names must be unique", Toast.LENGTH_LONG).show();
				} else {
					ImportExport.Import(mContext, directory.getAbsolutePath() + "/" + fileName, value);
					fillData();
				}
		  }
		});

		alert.show();
	}
	
	private void exportScript(Script script) {
		ImportExport.ExportDialog(this, getResources().getInteger(R.integer.database_version), script, fetchCommandsForScript(script));
	}
}