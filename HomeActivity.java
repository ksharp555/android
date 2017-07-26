package com.example.emergencymessaging;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends ActionBarActivity {
	Button chatButton, messgingButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		chatButton = (Button) findViewById(R.id.chatButton);
		messgingButton = (Button) findViewById(R.id.messagingButton);
		chatButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, ChatGroupsActivity.class);
				startActivity(intent);
			}
		});
		messgingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, EmergencyMessagingctivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_home, menu);
		return true;
	}

	public static final String MyPREFERENCES = "MyPrefs";

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.signout:
			finish();
			Intent intent = new Intent(HomeActivity.this, RegistrationActivity.class);
			SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
			sharedpreferences.edit().clear().commit();
			startActivity(intent);
			break;

		default:
		}

		return super.onOptionsItemSelected(item);
	}

}
