package com.example.retrievevideo;



import com.example.retrievevideo.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Selectfunction extends Activity implements OnClickListener {
	public static final int SELECT_TAKE_PHOTO = 1;
	
	public static final int SELECT_TAKE_VIDEO = 2;
	
	public static final int SELECT_TAKE_AUDIO = 3;
	
	private static final String TAG = "SelectActivity";
	private LinearLayout dialogLayout;
	private Button takePhotoBtn,takeVideoBtn, takeAudioBtn, sendMesgBtn, cancelBtn, downloadImgBtn;
	private Intent lastIntent;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.selectfunction);
		
		dialogLayout = (LinearLayout) findViewById(R.id.dialog_layout);
		dialogLayout.setOnClickListener(this);
		takePhotoBtn = (Button) findViewById(R.id.btn_take_photo);
		takePhotoBtn.setOnClickListener(this);
		takeVideoBtn = (Button) findViewById(R.id.btn_take_video);
		takeVideoBtn.setOnClickListener(this);
		takeAudioBtn = (Button) findViewById(R.id.btn_take_audio);
		takeAudioBtn.setOnClickListener(this);
		sendMesgBtn = (Button) findViewById(R.id.btn_send_mesg);
		sendMesgBtn.setOnClickListener(this);
		downloadImgBtn = (Button) findViewById(R.id.btn_download_img);
		downloadImgBtn.setOnClickListener(this);
		cancelBtn = (Button) findViewById(R.id.btn_cancel);
		cancelBtn.setOnClickListener(this);	
		lastIntent = getIntent();
	}
	public static final String MyPREFERENCES = "MyPrefs";
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_layout:
			finish();
			break;
		case R.id.btn_take_video:
			Intent intent = new Intent(this,videoactivity.class);
			startActivity(intent);
			break;
		case R.id.btn_take_photo:
			Intent intent1 = new Intent(this,Mainupload.class);
			startActivity(intent1);
			break;
		case R.id.btn_take_audio:
			Intent intent2 = new Intent(this,audioactivity.class);
			startActivity(intent2);
			break;
		case R.id.btn_send_mesg:
			Intent intent3 = new Intent(this,HomeActivity.class);
			startActivity(intent3);
			break;	
		case R.id.btn_download_img:
			Intent intent4 = new Intent(this,RetrievePictureActivity.class);
			startActivity(intent4);
			break;	
			
		default:
			
			finish();
			Intent intent5 = new Intent(this, RegistrationActivity.class);
			SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
			sharedpreferences.edit().clear().commit();
			startActivity(intent5);
			break;
			
		}
	}
	
}
