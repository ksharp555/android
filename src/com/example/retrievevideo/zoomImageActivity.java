package com.example.retrievevideo;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import com.example.retrievevideo.emergencymessaging.blurImageFunction; 

public class zoomImageActivity extends Activity implements OnTouchListener 
{
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    private String picpath;
    private String[] picpathFull;
    private Button next, prev;
    private int positionIndex;
    private ImageView view, backgroundView;
    
    private blurImageFunction blurImage; 
    private Context context;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wholeimage);
        context = zoomImageActivity.this;
        view = (ImageView) findViewById(R.id.wholeImage);
        backgroundView = (ImageView) findViewById(R.id.blurImage);
        next = (Button)findViewById(R.id.NEXT);
        prev = (Button)findViewById(R.id.PREV);
        
        Intent i = getIntent();
        picpath = i.getStringExtra("picpath");
        picpathFull = i.getStringArrayExtra("picpathFull");
        positionIndex = i.getIntExtra("positionIndex", 0);
        Log.i("picpath-----------", picpath);
        new GettingPhotos(view, backgroundView).execute(picpath);
        view.setOnTouchListener(this);
        next.setOnClickListener(new OnClickListener(){
        	 @Override
      	    public void onClick(View v){
        		 positionIndex++;
        		 if(positionIndex >= picpathFull.length - 1){
	        		 Log.i("Sring array length", "the length is "+picpathFull.length);
	        		 positionIndex = picpathFull.length - 1;
	        		 new GettingPhotos(view, backgroundView).execute(picpathFull[(+positionIndex)]);
	        	}else{
	        		new GettingPhotos(view, backgroundView).execute(picpathFull[(+positionIndex)]);
	        	}
        	 }
        });
        
        prev.setOnClickListener(new OnClickListener(){
       	 @Override
     	    public void onClick(View v){
       		 positionIndex--;
       		 if(positionIndex < 0){
       			positionIndex = 0;
       			 new GettingPhotos(view, backgroundView).execute(picpathFull[(+positionIndex)]);
       		 }else{
       			new GettingPhotos(view, backgroundView).execute(picpathFull[(+positionIndex)]);
       		 }
       	 }
       });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) 
    {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK) 
        {
            case MotionEvent.ACTION_DOWN:   // first finger down only
            									matrix.set(view.getImageMatrix());
                                                savedMatrix.set(matrix);
                                                start.set(event.getX(), event.getY());
                                                Log.d(TAG, "mode=DRAG"); // write to LogCat
                                                mode = DRAG;
                                                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                                                mode = NONE;
                                                Log.d(TAG, "mode=NONE");
                                                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                                                oldDist = spacing(event);
                                                Log.d(TAG, "oldDist=" + oldDist);
                                                if (oldDist > 5f) {
                                                    savedMatrix.set(matrix);
                                                    midPoint(mid, event);
                                                    mode = ZOOM;
                                                    Log.d(TAG, "mode=ZOOM");
                                                }
                                                break;

            case MotionEvent.ACTION_MOVE:

                                                if (mode == DRAG) 
                                                { 
                                                    matrix.set(savedMatrix);
                                                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                                                } 
                                                else if (mode == ZOOM) 
                                                { 
                                                    // pinch zooming
                                                    float newDist = spacing(event);
                                                    Log.d(TAG, "newDist=" + newDist);
                                                    if (newDist > 5f) 
                                                    {
                                                        matrix.set(savedMatrix);
                                                        scale = newDist / oldDist; // setting the scaling of the
                                                                                    // matrix...if scale > 1 means
                                                                                    // zoom in...if scale < 1 means
                                                                                    // zoom out
                                                        matrix.postScale(scale, scale, mid.x, mid.y);
                                                    }
                                                }
                                                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event) 
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event) 
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event) 
    {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) 
        {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) 
        {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }
    
    class GettingPhotos extends AsyncTask<String, Void, Bitmap>{
		private final WeakReference<ImageView> imageViewReference;
		private final WeakReference<ImageView> imageViewReference1;
		
		public GettingPhotos(ImageView imageView, ImageView imageView1) {
			imageViewReference = new WeakReference<ImageView>(imageView);
			imageViewReference1 = new WeakReference<ImageView>(imageView1);
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			return downloadBitmap(params[0]);
		}
		
		 @Override
		 protected void onPostExecute(Bitmap bitmap) {
	        if (isCancelled()) {
	            bitmap = null;
	        }

	        if (imageViewReference != null) {
	            ImageView imageView = imageViewReference.get();
	            ImageView imageView1 = imageViewReference1.get();
	            if (imageView != null) {
	                if (bitmap != null) {
	                    imageView.setImageBitmap(bitmap);
	                    blurImage = new blurImageFunction();
	                    imageView1.setImageBitmap(blurImage.blur(context, bitmap));
	                   
	                } else {
	                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.icon);
	                    imageView.setImageDrawable(placeholder);
	                }
	            }
	        }
		 }	
	}
	
	private Bitmap downloadBitmap(String url) {
	    HttpURLConnection urlConnection = null;
	    try {
	        URL uri = new URL(url);
	        urlConnection = (HttpURLConnection) uri.openConnection();
	        int statusCode = urlConnection.getResponseCode();
	        if (statusCode != HttpStatus.SC_OK) {
	            return null;
	        }

	        InputStream inputStream = urlConnection.getInputStream();
	        if (inputStream != null) {
	        	BitmapFactory.Options options = new BitmapFactory.Options();
	        	options.inSampleSize = 2;
	            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
	            return bitmap;
	        }
	    } catch (Exception e) {
	        urlConnection.disconnect();
	        Log.w("ImageDownloader", "Error downloading image from " + url);
	    } finally {
	        if (urlConnection != null) {
	            urlConnection.disconnect();
	        }
	    }
	    return null;
	}

}
