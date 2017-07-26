package com.example.retrievevideo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import com.example.retrievevideo.emergencymessaging.NetworkOperation;
import com.example.retrievevideo.emergencymessaging.HttpResponseFormatDto;
import com.example.retrievevideo.emergencymessaging.NetworkUtils;

import com.example.retrievevideo.emergencymessaging.Utils;
import com.example.retrievevideo.BuildConfig;
import com.example.retrievevideo.ChatGroupsActivity;
import com.example.retrievevideo.R;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import dataAccessLayer.connectDB;

public class GroupChattingActivity extends ActionBarActivity {
	LinearLayout parent;
	String TAG = "GroupChattingActivity";
	List<String> messageList = new ArrayList<String>();
	
	String group = "";
	EditText messageEdt;
	Button sendButon;
	SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs";

	
    public static List<String> member = new ArrayList<String>();
    public static int flag=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatting);
		messageEdt = (EditText) findViewById(R.id.message);
		sendButon = (Button) findViewById(R.id.sendButton);
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

		sendButon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendButon.setEnabled(false);
				new SendMessageTask().execute();
			}
		});
		parent = (LinearLayout) findViewById(R.id.parent);
		group = getIntent().getStringExtra("group");
		new RetrieveGroupsTask().execute();
		enableResendTextWithTimer();

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.show_members, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.showMembers:
			new NetworkOperation().execute();
			
			Thread th1=new Thread(){
				@Override
    			public void run(){
					
					try{
						sleep(4000);
						Log.i("doing query", "processing.....");
						Log.i("groupName", ChatGroupsActivity.groupName);
					String memberQuery="select username from users, usergroup, groupusers where users.userid=groupusers.userid and usergroup.groupid=groupusers.groupid and usergroup.groupname="+"'"+ChatGroupsActivity.groupName+"'";
					Log.i("after query", "processing.....");
					Statement  statementMedia = NetworkOperation.dbconn.createStatement();
					Log.i("after statement", "processing.....");
					ResultSet rs=statementMedia.executeQuery(memberQuery);
					Log.i("after rs", "processing.....");
					
					int i=0;
					member=new ArrayList<String>();
					Log.i("before while ", "processing.....");
					while(rs.next()){
						member.add(i, rs.getString("username"));
						i++;
					}
					Log.i("set value to member", "processing.....");
					for(i=0;i<member.size();i++){
						Log.i("member",member.get(i));
					}
					flag=1;
					
					}catch (Exception err) { 
						Log.i("set value to member", err.getMessage());
    		        }
				}
			};
			th1.start();
			do{
				
			}
			while(flag==0);
			flag=0;
			AlertDialog.Builder builder = new AlertDialog.Builder(GroupChattingActivity.this);
			LayoutInflater inflater = getLayoutInflater();
			View convertView = (View) inflater.inflate(R.layout.listgroupmember, null);
			
			builder.setTitle("List_Member");
			builder.setView(convertView);
			ListView lv = (ListView) convertView.findViewById(R.id.listView1);
		    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
					GroupChattingActivity.this,
                    android.R.layout.select_dialog_singlechoice);
			for(int i=0;i<member.size();i++){
				arrayAdapter.add(member.get(i));
			}
			lv.setAdapter(arrayAdapter);
			builder.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            
                        }
                    });
			 /*builder.setAdapter(arrayAdapter,
	                    new DialogInterface.OnClickListener() {

	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            String strName = arrayAdapter.getItem(which);
	                            AlertDialog.Builder builderInner = new AlertDialog.Builder(
	                            		GroupChattingActivity.this);
	                            builderInner.setMessage(strName);
	                            builderInner.setTitle("Your Selected Item is");
	                            builderInner.setPositiveButton("Ok",
	                                    new DialogInterface.OnClickListener() {

	                                        @Override
	                                        public void onClick(
	                                                DialogInterface dialog,
	                                                int which) {
	                                            dialog.dismiss();
	                                        }
	                                    });
	                            builderInner.show();
	                        }
	                    });*/
	            builder.show();
			break;

		

		default:
		}

		return super.onOptionsItemSelected(item);
	}
	
	void updateGroupInfo() {
		parent.removeAllViews();

		for (int i = 0; i < messageList.size(); i++) {
			View view = getLayoutInflater().inflate(R.layout.list_item_chat_group, null);
			TextView heading = (TextView) view.findViewById(R.id.groupName);
			heading.setText(messageList.get(i));
			final int j = i;
			parent.addView(view);
		}
	}

	private void enableResendTextWithTimer() {
		if (parent != null)
			parent.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (parent != null) {
						new RetrieveGroupsTask().execute();
						enableResendTextWithTimer();
					}
				}
			}, 8000);
	}

	class RetrieveGroupsTask extends AsyncTask<Void, Void, Void> {
		boolean success;
		String message;
		HttpResponseFormatDto httpResponseFormatDto;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// Utils.showProgressDialog(GroupChattingActivity.this,
			// "Getting...");
		}

		public RetrieveGroupsTask() {

		}

		@Override
		protected Void doInBackground(Void... params) {
			String regJson = getRetrieveGroupsBody();

			try {
				httpResponseFormatDto = NetworkUtils.getPOSTResponse(getApplicationContext(),
						"http://104.236.202.116/php/upload/retrivetext.php", regJson, Utils.getHeaderParams(regJson));
				// message = Utils.isErrorOrSuccess(httpResponseFormatDto);
				// if (message != null) {
				// success = false;
				// } else {
				/*
				 * [ { "textMessage": "hi", "groupname": "group2", "userName":
				 * "user2                                             " }, {
				 * "textMessage": "hi", "groupname": "group2", "userName":
				 * "user2                                             " }, {
				 * "textMessage": "hi", "groupname": "group2", "userName":
				 * "user2                                             " }, {
				 * "textMessage": "hi", "groupname": "group2", "userName":
				 * "user2                                             " }, {
				 * "textMessage": "hi", "groupname": "group2", "userName":
				 * "user2                                             " }, {
				 * "textMessage": "dushhd", "groupname": "group2", "userName":
				 * "User1                                             " }, {
				 * "textMessage": "heidhda", "groupname": "group2", "userName":
				 * "User1                                             " }, {
				 * "textMessage": "first login ", "groupname": "group2",
				 * "userName":
				 * "User1                                             " }, {
				 * "textMessage": "gdjdiis", "groupname": "group2", "userName":
				 * "User1                                             " }, {
				 * "textMessage": "dhcj", "groupname": "group2", "userName":
				 * "User1                                             " } ]
				 */
				success = true;
				messageList.clear();
				if (httpResponseFormatDto != null && httpResponseFormatDto.getData() != null) {
					JSONArray groups = new JSONArray(httpResponseFormatDto.getData());
					if (groups != null) {

						for (int i = 0; i < groups.length(); i++) {
							JSONObject group = groups.getJSONObject(i);
							if (group != null) {
								String textMessage = group.getString("textMessage");
								String userName = group.getString("userName");

								GroupChattingActivity.this.messageList.add(userName.trim() + " : " + textMessage);
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

			// Utils.dismissProgressDialog();
			if (success) {
				updateGroupInfo();
			} else {
				Utils.showOkAlertAndFinishBasedOnFlag(GroupChattingActivity.this, "Failure", message, false);
			}
		}
	}

	String getRetrieveGroupsBody() {
		return "groupname=" + group;
	}

	class SendMessageTask extends AsyncTask<Void, Void, Void> {
		boolean success;
		String message;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// Utils.showProgressDialog(GroupChattingActivity.this,
			// "Checking...");
		}

		public SendMessageTask() {

		}

		@Override
		protected Void doInBackground(Void... params) {
			String regJson = gesendMessageBody();
			HttpResponseFormatDto httpResponseFormatDto;
			try {
				/*
				 * Connected successfully New record created
				 */
				httpResponseFormatDto = NetworkUtils.getPOSTResponse(getApplicationContext(),
						"http://104.236.202.116/php/upload/uploadtext.php", regJson, Utils.getHeaderParams(regJson));
				// message = Utils.isErrorOrSuccess(httpResponseFormatDto);
				// if (message != null) {
				// success = false;
				// } else {
				success = true;
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
			sendButon.setEnabled(true);

			// Utils.dismissProgressDialog();
			if (success) {
				messageList.add(sharedpreferences.getString("username", "") + " : " + messageEdt.getText().toString());
				updateGroupInfo();
				messageEdt.setText("");
				// onLoginSuccess();
			} else {
				Utils.showOkAlertAndFinishBasedOnFlag(GroupChattingActivity.this, "Failure", message, false);
			}
		}
	}

	String gesendMessageBody() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

		String formatted = format1.format(cal.getTime());

		return "groupname=" + group + "&" + "message=" + messageEdt.getText().toString() + "&" + "username="
				+ sharedpreferences.getString("username", "") + "&" + "senddate=" + formatted;
	}
	 
	
    
}
