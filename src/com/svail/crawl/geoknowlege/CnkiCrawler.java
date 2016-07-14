package com.svail.crawl.geoknowlege;

import java.io.File;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.svail.util.FileTool;
import com.svail.util.HTMLTool;

public class CnkiCrawler {

	public static String geocnki = null;
	public static String geourl = null;
	public static String NAME = null;
	
	// "http://www.cnki.com.cn/Journal/A-A5-DLXB.htm"
	public class CnkiPoi
	{
		public String title = null;
		public String linkUrl = null;
		public String authors = null;
		public String abstractw = null;
		public String keywords = null;
		public String orgnization = null;
		public String publish = null;
		
		public CnkiPoi()
		{			
		};
		public String toString()
		{
			String str = null;
			if (title != null)
			{
				if (str == null)
					str = "";
				str += "<TITLE>" + title + "</TITLE>";
				
			}
			if (authors != null)
			{
				if (str == null)
					str = "";
				str += "<AUTHORS>" + authors + "</AUTHORS>";
				
			}
			if (orgnization != null)
			{
				if (str == null)
					str = "";
				str += "<ORGNIZATION>" + orgnization + "</ORGNIZATION>";
			}
			if (abstractw != null)
			{
				if (str == null)
					str = "";
				str += "<ABSTRACT>" + abstractw + "</ABSTRACT>";
				
			}
			
			if (keywords != null)
			{
				if (str == null)
					str = "";
				str += "<KEYWORDS>" + keywords + "</KEYWORDS>";
				
			}
			
			if (publish != null)
			{
				if (str == null)
					str = "";
				str += "<PUBLISHINFO>" + publish + "</PUBLISHINFO>";
				
			}
			
			if (linkUrl != null)
			{
				if (str == null)
					str = "";
				str += "<URL>" + linkUrl + "</URL>";
				
			}
			
			if (str != null)
			{
				str = "<POI>" + str + "</POI>";
				str = str.replace("\n", "").replace("\t", "").replace("\r", "");
				
			}
			
			return str;
		}
	};
	
	public static CnkiPoi parsePoi(String pageUrl)
	{
		if (pageUrl == null)
			return null;
		System.out.println(pageUrl);
		CnkiCrawler cnki = new CnkiCrawler();
		String html = HTMLTool.fetchURL(pageUrl, "utf-8", "post");
		if (html == null)
			return null;
		
		Document doc = Jsoup.parse(html);
		CnkiPoi poi =  cnki.new CnkiPoi();
		poi.linkUrl = pageUrl;
		
		Elements es = doc.getElementsByAttributeValue("class", "xx_title");
		
		if (es.size() > 0)
		{
			poi.title = StringEscapeUtils.unescapeHtml(es.get(0).text());
		}
		
		es = doc.getElementsByAttributeValue("id", "content");
		if (es.size() > 0)
		{
			String content = StringEscapeUtils.unescapeHtml(es.get(0).text());
			
			int n = content.indexOf("加入收藏 投稿");
			if (n != -1)
			{
				poi.publish = content.substring(0, n);
				
				int m = content.indexOf(poi.title);
				
				if (m != -1)
				{
					content = content.substring(m + poi.title.length());
					
					content = content.replace("【摘要】：", ";分割;【摘要】").replace("【作者单位】：", ";分割;【作者单位】");
					content = content.replace("【关键词】：", ";分割;【关键词】").replace("【分类号】：", ";分割;【分类号】");
					content = content.replace("【基金】：", ";分割;【基金】").replace("【正文快照】：", ";分割;【正文快照】");
					
					String toks[] = content.split(";分割;");
					
					boolean noAuthor = false;
					for (int k = 0; k < toks.length; k ++)
					{
						if (toks[k].startsWith("【摘要】"))
						{
							poi.abstractw = toks[k].replace("【摘要】", "");
							if (k == 0)
								noAuthor = true;
						}
						else if (toks[k].startsWith("【作者单位】"))
						{
							poi.orgnization = toks[k].replace("【作者单位】", "");
							if (k == 0)
								noAuthor = true;
						}
						else if (toks[k].startsWith("【关键词】"))
						{
							poi.keywords = toks[k].replace("【关键词】", "");
							if (k == 0)
								noAuthor = true;
						}
					}
					
					if (!noAuthor)
					{
						String sas[] = toks[0].split(" ");
						
						if (sas.length > 0)
						{
							poi.authors = sas[0];
							
							for (int k = 1; k < sas.length; k ++)
								poi.authors += ";" + sas[k];
						}
					}
				}
			}
			// System.out.println(content);
			
		}
		if (poi.title == null)
			return null;
		return poi;
	};

	public static void parsePage(String pageUrl)
	{
		String html = HTMLTool.fetchURL(pageUrl, "utf-8", "post");
		
		if (html != null)
		{
			Document doc = Jsoup.parse(html);
			
			Elements es = doc.getElementsByAttributeValue("class", "zt_name");
			
			for (int n = 0; n < es.size(); n ++)
			{
				String page = es.get(n).attr("href");
				if (page.indexOf("/Article/") != -1)
				{
					CnkiPoi poi = parsePoi("http://www.cnki.com.cn" + page);
					if (poi != null)
						FileTool.Dump(poi.toString(), geocnki + File.separator + NAME + ".txt", "utf-8");
				}
			}
		}
		else
		{
			FileTool.Dump(pageUrl, geocnki + File.separator + "failedPage.log", "utf-8");
		}
	}
	
	public static void parsePagesYear(String pageUrl)
	{
		String html = HTMLTool.fetchURL(pageUrl, "utf-8", "post");
		
		if (html != null)
		{
			Document doc = Jsoup.parse(html);
			
			Elements es = doc.getElementsByAttributeValue("class", "content_gray02");
			
			for (int n = 0; n < es.size(); n ++)
			{
				String t[] = es.get(n).attr("href").split("-");
				try {
					Thread.sleep(800 * ((int) (Math.max(1, Math.random() * 4))));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (t[t.length - 1].indexOf(".htm") != -1)
				{
					String ts = t[t.length - 1].replace(".htm", "");
					
					if (ts.length() == 2) // 期
					{
						parsePage("http://www.cnki.com.cn" + es.get(n).attr("href"));
					}
				}
			}
		}
		else
			FileTool.Dump(pageUrl, geocnki + File.separator + "failedYear.log", "utf-8");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int CUR = 0;
		geocnki = "D:\\Temp\\crawldb\\geocnki";
		geourl = "D:\\Temp\\crawldb\\Cnki";
		
		String volumes[] = {
			"H-H2",
			"J-J2",
			"J-J3",
			"J-J4",
			"J-J7",
		};
		
		for (int cnt = 0; cnt < volumes.length; cnt ++)
		{
			NAME = volumes[cnt];
		
			Vector<String> pos = FileTool.Load(geocnki + File.separator + "日志.log", "utf-8");
			
			if (pos != null && pos.size() > 0)
			{
				int s = pos.elementAt(0).indexOf("<INDEX>");
				if (s != -1)
				{
					int e = pos.elementAt(0).indexOf("</INDEX>");
					if (e != -1)
					{
						CUR = Integer.parseInt(pos.elementAt(0).substring(s + "<INDEX>".length(), e));						
					}
				} 
			}
			
			Vector<String> urls = FileTool.Load(geourl + File.separator + NAME + ".txt", "gbk");
					
			for (int CNT = 0; CNT < urls.size(); CNT ++)
			{
				String toks[] = urls.get(CNT).replace("\"", "").split(",");
				String html = HTMLTool.fetchURL("http://www.cnki.com.cn" + toks[0], "utf-8", "post");
				if (html == null)
				{
					System.out.println("URL: " + "http://www.cnki.com.cn" + toks[0] + " is not available");
					continue;
				}
				
				Document doc = Jsoup.parse(html);
				
				Elements es = doc.getElementsByAttributeValue("class", "content_gray02");
				
				for (int n = 0; n < es.size(); n ++)
				{
					String t[] = es.get(n).attr("href").split("-");
					
					if (t[t.length - 1].indexOf(".htm") != -1)
					{
						String ts = t[t.length - 1].replace(".htm", "");
						
						if (ts.length() == 2) // 期
						{
							try {
								Thread.sleep(800 * ((int) (Math.max(1, Math.random() * 4))));
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							parsePage("http://www.cnki.com.cn" + es.get(n).attr("href"));
						}	
						else // 年
						{
							try {
								Thread.sleep(800 * ((int) (Math.max(1, Math.random() * 4))));
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							parsePagesYear("http://www.cnki.com.cn" + es.get(n).attr("href"));
						}
					}
				}
			}
		}
	}	
}
