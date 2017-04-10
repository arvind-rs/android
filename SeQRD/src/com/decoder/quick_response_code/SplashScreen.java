package com.decoder.quick_response_code;

import com.decoder.quick_response_code.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		new Handler().postDelayed(new Runnable(){
			public void run(){
				Intent intent=new Intent(SplashScreen.this,MainActivity.class);
				startActivity(intent);
				SplashScreen.this.finish();
			}
		},1000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_splash_screen, menu);
		return true;
	}

}
