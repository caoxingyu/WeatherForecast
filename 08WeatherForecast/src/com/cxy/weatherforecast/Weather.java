package com.cxy.weatherforecast;

public class Weather {

	public static Weatherday[] day=new Weatherday[4];
	static{
		for(int i=0;i<day.length;i++){
			day[i]=new Weatherday();
		}
	}
	public static String[] Index=new String[6];
	public static String currentTemperature="";

}
