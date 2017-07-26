package com.example.retrievevideo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.retrievevideo.listAD_activity.retrieveDataTask;
import com.yzi.util.PHP_getAD;
import com.yzi.util.phpGetMenuAds;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListTreeAds extends Activity  {
	
	int maxlevel;
	
	String[] stringtagcollection;
	EditText searchText;
	Button searchBtn;
	String[] itemname;
	String[] imagePath;
	String[] description;
	String[] adid;
	ListView list;
	String query;
	String[] longi;
	String[] lat;
	ArrayList<String> stringList;
	boolean finishNetworkTask=false, finishNetworkTask_search=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		searchText = (EditText)findViewById(R.id.searchtext);
		searchBtn = (Button)findViewById(R.id.searchbtn);
		Intent intent = getIntent();
		maxlevel = intent.getIntExtra("level", 0);
		stringtagcollection = new String[5];
		Log.i("level and tagname in adsssssss", "**"+intent.getIntExtra("level",-1)+"**"+intent.getStringExtra("tagname2")+intent.getStringExtra("tagname1"));
		for (int i = 0; i < 5; i++){
			stringtagcollection[i] = intent.getStringExtra("tagname"+(i+1));
		}
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
			
			phpGetMenuAds php =new phpGetMenuAds(stringtagcollection, maxlevel);
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
