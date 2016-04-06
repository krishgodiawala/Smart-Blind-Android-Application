package edu.rit.csci759.pervasivemobile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class History extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		populate();
	}

	public String[] readFromDatabase() {

		String hist[] = null;
		try {
			FileInputStream fis = openFileInput("Db.txt");
			InputStreamReader is = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(is);

			String text;
			String app = "";
			while ((text = br.readLine()) != null) {
				app = app + "---" + text;
			}
			hist = app.split("---");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (hist == null)
			return null;

		return hist;

	}


	//read from the database
	public void populate() {
		String rules[] = readFromDatabase();
		if (rules != null) {
			ListView rulelist = (ListView) findViewById(R.id.listViewHistory);
			ArrayAdapter<String> allRules = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, rules){
						
						@Override
						public View getView(int position,View convertView,ViewGroup parent){
							View view =super.getView(position, convertView, parent);
							LayoutParams parameters=view.getLayoutParams();
							parameters.height=140;
							
							view.setLayoutParams(parameters);
							return view;
							
						}
					};
			rulelist.setAdapter(allRules);
		}
	}


	//clearing the database after clicking the clear history action bar
	public void clearDatabase() {
		try {
			FileOutputStream op = openFileOutput("Db.txt", MODE_PRIVATE);
			String st = new String();
			op.write(st.getBytes());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 populate();
	}

	@Override
	public boolean onKeyDown(int code, KeyEvent event) {
		if (code == KeyEvent.KEYCODE_BACK)
			finish();
		return super.onKeyDown(code, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		clearDatabase();
		return true;
	}
}
