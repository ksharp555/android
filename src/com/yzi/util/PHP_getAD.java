package com.yzi.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

public class PHP_getAD {
	
	String result=null;
	InputStream is;
	JSONArray jArray=null;
	String lat, longti;
	public PHP_getAD(String lat, String longti){
		this.lat = lat;
		this.longti = longti;
	}
	
	public JSONArray getdata(){
		//get from server
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://104.236.202.116/php/supplyndemand/locationlist.php");
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			Log.i("php post", "***"+lat+"***"+longti+"***");
			reqEntity.addPart("curlat", new StringBody(lat));
			reqEntity.addPart("curlon", new StringBody(longti));
			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			is=resEntity.getContent();
			//final String response_str = EntityUtils.toString(resEntity);
    		Log.i("serverStatus", "response_str");
    		Log.e("log_tag", "connection success");
		}catch(Exception e){
			Log.e("log_tag", "Error in http connection " + e.toString());
		
		}
		//covert data to string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line=null;
			while ((line=reader.readLine())!=null){
				sb.append(line+"\n");
			}
			is.close();
			result = sb.toString();
			Log.e("result--------", result);
		}catch (Exception e) {
			Log.e("log_tag", "Error converting result 11 " + e.toString());
		}
		//parse json data
		try{
			 jArray = new JSONArray(result);
			
		}catch(Exception e){
			Log.e("log_tag1", "Error converting result****" + e.toString());
		}
		
		return jArray;
		
	}
			
		
}
