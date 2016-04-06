package edu.rit.csci759.pervasivemobile;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import edu.rit.csci759.pervasivemobile.logic.JSONHandler;

public class UpdateRuleActivity extends Activity {

	static String serverURL_text = new String("10.10.10.103:8080");
	String rules[];
	ListView rulelist = null;
	String op = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_rule);
		//async task which is used to get all the rules from the database
		new SendJSONRequest().execute();
		//button for getting content of the rule to be updated and sends it to the next screen
		final Button deleteButton = (Button) findViewById(R.id.UpdateRules);
		deleteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("yup", "yup");
				Intent i = new Intent(UpdateRuleActivity.this,
						Create_rule_activity.class);
				SparseBooleanArray checked = rulelist.getCheckedItemPositions();
				for (int j = 0; j < rulelist.getCount(); j++) {
					if (checked.get(j)) {
						op = rules[j];
					}
				}
				Log.e("INUPDATE", op);
				bundle.putString("Update", op);
				i.putExtras(bundle);

				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_rule, menu);
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


	//populates the list view with the rules
	private void populate() {

		rulelist = (ListView) findViewById(R.id.listViewUpdate);

		ArrayAdapter<String> allRules = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, rules){
			
			@Override
			public View getView(int position,View convertView,ViewGroup parent){
				View view =super.getView(position, convertView, parent);
				LayoutParams parameters=view.getLayoutParams();
				parameters.height=140;
				view.setLayoutParams(parameters);
				return view;
				
			}
		};

		rulelist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	

		rulelist.setAdapter(allRules);
	}

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
