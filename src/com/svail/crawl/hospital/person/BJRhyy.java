package com.svail.crawl.hospital.person;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.svail.util.FileTool;
import com.svail.util.HTMLTool;

// <POI><RID>7</RID><NAME>北京市仁和医院</NAME><URL>http://www.bjrhyy.cn</URL><TELEPHONE>010-69242469-2050</TELEPHONE></POI>
public class BJRhyy {
	public static String URL = "http://www.bjrhyy.cn/Hospitals/Doctors/Overview";
	public static void parseDoctor(String url, int hospital, String partment)
	{
		String content = HTMLTool.fetchURL(url, "UTF-8", "get");

		Parser parser = new Parser();
		if (content == null)
		{
			return;
		}
		
		try {
			int ins =  content.indexOf("<!---------------------content--------------------------------->");
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
			parser.setInputHTML(content);
			parser.setEncoding("UTF-8");
			
			/* class="doctor_con"
			 * */
			NodeFilter helper1 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "doctor_con"));
			String poi = null;
			NodeList cld = parser.extractAllNodesThatMatch(helper1);
			
			if(cld != null && cld.size() > 0)
			{
				String str = ((TagNode) cld.elementAt(0)).toPlainTextString().replace("&amp;", "").replace(" ", "").replace("\t", "").replace("\n", ";").replace("\r", "").trim();

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
					else if (toks[n].startsWith("职称："))
					{
						str = toks[n].substring("职称：".length());
						if (!str.isEmpty())
							poi += "<POSITION>" + str + "</POSITION>";
					}	
					else if (toks[n].startsWith("专业特长："))
					{
						str = toks[n].substring("专业特长：".length());
						if (!str.isEmpty())
							poi += "<SKILL>" + str + "</SKILL>";
					}else if (toks[n].startsWith("专长："))
					{
						str = toks[n].substring("专长：".length());
						if (!str.isEmpty())
							poi += "<SKILL>" + str + "</SKILL>";
					}else if (toks[n].startsWith("经历："))
					{
						str = toks[n].substring("经历：".length());
						if (!str.isEmpty())
							poi += "<INTRODUCTION>" + str + "</INTRODUCTION>";
					}
				}
				if (poi != null)
				{
					poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + poi + "<PARTMENT>" + partment + "</PARTMENT><URL>" + url + "</URL></DOCTOR>";
					
					FileTool.Dump(poi, "D:\\temp\\医院\\BJRhyy.csv", "UTF-8");				
				}
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
			int ins =  content.indexOf("<!---------------------content--------------------------------->");
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
			
			 
			parser.setInputHTML(content);
			parser.setEncoding("UTF-8");
			
			// class="linchuang_ul
			NodeFilter filter = new AndFilter(new TagNameFilter("ul"), new HasAttributeFilter("class", "linchuang_ul"));
			NodeList cld = parser.parse(filter);
			if(cld != null && cld.size() > 0)
			{
				cld = cld.elementAt(0).getChildren();
				
				
				if (cld != null)
				{
					String partment = null;
					for(int k = 0; k < cld.size(); k++)
					{					
						if (cld.elementAt(k) instanceof TagNode)
						{
							TagNode parnode = (TagNode) cld.elementAt(k);
							if (parnode.getTagName().equalsIgnoreCase("h2"))
							{
								partment = parnode.toPlainTextString();
							}
							else if (parnode.getTagName().equalsIgnoreCase("ul"))
							{
								Set<String> docs = new TreeSet<String>();
								
								NodeFilter helper1 = new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href"));
								NodeList scld = parnode.getChildren().extractAllNodesThatMatch(helper1, true);
								
								if (scld != null)
								{
									for (int n = 0; n < scld.size(); n ++)
									{
										docs.add(((TagNode)scld.elementAt(n)).getAttribute("href"));
									}
								}
								Iterator<String> itr = docs.iterator();
								while (itr.hasNext())
								{
									String str = itr.next();
									parseDoctor("http://www.bjrhyy.cn" + str, hospital, partment);
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
	}
	
	public static void main(String[] args) throws Exception {

		parsePartmentSheet(URL, 7);
		
	}

}
