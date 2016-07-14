package com.svail.crawl.hospital.person;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.svail.util.FileTool;
import com.svail.util.HTMLTool;
//<POI><RID>9</RID><NAME>北京市大兴区精神病医院</NAME><URL>http://www.dxbughouse.com/</URL><TELEPHONE>010-61216048</TELEPHONE></POI>
public class BJDxbughouse {
	public static String URL = "http://www.dxbughouse.com/zhuajia/";
	public static void parseDoctor(String url, int hospital)
	{
		String content = HTMLTool.fetchURL(url, "gb2312", "get");

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
			// lass="cPic"
			NodeFilter helper1 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "cPic"));
			String poi = null;
			NodeList cld = parser.extractAllNodesThatMatch(helper1);
			if(cld != null && cld.size() > 0)
			{
				String str = ((TagNode) cld.elementAt(0)).toPlainTextString().replace("&amp;", "").replace(" ", "").replace("\t", "").replace("\n", "").replace("\r", "").trim();
				if (str.isEmpty())
					return;
				
				poi = "<NAME>" + str + "</NAME>";
			}
			else
				return;
			
			/* class="content"
			 * */
			parser.reset();
			helper1 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "content"));
			cld = parser.extractAllNodesThatMatch(helper1);
			
			if(cld != null && cld.size() > 0)
			{
				String str = ((TagNode) cld.elementAt(0)).toPlainTextString().replace("&amp;", "").replace("&nbsp;", "").replace(" ", "").replace("\t", "").replace("\n", "").replace("\r", "").trim();

				if (!str.isEmpty())
					poi += "<INTRODUCTION>" + str + "</INTRODUCTION>";
			}
			if (poi != null)
			{
				poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + poi + "<URL>" + url + "</URL></DOCTOR>";
				
				FileTool.Dump(poi, "D:\\temp\\医院\\BJDxbughouse.csv", "UTF-8");				
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
			NodeFilter filter = new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href"));
			HasParentFilter parentFilter = new HasParentFilter(new AndFilter(new TagNameFilter("li"), new HasAttributeFilter("class", "nt_title")));
			NodeList cld = parser.extractAllNodesThatMatch(new AndFilter(filter, parentFilter));
			if(cld != null && cld.size() > 0)
			{
				for(int k = 0; k < cld.size(); k++)
				{					
					TagNode t = (TagNode) cld.elementAt(k);
					parseDoctor("http://www.dxbughouse.com" + t.getAttribute("href"), hospital);
					
				}
			}		
			
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 
	}
	
	public static void main(String[] args) throws Exception {

		parsePartmentSheet(URL, 9);
		
	}
}
