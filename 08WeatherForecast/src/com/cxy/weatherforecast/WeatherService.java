package com.cxy.weatherforecast;




import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WeatherService extends Service{
	private Thread workThread;
	protected static int timeCounter = 1;
	
//    private final IBinder wBinder=new LocalBinder();
//	
//	public class  LocalBinder extends Binder{
//		WeatherService getService(){
//			return WeatherService.this;
//		}
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		  Toast.makeText(this, "天气服务启动", Toast.LENGTH_LONG).show(); 
//		return wBinder;
//	}
//	
//	@Override
//	public boolean onUnbind(Intent intent) {
//		  Toast.makeText(this, "天气服务停止", Toast.LENGTH_LONG).show(); 
//		return false;
//	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	
		  Toast.makeText(this, "天气服务启动", Toast.LENGTH_SHORT).show();    
		    workThread = new Thread(null,backgroudWork,"WorkThread");
	
	}
	
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
		   if (!workThread.isAlive()){
		    	  workThread.start();
		      }
	}
	

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		 timeCounter=1;
		 Toast.makeText(this, "天气服务停止", Toast.LENGTH_SHORT).show();     
	     workThread.interrupt();
	}
	
	private Runnable backgroudWork = new Runnable(){
		@Override
		public void run() {
			try {
		
				while(!Thread.interrupted()){				

					

					GetBaiduWeatherData();
					
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};
	
	private void GetBaiduWeatherData(){
		//Log.i("TIMER",String.valueOf(timeCounter));
		
		if (timeCounter-- < 0){
			timeCounter = Integer.parseInt(Config.RefreshSpeed);
			Log.i("TIMER","开始获取数据");
			try {
				WeatherAdapter.GetWeater();
				//Log.i("TIMER","获取数据进行中。。。。。");
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	

}
