package com.svail.geoplan;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.svail.util.FileTool;
import com.svail.util.HTMLTool;

/* 处理北京规划数据 
 * */
public class BeijingPlanCrawler {
	
	private static Set<String> urls = new TreeSet<String>();
	
	public static String pageInfo(String url)
	{
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String html = HTMLTool.fetchURL(url, "utf-8", "get");
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
		}else {
			es = doc.getElementsByAttributeValue("style", "border-left:1px solid #D9D9D9; border-right:1px solid #D9D9D9; border-bottom:1px solid #D9D9D9;");
			if (es.size() > 0)
			{
				Element se = es.get(0);
				content = StringEscapeUtils.unescapeHtml(se.text());
			}
		}
		// <td height="40" align="center" style="font-weight: bold;">北京市规划委员会石景山分局关于中关村科技园区石景山园世纪盛达园项目进行控规调整的公示说明（公示已结束）</td>
		
		es = doc.getElementsByAttributeValue("style", "font-weight: bold;");
		
		if (es.size() > 0)
		{
			Element se = es.get(0);
			NAME = StringEscapeUtils.unescapeHtml(se.text()).trim();
			int t = NAME.indexOf("（公示");
			if (t != -1)
				NAME = NAME.substring(0, t).trim();
			
		}
		else
		{
			es = doc.getElementsByAttributeValue("class", "STYLE3 STYLE7");
			if (es.size() == 0)
				es = doc.getElementsByAttributeValue("class", "line02");
			if (es.size() > 0)
			{
				Element se = es.get(0);
				NAME = StringEscapeUtils.unescapeHtml(se.text()).trim();
				int t = NAME.indexOf("（公示");
				if (t != -1)
					NAME = NAME.substring(0, t).trim();
				
			} else
			{
				es = doc.getElementsByAttributeValue("class", "wenzi-2");
				if (es.size() > 0)
				{
					for (int n = 0; n < es.size(); n ++) {
						Element se = es.get(n);
						if (se.tagName() == "strong") {
							NAME = StringEscapeUtils.unescapeHtml(se.text()).trim();
							int t = NAME.indexOf("（公示");
							if (t != -1)
								NAME = NAME.substring(0, t).trim();
							break;
						}
					}
				} else
					NAME = null;
			}
		}
		
		
		es = doc.getElementsContainingOwnText("发布时间:");
		
		if (es.size() > 0)
		{
			Element se = es.get(0);
			TIME = StringEscapeUtils.unescapeHtml(se.text()).trim();
			int t = TIME.indexOf("发布时间:");
			if (t != -1)
				TIME = TIME.substring(t + "发布时间:".length());
			
			t = TIME.indexOf("【");
			
			if (t != -1)
				TIME = TIME.substring(0, t).trim();
			
		}
		else
			TIME = null;
		return content;
	}
	private static String NAMEKEY_GLOBAL = null;
	private static String TIMEKEY_GLOBAL = null;
	private static String NAME = null;
	private static String TIME = null;
	
	public static boolean records(String url, String type, String nameKey, String timeKey)
	{
		if (url == null)
			return false;
		
		String html = HTMLTool.fetchURL(url, "utf-8", "get");
		if (html == null)
			return false;
		
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
			String title = null, time = null;
			
			String ct = pageInfo(page);
			Elements subess = es.get(n).getElementsByAttributeValueContaining("class", "styl-62");
			if (subess.size() > 0) {
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
			}
			else{
				title = NAME;
				time = TIME;
			}
			if (title != null)
			{
				String shortName = title;
				if (shortName.length() > 40)
					shortName = shortName.substring(0, 40);
				int t = shortName.indexOf("（公示");
				if (t != -1)
					shortName = shortName.substring(0, t);
				
				if (shortName != null && timeKey != null) {
					if (nameKey.equalsIgnoreCase(shortName) && timeKey.equalsIgnoreCase(time))
					{
						return false;
					}
				}
				String out = "<POI><NAME>" + title + "</NAME>";
				if (time != null)
				{
					out += "<TIME>" + time + "</TIME>";
				}
				if (ct != null)
				{
					out += "<CONTENT>" + ct.trim() + "</CONTENT>";
				}
				out += "<URL>" + url + "</URL>";
				
				out += "</POI>";
				if (NAMEKEY_GLOBAL == null)
				{
					NAMEKEY_GLOBAL = title;
					TIMEKEY_GLOBAL = time;
				}
				
				FileTool.Dump(out,  "D:\\temp\\crawldb\\plan\\Beijing_" + type + ".txt", "UTF-8");
				
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
				String shortName = title;
				if (shortName.length() > 40)
					shortName = shortName.substring(0, 40);
				int t = shortName.indexOf("（公示");
				if (t != -1)
					shortName = shortName.substring(0, t);
				
				if (shortName != null && timeKey != null) {
					if (nameKey.equalsIgnoreCase(shortName) && timeKey.equalsIgnoreCase(time))
					{
						return false;
					}
				}
				String out = "<POI><NAME>" + title.trim() + "</NAME>";
				if (time != null)
				{
					out += "<TIME>" + time.trim() + "</TIME>";
				}
				out += "<URL>" + url + "</URL>";
				out += "</POI>";
				
				if (NAMEKEY_GLOBAL == null)
				{
					NAMEKEY_GLOBAL = title;
					TIMEKEY_GLOBAL = time;
				}
				FileTool.Dump(out,  "D:\\temp\\crawldb\\plan\\Beijing_" + type + ".txt", "UTF-8");
				
			}
		}
		
		Elements nets = doc.getElementsContainingOwnText("下一页");
		
		if (nets.size() > 0)
		{
			if (nets.get(0).hasAttr("href"))
			{
				String nextpage = nets.get(0).attr("href");
				if (nextpage.indexOf("javascript:void(0)") == -1)
					urls.add("http://www.bjghw.gov.cn" + nextpage);
			}
			else {
				String nextpage = nets.get(0).parent().attr("href");
				if (nextpage.indexOf("javascript:void(0)") == -1)
					urls.add("http://www.bjghw.gov.cn" + nextpage);
			}
			
		}
		return false;
	}
	
	public static String itemInfo(String url)
	{
		if (url == null)
			return null;
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String html = HTMLTool.fetchURL(url, "utf-8", "get");
		if (html == null)
			return null;
		
		Document doc = Jsoup.parse(html);
		Elements nets = doc.getElementsMatchingOwnText("内容");
		String name = null, address = null, time = null, id = null, org = null, descript = null, english = null, type = null;
		for (int n = 0; n < nets.size(); n ++)
		{
			Element parent = nets.get(n).parent().parent().parent();
			
			if (parent != null)
			{
				Elements itmes = parent.getElementsByTag("tr");
				
				for (int m = 0; m < itmes.size(); m ++)
				{
					String t = StringEscapeUtils.unescapeHtml(itmes.get(m).text());
					
					if (t.startsWith("项目名称"))
						name = t.replace("项目名称", "");
					
					else if (t.startsWith("核准名称"))
						name = t.replace("核准名称", "");
					else if (t.startsWith("标准名称"))
						name = t.replace("标准名称", "");
						
					else if (t.startsWith("建设位置"))
					{
						if (address == null)
							address = t.replace("建设位置", "").trim();
						else
							address = t.replace("建设位置", "").trim() + " " + address;
					}
					else if (t.startsWith("详细地址")) {
						if (address == null)
							address = t.replace("详细地址", "").trim();
						else
							address = t.replace("详细地址", "").trim() + " " + address;
					}
						
					else if (t.startsWith("建设地点"))
					{
						if (address == null)
							address = t.replace("建设地点", "").trim();
						else
							address = t.replace("建设地点", "").trim() + " " + address;
					}
					else if (t.startsWith("位置"))
					{
						if (address == null)
							address = t.replace("位置", "").trim();
						else
							address = t.replace("位置", "").trim() + " " + address;
					}
					else if (t.indexOf("建设地区") != -1)
					{
						if (address == null)
							address = t.replace("建设地区", "").trim();
						else
							address = t.replace("建设地区", "").trim() + " " + address;
					}
					else if (t.startsWith("核发日期"))
						time = t.replace("核发日期", "").trim();
					else if (t.startsWith("验收日期"))
						time = t.replace("验收日期", "").trim();
					else if (t.startsWith("发布时间"))
						time = t.replace("发布时间", "").trim();
					else if (t.indexOf("证书编号") != -1)
					{
						id = t.replace("证书编号", "").trim();
					}
					else if (t.indexOf("规划验收发文号") != -1)
					{
						id = t.replace("规划验收发文号", "").trim();
					}
					else if (t.indexOf("审批文号") != -1)
					{
						id = t.replace("审批文号", "").trim();
					}
					else  if (t.indexOf("批准文号") != -1)
					{
						id = t.replace("批准文号", "").trim();
					}
					else if (t.indexOf("建设单位") != -1)
					{
						org = t.replace("建设单位", "").trim();
					}
					
					else if (t.indexOf("申报单位") != -1)
					{
						org = t.replace("申报单位", "").trim();
					}
					else if (t.indexOf("附加说明") != -1){
						descript = t.replace("附加说明", "").trim();
					}
					else if (t.startsWith("汉语拼音拼写"))
						english = t.replace("汉语拼音拼写", "").trim();
					else if (t.startsWith("项目"))
						type = t.replace("项目", "").trim();
				}
			}			
		}
		if (name == null)
			return null;
		
		String ts = "<NAME>" + name.trim() + "</NAME>"; 

		if (english != null)
			ts += "<ENG>" + english.trim() + "</ENG>";
		
		if (address != null)
			ts += "<ADDRESS>" + address.trim() + "</ADDRESS>";
		
		if (id != null)
			ts += "<ID>" + id.trim() + "</ID>";
		if (org != null)
			ts += "<ORG>" + org.trim() + "</ORG>";
		
		if (time != null)
			ts += "<TIME>" + time.trim() + "</TIME>";
		if (type != null)
			ts += "<TYPE>" + type.trim() + "</TYPE>";
		if (descript != null)
			ts += "<DECRIPT>" + descript.trim() + "</DECRIPT>";
		return "<POI>" + ts + "<URL>" + url + "</URL></POI>";
		
	}
	private static String ID_NEWEST = null;
	
	public static boolean items(String url, String fileName, String key, int type)
	{
		if (url == null)
			return false;
		
		String html = HTMLTool.fetchURL(url, "utf-8", "get");
		if (html == null)
			return false;
		
		Document doc = Jsoup.parse(html);
		Elements nets = doc.getElementsByAttributeValue("class", "a02");
		String pres = null;
		if (nets.size() > 0) {
			for (int n = 0; n < nets.size(); n ++)
			{
				Element el = nets.get(n);
				Element parent = el.parent().parent();
				
				Elements trs = parent.children();
				
				String urlInfo = null;
				
				if (trs.size() == 7 && type == 1) {
					String id = trs.get(0).text();
					if (ID_NEWEST == null)
						ID_NEWEST = id;
					
					String org = trs.get(1).text();
					String name = trs.get(2).text();
					String address = trs.get(3).text();
					String time = trs.get(4).text();
					Elements hrs = trs.get(5).children();
					if (hrs.size() == 0)
					{
						if (key != null && key.equalsIgnoreCase(id))
						{
							return true;
						}
						String cnt = "<POI><NAME>" + name  + "</NAME><ADDRESS>" + address + "</ADDRESS><ORG>" + org + "</ORG><TIME>" + time + "</TIME></POI>";
						
						FileTool.Dump(cnt, "D:\\temp\\crawldb\\plan\\" + fileName + ".txt", "UTF-8");
						continue;
						
					}else
					{
						urlInfo = "http://www.bjghw.gov.cn" + hrs.get(0).attr("href");
						if (!urlInfo.endsWith("v=1")) {
							urlInfo = null;
						}
					}
				}
				else {
					assert (trs.size() == 5);
					if (type == 2)
					{
						String org = trs.get(0).text();
						String name = trs.get(1).text();
						String address = trs.get(2).text();
						String id = trs.get(3).text();
						if (ID_NEWEST == null)
							ID_NEWEST = id;
						
						Elements hrs = trs.get(4).children();
						if (hrs.size() == 0)
						{
							if (key != null && key.equalsIgnoreCase(id))
							{
								return true;
							}
							
							String cnt = "<POI><NAME>" + name  + "</NAME><ADDRESS>" + address + "</ADDRESS><ID>" + id + "</ID><ORG>" + org + "</ORG></POI>";
							
							FileTool.Dump(cnt, "D:\\temp\\crawldb\\plan\\" + fileName + ".txt", "UTF-8");
							continue;
							
						}else
						{
							urlInfo = "http://www.bjghw.gov.cn" + hrs.get(0).attr("href");
						}
					}
					else if (type == 3)
					{
						String name = trs.get(0).text();
						String address = trs.get(1).text();
						String id = trs.get(2).text();
						String ptype = trs.get(3).text();
						
						if (ID_NEWEST == null)
							ID_NEWEST = id;
						
						Elements hrs = trs.get(4).children();
						if (hrs.size() == 0)
						{
							if (key != null && key.equalsIgnoreCase(id))
							{
								return true;
							}
							
							String cnt = "<POI><NAME>" + name  + "</NAME><ADDRESS>" + address + "</ADDRESS><ID>" + id + "</ID><TYPE>" + ptype + "</TYPE></POI>";
							
							FileTool.Dump(cnt, "D:\\temp\\crawldb\\plan\\" + fileName + ".txt", "UTF-8");
							continue;
							
						}else
						{
							urlInfo = "http://www.bjghw.gov.cn" + hrs.get(0).attr("href");
						}
					}
				}	
				String ts = null;
				if (pres == null)
				{
					ts = itemInfo(urlInfo);
					pres = urlInfo.substring(0, urlInfo.length() - 2);
				} else {
					String tss = urlInfo.substring(0, urlInfo.length() - 2);
					if (tss.equalsIgnoreCase(pres)) {
						continue;
					}
					else
					{
						pres = tss;
						ts = itemInfo(urlInfo);
					}
				}
				if (ts == null) {
					if (trs.size() == 7 && type == 1) {
						String id = trs.get(0).text();
						if (key != null && key.equalsIgnoreCase(id))
						{
							return true;
						}
						
						String org = trs.get(1).text();
						String name = trs.get(2).text();
						String address = trs.get(3).text();
						String time = trs.get(4).text();
						
						String cnt = "<POI><NAME>" + name  + "</NAME><ADDRESS>" + address + "</ADDRESS><ORG>" + org + "</ORG><TIME>" + time + "</TIME><URL>" + urlInfo + "</URL></POI>";
						FileTool.Dump(cnt, "D:\\temp\\crawldb\\plan\\" + fileName + ".txt", "UTF-8");
						continue;
					}
					else {
						assert (trs.size() == 5);
						if (type == 2) {
							String org = trs.get(0).text();
							String name = trs.get(1).text();
							String address = trs.get(2).text();
							String id = trs.get(3).text();
							if (key != null && key.equalsIgnoreCase(id))
							{
								return true;
							}
							
							String cnt = "<POI><NAME>" + name  + "</NAME><ADDRESS>" + address + "</ADDRESS><ID>" + id + "</ID><ORG>" + org + "</ORG><URL>" + urlInfo + "</URL></POI>";
							FileTool.Dump(cnt, "D:\\temp\\crawldb\\plan\\" + fileName + ".txt", "UTF-8");continue;
						}
						else if (type == 3) {
							String name = trs.get(0).text();
							String address = trs.get(1).text();
							String id = trs.get(2).text();
							String ptype = trs.get(3).text();
							
							if (key != null && key.equalsIgnoreCase(id))
							{
								return true;
							}
							String cnt = "<POI><NAME>" + name  + "</NAME><ADDRESS>" + address + "</ADDRESS><ID>" + id + "</ID><TYPE>" + ptype + "</TYPE><URL>" + urlInfo + "</URL></POI>";
							FileTool.Dump(cnt, "D:\\temp\\crawldb\\plan\\" + fileName + ".txt", "UTF-8");
							
							continue;
						}
					}	
				}
				
				int s = ts.indexOf("<ID>");
				if (s != -1) {
					int e = ts.indexOf("</ID>");

					if (e != -1) {
						String id = ts.substring(s + "<ID>".length(), e);
						if (key != null && key.equalsIgnoreCase(id))
						{
							return true;
						}
					}
					FileTool.Dump(ts, "D:\\temp\\crawldb\\plan\\" + fileName + ".txt", "UTF-8");
					
				}
			}			
		}
		
		nets = doc.getElementsContainingOwnText("下一页");
		
		if (nets.size() > 0)
		{
			Element parent = nets.get(0).parent();
			String newUrl = parent.attr("onclick").replace("window.location.href=encodeURI(\'", "").replace("\');", "");
			if (items("http://www.bjghw.gov.cn/" + newUrl, fileName, key, type))
				return true;
		}		
		return false;
		
	}
	
	public static void BJPlanTask() {
		String notice_urls[] = {
				"http://chy.bjghw.gov.cn/web/static/catalogs/catalog_223400/223400.html", /* 朝阳 */
				"http://www.bjghw.gov.cn/web/static/catalogs/catalog_26/26.html", /* 规划类公示  */
				"http://www.bjghw.gov.cn/web/static/catalogs/catalog_14300/14300.html", /* 规划类公告  */
				"http://www.bjghw.gov.cn/web/static/catalogs/catalog_98000/98000.html", /* 地名公示  */
				"http://hd.bjghw.gov.cn/web/static/catalogs/catalog_50200/50200.html", /* 海淀 */
				"http://tzh.bjghw.gov.cn/web/static/catalogs/catalog_80000/80000.html", /* 通州*/
				"http://shy.bjghw.gov.cn/web/static/catalogs/catalog_150000/150000.html", /* 顺义 */
				"http://yq.bjghw.gov.cn/web/static/catalogs/catalog_85700/85700.html", /* 延庆 */
				"http://xch.bjghw.gov.cn/web/static/catalogs/catalog_208800/208800.html",
				"http://shjsh.bjghw.gov.cn/web/static/catalogs/catalog_184600/184600.html",
				"http://www.bjghw.gov.cn/web/static/catalogs/catalog_256100/256100.html",
				"http://www.bjghw.gov.cn/web/static/catalogs/catalog_348800/348800.html",
				"http://chy.bjghw.gov.cn/web/static/catalogs/catalog_223500/223500.html",
				"http://dch.bjghw.gov.cn/web/static/catalogs/catalog_234000/234000.html",
				"http://dch.bjghw.gov.cn/web/static/catalogs/catalog_342000/342000.html",
				"http://xch.bjghw.gov.cn/web/static/catalogs/catalog_204100/204100.html",
				"http://xch.bjghw.gov.cn/web/static/catalogs/catalog_294300/294300.html",
		};

		String notice_datas[] = {
				/**/"朝阳规划公示",
				"规划类公示",
				"规划类公告",
				"地名公示",
				"海淀规划公示",
				"通州规划公示",
				"顺义规划公示",
				"延庆规划公示",
				"平谷规划公示",
				"石景山规划公示",
				"丰台规划公示",
				"丰台规划公告",
				"朝阳规划公告",
				"东城规划公示",
				"东城规划公告",
				"西城规划公示",
				"西城规划公告",
		};

		for (int n = 0; n < notice_urls.length; n ++)
		{
			urls.clear();

			urls.add(notice_urls[n]);

			Vector<String> strs = FileTool.Load("D:\\Temp\\crawldb\\plan\\Beijing_" + notice_datas[n] + "_log.txt", "UTF-8");
			String nameKey = null, timeKey = null;
			if (strs != null && strs.size() > 0) {
				String tk[] = strs.get(0).split(";");

				if (tk.length == 2)
				{
					nameKey = tk[0];
					timeKey = tk[1];
					if (nameKey.length() > 40)
						nameKey = nameKey.substring(0, 40);
					int t = nameKey.indexOf("（公示");
					if (t != -1)
						nameKey = nameKey.substring(0, t);
				}
			}

			Iterator<String> itr = urls.iterator();
			String key;
			while (itr.hasNext())
			{
				key = itr.next();
				urls.remove(key);

				records(key, notice_datas[n], nameKey, timeKey );

				itr = urls.iterator();
			}

			File file = new File("D:\\Temp\\crawldb\\plan\\Beijing_" + notice_datas[n] + "_log.txt");
			file.delete();
			if (NAMEKEY_GLOBAL != null)
			{
				if (NAMEKEY_GLOBAL.length() > 40)
					NAMEKEY_GLOBAL = NAMEKEY_GLOBAL.substring(0, 40);
				int t = NAMEKEY_GLOBAL.indexOf("（公示");
				if (t != -1)
					NAMEKEY_GLOBAL = NAMEKEY_GLOBAL.substring(0, t);

				FileTool.Dump(NAMEKEY_GLOBAL + ";" + TIMEKEY_GLOBAL, "D:\\Temp\\crawldb\\plan\\Beijing_" + notice_datas[n] + "_log.txt", "UTF-8");
			}
			else if (nameKey != null) {
				System.out.println(nameKey + "; " + timeKey);
				FileTool.Dump(nameKey + ";" + timeKey, "D:\\Temp\\crawldb\\plan\\Beijing_" + notice_datas[n] + "_log.txt", "UTF-8");
			}

			NAMEKEY_GLOBAL = null;
			TIMEKEY_GLOBAL = null;

		}

		String item_urls[] = 
			{
				"http://bjghw.gov.cn/query/business/query/queryTableAction$searchTable1Yi.action?searchContent=&pageBean.currentPage=0&pageBean.itemsPerPage=800", // 规划意见书
				"http://bjghw.gov.cn/query/business/query/queryTableAction$searchTable1Di.action?searchContent=&pageBean.currentPage=0&pageBean.itemsPerPage=4000", // 用地许可证
				"http://bjghw.gov.cn/query/business/query/queryTableAction$searchTable1Jian.action?searchContent=&pageBean.currentPage=0&pageBean.itemsPerPage=4000", // 规划许可证

				"http://bjghw.gov.cn/query/business/query/queryTableAction$searchTable3.action?searchContent=&pageBean.currentPage=0&pageBean.itemsPerPage=4000", // 规划竣工验收
				"http://bjghw.gov.cn/query/business/query/queryTableAction$searchTable4.action?searchContent=&pageBean.currentPage=0&pageBean.itemsPerPage=4000", // 建筑名称核准
				/**/
				"http://bjghw.gov.cn/query/business/query/queryTableAction$getAllGonggaoList.action?nothing=nothing&pageBean.currentPage=0&pageBean.itemsPerPage=4000" // 地名
			};		
		String item_names[] = {
				"Beijing_规划意见书",
				"Beijing_用地许可证",
				"Beijing_规划许可证",
				"Beijing_规划竣工验收",
				"Beijing_建筑名称核准",
				/**/"Beijing_地名",
		};

		int item_types[] = {
				1,
				1,
				1,
				2,
				2,/**/
				3,
		};
		for (int n = 0; n < item_urls.length; n ++)
		{
			System.out.println(item_urls[n]);
			Vector<String> strs = FileTool.Load("D:\\Temp\\crawldb\\plan\\" + item_names[n] + "_log.txt", "UTF-8");
			String key = null;
			if (strs != null && strs.size() > 0) {
				key = strs.get(0);
			}

			items(item_urls[n], item_names[n], key, item_types[n]);

			File file = new File("D:\\Temp\\crawldb\\plan\\" + item_names[n] + "_log.txt");
			file.delete();
			assert(ID_NEWEST != null);
			if (ID_NEWEST != null)
				FileTool.Dump(ID_NEWEST, "D:\\Temp\\crawldb\\plan\\" + item_names[n] + "_log.txt", "UTF-8");
			else
				FileTool.Dump(key, "D:\\Temp\\crawldb\\plan\\" + item_names[n] + "_log.txt", "UTF-8");
			ID_NEWEST = null;
		}
	}
	
	public static void main(String[] args) {
	
		String notice_urls[] = {
			"http://chy.bjghw.gov.cn/web/static/catalogs/catalog_223400/223400.html", /* 朝阳 */
			"http://www.bjghw.gov.cn/web/static/catalogs/catalog_26/26.html", /* 规划类公示  */
			"http://www.bjghw.gov.cn/web/static/catalogs/catalog_14300/14300.html", /* 规划类公告  */
			"http://www.bjghw.gov.cn/web/static/catalogs/catalog_98000/98000.html", /* 地名公示  */
			"http://hd.bjghw.gov.cn/web/static/catalogs/catalog_50200/50200.html", /* 海淀 */
			"http://tzh.bjghw.gov.cn/web/static/catalogs/catalog_80000/80000.html", /* 通州*/
			"http://shy.bjghw.gov.cn/web/static/catalogs/catalog_150000/150000.html", /* 顺义 */
			"http://yq.bjghw.gov.cn/web/static/catalogs/catalog_85700/85700.html", /* 延庆 */
			"http://xch.bjghw.gov.cn/web/static/catalogs/catalog_208800/208800.html",
			"http://shjsh.bjghw.gov.cn/web/static/catalogs/catalog_184600/184600.html",
			"http://www.bjghw.gov.cn/web/static/catalogs/catalog_256100/256100.html",
			"http://www.bjghw.gov.cn/web/static/catalogs/catalog_348800/348800.html",
			"http://chy.bjghw.gov.cn/web/static/catalogs/catalog_223500/223500.html",
			"http://dch.bjghw.gov.cn/web/static/catalogs/catalog_234000/234000.html",
			"http://dch.bjghw.gov.cn/web/static/catalogs/catalog_342000/342000.html",
			"http://xch.bjghw.gov.cn/web/static/catalogs/catalog_204100/204100.html",
			"http://xch.bjghw.gov.cn/web/static/catalogs/catalog_294300/294300.html",
		};
		
		String notice_datas[] = {
			/**/"朝阳规划公示",
			"规划类公示",
			"规划类公告",
			"地名公示",
			"海淀规划公示",
			"通州规划公示",
			"顺义规划公示",
			"延庆规划公示",
			"平谷规划公示",
			"石景山规划公示",
			"丰台规划公示",
			"丰台规划公告",
			"朝阳规划公告",
			"东城规划公示",
			"东城规划公告",
			"西城规划公示",
			"西城规划公告",
		};

		for (int n = 0; n < notice_urls.length; n ++)
		{
			urls.clear();
			
			urls.add(notice_urls[n]);
		
			Vector<String> strs = FileTool.Load("D:\\Temp\\crawldb\\plan\\Beijing_" + notice_datas[n] + "_log.txt", "UTF-8");
			String nameKey = null, timeKey = null;
			if (strs != null && strs.size() > 0) {
				String tk[] = strs.get(0).split(";");
				
				if (tk.length == 2)
				{
					nameKey = tk[0];
					timeKey = tk[1];
					if (nameKey.length() > 40)
						nameKey = nameKey.substring(0, 40);
					int t = nameKey.indexOf("（公示");
					if (t != -1)
						nameKey = nameKey.substring(0, t);
				}
			}
			
			Iterator<String> itr = urls.iterator();
			String key;
			while (itr.hasNext())
			{
				key = itr.next();
				urls.remove(key);

				records(key, notice_datas[n], nameKey, timeKey );

				itr = urls.iterator();
			}
			
			File file = new File("D:\\Temp\\crawldb\\plan\\Beijing_" + notice_datas[n] + "_log.txt");
			file.delete();
			if (NAMEKEY_GLOBAL != null)
			{
				if (NAMEKEY_GLOBAL.length() > 40)
					NAMEKEY_GLOBAL = NAMEKEY_GLOBAL.substring(0, 40);
				int t = NAMEKEY_GLOBAL.indexOf("（公示");
				if (t != -1)
					NAMEKEY_GLOBAL = NAMEKEY_GLOBAL.substring(0, t);
				
				FileTool.Dump(NAMEKEY_GLOBAL + ";" + TIMEKEY_GLOBAL, "D:\\Temp\\crawldb\\plan\\Beijing_" + notice_datas[n] + "_log.txt", "UTF-8");
			}
			else if (nameKey != null) {
				System.out.println(nameKey + "; " + timeKey);
				FileTool.Dump(nameKey + ";" + timeKey, "D:\\Temp\\crawldb\\plan\\Beijing_" + notice_datas[n] + "_log.txt", "UTF-8");
			}
			
			NAMEKEY_GLOBAL = null;
			TIMEKEY_GLOBAL = null;
			
		}

		String item_urls[] = 
		{
			"http://bjghw.gov.cn/query/business/query/queryTableAction$searchTable1Yi.action?searchContent=&pageBean.currentPage=0&pageBean.itemsPerPage=800", // 规划意见书
			"http://bjghw.gov.cn/query/business/query/queryTableAction$searchTable1Di.action?searchContent=&pageBean.currentPage=0&pageBean.itemsPerPage=4000", // 用地许可证
			"http://bjghw.gov.cn/query/business/query/queryTableAction$searchTable1Jian.action?searchContent=&pageBean.currentPage=0&pageBean.itemsPerPage=4000", // 规划许可证
			
			"http://bjghw.gov.cn/query/business/query/queryTableAction$searchTable3.action?searchContent=&pageBean.currentPage=0&pageBean.itemsPerPage=4000", // 规划竣工验收
			"http://bjghw.gov.cn/query/business/query/queryTableAction$searchTable4.action?searchContent=&pageBean.currentPage=0&pageBean.itemsPerPage=4000", // 建筑名称核准
			/**/
			"http://bjghw.gov.cn/query/business/query/queryTableAction$getAllGonggaoList.action?nothing=nothing&pageBean.currentPage=0&pageBean.itemsPerPage=4000" // 地名
		};		
		String item_names[] = {
			"Beijing_规划意见书",
			"Beijing_用地许可证",
			"Beijing_规划许可证",
			"Beijing_规划竣工验收",
			"Beijing_建筑名称核准",
			/**/"Beijing_地名",
		};
		
		int item_types[] = {
			 1,
			1,
			1,
			2,
			2,/**/
			3,
		};
		for (int n = 0; n < item_urls.length; n ++)
		{
			System.out.println(item_urls[n]);
			Vector<String> strs = FileTool.Load("D:\\Temp\\crawldb\\plan\\" + item_names[n] + "_log.txt", "UTF-8");
			String key = null;
			if (strs != null && strs.size() > 0) {
				key = strs.get(0);
			}
			
			items(item_urls[n], item_names[n], key, item_types[n]);
			
			File file = new File("D:\\Temp\\crawldb\\plan\\" + item_names[n] + "_log.txt");
			file.delete();
			assert(ID_NEWEST != null);
			if (ID_NEWEST != null)
				FileTool.Dump(ID_NEWEST, "D:\\Temp\\crawldb\\plan\\" + item_names[n] + "_log.txt", "UTF-8");
			else
				FileTool.Dump(key, "D:\\Temp\\crawldb\\plan\\" + item_names[n] + "_log.txt", "UTF-8");
			ID_NEWEST = null;
		}
	}
}
