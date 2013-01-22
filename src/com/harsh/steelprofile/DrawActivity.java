package com.harsh.steelprofile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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

public class DrawActivity extends SherlockActivity {
	public final static String SIS_BITMAP = "Bitmap";
	public final static String SIS_FIRSTDRAW = "FirstDraw";
	public final static String SIS_SHOWICON = "ShowIcon";
	public final static String SIS_CURRX = "CurrX";
	public final static String SIS_CURRY = "CurrY";
	public final static String SIS_ENDX = "EndX";
	public final static String SIS_ENDY = "EndY";
	public final static String SIS_ANGLE = "Angle";
	public final static String SIS_T = "Thick";
    public final static String SIS_L = "Length";
    public final static String SIS_A = "Area";
    public final static String SIS_LX = "LengthX";
    public final static String SIS_LY = "LengthY";
    public final static String SIS_CX = "CentroidX";
    public final static String SIS_CY = "CentroidY";
    public final static String SIS_ISY = "SYY";
    public final static String SIS_ISX = "SXX";
    public final static String SIS_IA_Y = "IY";
    public final static String SIS_IA_X = "IX";
    public final static String SIS_IA_XY = "IXY";
	public final static String SIS_PAINTCOLOR = "PaintColor";
	public final static String SIS_PENDOWN = "PenDown";
	public final static String SIS_COUNTER = "Counter";
	public final static String SIS_RPTARRAYINDEX = "RepeatArrayIndex";
	public final static String SIS_RPTINDICES = "RepeatIndices";
	public final static String SIS_NUMREPEATS = "NumRepeats";
	public final static String SIS_NUMREPEATSSET = "NumRepeatsSet";
	
	private Script mScript;
	private ArrayList<Command> mCommands;
	private DrawView mDrawView;

	private boolean mFirstLoad = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);		
		
		ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
		
        mScript = (savedInstanceState == null) ? null :
        	(Script) savedInstanceState.getSerializable(ScriptsDbAdapter.SCRIPT);
        
        Bundle extras = getIntent().getExtras();
        
        if (mScript == null) {
        	mScript = extras != null ? (Script) extras.getParcelable(ScriptsDbAdapter.SCRIPT) : null;
        }
        
        if (savedInstanceState == null) {
        	mCommands = null;
        } else {
        	mCommands = savedInstanceState.getParcelableArrayList(CommandsDbAdapter.DATABASE_TABLE);
        }
                
        if (mCommands == null) {
        	if (extras == null) {
        		mCommands = null;
        	} else {
        		mCommands = extras.getParcelableArrayList(CommandsDbAdapter.DATABASE_TABLE);
        	}
        }
        
        if (mScript != null) {
        	setTitle(mScript.get_name());
        }
        
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int winWidth = Math.min(displayMetrics.widthPixels * 2, 2048);
        int winHeight = Math.min(displayMetrics.heightPixels * 2, 2048);
        
        boolean animate = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getResources().getString(R.string.pref_animate), true);
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        
        if (savedInstanceState == null) {
        	mDrawView = new DrawView(this, mCommands, winWidth, winHeight, animate, pm);
        } else {
        	ArrayList<Boolean> temp = new ArrayList<Boolean>();
        	for (Boolean b : savedInstanceState.getBooleanArray(SIS_NUMREPEATSSET)) {
				temp.add(b);
			}
        	
        	mDrawView = new DrawView(this, mCommands, winWidth, winHeight, 
        			animate, (Bitmap) savedInstanceState.getParcelable(SIS_BITMAP),
        			savedInstanceState.getBoolean(SIS_FIRSTDRAW),
        			savedInstanceState.getBoolean(SIS_SHOWICON),
        			savedInstanceState.getFloat(SIS_CURRX),
        			savedInstanceState.getFloat(SIS_CURRY),
        			savedInstanceState.getFloat(SIS_ENDX),
        			savedInstanceState.getFloat(SIS_ENDY),
        			savedInstanceState.getFloat(SIS_ANGLE),
        			savedInstanceState.getFloat(SIS_T),
        			savedInstanceState.getFloat(SIS_L),
        			savedInstanceState.getFloat(SIS_A),
        			savedInstanceState.getFloat(SIS_LX),
        			savedInstanceState.getFloat(SIS_LY),
        			savedInstanceState.getFloat(SIS_CX),
        			savedInstanceState.getFloat(SIS_CY),
        			savedInstanceState.getFloat(SIS_ISY),
        			savedInstanceState.getFloat(SIS_ISX),
        			savedInstanceState.getFloat(SIS_IA_Y),
        			savedInstanceState.getFloat(SIS_IA_X),
        			savedInstanceState.getFloat(SIS_IA_XY),
        			savedInstanceState.getInt(SIS_PAINTCOLOR),
        			savedInstanceState.getBoolean(SIS_PENDOWN),
        			savedInstanceState.getInt(SIS_COUNTER),
        			savedInstanceState.getInt(SIS_RPTARRAYINDEX),
        			savedInstanceState.getIntegerArrayList(SIS_RPTINDICES),
        			savedInstanceState.getIntegerArrayList(SIS_NUMREPEATS),
        			temp, pm);
        }
        
        
        mDrawView.setDrawingCacheEnabled(true);
        mDrawView.setBackgroundColor(Color.BLACK);
    	//setContentView(mDrawView);
        
        final FrameLayout frame = new FrameLayout(this);
        frame.addView(mDrawView);
        
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean showTutorial = sp.getBoolean(getResources().getString(R.string.pref_draw_tut), true);
		
		final View tutView = LayoutInflater.from(getBaseContext()).inflate(R.layout.draw_tut, null);
		
        if (showTutorial) {
			frame.addView(tutView);			
		}
        
        setContentView(frame);
        
        if (showTutorial) {
        	// Set up the button event for the tutorial view
 			Button button = (Button) findViewById(R.id.completeButton);
 			
 			button.setOnClickListener(new View.OnClickListener() {
 	        	public void onClick(final View v) {
 	        		frame.removeView(tutView);
 	        		
 	        		Editor edit = sp.edit();
 	    			edit.putBoolean(getResources().getString(R.string.pref_draw_tut), false);
 	    			edit.commit();
 	            }
 			});
        }
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(SIS_BITMAP, mDrawView.mScreenBitmap);
		outState.putBoolean(SIS_FIRSTDRAW, mDrawView.mFirstDraw);
		outState.putBoolean(SIS_SHOWICON, mDrawView.mShowIcon);
		outState.putFloat(SIS_CURRX, mDrawView.mCurrX);
		outState.putFloat(SIS_CURRY, mDrawView.mCurrY);
		outState.putFloat(SIS_ENDX, mDrawView.mEndX);
		outState.putFloat(SIS_ENDY, mDrawView.mEndY);
		outState.putFloat(SIS_ANGLE, mDrawView.mAngle);
		outState.putFloat(SIS_T, mDrawView.mT);
		outState.putFloat(SIS_L, mDrawView.mL);
		outState.putFloat(SIS_A, mDrawView.mA);
		outState.putFloat(SIS_LX, mDrawView.mLx);
		outState.putFloat(SIS_LY, mDrawView.mLy);
		outState.putFloat(SIS_CX, mDrawView.mCx);
		outState.putFloat(SIS_CY, mDrawView.mCy);
		outState.putFloat(SIS_ISY, mDrawView.mSYi);
		outState.putFloat(SIS_ISX, mDrawView.mSXi);
		outState.putFloat(SIS_IA_Y, mDrawView.mIA_y);
		outState.putFloat(SIS_IA_X, mDrawView.mIA_x);
		outState.putFloat(SIS_IA_XY, mDrawView.mIA_xy);
		outState.putInt(SIS_PAINTCOLOR, mDrawView.mPaint.getColor());
		outState.putBoolean(SIS_PENDOWN, mDrawView.mPenDown);
		outState.putInt(SIS_COUNTER, mDrawView.mCounter);
		outState.putInt(SIS_RPTARRAYINDEX, mDrawView.mRepeatArrayIndex);
		outState.putIntegerArrayList(SIS_RPTINDICES, mDrawView.mRepeatIndices);
		outState.putIntegerArrayList(SIS_NUMREPEATS, mDrawView.mNumRepeats);
		boolean[] temp = new boolean[mDrawView.mNumRepeatsSet.size()];
		for (int i=0; i<mDrawView.mNumRepeatsSet.size(); i++) {
			temp[i] = mDrawView.mNumRepeatsSet.get(i);
		}
		outState.putBooleanArray(SIS_NUMREPEATSSET, temp);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.draw, menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case android.R.id.home:
	        	setResult(RESULT_CANCELED);
	        	finish();
	            return true;
	        case R.id.menu_replay:
	        	DisplayMetrics displayMetrics = new DisplayMetrics();
	            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	            int winWidth = Math.min(displayMetrics.widthPixels * 2, 2048);
	            int winHeight = Math.min(displayMetrics.heightPixels * 2, 2048);
	            
	            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	            
	            mDrawView.cleanUp();
	            
	            boolean animate = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getResources().getString(R.string.pref_animate), true);
	            mDrawView = new DrawView(this, mCommands, winWidth, winHeight, animate, pm);
	            mDrawView.setDrawingCacheEnabled(true);
	            mDrawView.setBackgroundColor(Color.BLACK);
	        	setContentView(mDrawView);
	        	
	        	return true;
	        case R.id.menu_share:
	        	File file = takeScreenshot();
	        	
	        	if (file != null) {
		        	Intent share = new Intent(android.content.Intent.ACTION_SEND);
		        	share.setType("image/jpeg");
		        	share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		        	
		        	startActivity(Intent.createChooser(share, "Share via"));
	        	} else {
	        		Toast.makeText(this, "Could not share image", Toast.LENGTH_SHORT).show();
	        	}
	        	return true;
	        case R.id.result:
	        	Intent ResultsView = new Intent(this,ResultsCalc.class);	        	
	        	ResultsView.putExtra("rA",mDrawView.mA);
	        	ResultsView.putExtra("rCx",mDrawView.mCx);
	        	ResultsView.putExtra("rCy",mDrawView.mCy);
	        	ResultsView.putExtra("rSy",mDrawView.mSYi);
	        	ResultsView.putExtra("rSx",mDrawView.mSXi);
	        	ResultsView.putExtra("rIy",mDrawView.mIA_y);
	        	ResultsView.putExtra("rIx",mDrawView.mIA_x);
	        	ResultsView.putExtra("rIxy",mDrawView.mIA_xy);
	        	ResultsView.putExtra("rMidx",mDrawView.mMidX);
	        	ResultsView.putExtra("rMidy",mDrawView.mMidY);
	        	startActivity(ResultsView);
	        	
	    		return true;
	        case R.id.menu_animate:
	        	boolean menu_animate = false;
	        	if(item.isChecked()) {
	        		menu_animate = false;
	        	} else {
	        		menu_animate = true;
	        	}
	        	item.setChecked(menu_animate);
	        	
	        	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	    		Editor edit = sp.edit();
	            edit.putBoolean(getResources().getString(R.string.pref_animate), menu_animate);
	    		edit.commit();
	        	return true;
	        default:
	        	Toast.makeText(this, "Got click: " + item.toString(), Toast.LENGTH_SHORT).show();
	            return true;
	            //return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		if(mFirstLoad) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			
	        MenuItem animate = menu.findItem(R.id.menu_animate);
	        
	        boolean isAnimated = sp.getBoolean(getResources().getString(R.string.pref_animate), true);
	        animate.setChecked(isAnimated);
	        
	        mFirstLoad = false;
		}
		return true;
	}

	private File takeScreenshot() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date now = new Date();
		mDrawView.setDrawingCacheEnabled(true);
		mDrawView.buildDrawingCache(true);
		Bitmap bitmap = Bitmap.createBitmap(mDrawView.getDrawingCache());
		mDrawView.setDrawingCacheEnabled(false);
		String imageName = getExternalFilesDir(null).getPath() + "/" +
				mScript.get_name() + "_" + formatter.format(now) + getResources().getString(R.string.jpg);
		File file = new File(imageName);
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e("DroidDrawShare", "File not found", e);
		} catch (IOException e) {
			Log.e("DroidDrawShare", "IOException", e);
		}
		
		return file;
	}
}
