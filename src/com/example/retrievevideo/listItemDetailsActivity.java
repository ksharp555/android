package com.example.retrievevideo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;




import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yzi.util.php_retrievephotos;

import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class listItemDetailsActivity extends FragmentActivity implements OnMapReadyCallback {
	
	
	double lat;
	double longi;
	private Integer images[] = {R.drawable.nfs18, R.drawable.google, R.drawable.lol};
	int numberdata=10;
	boolean flag=false;
	String adid;
	String[] picpath;
	LinearLayout imageGallery;
	GridView noScrollgridview;
	PhotoAdapter adapter;
	SupportMapFragment mapFragment;
	
	private Activity context;
	int flag1=0;
	@Override
	 public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list_item_details);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        context = this;
        
        Log.i("number data********************", "number is "+numberdata);
        TextView txtProduct = (TextView) findViewById(R.id.TextView01);
        TextView txtDetails = (TextView) findViewById(R.id.details);
        // ListView list1=(ListView)findViewById(R.id.list1);
        //ad_customlist adapter=new ad_customlist(this, itemname, imgid);
        //list1.setAdapter(adapter);
        Intent i = getIntent();
        // getting attached intent data
        String product = i.getStringExtra("product");
        adid = i.getStringExtra("adid");
        // displaying selected product name
        Log.i("title",product);
        Log.i("details",i.getStringExtra("details"));
        Log.i("adid***********",adid);
        txtProduct.setText(product);
        txtDetails.setText(i.getStringExtra("details"));
        lat = Double.parseDouble(i.getStringExtra("lat"));
        longi = Double.parseDouble(i.getStringExtra("longi"));
        Log.i("location***********",lat+","+longi);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        try{
        		mapFragment.getMapAsync(this);
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        
        Log.i("number data********************---------", "number is "+numberdata);
        //addImagesToThegallery();
        getImageView();
        /*while(flag1 != 1){
        	if(picpath != null && flag1 == 1){
	        	adapter = new PhotoAdapter(this, picpath);
	            noScrollgridview.setAdapter(adapter);
	            break;
            }
        }*/
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
        	@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
        		//String Slecteditem= picpath[+position];
				//Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getApplicationContext(), zoomImageActivity.class);
				i.putExtra("picpath", picpath[+position]);
				i.putExtra("picpathFull", picpath);
				i.putExtra("positionIndex", position);
				startActivity(i);
        	}
        });
        
	}
	
	 @Override
	    public void onMapReady(GoogleMap map) {
	        // Add a marker in Houston
	        final LatLng Houston = new LatLng(lat, longi);
	        map.addMarker(new MarkerOptions().position(Houston).title("Marker in here"));
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Houston, 15));
	        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
				@Override
				public void onMapClick(LatLng point) {
					// TODO Auto-generated method stub
					Intent i =new Intent(getApplicationContext(), FullMapActivity.class);
			        i.putExtra("location", Houston);
			        startActivity(i);
				}
			});
	        
	    }
	 
	 /*private void addImagesToThegallery() {
	        imageGallery = (LinearLayout) findViewById(R.id.imageGallery);
	        View[] imageView = new ImageView[numberdata];
	        for (int i=0;i<numberdata;i++){
	        	imageView[i]=new ImageView(getApplicationContext());
	        }
	        imageView = getImageView();
	        for (int i=0; i<numberdata; i++) {
	            imageGallery.addView(imageView[i]);
	        }
	    }*/
	 
	 private View[] getImageView() {
	        ImageView[] imageView = new ImageView[numberdata];
	        for (int i=0;i<numberdata;i++){
	        	imageView[i]=new ImageView(getApplicationContext());
	        	imageView[i].setId(i);
	        }
	        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	        lp.setMargins(0, 0, 10, 0);
	        for(int i=0; i<numberdata;i++){
	        	imageView[i].setLayoutParams(lp);
	        }
	        new retrievephotos(imageView).execute();
            
	        return imageView;
	    }
	 
	 class retrievephotos extends AsyncTask<Void, Void, Bitmap[]>{
		 
		 ImageView[] bmImage;
		 //Activity context;
		 
		 public retrievephotos(ImageView[] bmImage){
			 this.bmImage = bmImage;
			 
		 }
		 
		 @Override
			protected Bitmap[] doInBackground(Void... params) {
			 php_retrievephotos php = new php_retrievephotos(adid);
			 JSONArray dataGroup;
		     dataGroup = php.getdata();
		     Bitmap[] mIcon11 = new Bitmap[dataGroup.length()];
		     try{
		    	 picpath = new String[dataGroup.length()];
		    	 
		    		for(int i=0; i<dataGroup.length();i++){
		    			JSONObject jObject= dataGroup.getJSONObject(i);
						Log.i("jsonobject", dataGroup.getJSONObject(i).toString());
						if(jObject!=null){
							picpath[i] = jObject.getString("picpaths");
							Log.i("picpath**************", picpath[i]);
							//BitmapFactory.Options options = new BitmapFactory.Options();
				        	//options.inSampleSize = 2;
				            //InputStream in = new java.net.URL(picpath[i]).openStream();
				            mIcon11[i] = revitionImageSize(picpath[i]);
						}
		    		}
		    		//flag1 = 1;
		     }catch(Exception e){
		    	 e.printStackTrace();
		     }
		     return mIcon11;
		 }
		 
		 protected void onPostExecute(Bitmap[] result) {
			 for (int i=0; i<picpath.length;i++){
				 Log.i("number data********************", "number is11111"+picpath.length);
				 bmImage[i].setImageBitmap(result[i]);
				 
			 }
			 adapter = new PhotoAdapter(context, picpath);
	            noScrollgridview.setAdapter(adapter);  
		    }
	 }
	 
	 class numbertask extends AsyncTask<Void,Void,Integer>{
		 private ProgressDialog dialog;
	     
		 public numbertask(Activity activity) {
		        dialog = new ProgressDialog(activity);
		    }
		 
		 @Override
		    protected void onPreExecute() {
		        dialog.setMessage("Retriving data");
		        dialog.show();
		    }
		 
		 @Override
		 protected Integer doInBackground(Void... params){
			 php_retrievephotos php = new php_retrievephotos(adid);
			 JSONArray dataGroup;
			 dataGroup = php.getdata();
			 //numberdata = dataGroup.length();
			 return dataGroup.length();
		 }
		 @Override
		    protected void onPostExecute(Integer result) {
		        if (dialog.isShowing()) {
		            dialog.dismiss();
		            numberdata = result;
		        }
		    }
		
		 
	 }
	 
	 public Bitmap revitionImageSize(String path) throws IOException {
		 InputStream in = new java.net.URL(path).openStream();
			//BufferedInputStream in = new BufferedInputStream(new FileInputStream(
					//new File(path)));
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, options);
			in.close();
			
			int i = 0;
			Bitmap bitmap = null;
			while (true) {
				if ((options.outWidth >> i <= 256)
						&& (options.outHeight >> i <= 256)) {
					InputStream inn = new java.net.URL(path).openStream();
					options.inSampleSize = (int) Math.pow(2.0D, i);
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeStream(inn, null, options);
					break;
				}
				i += 1;
			}
			return bitmap;
		}
	 
	   
	 
}

