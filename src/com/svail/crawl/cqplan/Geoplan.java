package com.svail.crawl.cqplan;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Vector;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.svail.util.FileTool;
import com.svail.util.HTMLTool;

public class Geoplan {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		intitate();
	}

	public static String PROCLAMATION_URL = "http://www.cqupb.gov.cn/wsfw/wsfw_1S3Z_listDetails.aspx?classname=%E5%BB%BA%E8%AE%BE%E5%B7%A5%E7%A8%8B%E9%80%89%E5%9D%80%E6%84%8F%E8%A7%81%E4%B9%A6";
	public static void intitate()
	{
		final WebClient webClient = new WebClient();

		try
		{
			final HtmlPage page = webClient.getPage(PROCLAMATION_URL);
		    
			String content = page.asXml();
			int n = content.indexOf("<span id=\"lblPageCount\">");
		    int pages = 0;
			if (n != -1)
		    {
		    	int m = content.indexOf("</span>", n);
		    	String str = content.substring(n + "<span id=\"lblPageCount\">".length(), m).replace("\r", "").replace("\n", "").trim();
		    	try {
		    		pages = Integer.parseInt(str);
		    	}
		    	catch( NumberFormatException e)
		    	{	    		
		    	}	    	
		    }
			parsePage(page, pages);
		}
		catch (FailingHttpStatusCodeException e1)
		{}
		catch (MalformedURLException e2)
		{}
		catch (IOException e3)
		{}
	}
	public static void parsePage(HtmlPage page, int pageCount) throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{
		Parser parser = new Parser();
		try {
					
			parser.setInputHTML(page.asXml());
			parser.setEncoding("gb18030");

			// <img src="images/074.gif" width="7" height="7">
			
			NodeFilter filter = new AndFilter(new TagNameFilter("img"), new HasAttributeFilter("src", "images/074.gif"));
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
			
			for (int n = 0; n < nodes.size(); n ++)
			{
				Node no = nodes.elementAt(n);
				Node sibling = no.getNextSibling();
				String time = null;
				String tur = null;
				while (sibling != null)
				{
					String title = sibling.toPlainTextString().trim();
					int i = title.indexOf("日");
					if (i != -1)
					{
						time = title.substring(0, i + 1);
						tur = ((TagNode) sibling).getAttribute("href");
						
						break;
					}
					sibling = sibling.getNextSibling();
				}
	
				if (tur != null)
				{
					Vector<String> pois = parsePois("http://www.cqupb.gov.cn/" + tur);	
					for (int i = 0; i < pois.size(); i ++)
					{
						FileTool.Dump("<POI>" + pois.elementAt(i) + "<TIME>" + time + "</TIME><URL>" + "http://www.cqupb.gov.cn/" + tur + "</URL></POI>", "D:\\geoplan.csv", "UTF-8");
					}
				}
				
			}
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		if (pageCount == 0)
			return;
		
		//div[@id='blog_owner_name']
		List<HtmlAnchor> pages = (List<HtmlAnchor>) page.getByXPath("//a[@id='btnNext']");

		for (int pai = 0; pai < pages.size(); pai ++)
		{
			HtmlPage issuePage = pages.get(pai).click();
			parsePage(issuePage, pageCount - 1);
			try {
				Thread.sleep(1000 * ((int) (Math.max(1, Math.random() * 4))));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static Vector<String> parsePois(String url)
	{
		String content = HTMLTool.fetchURL(url, "gb2312", "post");
		Parser parser = new Parser();
		if (content == null)
		{
			return null;
		}
		
		Vector<String> pois = new Vector<String>();
		
		String poi = null;
		try {
			
			parser.setInputHTML(content);
			parser.setEncoding("gb2312");
			// tr style=
			NodeFilter filter = new AndFilter(new TagNameFilter("tr"), new HasAttributeFilter("style"));
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
			
			Vector<String> items = new Vector<String>();
			Vector<String> titles = new Vector<String>();
			
			if (nodes != null)
			{
				for (int n = 0; n < nodes.size(); n ++)
				{
					
					if (nodes.elementAt(n) instanceof TagNode)
					{
						NodeList child = nodes.elementAt(n).getChildren();
						
						int type = 0;
						int loc = 0;
						boolean isTitle = false;
						for (int m = 0; m < child.size(); m ++)
						{
							Node ni = child.elementAt(m);
							if (ni instanceof TagNode)
							{
								TagNode tni = (TagNode) ni;
								
								String ct = tni.toPlainTextString().trim();
								int ix = ct.indexOf("<?xml:namespace");
								while (ix != -1)
								{
									int xe = ct.indexOf("/>");
									if (xe != -1)
									{
										String h = ct.substring(0, ix);
										String t = ct.substring(xe + "/>".length());
										ct = h + t;
										
										ix = ct.indexOf("<?xml:namespace");
										
									}
									else
										break;
								}
								items.add(ct.replace(" ", "").replace("\r", "").replace("　", "").replace("\n", ""));
								
								if (ct.indexOf("序号") != -1)
								{
									isTitle = true;
									type = 1;
								}
								
								if (ct.indexOf("位置") != -1)
								{
									type = 2;
									loc = items.size() - 1;
								}
							}
						}
						
						if (isTitle)
						{
							titles.clear();
							titles.addAll(items);
							items.clear();
						}
						else
						{
							poi = "<ITME1>" + items.elementAt(1) + "@" + titles.elementAt(2) + "<ITEM1>";
							if (items.size() == titles.size())
							{
								items.remove(0);
							}
							
							for(int m = 2; m < items.size(); m ++)
							{
								poi += "<ITME" + m + ">" + items.elementAt(m) + "@" + titles.elementAt(m + 1) + "<ITEM" + m + ">";
							}
							pois.add(poi);
							items.clear();
						}						
					}
				}
			}

			return pois;

		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		return pois;
	}

}
