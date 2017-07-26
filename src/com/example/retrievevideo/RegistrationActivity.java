package com.example.retrievevideo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Locale;

import org.json.JSONException;


import com.example.retrievevideo.BuildConfig;
import com.example.retrievevideo.R;
import com.example.retrievevideo.emergencymessaging.HttpResponseFormatDto;
import com.example.retrievevideo.emergencymessaging.NetworkUtils;
import com.example.retrievevideo.emergencymessaging.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends Activity {
	String TAG = "RegistrationActivity";
	public static final String MyPREFERENCES = "MyPrefs";

	// SettingsDao settingsDao;
	Button registerButton;
	EditText usernameEdt, passwordEdt;
	SharedPreferences sharedpreferences;
	Locale myLocale;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		// settingsDao = SettingsDao.getInstance(getApplicationContext());
		registerButton = (Button) findViewById(R.id.registerBtn);
		usernameEdt = (EditText) findViewById(R.id.username_edt);
		passwordEdt = (EditText) findViewById(R.id.password_edt);
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		if (sharedpreferences.getBoolean("reg", false)) {
			Intent intent = new Intent(RegistrationActivity.this, HomeTabActivity.class);
			startActivity(intent);
			finish();
		}
		// institution_id_edt.setText("9ebb0fdbb73f46c88322509d0d812913");
	}
	
	public void setLocale(String lang) {
		 
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, RegistrationActivity.class);
        startActivity(refresh);
    }

	
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	public void onLoginButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, "Reg button clickd");
		}
		Utils.hideKeyboard(view, this);
		if (TextUtils.isEmpty(usernameEdt.getText().toString())) {
			Toast.makeText(this, "Please enter valid Username.", Toast.LENGTH_LONG).show();
		} else if (TextUtils.isEmpty(passwordEdt.getText().toString())) {
			Toast.makeText(this, "Please enter valid password.", Toast.LENGTH_LONG).show();
		} else {
			// login the device
			new LoginTask().execute();

		}
	}

	void onLoginSuccess() {
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putBoolean("reg", true);
		editor.putString("username", usernameEdt.getText().toString() );
		editor.commit();
		Toast.makeText(this, "Login successful..", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(RegistrationActivity.this, HomeTabActivity.class);
		startActivity(intent);
		finish();
	}

	public void onRegisterButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, "Reg button clickd");
		}
		Utils.hideKeyboard(view, this);
		if (TextUtils.isEmpty(usernameEdt.getText().toString())) {
			Toast.makeText(this, "Please enter valid Username.", Toast.LENGTH_LONG).show();
		} else if (TextUtils.isEmpty(passwordEdt.getText().toString())) {
			Toast.makeText(this, "Please enter valid password.", Toast.LENGTH_LONG).show();
		} else {
			// register the device
			SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putBoolean("reg", true);
			editor.commit();
			Toast.makeText(this, "Registartion successful..", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(RegistrationActivity.this, HomeTabActivity.class);
			startActivity(intent);
			finish();
		}

	}

	class LoginTask extends AsyncTask<Void, Void, Void> {
		boolean success;
		String message;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Utils.showProgressDialog(RegistrationActivity.this, "Checking...");
		}

		public LoginTask() {

		}

		@Override
		protected Void doInBackground(Void... params) {
			String regJson = getLoginBody();
			HttpResponseFormatDto httpResponseFormatDto;
			try {
				httpResponseFormatDto = NetworkUtils.getPOSTResponse(getApplicationContext(),
						"http://104.236.202.116/php/upload/login.php", regJson, Utils.getHeaderParams(regJson));
				message = Utils.isErrorOrSuccess(httpResponseFormatDto);
				if (message != null) {
					success = false;
				} else {
					success = true;
				}
			} catch (MalformedURLException e) {
				Log.e(TAG, "Bad URL: " + e.toString(), e);
				message = "Invalid url.";
			} catch (IOException e) {
				Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);
				message = "Failed to register due to network error.";
			} catch (JSONException e) {
				Log.e(TAG,
						"Failed to parse JSON response: " + e.toString(), e);
				message = "Invalid reponse from the server.";
			} catch (ParseException e) {
				Log.e(TAG,
						"Failed to parse JSON response: " + e.toString(), e);
				message = "Invalid reponse from the server.";
			} catch (Exception e) {
				Log.e(TAG,
						"some error while registering : " + e.toString(), e);
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
				onLoginSuccess();
			} else {
				Utils.showOkAlertAndFinishBasedOnFlag(RegistrationActivity.this, "Failure", message, false);
			}
		}
	}

	String getLoginBody() {
		return "username=" + usernameEdt.getText().toString() + "&password=" + passwordEdt.getText().toString();
	}

}
