package com.example.retrievevideo;

import java.io.File;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.example.retrievevideo.R;
import com.yzi.util.UploadUtil;
import GetLocation.GPSTracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class videoactivity extends ActionBarActivity {
	private String outputFile = null;
    private SurfaceHolder surfaceHolder1;
    private SurfaceView surfaceView;
    private boolean safeToTakePicture = false;
    public MediaRecorder mrec = new MediaRecorder();
    File video;
    private Camera mCamera=null;
    private Button startbutton;
    private static String requestURL = "http://104.236.202.116:8080/abc/p/file!upload";
    public static String videoPath = null;
    GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoui);
        gps=new GPSTracker(videoactivity.this);

                Log.i(null , "Video starting");
                mCamera = Camera.open();
                mCamera.setDisplayOrientation(90);
                surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
                surfaceHolder1=surfaceView.getHolder();
                surfaceView.getHolder().setKeepScreenOn(true); 
                surfaceHolder1.addCallback(new SurfaceCallback());
                surfaceHolder1.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                startbutton=(Button)findViewById(R.id.startbtn);
                startbutton.setOnClickListener(new OnClickListener(){
                	public void onClick(View v) {
                		try{
                			
                			startRecording();
                		}catch (Exception e) {
                            String message = e.getMessage();
                            Log.i(null, "Problem Start"+message);
                            mrec.release();
                        }
                		
                	}
                	}
                	
                );
            }

		    /*@Override
		    public boolean onKeyDown(int keyCode, KeyEvent event)  {
		        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
		            // do something on back.
		        	try{
		        	mCamera.release();
		        	mCamera=null;
	                Log.i("video_camera","camera release");
	                Intent intent = new Intent(videoactivity.this, Mainupload.class);
					startActivity(intent);
					}catch(Exception e)
					{
						Toast.makeText(videoactivity.this, e.getMessage(),  
                                Toast.LENGTH_SHORT).show(); 
					}
		            return true;
		        }
		
		        return super.onKeyDown(keyCode, event);
		    }*/
    
            @Override
            public boolean onCreateOptionsMenu(Menu menu)
            {
            	getMenuInflater().inflate(R.menu.menu_record2, menu);
                //menu.add(0, 0, 0, "5 sec recording");
                //menu.add(0, 1, 0, "10 sec recording");
            	return true;
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item)
            {
            	switch (item.getItemId()) {

        		case R.id.picRecord:
        			try{
        				
        				mCamera.release();
        				finish();
        				Intent intent = new Intent(videoactivity.this, Mainupload.class);
        				startActivity(intent);
        			}catch (Exception e) {  
                        e.printStackTrace();
                        Toast.makeText(videoactivity.this, "Some error comes",  
                                Toast.LENGTH_SHORT).show();
                    }
        			break;

        		case R.id.audioRecord:
        			try{
        				if(mCamera!=null)
	        			mCamera.release();
        				if(mrec!=null)
        				mrec.release();
	        			Intent intent1 = new Intent(videoactivity.this, audioactivity.class);
	        			startActivity(intent1);
        			}catch(Exception e){
        				System.out.println(e.getMessage());
        			}
        			break;

        		default:
        		}

        		return super.onOptionsItemSelected(item);
            }

            protected void startRecording() throws IOException 
            {
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
                

                //mrec.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
                
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
  		      .format(new Date());
                mrec.setPreviewDisplay(surfaceHolder1.getSurface());
                //videoPath=Environment.getExternalStorageDirectory().getPath()+"/video"+timeStamp+".mp4";
                outputFile = Environment.getExternalStorageDirectory().
  		    		  getAbsolutePath() + "/video"+timeStamp+".mp4";
                mrec.setOutputFile(outputFile); 
                videoPath=outputFile;
                
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
        		uploadUtil.uploadFile(videoPath,fileKey, requestURL,params,LatAndLong);
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
            // SurfaceCallback  
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