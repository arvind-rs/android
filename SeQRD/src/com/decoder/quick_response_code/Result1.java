package com.decoder.quick_response_code;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Result1 extends Activity {

	String result;
	String sec_per;
	String get_result;
	int c;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result1);
		TextView tv=(TextView)findViewById(R.id.textView3);
		Bundle b=getIntent().getExtras();
		result=b.getString("url");
		String get_result1=b.getString("sco");
		tv.setMovementMethod(new ScrollingMovementMethod());
		tv.setText(get_result1);
	}
	
	public void openURL(View v){
		try{
			String url;
			url=result;
			Intent intent_browser=new Intent(Intent.ACTION_VIEW);
			intent_browser.setData(Uri.parse(url));
			startActivity(intent_browser);
		}
		catch(Exception e){
			Toast toast=Toast.makeText(this, "There is no URL to check", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 20, 100);
			toast.show();
		}
	}
	
	public void back(View v){
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_result1, menu);
		return true;
	}

}
