package edu.rit.csci759.pervasivemobile;

import edu.rit.csci759.pervasivemobile.logic.JSONHandler;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class Create_rule_activity extends Activity {
	Button createRule;
	String value = new String();
	String deleteThis;
	boolean isDelete = false;
	static String serverURL_text = new String("10.10.10.103:8080");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_rule_activity);

		// ComponentName component = this.getCallingActivity();
		Log.e("kuch bhi part 2", "Yup i am here");




		//extracting bundle to get source of request
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			String classname = bundle.getString("yup");
			/
			if (classname.equalsIgnoreCase("yup")) {

				deleteThis = bundle.getString("Update");
				Log.e("KCUS", deleteThis);
				isDelete = true;
			}
		}

		//updating the spinner with contents
		final Spinner temp = (Spinner) findViewById(R.id.spinner1);
		final Spinner condition = (Spinner) findViewById(R.id.spinner2);
		final Spinner ambient = (Spinner) findViewById(R.id.spinner3);
		final Spinner blind = (Spinner) findViewById(R.id.spinner4);
		createRule = (Button) findViewById(R.id.createButton);


		createRule.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String temperature = String.valueOf(temp.getSelectedItem());
				String condition_string = String.valueOf(condition
						.getSelectedItem());
				String ambient_string = String.valueOf(ambient
						.getSelectedItem());
				String blind_string = String.valueOf(blind.getSelectedItem());
				value = temperature + " " + condition_string + " "
						+ ambient_string + " " + blind_string;
				new SendJSONRequest().execute();
				if (isDelete)
					finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_rule_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class SendJSONRequest extends AsyncTask<Void, String, String> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(Void... params) {
			if (isDelete)
				JSONHandler.testJSONRequest(serverURL_text, deleteThis,
						"deleteRule");

			JSONHandler.testJSONRequest(serverURL_text, value, "addRule");

			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
			// setProgressPercent(progress[0]);
		}

		protected void onPostExecute(String result) {

		}

	}

}
