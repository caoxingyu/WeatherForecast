package com.cxy.weatherforecast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class WeatherAdapter {

	public static void GetWeater() {

		try {
			String xml = GetXmlCode(URLEncoder.encode(Config.CityName, "utf-8")); // 设置输入城市的编码，以满足百度天气api需要，返回获取的xml字符串
			Log.i("TIMER", Config.CityName);
			readStringXml(xml);// 调用xml解析函数
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param city
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String GetXmlCode(String city) throws UnsupportedEncodingException {
		//Log.i("TIMER", city);
		String requestUrl = "http://api.map.baidu.com/telematics/v3/weather?location="+ city
				+ "&output=xml&ak=85NDUQblmlgZjX6BNu8It7Ta&mcode=ED:3E:91:70:CE:4A:3D:4F:8F:9A:64:15:A2:1C:3D:5E:D3:E5:77:8E;com.cxy.weatherforecast";
		StringBuffer buffer = null;
		try {
			// 建立连接
			URL url = new URL(requestUrl);
			HttpURLConnection httpUrlConn = (HttpURLConnection) url
					.openConnection();
			httpUrlConn.setDoInput(true);
			httpUrlConn.setRequestMethod("GET");
			// 获取输入流
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			// 读取返回结果
			buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}

			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			httpUrlConn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString(); // 返回获取的xml字符串
	}

	/**
	 * 
	 * @param xml
	 * @param ifcity
	 * @return
	 */
	public static void readStringXml(String xml) {
		StringBuffer buff = new StringBuffer(); // 用来拼接天气信息的
		Document doc = null;
		List listdate = null; // 用来存放日期
		List listday = null; // 用来存放白天图片路径信息
		List listnight = null; // 用来存放晚上图片路径信息
		List listweather = null;
		List listwind = null;
		List listtem = null;
		List listIndex=null;
		try {
			// 读取并解析XML文档
			// 下面的是通过解析xml字符串的
			doc = DocumentHelper.parseText(xml);// 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			Iterator iter = rootElt.elementIterator("results"); // 获取根节点下的子节点results
			String status = rootElt.elementText("status"); // 获取状态，如果等于success,表示有数据
			if (!status.equals("success"))
				return; // 如果不存在数据，直接返回
			String date = rootElt.elementText("date"); // 获取根节点下的，当天日期
			buff.append(date + "\n");
			// 遍历results节点
			while (iter.hasNext()) {
				Element recordEle = (Element) iter.next();
				Iterator iters = recordEle.elementIterator("weather_data"); //获取weather_data节点
				Iterator indexs=recordEle.elementIterator("index");          //获取index节点
				// 遍历results节点下的weather_data节点
				while (iters.hasNext()) {
					Element itemEle = (Element) iters.next();
					listdate = itemEle.elements("date"); // 将date集合放到listdate中
					listday = itemEle.elements("dayPictureUrl");
					listnight = itemEle.elements("nightPictureUrl");
					listweather = itemEle.elements("weather");
					listwind = itemEle.elements("wind");
					listtem = itemEle.elements("temperature");
				}
				//// 遍历results节点下的index节点
				while(indexs.hasNext()){
					Element itemEle=(Element) indexs.next();
					listIndex=itemEle.elements("zs");
				}
				
				for (int i = 0; i <listIndex.size(); i++) {
					Weather.Index[i]=((Element)listIndex.get(i)).getText();    //添加生活指数信息
					
				}
				for (int i = 0; i < listdate.size(); i++) { // 由于每一个list.size都相等，这里统一处理
					Element eledate = (Element) listdate.get(i); // 依次取出date
					Element eleday = (Element) listday.get(i);// ..
					Element elenight = (Element) listnight.get(i);
					Element eleweather = (Element) listweather.get(i);
					Element elewind = (Element) listwind.get(i);
					Element eletem = (Element) listtem.get(i);
					buff.append(eledate.getText() + "==="
							+ eleweather.getText() + "===" + elewind.getText()
							+ "===" + eletem.getText() + "\n"); // 拼接信息
				//	System.out.println(buff);

					// 对获取到的天气信息进行处理
					Weather.day[i].date = eledate.getText();
					Weather.day[i].temperature = eletem.getText();
					Weather.day[i].weather = eleweather.getText();
					Weather.day[i].wind = elewind.getText();
					Weather.day[i].dayImage = GetURLBitmap(eleday.getText());
					Weather.day[i].nightImage = GetURLBitmap(elenight.getText());
				}
				String str=Weather.day[0].date;
				String newStr=str.substring(str.indexOf("(")+1,str.lastIndexOf(")"));
				Weather.currentTemperature=newStr.split("：")[1]; 
				Log.i("TIMER", Weather.currentTemperature);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取图片
	 * 
	 * @param urlString
	 * @return
	 */
	private static Bitmap GetURLBitmap(String urlString) {
		URL url = null;
		Bitmap bitmap = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;

	}

}
