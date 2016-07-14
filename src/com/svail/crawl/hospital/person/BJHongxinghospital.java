package com.svail.crawl.hospital.person;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.svail.util.FileTool;
import com.svail.util.HTMLTool;

// <POI><RID>5</RID><NAME>北京市大兴区红星医院</NAME><ADDRESS><URL>http://hongxinghospital.com</URL><TELEPHONE>010-67992043</TELEPHONE></POI>
public class BJHongxinghospital {
	public static String URLS[] = {
		"http://hongxinghospital.com/hospital/class/index.php?page=1&catid=0&myord=uptime&myshownums=&key=",
		"http://hongxinghospital.com/hospital/class/index.php?page=2&catid=0&myord=uptime&myshownums=&key=",
		"http://hongxinghospital.com/hospital/class/index.php?page=3&catid=0&myord=uptime&myshownums=&key="
	};
	public static void parseDoctor(String url, int hospital)
	{
		String content = HTMLTool.fetchURL(url, "UTF-8", "get");

		Parser parser = new Parser();
		if (content == null)
		{
			return;
		}
		
		try {
			
			parser.setInputHTML(content);
			parser.setEncoding("UTF-8");
			
			/*
			<div class="right">
			<div class="line1">医生名称：邸保林</div>
			<div class="line2"><font class="lm">所在科室：</font>骨伤科</div>
			<div class="line2"><font class="lm">专家职称：</font>副主任医师</div>
			<div class="line2"><font class="lm">专家专长：</font>头痛、头晕、失眠、颈肩痛、腰腿痛、四肢麻木无力、关节疼痛、功能障碍等</div>
			<div class="line2"><font class="lm">门诊时间：</font></div>
			<div class="line2"><font class="lm">专家介绍：</font><br />

			*/
			// <div class="right">

			NodeFilter helper1 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "right"));
			String poi = null;
			NodeList cld = parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("div"), new HasParentFilter(helper1)));
			
			if(cld != null && cld.size() > 0)
			{
				
				for (int n = 0; n < cld.size(); n ++)
				{
					String str = ((TagNode) cld.elementAt(n)).toPlainTextString().replace("&amp;", "").trim();
					
					if (str.startsWith("医生名称："))
					{
						str = str.substring("医生名称：".length());
						if (!str.isEmpty())
							poi = "<NAME>" + str + "</NAME>";
						else
							return;
					}
					else if (str.startsWith("专家职称："))
					{
						str = str.substring("专家职称：".length());
						if (!str.isEmpty())
							poi += "<POSITION>" + str + "</POSITION>";
					}	
					else if (str.startsWith("所在科室："))
					{
						str = str.substring("所在科室：".length());
						if (!str.isEmpty())
							poi += "<PARTMENT>" + str + "</PARTMENT>";
					}else if (str.startsWith("专家专长："))
					{
						str = str.substring("专家专长：".length());
						if (!str.isEmpty())
							poi += "<SKILL>" + str + "</SKILL>";
					}else if (str.startsWith("专家介绍："))
					{
						str = str.substring("专家介绍：".length()).replace("&amp;", "").replace("&nbsp;", "").replace("\t", "").replace("\r", "").replace("\n", "").trim();;
						if (!str.isEmpty())
							poi += "<INTRODUCTION>" + str.replace("<?xml:namespace prefix = \"st1\" />", "").replace("<?xml:namespace prefix = \"o\" />","") + "</INTRODUCTION>";
					}
				}
			}
			
			
			if (poi != null)
			{
				poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + poi + "<URL>" + url + "</URL></DOCTOR>";
				
				FileTool.Dump(poi, "D:\\temp\\医院\\BJHongxinghospital.csv", "UTF-8");				
			}
			
			try {
				Thread.sleep(500 * ((int) (Math
					.max(1, Math.random() * 3))));
			} catch (final InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 
	}
	
	public static void parsePartmentSheet(String url, int hospital)
	{
		String content = HTMLTool.fetchURL(url, "UTF-8", "get");
		// 该网页有问题
		
		Parser parser = new Parser();
		if (content == null)
		{
			return;
		}
		
		try {
			
			parser.setInputHTML(content);
			parser.setEncoding("UTF-8");

			// class="abcd
			NodeList cld = parser.extractAllNodesThatMatch(new StringFilter("详细介绍"));
			if(cld != null)
			{
				for(int k = 0; k < cld.size(); k++)
				{					
					TagNode parnode = (TagNode) cld.elementAt(k).getParent();
					String turl = parnode.getAttribute("href");
					
					if (turl != null)
					{
						// ../../hospital/html/?28.html
						parseDoctor("http://hongxinghospital.com/" + turl.substring("../../".length()), hospital);
					}
				}
			}		
			
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 
	}
	
	public static void main(String[] args) throws Exception {

		for (int n = 0; n < URLS.length; n += 2)
		{
			parsePartmentSheet(URLS[n], 5);
		}
	}
}
