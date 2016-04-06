package edu.rit.csci759.pervasivemobile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/*
The Main Activity displays the home page of the application
 */
public class MainActivity extends Activity


	private static final int TEMPPORT = 8090;
	Socket client;
	Button mainButton;
	private BufferedReader in;
	TextView temp;
	TextView ambi;
	TextView blind;
	String op[];
	static int notification_id = 0;
	String blindPosition = "";
	boolean serverAvailable = true;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		temp = (TextView) findViewById(R.id.textViewtemp);

		ambi = (TextView) findViewById(R.id.textViewambi);

		blind = (TextView) findViewById(R.id.textViewblind);


		//New thread to get updates from the server
		Thread thread = new Thread() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {

				try {

					client = new Socket("10.10.10.103", TEMPPORT);

					in = new BufferedReader(new InputStreamReader(
							client.getInputStream()));
				} catch (Exception e) {
					serverAvailable = false;

				}

				while (serverAvailable) {
					try {

						//displays the updated temperature
						String str = in.readLine();
						op = str.split(" ");
						op[0] = op[0] + " " + (char) 0x00B0 + "C";

					} catch (Exception serverOff) {
						serverAvailable = false;

					}

					//this updates the UI
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								temp.setText(op[0]);
								ambi.setText(op[1]);
								blind.setText(op[2]);
							} catch (Exception e) {
								serverAvailable = false;
							}
						}
					});

					//writes the updated the blind position to the database for maintaining
					//history purposes
					try {
						if (!blindPosition.equals(op[2])) {
							writeToDatabase(op[0], op[1], op[2]);
							blindPosition = op[2];
							//creates a new notification for the blind updates
							NotificationManager UIupdater = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
							@SuppressWarnings("deprecation")
							Notification ui_notification = new Notification(
									R.drawable.abc_ic_menu_selectall_mtrl_alpha,
									"Blind Update", System.currentTimeMillis());

							//creating a Intent to get to the main page after clicking the notification
							Intent his_intent = new Intent(MainActivity.this,
									MainActivity.class);
							PendingIntent p = PendingIntent.getActivity(
									getApplication(), 0, his_intent, 0);
							ui_notification.setLatestEventInfo(
									getApplication(),

									"Blind Update", "Blind is now " + op[2], p);
							UIupdater
									.notify(notification_id++, ui_notification);

						}
					} catch (Exception e) {
						serverAvailable = false;

					}
				}
				//if server not available then return an error message
				if (!serverAvailable) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getBaseContext(),
									"Cannot communicate with the Server",
									Toast.LENGTH_LONG).show();
							// finish();
						}
					});
				}

			}
		};
		thread.start();
         //creating a personalized button and go to other activity after clicking it
		mainButton = (Button) findViewById(R.id.mainbutton);

		mainButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				startActivity(new Intent(MainActivity.this,
						Other_activity.class));
			}
		});
	}

	//write the data to database

	public void writeToDatabase(String temperature_update, String light_update,
			String blind_update) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss    ");
		Calendar cam = Calendar.getInstance();

		try {
			String toOutput = new String();
			toOutput = df.format(cam.getTime()) + "   " + temperature_update
					+ "   " + light_update + "   " + blind_update + "---";
			FileOutputStream op = openFileOutput("Db.txt", MODE_APPEND);
			op.write(toOutput.getBytes());
			// readFromDatabase();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//reading from the database

	public String[] readFromDatabase() {
		ArrayList<String> history = new ArrayList<String>();

		try {
			FileInputStream fis = openFileInput("Db.txt");
			InputStreamReader is = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(is);
			String text;
			while ((text = br.readLine()) != null) {
				history.add(text);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String hist[] = new String[history.size()];
		history.toArray(hist);
		return hist;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

}
