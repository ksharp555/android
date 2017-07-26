	package com.yzi.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import dataAccessLayer.connectDB;

import android.util.Log;
import dataAccessLayer.connectDB;
import GetLocation.GPSTracker;

import com.example.retrievevideo.Mainupload;
//uploading tools class support file and parameters
public class UploadUtil {
	private static UploadUtil uploadUtil;
	private static final String BOUNDARY =  UUID.randomUUID().toString(); // bound identification 
	private static final String PREFIX = "--";
	private static final String LINE_END = "\r\n";
	private static final String CONTENT_TYPE = "multipart/form-data"; // content type
	private UploadUtil() {

	}
	/**
	 * 
	 * @return
	 */
	public static UploadUtil getInstance() {
		if (null == uploadUtil) {
			uploadUtil = new UploadUtil();
		}
		return uploadUtil;
	}

	private static final String TAG = "UploadUtil";
	private int readTimeOut = 10 * 1000; // readTimeOut
	private int connectTimeout = 10 * 1000; // connectTimeout
	private static int requestTime = 0;	// requestTime
	private static final String CHARSET = "utf-8"; // set encoding
	public static final int UPLOAD_SUCCESS_CODE = 1; // UPLOAD_SUCCESS
	
	public static final int UPLOAD_FILE_NOT_EXISTS_CODE = 2;

	public static final int UPLOAD_SERVER_ERROR_CODE = 3;
	protected static final int WHAT_TO_UPLOAD = 1;
	protected static final int WHAT_UPLOAD_DONE = 2;
	
	/**
	 * android: upload file to server
	 * 
	 * @param filePath
	 *            upload_filepath
	 * @param fileKey
	 *            <input type=file name=xxx/> xxx is fileKey
	 * @param RequestURL
	 *            
	 */
	public void uploadFile(String filePath, String fileKey, String RequestURL,
			Map<String, String> param,String latAndlong) {
		if (filePath == null) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE,"file_not_exist");
			return;
		}
		try {
			File file = new File(filePath);
			uploadFile(file, fileKey, RequestURL, param,latAndlong);
		} catch (Exception e) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE,"file_not_exist");
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 
	 */
	public void uploadFile(final File file, final String fileKey,
			final String RequestURL, final Map<String, String> param,final String latAndlong) {
		if (file == null || (!file.exists())) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE,"FILE_NOT_EXISTS");
			return;
		}

		Log.i(TAG, "RequestURL=" + RequestURL);
		Log.i(TAG, "RequestfileName=" + file.getName());
		Log.i(TAG, "RequestfileKey=" + fileKey);
		new Thread(new Runnable() {  //start thread of upload file
			 
			public void run() {
				toUploadFile(file, fileKey, RequestURL, param,latAndlong);
			}
		}).start();
		
	}

	private void toUploadFile(File file, String fileKey, String RequestURL,
			Map<String, String> param,String latAndlong) {
		String result = null;
		requestTime= 0;
		
		long requestTime = System.currentTimeMillis();
		long responseTime = 0;

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(readTimeOut);
			conn.setConnectTimeout(connectTimeout);
			conn.setDoInput(true); // allow input stream
			conn.setDoOutput(true); // allow output stream
			conn.setUseCaches(false); // disallow use cache
			conn.setRequestMethod("POST"); // request format
			conn.setRequestProperty("Charset", CHARSET); // set encoding
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
//			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			// if file is not null, upload file
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			StringBuffer sb = null;
			String params = "";
			
			/***
			 * for upload parameters above code
			 */
			if (param != null && param.size() > 0) {
				Iterator<String> it = param.keySet().iterator();
				while (it.hasNext()) {
					sb = null;
					sb = new StringBuffer();
					String key = it.next();
					String value = param.get(key);
					sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
					sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
					sb.append(value).append(LINE_END);
					params = sb.toString();
					Log.i(TAG, key+"="+params+"##");
					dos.write(params.getBytes());
//					dos.flush();
				}
			}
			
			sb = null;
			params = null;
			sb = new StringBuffer();
			/**
			 * name's value is requested by server, only having the key can upload related file 
			 * filename like:abc.png
			 */
			sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
			sb.append("Content-Disposition:form-data; name=\"" + fileKey
					+ "\"; filename=\"" + file.getName() + "\"" + LINE_END);
			sb.append("Content-Type:image/pjpeg" + LINE_END); // Content-type for identify file type
			sb.append(LINE_END);
			params = sb.toString();
			sb = null;
			
			Log.i(TAG, file.getName()+"=" + params+"##");
			dos.write(params.getBytes());
			/**upload file*/
			InputStream is = new FileInputStream(file);
			//onUploadProcessListener.initUpload((int)file.length());
			byte[] bytes = new byte[1024];
			int len = 0;
			int curLen = 0;
			while ((len = is.read(bytes)) != -1) {
				curLen += len;
				dos.write(bytes, 0, len);
				//onUploadProcessListener.onUploadProcess(curLen);
			}
			is.close();
			
			dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
			dos.write(end_data);
			dos.flush();	
			//dos.write(tempOutputStream.toByteArray());
			
			//aquire responding time 200ms when is successful, aquire respondingstream
			int res = conn.getResponseCode();
			responseTime = System.currentTimeMillis();
			this.requestTime = (int) ((responseTime-requestTime)/1000);
			Log.e(TAG, "response code:" + res);
			if (res == 200) {
				Log.e(TAG, "request success");
				InputStream input = conn.getInputStream();
				StringBuffer sb1 = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1) {
					sb1.append((char) ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : " + result);
				sendMessage(UPLOAD_SUCCESS_CODE, "result"
						+ result);
				//Upload function ends
				//get the GPS Locaiton 
				//MainActivity main=new MainActivity();
				//Log.i("lattttt","getlat is :"+main.Getlat());
				//database save functionality
			Connection dbconn= null;
		        Statement stmt = null;
		        Date dt=new Date(requestTime);
		        connectDB db = new connectDB();
		        dbconn= db.conect();
		        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(dt);
		        String lAndl[] = latAndlong.split(",");
		        double lat = Double.parseDouble(lAndl[0]);
		        double lng = Double.parseDouble(lAndl[1]);
		        Log.i("TAG", "Initiating to insert the Location ::::"+lat);
		        Log.i("TAG", "Initiating to insert the Location ::::"+lng);
		        String  locationQuerry="INSERT INTO  Positions Values (Default,1,Point("+lat+","+lng+"))";
		        
		        Log.i("TAG", "Initiating to insert the Location and Media items");
		        try {int key=1;//assuming 1 to not get any error for testing 
					     	try {
					        
					        	Log.i("TAG", "Preparing Command Statement for Inserting Locaiton");
					            Statement statementLocation = dbconn.createStatement();
					            statementLocation.executeUpdate(locationQuerry,Statement.RETURN_GENERATED_KEYS);
					            ResultSet rs = statementLocation.getGeneratedKeys();
					            if ( rs.next() ) {
					                // Retrieve the auto generated key(s).
					                key = rs.getInt(1);
					            }
							        try {
							        	String mediaItemsQuery = "Insert INTO Mediaitems (mediaitemid,userid,positionid,filesize,media,mediatimestamp,mediatypeid,mediapath)"+
						                        "VALUES (default, 7,"+key+",666.00,'','"+ timeStamp+"',1,'http://104.236.202.116:8080/abc/upload/"+file.getName()+"')";
						        
							        	Log.i("TAG", "Preparing Command Statement for Inserting edia item");
							            Statement statementMedia = dbconn.createStatement();
							            statementMedia.executeUpdate(mediaItemsQuery);
							            
							        } catch (Exception e) { 
							            System.err.println("Got an exception IN Inserting Media Item! "); 
							            System.err.println(e.getMessage()); 
							        }
					     	} catch (Exception e) { 
					            System.err.println("Got an exception IN Inserting Locaiton! "); 
					            System.err.println(e.getMessage()); 
					        } 
			} catch (Exception e) { 
	            System.err.println("Got an exception IN Inserting Media Item Or Locaiton! "); 
	            System.err.println(e.getMessage()); 
	        }
				return;
			} else {
				Log.e(TAG, "request error");
				sendMessage(UPLOAD_SERVER_ERROR_CODE,"fail_code=" + res);
				return;
			}
		} catch (MalformedURLException e) {
			sendMessage(UPLOAD_SERVER_ERROR_CODE,"fail_error=" + e.getMessage());
			e.printStackTrace();
			return;
		} catch (IOException e) {
			sendMessage(UPLOAD_SERVER_ERROR_CODE,"fail_error=" + e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	//send upload result
	private void sendMessage(int responseCode,String responseMessage)
	{
		//onUploadProcessListener.onUploadDone(responseCode, responseMessage);
	}
	
	// upload listener
	public static interface OnUploadProcessListener {
		/**
		 * 
		 * @param responseCode
		 * @param message
		 */
		void onUploadDone(int responseCode, String message);
		/**
		 * uploading
		 * @param uploadSize
		 */
		void onUploadProcess(int uploadSize);
		/**
		 * ready to upload
		 * @param fileSize
		 */
		void initUpload(int fileSize);
	}
	private OnUploadProcessListener onUploadProcessListener;
	

	public void setOnUploadProcessListener(
			OnUploadProcessListener onUploadProcessListener) {
		this.onUploadProcessListener = onUploadProcessListener;
	}

	public int getReadTimeOut() {
		return readTimeOut;
	}

	public void setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	//obtain upload time.
	public static int getRequestTime() {
		return requestTime;
	}

	public static interface uploadProcessListener{
		
	}
	
	
	
	
}
