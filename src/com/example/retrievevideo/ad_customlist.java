package com.example.retrievevideo;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ad_customlist  extends ArrayAdapter<String>{
	private final Activity context;
	private final String[] itemname;
	private final String[] imagePath;
	private final String[] description;
	URL url;
	Bitmap bm;
	boolean finishNewworkingFlag=false;
	final BitmapFactory.Options options = new BitmapFactory.Options();
	

	public ad_customlist(Activity context, String[] itemname, String[] imagePath, String[] description) {
		super(context, R.layout.listrow, itemname);
		// TODO Auto-generated constructor stub
		
		this.context=context;
		this.itemname=itemname;
		this.imagePath=imagePath;
		this.description=description;
	}
	
	public View getView(int position,View view,ViewGroup parent) {
		LayoutInflater inflater=context.getLayoutInflater();
		View rowView=inflater.inflate(R.layout.listrow, null,true);
		
		TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		TextView extratxt = (TextView) rowView.findViewById(R.id.search);
		//new GettingPhotos(position).execute();
		//while(finishNewworkingFlag==false){;}
		//content input
		
		txtTitle.setText(itemname[position]);
		if(imageView !=null){
			new GettingPhotos(imageView).execute(imagePath[position]);
		}
		extratxt.setText(description[position]);
		
		return rowView;
		
	};
	class GettingPhotos extends AsyncTask<String, Void, Bitmap>{
		private final WeakReference<ImageView> imageViewReference;

		public GettingPhotos(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
	    
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
	            if (imageView != null) {
	                if (bitmap != null) {
	                    imageView.setImageBitmap(bitmap);
	                    
	                   
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
	        	options.inSampleSize = 8;
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
