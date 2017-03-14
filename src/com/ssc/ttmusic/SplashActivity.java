package com.ssc.ttmusic;

import com.ssc.ttmusic.service.PlayService;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity {

	public static final int DELAY_TIME=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        startService(new Intent(this,PlayService.class));
        new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startActivity(new Intent(SplashActivity.this,MainActivity.class));
				finish();
			}
		}, DELAY_TIME);
    }
   
}
