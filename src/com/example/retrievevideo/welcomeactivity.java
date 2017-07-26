package com.example.retrievevideo;



import com.example.retrievevideo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class welcomeactivity extends Activity {

	private final long SPLASH_LENGTH=500;
	Handler handler = new Handler();
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomesurface);
        handler.postDelayed(new Runnable() {  
        	
            public void run() {  
                Intent intent = new Intent(welcomeactivity.this, Selectfunction.class);  
                startActivity(intent);  
                finish();     
            }  
        }, SPLASH_LENGTH);//hold this surfaceView for 2s
        
        
	}
}
