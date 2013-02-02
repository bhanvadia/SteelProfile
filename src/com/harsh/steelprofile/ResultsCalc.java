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

package com.harsh.steelprofile;

import java.util.HashMap;
import java.util.ArrayList;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ResultsCalc extends SherlockActivity {
	
	ListView list;
	
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
        
        // Further Calculations
        FurtherCalc();
        
        list = (ListView)findViewById(R.id.resultslist);  
        
        ArrayList<HashMap<String, String>> listresult = new ArrayList<HashMap<String, String>>();
        
        HashMap<String, String> map = new HashMap<String, String>();
		map.put("props", "Area");
		map.put("res", String.format("%.1f", Area/100));
		map.put("uni", "cm^2");
		listresult.add(map);
		
		map = new HashMap<String, String>();
		map.put("props", "Sx");
		map.put("res", String.format("%.1f", rSX/1000));
		map.put("uni", "cm^3");
		listresult.add(map);
		
		map = new HashMap<String, String>();
		map.put("props", "Sy");
		map.put("res", String.format("%.1f", rSY/1000));
		map.put("uni", "cm^3");
		listresult.add(map);
		
		map = new HashMap<String, String>();
		map.put("props", "I_x");
		map.put("res", String.format("%.0f", Ixc/10000));
		map.put("uni", "cm^4");
		listresult.add(map);
		
		map = new HashMap<String, String>();
		map.put("props", "I_y");
		map.put("res", String.format("%.0f", Iyc/10000));
		map.put("uni", "cm^4");
		listresult.add(map);
		
		map = new HashMap<String, String>();
		map.put("props", "I_xy");
		map.put("res", String.format("%.0f", Ixyc/10000));
		map.put("uni", "cm^4");
		listresult.add(map);
		
		map = new HashMap<String, String>();
		map.put("props", "I_\u03be");
		map.put("res", String.format("%.0f", Ie/10000));
		map.put("uni", "cm^4");
		listresult.add(map);
		
		map = new HashMap<String, String>();
		map.put("props", "I_\u03b7");
		map.put("res", String.format("%.0f", In/10000));
		map.put("uni", "cm^4");
		listresult.add(map);
		
         
        // listresult.add("Centroid of Profile(screen ref) = (" + xc + ", " + yc + ")");
         //Create an adapter for the listView and add the ArrayList to the adapter.
		SimpleAdapter mResult = new SimpleAdapter(this, listresult, R.layout.row,
	            new String[] {"props", "res", "uni"}, new int[] {R.id.property, R.id.results, R.id.units});
		list.setAdapter(mResult);
        
	}
	
	private void FurtherCalc()
	{
		//Importing values from DrawActivity
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
