package com.example.retrievevideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yzi.util.PHP_getAD;
import com.yzi.util.phpGetEvent;

import GetLocation.GPSTracker;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class personalEventActivity extends Activity{
	String[] itemname;
	String[] imagePath;
	String[] description;
	String[] adid;
	ListView list;
	String query;
	String[] longi;
	String[] lat;
	private Activity context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manageevent);
		context = this;
		new retrieveDataTask().execute();
		}
	
	class retrieveDataTask extends AsyncTask<Void, Void, JSONArray>{
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
		protected JSONArray doInBackground(Void... params) {
			phpGetEvent php =new phpGetEvent("7");
			JSONArray dataGroup;
			dataGroup = php.getdata();
			return dataGroup;
		}
		
		protected void onPostExecute(JSONArray dataGroup) {
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
				ad_customlist adapter=new ad_customlist(context, itemname, imagePath, description);
				list=(ListView)findViewById(R.id.listEvent);
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
			}catch(JSONException e){
				e.printStackTrace();
				Log.e("internet error", "no connection");
				Toast.makeText(getApplicationContext(), "no connection", Toast.LENGTH_SHORT).show();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		}
}
