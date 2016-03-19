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
			String xml = GetXmlCode(URLEncoder.encode(Config.CityName, "utf-8")); // ����������еı��룬������ٶ�����api��Ҫ�����ػ�ȡ��xml�ַ���
			Log.i("TIMER", Config.CityName);
			readStringXml(xml);// ����xml��������
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
			// ��������
			URL url = new URL(requestUrl);
			HttpURLConnection httpUrlConn = (HttpURLConnection) url
					.openConnection();
			httpUrlConn.setDoInput(true);
			httpUrlConn.setRequestMethod("GET");
			// ��ȡ������
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			// ��ȡ���ؽ��
			buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}

			// �ͷ���Դ
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			httpUrlConn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString(); // ���ػ�ȡ��xml�ַ���
	}

	/**
	 * 
	 * @param xml
	 * @param ifcity
	 * @return
	 */
	public static void readStringXml(String xml) {
		StringBuffer buff = new StringBuffer(); // ����ƴ��������Ϣ��
		Document doc = null;
		List listdate = null; // �����������
		List listday = null; // ������Ű���ͼƬ·����Ϣ
		List listnight = null; // �����������ͼƬ·����Ϣ
		List listweather = null;
		List listwind = null;
		List listtem = null;
		List listIndex=null;
		try {
			// ��ȡ������XML�ĵ�
			// �������ͨ������xml�ַ�����
			doc = DocumentHelper.parseText(xml);// ���ַ���תΪXML
			Element rootElt = doc.getRootElement(); // ��ȡ���ڵ�
			Iterator iter = rootElt.elementIterator("results"); // ��ȡ���ڵ��µ��ӽڵ�results
			String status = rootElt.elementText("status"); // ��ȡ״̬���������success,��ʾ������
			if (!status.equals("success"))
				return; // ������������ݣ�ֱ�ӷ���
			String date = rootElt.elementText("date"); // ��ȡ���ڵ��µģ���������
			buff.append(date + "\n");
			// ����results�ڵ�
			while (iter.hasNext()) {
				Element recordEle = (Element) iter.next();
				Iterator iters = recordEle.elementIterator("weather_data"); //��ȡweather_data�ڵ�
				Iterator indexs=recordEle.elementIterator("index");          //��ȡindex�ڵ�
				// ����results�ڵ��µ�weather_data�ڵ�
				while (iters.hasNext()) {
					Element itemEle = (Element) iters.next();
					listdate = itemEle.elements("date"); // ��date���Ϸŵ�listdate��
					listday = itemEle.elements("dayPictureUrl");
					listnight = itemEle.elements("nightPictureUrl");
					listweather = itemEle.elements("weather");
					listwind = itemEle.elements("wind");
					listtem = itemEle.elements("temperature");
				}
				//// ����results�ڵ��µ�index�ڵ�
				while(indexs.hasNext()){
					Element itemEle=(Element) indexs.next();
					listIndex=itemEle.elements("zs");
				}
				
				for (int i = 0; i <listIndex.size(); i++) {
					Weather.Index[i]=((Element)listIndex.get(i)).getText();    //�������ָ����Ϣ
					
				}
				for (int i = 0; i < listdate.size(); i++) { // ����ÿһ��list.size����ȣ�����ͳһ����
					Element eledate = (Element) listdate.get(i); // ����ȡ��date
					Element eleday = (Element) listday.get(i);// ..
					Element elenight = (Element) listnight.get(i);
					Element eleweather = (Element) listweather.get(i);
					Element elewind = (Element) listwind.get(i);
					Element eletem = (Element) listtem.get(i);
					buff.append(eledate.getText() + "==="
							+ eleweather.getText() + "===" + elewind.getText()
							+ "===" + eletem.getText() + "\n"); // ƴ����Ϣ
				//	System.out.println(buff);

					// �Ի�ȡ����������Ϣ���д���
					Weather.day[i].date = eledate.getText();
					Weather.day[i].temperature = eletem.getText();
					Weather.day[i].weather = eleweather.getText();
					Weather.day[i].wind = elewind.getText();
					Weather.day[i].dayImage = GetURLBitmap(eleday.getText());
					Weather.day[i].nightImage = GetURLBitmap(elenight.getText());
				}
				String str=Weather.day[0].date;
				String newStr=str.substring(str.indexOf("(")+1,str.lastIndexOf(")"));
				Weather.currentTemperature=newStr.split("��")[1]; 
				Log.i("TIMER", Weather.currentTemperature);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ��ȡͼƬ
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
