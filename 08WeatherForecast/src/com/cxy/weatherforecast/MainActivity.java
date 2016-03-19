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
		
		
		mLocationClient = new LocationClient(getApplicationContext());     //����LocationClient��
	    mLocationClient.registerLocationListener( myListener );
	    initLocation();
	}
	
	/**
	 * ���ö�λ������������λģʽ���߾��ȶ�λģʽ���͹��Ķ�λģʽ�ͽ����豸��λģʽ����
	 * �����������ͣ��Ƿ��GPS���Ƿ񷵻ص�ַ��Ϣ��λ�����廯��Ϣ��POI��Ϣ�ȵȡ�
	 */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
		option.setCoorType("bd09ll");// ��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
		int span = 1000;
		option.setScanSpan(span);// ��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
		option.setIsNeedAddress(true);// ��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
		option.setOpenGps(true);// ��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
		option.setLocationNotify(true);// ��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
		option.setIsNeedLocationDescribe(true);//�������� ��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
		option.setIsNeedLocationPoiList(true);// ��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
		option.setIgnoreKillProcess(false);// ��ѡ��Ĭ��false����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ��ɱ��
		option.SetIgnoreCacheException(false);// ��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
		option.setEnableSimulateGps(false);// ��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
		mLocationClient.setLocOption(option);
	}
	
	/**
	 * ʵ��BDLocationListener�ӿ�
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
//            sb.append(location.getLocType());   //location.getLocType()��ȡ��λ����
//            sb.append("\nlatitude : ");         //γ��
//            sb.append(location.getLatitude());  
//            sb.append("\nlontitude : ");        //����
//            sb.append(location.getLongitude());
//            sb.append("\nradius : ");          //����
//            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS��λ���
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// ��λ������ÿСʱ
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// ��λ����
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// ��λ����
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());   //��ϸ��Ϣ
                sb.append("\ndescribe : ");
                info.append("Ŀǰ��λ�ã�");
                info.append(location.getAddrStr());
                sb.append("gps��λ�ɹ�");
                cityName=location.getCity();
 
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// ���綨λ���
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                info.append("Ŀǰ��λ�ã�");
                info.append(location.getAddrStr());
                //��Ӫ����Ϣ
                sb.append("\noperationers : ");
                sb.append(location.getOperators());   //��Ӫ������
                sb.append("\ndescribe : ");
                sb.append("���綨λ�ɹ�");
                cityName=location.getCity();
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// ���߶�λ���
                sb.append("\ndescribe : ");
                sb.append("���߶�λ�ɹ������߶�λ���Ҳ����Ч��");
                cityName=location.getCity();
                info.append("Ŀǰ��λ�ã�");
                info.append(location.getAddrStr());
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("��������綨λʧ�ܣ����Է���IMEI�źʹ��嶨λʱ�䵽loc-bugs@baidu.com��������׷��ԭ��");
                info.append("\ndescribe : ");
                info.append("��������綨λʧ�ܣ����Է���IMEI�źʹ��嶨λʱ�䵽loc-bugs@baidu.com��������׷��ԭ��");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("���粻ͬ���¶�λʧ�ܣ����������Ƿ�ͨ��");
                info.append("\ndescribe : ");
                info.append("���粻ͬ���¶�λʧ�ܣ����������Ƿ�ͨ��");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("�޷���ȡ��Ч��λ���ݵ��¶�λʧ�ܣ�һ���������ֻ���ԭ�򣬴��ڷ���ģʽ��һ���������ֽ�����������������ֻ�");
                info.append("\ndescribe : ");
                info.append("�޷���ȡ��Ч��λ���ݵ��¶�λʧ�ܣ�һ���������ֻ���ԭ�򣬴��ڷ���ģʽ��һ���������ֽ�����������������ֻ�");
            }
                sb.append("\nlocationdescribe : ");
                sb.append(location.getLocationDescribe());         // λ�����廯��Ϣ
                info.append("\n��ϸλ�� : ");
                info.append(location.getLocationDescribe());  
//                List<Poi> list = location.getPoiList();// POI����
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
            Log.i("TIMER", "������"+cityName);
            if (cityName!=null&&cityName.length()>0) {
            	//Log.i("TIMER", "��������Ϊ��"+cityName);
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
            Log.i("TIMER", "������һ�ζ�λ");
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
		
		
		//������������
		ImageView dayImage=(ImageView) findViewById(R.id.day_image);
		ImageView nightImage=(ImageView) findViewById(R.id.night_image);
		TextView weather=(TextView) findViewById(R.id.weather);
		TextView temperature=(TextView) findViewById(R.id.temperature);
		currentTem.setText(Weather.currentTemperature);        //ʵʱ�¶�
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
		
		//δ����һ��
		TextView d1_date=(TextView) findViewById(R.id.d1_date);
		TextView d1_temperature=(TextView) findViewById(R.id.d1_temperature);
		TextView d1_weather=(TextView) findViewById(R.id.d1_weather);
		ImageView d1_image=(ImageView) findViewById(R.id.d1_image);
		
		d1_date.setText(Weather.day[1].date);
		d1_temperature.setText(Weather.day[1].temperature);
		d1_weather.setText(Weather.day[1].weather);
		d1_image.setImageBitmap(Weather.day[1].dayImage);
		
		// δ���ڶ���
		TextView d2_date = (TextView) findViewById(R.id.d2_date);
		TextView d2_temperature = (TextView) findViewById(R.id.d2_temperature);
		TextView d2_weather = (TextView) findViewById(R.id.d2_weather);
		ImageView d2_image = (ImageView) findViewById(R.id.d2_image);

		d2_date.setText(Weather.day[2].date);
		d2_temperature.setText(Weather.day[2].temperature);
		d2_weather.setText(Weather.day[2].weather);
		d2_image.setImageBitmap(Weather.day[2].dayImage);
		
		// δ��������
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
