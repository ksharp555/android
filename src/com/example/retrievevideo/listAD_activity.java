package com.example.retrievevideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import GetLocation.GPSTracker;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.ListView;
import android.widget.Toast;
import com.yzi.util.PHP_getAD;
import com.yzi.util.PHP_search;
public class listAD_activity extends ActionBarActivity{
	String[] itemname;
	String[] imagePath;
	String[] description;
	String[] adid;
	ListView list;
	String query;
	String[] longi;
	String[] lat;
	EditText searchText;
	Button searchBtn;
	boolean finishNetworkTask=false, finishNetworkTask_search=false;
	int i=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		//Intent intent = getIntent();
		searchText = (EditText)findViewById(R.id.searchtext);
		searchBtn = (Button)findViewById(R.id.searchbtn);
		finishNetworkTask=false;
	    new retrieveDataTask().execute();
		while(finishNetworkTask==false){
			;
		}
		//if(finishNetworkTask){
		try{
			ad_customlist adapter=new ad_customlist(this, itemname, imagePath, description);
			list=(ListView)findViewById(R.id.list1);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					String Slecteditem= itemname[+position];
					Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
					LinearLayout ll = (LinearLayout) view;
					TextView tv = (TextView) ll.findViewById(R.id.item);
					String product ="This is the "+ tv.getText().toString()+
							" details and it will be improved by retrieving details " +
							"from database and picture will be showed below";
					 Intent i = new Intent(getApplicationContext(), listItemDetailsActivity.class);
					 i.putExtra("product", Slecteditem);
					 i.putExtra("details", description[+position]);
					 i.putExtra("lat", lat[+position]);
					 i.putExtra("longi", longi[+position]);
					 i.putExtra("adid", adid[+position]);
					 startActivity(i);
				}
			});
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "no connection or no data", Toast.LENGTH_SHORT).show();
		}
		
		searchBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finishNetworkTask_search=false;
				new searchDataTask(searchText.getText().toString()).execute();
				while(finishNetworkTask_search==false){
					//Log.i("running......", "networking");
				}
				ad_customlist adapter=new ad_customlist(listAD_activity.this, itemname, imagePath, description);
				list=(ListView)findViewById(R.id.list1);
				list.setAdapter(adapter);
			}
		});

	//}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.upload_advertisement, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.uploadAD:
			Intent intent = new Intent(listAD_activity.this, TestPicActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
	    switch(keycode) {
	        case KeyEvent.KEYCODE_MENU:
	        	 openOptionsMenu();
	            return true;
	    }

	    return super.onKeyDown(keycode, e);
	}
	
	class searchDataTask extends AsyncTask<Void, Void, Void>{
		private String query;
		public searchDataTask(String string) {
			query=string;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			PHP_search php = new PHP_search(query);
			JSONArray dataGroup;
			
			dataGroup = php.getdata();
			Log.i("query-------", query);
			
			try{
				itemname=new String[dataGroup.length()];
				description=new String[dataGroup.length()];
				imagePath=new String[dataGroup.length()];
				longi = new String[dataGroup.length()];
				lat = new String[dataGroup.length()];
				adid = new String[dataGroup.length()];
				for(int i=0; i<dataGroup.length();i++){
					JSONObject jObject= dataGroup.getJSONObject(i);
					Log.i("jsonobject1", dataGroup.getJSONObject(i).toString());
					if(jObject!=null){
						String title=jObject.getString("title");
						Log.i("jsonobject1", title);
						itemname[i]=title;
						description[i]=jObject.getString("description");
						imagePath[i]=jObject.getString("picpath");
						adid[i] = jObject.getString("advertisementid");
						Log.i("adid-------------1", jObject.getString("advertisementid"));
						Log.i("adid-------------1", adid[i]);
						Log.i("position-------------1", jObject.getString("location"));
						String lAndl[] =jObject.getString("location").split(",");
						lat[i] = lAndl[0].substring(1);
						longi[i] = lAndl[1].substring(0,lAndl[1].length()-1);
						Log.i("position-------------1", lat[i]+" **** "+longi[i]);
					}else
					{
						Log.i("query-------", query+"is null");
					}
				}
				}catch(JSONException e){
					e.printStackTrace();
				}
			finishNetworkTask_search=true;
			return null;
		}
	}
	class retrieveDataTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// Utils.showProgressDialog(GroupChattingActivity.this,
			// "Getting...");
		}

		public retrieveDataTask() {

		}

		@Override
		protected Void doInBackground(Void... params) {
			GPSTracker gps;
			gps = new GPSTracker(listAD_activity.this);
			String lat1 = String.valueOf(gps.getLatitude());
			String longi1 = String.valueOf(gps.getLongitude());
			PHP_getAD php =new PHP_getAD(lat1, longi1);
			JSONArray dataGroup;
			dataGroup = php.getdata();
			try{
				if(dataGroup == null){
					finish();
					
				}else{
					itemname=new String[dataGroup.length()];
					description=new String[dataGroup.length()];
					imagePath=new String[dataGroup.length()];
					longi = new String[dataGroup.length()];
					lat = new String[dataGroup.length()];
					adid = new String[dataGroup.length()];
					for(int i=0; i<dataGroup.length();i++){
						JSONObject jObject= dataGroup.getJSONObject(i);
						Log.i("jsonobject", dataGroup.getJSONObject(i).toString());
						if(jObject!=null){
							String title=jObject.getString("title");
							Log.i("jsonobject", title);
							itemname[i]=title;
							description[i]=jObject.getString("description");
							imagePath[i]=jObject.getString("picpath");
							adid[i] = jObject.getString("advertisementid");
							Log.i("adid-------------", jObject.getString("advertisementid"));
							Log.i("adid-------------", adid[i]);
							Log.i("position-------------", jObject.getString("location"));
							String lAndl[] =jObject.getString("location").split(",");
							lat[i] = lAndl[0].substring(1);
							longi[i] = lAndl[1].substring(0,lAndl[1].length()-1);
							Log.i("position-------------", lat[i]+" **** "+longi[i]);
						}
				}
			}
			}catch(JSONException e){
				e.printStackTrace();
				Log.e("internet error", "no connection");
				Toast.makeText(getApplicationContext(), "no connection", Toast.LENGTH_SHORT).show();
			}
			finishNetworkTask=true;
			return null;
		}

	}
	
}
