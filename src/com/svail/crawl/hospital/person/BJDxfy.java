package com.svail.crawl.hospital.person;

import java.io.UnsupportedEncodingException;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.svail.util.FileTool;
import com.svail.util.HTMLTool;

// 北京市大兴区妇幼保健院
public class BJDxfy {
	
	private static String HOSURL = "http://www.dxfy.com/er1.asp?lei=%D7%A8%BC%D2%BD%E9%C9%DC&tu=7";
	
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
			
			String poi = null;
			NodeList cld = parser.extractAllNodesThatMatch(new StringFilter("业务专长："));
			if(cld != null && cld.size() > 0)
			{
				TagNode parnode = (TagNode) cld.elementAt(0).getParent().getParent();
				String str = parnode.toPlainTextString().replace(" ", "").replace("业务专长：", ";").replace(".hg{line-height:180%;}", "").replace("&nbsp", ";").replace("\t", "").replace("\r", "").replace("\n", "").trim();
				
				String toks[] = str.split(";");
				int k = 0;
				// 副主任医师 主任医师
				for (int n = 0; n < toks.length; n ++)
				{
					if (!toks[n].isEmpty())
					{
						if (k == 0)
							poi += "<NAME>" + toks[n] + "</NAME>";
						k ++;
					}
				}
				if (poi == null)
					return;
				
				if (str.indexOf("副主任医师") != -1)
					poi += "<POSITION>副主任医师</POSITION>";
				else
					poi += "<POSITION>主任医师</POSITION>";
				// 儿童保健科 眼科 妇产科 儿科 乳腺外科 妇女保健科
				poi += "<SKILL>" + cld.elementAt(0).toPlainTextString().substring("业务专长：".length()) + "</SKILL>";
				
				if (str.indexOf("儿童保健科") != -1)
					poi += "<PARTMENT>儿童保健科</PARTMENT>";
				else if (str.indexOf("眼科") != -1)
					poi += "<PARTMENT>眼科</PARTMENT>";
				else if (str.indexOf("妇产科") != -1)
					poi += "<PARTMENT>妇产科</PARTMENT>";
				else if (str.indexOf("儿科") != -1)
					poi += "<PARTMENT>儿科</PARTMENT>";
				else if (str.indexOf("乳腺外科") != -1)
					poi += "<PARTMENT>乳腺外科</PARTMENT>";
				else if (str.indexOf("妇女保健科") != -1)
					poi += "<PARTMENT>妇女保健科</PARTMENT>";
				
			}
			
			if (poi != null)
			{
				poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + poi + "<URL>" + url + "</URL></DOCTOR>";
				
				FileTool.Dump(poi, "D:\\temp\\医院\\BJDxfy.csv", "UTF-8");				
			}
			
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 
	}
	public static void parsePartmentSheet(String url, int hospital) throws UnsupportedEncodingException
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
			// <div class="doc_cont">
			// er1.asp?lei=专家介绍&xlei=刘彩云&tu=7
			NodeFilter filter = new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href"));
			NodeList cld = parser.parse(filter);
			if(cld != null)
			{
				for(int k = 0; k < cld.size(); k++)
				{					
					TagNode parnode = (TagNode) cld.elementAt(k);
					String str = parnode.getAttribute("href");
					if (str != null && str.startsWith("er1.asp?lei=专家介绍&xlei"))
					{
						int inx = str.indexOf("&tu=7");
						if (inx != -1)
						{
							String name = str.substring("er1.asp?lei=专家介绍&xlei=".length(), inx);
							parseDoctor("http://www.dxfy.com/er1.asp?lei=" + java.net.URLEncoder.encode("专家介绍", "gb2312") + "&xlei=" + java.net.URLEncoder.encode(name, "gb2312") + "&tu=7", hospital);
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
	
	public static void main(String[] args) throws Exception {

		parsePartmentSheet(HOSURL, 3);
	}
}
