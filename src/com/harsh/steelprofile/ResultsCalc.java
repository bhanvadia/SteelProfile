package com.harsh.steelprofile;


import android.os.Bundle;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;


public class ResultsCalc extends SherlockActivity {
	
	TextView display0;
	TextView display1;
	TextView display2;
	TextView display3;
	TextView display4;
	TextView display5;
	TextView display6;
	TextView display7;
	
	public float Area;
	public float CentroidX;
	public float CentroidY;
	public float rSY;
	public float rSX;
	public float rIY;
	public float rIX;
	public float rIXY;
    public float rMIDx;
	public float rMIDy;
	public float xc;
    public float yc;
	
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        final FrameLayout frame = new FrameLayout(this);
        frame.addView(LayoutInflater.from(getBaseContext()).inflate(R.layout.resultsview, null));
        
        setContentView(frame);
        ActionBar actionBarr = getSupportActionBar();
        actionBarr.setDisplayHomeAsUpEnabled(true);
        
        Bundle extras = getIntent().getExtras();

		if (extras != null) {
			Area = extras.getFloat("rA");
			CentroidX = extras.getFloat("rCx");
			CentroidY = extras.getFloat("rCy");
			rSY = extras.getFloat("rSy");
			rSX = extras.getFloat("rSx");
			rIY = extras.getFloat("rIy");
			rIX = extras.getFloat("rIx");
			rIXY = extras.getFloat("rIxy");
			rMIDx = extras.getFloat("rMidx");
			rMIDy = extras.getFloat("rMidy");
		}
        CentroidX = (CentroidX/Area);
        CentroidY = (CentroidY/Area);
        xc = rSY/Area;
        yc = rSX/Area;

        float Ixc;
        float Iyc;
        float Ixyc;
        Ixc = rIX - yc*yc*Area;
        Iyc = rIY - xc*xc*Area;
        Ixyc= rIXY - xc*yc*Area;

        float Idel;
        float Isum;
        float Isqr;
        Idel = Iyc - Ixc;
        Isum = Iyc + Ixc;
        Isqr = FloatMath.sqrt(Idel*Idel + 4*Ixyc*Ixyc);

        float Ie;
        float In;
        Ie = (1/2) * (Isum + Isqr);
        In = (1/2) * (Isum - Isqr);

        
        display1 = (TextView) findViewById(R.id.testDisplay1);
        display1.setText("Profile Area = " + Area);
        display2 = (TextView) findViewById(R.id.testDisplay2);
        display2.setText("Centroid of Profile(screen ref) = (" + xc + "," + yc + ")");
        display3 = (TextView) findViewById(R.id.testDisplay3);
        display3.setText("First Moment Sx = " + rSX);
        display4 = (TextView) findViewById(R.id.testDisplay4);
        display4.setText("First Moment Sy = " + rSY);
        display5 = (TextView) findViewById(R.id.testDisplay5);
        display5.setText("Moments of Inertia x = " + Ixc);
        display6 = (TextView) findViewById(R.id.testDisplay6);
        display6.setText("Moments of Inertia y = " + Iyc);
        display7 = (TextView) findViewById(R.id.testDisplay7);
        display7.setText("Moments of Inertia xy = " + Ixyc);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.result, menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case android.R.id.home:
	        	setResult(RESULT_CANCELED);
	        	finish();
	            return true;
	        default:
	        	Toast.makeText(this, "Got click: " + item.toString(), Toast.LENGTH_SHORT).show();
	            return true;
	            //return super.onOptionsItemSelected(item);
	    }
	}
	
}
