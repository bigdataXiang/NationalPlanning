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

// 北京市大兴区人民医院
// http://www.dxqyy.com
public class BJDxqyy {
	public static String PARTMENTS[] = {
		"html/zjnk/index.html","外科",
		"html/zjwk/index.html","内科",
		"html/zjfck/index.html","妇产科",
		"html/zjek/index.html","儿科",
		"html/zjwgk/index.html","五官科",
		"html/zjzyk/index.html","中医科",
	};
	public static void parseDoctor(String url, int hospital, String partment)
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
			
			/*<div class="a_a">
			<ul>
			<li>姓名：刘翠萍</li>
			<li>职称：副主任医师</li>
			<li>科室：中医科</li>
			<li>学历：本科</li>
			<li>专业：中医科</li>
			<li>毕业院校：北京中医药大学</li>
			<li>出诊时间：每周一、三、五上午</li>
			<li>联系电话：010-60283031</li>
			</ul>
			</div>
			<div class="a_b1"><p>所从事专业及专业特长：从事中医临床专业，擅长应用中医技术治疗心脑血管疾病、急慢性肾炎、消化系疾病及妇科、皮肤科等疑难杂症。<br />
			*/
			NodeFilter helper1 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "a_a"));
			String poi = null;
			HasParentFilter parentFilter = new HasParentFilter(new AndFilter(new TagNameFilter("ul"), new HasParentFilter(helper1)));
			NodeList cld = parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("li"), parentFilter));
			
			if(cld != null && cld.size() > 0)
			{
				
				for (int n = 0; n < cld.size(); n ++)
				{
					String str = ((TagNode) cld.elementAt(n)).toPlainTextString().replace("&amp;", "").trim();
					
					if (str.startsWith("姓名："))
					{
						str = str.substring("姓名：".length());
						if (!str.isEmpty())
							poi = "<NAME>" + str + "</NAME>";
						else
							return;
					}
					else if (str.startsWith("职称："))
					{
						str = str.substring("职称：".length());
						if (!str.isEmpty())
							poi += "<POSITION>" + str + "</POSITION>";
					}	
					else if (str.startsWith("科室："))
					{
						str = str.substring("科室：".length());
						if (!str.isEmpty())
							poi += "<PARTMENT>" + str + "</PARTMENT>";
					}else if (str.startsWith("学历："))
					{
						str = str.substring("学历：".length());
						if (!str.isEmpty())
							poi += "<EDUCATION>" + str + "</EDUCATION>";
					}else if (str.startsWith("专业："))
					{
						str = str.substring("专业：".length());
						if (!str.isEmpty())
							poi += "<MAJOR>" + str + "</MAJOR>";
					}
					else if (str.startsWith("毕业院校："))
					{
						str = str.substring("毕业院校：".length());
						if (!str.isEmpty())
							poi += "<SCHOOL>" + str + "</SCHOOL>";
					}else if (str.startsWith("联系电话："))
					{
						str = str.substring("联系电话：".length());
						if (!str.isEmpty())
							poi += "<TELEPHONE>" + str + "</TELEPHONE>";
					}
				}
			}
			
			if (poi == null)
				return;
			
			parser.reset();
			
			// <div class="a_b1"><p>所从事专业及专业特长：从事中医临床专业，擅长应用中医技术治疗心脑血管疾病、急慢性肾炎、消化系疾病及妇科、皮肤科等疑难杂症。<br />
			helper1 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "a_b1"));
			
			cld = parser.extractAllNodesThatMatch(helper1);
			if(cld != null && cld.size() > 0)
			{
				TagNode parnode = (TagNode) cld.elementAt(0);
				assert (poi != null);
				String pos = parnode.toPlainTextString().replace("&amp;", "").replace("&nbsp;", "").replace("\t", "").replace("\r", "").replace("\n", "").replace("所从事专业及专业特长：", "").replace("专业特长及主治疾病：", "").trim();
				
				if (!pos.isEmpty())
					poi += "<SKILL>" + pos + "</SKILL>";
			}
			
			if (poi != null)
			{
				poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + poi + "<PARTMENT>" + partment + "</PARTMENT><URL>" + url + "</URL></DOCTOR>";
				
				FileTool.Dump(poi, "D:\\temp\\医院\\BJDxqyy.csv", "UTF-8");				
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
	
	public static void parsePartmentSheet(String url, int hospital, String partment)
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
			NodeFilter filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "abcd"));
			NodeList cld = parser.parse(filter);
			if(cld != null)
			{
				for(int k = 0; k < cld.size(); k++)
				{					
					TagNode parnode = (TagNode) cld.elementAt(k);
					String str = parnode.toHtml();
					int inx = str.indexOf("href=\"");
					if (inx != -1)
					{
						int ine = str.indexOf("\"", inx + "href=\"".length());
						
						if (ine != -1)
						{
							parseDoctor("http://www.dxqyy.com" + str.substring(inx + "href=\"".length(), ine), hospital, partment);
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

		for (int n = 0; n < PARTMENTS.length; n += 2)
		{
			parsePartmentSheet("http://www.dxqyy.com/" + PARTMENTS[n], 4, PARTMENTS[n + 1]);
		}
	}
}
