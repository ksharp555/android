package com.example.retrievevideo.emergencymessaging;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by m.susmitha on 7/4/15.
 */
public class Utils {
	public static final int THREE_MINUTES_IN_MILLIS = 180000;

	public static void hideKeyboard(View view, Context context) {
		try {
			InputMethodManager inputManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);

			inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showKeyboard(View view, Context context) {

		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}

	// static ProgressDialog progressDialog;
	static ProgressDialog progressDialog;

	public static void showProgressDialog(Context context, String message) {
		try {

			// dismiss progress dialog if there is any prevoious one
			dismissProgressDialog();
			progressDialog = ProgressDialog.show(context, "", "", true);
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setMessage(message);
			progressDialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dismissProgressDialog() {
		try {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setTimeouts(AbstractHttpMessage httpMessage) {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, THREE_MINUTES_IN_MILLIS);
		HttpConnectionParams.setSoTimeout(params, THREE_MINUTES_IN_MILLIS);
		httpMessage.setParams(params);
	}

	public static Map<String, String> getHeaderParams(String body) {
		Map<String, String> headerParams = new HashMap<String, String>();
		//headerParams.put("Content-Length", body.length() + "");
		headerParams.put("Accept", "application/json");
		headerParams.put("Content-Type", "application/x-www-form-urlencoded");

		return headerParams;
	}

	/*
	 * { success: 0 error_message: "Username and/or password is invalid." }
	 */
	public static String isErrorOrSuccess(HttpResponseFormatDto httpResponseDto) throws JSONException {
		String message = null;

		if (httpResponseDto.getStatusCode() != 0) {
			if (httpResponseDto.getStatusCode() >= 200 && httpResponseDto.getStatusCode() <= 299) {
				if (httpResponseDto.getData() != null) {
					JSONObject jsonObject = new JSONObject(httpResponseDto.getData());
					if(jsonObject.has("success")
							&& (!jsonObject.isNull("success"))){
						int status=jsonObject.getInt("success");
						if(status==0){
							if(jsonObject.has("error_message")
									&& (!jsonObject.isNull("error_message"))){
								message=jsonObject.getString("error_message");
							}
						}
						
					}
				} else {
					message = null;
				}
			} else {
				message = "some unknown error";
			}
		}
		return message;
	}
	
	public static void showOkAlertAndFinishBasedOnFlag(final Context mContext,
			String heading, String message, final boolean canFinish) {
		AlertDialog alertDialog = new AlertDialog.Builder(mContext)
				.setTitle(heading)

				.setMessage(message)

				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (canFinish) {
							((Activity) mContext).finish();
						} else {
							dialog.dismiss();
						}
					}
				}).create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.setCancelable(false);
		alertDialog.show();
	}


}
