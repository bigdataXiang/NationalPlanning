package com.svail.crawl.hospital.person;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

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
//<POI><RID>11</RID><NAME>首都医科大学附属北京潞河医院</NAME><URL>http://www.luhehospital.com</URL><TELEPHONE>010-69543901</TELEPHONE></POI>

public class BJLuhehospital {
	public static String URL = "http://www.luhehospital.com/zjjs.asp?classid=1";
	public static void parseDoctor(String url, int hospital)
	{
		String content = HTMLTool.fetchURL(url, "gb2312", "get");

		Parser parser = new Parser();
		if (content == null)
		{
			return;
		}
		
		try {
			parser.setInputHTML(content);
			parser.setEncoding("gb2312");
			
			/* &gt;&gt;
			 * */
			
			NodeList cld = parser.extractAllNodesThatMatch(new StringFilter("&gt;&gt;"));
			String partment = null;
			if(cld != null && cld.size() > 0)
			{
				TagNode tgd = (TagNode) cld.elementAt(0).getParent();
				String str = tgd.toPlainTextString().replace("&amp;", "").replace("&nbsp;",  "").replace(" ", "").replace("\t", "").replace("\n", ";").replace("\r", "").trim();
				String toks[] = str.split("&gt;&gt;");
				for (int m = toks.length; m > 0; m -- )
				{
					if (!toks[m - 1].isEmpty())
					{
						partment = toks[m - 1];
						break;
					}
				}
			}
			parser.reset();
			cld = parser.extractAllNodesThatMatch(new StringFilter("姓名："));
			String poi = null;
			if(cld != null && cld.size() > 0)
			{
				TagNode tgd = (TagNode) cld.elementAt(0).getParent();
				String str = tgd.toPlainTextString().replace("&amp;", "").replace("&nbsp;",  "").replace(" ", "").replace("\t", "").replace("\n", ";").replace("\r", "").trim();
				
				System.out.println(str);
				// 姓名：王旭红;职位：副主任医师;出诊时间：周一全天、周二下午;挂诊费：7元;特长：糖尿病、甲亢、甲减、骨质疏松、痛风等内分泌疾病;
				
				String toks[] = str.split(";");
				for (int n = 0; n < toks.length; n ++)
				{
					if (toks[n].startsWith("姓名："))
					{
						str = toks[n].substring("姓名：".length());
						if (!str.isEmpty())
							poi = "<NAME>" + str + "</NAME>";
						else
							return;
					}
					else if (toks[n].startsWith("职位："))
					{
						str = toks[n].substring("职位：".length());
						if (!str.isEmpty())
							poi += "<POSITION>" + str + "</POSITION>";
					}	
					else if (toks[n].startsWith("特长："))
					{
						str = toks[n].substring("特长：".length());
						if (!str.isEmpty())
							poi += "<SKILL>" + str + "</SKILL>";
					}
				}
			}
			if (poi == null)
				return;
			
			parser.reset();
			NodeFilter filter = new AndFilter(new TagNameFilter("span"), new HasAttributeFilter("style", "font-family:宋体;"));
			
			cld = parser.extractAllNodesThatMatch(filter);
			if(cld != null && cld.size() > 0)
			{
				String str = cld.elementAt(0).getParent().toPlainTextString();
				str = str.replace("&amp;", "").replace("&nbsp;",  "").replace(" ", "").replace("\t", "").replace("\n", "").replace("\r", "").trim();
				if (!str.isEmpty())
					poi += "<INTRODUCTION>" + str + "</INTRODUCTION>";
			}	
			
			if (poi != null)
			{
				if (partment != null)
					poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + poi + "<PARTMENT>" + partment + "</PARTMENT><URL>" + url + "</URL></DOCTOR>";
				else
					poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + poi + "<URL>" + url + "</URL></DOCTOR>";
				FileTool.Dump(poi, "D:\\temp\\医院\\BJLuhehospital.csv", "UTF-8");				
			}
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 
	}
	
	public static void parsePartmentSheet(String url, int hospital)
	{
		String content = HTMLTool.fetchURL(url, "gb2312", "get");
		// 该网页有问题
		
		Parser parser = new Parser();
		if (content == null)
		{
			return;
		}
		
		try {
			/* int ins =  content.indexOf("<!---------------------content--------------------------------->");
			if (ins != -1)
			{
				content = content.substring(ins + "<!---------------------content--------------------------------->".length());
				ins =  content.indexOf("<!---------------------content--end------------------------------->");
				
				if (ins != -1)
					content = content.substring(0, ins);
				
				else 
					content = null;
			}
			else content = null;
			
			if (content == null)
				return;
			
			 */
			parser.setInputHTML(content);
			parser.setEncoding("gb2312");
			
			// class="linchuang_ul
			// <option value="/zjjs.asp?nowpage=3&lan=1&classid=1"

			NodeFilter filter = new AndFilter(new TagNameFilter("option"), new HasAttributeFilter("value"));
			NodeList cld = parser.parse(filter);
			Vector<String> pages = new Vector<String>();
			if(cld != null && cld.size() > 0)
			{
				TagNode tdg = (TagNode) cld.elementAt(cld.size() - 1);
				String str = tdg.getAttribute("value");
				int ins = str.indexOf("nowpage=");
				if (ins != -1)
				{
					String fore = str.substring(0, ins + "nowpage=".length()), tail = "";
					
					str = str.substring(ins + "nowpage=".length());
					
					ins = str.indexOf("&");
					if (ins != -1)
					{	
						tail = str.substring(ins);
						str = str.substring(0, ins);					
					}
					
					ins = Integer.parseInt(str);
					
					for (int n = 0; n < ins; n ++)
					{
						pages.add(fore + (n + 1) + tail);
					}	
				}
				
			}	
			else
				pages.add(url);
			
			for (int n = 0; n < pages.size(); n ++)
			{
				content = HTMLTool.fetchURL("http://www.luhehospital.com" + pages.elementAt(n), "gb2312", "get");
				if (content == null)
				{
					continue;
				}
				
				parser.setInputHTML(content);
				parser.reset();
				//<td width="86" class="bk_08"><a href=
				filter = new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href"));
				AndFilter parent = new AndFilter(new TagNameFilter("td"), new AndFilter(new HasAttributeFilter("width", "86"), new HasAttributeFilter("class", "bk_08")));
				
				cld = parser.extractAllNodesThatMatch(new AndFilter(filter, new HasParentFilter(parent)));
				if (cld != null)
				{
					for (int m = 0; m < cld.size(); m ++ )
					{
						TagNode tnd = (TagNode) cld.elementAt(m);
						
						String turl = tnd.getAttribute("href");
						if (turl != null)
						{
							parseDoctor("http://www.luhehospital.com/" + turl, hospital);
						}
					}
				}
			}
			
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 
	}
	
	public static void parseFirstPartmentSheet(String url, int hospital)
	{
		String content = HTMLTool.fetchURL(url, "gb2312", "get");
		// 该网页有问题
		
		Parser parser = new Parser();
		if (content == null)
		{
			return;
		}
		
		try {
			/* int ins =  content.indexOf("<!---------------------content--------------------------------->");
			if (ins != -1)
			{
				content = content.substring(ins + "<!---------------------content--------------------------------->".length());
				ins =  content.indexOf("<!---------------------content--end------------------------------->");
				
				if (ins != -1)
					content = content.substring(0, ins);
				
				else 
					content = null;
			}
			else content = null;
			
			if (content == null)
				return;
			*/
			 
			parser.setInputHTML(content);
			parser.setEncoding("gb2312");
			
			//  <td align="left" style="padding-left:30px;"><a href=
			NodeFilter filter = new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href"));
			AndFilter parent = new AndFilter(new TagNameFilter("td"), new AndFilter(new HasAttributeFilter("style", "padding-left:30px;"), new HasAttributeFilter("align", "left")));
			
			NodeList cld = parser.parse(new AndFilter(filter, new HasParentFilter(parent)));
			if(cld != null && cld.size() > 0)
			{
				for(int k = 0; k < cld.size(); k++)
				{					
					TagNode parnode = (TagNode) cld.elementAt(k);
					String turl = parnode.getAttribute("href");
					parsePartmentSheet("http://www.luhehospital.com/" + turl, hospital);
				}
			}		
			
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 
	}
	public static void main(String[] args) throws Exception {

		parseFirstPartmentSheet(URL, 11);
		
	}
}
