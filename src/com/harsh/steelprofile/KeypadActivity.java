package com.harsh.steelprofile;

import java.util.Stack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.harsh.steelprofile.databases.Command;
import com.harsh.steelprofile.databases.CommandsDbAdapter;
import com.harsh.steelprofile.databases.Script;
import com.harsh.steelprofile.databases.ScriptsDbAdapter;

public class KeypadActivity extends SherlockActivity {
	public final static String SIS_TEXTBOX = "textBox";
	public final static String SIS_COUNTER = "counter";
	
	GridView mKeypadGrid;
	TextView mUserInputText;
	TextView memoryStatText;

	Stack<String> mInputStack;
	Stack<String> mOperationStack;

	KeypadAdapter mKeypadAdapter;
	TextView mStackText;
	boolean resetInput = false;
	boolean hasFinalResult = false;

	String mDecimalSeperator;
	double memoryValue = Double.NaN;
	
	private Script mScript;
	private long mPosition = -1;
	private String mCommandString = null;
	public static String FoundRepeat = "FoundRepeat";
	
	final String[] cmdText = new String[] {"FD x",
										   "BK x",
										   "LT x",
										   "RT x",
										   "PU",
										   "PD",
										   "RPT n",
										   "]",
										   "PT x",
										   "DIR x",
										   "SPC x",
										   "HOM"};
	
	final String[] descText = new String[] {"Move forward 'x' length",
										    "Move backward 'x' length",
										    "Rotate the 'x' degrees left",
										    "Rotate the 'x' degrees right",
										    "Short for pen up.  This lifts the pen from the screen so that move command doesn’t draw a line",
										    "Short for pen down.  This puts the pen down so that move command draws a line",
										    "Repeats the actions between 'RPT n' and ']' an n number of times.",
										    "This closes off a RPT command. Anything between a 'RPT n' and ']' will be repeated.",
										    "Sets thickness.",
										    "Directly sets the direction of the Drawing with 0 degrees = pointing up.",
										    "Sets the pen color x (select from patches).",
										    "Returns the Drawing to the home position at the middle of the screen (start)"};
	
	final String[] exText = new String[] {"FD 100",
										  "BK 45",
										  "LT 45",
										  "RT 45",
										  "PU",
										  "PD",
										  "RPT 360\r\n"+
										  "FD 1\r\n"+
										  "RT 1\r\n"+
										  "]",
										  "]",
										  "PT 5",
										  "DIR 90",
										  "SPC \r\n"+
										  "FD 100",
										  "SPC RED",
										  "HOM"};
	
	static int mScreenOrientation;
	

	int mTutCounter = 0;
	TextView mCmd1;
	TextView mDesc1;
	TextView mEx1;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);

		final FrameLayout frame = new FrameLayout(this);
        frame.addView(LayoutInflater.from(getBaseContext()).inflate(R.layout.keypadactivity, null));
        
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean showTutorial = sp.getBoolean(getResources().getString(R.string.pref_keypad_tut), true);
		
		final View tutView = LayoutInflater.from(getBaseContext()).inflate(R.layout.keypad_tut, null);
		
        if (showTutorial) {
			frame.addView(tutView);
		}
        
        setContentView(frame);
        
        mScreenOrientation = getResources().getConfiguration().orientation;
        
        
		
		ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        Bundle extras = getIntent().getExtras();
        
        mUserInputText = (TextView) findViewById(R.id.txtInput);
        
        mScript = (savedInstanceState == null) ? null :
        	(Script) savedInstanceState.getSerializable(ScriptsDbAdapter.SCRIPT);
        
        mPosition = (savedInstanceState == null) ? -1 :
        	savedInstanceState.getInt(CommandsDbAdapter.COLUMN_ORDER);
        
        mCommandString = (savedInstanceState == null) ? null :
        	savedInstanceState.getString(CommandsDbAdapter.COLUMN_COMMAND);
        
        if (mScript == null) {
        	mScript = extras != null ? (Script) extras.getParcelable(ScriptsDbAdapter.SCRIPT) : null;
        }
        
        if (mPosition == -1) {
        	mPosition = extras != null ? extras.getLong(CommandsDbAdapter.COLUMN_ORDER) : -1;
        }
        
        if (mCommandString == null) {
        	mCommandString = extras != null ? extras.getString(CommandsDbAdapter.COLUMN_COMMAND) : null;
        	mUserInputText.setText(mCommandString);
        }
        
        if (savedInstanceState != null) {
        	mUserInputText.setText(savedInstanceState.getString(SIS_TEXTBOX));
        	mTutCounter = savedInstanceState.getInt(SIS_COUNTER);
        }
        
        setTitle(mScript.get_name());
        
		mKeypadGrid = (GridView) findViewById(R.id.grdButtons);

		// Create Keypad Adapter
		mKeypadAdapter = new KeypadAdapter(this);

		// Set adapter of the keypad grid
		mKeypadGrid.setAdapter(mKeypadAdapter);
		

		// Set button click listener of the keypad adapter
		mKeypadAdapter.setOnButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button btn = (Button) v;
				KeypadButton keypadButton = (KeypadButton) btn.getTag();

				ProcessKeypadInput(keypadButton);
			}
		});

		mKeypadGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

			}
		});
		
		// Set up the button event for the tutorial view
        if(showTutorial) {
        	
        	Button hideButton = (Button) findViewById(R.id.completeButton);
			
        	hideButton.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(final View v) {
	        		frame.removeView(tutView);
	        		
	        		Editor edit = sp.edit();
	    			edit.putBoolean(getResources().getString(R.string.pref_keypad_tut), false);
	    			edit.commit();
	            }
			});
			        	
        	mCmd1 = (TextView) findViewById(R.id.textViewCmd1);
        	mDesc1 = (TextView) findViewById(R.id.textViewDesc1);
        	mEx1 = (TextView) findViewById(R.id.textViewEx);
        	
        	updateTutText();
        	
			Button prevButton = (Button) findViewById(R.id.prevButton);
			prevButton.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(final View v) {
	        		if (mTutCounter > 0) {
	        			mTutCounter--;
	        		} else {
	        			mTutCounter = cmdText.length - 1;
	        		}
	        		updateTutText();
	            }
			});
			
			Button nextButton = (Button) findViewById(R.id.nextButton);
			nextButton.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(final View v) {
	        		if (mTutCounter < cmdText.length - 1) {
	        			mTutCounter++;
	        		} else {
	        			mTutCounter = 0;
	        		}
	        		updateTutText();
	            }
			});
			
        }

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(SIS_TEXTBOX, mUserInputText.getText().toString());
		outState.putInt(SIS_COUNTER, mTutCounter);
	}

	private void updateTutText() {
		mCmd1.setText(cmdText[mTutCounter]);
		mDesc1.setText(descText[mTutCounter]);
		mEx1.setText(exText[mTutCounter]);
	}
	
	private void ProcessKeypadInput(KeypadButton keypadButton) {
		String text = keypadButton.getText().toString();
		String currentInput = mUserInputText.getText().toString();
        int currentInputLength = currentInput.length();
        String[] split = currentInput.split(" ");
        String s = "";
		
		switch (keypadButton) {
		case BKSP:
			int endIndex = currentInputLength - 1;
			if(split.length <= 1){
				mUserInputText.setText("");
			}
			else{
				if(Character.isDigit(split[split.length - 1].charAt(0)) || (split[split.length - 1].charAt(0)) == '.'){
					if(endIndex < 1)
						mUserInputText.setText("");
					else
						mUserInputText.setText(currentInput.subSequence(0, endIndex));
				}
				else{
					split[split.length - 1] = "";
					for(int i =0;i<(split.length-1);i++){
						s = s + split[i] + " ";
					}
					mUserInputText.setText(s);
				}
			}
			break;
	    case CLR: 
	    	mUserInputText.setText("");
	    	break;
		default:
			if(currentInputLength < 21){
				if (Character.isDigit(text.charAt(0)) || (text.charAt(0)) == '.' ) {
					mUserInputText.append(text);
				}
				else{
					mUserInputText.append(text+" ");
				}
			}
			else{
				Toast.makeText(this, "Input length exceeded", Toast.LENGTH_SHORT).show();
			}
			break;
		}


	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.keyboard, menu);
        return true;
    }	
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case android.R.id.home:
	        	setResult(RESULT_CANCELED);
	        	finish();
	            return true;
	        case R.id.menu_keyboard_accept:
	        	
	        	if (Command.Validate(mUserInputText.getText().toString())) {
	        		Intent command = this.getIntent();
	        		command.putExtra(CommandsDbAdapter.COLUMN_SCRIPT_ID, mScript.mId);
	        		command.putExtra(CommandsDbAdapter.COLUMN_ORDER, mPosition);
	        		command.putExtra(CommandsDbAdapter.COLUMN_COMMAND, mUserInputText.getText().toString());
	        		
	        		//Only auto add ] when the new command is RPT
	        		if(mUserInputText.getText().toString().contains(KeypadButton.RPT.getText()) &&
	        				mCommandString != null && mCommandString.contains(KeypadButton.RPT.getText())) {
	        			command.putExtra(FoundRepeat, false);
	        		} else if(mUserInputText.getText().toString().contains(KeypadButton.RPT.getText())) {
	        			command.putExtra(FoundRepeat, true);
	        		}
	        		
	        		setResult(RESULT_OK, command);
		        	finish();
	        	} else {
	        		Toast.makeText(this, "The input is invalid", Toast.LENGTH_SHORT).show();
	        	}
	        	return true;
	        case R.id.menu_keyboard_cancel:
	        	setResult(RESULT_CANCELED);
	        	finish();
	        	return true;
	        case R.id.menu_help:
				Intent help = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.uni-due.de/"));
				startActivity(help);
	   			return true;
	        default:
	        	Toast.makeText(this, "Got click: " + item.toString(), Toast.LENGTH_SHORT).show();
	            return true;
	            //return super.onOptionsItemSelected(item);
	    }
	}
	
	public static int getScreenOrientation() {
		return mScreenOrientation;
	}

}
