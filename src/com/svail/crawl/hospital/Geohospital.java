/**
 * 
 */
package com.svail.crawl.hospital;


import java.io.UnsupportedEncodingException;
import java.util.Random;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.svail.geotext.GeoQuery;
import com.svail.util.FileTool;
import com.svail.util.HTMLTool;

import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;  

/**
 * @author Administrator
 *
 */
public class Geohospital {
	
	/**
	 * @param args
	 */
	private static Random random = new Random();
	private static final Log log = LogFactory.getLog(Geohospital.class);  
	private static int HOSPTIAL_NO = 0;
	
	public static String LOG = "D:\\test";

	// 医院信息入口为 list-0-0-0-n.html n 为[1, 16]
	/* 抓取医院数据
	 * */
	public static String HOPITAL_INDEX = "http://www.bjguahao.gov.cn/comm";
		
	/* 解析医院清单 */
	public static void parseHospitalSheet(String url)
	{
		Parser parser = new Parser();
		String content = HTMLTool.fetchURL(url, "gb2312","post");

		if (content != null)
		{
			try {
				
				parser.setInputHTML(content);
				parser.setEncoding("gb18030");

				NodeFilter helper = new AndFilter(new HasParentFilter(new TagNameFilter("ul")), 
					new AndFilter(new HasChildFilter(new TagNameFilter("img")), new HasChildFilter(new TagNameFilter("strong"))));
				
				NodeFilter filter = new AndFilter(new TagNameFilter("li"), helper );

				NodeList nodes = parser.extractAllNodesThatMatch(filter);

				if (nodes != null)
				{
					for (int n = 0; n < nodes.size(); n ++)
					{
						String tel = null, poi = null;
						NodeList ncls = nodes.elementAt(n).getChildren();
						
						if (ncls != null)
						{
							for (int m = 0; m < ncls.size(); m ++)
							{
								if (ncls.elementAt(m) instanceof TagNode)
								{
									TagNode tn = (TagNode)ncls.elementAt(m);
									
									if (tn.getTagName().equalsIgnoreCase("span"))
									{
										String str = tn.toPlainTextString().trim().replace("\r", "").replace("\t", "");
										String tokens[] = str.split("\n");	
										
										for (int cnt = 0; cnt < tokens.length; cnt ++)
										{
											if (tokens[cnt].startsWith("电话："))
											{
												tel = tokens[cnt].substring("电话：".length());
											}
										}
									}
									else if (tn.getTagName().equalsIgnoreCase("strong"))
									{
										String str = tn.toPlainTextString().trim();
									}
									else if (tn.getTagName().equalsIgnoreCase("label"))
									{
										NodeList cld = tn.getChildren();
										if (cld != null)
										{
											NodeFilter filter2 = new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href"));
											TagNode tn2 = (TagNode)cld.extractAllNodesThatMatch(filter2).elementAt(0);
											
											//传值子链接地址
											String purl = tn2.getAttribute("href");
											
											if (purl.startsWith("/comm"))
											{
												poi = parseHospitalInfo("http://www.bjguahao.gov.cn/" + purl);
												
											}
										}
									}
								}
							}
						}
						
						if (poi != null)
						{
							if (tel != null)
							{
								poi += "<TELEPHONE>" + tel + "</TELEPHONE>";
								
							}
							FileTool.Dump("<POI>" + poi + "</POI>" , "D:\\Temp\\医院\\poi.csv", "UTF-8");
						}
						
					}
				}
				
				parser.reset();

			}
			catch (ParserException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}
		else
		{
			System.out.println("网页无法打开:" + url);
		}
	}
	public static String parseLngLat(String query) throws UnsupportedEncodingException
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
	

	/* 解析医院信息页面 */
	private static String parseHospitalInfo(String url)
	{
		String content = HTMLTool.fetchURL(url, "gb2312", "post");

		Parser parser = new Parser();
		if (content == null)
		{
			return null;
		}
		
		String poi = null;
		try {
			
			parser.setInputHTML(content);
			parser.setEncoding("gb2312");
			
			/**
			 * 取二级页面的联系地址和医院地址
			 */
			
			NodeFilter helper1 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "hospital"));
			NodeList nodes1 = parser.extractAllNodesThatMatch(helper1);
			if(nodes1 != null)
			{
				for(int k = 0; k < nodes1.size(); k++)
				{
					
					TagNode parnode = (TagNode) nodes1.elementAt(k);
					NodeList snodes = parnode.getChildren();
					if(snodes != null)
					{
						for(int l = 0; l<snodes.size(); l++)
						{
							if(snodes.elementAt(l) instanceof TagNode)
							{
								TagNode tg = (TagNode) snodes.elementAt(l);
								
								if(tg.getTagName().toUpperCase().equalsIgnoreCase("P"))
								{
									String strHosName = tg.toPlainTextString().trim().replace("\t", "").replace("\n", "").replace("\r", "");
									poi = "<RID>" + HOSPTIAL_NO + "</RID><NAME>" + strHosName + "</NAME>";
									HOSPTIAL_NO ++;		
								}
							}
						}
					}
				}
			}

			parser.reset();
			
			NodeFilter helper2 = new AndFilter(new HasParentFilter(new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "notes"))), 
					new AndFilter(new TagNameFilter("span"), new HasChildFilter(new TagNameFilter("li"))));
			NodeList nodes2 = parser.extractAllNodesThatMatch(helper2);
			
			if (nodes2 != null)
			{
			 	for (int n = 0; n < nodes2.size(); n ++)
				{
					TagNode parnode = (TagNode) nodes2.elementAt(n);
					
					NodeList snodes = parnode.getChildren();
					
					if (snodes != null)
					{
						for (int j = 0; j < snodes.size(); j ++)
						{
							
							if (snodes.elementAt(j) instanceof TagNode)
							{
								TagNode tg = (TagNode) snodes.elementAt(j);
								String str = tg.toPlainTextString().trim().replace("\t", "").replace("\n", "").replace("\r", "");
								if (str.startsWith("联系地址"))
								{
									int index_strAddress = str.indexOf("联系地址：");
									
									if(index_strAddress != -1)
									{
										String address = str.substring(index_strAddress + "联系地址：".length(),str.length()).trim();
										
										String tokens[] = null;
										
										if (address.indexOf(";") != -1)
										{
											tokens = address.split(";");
										}
										else if (address.indexOf("；") != -1)
										{
											tokens = address.split("；");
										}
										else if (address.indexOf(" ") != -1)
										{
											tokens = address.split(" ");
										}
										else {
											tokens = new String[1];
											tokens[0] = address;								
										}
										
										String addr = "";
										for (int t = 0; t < tokens.length; t ++)
										{
											if (tokens[t].isEmpty())
												continue;
											
											try {
												String lnglat = parseLngLat(tokens[t]);
												if (lnglat != null && !lnglat.isEmpty())
												{
													addr += "<BRANCH><LOCATION>" + tokens[t] + "</LOCATION>" + "<LNGLAT>" + lnglat + "</LNGLAT></BRANCH>";
												}
											} catch (UnsupportedEncodingException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
										
										if (!addr.isEmpty())
											poi += "<ADDRESS>" + addr + "</ADDRESS>";
										
									}
								}
								else if (str.startsWith("医院网址"))
								{
									NodeList cds = tg.getChildren();
									
									if (cds != null)
									{
										for (int m = 0; m < cds.size(); m ++)
										{
											if (cds.elementAt(m) instanceof TagNode)
											{
												TagNode cnode = (TagNode)cds.elementAt(m);
												
												String hosurl = null;
												if (cnode.getTagName().equalsIgnoreCase("a") && (hosurl = cnode.getAttribute("href")) != null)
												{
													poi += "<URL>" + hosurl.trim() + "</URL>";
													break;
												}
											}
										}
									}
								}								
							}
						}								
					}
				}
			}

		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 

		return poi;
	}
	
	
	public static void main(String[] args) throws Exception {

		int step = 0; 
		
		if (step == 0)
		{
			for (int n = 0; n < 16; n ++ )
			{
				parseHospitalSheet(HOPITAL_INDEX + "/list-0-0-0-" +  (n + 1) + ".html" );
			}
		}
		
		//小抓抓
//		System.out.println("开始抓取");
//		
//		for(int index_html = 0; index_html<regions.length; index_html++)
//		{
//			System.out.println("正在抓取第:"+ regions[index_html]);
//			getResoldApartmentInfo(regions[index_html]);
//		}
//		System.out.println("完成抓取工作");
		

	}

}
