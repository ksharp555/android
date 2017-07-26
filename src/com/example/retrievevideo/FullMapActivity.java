package com.example.retrievevideo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FullMapActivity extends FragmentActivity implements OnMapReadyCallback{
	private SupportMapFragment mapFragment;
	private LatLng location;
	@Override
	 public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       this.setContentView(R.layout.activity_fullmap);
       Intent i = getIntent();
       location = i.getParcelableExtra("location");
       mapFragment = (SupportMapFragment) getSupportFragmentManager()
               .findFragmentById(R.id.map);
       try{
       		mapFragment.getMapAsync(this);
       	}catch(Exception e){
       		e.printStackTrace();
       	}
       
	}
	@Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Houston
        
        map.addMarker(new MarkerOptions().position(location).title("Marker in here"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        
    }
}
