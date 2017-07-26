package com.example.retrievevideo.emergencymessaging;

import java.sql.Connection;

import android.os.AsyncTask;
import dataAccessLayer.connectDB;

public class NetworkOperation extends AsyncTask<String, Void, String> {
	public static connectDB db;
	public static Connection dbconn= null;
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