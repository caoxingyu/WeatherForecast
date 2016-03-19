package com.cxy.weatherforecast;



import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.baidu.location.LocationClientOption.LocationMode;



public class MainActivity extends Activity {
	private  Intent serviceIntent=null;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	public   String cityName=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    mLocationClient.registerLocationListener( myListener );
	    initLocation();
	}
	
	/**
	 * 设置定位参数包括：定位模式（高精度定位模式，低功耗定位模式和仅用设备定位模式），
	 * 返回坐标类型，是否打开GPS，是否返回地址信息、位置语义化信息、POI信息等等。
	 */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);//———— 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}
	
	/**
	 * 实现BDLocationListener接口
	 */
	public class MyLocationListener implements BDLocationListener {
		 
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            StringBuffer info=new StringBuffer(100);
            sb.append("time : ");
            sb.append(location.getTime());
//            sb.append("\nerror code : ");
//            sb.append(location.getLocType());   //location.getLocType()获取定位类型
//            sb.append("\nlatitude : ");         //纬度
//            sb.append(location.getLatitude());  
//            sb.append("\nlontitude : ");        //经度
//            sb.append(location.getLongitude());
//            sb.append("\nradius : ");          //精度
//            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位：度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());   //详细信息
                sb.append("\ndescribe : ");
                info.append("目前的位置：");
                info.append(location.getAddrStr());
                sb.append("gps定位成功");
                cityName=location.getCity();
 
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                info.append("目前的位置：");
                info.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());   //运营商名称
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                cityName=location.getCity();
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
                cityName=location.getCity();
                info.append("目前的位置：");
                info.append(location.getAddrStr());
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                info.append("\ndescribe : ");
                info.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
                info.append("\ndescribe : ");
                info.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                info.append("\ndescribe : ");
                info.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
                sb.append("\nlocationdescribe : ");
                sb.append(location.getLocationDescribe());         // 位置语义化信息
                info.append("\n详细位置 : ");
                info.append(location.getLocationDescribe());  
//                List<Poi> list = location.getPoiList();// POI数据
//                if (list != null) {
//                    sb.append("\npoilist size = : ");
//                    sb.append(list.size());
//                    for (Poi p : list) {
//                        sb.append("\npoi= : ");
//                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
//                    }
//                }
            Log.i("TIMER", sb.toString());
            Toast.makeText(MainActivity.this, info.toString(),Toast.LENGTH_LONG ).show();
            Log.i("TIMER", "城市名"+cityName);
            if (cityName!=null&&cityName.length()>0) {
            	//Log.i("TIMER", "城市名不为空"+cityName);
            	Config.CityName=cityName;
			}
            mLocationClient.stop();
            serviceIntent = new Intent(MainActivity.this,WeatherService.class);
            startService(serviceIntent);
        }
        
      
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent=new Intent(MainActivity.this,SettingActivity.class);
			startActivity(intent);
			return true;
		

		case R.id.start_service:

			mLocationClient.start();
            Log.i("TIMER", "调用了一次定位");
			return true;

		case R.id.stop_service:
			
			stopService(serviceIntent);
			// mLocationClient.stop();
			return true;

        case R.id.refresh:
        	refreshWeatherData();
	    	return true;
        case R.id.exit:
            finish();
            return true;
	    
		}
		
		
	   
		return false;
	}
	
	private void refreshWeatherData(){
		TextView city=(TextView) findViewById(R.id.cityName);
		city.setText(Config.CityName);
		TextView currentTem=(TextView) findViewById(R.id.currentTem);
		
		
		//当天的天气情况
		ImageView dayImage=(ImageView) findViewById(R.id.day_image);
		ImageView nightImage=(ImageView) findViewById(R.id.night_image);
		TextView weather=(TextView) findViewById(R.id.weather);
		TextView temperature=(TextView) findViewById(R.id.temperature);
		currentTem.setText(Weather.currentTemperature);        //实时温度
		TextView wind=(TextView) findViewById(R.id.wind);
		TextView clothIndex=(TextView) findViewById(R.id.dressIndex);
		TextView travelIndex=(TextView) findViewById(R.id.travelIndex);
		TextView coldIndex=(TextView) findViewById(R.id.coldIndex);
		TextView sportsIndex=(TextView) findViewById(R.id.sportsIndex);
		TextView rayIndex=(TextView) findViewById(R.id.rayIndex);
		
		
		dayImage.setImageBitmap(Weather.day[0].dayImage);
		nightImage.setImageBitmap(Weather.day[0].nightImage);
		weather.setText(Weather.day[0].weather);
		temperature.setText(Weather.day[0].temperature);
		wind.setText(Weather.day[0].wind);
		 clothIndex.setText(Weather.Index[0]);
		 travelIndex.setText(Weather.Index[2]);
		 coldIndex.setText(Weather.Index[3]);
		 sportsIndex.setText(Weather.Index[4]);
		 rayIndex.setText(Weather.Index[5]);
		
		//未来第一天
		TextView d1_date=(TextView) findViewById(R.id.d1_date);
		TextView d1_temperature=(TextView) findViewById(R.id.d1_temperature);
		TextView d1_weather=(TextView) findViewById(R.id.d1_weather);
		ImageView d1_image=(ImageView) findViewById(R.id.d1_image);
		
		d1_date.setText(Weather.day[1].date);
		d1_temperature.setText(Weather.day[1].temperature);
		d1_weather.setText(Weather.day[1].weather);
		d1_image.setImageBitmap(Weather.day[1].dayImage);
		
		// 未来第二天
		TextView d2_date = (TextView) findViewById(R.id.d2_date);
		TextView d2_temperature = (TextView) findViewById(R.id.d2_temperature);
		TextView d2_weather = (TextView) findViewById(R.id.d2_weather);
		ImageView d2_image = (ImageView) findViewById(R.id.d2_image);

		d2_date.setText(Weather.day[2].date);
		d2_temperature.setText(Weather.day[2].temperature);
		d2_weather.setText(Weather.day[2].weather);
		d2_image.setImageBitmap(Weather.day[2].dayImage);
		
		// 未来第三天
		TextView d3_date = (TextView) findViewById(R.id.d3_date);
		TextView d3_temperature = (TextView) findViewById(R.id.d3_temperature);
		TextView d3_weather = (TextView) findViewById(R.id.d3_weather);
		ImageView d3_image = (ImageView) findViewById(R.id.d3_image);

		d3_date.setText(Weather.day[3].date);
		d3_temperature.setText(Weather.day[3].temperature);
		d3_weather.setText(Weather.day[3].weather);
		d3_image.setImageBitmap(Weather.day[3].dayImage);
	}

}
