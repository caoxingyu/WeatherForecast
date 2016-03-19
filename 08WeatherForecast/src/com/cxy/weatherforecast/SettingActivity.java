package com.cxy.weatherforecast;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener{
	EditText etCity,etReFresh;
	Button saveSetting_btn,backButton;
	
	public static final String PREFERENCE_NAME="SaveSetting";
	@SuppressWarnings("deprecation")
	public static int MODE=Context.MODE_WORLD_READABLE+Context.MODE_WORLD_WRITEABLE;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settting);
		etCity=(EditText) findViewById(R.id.city);
		etReFresh=(EditText) findViewById(R.id.refresh_rate);
		saveSetting_btn=(Button) findViewById(R.id.save_settings);
		backButton=(Button) findViewById(R.id.backBtn);
		
		saveSetting_btn.setOnClickListener(this);
		backButton.setOnClickListener(this);
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		loadSharedPreferences();
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save_settings:           //保存设置
			saveSharedPreferences();
			break;

		case R.id.backBtn:
			finish();
			break;
		}
		
	}
	
	private void saveSharedPreferences(){
		Config.CityName = etCity.getText().toString().trim();
		Config.RefreshSpeed = etReFresh.getText().toString();
		SharedPreferences sharedPreferences=getSharedPreferences(PREFERENCE_NAME, MODE);
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putString("CityNmae", etCity.getText().toString().trim());
		editor.putInt("RefreshSpeed", Integer.parseInt(etReFresh.getText().toString()));
		editor.commit();
		WeatherService.timeCounter=1;
		 Toast.makeText(this, "保存设置成功，请稍后刷新数据！", Toast.LENGTH_SHORT).show();
	}
	
      private  void loadSharedPreferences(){
    	  SharedPreferences sharedPreferences=getSharedPreferences(PREFERENCE_NAME, MODE);
    	  String cityName=sharedPreferences.getString("CityNmae", "镇江");
    	  int refreshSpeed=sharedPreferences.getInt("RefreshSpeed", 60);
    	  
    	  etCity.setText(cityName);
    	  etReFresh.setText(String.valueOf(refreshSpeed));
		
	}

}
