package com.svail.crawl.fang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.htmlparser.Node;
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

import com.svail.util.FileTool;
import com.svail.util.HTMLTool;

import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;  

public class Geofang {

	private static Random random = new Random();
	private static final Log log = LogFactory.getLog(Geofang.class);  
	private static String CQ_NEWHOUSE = "NEW";
	private static String CQ_RENTINFO = "RENTINFO";
	private static String CQ_RENTOUT = "RENTOUT";
	private static String CQ_RESOLDS = "RESOLD";
	
	/* 解析出租页面 */
	private static String parseRentOut(String url)
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
			// 获取发布时间
			
			/* <dl class="title">
             * <dt class="name floatl">
             *   <p>
             * 长寿黄桷雅居 3室2厅126平米 押一付三(个人)
             *      
             * </p>
             * <p style="font-size: 12px; line-height: 16px" class="gray9">
             * 房源编号：51173910&nbsp;&nbsp;&nbsp;发布时间：2015/2/21 5:02:44(3天前更新)</p>
             *    <script type="text/javascript" src="http://img1.soufun.com/rent/image/rent/js/shareclick.js"></script>
             *   <a name="top" id="top"></a>
             *   <dd>
             *       <a class="btn-fabu" href="/Rent/Rent_Info/Rent_Input_Front.aspx" target="_blank"
             *           id="A1">免费发布出租</a></dd>
             * </dl>
             * */
			NodeFilter filter = new AndFilter(new TagNameFilter("dl"), new HasAttributeFilter("class", "title"));
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
			
			if (nodes != null && nodes.size() == 1)
			{
				String str = nodes.elementAt(0).toPlainTextString().replace("免费发布出租", "").replace("\r\n", "").replace("\t", "").trim();
				
				int n = str.indexOf("房源编号：");
				int m = str.indexOf("发布时间：");
				
				if (n != -1)
				{
					poi = "<TITLE>" + str.substring(0, n).replace(" ", "").trim() + "</TITLE>";
					if (m != -1)
					{
						int k = str.indexOf("(", m + "发布时间：".length());
						if (k != -1)
						{
							poi += "<TIME>" + str.substring(m + "发布时间：".length(), k).trim() + "</TIME>";
							parser.reset();
							
							// Huxing floatl
							filter = new AndFilter(new TagNameFilter("span"), new HasAttributeFilter("class", "num red"));
							
							nodes = parser.extractAllNodesThatMatch(filter);
							if (nodes != null)
							{
								for (int cnt = 0; cnt < nodes.size(); cnt ++)
								{
									Node cni = nodes.elementAt(cnt);
									
									str = cni.getParent().toPlainTextString().trim();
									if (str.indexOf("元/月") != -1)
									{
										poi += "<PRICE>" + str + "</PRICE>";
										break;
									}
								}
							}
							parser.reset();
							
							// <span class="num red">
							filter = new AndFilter(new TagNameFilter("li"), new HasParentFilter(new AndFilter(new TagNameFilter("ul"), new HasAttributeFilter("class", "Huxing floatl"))));
							nodes = parser.extractAllNodesThatMatch(filter);

							if (nodes != null)
							{
								for (int mm = 0; mm < nodes.size(); mm ++)
								{
									Node ni = nodes.elementAt(mm);
										
									if (ni instanceof TagNode && ((TagNode)ni).getTagName().equalsIgnoreCase("li"))
									{
										String tt = ni.toPlainTextString().trim();
										
										
										int ix = tt.indexOf("物业类型：");
										if (ix != -1)
										{
											String sub = tt.substring(ix + "物业类型：".length()).replace("\r\n", "").replace("\t", "").replace(" ", "").trim();
											if (sub.indexOf("暂无") == -1)
												poi += "<PROPERTY_TYPE>" + sub + "</PROPERTY_TYPE>";
											continue;
										}
										
										ix = tt.indexOf("小 区：");
										
										if (ix != -1)
										{
											String sub = tt.substring(ix + "小 区：".length()).replace("\r\n", "").replace("\t", "").replace(" ", "").trim();
											if (sub.indexOf("暂无") == -1)
												poi += "<COMMUNITY>" + sub + "</COMMUNITY>";
											continue;
										}

										ix = tt.indexOf("地 址：");
										
										if (ix != -1)
										{

											String sub = tt.substring(ix + "地 址：".length()).replace("\r\n", "").replace("\t", "").replace(" ", "").trim();
											if (sub.indexOf("暂无") == -1)
												poi += "<ADDRESS>" + sub + "</ADDRESS>";
											continue;
										}
										
										ix = tt.indexOf("户 型：");
										
										if (ix != -1)
										{

											String sub = tt.substring(ix + "户 型：".length()).replace("\r\n", "").replace("\t", "").replace(" ", "").trim();
											if (sub.indexOf("暂无") == -1)
												poi += "<HOUSE_TYPE>" + sub + "</HOUSE_TYPE>";
											
											continue;
										}

										ix = tt.indexOf("出租间：");
										
										if (ix != -1)
										{

											String sub = tt.substring(ix + "出租间：".length()).replace("\r\n", "").replace("\t", "").replace(" ", "").trim();
											if (sub.indexOf("暂无") == -1)
												poi += "<PARTMENT>" + sub + "</PARTMENT>";
											
											continue;
										}


										ix = tt.indexOf("面 积：");
										
										if (ix != -1)
										{

											String sub = tt.substring(ix + "面 积：".length()).replace("\r\n", "").replace("\t", "").replace(" ", "").trim();
											if (sub.indexOf("暂无") == -1)
												poi += "<AREA>" + sub + "</AREA>";

											continue;
										}

										ix = tt.indexOf("朝 向：");
										
										if (ix != -1)
										{

											String sub = tt.substring(ix + "朝 向：".length()).replace("\r\n", "").replace("\t", "").replace(" ", "").trim();
											if (sub.indexOf("暂无") == -1)
												poi += "<DIRECTION>" + sub + "</DIRECTION>";
											
											continue;
										}

										ix = tt.indexOf("楼 层：");
										
										if (ix != -1)
										{

											String sub = tt.substring(ix + "楼 层：".length()).replace("\r\n", "").replace("\t", "").replace(" ", "").trim();
											if (sub.indexOf("暂无") == -1)
												poi += "<FLOOR>" + sub + "</FLOOR>";

											continue;
										}
										

										ix = tt.indexOf("装 修：");
										
										if (ix != -1)
										{

											String sub = tt.substring(ix + "装 修：".length()).replace("\r\n", "").replace("\t", "").replace(" ", "").trim();
											if (sub.indexOf("暂无") == -1)
												poi += "<DECORATION>" + sub + "</DECORATION>";

											continue;
										}
										
										ix = tt.indexOf("公 交：");
										
										if (ix != -1)
										{

											String sub = tt.substring(ix + "公 交：".length()).replace("\r\n", "").replace("\t", "").replace(" ", "").trim();
											if (sub.indexOf("暂无") == -1)
												poi += "<TRAFFIC>" + sub + "</TRAFFIC>";

											continue;
										}
									}
								}
							
															
								String ll = parseLngLat(url, content);
								if (ll != null)
									return "<POI>" + poi + ll + "<URL>" + url + "</URL></POI>";
								else
									return "<POI>" + poi + "<URL>" + url + "</URL></POI>";
							}
						}
					}		
				}
			}
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		if (poi != null)
		{
			poi = poi.replace("&nbsp;", "");
			int ss = poi.indexOf("[");
			while (ss != -1)
			{
				int ee = poi.indexOf("]", ss + 1);
				if (ee != -1)
				{
					String sub = poi.substring(ss, ee + 1);
					poi = poi.replace(sub, "");
				}
				else
					break;
				ss = poi.indexOf("[", ss);
			}
		}
		return poi;
	}
	
	/* 解析二手房页面 */
	private static String parseResold(String url)
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
			// 获取发布时间
			
			/* <dl class="title">
             * <dt class="name floatl">
             *   <p>
             * 长寿黄桷雅居 3室2厅126平米 押一付三(个人)
             *      
             * </p>
             * <p style="font-size: 12px; line-height: 16px" class="gray9">
             * 房源编号：51173910&nbsp;&nbsp;&nbsp;发布时间：2015/2/21 5:02:44(3天前更新)</p>
             *    <script type="text/javascript" src="http://img1.soufun.com/rent/image/rent/js/shareclick.js"></script>
             *   <a name="top" id="top"></a>
             *   <dd>
             *       <a class="btn-fabu" href="/Rent/Rent_Info/Rent_Input_Front.aspx" target="_blank"
             *           id="A1">免费发布出租</a></dd>
             * </dl>div class="mainBoxL
             * */
			NodeFilter filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "mainBoxL"));
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
			
			if (nodes != null && nodes.size() == 1)
			{
				String str = nodes.elementAt(0).toPlainTextString().replace("\r\n", "").replace("\t", "").trim();
				
				int n = str.indexOf("房源编号：");
				int m = str.indexOf("发布时间：");
				
				if (n != -1)
				{
					poi = "<TITLE>" + str.substring(0, n).trim() + "</TITLE>";
					if (m != -1)
					{
						int k = str.indexOf("(", m + "发布时间：".length());
						if (k != -1)
						{
							poi += "<TIME>" + str.substring(m + "发布时间：".length(), k) + "</TIME>";
						}
					}
				}
			}
			if (poi == null)
				return poi;
			
			parser.reset();
			filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "inforTxt"));
			nodes = parser.extractAllNodesThatMatch(filter);
			
			if (nodes != null)
			{
				for (int rds = 0; rds < nodes.size(); rds ++)
				{
					NodeList hs = nodes.elementAt(rds).getChildren();
					if (hs == null)
						continue;
					
					for (int jk = 0; jk < hs.size(); jk ++)
					{
						if (hs.elementAt(jk) instanceof TagNode)
						{
							TagNode tn = (TagNode) hs.elementAt(jk);
							if (tn.getTagName().equalsIgnoreCase("dl"))
							{
								NodeList chld = tn.getChildren();
								
								if (chld != null)
								{
									for (int cnt = 0; cnt < chld.size(); cnt ++)
									{
										if (chld.elementAt(cnt) instanceof TagNode)
										{
											String str = ((TagNode) chld.elementAt(cnt)).toPlainTextString().replace("\r\n", "").replace("\t", "").replace(" ","").trim();
											int kk = str.indexOf("总价：");
											if (kk != -1)
											{
												int kk1 = str.indexOf("(", kk + "总价：".length());
												if (kk1 != -1)
												{
													String substr = str.substring(kk + "总价：".length(), kk1);
													poi += "<PRICE>" + substr + "</PRICE>";
												}
												continue;
											}
											kk = str.indexOf("户型：");
											if (kk != -1)
											{
												String substr = str.substring(kk + "户型：".length());
												if (substr.indexOf("暂无") == -1)
													poi += "<HOUSE_TYPE>" + substr + "</HOUSE_TYPE>";
												continue;
											}
											kk = str.indexOf("面积：");
											if (kk != -1)
											{
												String substr = str.substring(kk + "面积：".length());
												if (substr.indexOf("暂无") == -1)
													poi += "<BUILDING_AREA>" + substr + "</BUILDING_AREA>";
												continue;
											}
											
											kk = str.indexOf("年代：");
											if (kk != -1)
											{
												String substr = str.substring(kk + "年代：".length());
												if (substr.indexOf("暂无") == -1)
													poi += "<BUILDING_TIME>" + substr + "</BUILDING_TIME>";
												continue;
											}
											
											kk = str.indexOf("朝向：");
											if (kk != -1)
											{
												String substr = str.substring(kk + "朝向：".length());
												if (substr.indexOf("暂无") == -1)
													poi += "<BUILDING_DIR>" + substr + "</BUILDING_DIR>";
												continue;
											}
											kk = str.indexOf("楼层：");
											if (kk != -1)
											{
												String substr = str.substring(kk + "楼层：".length());
												if (substr.indexOf("暂无") == -1)
													poi += "<BUILDING_FLOOR>" + substr + "</BUILDING_FLOOR>";
												continue;
											}
											kk = str.indexOf("结构：");
											if (kk != -1)
											{
												String substr = str.substring(kk + "结构：".length());
												if (substr.indexOf("暂无") == -1)
													poi += "<BUILDING_STRUCT>" + substr + "</BUILDING_STRUCT>";
												continue;
											}
											kk = str.indexOf("装修：");
											if (kk != -1)
											{
												String substr = str.substring(kk + "装修：".length());
												if (substr.indexOf("暂无") == -1)
													poi += "<BUILDING_CONDITION>" + substr + "</BUILDING_CONDITION>";
												continue;
											}
											kk = str.indexOf("名称：");
											if (kk != -1)
											{
												String substr = str.substring(kk + "名称：".length());
												if (substr.indexOf("暂无") == -1)
													poi += "<BUILDING_NAME>" + substr + "</BUILDING_NAME>";
												continue;
											}
											kk = str.indexOf("住宅类别：");
											if (kk != -1)
											{
												String substr = str.substring(kk + "住宅类别：".length());
												if (substr.indexOf("暂无") == -1)
													poi += "<BUILDING_TYPE>" + substr + "</BUILDING_TYPE>";
												continue;
											}
											
											kk = str.indexOf("产权性质：");
											if (kk != -1)
											{
												String substr = str.substring(kk + "产权性质：".length());
												if (substr.indexOf("暂无") == -1)
													poi += "<BUILDING_OWNERTYPE>" + substr + "</BUILDING_OWNERTYPE>";
												continue;
											}
											
											kk = str.indexOf("配套设施：");
											if (kk != -1)
											{
												String substr = str.substring(kk + "配套设施：".length());
												if (substr.indexOf("暂无") == -1)
													poi += "<BUILDING_SERVICE>" + substr + "</BUILDING_SERVICE>";
												continue;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			parser.reset();
			filter = new AndFilter(new  TagNameFilter("p"), new HasParentFilter(new HasAttributeFilter("class", "traffic mt10")));
			nodes = parser.extractAllNodesThatMatch(filter);
			
			if (nodes != null)
			{
				for (int cnt = 0; cnt < nodes.size(); cnt ++)
				{
					String str = nodes.elementAt(cnt).toPlainTextString().trim();
					int si = str.indexOf("址：");
					if (str.indexOf("地") != -1 && si != -1)
					{
						if (str.indexOf("暂无") == -1)
							poi += "<ADDRESS>" + str.substring(si + "址：".length()) + "</ADDRESS>";
					}
				}
			}
			poi = poi.replace("&nbsp;", "");
			int ss = poi.indexOf("[");
			while (ss != -1)
			{
				int ee = poi.indexOf("]", ss + 1);
				if (ee != -1)
				{
					String sub = poi.substring(ss, ee + 1);
					poi = poi.replace(sub, "");
				}
				else
					break;
				ss = poi.indexOf("[", ss);
			}
			String ll = parseLngLat(url, content);
			if (ll != null)
				return "<POI>" + poi + ll + "<URL>" + url + "</URL></POI>";
			else
				return "<POI>" + poi + "<URL>" + url + "</URL></POI>";

		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		return poi;
	}
	
	/* 解析本月开盘页面 */
	private static String parseNewBuilding(String url)
	{
		String content = HTMLTool.fetchURL(url, "gb2312", "post");

		Parser parser = new Parser();
		if (content == null)
		{
			return null;
		}
		
		String poi = "";
		try {
			
			parser.setInputHTML(content);
			parser.setEncoding("gb2312");
			// <a id="xfxq_C03_14" target="_blank" href="http://haitangwanld023.fang.com/house/3111064820/housedetail.htm">更多详细信息&gt;&gt;</a>
			NodeFilter filter = new AndFilter(new TagNameFilter("a"), new AndFilter(new HasAttributeFilter("id", "xfxq_C03_14"), new HasAttributeFilter("href")));
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
			
			if (nodes != null)
			{
				for (int n = 0; n < nodes.size(); n ++)
				{
					if (nodes.elementAt(n) instanceof TagNode)
					{
						TagNode tn = (TagNode) nodes.elementAt(n);
						
						if (tn.getAttribute("href").endsWith("housedetail.htm"))
						{
							content = HTMLTool.fetchURL(tn.getAttribute("href"), "gb18030", "post");
							
							if (content == null)
							{
								return null;
							}
							try {
								
								parser.setInputHTML(content);
								parser.setEncoding("gb2312");
								
								// filter = new AndFilter(new TagNameFilter("table"), new HasParentFilter(new HasAttributeFilter("class", "besic_inform")));
								
								nodes = parser.extractAllNodesThatMatch(new HasAttributeFilter("class", "besic_inform"));
								
								for (int jk = 0; jk < nodes.size(); jk ++)
								{
									NodeList cds = nodes.elementAt(jk).getChildren();
									if (cds == null)
										continue;
									
									for (int jn = 0; jn < cds.size(); jn ++)
									{
										if (cds.elementAt(jn) instanceof TagNode && ((TagNode)cds.elementAt(jn)).getTagName().equalsIgnoreCase("table"))
										{
											NodeList cnodes = ((TagNode)cds.elementAt(jn)).getChildren();
											if (cnodes == null)
												continue;
											
											for (int cnt = 0; cnt < cnodes.size(); cnt ++)
											{
												Node  ni = cnodes.elementAt(cnt);
												NodeList chld = ni.getChildren();
												if (chld == null)
													continue;
												
												for (int ik = 0; ik < chld.size(); ik ++)
												{
													ni = chld.elementAt(ik);
	
													if (ni instanceof TagNode && ((TagNode)ni).getTagName().equalsIgnoreCase("td"))
													{
														String tt = ni.toPlainTextString().replace("\r\n", "").replace("\t", "").replace(" ", "").replace("&nbsp", "").trim();
														if (tt.indexOf("物业类别") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<BUILDING_USAGE>" + tt.replace("物业类别", "") + "</BUILDING_USAGE>";
														}
														else if (tt.indexOf("项目特色") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<CHARACTER>" + tt.replace("项目特色", "") + "</CHARACTER>";
														}
														else if (tt.indexOf("建筑类别") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<BUILDING_TYPE>" + tt.replace("建筑类别", "") + "</BUILDING_TYPE>";
														}
														else if (tt.indexOf("装修状况") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<BUILDING_CONDITION>" + tt.replace("装修状况", "") + "</BUILDING_CONDITION>";
														}
														else if (tt.indexOf("容 积 率") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<FAR>" + tt.replace("容 积 率", "") + "</FAR>";
														}
														else if (tt.indexOf("绿化率") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<GREEN>" + tt.replace("绿化率", "") + "</GREEN>";
														}
														else if (tt.indexOf("开盘时间") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<SALE_TIME>" + tt.replace("开盘时间", "") + "</SALE_TIME>";
														}
														else if (tt.indexOf("交房时间") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<SUBMIT_TIME>" + tt.replace("交房时间", "") + "</SUBMIT_TIME>";
														}
														else if (tt.indexOf("物业费") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<PROPERTY_FEE>" + tt.replace("物业费", "") + "</PROPERTY_FEE>";
														}
														else if (tt.indexOf("物业公司") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<SERVER>" + tt.replace("物业公司", "") + "</SERVER>";
														}
														else if (tt.indexOf("开发商") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<DEVELOPER>" + tt.replace("开 发 商", "").replace("开发商", "") + "</DEVELOPER>";
														}
														else if (tt.indexOf("售楼地址") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<SALE_ADDRESS>" + tt.replace("售楼地址", "") + "</SALE_ADDRESS>";
														}
														else if (tt.indexOf("交通状况") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<TRAFFIC>" + tt.replace("交通状况", "") + "</TRAFFIC>";
														}
														else if (tt.indexOf("房价") != -1)
														{
															int kok = tt.indexOf("房价");
															if (tt.indexOf("暂无") == -1)
																poi += "<PRICE>" + tt.substring(kok + "房价".length()) + "</PRICE>";
														}
														else if (tt.indexOf("预售许可证") != -1)
														{
															if (tt.indexOf("暂无") == -1)
																poi += "<LISENCE>" + tt.replace("预售许可证", "") + "</LISENCE>";
														}
													}
												}
											}
										}							
									}
								}
									
								if (!poi.isEmpty())
								{
									int ss = poi.indexOf("[");
									while (ss != -1)
									{
										int ee = poi.indexOf("]", ss + 1);
										if (ee != -1)
										{
											String sub = poi.substring(ss, ee + 1);
											poi = poi.replace(sub, "");
										}
										else
											break;
										ss = poi.indexOf("[", ss);
									}
									return "<POI>" + poi + "<URL>" + url + "</URL></POI>";
								}
							
							} catch (ParserException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
						}
					}
				}
			}

			return null;

		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		if (poi != null)
		{
			poi = poi.replace("&nbsp;", "");
			int ss = poi.indexOf("[");
			while (ss != -1)
			{
				int ee = poi.indexOf("]", ss + 1);
				if (ee != -1)
				{
					String sub = poi.substring(ss, ee + 1);
					poi = poi.replace(sub, "");
				}
				else
					break;
				ss = poi.indexOf("[", ss);
			}
		}
		return poi;
	}
	
	/* 解析求租页面 */
	private static String parseRental(String url)
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
			// 获取发布时间
			// div class="title"
			NodeFilter filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "title"));
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
			
			if (nodes != null && nodes.size() == 1)
			{
				nodes = nodes.elementAt(0).getChildren();
				for (int n = 0; n < nodes.size(); n ++)
				{
					String str = nodes.elementAt(n).toPlainTextString().trim();
					
					int mm = str.indexOf("]");
					if (mm != -1)
					{
						if (poi == null)
							poi = "<TITLE>" + str.substring(mm + 1) + "</TITLE>";
						else
							poi += "<TITLE>" + str.substring(mm + 1) + "</TITLE>";
						continue;
					}
					
					mm = str.indexOf("发布");
					if (mm != -1)
					{
						if (poi == null)
							poi = "<TIME>" + str.replace("发布", "") + "</TIME>";
						else
							poi += "<TIME>" + str.replace("发布", "") + "</TIME>";
					}
				}
			}
			if (poi == null)
				return null;
			
			parser.reset();
			// class="house"
			filter = new AndFilter(new TagNameFilter("dl"), new HasAttributeFilter("class", "house"));
			// 期望租金：1500元/月 期望面积： 不小于65平米 租赁方式：整租求租地点：九龙坡，石桥铺期望户型：二居房屋配套：暂无资料
			nodes = parser.extractAllNodesThatMatch(filter);
			
			if (nodes != null)
			{
				for (int n = 0; n < nodes.size(); n ++)
				{
					Node no = nodes.elementAt(n);
					if (no instanceof TagNode)
					{
						TagNode tno = (TagNode) no;
						String str = tno.toPlainTextString().replace(" ", "").replace("\t", "").trim();
						String toks[] = str.split("\r\n");
						for (int jk = 0; jk < toks.length; jk ++)
						{
							if (toks[jk].startsWith("期望租金："))
							{
								str = toks[jk].substring("期望租金：".length());
								if (str.indexOf("暂无") == -1)
								{
									poi += "<PRICE>" + str.trim() + "</PRICE>";
									continue;
								}
							}
							else if (toks[jk].startsWith("期望面积："))
							{
								str = toks[jk].substring("期望面积：".length());
								if (str.indexOf("暂无") == -1)
								{
									poi += "<AREA>" + str.trim() + "</AREA>";
									continue;
								}
							}
							else if (toks[jk].startsWith("租赁方式："))
							{
								str = toks[jk].substring("租赁方式：".length());
								if (str.indexOf("暂无") == -1)
								{
									poi += "<SCHEMA>" + str.trim() + "</SCHEMA>";
									continue;
								}
							}else if (toks[jk].startsWith("求租地点："))
							{
								str = toks[jk].substring("求租地点：".length());
								if (str.indexOf("暂无") == -1)
								{
									poi += "<ADDRESS>" + str.trim() + "</ADDRESS>";
									continue;
								}
							}else if (toks[jk].startsWith("期望户型："))
							{
								str = toks[jk].substring("期望户型：".length());
								if (str.indexOf("暂无") == -1)
								{
									poi += "<BUILDTYPE>" + str.trim() + "</BUILDTYPE>";
									continue;
								}
							}else if (toks[jk].startsWith("配套："))
							{
								str = toks[jk].substring("配套：".length());
								if (str.indexOf("暂无") == -1)
								{
									poi += "<EQUITMENT>" + str.trim() + "</EQUITMENT>";
									continue;
								}
							}
						}						
					}
				}
			}
			
			parser.reset();
			// <span class="tel"> <span>15922777917</span><span class="font14 gray6 master">小李</span> </span>
			filter = new AndFilter(new TagNameFilter("span"), new HasAttributeFilter("class", "tel"));
			nodes = parser.extractAllNodesThatMatch(filter);
			if (nodes != null)
			{
				String s = nodes.elementAt(0).toPlainTextString().replace(" ", "").replace("\t", "").replace("\r\n", "").trim();
				poi += "<CONTACT>" + s + "</CONTACT>";
			}
						
			parser.reset();
			// <p class="beizhu">家电齐全，价钱看房后面议</p>
			filter = new AndFilter(new TagNameFilter("p"), new HasAttributeFilter("class", "beizhu"));
			nodes = parser.extractAllNodesThatMatch(filter);
			if (nodes != null)
			{
				String s = nodes.elementAt(0).toPlainTextString().replace(" ", "").replace("\t", "").replace("\r\n", "").trim();
				poi += "<NOTATION>" + s + "</NOTATION>";
			}
			
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		if (poi != null)
		{
			poi = poi.replace("&nbsp;", "");
			int ss = poi.indexOf("[");
			while (ss != -1)
			{
				int ee = poi.indexOf("]", ss + 1);
				if (ee != -1)
				{
					String sub = poi.substring(ss, ee + 1);
					poi = poi.replace(sub, "");
				}
				else
					break;
				ss = poi.indexOf("[", ss);
			}
			
			if (poi != null  && !poi.isEmpty())
				poi = "<POI>" + poi + "<URL>" + url + "</URL></POI>";
		}
		return poi;
	}
	private static String parseLngLat(String url, String content)
	{
		if (content == null)
			return null;
		/*
		 *   <iframe frameborder="0" id="iframeBaiduMap" scrolling="no" width="930px" height="415px" src="/newsecond/map/NewMapDetail.aspx?newcode=3110885386&isrent=Y&width=788&height=355"> </iframe>
		 *
		 *  px:"116.19629669189453125000",py:"39.91481781005859375000"
		 * */
		
		/*
		 *  <div class="newtitle_center"><a target="_blank" href="http://map.soufun.com/house/sh/index.php?newcode=1211095510">
		 *  
		 * var cityx=121.07670593261718750000;
		 * var cityy=31.31203269958496093750;
		 * */
		
		int s = content.indexOf("src=\"/newsecond/map/NewMapDetail.aspx?newcode=");
		
		String ref = null;
		if (s != -1)
		{
			int e = content.indexOf("\"", s + "src=\"/newsecond/map/NewMapDetail.aspx?newcode=".length());
			if (e != -1)
			{
				ref = "http://esf.cq.fang.com" + content.substring(s + "src=\"".length(), e);
			}
		}
		else
		{
			s = content.indexOf("src=\"http://zu.cq.fang.com/map/NewMapDetail.aspx?newcode=");
			if (s != -1)
			{
				int e = content.indexOf("\"", s + "src=\"http://zu.cq.fang.com/map/NewMapDetail.aspx?newcode=".length());
				if (e != -1)
				{
					ref = content.substring(s + "src=\"".length(), e);
				}
			}
		}
		
		if (ref != null)
		{
			String contentd = HTMLTool.fetchURL(ref, "gb2312", "post");

			if (contentd == null)
				return null;
			
			String px = null, py = null;
			if (ref.startsWith("http://esf.cq.fang.com/newsecond/map/NewMapDetail.aspx?newcode")
				|| ref.startsWith("http://zu.cq.fang.com/map/NewMapDetail.aspx?newcode="))
			{
				// px:"116.19629669189453125000",py:"39.91481781005859375000"
				
				s = contentd.indexOf("px:\"");
				if (s != -1)
				{
					int e = contentd.indexOf("\"", s + "px:\"".length());
					if (e != -1)
					{
						px = contentd.substring(s + "px:\"".length(), e);
						
						s = contentd.indexOf("py:\"");
						if (s != -1)
						{
							e = contentd.indexOf("\"", s + "py:\"".length());
							if (e != -1)
							{
								py = contentd.substring(s + "py:\"".length(), e);
								
							}
							else
							{
								System.out.println("解析y坐标出错:" + url);
							}
						}
						else
							System.out.println("解析y坐标出错:" + url);
					}
					else
					{
						System.out.println("解析x坐标出错:" + url);
					}
				}
				else
					System.out.println("解析x坐标出错:" + url);
			}
			else
			{
				/* var cityx=121.07670593261718750000;
				 * var cityy=31.31203269958496093750;
				 */
				s = contentd.indexOf("var cityx=");
				if (s != -1)
				{
					int e = contentd.indexOf(";", s + "var cityx=".length());
					if (e != -1)
					{
						px = contentd.substring(s + "var cityx=".length(), e);

						s = contentd.indexOf("var cityy=");
						if (s != -1)
						{
							e = contentd.indexOf(";", s + "var cityy=".length());
							if (e != -1)
							{
								py = contentd.substring(s + "var cityy=".length(), e);

							}
							else
							{
								System.out.println("解析y坐标出错:" + url);
							}
						}
						else
							System.out.println("解析y坐标出错:" + url);
					}
					else
					{
						System.out.println("解析x坐标出错:" + url);
					}
				}
				else
					System.out.println("解析x坐标出错:" + url);
			}
			
			if (px != null)
			{
				return "<LNGLAT>" + px + ";" + py + "</LNGLAT>";
			} 
		} 
		
		return null;
	}
	
	public static String LOG = "D:\\test";
	/*
	 * 二手房 http://esf.cq.fang.com/
	 * 出租房  http://zu.cq.fang.com/
     * */
	public static String regions[] = {
		"/house-a011825/", "/house-a011828/", "/house-a01182-b02165/", "/house-a01182-b02166/", "/house-a01182-b02167/", "/house-a01182-b0262/",
		"/house-a01182-b0263/", "/house-a01182-b0264/", "/house-a01182-b04880/", "/house-a01182-b05913/", "/house-a01182-b05916/", "/house-a01182-b07491/",
		"/house-a01182-b07494/", "/house-a01182-b07495/", "/house-a01182-b07496/", "/house-a01182-b07497/", "/house-a01182-b07498/", "/house-a01182-b07499/",
		"/house-a01182-b07500/", "/house-a01182-b07501/", "/house-a01182-b07502/", "/house-a01182-b07503/", "/house-a01182-b07504/", "/house-a01182-b07505/",
		"/house-a01182-b07506/", "/house-a01182-b07507/", "/house-a01182-b07508/", "/house-a011831/", "/house-a011837/", "/house-a011840/",
		"/house-a011841/", "/house-a056/", "/house-a057/", "/house-a058/", "/house-a059/", "/house-a060/", "/house-a061/", "/house-a062/",
		"/house-a063/", "/house-a064/",
	};
	
	/* 抓取二手房数据
	 * */
	public static String RESOLDAPARTMENT_URL = "http://esf.cq.fang.com";
	
	public static void getResoldApartmentInfo(String region)
	{
		// 首先加载
		Vector<String> log = null;
		synchronized(CQ_RESOLDS)
		{
			log = FileTool.Load(LOG + File.separator + region + "_resold.log", "UTF-8");
		}
		// 2014/12/8 17:16:42
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");//小写的mm表示的是分钟
		
		java.util.Date latestdate = null;
		Date newest = null;
		
		if (log != null)
		{
			try {
				latestdate = sdf.parse(log.elementAt(0));
				latestdate = new Date(latestdate.getTime() - 1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String url = RESOLDAPARTMENT_URL + region;
		Vector<String> urls = new Vector<String>();
		
		Set<String> visited = new TreeSet<String>();
		urls.add(url);
		
		Parser parser = new Parser();
		boolean quit = false;
		
		while (urls.size() > 0)
		{
			// 解析页面
			url = urls.get(0);
			
			urls.remove(0);
			visited.add(url);
			
			String content = HTMLTool.fetchURL(url, "gb2312", "post");

			if (content == null)
			{
				continue;
			}
			try {
				
				parser.setInputHTML(content);
				parser.setEncoding("gb18030");
				// <dd class="info rel floatr">
				// <p class="title"><a href="/chushou/3_153703104.htm" target="_blank" title="冉家坝龙山小学旁 光宇阳光地中海精装三房急售 无营业税拎包入住">冉家坝龙山小学旁 光宇阳光地中海精装三房急售 无营业税拎包入住</a></p>					
				// <p class="title"
				HasParentFilter parentFilter = new HasParentFilter(new AndFilter(new TagNameFilter("p"), new HasAttributeFilter("class", "title")));
				NodeFilter filter = new AndFilter(new TagNameFilter("a"), new AndFilter(new AndFilter(parentFilter, new HasAttributeFilter("title")), new HasAttributeFilter("href"))); 
				
				NodeList nodes = parser.extractAllNodesThatMatch(filter);
				
				if (nodes != null)
				{
					for (int n = 0; n < nodes.size(); n ++)
					{
						TagNode tn = (TagNode)nodes.elementAt(n);
						String purl = tn.getAttribute("href");
						if (purl.startsWith("/chushou"))
						{
							String poi = parseResold("http://esf.cq.fang.com" + purl);
							if (poi != null)
							{
								// 获取时间
								int m = poi.indexOf("<TIME>");
								int k = poi.indexOf("</TIME>");
								
								if (m != -1 && k != -1)
								{
									assert(m < k);
									String tm = poi.substring(m + "<TIME>".length(), k);
									try {
										Date date = sdf.parse(tm);
										if (latestdate != null)
										{
											if (date.before(latestdate))
											{
												quit = true;
											}
											else if (newest == null)
											{
												newest = date;
											}
											else{
												if (newest.before(date))
													newest = date;
											}
												
										}
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									if (quit)
									{
										break;
									}
									else
									{
										synchronized(CQ_RESOLDS)
										{
											FileTool.Dump(poi, "D:\\二手房.csv", "UTF-8");
										}
									}
								}
							}
							
							try {
								Thread.sleep(500 * ((int) (Math
									.max(1, Math.random() * 3))));
							} catch (final InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
				
				parser.reset();
				
				// <div class="fanye gray6">  <a class="pageNow">
				filter = new AndFilter(new TagNameFilter("div"), 
					new HasChildFilter(new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("class", "pageNow")))); 
				
				nodes = parser.extractAllNodesThatMatch(filter);
				
				if (nodes != null)
				{
					for (int nn = 0; nn < nodes.size(); nn ++)
					{
						NodeList chds = nodes.elementAt(nn).getChildren();

						if (chds == null)
							continue;
						
						for (int jk = 0; jk < chds.size(); jk ++)
						{
							if (chds.elementAt(jk) instanceof TagNode)
							{
								TagNode tni = (TagNode) chds.elementAt(jk);
								String href = tni.getAttribute("href");
								if (tni.getTagName().equalsIgnoreCase("a") && tni.getAttribute("id") == null && href != null)
								{
									if (!visited.contains("http://esf.cq.fang.com" + href))
									{
										int kk = 0;
										for (; kk < urls.size(); kk ++)
										{
											if (urls.elementAt(kk).equalsIgnoreCase("http://esf.cq.fang.com" + href))
											{
												break;
											}
										}
										
										if (kk == urls.size())
											urls.add("http://esf.cq.fang.com" + href);
									}
								}
							}	
						}
					}
				}
				
				if (quit)
					break;
			}
			catch (ParserException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}
		
		synchronized(CQ_RESOLDS)
		{
			File f = new File(LOG + File.separator + region + "_resold.log");
			f.delete();
			if (newest != null)
			{			
				FileTool.Dump(sdf.format(newest), LOG + File.separator + region + "_resold.log", "UTF-8");
			}
		}
	}
	
	/* 抓取出租数据
	 * */
	public static String RENTOUT_URL = "http://zu.cq.fang.com";
	
	public static void getRentOutInfo(String region)
	{
		// 首先加载
		Vector<String> log = null;
		synchronized(CQ_RENTOUT)
		{
			log = FileTool.Load(LOG + File.separator + region + "_rentout.log", "UTF-8");
		}
		// 2014/12/8 17:16:42
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");//小写的mm表示的是分钟
		
		java.util.Date latestdate = null;
		Date newest = null;
		
		if (log != null)
		{
			try {
				latestdate = sdf.parse(log.elementAt(0));
				latestdate = new Date(latestdate.getTime() - 1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String url = RENTOUT_URL + region;
		Vector<String> urls = new Vector<String>();
		
		Set<String> visited = new TreeSet<String>();
		urls.add(url);
		
		Parser parser = new Parser();
		boolean quit = false;
		
		while (urls.size() > 0)
		{
			// 解析页面
			url = urls.get(0);
			
			urls.remove(0);
			visited.add(url);
			
			String content = HTMLTool.fetchURL(url, "gb2312", "post");
			
			if (content == null)
			{
				continue;
			}
			try {
				
				parser.setInputHTML(content);
				parser.setEncoding("gb2312");
				// <dd class="info rel floatr">
				// <p class="title"><a href="/chuzu/3_153703104.htm" target="_blank" title="冉家坝龙山小学旁 光宇阳光地中海精装三房急售 无营业税拎包入住">冉家坝龙山小学旁 光宇阳光地中海精装三房急售 无营业税拎包入住</a></p>					
				// <p class="title"
				HasParentFilter parentFilter = new HasParentFilter(new AndFilter(new TagNameFilter("p"), new HasAttributeFilter("class", "title")));
				NodeFilter filter = new AndFilter(new TagNameFilter("a"), new AndFilter(new AndFilter(parentFilter, new HasAttributeFilter("target")), new HasAttributeFilter("href"))); 
				
				NodeList nodes = parser.extractAllNodesThatMatch(filter);
				
				if (nodes != null)
				{
					for (int n = 0; n < nodes.size(); n ++)
					{
						TagNode tn = (TagNode)nodes.elementAt(n);
						String purl = tn.getAttribute("href");
						if (purl.startsWith("/chuzu"))
						{
							String poi = parseRentOut("http://zu.cq.fang.com" + purl);
							if (poi != null)
							{
								// 获取时间
								int m = poi.indexOf("<TIME>");
								int k = poi.indexOf("</TIME>");
								
								if (m != -1 && k != -1)
								{
									assert(m < k);
									String tm = poi.substring(m + "<TIME>".length(), k);
									try {
										Date date = sdf.parse(tm);
										if (latestdate != null)
										{
											if (date.before(latestdate))
											{
												quit = true;
											}
											else if (newest == null)
											{
												newest = date;
											}
											else{
												if (newest.before(date))
													newest = date;
											}
												
										}
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									if (quit)
									{
										break;
									}
									else
									{
										synchronized(CQ_RENTOUT)
										{
											FileTool.Dump(poi, "D:\\出租.csv", "UTF-8");
										}
									}
								}
							}
							
							try {
								Thread.sleep(500 * ((int) (Math
									.max(1, Math.random() * 3))));
							} catch (final InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
				
				parser.reset();
				
				// <div class="fanye gray6">  <a class="pageNow">
				filter = new AndFilter(new TagNameFilter("div"), 
					new HasChildFilter(new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("class", "pageNow")))); 
				
				nodes = parser.extractAllNodesThatMatch(filter);
				
				if (nodes != null)
				{
					for (int nn = 0; nn < nodes.size(); nn ++)
					{
						NodeList cdl = nodes.elementAt(nn).getChildren();
						
						if (cdl == null)
							continue;
						
						for (int jj = 0; jj < cdl.size(); jj ++)
						{
							if (cdl.elementAt(jj) instanceof TagNode)
							{
								TagNode tni = (TagNode) cdl.elementAt(jj);
								String href = tni.getAttribute("href");
								if (tni.getTagName().equalsIgnoreCase("a") && tni.getAttribute("id") == null && href != null)
								{
									if (!visited.contains("http://esf.cq.fang.com" + href))
									{
										int kk = 0;
										for (; kk < urls.size(); kk ++)
										{
											if (urls.elementAt(kk).equalsIgnoreCase("http://esf.cq.fang.com" + href))
											{
												break;
											}
										}
										
										if (kk == urls.size())
											urls.add("http://esf.cq.fang.com" + href);
									}
								}
							}
						}						
					}
				}
				
				if (quit)
					break;
			}
			catch (ParserException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}
		
		synchronized(CQ_RENTOUT)
		{
			File f = new File(LOG + File.separator + region + ".log");
			f.delete();
			if (newest != null)
			{			
				FileTool.Dump(sdf.format(newest), LOG + File.separator + region + ".log", "UTF-8");
			}
		}
	}
	
	/* 抓取求租数据
	 * */
	public static String RENTAL_URL = "http://zu.cq.fang.com/qiuzu/h316/";
	
	public static void getRentalInfo()
	{
		// 首先加载
		Vector<String> log = null;
		synchronized(CQ_RENTINFO)
		{
			log = FileTool.Load(LOG + File.separator + "rental.log", "UTF-8");
		}
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");//小写的mm表示的是分钟
		
		java.util.Date latestdate = null;
		Date newest = null;
		
		if (log != null)
		{
			try {
				latestdate = sdf.parse(log.elementAt(0));
				latestdate = new Date(latestdate.getTime() - 1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String url = RENTAL_URL;
		Vector<String> urls = new Vector<String>();
		
		Set<String> visited = new TreeSet<String>();
		urls.add(url);
		
		Parser parser = new Parser();
		boolean quit = false;
		
		while (urls.size() > 0)
		{
			// 解析页面
			url = urls.get(0);
			
			urls.remove(0);
			visited.add(url);
			
			String content = HTMLTool.fetchURL(url, "gb2312", "post");
			
			if (content == null)
			{
				continue;
			}
			try {
				
				parser.setInputHTML(content);
				parser.setEncoding("gb2312");
				// <dd class="info rel floatr">
				// <p class="title"><a href="/qiuzu/3_153703104.htm" target="_blank" title="冉家坝龙山小学旁 光宇阳光地中海精装三房急售 无营业税拎包入住">冉家坝龙山小学旁 光宇阳光地中海精装三房急售 无营业税拎包入住</a></p>					
				// <p class="title"
				HasParentFilter parentFilter = new HasParentFilter(new AndFilter(new TagNameFilter("p"), new HasAttributeFilter("class", "title")));
				NodeFilter filter = new AndFilter(new TagNameFilter("a"), new AndFilter(new AndFilter(parentFilter, new HasAttributeFilter("target")), new HasAttributeFilter("href"))); 
				
				NodeList nodes = parser.extractAllNodesThatMatch(filter);
				
				if (nodes != null)
				{
					for (int n = 0; n < nodes.size(); n ++)
					{
						TagNode tn = (TagNode)nodes.elementAt(n);
						String purl = tn.getAttribute("href");
						if (purl.startsWith("/qiuzu"))
						{
							String poi = parseRental("http://zu.cq.fang.com" + purl);
							if (poi != null)
							{
								// 获取时间
								int m = poi.indexOf("<TIME>");
								int k = poi.indexOf("</TIME>");
								
								if (m != -1 && k != -1)
								{
									assert(m < k);
									String tm = poi.substring(m + "<TIME>".length(), k);
									try {
										Date date = sdf.parse(tm);
										if (latestdate != null)
										{
											if (date.before(latestdate))
											{
												quit = true;
											}
											else if (newest == null)
											{
												newest = date;
											}
											else{
												if (newest.before(date))
													newest = date;
											}
												
										}
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									if (quit)
									{
										break;
									}
									else
									{
										synchronized(CQ_RENTINFO)
										{
											FileTool.Dump(poi, "D:\\求租.csv", "UTF-8");
										}
									}
								}
							}
							
							try {
								Thread.sleep(500 * ((int) (Math
									.max(1, Math.random() * 3))));
							} catch (final InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
				
				parser.reset();
				
				// <div class="fanye gray6">  <a class="pageNow">
				filter = new AndFilter(new TagNameFilter("div"), 
					new HasChildFilter(new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("class", "pageNow")))); 
				
				nodes = parser.extractAllNodesThatMatch(filter);
				
				if (nodes != null)
				{
					for (int nn = 0; nn < nodes.size(); nn ++)
					{
						NodeList cld = nodes.elementAt(nn).getChildren();
						if (cld == null)
							continue;
						
						for (int jj = 0; jj < cld.size(); jj ++)
						{
							if (cld.elementAt(jj) instanceof TagNode)
							{
								TagNode tni = (TagNode) cld.elementAt(jj);
								String href = tni.getAttribute("href");
								if (tni.getTagName().equalsIgnoreCase("a") && tni.toPlainTextString().indexOf("下一页") == -1 &&  tni.toPlainTextString().indexOf("末页") == -1 && href != null)
								{
									if (!visited.contains("http://zu.cq.fang.com" + href))
									{
										int kk = 0;
										for (; kk < urls.size(); kk ++)
										{
											if (urls.elementAt(kk).equalsIgnoreCase("http://zu.cq.fang.com" + href))
											{
												break;
											}
										}
										
										if (kk == urls.size())
											urls.add("http://zu.cq.fang.com" + href);
									}
								}
							}
						}
					}
				}
				
				if (quit)
					break;
			}
			catch (ParserException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}
		
		synchronized(CQ_RENTINFO)
		{
			File f = new File(LOG + File.separator + "rental.log");
			f.delete();
			if (newest != null)
			{			
				FileTool.Dump(sdf.format(newest), LOG + File.separator + "rental.log", "UTF-8");
			}
		}
		
	}
	
	/*
	 * 新盘
	 *  本月开盘  http://newhouse.cq.fang.com/house/saledate/201502.htm
	 *  top100楼盘 http://newhouse.cq.fang.com/house/asp/trans/buynewhouse/default.htm
	 * */
	public static String NEWBUILDING_URL = "http://newhouse.cq.fang.com/house/saledate/";
	
	public static void getNewBuildingInfo(int year, int month)
	{
		String url = NEWBUILDING_URL;
		
		if (month < 10)
			url += year + "0" + month;
		else
			url += year + month;
		url += ".htm";
		
		Vector<String> urls = new Vector<String>();
		
		Set<String> visited = new TreeSet<String>();
		urls.add(url);
		
		Parser parser = new Parser();
		boolean quit = false;
		
		while (urls.size() > 0)
		{
			// 解析页面
			url = urls.get(0);
			
			urls.remove(0);
			visited.add(url);
			
			String content = HTMLTool.fetchURL(url, "gb2312", "post");

			if (content == null)
			{
				continue;
			}
			try {
				
				parser.setInputHTML(content);
				parser.setEncoding("gb2312");
				// <strong class="f14 fb_blue"><a href="http://shijicaifuxingzuo.fang.com/" target="_blank" title="世纪财富星座">世纪财富星座<
				// HasParentFilter parentFilter = new HasParentFilter(new AndFilter(new TagNameFilter("strong"), new HasAttributeFilter("class", "f14 fb_blue")));
				// NodeFilter filter = new AndFilter(new TagNameFilter("a"), new AndFilter(new AndFilter(parentFilter, new HasAttributeFilter("title")), new HasAttributeFilter("href"))); 
				// NodeFilter filter = new AndFilter(new TagNameFilter("a"), parentFilter);// new AndFilter(new AndFilter(parentFilter, new HasAttributeFilter("title")), new HasAttributeFilter("href")));
				
				int ss = content.indexOf("<strong class=\"f14 fb_blue\">");
				
				while (ss != -1)
				{
					int en = content.indexOf("</strong>", ss + "<strong class=\"f14 fb_blue\">".length());
					if (en != -1)
					{
						String sub = content.substring(ss, en);
						
						int rfs = sub.indexOf("href=\"");
						if (rfs != -1)
						{
							int rfe = sub.indexOf("\"", rfs + "href=\"".length());
							if (rfe != -1)
							{
								String purl = sub.substring(rfs + "href=\"".length(), rfe);
								String poi = parseNewBuilding(purl);
								if (poi != null)
								{
									synchronized(CQ_NEWHOUSE)
									{
										FileTool.Dump(poi, "D:\\新房.csv", "UTF-8");
									}
								}
							}
							else
								break;
						}
						else
							break;
					}
					else
						break;
					
					ss = content.indexOf("<strong class=\"f14 fb_blue\">", en + "</strong>".length());
					
					try {
						Thread.sleep(500 * ((int) (Math
							.max(1, Math.random() * 3))));
					} catch (final InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}				
				parser.reset();
				// <div class="fanye gray6">  <a class="pageNow">
				NodeFilter filter = new AndFilter(new TagNameFilter("div"), new HasParentFilter(new HasAttributeFilter("class", "searchListPage"))); 
				
				NodeList nodes = parser.extractAllNodesThatMatch(filter);
				
				if (nodes != null)
				{
					for (int nn = 0; nn < nodes.size(); nn ++)
					{
						Node ni = nodes.elementAt(nn);
						NodeList cld = ni.getChildren();
						if (cld != null)
						{
							for (int kkk = 0; kkk < cld.size(); kkk ++)
							{
								if (cld.elementAt(kkk) instanceof TagNode)
								{
									String href = ((TagNode)cld.elementAt(kkk)).getAttribute("href");
									if (href != null)
									{
										if (!href.startsWith("http://"))
										{
											if (href.startsWith("/house"))
												href = "http://newhouse.cq.fang.com" + href;
											else
												continue;
										}
										
										if (!visited.contains(href))
										{
											int kk = 0;
											for (; kk < urls.size(); kk ++)
											{
												if (urls.elementAt(kk).equalsIgnoreCase(href))
												{
													break;
												}
											}
											
											if (kk == urls.size())
												urls.add(href);
										}
									}
								}
							}
						}						
					}
				}
			}
			catch (ParserException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}
		
	}
	
	/* 求租 http://zu.cq.fang.com/qiuzu/
	 * */	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String url = "http://113.108.142.147:20035/emcpublish/ClientBin/Env-CnemcPublish-RiaServices-EnvCnemcPublishDomainService.svc/binary/GetProvincePublishLives?pid=1";//"http://113.108.142.147:20035/emcpublish/ClientBin/Env-CnemcPublish-RiaServices-EnvCnemcPublishDomainService.svc/binary/GetCityRealTimeAQIModelByCitycode?cityCode=150600";
		String co = HTMLTool.fetchURL(url, "UTF-8", "post");
		if (true)
		{
			String requestUrl = "http://113.108.142.147:20035/emcpublish/ClientBin/Env-CnemcPublish-RiaServices-EnvCnemcPublishDomainService.svc/binary/GetAreaAQIPublishLive"; //"http://113.108.142.147:20035/emcpublish/ClientBin/Env-CnemcPublish-RiaServices-EnvCnemcPublishDomainService.svc/binary/GetCityRealTimeAQIModelByCitycode";  
	        Map<String, Object> requestParamsMap = new HashMap<String, Object>();  
	        requestParamsMap.put("cityCode", "150600");  //cityCode=150600
	        BufferedWriter printWriter = null;  
	        Vector<Byte> responseResult = new Vector<Byte>();  
	        StringBuffer params = new StringBuffer();  
	        HttpURLConnection httpURLConnection = null;  
	        // 组织请求参数  
	        Iterator it = requestParamsMap.entrySet().iterator();  
	        while (it.hasNext()) {  
	            Map.Entry element = (Map.Entry) it.next();  
	            params.append(element.getKey());  
	            params.append("=");  
	            params.append(element.getValue());  
	            params.append("&");  
	        }  
	        if (params.length() > 0) {  
	            params.deleteCharAt(params.length() - 1);  
	        }  
	        String preq = "@GetAreaAQIPublishLivehttp://tempuri.org/@area";
	        String area = "北京市";
	        byte preqbytes[] = preq.getBytes();
	        byte areabytes[] = null;
	        int len = 0;
			
	        try {
				areabytes = area.getBytes("UTF-16");
				
				assert(areabytes.length % 2 == 0);
				
				for (int n = 0; n < areabytes.length; n += 2)
				{
					byte c = areabytes[n];
					areabytes[n] = areabytes[n + 1];
					areabytes[n + 1] = c;
				}
				
				if (areabytes[0] == (byte)0xFF && areabytes[1] == (byte)0xFE)
					len = areabytes.length - 2;
				else
					len = areabytes.length;
			
	        } catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        byte query[] = new byte[len + preqbytes.length + 3];
	        
	        int n = 0;
	        for (; n < preqbytes.length; n ++)
	        	query[n] = preqbytes[n];
	        
	        query[n ++] = (byte) 0xB7;
	        
	        int m = 0;
	        if (len < areabytes.length)
	        {
	        	m = 2;
	        }
	        query[n ++] = (byte) len;
	        
	        for (; m < areabytes.length; m ++)
	        {
	        	query[n ++] = areabytes[m];
	        }
	        
	        query[n ++] = (byte)0x01;
	        
	        for (n = 0; n < query.length; n ++)
	        {
	        	String hex = Integer.toHexString(query[n] & 0xFF);
	            if (hex.length() == 1)
	            {
	                hex = '0' + hex;
	            }
	            System.out.print(hex.toUpperCase() + " ");
	    	}
	        try {  
	            URL realUrl = new URL(requestUrl);  
	            // 打开和URL之间的连接  
	            httpURLConnection = (HttpURLConnection) realUrl.openConnection();  
	            // 设置通用的请求属性  
	            // httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.7 (compatible; MSIE 5.0; Windows NT; DigExt)");
	            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; rv:11.0) like Gecko");
	            httpURLConnection.setRequestProperty("accept", "*/*");  
	            httpURLConnection.setRequestProperty("Accept-Encoding", "identity");  
	            httpURLConnection.setRequestProperty("connection", "Keep-Alive");  
	            httpURLConnection.setRequestProperty("Content-Length", String  
	                    .valueOf(query.length));//params.length()));  
	            httpURLConnection.setConnectTimeout(20000);  
	            httpURLConnection.setReadTimeout(300000);
	            
	            httpURLConnection.setRequestProperty("Content-Type", "application/msbin1");
	            // 发送POST请求必须设置如下两行  
	            httpURLConnection.setDoOutput(true);  
	            httpURLConnection.setDoInput(true); 
	            // 获取URLConnection对象对应的输出流  
	            OutputStream os = httpURLConnection.getOutputStream();
	            os.write(query);
	            // flush输出流的缓冲  
	            // printWriter.flush();  
	            os.flush();
	            
	            // 根据ResponseCode判断连接是否成功  
	            int responseCode = httpURLConnection.getResponseCode();  
	            if (responseCode != 200) {  
	                log.error(" Error===" + responseCode);  
	            } else {  
	                log.info("Post Success!");  
	            }  
	            // 定义BufferedReader输入流来读取URL的ResponseData  
	            
	            // InputStreamReader isr = new InputStreamReader(  
	            //        httpURLConnection.getInputStream());
	            BufferedInputStream readr = new BufferedInputStream(httpURLConnection.getInputStream());

	            byte[] buffer = new byte[1024];

	            try {

		            int bytesRead = 0;
		           
		            //从文件中按字节读取内容，到文件尾部时read方法将返回-1
		            while ((bytesRead = readr.read(buffer)) != -1) {
		            	for (int mc = 0; mc < bytesRead; mc ++)
		            	{
		            		responseResult.add(buffer[mc]);
		            	}
		            }
		            
		            readr.close();
		            for (n = 0; n < responseResult.size(); n ++)
			        {
			        	String hex = Integer.toHexString(responseResult.get(n) & 0xFF);
			            if (hex.length() == 1)
			            {
			                hex = '0' + hex;
			            }
			            System.out.print(hex.toUpperCase() + " ");
			    	}
		            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream("D:\\GetAreaAQIPublishLive2.dat"));
		            for (n = 0; n < responseResult.size(); n ++)
		            {
		            	writer.write(responseResult.elementAt(n));
		            }
		            writer.close();
		            
	            } catch (Exception e) {  
	            	log.error("send post request error!" + e);  
	            } finally {  
	            	httpURLConnection.disconnect();  
	            } 
	      	} catch (IOException ex) {  
	                ex.printStackTrace();  
	        }  
	        log.info(responseResult.toString());  
		}

		getRentOutInfo(regions[0]);
		
		getRentalInfo();
		
		getNewBuildingInfo(2015, 1);
		
		getResoldApartmentInfo(regions[0]);
	}
}
