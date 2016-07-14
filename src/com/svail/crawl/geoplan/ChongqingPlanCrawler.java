package com.svail.crawl.geoplan;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.svail.util.FileTool;
import com.svail.util.HTMLTool;

public class ChongqingPlanCrawler {

	/*
	 * 沪奉书(2016)BA31012020164634
	 * */
	public static String GLOBAL_ID = null;
	
	public static String parsePage(String url, int type) {
		if (url == null)
			return null;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String html = HTMLTool.fetchURL(url, "UTF-8", "get");
		if (html == null)
			return null;
		
		/* 建设工程选址意见书
		 * 建设用地规划许可证
		 * 建设工程规划许可证
		 * 竣工规划核实确认书
		 */
		Document doc = Jsoup.parse(html);
		
		String id = "报建编号";
		
		Elements nets = doc.getElementsContainingOwnText(id);
		
		String txt = nets.get(0).text().trim();
		
		int x = txt.indexOf(id);
		if (x == -1)
			return null;
		
		txt = txt.substring(x + id.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
		
		if (txt.isEmpty())
		{
			txt = nets.get(0).parent().text().trim();
			
			x = txt.indexOf(id);
			if (x == -1)
				return null;
			
			txt = txt.substring(x + id.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
		}
		
		// 办结日期： 2016/7/8 9:26:17
		nets = doc.getElementsContainingOwnText("日期");
		String time = null;
		
		if (nets.size() > 0)
		{
			time  = nets.get(0).text().trim();
		
			x = time.indexOf("日期");
			if (x != -1)
			{
				time = time.substring(x + "日期".length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
				if (time.isEmpty())
				{
					time  = nets.get(0).parent().text().trim();
					
					x = time.indexOf("日期");
					if (x != -1)
					{
						time = time.substring(x + "日期".length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
					}
					else
						time = null;
					
				}
			}
			else {
				time = null;
			}
		}
		String kw = "建设项目";
		nets = doc.getElementsContainingOwnText(kw);
			
		String projName = null, projOrg = null, projArea = null, projDocId = null ;
			
		if (nets.size() > 0)
		{
			projName = nets.get(0).text().trim();
			x = projName.indexOf(kw);
			if (x != -1)
			{
				projName = projName.substring(x + kw.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
				if (projName.isEmpty())
				{
					projName = nets.get(0).parent().text().trim();
					x = projName.indexOf(kw);
					if (x != -1)
					{
						projName = projName.substring(x + kw.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
					}
					else
						projName = null;
				}
			}
			else
				projName = null;
		}
			
		kw = "建设单位";
		nets = doc.getElementsContainingOwnText(kw);
			
		if (nets.size() > 0)
		{
			projOrg = nets.get(0).text().trim();
			x = projOrg.indexOf(kw);
			if (x != -1)
			{
				projOrg = projOrg.substring(x + kw.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
				if (projOrg.isEmpty()) 
				{
					projOrg = nets.get(0).parent().text().trim();
					x = projOrg.indexOf(kw);
					if (x != -1)
					{
						projOrg = projOrg.substring(x + kw.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
						if (projOrg.isEmpty())
						{
							projOrg = nets.get(0).parent().parent().text().trim();
							
							projOrg = projOrg.substring(x + kw.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
							if (projOrg.isEmpty()) 
							{
								projOrg = nets.get(0).parent().text().trim();
								x = projOrg.indexOf(kw);
								if (x != -1)
								{
									projOrg = projOrg.substring(x + kw.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
								}
								else
									projOrg = null;
							}
						}		
					}
					else
						projOrg = null;
				}
			}
			else
				projOrg = null;
		}
		// 文号 	上海市松江区土地储备中心
		kw = "文号";
		nets = doc.getElementsContainingOwnText(kw);
			
		if (nets.size() > 0)
		{
			projDocId = nets.get(0).text().trim();
			x = projDocId.indexOf(kw);
			if (x != -1)
			{
				projDocId = projDocId.substring(x + kw.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
				if (projDocId.isEmpty()) {
					projDocId = nets.get(0).parent().text().trim();
					x = projDocId.indexOf(kw);
					if (x != -1)
					{
						projDocId = projDocId.substring(x + kw.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
					}
					else
						projDocId = null;
				}
			}
			else
				projDocId = null;
		}
			
		// 地块面积	
		kw = "地块面积";
		nets = doc.getElementsContainingOwnText(kw);
			
		if (nets.size() > 0)
		{
			projArea = nets.get(0).text().replace("：", "").replace(":", "").trim();
			x = projArea.indexOf(kw);
			if (x != -1)
			{
				projArea = projArea.substring(x + kw.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
				if (projArea.isEmpty())
				{
					projArea = nets.get(0).parent().text().replace("：", "").replace(":", "").trim();
					x = projArea.indexOf(kw);
					if (x != -1)
					{
						projArea = projArea.substring(x + kw.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
						
					}
					else
						projArea = null;
				}
			}
			else
				projArea = null;
		}
		else {
			// 建设规模:74756.82平方米
			kw = "建设规模";
			nets = doc.getElementsContainingOwnText(kw);
			
			if (nets.size() > 0)
			{
				projArea = nets.get(0).text().replace("：", "").replace(":", "").trim();
				x = projArea.indexOf(kw);
				if (x != -1)
				{
					projArea = projArea.substring(x + kw.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
					if (projArea.isEmpty())
					{
						projArea = nets.get(0).parent().text().replace("：", "").replace(":", "").trim();
						x = projArea.indexOf(kw);
						if (x != -1)
						{
							projArea = projArea.substring(x + kw.length()).replace("&nbsp;", "").replace("：", "").replace(":", "").trim();
						}
						else
							projArea = null;
					}
				}
				else
					projArea = null;
			}
		}
		String rs = null;
		if (projName != null && !projName.isEmpty())
		{
			rs = "<NAME>" + projName + "</NAME>";
		}
		
		if (projOrg != null && !projOrg.isEmpty())
		{
			if (rs == null)
				rs = "<ORG>" + projOrg + "</ORG>";
			else
				rs += "<ORG>" + projOrg + "</ORG>";
		}
		if (projArea != null && !projArea.isEmpty())
		{
			if (rs == null)
				rs = "<AREA>" + projArea + "</AREA>";
			else
				rs += "<AREA>" + projArea + "</AREA>";
		}
		if (projDocId != null && !projDocId.isEmpty())
		{
			if (rs == null)
				rs = "<DOCID>" + projDocId + "</DOCID>";
			else
				rs += "<DOCID>" + projDocId + "</DOCID>";
		}
		if (time != null && !time.isEmpty())
		{
			if (rs == null)
				rs = "<TIME>" + time + "</TIME>";
			else
				rs += "<TIME>" + time + "</TIME>";
		}
		if (rs == null)
			rs = "<URL>" + url + "</URL>";
		else
			rs += "<URL>" + url + "</URL>";
		
		if (GLOBAL_ID == null)
			GLOBAL_ID = txt;
		
		return "<ID>" + txt + "</ID>" + rs;
	}

	public static boolean crawl(String url, String fileName, String key) {
		
		// 等待数据加载的时间
		// 为了防止服务器封锁，这里的时间要模拟人的动作，随机太短
		long waitLoadBaseTime = 3000;
		int waitLoadRandomTime = 3000;
		Random random = new Random(System.currentTimeMillis());

		// System.getProperties().setProperty("webdriver.chrome.driver",
		//		chromeDriverPath);

		// System.getProperties().setProperty("webdriver.firefox.bin","E://software//firefox//firefox.exe");
		FirefoxProfile profile = new FirefoxProfile();
		profile.setEnableNativeEvents(true);
		WebDriver driver = new FirefoxDriver(profile);
		// WebDriver driver = new FirefoxDriver();
		// WebDriver driver = new ChromeDriver();
		//要抓取的网页
		driver.get(url); //等待页面动态加载完毕
		while(true)
		{
			try {
				Thread.sleep(waitLoadBaseTime + random.nextInt(waitLoadRandomTime));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String html = driver.getPageSource();
			
			Document doc = Jsoup.parse(html);
			
			Elements nets = doc.getElementsByAttributeValueStarting("href", "wsfw_1S3Z_content.aspx?");
			
			for (int n = 0; n < nets.size(); n ++) {
				// 提取详细信息
				String rts = parsePage("http://www.cqupb.gov.cn/wsfw/" + nets.get(n).attr("href"), -1);
				if (rts == null) {
					System.out.println("Error: " + "http://www.cqupb.gov.cn/wsfw/" + nets.get(n).attr("href"));
					continue;
				}
				int s = rts.indexOf("<ID>");
				if (s != -1) {
					int e = rts.indexOf("</ID>");

					if (e != -1) {
						String id = rts.substring(s + "<ID>".length(), e);
						if (key != null && key.equalsIgnoreCase(id))
						{
							return true;
						}
					}

					String cnt = "<POI>" + rts + "</POI>";
					
					FileTool.Dump(cnt, "/home/sinan/workspace/crawldb/plan/" + fileName + ".txt", "UTF-8");
					
				}				
			}
			WebElement e = driver.findElement(By.id("btnNext"));
			
			if (e.isEnabled())
				e.click();
			else
				break;
		//等待页面动态加载完毕

			try {
				Thread.sleep(waitLoadBaseTime + random.nextInt(waitLoadRandomTime));
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		//输出内容
		//找到标题元素
		//关闭浏览器
		driver.close();
		return false;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String urls[] = {
			"http://www.cqupb.gov.cn/wsfw/wsfw_1S3Z_listDetails.aspx?classname=%u5efa%u8bbe%u5de5%u7a0b%u9009%u5740%u610f%u89c1%u4e66",
			"建设工程选址意见书",
			"http://www.cqupb.gov.cn/wsfw/wsfw_1S3Z_listDetails.aspx?classname=%u5efa%u8bbe%u7528%u5730%u89c4%u5212%u8bb8%u53ef%u8bc1",
			"建设用地规划许可证",
			"http://www.cqupb.gov.cn/wsfw/wsfw_1S3Z_listDetails.aspx?classname=%E5%BB%BA%E8%AE%BE%E5%B7%A5%E7%A8%8B%E8%A7%84%E5%88%92%E8%AE%B8%E5%8F%AF%E8%AF%81",
			"建设工程规划许可证",
			"http://www.cqupb.gov.cn/wsfw/wsfw_1S3Z_listDetails.aspx?classname=%E7%AB%A3%E5%B7%A5%E8%A7%84%E5%88%92%E6%A0%B8%E5%AE%9E%E7%A1%AE%E8%AE%A4%E4%B9%A6",
			"竣工规划核实确认书"
		};
		
		for (int n = 6; n < urls.length; n += 2) {
			Vector<String> strs = FileTool.Load("D:\\Temp\\crawldb\\plan\\Chongqing_" + urls[n + 1] + "_log.txt", "UTF-8");
			String key = null;
			if (strs != null && strs.size() > 0) {
				key = strs.get(0);
			}
			crawl(urls[n], "Chongqing_" + urls[n + 1], key);
			File file = new File("D:\\Temp\\crawldb\\plan\\Chongqing_" + urls[n + 1] + "_log.txt");
			file.delete();
			
			assert(GLOBAL_ID != null);
			
			if (GLOBAL_ID != null)
				FileTool.Dump(GLOBAL_ID, "D:\\Temp\\crawldb\\plan\\Chongqing_" + urls[n + 1] + "_log.txt", "UTF-8");
			else
				FileTool.Dump(key, "D:\\Temp\\crawldb\\plan\\Chongqing_" + urls[n + 1] + "_log.txt", "UTF-8");
			GLOBAL_ID = null;
		}
	}

}
