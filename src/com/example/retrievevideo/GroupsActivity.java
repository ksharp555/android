package com.example.retrievevideo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.example.retrievevideo.R;
import com.example.retrievevideo.emergencymessaging.ContactDto;
import com.example.retrievevideo.emergencymessaging.DBHelper;

/**
 * Created by m.susmitha on 7/4/15.
 */
public class GroupsActivity extends ActionBarActivity {
	public static final int PICK_CONTACT = 1;
	LinearLayout parent;
	DBHelper dbHelper;
	int selectedGroupId = 0;
	int groupsSize = 3;
	String[] groups = new String[] { "Home", "Friends", "Work" };
	int[] ids = new int[] { R.drawable.home, R.drawable.friends, R.drawable.work };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String tag = "GroupsActivity";

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		parent = (LinearLayout) findViewById(R.id.parent);
		dbHelper = DBHelper.getInstance(getApplicationContext());
		updateGroupInfo();

	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		switch (reqCode) {
		case (PICK_CONTACT):
			if (resultCode == Activity.RESULT_OK) {

				Cursor c = null;
				try {
					Uri contactUri = data.getData();

					// String[] projection = {Phone.NUMBER,Phone.DISPLAY_NAME};

					// Perform the query on the contact to get the NUMBER column
					// We don't need a selection or sort order (there's only one
					// result for the given URI)
					// CAUTION: The query() method should be called from a
					// separate thread to avoid blocking
					// your app's UI thread. (For simplicity of the sample, this
					// code doesn't do that.)
					// Consider using CursorLoader to perform the query.
					Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
					cursor.moveToFirst();

					// Retrieve the phone number from the NUMBER column
					int column = cursor.getColumnIndex(Phone.NUMBER);
					int column1 = cursor.getColumnIndex(Phone.DISPLAY_NAME);
					String senderEmails = "";
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
							String name = cur1.getString(
									cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
							Log.e("Name :", name);
							String email = cur1
									.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
							Log.e("Email", email);
							emails[i] = email;
							i = i + 1;

						}

						String strEmails = TextUtils.join(",", emails);
						senderEmails = strEmails;
					}
					// Do something with the phone number...
					ContactDto contactDto = new ContactDto();
					contactDto.setName(cursor.getString(column1));
					contactDto.setNumber(cursor.getString(column));
					contactDto.setGroupid(selectedGroupId);
					contactDto.setEmailId(senderEmails);

					dbHelper.addContact(contactDto, getApplicationContext());
					updateGroupInfo();
				} finally {
				}

			}
			break;
		}
	}

	void updateGroupInfo() {
		parent.removeAllViews();
		for (int i = 0; i < groupsSize; i++) {
			View view = getLayoutInflater().inflate(R.layout.list_item_group_contained_persons, null);
			TextView heading = (TextView) view.findViewById(R.id.dateTextView);
			TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
			TextView extraInfoTextView = (TextView) view.findViewById(R.id.extraInfoTextView);
			ImageView add = (ImageView) view.findViewById(R.id.nextImageView);
			ImageView typeImageView = (ImageView) view.findViewById(R.id.typeImageView);

			List<ContactDto> contacts = dbHelper.getAllContactsByGroupId(i + 1, getApplicationContext());
			heading.setText(groups[i]);
			typeImageView.setImageResource(ids[i]);
			if (contacts != null && contacts.size() > 0) {
				titleTextView.setText(contacts.size() + "");
				String persons = "";
				for (ContactDto contactDto : contacts) {
					persons = persons + "\n " + contactDto.getName() + "-" + contactDto.getNumber() + " - Email:"
							+ contactDto.getEmailId();
				}
				extraInfoTextView.setText(persons);
			} else {
				titleTextView.setText("0");
				extraInfoTextView.setText("None");
			}

			final int j = i;
			add.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					selectedGroupId = j + 1;
					Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
					pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user
																	// only
																	// contacts
																	// w/ phone
																	// numbers
					startActivityForResult(pickContactIntent, PICK_CONTACT);
				}
			});

			parent.addView(view);
		}
	}

}
