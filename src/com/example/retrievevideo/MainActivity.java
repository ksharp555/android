package com.example.retrievevideo;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import com.example.retrievevideo.R;







import dataAccessLayer.connectDB;
import android.support.v7.app.ActionBarActivity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;


public class MainActivity extends ActionBarActivity {

	Button retrieveButton, previousbtn, nextbtn;
	Button pickdate1, pickdate2;
	Button openapp, cam, sendmsg;
	Context context;
	
	TextView dateview1, dateview2;
	Connection dbconn= null;
    Statement stmt = null;
    connectDB db;
    URL url;
    String timestamp,timestamp2, packageName="com.example.vs";
    
    
    int currentimgIndex=0;
    int flag=0; //for pickingdate
    private int year;
    private int month;
    private int day;
    
    static final int DATE_PICKER_ID = 1111;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        retrieveButton=(Button)findViewById(R.id.retrievepic);
        previousbtn=(Button)findViewById(R.id.pre);
        nextbtn=(Button)findViewById(R.id.nxt);
        pickdate1=(Button)findViewById(R.id.date1);
        pickdate2=(Button)findViewById(R.id.date2);
        dateview1=(TextView)findViewById(R.id.TextView01);
        dateview2=(TextView)findViewById(R.id.textView2);
        openapp=(Button)findViewById(R.id.openapp);
        cam=(Button)findViewById(R.id.cam);
        sendmsg=(Button)findViewById(R.id.sendmesg);
        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);
        dateview1.setText(new StringBuilder().append(year)
                        .append("-").append(month+1).append("-").append(day)
                        .append(" "));
        dateview2.setText(new StringBuilder().append(year)
                		.append("-").append(month+1).append("-").append(day)
                		.append(" "));
        
        retrieveButton.setOnClickListener(new OnClickListener(){
        	 @Override
     	    public void onClick(View v){
        		 switch (v.getId()){
     	    	
     	    		case R.id.retrievepic:
     	    			new NetworkOperation().execute();
     	    			Thread th1=new Thread(){
     	    				@Override
        	    			public void run(){
        	    				try{
        	    					sleep(5000);
        	    	    			Log.i("SELECTQUERY","READING...");
        	    	    			
        	    	    			
        	    	    			//timestamp=year+"-"+month+"-"+day +" "+"00:00:00";
        	    	    			timestamp=dateview1.getText() +"00:00:00";
        	    	    			timestamp2=dateview2.getText() +"23:59:59";
        	    	    			Log.i("time", timestamp);
        	    	    			String mediaItemsQuery ="SELECT mediapath FROM mediaitems where mediatimestamp between '"+timestamp+"' and '"+timestamp2+"'" +" and mediapath like '%.mp4'";
        	    	    			Log.i("SELECTQUERY","CreateREADING...");
        	    	    			Statement  statementMedia = dbconn.createStatement();
        	    	    			//statementMedia.executeUpdate(mediaItemsQuery);
        	    	    			Log.i("SELECTQUERY","querying...");
        	    	    			ResultSet rs=statementMedia.executeQuery(mediaItemsQuery);
        	    	    			Log.i("SELECTQUERY","queryingfinish...");
        	    	    			rs.next();
        	    	    			Log.i("url", "geturl");
        	    	    			url=new URL(rs.getString("mediapath"));
        	    	    			Log.i("url", "start url"+url.toString());
        	    	    			final Uri uri=Uri.parse(url.toString());
        	    	    			Log.i("uri", "start uri"+uri.getPath());
        	    	    			runOnUiThread(new Runnable() {  
        	    	    				@Override
          	    	                     public void run() {
        	    	    					try{
                	    	    				Log.i("uri", "start uri2"+uri.getPath());
        	        	    	    			
        	        	    	    			final VideoView mVideoView;
        	        	    	    			mVideoView=(VideoView)findViewById(R.id.images);
        	        	    	    			MediaController mediaController = new MediaController(MainActivity.this);
        	        	    	    			Log.i("mediaController"," mediaController finished");
        	        	    	    			mediaController.setAnchorView(mVideoView);
        	        	    	    			mediaController.setMediaPlayer(mVideoView);        	        	    	    		
        	        	    	    			mVideoView.setMediaController(mediaController);
        	        	    	    			
        	        	    	    			Log.i("uri", uri.toString());
        	        	    	    		 	mVideoView.setVideoPath(url.toString());
        	        	    	    			mVideoView.requestFocus(); 
        	        	    	    			
        	         	    	                Log.i("focus", "requestfinished");
        	         	    	                mVideoView.start();
        	         	    	                Log.i("play", "it should be played");
        	         	    	                
                	    	    			}catch (Exception e) {
                	    	    				Log.e("Error", e.getMessage());
                	    	    				e.printStackTrace();
                	    	    			}	
        	    	    				
        	    	    				
        	    	    				}
        	    	    			});
        	    	    			
        	    	    			
        	    	               
        	    	    			int i=0;
        	    	    			}catch (Exception err) { 
            	    	    			;
            	    		        }
        	    				}
     	    				
     	    			};
     	    			th1.start();
     	    			
        		 }
        	 }
        });
        pickdate1.setOnClickListener(new OnClickListener() {
       	 
            @Override
            public void onClick(View v) {
                 
                // On button click show datepicker dialog 
            	flag=0;
                showDialog(DATE_PICKER_ID);
                /*dateview1.setText(new StringBuilder().append(year)
                        .append("-").append(month+1).append("-").append(day)
                        .append(" "));*/
                
            }
 
        });
        
        
        pickdate2.setOnClickListener(new OnClickListener() {
       	 
            @Override
            public void onClick(View v) {
                
                // On button click show datepicker dialog
            	flag=1;
                showDialog(DATE_PICKER_ID);
                
            }
 
        });
        
        openapp.setOnClickListener(new OnClickListener() {
       	 
            @Override
            public void onClick(View v) {
                
            	startActivity( new Intent(MainActivity.this, RetrievePictureActivity.class));
            	 /*PackageManager manager = MainActivity.this.getPackageManager();
            	    try {
            	        Intent i = manager.getLaunchIntentForPackage("com.example.vs");
            	        if (i == null) {
            	            //return false;
            	            //throw new PackageManager.NameNotFoundException();
            	        }
            	        i.addCategory(Intent.CATEGORY_LAUNCHER);
            	        MainActivity.this.startActivity(i);
            	        //return true;
            	    } catch (Exception e) {
            	        Log.i("error", e.getMessage());
            	    	//return false;
            	    }*/
            }
 
        });
        
        cam.setOnClickListener(new OnClickListener() {
          	 
            @Override
            public void onClick(View v) {
            	
    			startActivity( new Intent(MainActivity.this, welcomeactivity.class));
    			
            	 /*PackageManager manager = MainActivity.this.getPackageManager();
            	    try {
            	        Intent i = manager.getLaunchIntentForPackage("com.example.test");
            	        if (i == null) {
            	            //return false;
            	            //throw new PackageManager.NameNotFoundException();
            	        }
            	        i.addCategory(Intent.CATEGORY_LAUNCHER);
            	        MainActivity.this.startActivity(i);
            	        //return true;
            	    } catch (Exception e) {
            	        Log.i("error", e.getMessage());
            	    	//return false;
            	    }*/
            }
 
        });
        
        sendmsg.setOnClickListener(new OnClickListener() {
          	 
            @Override
            public void onClick(View v) {
                
            	
            	startActivity( new Intent(MainActivity.this, RegistrationActivity.class));
            	 /*PackageManager manager = MainActivity.this.getPackageManager();
            	    try {
            	        Intent i = manager.getLaunchIntentForPackage("com.example.emergencymessaging");
            	        if (i == null) {
            	            //return false;
            	            //throw new PackageManager.NameNotFoundException();
            	        }
            	        i.addCategory(Intent.CATEGORY_LAUNCHER);
            	        MainActivity.this.startActivity(i);
            	        //return true;
            	    } catch (Exception e) {
            	        Log.i("error", e.getMessage());
            	    	//return false;
            	    }*/
            }
        });
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	switch (item.getItemId()) {
    		case R.id.showMap:
    			Intent intentMap = new Intent(this,GeoCoderDemo.class);
    			startActivity(intentMap);
    			break;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    private class NetworkOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
        	db=new connectDB();
    		dbconn=db.conect();
    		/*try{
    			Log.i("SELECTQUERY","READING...");
    			String mediaItemsQuery ="SELECT mediapath FROM mediaitems where mediapath='http://104.236.202.116:8080/abc/upload/camera20150305_132905.jpg'";
    			Log.i("SELECTQUERY","CreateREADING...");
    			Statement  statementMedia = dbconn.createStatement();
    			//statementMedia.executeUpdate(mediaItemsQuery);
    			Log.i("SELECTQUERY","querying...");
    			ResultSet rs=statementMedia.executeQuery(mediaItemsQuery);
    			Log.i("SELECTQUERY","queryingfinish...");
    			//rs.next();
    				url=new URL(rs.getString("mediapath"));
    			
    			Log.i("SELECTQUERY","picing...");
    			Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        		pic.setImageBitmap(bmp);
    		}catch (Exception e) { 
    			String err = (e.getMessage()==null)?"SD Card failed":e.getMessage();
	            Log.e("SELECTQUERY",e.getMessage());
	        }*/
    		return null;
        }
        @Override
        protected void onPostExecute(String result) {

        }
        }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_PICKER_ID:
             
            // open datepicker dialog. 
            // set date picker for current date 
            // add pickerListener listner to date picker
            return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }
    
    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
 	   
        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                int selectedMonth, int selectedDay) {
             
            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            // Show selected date 
            if(flag==0){
            dateview1.setText(new StringBuilder().append(year)
                    .append("-").append(month + 1).append("-").append(day)
                    .append(" "));
            }
            else{
         	   dateview2.setText(new StringBuilder().append(year)
                        .append("-").append(month + 1).append("-").append(day)
                        .append(" "));
            }
           }
        };
        private static boolean openApp(Context context, String packageName) {
            PackageManager manager = context.getPackageManager();
            try {
                Intent i = manager.getLaunchIntentForPackage(packageName);
                if (i == null) {
                    return false;
                    //throw new PackageManager.NameNotFoundException();
                }
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                context.startActivity(i);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
}
