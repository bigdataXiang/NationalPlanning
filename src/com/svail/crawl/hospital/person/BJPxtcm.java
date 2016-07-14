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

// <POI><RID>6</RID><NAME>北京普祥中医肿瘤医院</NAME><URL>http://www.pxtcm.com/</URL><TELEPHONE>400-006-5262</TELEPHONE></POI>
public class BJPxtcm {
	
		public static String URLS[] = {
		"http://www.pxtcm.com/zhuanjia.asp?page=1",
		"http://www.pxtcm.com/zhuanjia.asp?page=2"
	};
	public static String parseDoctor(String url, int hospital)
	{
		String content = HTMLTool.fetchURL(url, "gb2312", "get");

		Parser parser = new Parser();
		if (content == null)
		{
			return null;
		}
		
		try {
			
			parser.setInputHTML(content);
			parser.setEncoding("gb2312");
			
			
			// <div id="content"

			NodeFilter helper1 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("id", "content"));
			String poi = null;
			NodeList cld = parser.extractAllNodesThatMatch(helper1);
			
			if(cld != null && cld.size() > 0)
			{
				String str = ((TagNode) cld.elementAt(0)).toPlainTextString().replace("&amp;", "").trim();
				str = str.replace("&nbsp;", "").replace("\t", "").replace("\r", "").replace("\n", "").replace("　", "").trim();;
				
				return str;
			}
			
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 
		
		return null;
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
			
			parser.setInputHTML(content);
			parser.setEncoding("gb2312");
			/*
			 * <ul class="u4">
                    	
                    	<li>
                        	<div><a href="zhuanjia_show.asp?id=37" target="_blank">林洪生 主任医师 博士生导师</a></div>
                        	<img src="upfile/image/20141201/20141201114613621362.jpg"  width="66" height="81"/>
                        	<span class="su4"><span style="font-family:'Microsoft YaHei';">&nbsp;&nbsp;&nbsp; 中医药管理局肿瘤重点学科带头人、现任中国中医科学院广安门医院肿瘤科主任，享受国务院政府津贴。</span></span>
                            <div class="clear"></div>
                        </li>

			 * */
			// class="abcd
			
			NodeFilter baseFilter = new AndFilter(new TagNameFilter("ul"), new HasAttributeFilter("class", "u4"));
			// NodeFilter baseFilter = new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href"));
			HasParentFilter parentFilter = new HasParentFilter(new AndFilter(new TagNameFilter("div"), new HasParentFilter(new TagNameFilter("li"))));
			
			NodeList cld = parser.extractAllNodesThatMatch(baseFilter);
			if(cld != null)
			{
				cld = cld.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href")), true);
				
				for(int k = 0; k < cld.size(); k++)
				{					
					TagNode parnode = (TagNode) cld.elementAt(k);
					String turl = parnode.getAttribute("href");
					String str = parnode.toPlainTextString().trim();
					String toks[] = str.split(" ");
					String poi = "<NAME>" + toks[0] + "</NAME>";
					
					for (int m = 1; m < toks.length; m ++)
					{
						if (!toks[m].isEmpty())
						{
							if (!poi.endsWith(">"))
								poi += "," + toks[m];
							else
								poi += "<POSITION>" + toks[m];
						}
					}
					if (!poi.endsWith(">"))
						poi += "</POSITION>";
					if (turl != null)
					{
						String intro = parseDoctor("http://www.pxtcm.com/" + turl, hospital);
						
						if (intro != null)
							poi += "<INTRODUCTION>" + intro + "</INTRODUCTION>";
						
					}
					
					if (poi != null)
					{
						poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + poi + "<URL>" + url + "</URL></DOCTOR>";
						
						FileTool.Dump(poi, "D:\\temp\\医院\\BJPxtcm.csv", "UTF-8");				
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

		for (int n = 0; n < URLS.length; n ++)
		{
			parsePartmentSheet(URLS[n], 5);
		}
	}
}
