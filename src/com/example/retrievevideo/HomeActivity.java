package com.example.retrievevideo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.retrievevideo.R;
import com.example.retrievevideo.Mainupload.PHPupload;
import com.example.retrievevideo.emergencymessaging.HttpResponseFormatDto;
import com.example.retrievevideo.emergencymessaging.PreferenceUtil;

import com.yzi.util.PhpUpload;
import com.yzi.util.UploadUtil;


import GetLocation.GPSTracker;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button; 
import android.widget.Toast;
public class HomeActivity extends ActionBarActivity {
	Button chatButton, messgingButton, recordButton, viewButton, defaultButton, advertisementBtn,treeBtn;
	private Camera mCamera=null;
	private boolean mIsRunning = true;
	private ExecutorService pool = null;
	private boolean safeToTakePicture = true;
	public static String picPath = null;
	GPSTracker gps;
	SurfaceTexture surfaceTexture = new SurfaceTexture(10);
	SharedPreferences sharedpreferences;
	int  defaultButtonFlag=1;
	public String username="hghjgjhggjhgjh";
	private SurfaceHolder surfaceHolder1;
	private SurfaceView surfaceView;
	public MediaRecorder mrec = new MediaRecorder();
    File video;
    private String outputFile = null;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//初始化PreferenceUtil
		PreferenceUtil.init(this);
		//根据上次的语言设置，重新设置语言
		switchLanguage(PreferenceUtil.getString("language", "en"));
		setContentView(R.layout.activity_home);
		gps=new GPSTracker(HomeActivity.this);
		chatButton = (Button) findViewById(R.id.chatButton);
		messgingButton = (Button) findViewById(R.id.messagingButton);
		recordButton = (Button) findViewById(R.id.recordButton);
		viewButton = (Button) findViewById(R.id.viewButton);
		defaultButton = (Button) findViewById(R.id.defaultbutton);
		advertisementBtn=(Button)findViewById(R.id.adBtn);
		treeBtn = (Button)findViewById(R.id.tree);
		pool = Executors.newFixedThreadPool(1);
		sharedpreferences= getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		username=sharedpreferences.getString("username", "no_user");
		chatButton.setOnClickListener(new OnClickListener() {
	    
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, ChatGroupsActivity.class);
				startActivity(intent);
			}
		});
		messgingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, EmergencyMessagingctivity.class);
				startActivity(intent);
			}
		});
		
		viewButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, RetrievePictureActivity.class);
				startActivity(intent);
			}
		});
		
		
		
		advertisementBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(HomeActivity.this, listAD_activity.class);
				startActivity(intent);
			}
		});
		
		treeBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(HomeActivity.this, ListTreeActivity.class);
				startActivity(intent);
			}
		});
		
		recordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, Mainupload.class);
				startActivity(intent);
			}
		});
		defaultButton.setOnClickListener(new OnClickListener() {
			boolean btnFlag=true; 
			
			@Override
			public void onClick(View v) {
				if(defaultButtonFlag==1){
					if(btnFlag){
						mIsRunning = true;
						btnFlag=false;
						try{
							mCamera=Camera.open();
							mCamera.setDisplayOrientation(90);
							mCamera.setPreviewTexture(surfaceTexture);
							Log.i("camera", "open....");
							Camera.Parameters param = mCamera.getParameters();
			                param.setPictureSize(640, 480);
			                mCamera.setParameters(param);
			                Log.i("camera", "set finish");
			                mCamera.startPreview();
			                //mCamera.autoFocus(new AutoFocusCallback() {  
			                    // autofocus over  
			                  //  @Override  
			                    //public void onAutoFocus(boolean success, Camera camera) {  
			                        // focus successfully  
			                      //  Toast.makeText(HomeActivity.this, "autofocus is ready !!",  
			                        //        Toast.LENGTH_SHORT).show();  
			                          
			                        // 5s take a picture  
			                        pool.execute(mRunnable);  
			                        //mCamera.takePicture(null, null, new MyPictureCallback());  
			                  //  }  
			               // });
						}catch(Exception e){
							e.printStackTrace();
						}
					}else{
						btnFlag=true;
						mIsRunning=false;
						mCamera.release();
					}
				}else if (defaultButtonFlag==2){
					if(btnFlag){
						mIsRunning = true;
						btnFlag=false;
					try{
						
						mCamera = Camera.open();
		                mCamera.setDisplayOrientation(90);
		                surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		                surfaceHolder1=surfaceView.getHolder();
		                surfaceView.getHolder().setKeepScreenOn(true); 
		                surfaceHolder1.addCallback(new SurfaceCallback());
		                surfaceHolder1.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		                pool.execute(mRunnableVideo); 
		                
					}catch(Exception e){
						Log.i("video Exception", e.getMessage());
					}
					}else{
						btnFlag=true;
						mIsRunning=false;
						//stopRecording();
					}
				}
			}
			
		});
	}
		
	

	protected void switchLanguage(String language) {
		//设置应用语言类型
		Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
       if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        }
       else if (language.equals("es")){
    	   Locale spanish = new Locale("es", "ES");
    	   config.locale = spanish;
       }
       else if (language.equals("rTW")){
    	   
    	   config.locale = Locale.TRADITIONAL_CHINESE;
       }
       else {
        	 config.locale = Locale.SIMPLIFIED_CHINESE;
        }
        resources.updateConfiguration(config, dm);
        
        //保存设置语言的类型
        PreferenceUtil.commitString("language", language);
       
    }
	
	private void refreshview()
	{
		finish();
		Intent it = new Intent(HomeActivity.this, HomeActivity.class);
	    startActivity(it);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_home, menu);
		return true;
	}

	public static final String MyPREFERENCES = "MyPrefs";

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.signout:
			finish();
			Intent intent = new Intent(HomeActivity.this, RegistrationActivity.class);
			SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
			sharedpreferences.edit().clear().commit();
			startActivity(intent);
			break;
		case R.id.selected_SimChinese:
			switchLanguage("zh");
			refreshview();
		    break;
		case R.id.selected_english:
			switchLanguage("en");
			refreshview();
		    break;
		case R.id.selected_Spanish:
			switchLanguage("es");
			refreshview();
			break;
		case R.id.selected_TraChinese:
			switchLanguage("rTW");
			refreshview();
		    break;
		case R.id.videosettings:
			defaultButtonFlag=2;
			Log.i("defaultButtonFlag", 
					defaultButtonFlag+"adsf");
			break;
		case R.id.picturesettings:
			defaultButtonFlag=1;
			Log.i("defaultButtonFlag", 
					defaultButtonFlag+"adsf");
			break;
		case R.id.audiosettings:
			defaultButtonFlag=3;
			Log.i("defaultButtonFlag", 
					defaultButtonFlag+"adsf");
			break;
		default:
		}

		return super.onOptionsItemSelected(item);
	}

	 private Runnable mRunnable = new Runnable() {  
	        @Override  
	        public void run() {  
	            // takepicture method 
	            while (mIsRunning) {
	            	Log.i("run..", "should be running...");
	            	if(safeToTakePicture)
	            	{
	            		Log.i("run..", "should be running2...");
	                mCamera.takePicture(null, null, takePicPictureListener);  
	                try {  
	                    Thread.sleep(5000);  
	                } catch (InterruptedException e) {  
	                    e.printStackTrace();  
	                }
	                
	            	}
	            }  
	            
	        }  
	    };
	    
	    private Runnable mRunnableVideo = new Runnable() {  
	        @Override  
	        public void run() {  
	            // takevideo method 
	            while (mIsRunning) {
	            	Log.i("run..", "should be running...");
	            	if(safeToTakePicture)
	            	{
	            		Log.i("run..", "should be running2...");
	                  
	                try {
	                	startRecording();
	                    Thread.sleep(10000);  
	                } catch (InterruptedException e) {  
	                    e.printStackTrace();  
	                } catch(Exception e){
	                	e.printStackTrace();
	                }
	                
	            	}
	            }  
	            
	        }  
	    };
	    
	    private Camera.PictureCallback  takePicPictureListener = new Camera.PictureCallback() {  
	        
	        @Override  
	        public void onPictureTaken(byte[] data, Camera camera) {
	        	safeToTakePicture=false;
	        	
	        	try {  
	        		  
	        		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
	                .format(new Date());
	    			File pictureFile = new File(getDir(), "camera"+timeStamp+".jpg");
	    			picPath=pictureFile.getPath();     			
	    			FileOutputStream fos = new FileOutputStream(pictureFile);
	    			fos.write(data);
	    			
		            
	    			//fos.flush();
		            fos.close();
		            
		           /* ExifInterface exif = new ExifInterface("/sdcard/camera20141217_212446.jpg");
		            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GPS.convert(lat));
		            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GPS.convert(lon));
		            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, GPS.latitudeRef(lat));
		            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, GPS.longitudeRef(lon));
		            exif.saveAttributes();*/
		            //MarkGeoTagImage("/sdcard/camera20141217_212446.jpg", location);
		            
		            toUploadFile();
		            
		            safeToTakePicture=true;
	                camera.startPreview();  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }  
	    
	    }; 
	    
	    };
	    
	    private File getDir()
		{
			//get sdcard root path
			File dir = Environment.getExternalStorageDirectory();
			
			if (dir.exists()) {
				return dir;
			}
			else {
				dir.mkdirs();
				return dir;
			}
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
			UploadUtil uploadUtil = UploadUtil.getInstance();
			
			//uploadUtil.setOnUploadProcessListener(this);  //���ü����������ϴ�״̬
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("orderId", "111");
			//picPath="//storage//emulated//0//SC//2014-11-01-13-31-52.jpg";
			String LatAndLong=Getlat()+","+Getlong();
			//uploadUtil.uploadFile(picPath,fileKey, requestURL,params,LatAndLong);
			//phpUplaod.uploadFile(picPath, Getlat(), Getlong());
			new PHPupload().execute();
			
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
	    class PHPupload extends  AsyncTask<Void, Void, Void>{
	    	public PHPupload() {

			}
	    	@Override
			protected Void doInBackground(Void... params) {
	    		PhpUpload phpUplaod=new PhpUpload();
	    		
	    		Calendar cal = Calendar.getInstance();
	    		String timeStamp = Long.toString(cal.getTimeInMillis()/1000);
	    		username=sharedpreferences.getString("username", "no_user");
	    		Log.i("username_background",username);
	    		phpUplaod.uploadFile(picPath, Double.toString(Getlat()), Double.toString(Getlong()), username, timeStamp);
	    		
	    		
	    		
	    		
				return null;
	    		
	    		
	    	
	    	
	    	}
	    }
	    @Override  
	    protected void onDestroy() {  
	        super.onDestroy();  
	        mIsRunning = false;  
	        if (pool != null) {  
	            pool.shutdownNow();  
	        }  
	    }  
	    
	    protected void startRecording() throws IOException 
        {
	    	if(mCamera==null){
	    		mCamera=Camera.open();
	    	}
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
            mrec = new MediaRecorder();
            mCamera.unlock();

            mrec.setCamera(mCamera);

            mrec.setPreviewDisplay(surfaceHolder1.getSurface());
            mrec.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mrec.setAudioSource(MediaRecorder.AudioSource.MIC); 
            mrec.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            

            mrec.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            mrec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            
            mrec.setMaxDuration(50000);
            //mrec.setVideoFrameRate(24);
            //mrec.setVideoSize(720, 1280);
            mrec.setVideoEncodingBitRate(3000000);
            mrec.setAudioEncodingBitRate(8000);
            
            safeToTakePicture=false;
            //mrec.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            //safeToTakePicture=false;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		      .format(new Date());
            mrec.setPreviewDisplay(surfaceHolder1.getSurface());
            //videoPath=Environment.getExternalStorageDirectory().getPath()+"/video"+timeStamp+".mp4";
            outputFile = Environment.getExternalStorageDirectory().
		    		  getAbsolutePath() + "/video"+timeStamp+".mp4";
            mrec.setOutputFile(outputFile); 
            picPath = outputFile;
            
            mrec.prepare();
            mrec.start();
           
            new CountDownTimer(5000, 1000) {
                public void onTick(long millisUntilFinished) {                    	
                	Toast.makeText(getApplicationContext(), "recording for 5 sec", Toast.LENGTH_SHORT).show();

                }

                public void onFinish() {
                	Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                	
                	stopRecording();
                	toUploadFile();
                }
            }.start();
            safeToTakePicture=true;
        }
	    protected void stopRecording() {
            //mrec.stop();
            mrec.stop();     // stop recording
            mrec.reset();    // set state to idle
            mrec.release();  // release resources back to the system
            mrec = null;
            //mrec.release();
            //mCamera.stopPreview();
            //mCamera.release();
        }
	    private final class SurfaceCallback implements Callback {  
            // surfaceCreated  
     	   
            public void surfaceCreated(SurfaceHolder holder) {
                if (mCamera != null){
                	System.out.println("surfaceCreated_video");
                    /*Parameters params = mCamera.getParameters();
                    params.setPreviewSize(800, 480);
                    mCamera.setParameters(params);*/
                	
                	try {  
                     System.out.println("surfaceCreated_video"); 
                     
                     mCamera = Camera.open();// open camera
                     mCamera.setDisplayOrientation(90);
                     Camera.Parameters parameters = mCamera.getParameters();  
                       
                       
                     mCamera.setParameters(parameters);  
                       
                 } catch (Exception e) {  
                     e.printStackTrace();  
                 }  
                	
                }
                else {
             	   
                        mCamera = Camera.open();
                }
            }
      
            public void surfaceChanged(SurfaceHolder holder, int format, int width,  
                    int height) {  
                System.out.println("surfaceChanged_video");
                /*solve huaping*/
                if(mCamera == null)
                    mCamera = Camera.open();
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> previewSize  = parameters.getSupportedPreviewSizes();
                Camera.Size cs = previewSize.get(0); 
                parameters.setPreviewSize(cs.width, cs.height);
                mCamera.setParameters(parameters);
                try {  
                    mCamera.setPreviewDisplay(holder);  
                } catch (IOException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
                mCamera.startPreview();// startPreview
                safeToTakePicture=true;
            }  
            
      
            // destroy preview  
            public void surfaceDestroyed(SurfaceHolder holder) {  
                System.out.println("surfaceDestroyed_video");  
            if (mCamera != null) {
             	        // stop recording
                        // set state to idle
         	   if (mrec!=null)
                    mrec.release();  // release resources back to the system
                    
                    mCamera.release();  
                     
                }
                else{
             	   if (mrec!=null)
             	   mrec.release();  // release resources back to the system
                   
                    //mCamera = null;
                }
            }  
      
        }  
}
