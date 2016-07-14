package com.svail.geotext;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.svail.util.FileTool;
import com.svail.util.HTMLTool;
public class TextPoi {
	
	public String name = null;
	public String address = null;
	public double lng = Double.NaN;
	public double lat = Double.NaN;
	public int coorType = 0;
	public String gbcode = null;
	public String minbox = null;
	public String offset = null;
	public String postcode = null;
	public String telephone = null;
	public String description = null;
	public String time = null;
	public String url = null;
	public String price = null;
	
	public String GENERAL_SITUATION = null;
	public String FLOOR = null;
	public String DISTRICT = null;
	public String CHECKIN = null;
	
	public int index = -1;
	public TextPoi()
	{
		
	}
	public TextPoi(TextPoi poi)
	{
		if (poi.name != null)
			name = new String(poi.name);
		if (poi.address != null)
			address = new String(poi.address);
		lng = poi.lng;
		lat = poi.lat;
		coorType = poi.coorType;
		if (poi.gbcode != null)
			gbcode = new String(poi.gbcode);

		if (poi.minbox != null)
			minbox = new String(poi.minbox);
		
		if (poi.offset != null)
			offset = new String(poi.offset);
		
		if (poi.postcode != null)
			postcode = new String(poi.postcode);
		
		if (poi.telephone != null)
			telephone = new String(poi.telephone);

		if (poi.url != null)
			url = new String(poi.url);
		
		if (poi.time != null)
			time = new String(poi.time);
		
		if (poi.description != null)
			description = new String (poi.description);
			
		if (poi.price != null)
			price = new String (poi.price);
		
		if (poi.GENERAL_SITUATION != null)
			GENERAL_SITUATION = new String (poi.GENERAL_SITUATION);
		
		if (poi.FLOOR != null)
			FLOOR = new String (poi.FLOOR);
		
		if (poi.DISTRICT != null)
			DISTRICT = new String (poi.DISTRICT);
		
		if (poi.CHECKIN != null)
			DISTRICT = new String (poi.CHECKIN);
		
		index = poi.index;		
	}
	
	public TextPoi(String buf)
    {
    	int s, e;
    	s = buf.indexOf("<NAME>");
    	e = buf.indexOf("</NAME>");
    	
    	if (s == -1 || e == -1)
    		return;
    	
    	name = buf.substring(s + "<NAME>".length(), e).replace("街道办事处", "街道");

    	s = buf.indexOf("<ADDRESS>");
    	e = buf.indexOf("</ADDRESS>");
    	
    	if (s != -1 && e != -1)   	
    		address = buf.substring(s + "<ADDRESS>".length(), e);

    	s = buf.indexOf("<GBCODE>");
    	e = buf.indexOf("</GBCODE>");
    	
    	if (s != -1 && e != -1)   	
    		gbcode = buf.substring(s + "<GBCODE>".length(), e);

    	s = buf.indexOf("<MINBOX>");
    	e = buf.indexOf("</MINBOX>");
    	
    	if (s != -1 && e != -1)   	
    		minbox = buf.substring(s + "<MINBOX>".length(), e);

    	
    	s = buf.indexOf("<OFFSET>");
    	e = buf.indexOf("</OFFSET>");
    	
    	if (s != -1 && e != -1)   	
    		offset = buf.substring(s + "<OFFSET>".length(), e);

    	
    	s = buf.indexOf("<POSTCODE>");
    	e = buf.indexOf("</POSTCODE>");
    	
    	if (s != -1 && e != -1)   	
    		postcode = buf.substring(s + "<POSTCODE>".length(), e);

    	
    	s = buf.indexOf("<TELEPHONE>");
    	e = buf.indexOf("</TELEPHONE>");
    	
    	if (s != -1 && e != -1)   	
    		telephone = buf.substring(s + "<TELEPHONE>".length(), e);
    	
    	s = buf.indexOf("<URL>");
    	e = buf.indexOf("</URL>");
    	
    	if (s != -1 && e != -1)   	
    		url = buf.substring(s + "<URL>".length(), e);
    	
    	s = buf.indexOf("<DESCRIPTION>");
    	e = buf.indexOf("</DESCRIPTION>");
    	
    	if (s != -1 && e != -1)   	
    		description = buf.substring(s + "<DESCRIPTION>".length(), e);
    	
    	s = buf.indexOf("<TIME>");
    	e = buf.indexOf("</TIME>");
    	
    	if (s != -1 && e != -1)   	
    		time = buf.substring(s + "<TIME>".length(), e);
    	
    	s = buf.indexOf("<PRICE>");
    	e = buf.indexOf("</PRICE>");
    	
    	if (s != -1 && e != -1)   	
    		price = buf.substring(s + "<PRICE>".length(), e);
    	
    	s = buf.indexOf("<GENERAL_SITUATION>");
    	e = buf.indexOf("</GENERAL_SITUATION>");
    	
    	if (s != -1 && e != -1)   	
    		GENERAL_SITUATION = buf.substring(s + "<GENERAL_SITUATION>".length(), e);
    	
		s = buf.indexOf("<FLOOR>");
    	e = buf.indexOf("</FLOOR>");
    	
    	if (s != -1 && e != -1)   	
    		FLOOR = buf.substring(s + "<FLOOR>".length(), e);
    	
    	s = buf.indexOf("<DISTRICT>");
    	e = buf.indexOf("</DISTRICT>");
    	
    	if (s != -1 && e != -1)   	
    		DISTRICT = buf.substring(s + "<DISTRICT>".length(), e);
		
		s = buf.indexOf("<CHECKIN>");
    	e = buf.indexOf("</CHECKIN>");
    	
    	if (s != -1 && e != -1)   	
    		CHECKIN = buf.substring(s + "<CHECKIN>".length(), e);
    			
    	s = buf.indexOf("<LNGLAT>");
    	e = buf.indexOf("</LNGLAT>");
    	
    	if (s != -1 && e != -1)   	
    	{
    		String str = buf.substring(s + "<LNGLAT>".length(), e);	
    		String tokens[] = str.split(";");
    		
    		if (tokens != null && tokens.length > 1)
    		{
    			if (!tokens[0].equals("NaN"))
    			{
    				try{
    					lng = Double.parseDouble(tokens[0]);
    					lat = Double.parseDouble(tokens[1]);
    					if (tokens.length == 3)
    						coorType = Integer.parseInt(tokens[2]);
    				}
    				catch( NumberFormatException ne)
    				{
    					System.out.println(ne.getMessage());
    				}
    			}
    		}
    	}    	
    }
	
	public String toString()
    {
    	String cont = new String("");
    	if (name == null)
    	{
    		return null;
    	}
    	cont = "<POI><NAME>" + this.name + "</NAME>";
    	
    	if (address != null)
    	{
    		cont += "<ADDRESS>" + this.address + "</ADDRESS>";    		
    		
    	}
    	
    	if (gbcode != null)
    	{
    		cont += "<GBCODE>" + this.gbcode + "</GBCODE>";    		
    		
    	}
    	
    	if (lng != Double.NaN && lat != Double.NaN)
    	{
    		if (coorType == 0)
    			cont += "<LNGLAT>" + lng + ";" + lat + "</LNGLAT>";
    		else
    			cont += "<LNGLAT>" + lng + ";" + lat + ";" + coorType + "</LNGLAT>";
    	}
    	
    	if (minbox != null)
    	{
    		cont += "<MINBOX>" + minbox + "</MINBOX>";
    		
    	}

    	if (offset != null)
    	{
    		cont += "<OFFSET>" + offset + "</OFFSET>";
    		
    	}

    	if (postcode != null)
    	{
    		cont += "<POSTCODE>" + postcode + "</POSTCODE>";
    		
    	}

    	if (telephone != null)
    	{
    		cont += "<TELEPHONE>" + telephone + "</TELEPHONE>";
    		
    	}
    	if (description != null)
    	{
    		cont += "<DESCRIPTION>" + description + "</DESCRIPTION>";
    		
    	}
    	if (time != null)
    	{
    		cont += "<TIME>" + time + "</TIME>";
    		
    	}
    	
    	if (url != null)
    	{
    		cont += "<URL>" + url + "</URL>";
    		
    	}
    	
    	if (price != null)
		{
    		cont += "<PRICE>" + price + "</PRICE>";
		}

		
    	if (GENERAL_SITUATION != null)
		{
    		cont += "<GENERAL_SITUATION>" + GENERAL_SITUATION + "</GENERAL_SITUATION>";
		}

		if (FLOOR != null)
		{
    		cont += "<FLOOR>" + FLOOR + "</FLOOR>";
		}
		
		if (DISTRICT != null)
		{
    		cont += "<DISTRICT>" + DISTRICT + "</DISTRICT>";
		}
		
		if (CHECKIN != null)
		{
    		cont += "<CHECKIN>" + CHECKIN + "</CHECKIN>";
		}
		
    	cont += "</POI>";
    	
    	return cont;
    	
    }
	
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		TextPoi other = (TextPoi) obj;
		if (!name.equalsIgnoreCase(other.name))
			return false;
		if (lat != other.lat)
			return false;
		if (lng != other.lng)
			return false;
		
		if (gbcode == null && other.gbcode == null)
			return true;
		else if (gbcode != null && other.gbcode != null)
		{
			if (!gbcode.equalsIgnoreCase(other.gbcode))
				return false;
		}
		return true;
	}

	public int compareTo(Object arg0) {
		TextPoi node =(TextPoi)arg0;
        int result = name.compareTo( node.name);
        
        if (result == 0)
        {
        	if (lng > node.lng)
        		return 1;
        	
        	else if (lng < node.lng)
        		return -1;
        	else
        	{
        		if (lat > node.lat)
        			return 1;
        		else if (lat < node.lat)
        			return -1;
        		else
        		{
        			return result = gbcode.compareTo(node.gbcode);
        			
        		}
        	}
        }
         
        return result;
	}
	
	public static Vector<TextPoi> Load(String poiName)
	{
		Vector<String> rds = FileTool.Load(poiName, "UTF-8");
		
		if (rds != null)
		{
			Vector<TextPoi> pois = new Vector<TextPoi>();
			for (int n = 0; n < rds.size(); n ++)
			{
				TextPoi poi = new TextPoi(rds.elementAt(n));
				pois.add(poi);
			}
			return pois;
		}
		else
			return null;
	}
	
	public static void Dump(String poisName, String csvName)
	{
		Vector<TextPoi> pois = Load(poisName);
		if (pois != null)
		{
			String fields = "NAME,ADDRESS,URL,LNG,LAT";
			FileTool.Dump(fields, csvName, "gb18030");
			
			for (int n = 0; n < pois.size(); n ++)
			{
				fields = "";
				if (pois.get(n).name != null)
					fields = pois.get(n).name;
				if (pois.get(n).address != null)
					fields += "," + pois.get(n).address;
				else
					fields += ",";
				
				if (pois.get(n).url != null)
					fields += "," + pois.get(n).url;
				else
					fields += ",";
				
				if (pois.get(n).lng != Double.NaN && pois.get(n).lat != Double.NaN)
				{
					fields += "," + pois.get(n).lng + "," + pois.get(n).lat;
					
					FileTool.Dump(fields, csvName, "gb18030");
				}
				
			}
		}
	}
	
	public static String parse(String query) throws UnsupportedEncodingException
	{
		String request = "http://geocode.svail.com:8080/p41?f=json";
		String parameters = "&within="
			+ java.net.URLEncoder.encode("北京市", "UTF-8")
			+ "&key=745381AAD4C711E48DAEB8CA3AF38724&queryStr=";

		Gson gson = new Gson();
		
		String uri = null;
		try {

			uri = request + parameters
				+ java.net.URLEncoder.encode(
					query, "UTF-8");
			
			String xml = HTMLTool.fetchURL(uri, "UTF-8", "post");
			if (xml != null)
			{
				// 创建一个JsonParser
				JsonParser parser = new JsonParser();
		
				//通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
				try {
					JsonElement el = parser.parse(xml);

					//把JsonElement对象转换成JsonObject
					JsonObject jsonObj = null;
					if(el.isJsonObject())
					{
						jsonObj = el.getAsJsonObject();
						GeoQuery gq = gson.fromJson(jsonObj, GeoQuery.class);
						String lnglat = "";
						if (gq != null && gq.getResult() != null && gq.getResult().size() > 0 && gq.getResult().get(0).getLocation() != null)
						{
							lnglat = gq.getResult().get(0).getLocation().getLng() + ";" + gq.getResult().get(0).getLocation().getLat();
							
							return lnglat;
						}
					}

					
					return null;
				}catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		
		return null;
	}
	public static String parseLngLat(String query) throws UnsupportedEncodingException
	{
		String request = "http://geocode.svail.com:8080/p41?f=json";;//"http://192.168.6.9:8080/p41?f=json"; //
		// String parameters = "&within="
		//	+ java.net.URLEncoder.encode("江苏", "UTF-8")
		//	+ "&key=327D6A095A8111E5BFE0B8CA3AF38727&queryStr=";
		String parameters = "&key=327D6A095A8111E5BFE0B8CA3AF38727&queryStr=";
		Gson gson = new Gson();
		
		String uri = null;
		try {

			uri = request + parameters
				+ java.net.URLEncoder.encode(
					query, "UTF-8");
			
			String xml = HTMLTool.fetchURL(uri, "UTF-8", "post");
			System.out.println(xml);
			FileTool.Dump(xml, "D:\\temp\\测试结果.txt", "UTF-8");
			if (xml != null)
			{
				// 创建一个JsonParser
				JsonParser parser = new JsonParser();
		
				//通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
				try {
					JsonElement el = parser.parse(xml);

					//把JsonElement对象转换成JsonObject
					JsonObject jsonObj = null;
					if(el.isJsonObject())
					{
						jsonObj = el.getAsJsonObject();
						GeoQuery gq = gson.fromJson(jsonObj, GeoQuery.class);
						String lnglat = "";
						if (gq != null && gq.getResult() != null && gq.getResult().size() > 0 && gq.getResult().get(0).getLocation() != null)
						{
							lnglat = gq.getResult().get(0).getLocation().getLng() + ";" + gq.getResult().get(0).getLocation().getLat();
							FileTool.Dump(gq.getResult().get(0).getQuery_string() + " @ " + gq.getResult().get(0).getLocation().getMatched() , "D:\\temp\\output.txt", "utf-8");
							return lnglat;
						}
					}

					
					return null;
				}catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		
		return null;
	}
	public static void processCSV(String file, int fieldIndex) throws UnsupportedEncodingException
	{
		Vector<String> pois = FileTool.Load(file, "utf-8");
		
		String srv = "http://geocode.svail.com";// "http://192.168.6.9"; // http://geocode.svail.com
		
		String request =  srv + ":8080/p41?f=json";//"http://192.168.6.9:8080/p41?f=json";//
		String parameters = "&f=json&within="
			 + java.net.URLEncoder.encode("北京", "UTF-8")
			 + "&key=327D6A095A8111E5BFE0B8CA3AF38727&queryStr=";
		// String parameters = "f=json&key=327D6A095A8111E5BFE0B8CA3AF38727&queryStr=";

		boolean batch = true;
		Gson gson = new Gson();
		if (batch)
			request = srv + ":8080/p4b?";//"http://192.168.6.9:8080/p4b?";// 
		
		StringBuffer sb = new StringBuffer();
		int offset = 1;
		FileTool.Dump(pois.elementAt(0) + ",lng, lat", file + ".result.txt", "UTF-8");
		
		for (int n = 1; n < pois.size(); n ++) {
			if (batch) {
				String rs = pois.get(n);
				String toks[] = rs.split(",");
				
				sb.append(toks[fieldIndex]).append("\n");
				// sb.append(rs).append("\n");
				if ((n + 1) % 2000 == 0 || n == pois.size() - 1) {
					String urlParameters = sb.toString();
					// System.out.print(urlParameters);
					
					byte[] postData;
					try {
						postData = (parameters + java.net.URLEncoder.encode(urlParameters,
								"UTF-8")).getBytes(Charset.forName("UTF-8"));
						int postDataLength = postData.length;
				            
						URL url = new URL(request);
						//System.out.println(request + urlParameters);
						HttpURLConnection cox = (HttpURLConnection) url
								.openConnection();
						cox.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; rv:11.0) like Gecko");
						cox.setDoOutput(true);
						cox.setDoInput(true);
						cox.setInstanceFollowRedirects(false);
						cox.setRequestMethod("POST");
						cox.setRequestProperty("Accept-Encoding", "gzip");  
						cox.setRequestProperty("Content-Type",
								"application/x-www-form-urlencoded");
						cox.setRequestProperty("charset", "utf-8");
						cox.setRequestProperty("Content-Length",
								Integer.toString(postDataLength));
						cox.setUseCaches(false);
						
						try (DataOutputStream wr = new DataOutputStream(
								cox.getOutputStream())) {
							wr.write(postData);
							
							// System.out.println(cox.toString());
							InputStream is = cox.getInputStream();
							if (is != null) {
								byte[] header = new byte[2];
								BufferedInputStream bis = new BufferedInputStream(is);
								bis.mark(2);
								int result = bis.read(header);

								// reset输入流到开始位置
								bis.reset();
								BufferedReader reader = null;
								// 判断是否是GZIP格式
								int ss = (header[0] & 0xff) | ((header[1] & 0xff) << 8);
								if (result != -1 && ss == GZIPInputStream.GZIP_MAGIC) {
									// System.out.println("为数据压缩格式...");
									reader = new BufferedReader(new InputStreamReader(
											new GZIPInputStream(bis), "utf-8"));
								} else {
									// 取前两个字节
									reader = new BufferedReader(new InputStreamReader(bis, "utf-8"));
								}
								
								// 创建一个JsonParser
								JsonParser parser = new JsonParser();
						
								//通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
								try {
									JsonElement el = parser.parse(reader.readLine());

									//把JsonElement对象转换成JsonObject
									JsonObject jsonObj = null;
									if(el.isJsonObject())
									{
										jsonObj = el.getAsJsonObject();
										GeoQuery gq = gson.fromJson(jsonObj, GeoQuery.class);
										String lnglat = "";
										if (gq != null && gq.getResult() != null && gq.getResult().size() > 0)
										{
											
											for (int m = 0; m < gq.getResult().size(); m ++)
											{
												System.out.println(gq.getResult().get(m));
												if (gq.getResult().get(m) != null && gq.getResult().get(m).getLocation() != null)
												{
													lnglat = "," + gq.getResult().get(m).getLocation().getLng() + "," + gq.getResult().get(m).getLocation().getLat() + "," + gq.getResult().get(m).getLocation().getRegion();
													FileTool.Dump(pois.elementAt(offset + m) + lnglat, file + ".result.txt", "UTF-8");
													
												}
												else
												{
													FileTool.Dump(pois.elementAt(offset + m), file + ".result.txt", "UTF-8");
												}
											}
										}
									}

								}catch (JsonSyntaxException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						}

					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					offset = n + 1;
					sb.setLength(0);
				}

			} else {

				try {

					String rs = pois.get(n);
					// String toks[] = rs.split("	");
					
					
					// String xml = parseLngLat(toks[fieldIndex]);
					String xml = parseLngLat(rs);
					if (xml != null)
					{
						//System.out.println(rs + " [" + xml + "]");		
						
						FileTool.Dump(rs + " [" + xml + "]", "D:\\temp\\error测试结果.txt", "UTF-8");
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
		
	}
	public static void main(String[] args)  throws UnsupportedEncodingException
	{
		if (true)
		{
			processCSV("D:\\Temp\\cq.txt", 0);
			// 
			return;
		}
		Vector<TextPoi> pois = Load("D:\\Temp\\error.txt"); // plan\\bj_items.txt");
		String srv = "192.168.6.9"; // http://geocode.svail.com
		String request = srv + ":8080/p41?f=json";// "http://geocode.svail.com:8080/p41?f=xml";
		/* String parameters = "&within="
			+ java.net.URLEncoder.encode("重庆", "UTF-8")
			+ "&key=F37EC86E46DD11E58EBB48F1DE870894&queryStr=";
	    */
		String parameters = "&key=F37EC86E46DD11E58EBB48F1DE870894&queryStr=";
		// http://geocode.svail.com:8080/p41?f=xml&queryStr=%E5%8C%97%E4%BA%AC%E5%B8%82%E6%9C%9D%E9%98%B3%E5%8C%BA%E5%8C%97%E8%BE%B0%E4%B8%9C%E8%B7%AF6%E5%8F%B7%E6%89%80&key=EF4070EC424911E58EBB48F1DE870894
		boolean batch = false;
		Gson gson = new Gson();
		if (batch)
			request = srv + ":8080/p4b?f=json";
		
		StringBuffer sb = new StringBuffer();
		for (int n = 0; n < pois.size(); n++) {
			if (batch) {
				if (pois.get(n).address != null) {
					sb.append(pois.get(n).address).append("\n");
				} else {
					sb.append(pois.get(n).name).append("\n");
				}

				if ((n + 1) % 1000 == 0 || n + 1 == pois.size()) {
					String urlParameters = sb.toString();
					// System.out.print(urlParameters);
					
					byte[] postData;
					try {
						postData = (parameters + java.net.URLEncoder.encode(urlParameters,
								"UTF-8")).getBytes(Charset.forName("UTF-8"));
						int postDataLength = postData.length;
				            
						URL url = new URL(request);
						//System.out.println(request + urlParameters);
						HttpURLConnection cox = (HttpURLConnection) url
								.openConnection();
						cox.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; rv:11.0) like Gecko");
						cox.setDoOutput(true);
						cox.setDoInput(true);
						cox.setInstanceFollowRedirects(false);
						cox.setRequestMethod("POST");
						cox.setRequestProperty("Accept-Encoding", "gzip");  
						cox.setRequestProperty("Content-Type",
								"application/x-www-form-urlencoded");
						cox.setRequestProperty("charset", "utf-8");
						cox.setRequestProperty("Content-Length",
								Integer.toString(postDataLength));
						cox.setUseCaches(false);
						
						DataOutputStream wr = new DataOutputStream(cox.getOutputStream());
						wr.write(postData);
							
						InputStream is = cox.getInputStream();
						if (is != null) {
							byte[] header = new byte[2];
							BufferedInputStream bis = new BufferedInputStream(is);
							bis.mark(2);
							int result = bis.read(header);

							// reset输入流到开始位置
							bis.reset();
							BufferedReader reader = null;
							// 判断是否是GZIP格式
							int ss = (header[0] & 0xff) | ((header[1] & 0xff) << 8);
							if (result != -1 && ss == GZIPInputStream.GZIP_MAGIC) {
								// System.out.println("为数据压缩格式...");
								reader = new BufferedReader(new InputStreamReader(
										new GZIPInputStream(bis), "utf-8"));
							} else {
								// 取前两个字节
								reader = new BufferedReader(new InputStreamReader(bis, "utf-8"));
							}
							//
							String line = new String();

							while ((line = reader.readLine()) != null) {
								System.out.println(n + ": " +line);
							}
						}

					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					sb.setLength(0);
				}

			} else {

				String uri = null;
				try {

					String addr = null;
					if (pois.get(n).address != null) {
						// System.out.println(pois.get(n).address);
						addr = pois.get(n).address;
					} else {

						addr = pois.get(n).name;
					}
					String xml = parseLngLat(addr);
					if (xml != null)
					{
						System.out.println("Line " + n + " [" + xml + "]");							
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}		
	}
}