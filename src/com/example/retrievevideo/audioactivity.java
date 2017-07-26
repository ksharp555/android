package com.example.retrievevideo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.example.retrievevideo.R;
import com.yzi.util.UploadUtil;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import GetLocation.GPSTracker;
import com.yzi.util.UploadUtil;

public class audioactivity extends Activity {

   private MediaRecorder myRecorder;
   private String outputFile = null;
   private Button startBtn;
   private Button stopBtn;
   private TextView text;
   private static String requestURL = "http://104.236.202.116:8080/abc/p/file!upload";
   public static String audioPath = null;
   GPSTracker gps;
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.audioui);
      gps=new GPSTracker(audioactivity.this);
      text = (TextView) findViewById(R.id.text1);
      /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
      .format(new Date());
      outputFile = Environment.getExternalStorageDirectory().
    		  getAbsolutePath() + "/test"+timeStamp+".3gpp";
      audioPath=outputFile;
      myRecorder = new MediaRecorder();
      myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
      myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
      myRecorder.setOutputFile(outputFile);*/
      
      startBtn = (Button)findViewById(R.id.start);
      startBtn.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//myRecorder = new MediaRecorder();
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		      .format(new Date());
		      outputFile = Environment.getExternalStorageDirectory().
		    		  getAbsolutePath() + "/test"+timeStamp+".3gpp";
		      audioPath=outputFile;
		      myRecorder = new MediaRecorder();
		      myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		      myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		      myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		      myRecorder.setOutputFile(outputFile);
			start(v);
			if (gps.canGetLocation()){
            	 double latitude = gps.getLatitude();
                 double longitude = gps.getLongitude();
                  
                 // \n is for new line
                 Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                 Log.i("TAG","lat:"+gps.getLatitude());
             }else{
                 // can't get location
                 // GPS or Network is not enabled
                 // Ask user to enable GPS/network in settings
                 Log.i("TAG","lat:"+gps.getLatitude());
                 gps.showSettingsAlert();                    
             }
		}
      });
      
      
   }
   public void toUploadFile()
	{
   	//progressDialog.setMessage("�����ϴ��ļ�...");
		//uploadImageResult.setText("�����ϴ���...");
		//progressDialog.setMessage("�����ϴ��ļ�...");
		/*uploadImageResult.setText("�����ϴ���...");
		progressDialog.setMessage("��������������ϴ�...");
		progressDialog.setTitle("��Ϣ");
		progressDialog.setIcon(drawable.ic_dialog_info);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgress(59);
		progressDialog.setIndeterminate(true);*/
		/*progressDialog.setButton("ȷ��", new DialogInterface.OnClickListener(){  
           public void onClick(DialogInterface dialog, int which) {  
               dialog.cancel();               
           }       
       }); */ 
		
		//progressDialog.show();
		String fileKey = "img";
		UploadUtil uploadUtil = UploadUtil.getInstance();;
		//uploadUtil.setOnUploadProcessListener(this);  //���ü����������ϴ�״̬
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", "111");
		//picPath="//storage//emulated//0//SC//2014-11-01-13-31-52.jpg";
		String LatAndLong=Getlat()+","+Getlong();
		uploadUtil.uploadFile(audioPath,fileKey, requestURL,params,LatAndLong);
	}
   public void stop(){
	   
	   try {
    	      myRecorder.stop();
    	      myRecorder.release();
    	      myRecorder  = null;   	      
    	      Toast.makeText(getApplicationContext(), "Stop recording...",
    	    		  Toast.LENGTH_SHORT).show();
    	      toUploadFile();
    	   } catch (IllegalStateException e) {
    			e.printStackTrace();
    	   } catch (RuntimeException e) {
    			e.printStackTrace();
    	   }
          
          startBtn.setEnabled(true);
   
   }

   public void start(View view){
	   try {
          myRecorder.prepare();
          myRecorder.start();
       } catch (IllegalStateException e) {
          e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
        }
	   
       startBtn.setEnabled(false);

       
       Toast.makeText(getApplicationContext(), "Start recording...", 
    		   Toast.LENGTH_SHORT).show();
       
       
       
       CountDownTimer myCountDown = new CountDownTimer(5000, 1000) {
           public void onTick(long millisUntilFinished) {
               //update the UI with the new count
           	text.setText("time left: " + millisUntilFinished / 1000);
           }

           public void onFinish() {
               //start the activity
           	text.setText("done!");
           	stop();

          }
       };
       myCountDown.start();
       
       
       
       
       
       
   }

   public double Getlat(){
   	if (gps.canGetLocation()){
         	 double latitude = gps.getLatitude();
              double longitude = gps.getLongitude();
               
              // \n is for new line
              //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
              Log.i("TAG","lat:"+gps.getLatitude());
          }else{
              // can't get location
              // GPS or Network is not enabled
              // Ask user to enable GPS/network in settings
              Log.i("TAG","lat:"+gps.getLatitude());
              gps.showSettingsAlert();                    
          }
   	return gps.getLatitude();
   }
   public double Getlong(){
   	if (gps.canGetLocation()){
         	 double latitude = gps.getLatitude();
              double longitude = gps.getLongitude();
               
              // \n is for new line
              //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
              Log.i("TAG","lat:"+gps.getLatitude());
          }else{
              // can't get location
              // GPS or Network is not enabled
              // Ask user to enable GPS/network in settings
              Log.i("TAG","lat:"+gps.getLatitude());
              gps.showSettingsAlert();                    
          }
   	return gps.getLongitude();
   }


}