package com.svail.crawl.geoplan;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.svail.util.FileTool;
import com.svail.util.HTMLTool;

/* 处理天津规划数据 
 * */
public class TianjinPlanCrawler {
	private static String tj_urls[] = {
		new String("http://www.cityplan.gov.cn/newslist.aspx?id=CK0401"), /*  规划制定  */
		new String("http://www.cityplan.gov.cn/newslist.aspx?id=CK0402"), /* 规划实施  */
		new String("http://www.cityplan.gov.cn/newslist.aspx?id=CK0501"), /* 规划制定  */
		new String("http://www.cityplan.gov.cn/newslist.aspx?id=CK0503"), /* 规划实施  */
		new String("http://www.cityplan.gov.cn/newslist.aspx?id=CK0502"), /*  规划监督  */
	};
	
	private static Set<String> urls = new TreeSet<String>();
	
	public static String pageInfo(String url)
	{
		if (!url.startsWith("http://www.cityplan.gov.cn/news.aspx"))
		{
			System.out.println(url);
			return null;
		}
		
		String html = HTMLTool.fetchURL(url, "gb18030", "get");
		if (html == null)
			return null;
		
		Document doc = Jsoup.parse(html);
		
		//id="articleContent"

		/*name="form1"*/
		Elements es = doc.getElementsByAttributeValueContaining("name", "form");
		String content = null;
		if (es.size() > 0)
		{
			Element se = es.get(0);
			content = se.text();
			content = content.replace("政务公开", ";").replace("政务动态", ";").
					replace("规划公示", ";").replace("规划公布", ";").replace("审批结果",";").
					replace("办事大厅", ";").replace("政策法规", ";").replace("管理文件", ";").replace("测绘管理", ";").
					replace("地名管理", ";").replace("政府信息公开", ";").
					replace("津城概览", ";").replace("· 规划制定", ";").replace("· 总体规划",";").replace("· 空间发展战略规划", ";").
					replace("· 控制性详细规划", ";").replace("· 其他规划", ";").replace("规划实施", ";").replace("· 修建性详细规划", ";").
					replace("· 总平面设计方案", ";").replace("· 建设工程设计方案", ";").replace("· 城市设计", ";").replace("· 其他", ";").
					replace("规划资质管理", ";").replace("栏目名称：", ";").replace("政务公开", ";").replace("政务动态", ";").replace("政府信息公开", ";").
					replace("规划公示", ";").replace("规划公布", ";").replace("办事大厅", ";").replace("测绘管理", ";").
					replace("地名管理", ";").replace("规划讲堂", ";").replace("津城概览", ";").replace("政策法规", ";").replace("管理文件", ";").replace("专题栏目", ";").replace("区县局(分局)", ";").replace("网站信息", ";").replace("基层单位",";").replace("规划院第四次交通调查", ";").replace("单位概况", ";").replace("业界动态", ";").replace("入会申请", ";").replace("会员单位", ";").replace("规划设计评优", ";").replace("行业管理", ";").replace("学术交流", ";").replace("规划讲堂", ";").replace("在线服务", ";").
					replace("政策法规", ";").replace("二级机构", ";").replace("图片展", ";").replace("全部 关 键 字：", ";").replace(" 搜索位置：", ";").replace("标题 内容", ";").replace("政策法规", ";").replace("审批结果", ";").replace("媒体声音", ";").replace("规划公示", ";").replace("规划公布", ";").replace("规划讲堂", ";").replace("·认识天津", ";").replace("·走向世界", ";").replace("·避险场所", ";").replace("·交通出行", ";").replace("·医疗卫生", ";").replace("·教育求学", ";").replace("·生活服务", ";").replace("·休闲旅游", ";").replace("当前位置：首页", ";").replace(">>", ";").replace("规划公示", ";").replace(">>", ";").replace("规划制定", ";").replace(">>", ";").replace("控制性详细规划", ";").replace("工作动态 规划公开 地名普查 执法监察 资质公开 工作动态 规划公开 地名普查 执法监察 网上互动 规划公开", "");
			
			content = content.replace("附件下载：", ";").replace("相关新闻：", ";").replace("网站地图", ";").replace("关于本站", ";").replace("帮助信息", ";").replace("联系我们", ";").replace("网站声明", ";").replace("隐私声明", ";").replace("地址：天津市和平区西康路48号", ";").replace("联系电话：022-23359045", ";").replace("传真：022-23359045", ";").replace("邮编：300070", ";").replace("版权所有：天津市规划局", ";").replace("津ICP备05000052号 网站信息仅供参考，权威数据以原始记录或档案为准。", ";").replace("工作动态 ","").replace("规划公开 ","")
					.replace("地名普查","").replace("执法监察 ","").replace("资质公开 ","").replace("规划公开 ","").replace("地名普查 ","").replace("执法监察 ","").replace("网上互动 ","").replace("规划公开 ","");
			
			content = content.replace("\t", ";").replace("\r", ";").replace("\n", ";").replace(" ", ";");
			String toks[] = content.split(";");
			
			content = null;
			for (int n = 0; n < toks.length; n ++)
			{
				toks[n] = toks[n].trim();
				
				if (toks[n].length() < 2)
				{}
				else
				{
					if (content == null)
						content = toks[n];
					else content += " " + toks[n];
				}
			}
			content = StringEscapeUtils.unescapeHtml(content);
		}
		
		return content;
	}
	
	public static void records(String html)
	{
		if (html == null)
			return;
		// System.out.println(html);
		Document doc = Jsoup.parse(html);
		/*id="dl_newslist"*/
		Elements es = doc.getElementsByAttributeValueContaining("id", "dl_newslist");
		for (int n = 0; n < es.size(); n ++)
		{
			Elements childs = es.get(n).getElementsByTag("table");
			for (int m = 0; m < childs.size(); m ++)
			{
				// align="left";";
				String cnt = null;
				String title = null ,time = null;
	
				Elements s = childs.get(m).getElementsByAttributeValue("align", "left");
				if (s != null && s.size() > 0)
				{
					Elements ss = s.get(0).getElementsByAttribute("href");
					if (ss != null && ss.size() > 0)
					{
						String ur = ss.get(0).attr("href");
						cnt = pageInfo("http://www.cityplan.gov.cn/" + ur);						
					}
					title = StringEscapeUtils.unescapeHtml(s.get(0).text());
				}
	
				s = childs.get(m).getElementsByAttributeValue("align", "right");
				if (s != null && s.size() > 0)
				{
					time = StringEscapeUtils.unescapeHtml(s.get(0).text());
	
				}
	
				if (title != null)
				{
					String out = "<POI><NAME>" + title + "</NAME>";
					if (time != null)
					{
						out += "<TIME>" + time + "</TIME>";
					}
					if (cnt != null)
					{
						out += "<CONTENT>" + cnt.trim() + "</CONTENT>";
					}
					out += "</POI>";
	
					FileTool.Dump(out,  "D:\\temp\\crawldb\\plan\\tj_plan.txt", "UTF-8");
				}
			}
		}
	}
	public static void parse(String url) {
		
		final WebClient webClient = new WebClient();
		HtmlPage page;
		
		try {
			webClient.getOptions().setJavaScriptEnabled(true);				
			webClient.getOptions().setCssEnabled(false);				
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());				
			webClient.getOptions().setTimeout(35000);
			webClient.getOptions().setThrowExceptionOnScriptError(true);
			webClient.getOptions().setPrintContentOnFailingStatusCode(true);

			{
				final List collectedAlerts = new ArrayList();

				webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));

				page = webClient.getPage(url);
				String ht = page.asXml();
				
				records(ht);
				List<HtmlAnchor> issues = (List<HtmlAnchor>) page.getByXPath("//a[@href and contains(text(), '下一页')]");
				
				while (issues != null && issues.size() > 0)
				{
					HtmlPage page1 = (HtmlPage)issues.get(0).click();
					ht = page1.asXml();					
					records(ht);
					
					issues = (List<HtmlAnchor>) page1.getByXPath("//a[@href and contains(text(), '下一页')]");
				}
				
			}
			
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		webClient.closeAllWindows();
	}
	public static void main(String[] args) {
		for (int n = 0; n < tj_urls.length; n ++)
			parse(tj_urls[n]);
	}
}
