package com.example.retrievevideo;

import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

import com.example.retrievevideo.R;
import com.example.retrievevideo.emergencymessaging.Utils;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import dataAccessLayer.connectDB;

public class RetrievePictureActivity extends ActionBarActivity {

	Button retrieveButton, previousbtn, nextbtn;
	Button pickdate1, pickdate2;
	TextView dateview1, dateview2;
	ImageView pic;
	Connection dbconn= null;
    Statement stmt = null;
    connectDB db;
    URL url;
    String timestamp,timestamp2;
    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

    //Bitmap[] mThumbIds=new Bitmap[];
    int currentimgIndex=0;
    int flag=0; //for pickingdate
    private int year;
    private int month;
    private int day;
    int countPic=0;
    static final int DATE_PICKER_ID = 1111; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrievepic_activity_main);
        pic=(ImageView)findViewById(R.id.images);
        retrieveButton=(Button)findViewById(R.id.retrievepic);
        previousbtn=(Button)findViewById(R.id.searchbtn);
        nextbtn=(Button)findViewById(R.id.defaultbutton);
        pickdate1=(Button)findViewById(R.id.date1);
        pickdate2=(Button)findViewById(R.id.date2);
        dateview1=(TextView)findViewById(R.id.TextView01);
        dateview2=(TextView)findViewById(R.id.textView2);
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
        	    		final ProgressDialog progDailog = ProgressDialog.show( 
        	    				RetrievePictureActivity.this, "retrieving", "Please wait...", true); 
        	    		currentimgIndex=0;
        	    		bitmapArray = new ArrayList<Bitmap>();
        	    		
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
        	    	    			String mediaItemsQuery ="SELECT mediapath FROM mediaitems where mediatimestamp between '"+timestamp+"' and '"+timestamp2+"'" +" and mediapath like '%.jpg'";
        	    	    			//String mediaItemsQuery ="SELECT mediapath FROM mediaitems where mediatimestamp='2015-05-04 19:54:02'";
        	    	    			Log.i("SELECTQUERY","CreateREADING...");
        	    	    			Statement  statementMedia = dbconn.createStatement();
        	    	    			//statementMedia.executeUpdate(mediaItemsQuery);
        	    	    			Log.i("SELECTQUERY","querying...");
        	    	    			ResultSet rs=statementMedia.executeQuery(mediaItemsQuery);
        	    	    			Log.i("SELECTQUERY","queryingfinish...");
        	    	    			rs.next();
        	    	    			int i=0;
        	    	    			while(rs.next()){
        	    	    				url=new URL(rs.getString("mediapath"));
        	    	    				bitmapArray.add(i, BitmapFactory.decodeStream(url.openConnection().getInputStream()));
        	    	    				i++;
        	    	    			}
        	    	    			countPic=i;
        	    	    			Log.i("SELECTQUERY","picing...");
        	    	    			//final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        	    	    			runOnUiThread(new Runnable() {
        	    	    				 @Override
        	    	                        public void run() {
        	    	    					 
        	    	    					 pic.setImageBitmap(bitmapArray.get(0));
        	    	    				 }
        	    	    			});
        	    	    		}catch (Exception err) { 
        	    	    			;
        	    		        }
        	    				progDailog.dismiss();
        	    			}
        	    			
        	    		};
        	    		th1.start();
        	    		Utils.dismissProgressDialog();
        	    		break;
        	    	default:
        	            break;
        	    	}
        	    }
        });
        nextbtn.setOnClickListener(new OnClickListener(){
        	 @Override
     	    public void onClick(View v){
        		 switch (v.getId()){
        		 	case R.id.defaultbutton:
        		 		currentimgIndex++;
        		 		if(currentimgIndex>=bitmapArray.size()){
        		 			currentimgIndex=bitmapArray.size()-1;
        		 		}
        		 		pic.setImageBitmap(bitmapArray.get(currentimgIndex));
        		 }
        	 }
        });
        
        previousbtn.setOnClickListener(new OnClickListener(){
       	 @Override
    	    public void onClick(View v){
       		 switch (v.getId()){
       		 	case R.id.searchbtn:
       		 		currentimgIndex--;
       		 		if(currentimgIndex<0){
       		 			currentimgIndex=0;
       		 		}
       		 		pic.setImageBitmap(bitmapArray.get(currentimgIndex));
       		 		
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
}
