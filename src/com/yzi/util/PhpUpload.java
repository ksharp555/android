package com.yzi.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.example.retrievevideo.emergencymessaging.HttpResponseFormatDto;

import android.content.SharedPreferences;
import android.util.Log;

public class PhpUpload {
	private static final String CHARSET = "utf-8";
	private static final String upLoadServerUri="http://104.236.202.116/php/upload/uploadfile1.php";
	int serverResponseCode = 0;
	
	
	public PhpUpload(){
		
	}
	public int uploadFile(String sourceFileUri, String lat, String longi, String username, String timestamp){

		 String fileName = sourceFileUri;
		 HttpURLConnection conn = null;
	     DataOutputStream dos = null; 
	     String lineEnd = "\r\n";
	     String twoHyphens = "--";
	     String boundary = "*****";
	     int bytesRead, bytesAvailable, bufferSize;
	     int typeid=1;
	     byte[] buffer;
	     int maxBufferSize = 1 * 1024 * 1024;
	     File sourceFile = new File(sourceFileUri);
	     
	     if (!sourceFile.isFile()) {
             
            Log.e("uploadFile", "Source File not exist :" +sourceFileUri);
            return 0;
        }else{
        	try{
        		 // open a URL connection to the Servlet
        		 /* FileInputStream fileInputStream = new FileInputStream(sourceFile);
        		  
        		  URL url = new URL(upLoadServerUri);
        		  
        		 // Open a HTTP  connection to  the URL
        		  conn = (HttpURLConnection) url.openConnection();
        		  conn.setDoInput(true); // Allow Inputs
                  conn.setDoOutput(true); // Allow Outputs
                  conn.setUseCaches(false); // Don't use a Cached Copy
                  conn.setRequestMethod("POST");
                  conn.setRequestProperty("Charset", CHARSET); // set encoding
                  conn.setRequestProperty("Connection", "Keep-Alive");
                  conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                  conn.setRequestProperty("userfile", fileName);
        		  
                  dos = new DataOutputStream(conn.getOutputStream());
                  //upload param
                  dos.writeBytes(twoHyphens + boundary + lineEnd); 
                  dos.writeBytes("Content-Disposition: form-data; name=\"userfile\";filename="+ fileName + "" + lineEnd);
                  dos.writeBytes(lineEnd);
                  // create a buffer of  maximum size
                  bytesAvailable = fileInputStream.available(); 
                  bufferSize = Math.min(bytesAvailable, maxBufferSize);
                  buffer = new byte[bufferSize];
                  // read file and write it into form...
                  bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                  
                  while (bytesRead > 0) {

                      dos.write(buffer, 0, bufferSize);
                      bytesAvailable = fileInputStream.available();
                      bufferSize = Math.min(bytesAvailable, maxBufferSize);
                      bytesRead = fileInputStream.read(buffer, 0, bufferSize);   

                  }	
               // send multipart form data necesssary after file data...
                  dos.writeBytes(lineEnd);
                  dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                  serverResponseCode = conn.getResponseCode();
                  String serverResponseMessage = conn.getResponseMessage();
                  Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);
                  fileInputStream.close();
                  dos.flush();
                  dos.close(); */
        		
        		// new HttpClient
        		HttpClient httpClient = new DefaultHttpClient();
        		// post header
        		if(sourceFileUri.contains(".mp4")){
        			typeid=4;
        		}else if(sourceFileUri.contains(".jpg")){
        			typeid=1;
        		}else{
        			typeid=3;
        		}
        		Log.i("URLwithParameters", "URLwithParameters:****************"+upLoadServerUri+"?lat="+lat+"&longi="+longi+
        				"&dat="+timestamp+"&typeid="+typeid+"&username="+username);
        		HttpPost httpPost = new HttpPost(upLoadServerUri+"?lat="+lat+"&longi="+longi+
        				"&dat="+timestamp+"&typeid="+typeid+"&username="+username);
        		
        		//add data
        		/*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        		nameValuePairs.add(new BasicNameValuePair("lat", lat));
        		nameValuePairs.add(new BasicNameValuePair("longi", longi));
        		nameValuePairs.add(new BasicNameValuePair("dat", timestamp));
        		nameValuePairs.add(new BasicNameValuePair("typeid", "1"));
        		nameValuePairs.add(new BasicNameValuePair("username", username));*/
        		
        		
        		File file = new File(sourceFileUri);
        		FileBody fileBody = new FileBody(file);
        		//send file and parameters simultaneously
        		// 然而对这种php 是用get方法的，并没有什么用，获取不到参数的
                MultipartEntity reqEntity = new MultipartEntity();
        		reqEntity.addPart("userfile", fileBody);
        		reqEntity.addPart("lat", new StringBody(lat));
        		Log.i("lat",lat);
        		reqEntity.addPart("longi", new StringBody(longi));
        		Log.i("longi",longi);
        		reqEntity.addPart("dat", new StringBody(timestamp));
        		Log.i("time",timestamp);
        		reqEntity.addPart("typeid", new StringBody("1"));
        		Log.i("username_entity",username);
        		reqEntity.addPart("username", new StringBody(username));
        		httpPost.setEntity(reqEntity);
        		// execute HTTP post request
        		HttpResponse response = httpClient.execute(httpPost);
        		HttpEntity resEntity = response.getEntity();
        		final String response_str = EntityUtils.toString(resEntity);
        		Log.i("serverStatus", response_str);
        	}catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
            } catch (Exception e) {
  
                e.printStackTrace();
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);  
            }	
        	return serverResponseCode; 
        }
	}
	
}
