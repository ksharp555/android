package com.example.retrievevideo;

import java.util.ArrayList;
import java.util.List;

import com.example.retrievevideo.R;
import com.example.retrievevideo.emergencymessaging.ContactDto;
import com.example.retrievevideo.emergencymessaging.DBHelper;
import com.example.retrievevideo.emergencymessaging.Mail;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EmergencyMessagingctivity extends ActionBarActivity {
	public static final int PICK_CONTACT = 1;

	Button emergencyButton;
	Button sendMessageButton;
	Spinner groupsSpinner;
	EditText enterInputMsg;
	TextView selectedNumber;
	int selectedGroupId = -1;
	String senderNumber = "";
	String senderEmails = "";
	DBHelper dbHelper;
	Handler handler;
	EditText to, subject, body;
	Button sendEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_main);
		handler = new Handler();
		emergencyButton = (Button) findViewById(R.id.emergencyButton);
		sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
		groupsSpinner = (Spinner) findViewById(R.id.groupSpinner);
		enterInputMsg = (EditText) findViewById(R.id.enterInputMsg);
		selectedNumber = (TextView) findViewById(R.id.selectedNumber);
		dbHelper = DBHelper.getInstance(getApplicationContext());

		to = (EditText) findViewById(R.id.to);
		subject = (EditText) findViewById(R.id.subject);
		body = (EditText) findViewById(R.id.body);
		sendEmail = (Button) findViewById(R.id.sendEmailButton);

		ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				getTypeNames());
		typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		groupsSpinner.setAdapter(typeSpinnerAdapter);
		groupsSpinner.setSelection(0);
		groupsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedGroupId = position;
				if (position == 4) {
					Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
					pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user
																	// only
																	// contacts
																	// w/ phone
																	// numbers
					startActivityForResult(pickContactIntent, PICK_CONTACT);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		emergencyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<ContactDto> contacts = dbHelper.getAllContactsByGroupId(1, getApplicationContext());
				if (contacts != null && contacts.size() > 0) {
					for (int i = 0; i < contacts.size(); i++) {
						sendsms(contacts.get(i).getNumber(), "Iam in danger");
						sendEmails(contacts.get(i).getEmailId(), "Iam in danger", "Iam in danger");
					}
					Toast.makeText(EmergencyMessagingctivity.this, "Done.", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(EmergencyMessagingctivity.this, "No persons in the group.", Toast.LENGTH_LONG).show();
				}
			}
		});

		// sendEmail.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Myclass myclass = new Myclass();
		// myclass.execute();
		// }
		// });

		sendMessageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String message = enterInputMsg.getText().toString();
				if (!TextUtils.isEmpty(message)) {
					sendMessageButton.setEnabled(false);

					if (selectedGroupId == 0) {
						Toast.makeText(EmergencyMessagingctivity.this, "Please select group.", Toast.LENGTH_LONG).show();
					} else if (selectedGroupId == 4) {
						sendsms(senderNumber, message);
						sendEmails(senderEmails, message, message);
					} else {
						List<ContactDto> contacts = dbHelper.getAllContactsByGroupId(selectedGroupId,
								getApplicationContext());
						if (contacts != null && contacts.size() > 0) {
							for (int i = 0; i < contacts.size(); i++) {
								sendsms(contacts.get(i).getNumber(), message);
								sendEmails(senderEmails, message, message);
							}
							Toast.makeText(EmergencyMessagingctivity.this, "Done.", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(EmergencyMessagingctivity.this, "No persons in the group.", Toast.LENGTH_LONG).show();
						}
					}

					selectedNumber.setText("");
					enterInputMsg.setText("");
					groupsSpinner.setSelection(0);
					sendMessageButton.setEnabled(true);
				} else {
					Toast.makeText(EmergencyMessagingctivity.this, "Enter message", Toast.LENGTH_LONG).show();

				}
			}
		});
	}

	private List<String> getTypeNames() {
		List<String> names = new ArrayList<String>();
		names.add("Select destination group/person");
		names.add("HOME");
		names.add("FRIENDS");
		names.add("WORK");
		names.add("Select Other Number");
		return names;
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

		// noinspection SimplifiableIfStatement
		if (id == R.id.addPerson) {
			Intent intent = new Intent(EmergencyMessagingctivity.this, GroupsActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request it is that we're responding to
		if (requestCode == PICK_CONTACT) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				// Get the URI that points to the selected contact
				Uri contactUri = data.getData();
				// We only need the NUMBER column, because there will be only
				// one row in the result
				String[] projection = { Phone.NUMBER, Phone.DISPLAY_NAME, Email.DATA };

				// Perform the query on the contact to get the NUMBER column
				// We don't need a selection or sort order (there's only one
				// result for the given URI)
				// CAUTION: The query() method should be called from a separate
				// thread to avoid blocking
				// your app's UI thread. (For simplicity of the sample, this
				// code doesn't do that.)
				// Consider using CursorLoader to perform the query.
				Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
				cursor.moveToFirst();

				// Retrieve the phone number from the NUMBER column
				int column = cursor.getColumnIndex(Phone.NUMBER);
				int column1 = cursor.getColumnIndex(Phone.DISPLAY_NAME);
				String id = cursor.getString(cursor.getColumnIndex(Phone.CONTACT_ID));
				ContentResolver cr = getContentResolver();
				Cursor cur1 = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
						ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { id }, null);
				// Cursor cur1 =
				// cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
				// null, null, null, null);
				if (cur1 != null && cur1.getCount() > 0) {
					String[] emails = new String[cur1.getCount()];
					int i = 0;
					while (cur1.moveToNext()) {
						// to get the contact names
						String name = cur1
								.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						Log.e("Name :", name);
						String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						Log.e("Email", email);
						emails[i] = email;
						i = i + 1;

					}

					String strEmails = TextUtils.join(",", emails);
					senderEmails = strEmails;
				}
				senderNumber = cursor.getString(column);
				selectedNumber
						.setText(cursor.getString(column1) + " - " + senderNumber + "\n " + "email : " + senderEmails);

				// Do something with the phone number...
			}
		}
	}

	// @Override
	// public void onActivityResult(int reqCode, int resultCode, Intent data) {
	// super.onActivityResult(reqCode, resultCode, data);
	// switch (reqCode) {
	// case (PICK_CONTACT):
	// if (resultCode == Activity.RESULT_OK) {
	//
	// Cursor c=null;
	// try {
	// Uri contactData = data.getData();
	// String id = contactData.getLastPathSegment();
	// Cursor cursor = getContentResolver()
	// .query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=?",
	// new String[]{id}, null);
	// senderNumber = cursor.getString(cursor.getColumnIndex(Phone.DATA));
	// selectedNumber.setText(c.getString(c.getColumnIndexOrThrow(Phone.DISPLAY_NAME))+"
	// - "+c.getString(c.getColumnIndexOrThrow(Phone.DATA)));

	// c = managedQuery(contactData, null, null, null, null);
	// if (c.moveToFirst()) {
	// senderNumber=c.getString(c.getColumnIndexOrThrow(Phone.NUMBER))+"";
	// selectedNumber.setText(c.getString(c.getColumnIndexOrThrow(Phone.DISPLAY_NAME))+"
	// - "+c.getInt(c.getColumnIndexOrThrow(Phone.NUMBER)));
	// }
	// }finally {
	// }
	//
	// }
	// break;
	// }
	// }

	void sendsms(String destinationAddress, String text) {
		try {

			String SENT = "sent";
			String DELIVERED = "delivered";

			Intent sentIntent = new Intent(SENT);
			/* Create Pending Intents */
			PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, sentIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			Intent deliveryIntent = new Intent(DELIVERED);

			PendingIntent deliverPI = PendingIntent.getBroadcast(getApplicationContext(), 0, deliveryIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			/* Register for SMS send action */
			registerReceiver(new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					String result = "";

					switch (getResultCode()) {

					case Activity.RESULT_OK:
						result = "Sending successful";
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						result = "Transmission failed";
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						result = "Radio off";
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						result = "No PDU defined";
						break;
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						result = "No service";
						break;
					}

					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
				}

			}, new IntentFilter(SENT));

			/* Register for Delivery event */
			registerReceiver(new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					Toast.makeText(getApplicationContext(), "Deliverd", Toast.LENGTH_LONG).show();
				}

			}, new IntentFilter(DELIVERED));

			/* Send SMS */
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(destinationAddress, null, text, sentPI, deliverPI);
		} catch (Exception ex) {
			Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
			ex.printStackTrace();
		}
	}

	// void sendEmail() {
	// // TODO Auto-generated method stub
	// // new Runnable() {
	// //
	// // @Override
	// // public void run() {
	// try {
	// GMailSender sender = new GMailSender("susmitha.maddula@gmail.com",
	// "commercial12345");
	// sender.sendMail("This is testing Subject", "This is testing Body",
	// "susmitha.maddula@gmail.com",
	// "susmitha.maddula@gmail.com");
	// } catch (Exception e) {
	// Log.e("SendMail", e.getMessage(), e);
	// }
	// // }
	// // };
	//
	// }

	class Myclass extends AsyncTask<Void, Void, Void> {
		String toDest, subjectStr, bodyStr;

		public Myclass(String toDest, String subjectStr, String bodyStr) {
			this.toDest = toDest;
			this.subjectStr = subjectStr;
			this.bodyStr = bodyStr;
		}

		@Override
		protected Void doInBackground(Void... params) {
			sendEmail(toDest, subjectStr, bodyStr);
			return null;
		}
	}

//	void sendEmail2() {
//
////		Mail m = new Mail("tatinenimaniram@gmail.com", "manijanu8132");
//		Mail m = new Mail("emergencymessaging@gmail.com", "utrgv123");
//
//
//		String dest = to.getText().toString();
//		String[] toArr = { dest };
//		m.set_to(toArr);
//		m.set_from("tatinenimaniram@gmail.com");
//		m.set_subject(subject.getText().toString());
//		m.setBody(body.getText().toString());
//
//		try {
//			// m.addAttachment("/sdcard/filelocation");
//
//			if (m.send()) {
//				handler.post(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						Toast.makeText(EmergencyMessagingctivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
//					}
//				});
//
//			} else {
//				handler.post(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						Toast.makeText(EmergencyMessagingctivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
//					}
//				});
//				Toast.makeText(EmergencyMessagingctivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
//			}
//		} catch (Exception e) {
//			// Toast.makeText(MailApp.this, "There was a problem sending the
//			// email.", Toast.LENGTH_LONG).show();
//			Log.e("MailApp", "Could not send email", e);
//		}
//
//	}

	void sendEmails(String toDest, String subjectStr, String bodyStr) {
		Myclass myclass = new Myclass(toDest, subjectStr, bodyStr);
		myclass.execute();
	}

	void sendEmail(String toDest, String subjectStr, String bodyStr) {

//		Mail m = new Mail("tatinenimaniram@gmail.com", "manijanu8132");
		Mail m = new Mail("emergencymessaging@gmail.com", "utrgv123");

		String dest = toDest;
		String[] toArr = { dest };
		m.set_to(toArr);
		m.set_from("tatinenimaniram@gmail.com");
		m.set_subject(subjectStr);
		m.setBody(bodyStr);

		try {
			// m.addAttachment("/sdcard/filelocation");

			if (m.send()) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(EmergencyMessagingctivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
					}
				});

			} else {
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(EmergencyMessagingctivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
					}
				});
				Toast.makeText(EmergencyMessagingctivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			// Toast.makeText(MailApp.this, "There was a problem sending the
			// email.", Toast.LENGTH_LONG).show();
			Log.e("MailApp", "Could not send email", e);
		}

	}

}
