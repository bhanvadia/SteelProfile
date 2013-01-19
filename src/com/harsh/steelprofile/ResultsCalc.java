package com.harsh.steelprofile;


import java.util.List;
import java.util.ArrayList;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ResultsCalc extends SherlockActivity {
	
	ListView list;
    private List<String> List_result;
	
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
    public float Ixc;
    public float Iyc;
    public float Ixyc;
    public float Idel;
    public float Isum;
    public float Isqr;
    public float Ie;
    public float In;
	
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
        
        FurtherCalc();
        
        List_result =new ArrayList<String>();
        list = (ListView)findViewById(R.id.resultslist);        
        CreateListView();
        
	}
	
	private void FurtherCalc()
	{
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

        Ixc = rIX - yc*yc*Area;
        Iyc = rIY - xc*xc*Area;
        Ixyc= rIXY - xc*yc*Area;

        Idel = Iyc - Ixc;
        Isum = Iyc + Ixc;
        Isqr = FloatMath.sqrt((Idel*Idel) + 4*(Ixyc*Ixyc));

        Ie = (Isum + Isqr)/2;
        In = (Isum - Isqr)/2;
        
        xc = xc - rMIDx;
        yc = -(yc - rMIDy);
	}
	
	private void CreateListView()
    {
         List_result.add("Profile Area = " + Area/100 + " cm^2");
         List_result.add("Centroid of Profile(screen ref) = (" + xc + ", " + yc + ")");
         List_result.add("First Moment Sx = " + rSX/1000 + " cm^3");
         List_result.add("First Moment Sy = " + rSY/1000 + " cm^3");
         List_result.add("Moments of Inertia x = " + Ixc/10000 + " cm^4");
         List_result.add("Moments of Inertia y = " + Iyc/10000 + " cm^4");
         List_result.add("Moments of Inertia xy = " + Ixyc/10000 + " cm^4");
         List_result.add("Moments of Inertia zi = " + Ie/10000 + " cm^4");
         List_result.add("Moments of Inertia eta = " + In/10000 + " cm^4");
         //Create an adapter for the listView and add the ArrayList to the adapter.
         list.setAdapter(new ArrayAdapter<String>(ResultsCalc.this, android.R.layout.simple_list_item_1,List_result));
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
