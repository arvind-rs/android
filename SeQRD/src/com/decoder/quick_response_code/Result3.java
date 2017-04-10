package com.decoder.quick_response_code;

import com.decoder.quick_response_code.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class Result3 extends Activity {

	String result;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result3);
		TextView tv=(TextView)findViewById(R.id.textView3);
		Bundle b=getIntent().getExtras();
		String get_result=b.getString("sco");
		result=b.getString("url");
		tv.setText(get_result);
	}
	
	public void openURL(View v){
		String url=result;
		Intent intent_browser=new Intent(Intent.ACTION_VIEW);
		intent_browser.setData(Uri.parse(url));
		startActivity(intent_browser);
	}
	
	public void back(View v){
		Result3.this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_result3, menu);
		return true;
	}

}
