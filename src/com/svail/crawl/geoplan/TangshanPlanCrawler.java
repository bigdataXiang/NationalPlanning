package com.svail.crawl.geoplan;

import java.util.Iterator;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.svail.util.FileTool;
import com.svail.util.HTMLTool;

/* 处理河北唐山规划数据 
 * */
public class TangshanPlanCrawler {
	private static String bj_urls[] = {
		new String("http://www.tsghj.gov.cn/004/4/default.aspx"),
		new String("http://www.tsghj.gov.cn/007/7/default.aspxl"),
		new String("http://www.tsghj.gov.cn/007/030/30/default.aspx"), 
		new String("http://www.tsghj.gov.cn/007/031/31/default.aspx"),
		new String("http://www.tsghj.gov.cn/007/032/32/default.aspx"), 
		new String("http://www.tsghj.gov.cn/007/099/99/default.aspx"), 
		new String("http://www.tsghj.gov.cn/Template/default/045/45/default.aspx")
	};
		
	private static Set<String> urls = new TreeSet<String>();
	
	public static String pageInfo(String url)
	{
		String html = HTMLTool.fetchURL(url, "utf-8", "post");
		if (html == null)
			return null;
		
		Document doc = Jsoup.parse(html);
		
		//id="articleContent"

		Elements es = doc.getElementsByAttributeValue("id", "articleContent");
		String content = null;
		if (es.size() > 0)
		{
			Element se = es.get(0);
			content = StringEscapeUtils.unescapeHtml(se.text());
		}
		return content;
	}
	
	public static void records(String url)
	{
		if (url == null)
			return;
		
		String html = HTMLTool.fetchURL(url, "utf-8", "post");
		if (html == null)
			return;
		
		Document doc = Jsoup.parse(html);
		/*<a target="_blank" href="/web/static/articles/catalog_26/article_ff80808145ee6dee014694a2dc9802a6/ff80808145ee6dee014694a2dc9802a6.html">
        <font class="styl-62"> ·        丰台区槐房路4号自住型商品房项目用地规划指标调整公示（公示期限30天）
			</font>
        <font class="styl-62"> (2014-06-13)</font>
         </a>*/
		Elements es = doc.getElementsByAttributeValueContaining("href", "/web/static/articles/catalog");
		
		for (int n = 0; n < es.size(); n ++)
		{
			String page = "http://www.bjghw.gov.cn" + es.get(n).attr("href");
			String ct = pageInfo(page);
			String title = null, time = null;
			Elements subess = es.get(n).getElementsByAttributeValueContaining("class", "styl-62");
			for (int m = 0; m < subess.size(); m ++)
			{
				String t = StringEscapeUtils.unescapeHtml(subess.get(m).ownText());
				if (t.startsWith("·"))
				{
					title = t.substring(1, t.length()).trim().replace("（公示期限30天）", "").replace("（公示已结束）", "");
					
				}
				else if (t.startsWith("("))
				{
					time = t.substring(1, t.length() - 1);
				}
				
			}
			
			if (title != null)
			{
				String out = "<POI><NAME>" + title + "</NAME>";
				if (time != null)
				{
					out += "<TIME>" + time + "</TIME>";
				}
				if (ct != null)
				{
					out += "<CONTENT>" + ct.trim() + "</CONTENT>";
				}
				out += "</POI>";
				
				FileTool.Dump(out,  "D:\\temp\\crawldb\\plan\\bj_plan.txt", "UTF-8");
			}
		}
		/*<a href="#" onclick="window.open('/web/dynamic/article_gongshi/articleDetailsForGongShiToFinishedAction$viewArticleDetailsById.action?catalogId=26&amp;articleId=ff80808145ee6dee014613dd1b6700c8');">
          <font class="styl-62">·        北京市海淀区常青行通达地区规划调整项目公示（公示期限30天）（公示已结束）
			</font>
          <font class="styl-62">(2014-05-19)</font>
          </a>
		 * */
		
		es = doc.getElementsByAttributeValueContaining("onclick", "/web/dynamic/article_gongshi/articleDetailsForGongShiToFinishedAction");
		
		for (int n = 0; n < es.size(); n ++)
		{
			String title = null, time = null;
			Elements subess = es.get(n).getElementsByAttributeValueContaining("class", "styl-62");
			for (int m = 0; m < subess.size(); m ++)
			{
				String t = StringEscapeUtils.unescapeHtml(subess.get(m).ownText());
				if (t.startsWith("·"))
				{
					title = t.substring(1, t.length()).trim().replace("（公示期限30天）", "").replace("（公示已结束）", "");
					
				}
				else if (t.startsWith("("))
				{
					time = t.substring(1, t.length() - 1);
				}				
			}
			
			if (title != null)
			{
				String out = "<POI><NAME>" + title.trim() + "</NAME>";
				if (time != null)
				{
					out += "<TIME>" + time.trim() + "</TIME>";
				}
				
				out += "</POI>";
				
				FileTool.Dump(out,  "D:\\temp\\crawldb\\plan\\bj_plan.txt", "UTF-8");
			}
		}
		
		Elements nets = doc.getElementsContainingOwnText("下一页");
		
		if (nets.size() > 0)
		{
			String nextpage = nets.get(0).parent().attr("href");
			if (nextpage.indexOf("javascript:void(0)") == -1)
				urls.add("http://www.bjghw.gov.cn" + nextpage);
		}
	}
	
	public static void main(String[] args) {
	
		for (int n = 0; n < bj_urls.length; n ++)
			urls.add(bj_urls[n]);

		Iterator<String> itr = urls.iterator();
		String key;
		while (itr.hasNext())
		{
			key = itr.next();
			urls.remove(key);
			
			records(key);
			
			itr = urls.iterator();
			
		}
	}
}
