package edu.rit.csci759.pervasivemobile;

import edu.rit.csci759.pervasivemobile.logic.JSONHandler;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class GetRulesActivity extends Activity {

	static String serverURL_text = new String("10.10.10.103:8080");
	String rules[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_rules);

		new SendJSONRequest().execute();
		// navigate();
	}
     //populate the list view with rules in the rule block
	private void populate() {

		ListView rulelist = (ListView) findViewById(R.id.listView1);

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

	private void navigate() {
		Button backButton = (Button) findViewById(R.id.backButtonGetRules);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// startActivity(new
				// Intent(MainActivity.this,GetRulesActivity.class));
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.get_rules, menu);
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


	//async task to call get rules from the server
	class SendJSONRequest extends AsyncTask<Void, String, String> {
		String response_txt;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(Void... params) {

			response_txt = JSONHandler.testJSONRequest(serverURL_text,
					"getRules");
			rules = response_txt.split("---");
			return response_txt;
		}

		protected void onProgressUpdate(Integer... progress) {
			// setProgressPercent(progress[0]);
		}

		protected void onPostExecute(String result) {
			Log.d("debug", result);
			Log.d("debug", response_txt);
			populate();
		}

	}
}
