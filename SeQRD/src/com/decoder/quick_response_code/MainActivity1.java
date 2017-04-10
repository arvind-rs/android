package com.decoder.quick_response_code;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity1 extends Activity {

	String result;
	TextView tv;
	TextView tv1;
	
	ProgressDialog pb;
	int progressbarstatus=0;
	Handler progressbarhandler=new Handler();
	int GLOBAL_SECURE_WEIGHT=0;
	String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity1);
		Bundle b=getIntent().getExtras();
		result=b.getString("res");
		tv=(TextView)findViewById(R.id.textView1);
		tv.setMovementMethod(new ScrollingMovementMethod());
		tv.setText(result);
	}
	
	public void checkURL(View v){
		try
		{
			GLOBAL_SECURE_WEIGHT=0;
			tv=(TextView)findViewById(R.id.textView1);
			String result_pass=tv.getText().toString();
			url=result_pass;
			
			if(!url.contains("http")&&!url.contains("https")){
				Toast toast=Toast.makeText(this, "There is no URL to check", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 15, 100);
				toast.show();
				throw new Exception();
			}
			
			if(!isNetworkAvailable()){
				Toast toast=Toast.makeText(this, "There is no network connection!", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 15, 100);
				toast.show();
				return;
			}
			
			pb=new ProgressDialog(v.getContext());
			pb.setCancelable(true);
			pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pb.setMessage("analyzing...");
			pb.setProgress(0);
			pb.setMax(100);
			pb.show();
			
			new Thread(new Runnable(){
				public void run(){
						try{
						analyzer(url,GLOBAL_SECURE_WEIGHT);
						pb.dismiss();
						}
						catch(Exception e){
							e.printStackTrace();
						}
				}
			}).start();
			
		}
		catch(Exception e){
			Toast toast=Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 15, 100);
			toast.show();
		}
	}

	public void rescan(View v){
		Intent intent_decode=new Intent(this,CaptureActivity.class);
		startActivityForResult(intent_decode,1);
	}
	
	protected void onActivityResult(int requestcode,int resultcode,Intent data){
		if(requestcode==1){
			if(resultcode==RESULT_OK){
				String result_again=data.getStringExtra("result");
				tv1=(TextView)findViewById(R.id.textView1);
				tv1.setText(result_again);
			}
		}
	}
	
	public void back(View v){
		MainActivity1.this.finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_activity1, menu);
		return true;
	}
	
	public boolean isNetworkAvailable(){
		ConnectivityManager connectivity_manager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo active_net=connectivity_manager.getActiveNetworkInfo();
		return active_net!=null && active_net.isConnected();
	}
	
	public static String getDomainName(String url) throws URISyntaxException {
		URI uri=new URI(url);
		String domain=uri.getHost();
		String domain1=domain.startsWith("www.")?domain.substring(4):domain;
		return domain1.endsWith(".com")?domain1.substring(0,domain1.length()-4):domain1;
	}

	public static double cosineSimilarityTest(String a,String b){
		
		String str1=a;
		String str2=b;
		
		str1=str1.toLowerCase();
		str2=str2.toLowerCase();
		
		str1=str1.replaceAll("\\W", "");
		str2=str2.replaceAll("\\W", "");
		str1=str1.replaceAll("\\s", "");
		str2=str2.replaceAll("\\s", "");
		
		String str=str1.concat(str2);
		
		StringBuilder strnd=new StringBuilder();
		for(int i=0;i<str.length();i++){
			String si=str.substring(i, i+1);
			if(strnd.indexOf(si)==-1){
				strnd.append(si);
			}
		}
		str=strnd.toString();
		
		int count=0;
		int[] str1_freq=new int[str.length()+1];
		int[] str2_freq=new int[str.length()+1];
		String[] str_l=str.split("");
		String str11[]=str1.split("");
		String str22[]=str2.split("");
		
		for(int i=0;i<str_l.length;i++){
			count=0;
			for(int j=0;j<str11.length;j++){
				if(str11[j].equals(str_l[i])){
					count++;
				}
			}
			str1_freq[i]=count;
		}
		for(int i=0;i<str_l.length;i++){
			count=0;
			for(int j=0;j<str22.length;j++){
				if(str22[j].equals(str_l[i])){
					count++;
				}
			}
			str2_freq[i]=count;
		}
		
		//finding cosine angle c=(v1.v2)/(|v1|*|v2|)
		double c;
		int sum=0;
		for(int i=1;i<str1_freq.length;i++){
			sum=sum+(str1_freq[i]*str2_freq[i]);
		}
		int s1=0;
		for(int i=1;i<str1_freq.length;i++){
			s1=s1+(str1_freq[i]*str1_freq[i]);
		}
		int s2=0;
		for(int i=1;i<str2_freq.length;i++){
			s2=s2+(str2_freq[i]*str2_freq[i]);
		}
		c=sum/(Math.sqrt(s1)*Math.sqrt(s2));
		return c;
	}
	
	public void analyzer(String u,int weight){
		
		try
		{
		int GLOBAL_SECURE_WEIGHT=weight;
		String url=u;
		
		//heuristic approach
		HttpClient client1=new DefaultHttpClient();
		HttpGet request1=new HttpGet(url);
		HttpResponse response1=client1.execute(request1);

		String html="";
		InputStream in=response1.getEntity().getContent();
		BufferedReader reader=new BufferedReader(new InputStreamReader(in));
		StringBuilder str=new StringBuilder();
		String line=null;
		
		while((line=reader.readLine())!=null)
		{
			str.append(line);
		}
		in.close();
		html=str.toString();
		
		Document doc=Jsoup.parse(html);
		
		int d_weight=0;
		String url_c[]=url.split("");
		for(int i=0;i<url_c.length;i++){
			if(url_c[i].equals(".")){
				d_weight++;
			}
		}
		
		
		if(d_weight<=2){
			GLOBAL_SECURE_WEIGHT++;
		}else if(d_weight>2){
			GLOBAL_SECURE_WEIGHT--;
		}
		
		int ds_weight=0;
		Pattern p1=Pattern.compile("//");
		Matcher m1=p1.matcher(url);
		while(m1.find()){
			ds_weight++;
		}
		if(ds_weight<=1){
			GLOBAL_SECURE_WEIGHT++;
		}
		else if(ds_weight>1){
			GLOBAL_SECURE_WEIGHT--;
		}
		
		int ip_weight=0;
		String ip_url=url;
		Pattern p=Pattern.compile("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");
		Matcher m=p.matcher(ip_url);
		if(m.find()){
			ip_weight++;
		}
		if(ip_weight==0){	
			GLOBAL_SECURE_WEIGHT++;
		}else if(ip_weight>0){
			GLOBAL_SECURE_WEIGHT--;
		}
		
		String domain=getDomainName(url);

		String title=doc.select("title").text();

		String title1;
		double c=cosineSimilarityTest(domain,title);
		if(c>=0.88){
			GLOBAL_SECURE_WEIGHT++;
		}
		else{
			GLOBAL_SECURE_WEIGHT--;
		}

		Elements links=doc.select("a[href]");
		
		int a_weight=0;
		double a_c=0.0;
		String a_hostname="host";
		for(Element link:links){
			String a_url=link.attr("href");
			if((a_url.startsWith("http"))||(a_url.startsWith("https"))){
				a_hostname=getDomainName(a_url);
				a_c=cosineSimilarityTest(domain,a_hostname);
				if(a_c>=0.88){
					a_weight++;
				}
				else{
					a_weight--;
				}
			}else if(a_url.startsWith("/")){
				a_weight++;
			}else if(a_url.startsWith("#")){
				continue;
			}
		}
		title1=a_hostname;
		title1=Double.toString(a_c);
		title1=Integer.toString(a_weight);
		if(a_weight>=0){
			GLOBAL_SECURE_WEIGHT++;
		}else if(a_weight<0){
			GLOBAL_SECURE_WEIGHT--;
		}
		
		Elements imgs=doc.select("img");
		int i_weight=0;
		String i_hostname="";
		for(Element img:imgs){
			String src=img.attr("src");
			if((src.startsWith("http"))||(src.startsWith("https"))){
				i_hostname=getDomainName(src);
				a_c=cosineSimilarityTest(domain,i_hostname);
				if(a_c>=0.88){
					i_weight++;
				}
				else{
					i_weight--;
				}
			}else if(src.startsWith("/")){
				i_weight++;
			}else if(src.startsWith("#")){
				continue;
			}
		}
		title1=Integer.toString(i_weight);
		if(i_weight>=0){
			GLOBAL_SECURE_WEIGHT++;
		}else if(i_weight<0){
			GLOBAL_SECURE_WEIGHT--;
		}
		
		Elements forms=doc.select("form");
		double f_c=0.0;
		int f_weight=0;
		String f_hostname="";
		for(Element form:forms){
			String action=form.attr("action");
			if((action.startsWith("http"))||(action.startsWith("https"))){
				f_hostname=getDomainName(action);
				f_c=cosineSimilarityTest(domain,f_hostname);
				if(f_c>=0.88){
					f_weight++;
				}
				else{
					f_weight--;
				}
			}else if(action.startsWith("/")){
				f_weight++;
			}else if(action.startsWith("#")){
				continue;
			}else{
				f_weight--;
			}
			
		}
		title1=Integer.toString(f_weight);
		if(f_weight>=0){
			title1="safe site";
			GLOBAL_SECURE_WEIGHT++;
		}else if(f_weight<0){
			title1="phishing site";
			GLOBAL_SECURE_WEIGHT--;
		}
		
		Elements forms_login=doc.select("form");
		int l_weight=0;
		for(Element form_l:forms_login){
			if(form_l.attr("id").contains("login")||form_l.attr("name").contains("login")){
				String action_l=form_l.attr("action");
				if((action_l.startsWith("https"))){
					l_weight++;
				}
				else{
					l_weight--;
				}
			}
			else{
				l_weight--;
			}
		}
		title1=Integer.toString(l_weight);
		if(l_weight>=0){
			GLOBAL_SECURE_WEIGHT++;
		}else if(l_weight<0){
			GLOBAL_SECURE_WEIGHT--;
		}
		
		String score=Integer.toString(GLOBAL_SECURE_WEIGHT);
		Bundle b=new Bundle();
		b.putString("sco",score);
		b.putString("url", url);
		
		if(GLOBAL_SECURE_WEIGHT>=2){
			Intent result1=new Intent(this,Result1.class);
			result1.putExtras(b);
			startActivity(result1);
		}
		else if((GLOBAL_SECURE_WEIGHT<2)&&(GLOBAL_SECURE_WEIGHT>=0)){
			Intent result2=new Intent(this,Result2.class);
			result2.putExtras(b);
			startActivity(result2);
		}
		else if(GLOBAL_SECURE_WEIGHT<0){
			Intent result3=new Intent(this,Result3.class);
			result3.putExtras(b);
			startActivity(result3);
		}
		
		}catch(Exception e){
			Toast toast=Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 15, 100);
			toast.show();
			
		}
		
	}
	
}
