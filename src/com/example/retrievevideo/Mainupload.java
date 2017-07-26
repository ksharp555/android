package com.example.retrievevideo;



import java.io.File;  
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;  
import java.util.Calendar;
import java.util.Date;  
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  

import org.json.JSONException;

import GetLocation.GPSTracker;
import android.util.Log;


import com.example.retrievevideo.R;
import com.example.retrievevideo.emergencymessaging.HttpResponseFormatDto;
import com.example.retrievevideo.emergencymessaging.NetworkUtils;
import com.example.retrievevideo.emergencymessaging.Utils;
import com.yzi.util.PhpUpload;
import com.yzi.util.UploadUtil;




import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;  
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;  
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;  
import android.hardware.Camera.AutoFocusCallback;  
import android.hardware.Camera.PictureCallback;  
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;  
import android.os.Environment;  
import android.os.Handler;
import android.os.Looper;  
import android.os.Message;
import android.util.Log;  
import android.view.MotionEvent;  
import android.view.SurfaceHolder;  
import android.view.SurfaceHolder.Callback;  
import android.view.SurfaceView;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.view.ViewGroup;  
import android.view.Window;  
import android.view.WindowManager;  
import android.widget.Button;  
import android.widget.ProgressBar;
import android.widget.Toast;  



public class Mainupload extends ActionBarActivity {
	private static final String TAG = "uploadImage";
	//go to upload file
	protected static final int TO_UPLOAD_FILE = 1;  
	//responding for upload
	protected static final int UPLOAD_FILE_DONE = 2;  
	//choose file
	public static final int TO_SELECT_PHOTO = 3;
	//upload initialize
	private static final int UPLOAD_INIT_PROCESS = 4;
	//upload processing
	private static final int UPLOAD_IN_PROCESS = 5;
    //JSP UPLOAD FUNCTION
	private static String requestURL = "http://104.236.202.116:8080/abc/p/file!upload";
	public static String picPath = null;
	private boolean safeToTakePicture = false;
	private ProgressDialog progressDialog;
	private ProgressBar progressBar;
    //control panel  
    //private View mPannelController;  
    //declare camera 
    private Camera mCamera=null;  
    GPSTracker gps;
    
    SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs";
	public String username="hghjgjhggjhgjh";
	
	
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        System.out.println("onCreate");  
  
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/  
        setContentView(R.layout.mainupload);  
        gps=new GPSTracker(Mainupload.this);
        sharedpreferences= getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        
        username=sharedpreferences.getString("username", "no_user");
        Log.i("username", username);
      
	    
        //mPannelController = this.findViewById(R.id.buttonlayout);  
        // create timing thread don't use timer!! 
        pool = Executors.newFixedThreadPool(1);  
        // preview widget  
        SurfaceView surfaceView = (SurfaceView) this  
                .findViewById(R.id.surfaceView);  
        
        SurfaceHolder holder = surfaceView.getHolder();
       
        // set parameters
        
        surfaceView.getHolder().setFixedSize(176, 144);  
        surfaceView.getHolder().setKeepScreenOn(true);  
        surfaceView.getHolder().addCallback(new SurfaceCallback()); 
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        getActionBar().hide();
        surfaceView.setOnTouchListener(new View.OnTouchListener(){
        	Boolean showBar = false; 
        	@Override
        	public boolean onTouch(View v, MotionEvent event){
        		
        		
        		       if(showBar) // Toggle action bar visiblity
        		          getActionBar().hide();
        		       else
        		          getActionBar().show();

        		       showBar = !showBar;
        		  
        		   return false;   
        	}
        	
        });
        	
        //add event  
        final Button start = (Button) findViewById(R.id.takepicture);
        
        start.setOnClickListener(new OnClickListener() {  
        	
        	//click start taking pic; click again, it will end taking pic
        	boolean btnFlag=true; 
            
        	@Override  
            public void onClick(View v) { 
            	if(btnFlag){if (mCamera != null) {  
                    btnFlag=false;  
                    // autofocus  
                    Camera.Parameters param = mCamera.getParameters();
                    param.setPictureSize(640, 480);
                    mCamera.setParameters(param);
                    mCamera.autoFocus(new AutoFocusCallback() {  
                        // autofocus over  
                        @Override  
                        public void onAutoFocus(boolean success, Camera camera) {  
                            // focus successfully  
                            Toast.makeText(Mainupload.this, "autofocus is ready !!",  
                                    Toast.LENGTH_SHORT).show();  
                            mIsRunning = true;  
                            // 5s take a picture  
                            pool.execute(mRunnable);  
                            //mCamera.takePicture(null, null, new MyPictureCallback());  
                        }  
                    });
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
                int id= v.getId();
                if (id == R.id.takepicture) {
    			if(picPath!=null)
    			{
    				handler.sendEmptyMessage(TO_UPLOAD_FILE);
    			}else{
    				//Toast.makeText(this, "upload file path error", Toast.LENGTH_LONG).show();
    			}
    			
                }
            }else{
            	btnFlag=true;
            	mIsRunning = false; 
            }
            	}
                
            
        });  
  
        Button end = (Button) findViewById(R.id.endtake);  
        end.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                mIsRunning = false;  
                // cancel task  
                start.setEnabled(true);  
  
            }  
        });  
         
          
        
    }  
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_record, menu);
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.videoRecord:
			try{
				mIsRunning = false;
				mCamera.release();
				finish();
				Intent intent = new Intent(Mainupload.this, videoactivity.class);
				startActivity(intent);
			}catch (Exception e) {  
                e.printStackTrace();
                Toast.makeText(Mainupload.this, "Some error comes",  
                        Toast.LENGTH_SHORT).show();
            }
			break;

		case R.id.audioRecord:
			try{
				mIsRunning = false;
    			mCamera.release();
    			Intent intent1 = new Intent(Mainupload.this, audioactivity.class);
    			startActivity(intent1);
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			break;

		default:
		}

		return super.onOptionsItemSelected(item);
	} 
  
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        mIsRunning = false;  
        if (pool != null) {  
            pool.shutdownNow();  
        }  
    }  
  
    // time   
    private ExecutorService pool = null;  
    private boolean mIsRunning = true;  
    // time task  
    private Runnable mRunnable = new Runnable() {  
        @Override  
        public void run() {  
            // takepicture method 
            while (mIsRunning) {
            	if(safeToTakePicture)
            	{
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
    
    // call back picture  
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
    private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TO_UPLOAD_FILE:
				toUploadFile();
				break;			
			case UPLOAD_INIT_PROCESS:
				progressBar.setMax(msg.arg1);
				break;
			case UPLOAD_IN_PROCESS:
				progressBar.setProgress(msg.arg1);
				break;
			case UPLOAD_FILE_DONE:
		//		String result = "��Ӧ�룺"+msg.arg1+"\n��Ӧ��Ϣ��"+msg.obj+"\n��ʱ��"+UploadUtil.getRequestTime()+"��";
				//uploadImageResult.setText(result);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
    public void onUploadDone(int responseCode, String message) {
		progressDialog.dismiss();
		Message msg = Message.obtain();
		msg.what = UPLOAD_FILE_DONE;
		msg.arg1 = responseCode;
		msg.obj = message;
		handler.sendMessage(msg);
	}
    
    public void onUploadProcess(int uploadSize) {
		Message msg = Message.obtain();
		msg.what = UPLOAD_IN_PROCESS;
		msg.arg1 = uploadSize;
		handler.sendMessage(msg);
	}
	 
	public void initUpload(int fileSize) {
		Message msg = Message.obtain();
		msg.what = UPLOAD_INIT_PROCESS;
		msg.arg1 = fileSize;
		handler.sendMessage(msg );
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
    // SurfaceCallback  
    private final class SurfaceCallback implements Callback {  
        // surfaceCreated  
        public void surfaceCreated(SurfaceHolder holder) {  
            try {  
                System.out.println("surfaceCreated"); 
                isCameraUsebyApp();
                mCamera = Camera.open();// open camera
                mCamera.setDisplayOrientation(90);
                Camera.Parameters parameters = mCamera.getParameters();  
                parameters.setPreviewSize(800, 480);  
                parameters.setPreviewFrameRate(5);  
                parameters.setPictureSize(800, 480);  
                parameters.setJpegQuality(80);  
                mCamera.setParameters(parameters);  
                  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
  
        public void surfaceChanged(SurfaceHolder holder, int format, int width,  
                int height) {  
            System.out.println("surfaceChanged");
            /*solve huaping*/
            
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
            System.out.println("surfaceDestroyed_Mainupload");  
            if (mCamera != null) {  
                mCamera.release();  
                mCamera = null;  
            }  
        }  
  
    }  
  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        if (event.getAction() == MotionEvent.ACTION_DOWN) {  
            //mPannelController.setVisibility(ViewGroup.VISIBLE);  
            return true;  
        }  
        return super.onTouchEvent(event);  
    }  
    /*public void MarkGeoTagImage(String imagePath,Location location)
    {
    try {
    ExifInterface exif = new ExifInterface(imagePath);
    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GPS.convert(location.getLatitude()));
    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, GPS.latitudeRef(location.getLatitude()));
    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GPS.convert(location.getLongitude()));
    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, GPS.longitudeRef(location.getLongitude()));
    SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    exif.setAttribute(ExifInterface.TAG_DATETIME,fmt_Exif.format(new Date(location.getTime())));
    exif.saveAttributes();
    } catch (IOException e) {
    e.printStackTrace();
    }
    }*/
   /* @Override
    public void onLocationChanged(Location location) {
    
    Log.i(TAG,"Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }
    @Override
    public void onProviderDisabled(String provider) {
    Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
    Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    Log.d("Latitude","status");
    }*/
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
    
    public boolean isCameraUsebyApp() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            return true;
        } finally {
            if (camera != null) camera.release();
        }
        return false;
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
    
    
}