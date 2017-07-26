package com.example.retrievevideo;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yzi.util.php_getMenuItem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListTreeActivity extends Activity {
	String[] itemname={"1"};
	ListView listView;
	String tagname="a";
	itemAdapter adapter;
	private int level=0;
	private Activity context;
	private String[] levelcollection;
	private int emptyflag=0;
	private php_getMenuItem php;
	private static Intent ListTreeAds;
	//int flagtaskcomplete=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		listView = (ListView)findViewById(R.id.list1);
		ListTreeAds = new Intent(getApplicationContext(), ListTreeAds.class);
		
		context = this;
		
		
		new retrieveDataTask().execute();
		//listView.setAdapter(adapter);
		
		
		
		
	}
	
	private class itemAdapter extends ArrayAdapter<String>{
		private final String[] itemname;
		private final Activity context;
		
		public itemAdapter(Activity context, String[] itemname){
			super(context, R.layout.simple_listrow, itemname);
			this.context=context;
			this.itemname=itemname;
		}
		@Override
		public View getView(int position,View view,ViewGroup parent) {
			LayoutInflater inflater=context.getLayoutInflater();
			View rowView=inflater.inflate(R.layout.simple_listrow, null,true);
			TextView TagView = (TextView) rowView.findViewById(R.id.tagname);
			TagView.setText(itemname[position]);
			return rowView;
		}
	}
	
	class retrieveDataTask extends AsyncTask<Void, Void, String[]>{
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
		protected String[] doInBackground(Void... params) {
			php = new php_getMenuItem(tagname, level);
			JSONArray dataGroup;
			dataGroup = php.getdata();
			if (dataGroup == null){
				return null;
			}else{
				Log.i("params-------", "-------"+tagname+"---"+level);
				try{
					itemname=new String[dataGroup.length()];
					for(int i=0; i<dataGroup.length();i++){
						JSONObject jObject= dataGroup.getJSONObject(i);
						Log.i("jsonobjectTree", dataGroup.getJSONObject(i).toString());
						if(jObject!=null){
							String ltag=jObject.getString("l"+(level+1)+"tag");
							Log.i("jsonobjectTree", ltag);
							itemname[i]=ltag;
						}
					}	
				}catch(Exception e){
					e.printStackTrace();
				}
				return itemname;
			}
		}
		
		protected void onPostExecute(final String[] itemname) {
			adapter = new itemAdapter(context, itemname);
			listView.setAdapter(adapter);
			Log.i("emptyflag", "************"+php.emptyflag);
			if(php.emptyflag == 1){
				ListTreeAds.putExtra("level", level);
				
				Log.i("level", "------------"+level+"---------");
				
				finish();
				startActivity(ListTreeAds);
			}
			listView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (emptyflag == 1){
					}else{
						String Slecteditem= itemname[+position];
						tagname=Slecteditem;
						Log.i("tagname", "******"+tagname+"********");
						level++;
						Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
						Intent i = new Intent(getApplicationContext(), ListTreeActivity2.class);
						i.putExtra("level", level);
						i.putExtra("tagname", tagname);
						ListTreeAds.putExtra("tagname1", tagname);
						/*switch (level){
						case 1:
							ListTreeAds.putExtra("tagname1", tagname);
							break;
						case 2:
							ListTreeAds.putExtra("tagname2", tagname);
							break;
						case 3:
							ListTreeAds.putExtra("tagname3", tagname);
							break;
						case 4:
							ListTreeAds.putExtra("tagname4", tagname);
							break;
						case 5:
							ListTreeAds.putExtra("tagname5", tagname);
							break;
						}*/
						
						finish();
					    startActivity(i);
					}
				}
			});
		}
	}
}
