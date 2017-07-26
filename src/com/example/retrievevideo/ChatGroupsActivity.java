package com.example.retrievevideo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.retrievevideo.BuildConfig;
import com.example.retrievevideo.R;
import com.example.retrievevideo.emergencymessaging.HttpResponseFormatDto;
import com.example.retrievevideo.emergencymessaging.NetworkUtils;
import com.example.retrievevideo.emergencymessaging.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ChatGroupsActivity extends ActionBarActivity {
	LinearLayout parent;
	String TAG = "ChatGroupsActivity";
	List<String> groupsList = new ArrayList<String>();
	SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs";
    public static String groupName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_groups);
		parent = (LinearLayout) findViewById(R.id.parent);
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		new RetrieveGroupsTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_groups, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.createGroup:
			addGroup();
			break;

		case R.id.joinGroup:
			break;

		default:
		}

		return super.onOptionsItemSelected(item);
	}

	void updateGroupInfo() {
		parent.removeAllViews();
		for (int i = 0; i < groupsList.size(); i++) {
			View view = getLayoutInflater().inflate(R.layout.list_item_chat_group, null);
			TextView heading = (TextView) view.findViewById(R.id.groupName);
			heading.setText(groupsList.get(i));
			final int j = i;
			heading.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String selectedGroupName = groupsList.get(j);
					groupName=selectedGroupName;
					Log.v(TAG, "sleceted group " + selectedGroupName);
					Intent intent = new Intent(ChatGroupsActivity.this, GroupChattingActivity.class);
					intent.putExtra("group", selectedGroupName);
					startActivity(intent);
				}
			});
			parent.addView(view);
		}
	}

	class RetrieveGroupsTask extends AsyncTask<Void, Void, Void> {
		boolean success;
		String message;
		HttpResponseFormatDto httpResponseFormatDto;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Utils.showProgressDialog(ChatGroupsActivity.this, "Getting...");
		}

		public RetrieveGroupsTask() {

		}

		@Override
		protected Void doInBackground(Void... params) {
			String regJson = getRetrieveGroupsBody();

			try {
				httpResponseFormatDto = NetworkUtils.getPOSTResponse(getApplicationContext(),
						"http://104.236.202.116/php/upload/groupretrive.php", regJson, Utils.getHeaderParams(regJson));
				// message = Utils.isErrorOrSuccess(httpResponseFormatDto);
				// if (message != null) {
				// success = false;
				// } else {
				/*
				 * success = true; [ { "groupname": "group2" }, { "groupname":
				 * "group008" }, { "groupname": "UTPA" }, { "groupname":
				 * "meeting" }, { "groupname": "group1" }, { "groupname":
				 * "maniram" } ]
				 */
				success = true;
				if (httpResponseFormatDto != null && httpResponseFormatDto.getData() != null) {
					JSONArray groups = new JSONArray(httpResponseFormatDto.getData());
					if (groups != null) {
						for (int i = 0; i < groups.length(); i++) {
							JSONObject group = groups.getJSONObject(i);
							if (group != null) {
								String groupName = group.getString("groupname");
								ChatGroupsActivity.this.groupsList.add(groupName);
							}
						}
					}
				}
				// }
			} catch (MalformedURLException e) {
				Log.e(TAG, "Bad URL: " + e.toString(), e);
				message = "Invalid url.";
			} catch (IOException e) {
				Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);
				message = "Failed to register due to network error.";
			} catch (JSONException e) {
				Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
				message = "Invalid reponse from the server.";
			} catch (ParseException e) {
				Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
				message = "Invalid reponse from the server.";
			} catch (Exception e) {
				Log.e(TAG, "some error while registering : " + e.toString(), e);
				message = "error : " + e.getMessage();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "got registration response.");
			}

			Utils.dismissProgressDialog();
			if (success) {
				updateGroupInfo();
			} else {
				Utils.showOkAlertAndFinishBasedOnFlag(ChatGroupsActivity.this, "Failure", message, false);
			}
		}
	}

	String getRetrieveGroupsBody() {
		return "username=" + sharedpreferences.getString("username", "");
	}

	public void addGroup() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ChatGroupsActivity.this);
		final EditText editText = new EditText(ChatGroupsActivity.this);
		editText.setHint("Enter group name");
		builder.setView(editText);
		builder.setTitle("create group!");
		builder.setMessage("Please enter group name:");
		builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.v(TAG, "craete group " + editText.getText().toString());
				dialog.dismiss();
				new CreateGroupTask(editText.getText().toString()).execute();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.v(TAG, "join group");
				dialog.dismiss();

			}
		}).create();
		AlertDialog alertDialog= builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.setCancelable(false);
		alertDialog.show();
	}

	class CreateGroupTask extends AsyncTask<Void, Void, Void> {
		boolean success;
		String gname;
		String message;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// Utils.showProgressDialog(GroupChattingActivity.this,
			// "Checking...");
		}

		public CreateGroupTask(String gname) {
			this.gname = gname;
		}

		@Override
		protected Void doInBackground(Void... params) {
			String regJson = getCreateGroupBody(gname);
			HttpResponseFormatDto httpResponseFormatDto;
			try {
				/*
				 * sender id is User1,group2222user id is 1groupid is 35New
				 * record created
				 * 
				 */
				httpResponseFormatDto = NetworkUtils.getPOSTResponse(getApplicationContext(),
						"http://104.236.202.116/php/upload/creategroup.php", regJson, Utils.getHeaderParams(regJson));
				// message = Utils.isErrorOrSuccess(httpResponseFormatDto);
				// if (message != null) {
				// success = false;
				// } else {
				if (httpResponseFormatDto != null && httpResponseFormatDto.getData() != null&&!httpResponseFormatDto.getData().contains("ERROR")) {
					success = true;
				}else{
					success=false;
					message=httpResponseFormatDto.getData();
				}
				// }
			} catch (MalformedURLException e) {
				Log.e(TAG, "Bad URL: " + e.toString(), e);
				message = "Invalid url.";
			} catch (IOException e) {
				Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);
				message = "Failed to register due to network error.";
			} catch (JSONException e) {
				Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
				message = "Invalid reponse from the server.";
			} catch (ParseException e) {
				Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
				message = "Invalid reponse from the server.";
			} catch (Exception e) {
				Log.e(TAG, "some error while registering : " + e.toString(), e);
				message = "error : " + e.getMessage();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "got registration response.");
			}

			// Utils.dismissProgressDialog();
			if (success) {
				Toast.makeText(ChatGroupsActivity.this, "Group created successfully!", Toast.LENGTH_LONG).show();
				// onLoginSuccess();
			} else {
				if(message.contains("exist")){
					message="Group already exists.";
				}
				Utils.showOkAlertAndFinishBasedOnFlag(ChatGroupsActivity.this, "Failure", message, false);
			}
		}
	}

	String getCreateGroupBody(String gname) {
		return "groupname=" + gname + "&username=" + sharedpreferences.getString("username", "");

	}

}
