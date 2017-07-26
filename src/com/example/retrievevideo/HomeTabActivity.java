package com.example.retrievevideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.yzi.util.PHP_getAD;
import com.yzi.util.PHP_search;

import GetLocation.GPSTracker;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HomeTabActivity extends FragmentActivity implements ActionBar.TabListener  {
    
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	ViewPager mViewPager;
	SharedPreferences sharedpreferences;
	
	public static final String MyPREFERENCES = "MyPrefs";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }
    
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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
			Intent TestPicActivityintent = new Intent(HomeTabActivity.this, TestPicActivity.class);
			startActivity(TestPicActivityintent);
			break;
		case R.id.tree:
			Intent ListTreeActivityintent = new Intent(HomeTabActivity.this, ListTreeActivity.class);
			startActivity(ListTreeActivityintent);
			break;
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

    
    
	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    return new TabListFragment();
                case 2:
                	 return new PersonalInfoFragment();
                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new TabSecurityFragment();
                   
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0){
            	return "list";
            }
            else if(position == 2){
            	return "personal";
            }
            else{
            	return "security";
            }
        }
    }
	
    public static class TabSecurityFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.tab_security, container, false);

         // Demonstration of navigating to external activities.
            rootView.findViewById(R.id.recordButton)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), Mainupload.class);
                            startActivity(intent);
                        }
                    });

            
            rootView.findViewById(R.id.viewButton)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create an intent that asks the user to pick a photo, but using
                            // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                            // the application from the device home screen does not return
                            // to the external activity.
                            Intent intent = new Intent(getActivity(), RetrievePictureActivity.class);
                            startActivity(intent);
                        }
                    });
            
            rootView.findViewById(R.id.chatButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create an intent that asks the user to pick a photo, but using
                    // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                    // the application from the device home screen does not return
                    // to the external activity.
                    Intent intent = new Intent(getActivity(), ChatGroupsActivity.class);
                    startActivity(intent);
                }
            });
            
            rootView.findViewById(R.id.messagingButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create an intent that asks the user to pick a photo, but using
                    // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                    // the application from the device home screen does not return
                    // to the external activity.
                    Intent intent = new Intent(getActivity(), EmergencyMessagingctivity.class);
                    startActivity(intent);
                }
            });
            return rootView;
        }
    }
    
    public static class TabListFragment extends Fragment {
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
    	String lat1,longi1;
    	boolean finishNetworkTask=false, finishNetworkTask_search=false;
    	int i=1;
    	
    	  @Override
          public View onCreateView(LayoutInflater inflater, ViewGroup container,
                  Bundle savedInstanceState) {
    		 final View rootView = inflater.inflate(R.layout.tab_list, container, false);
    		  searchText = (EditText)rootView.findViewById(R.id.searchtext);
    		  searchBtn = (Button)rootView.findViewById(R.id.searchbtn);
    		  GPSTracker gps;
			  gps = new GPSTracker(getActivity());
			  lat1 = String.valueOf(gps.getLatitude());
			  longi1 = String.valueOf(gps.getLongitude());
			  Log.i("gps", lat1+", "+longi1);
    		 finishNetworkTask=false;
    		 new retrieveDataTask().execute();
    			while(finishNetworkTask==false){
    				;
    			}
    			try{
    				ad_customlist adapter=new ad_customlist(getActivity(), itemname, imagePath, description);
    				list=(ListView)rootView.findViewById(R.id.list1);
    				list.setAdapter(adapter);
    				list.setOnItemClickListener(new OnItemClickListener() {

    					@Override
    					public void onItemClick(AdapterView<?> parent, View view,
    							int position, long id) {
    						// TODO Auto-generated method stub
    						String Slecteditem= itemname[+position];
    						Toast.makeText(getActivity(), Slecteditem, Toast.LENGTH_SHORT).show();
    						LinearLayout ll = (LinearLayout) view;
    						TextView tv = (TextView) ll.findViewById(R.id.item);
    						String product ="This is the "+ tv.getText().toString()+
    								" details and it will be improved by retrieving details " +
    								"from database and picture will be showed below";
    						 Intent i = new Intent(getActivity(), listItemDetailsActivity.class);
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
    				Toast.makeText(getActivity(), "no connection or no data", Toast.LENGTH_SHORT).show();
    			}
    			
    			searchBtn.setOnClickListener(new OnClickListener(){
    				@Override
    				public void onClick(View v) {
    					finishNetworkTask_search=false;
    					new searchDataTask(searchText.getText().toString()).execute();
    					while(finishNetworkTask_search==false){
    						//Log.i("running......", "networking");
    					}
    					ad_customlist adapter=new ad_customlist(getActivity(), itemname, imagePath, description);
    					list=(ListView)rootView.findViewById(R.id.list1);
    					list.setAdapter(adapter);
    				}
    			});
				return rootView;
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
    				
    				PHP_getAD php =new PHP_getAD(lat1, longi1);
    				JSONArray dataGroup;
    				dataGroup = php.getdata();
    				try{
    					if(dataGroup == null){
    						getActivity().finish();
    						
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
    					Toast.makeText(getActivity(), "no connection", Toast.LENGTH_SHORT).show();
    				}
    				finishNetworkTask=true;
    				return null;
    			}

    		}

    }
}
