package com.decoder.quick_response_code;

import com.decoder.quick_response_code.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	String result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void decode(View v){
		Intent intent_decode=new Intent(this,CaptureActivity.class);
		startActivityForResult(intent_decode,1);
	}
	
	protected void onActivityResult(int requestcode,int resultcode,Intent data){
		if(requestcode==1){
			if(resultcode==RESULT_OK){
				result=data.getStringExtra("result");
				Bundle b=new Bundle();
				b.putString("res", result);
				Intent intent_main=new Intent(this,MainActivity1.class);
				intent_main.putExtras(b);
				startActivity(intent_main);
			}
		}		
	}
	
	public void about(View v){
		Intent intent_about=new Intent(this,About.class);
		startActivity(intent_about);
	}
	
	public void exitApp(View v){
		MainActivity.this.finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
