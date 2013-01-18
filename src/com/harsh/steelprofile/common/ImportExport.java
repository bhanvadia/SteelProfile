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

package com.harsh.steelprofile.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.widget.EditText;
import android.widget.Toast;

import com.harsh.steelprofile.R;
import com.harsh.steelprofile.databases.Command;
import com.harsh.steelprofile.databases.CommandsDbAdapter;
import com.harsh.steelprofile.databases.Script;
import com.harsh.steelprofile.databases.ScriptsDbAdapter;

public class ImportExport {
	
	public static boolean Import(Context context, String fileName, String scriptName) {
		try {
			File myFile = new File(fileName);
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
						
			List<String> commands = new ArrayList<String>();
			
			boolean commandsValid = true;
			
			String command = null;
			
			while ((command = myReader.readLine()) != null) {
				commands.add(command);
				if (!Command.Validate(command)) {
					commandsValid = false;
					Toast.makeText(context, "Command '" + command + "' is not a valid.",
							Toast.LENGTH_LONG).show();
					break;
				}
			}
			
			myReader.close();
			
			if (!commandsValid) {
				return false;
			}
			
			ImportScriptAs(context, scriptName, commands);
		}
		catch (Exception e) {
			Toast.makeText(context, e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}
	
	private static void ImportScriptAs(Context context, String scriptName, List<String> commands) {
		ScriptsDbAdapter scriptsDbAdapter = new ScriptsDbAdapter(context);
		scriptsDbAdapter.open();
		
		Script script = scriptsDbAdapter.createScript(scriptName);
		
		CommandsDbAdapter commandsDbAdapter = new CommandsDbAdapter(context);
		commandsDbAdapter.open();
		
		for(int i=0; i<commands.size(); i++) {
			commandsDbAdapter.insertCommand(script.get_id(), i, commands.get(i));
		}
		
		commandsDbAdapter.close();
		scriptsDbAdapter.close();
	}
	
	public static void Export(Context context, File file, int databaseVersion, Script script, List<Command> commands) {
		try {
			String output = "";
			
			for (Command command : commands) {
				output += command.get_commandString() + "\r\n";
			}
			
			FileOutputStream fOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fOut);
			outWriter.append(output);
			outWriter.close();
			fOut.close();
			
			Toast.makeText(context, "Exported script to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
				
		} catch (IOException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	/*
	 * Initiates the Export dialog to get the file name to export the script and commands too.
	 * Once a valid name is chosen, export the script and commands
	 */
	public static void ExportDialog(final Context context, final int databaseVersion, final Script script, final List<Command> commands) {
		AlertDialog.Builder alertExport = new AlertDialog.Builder(context);

    	alertExport.setTitle(context.getResources().getString(R.string.menu_export));

		// Set an EditText view to get user input 
		final EditText inputExport = new EditText(context);
		inputExport.setText(script.get_name());
		alertExport.setView(inputExport);

		alertExport.setNegativeButton(context.getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});
		
		alertExport.setPositiveButton(context.getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			  String value = inputExport.getText().toString();
			  if(script != null) {
				  if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					  File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
							  context.getResources().getString(R.string.app_sdcard_path));
					  directory.mkdirs();
					  
					  File file = new File(directory, value + context.getResources().getString(R.string.script_extension));
					  
					  if (file.exists()) {
						  Toast.makeText(context, "File '" + value + 
								  context.getResources().getString(R.string.script_extension) + 
								  "' already exists.  Please try a different name", Toast.LENGTH_SHORT).show();
					  } else {
						  Export(context, file, databaseVersion, script, commands);
					  }
				  } else {
					  Toast.makeText(context, "SD Card is not mounted.  Please mount the SD Card", Toast.LENGTH_SHORT).show();
				  }
			  }
		  }
		});

		alertExport.show();
	}
}
