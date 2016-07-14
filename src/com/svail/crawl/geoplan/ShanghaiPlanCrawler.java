package com.svail.crawl.geoplan;

import java.io.File;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.svail.crawl.geoplan.shanghai.SHGhxk;
import com.svail.util.FileTool;
import com.svail.util.HTMLTool;

public class ShanghaiPlanCrawler {
	
	private static String GLOBAL_ID = null;
	
	public static String parsePage(String url, int type) {
		if (url == null)
			return null;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String html = HTMLTool.fetchURL(url, "gb2312", "get");
		if (html == null)
			return null;
		
		/* 建设项目选址意见书
		 * 建设用地规划许可证
		 * 建设工程规划许可证（建筑类）
		 * 建设工程竣工规划验收合格证
		 * 建设工程设计要求
		 * 建设工程设计方案
		 */
		Document doc = Jsoup.parse(html);
		String id = "选字第"; 	// 建设项目选址意见书
		if (type == 1)  		// 建设用地规划许可证
			id = "地字第";
		else if (type == 2) 	// 建设工程规划许可证 
			id = "建字第";
		else if (type > 2)
			id = "编 号"; 		// 建设工程竣工规划验收
								// 建设工程设计要求
								// 建设工程设计方案
		Elements nets = doc.getElementsContainingOwnText(id);
		
		if (nets.size() == 0)
		{
			id = "发证编号";
			nets = doc.getElementsContainingOwnText(id);
			if (nets.size() == 0)
				return null;
		}
		
		String txt = nets.get(0).parent().text().trim();
		
		int x = txt.indexOf(id);
		if (x == -1)
			return null;
		
		txt = txt.substring(x + id.length());
		if (GLOBAL_ID == null)
			GLOBAL_ID = txt;
		
		nets = doc.getElementsContainingOwnText("日 期");
		String time = null;
		if (nets.size() > 0)
		{
			time  = nets.get(0).text().trim();
		
			x = time.indexOf("日 期");
			if (x != -1)
				time = time.substring(x + "日 期".length()).replace("&nbsp;", "").replace("：", "").trim();
			else {
				time = null;
			}
		}
		else {
			nets = doc.getElementsContainingOwnText("日期");
			if (nets.size() > 0)
			{
				time  = nets.get(0).text().trim();
				x = time.indexOf("日期");
				if (x != -1)
					time = time.substring(x + "日期".length()).replace("&nbsp;", "").replace("：", "").trim();
				else {
					time = null;
				}
			}
		}
		
		if (type == 0) {
			
			// 建设项目名称	松江区佘山镇佘山北大型居住社区18A-02A号
			// 建设单位名称	上海市松江区土地储备中心
			// 建设项目依据	
			// 建设项目拟选位置	佘山北大型居住社区
			// 拟用地面积	2438.7
			// 拟建设规模
			/* 建设单位名称 	上海市嘉定区土地储备开发中心
			建设项目名称 	江桥镇黄家花园路以西、靖远路以南地块
			建设用地位置 	江桥镇
			建设工程性质 	工业厂房
			用地规划性质 	工业
			建设用地面积 */
			String kw = "建设项目名称";
			nets = doc.getElementsContainingOwnText(kw);
			
			String projName = null, projOrg = null, projResean = null, projAddr = null, projArea = null, projVolumn = null ;
			
			if (nets.size() > 0)
			{
				projName = nets.get(0).parent().parent().text().trim();
				x = projName.indexOf(kw);
				if (x != -1)
					projName = projName.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projName = null;
			}
			
			kw = "建设单位名称";
			nets = doc.getElementsContainingOwnText(kw);
			
			if (nets.size() > 0)
			{
				projOrg = nets.get(0).parent().parent().text().trim();
				x = projOrg.indexOf(kw);
				if (x != -1)
					projOrg = projOrg.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projOrg = null;
			}
			// 建设单位名称	上海市松江区土地储备中心
			kw = "建设单位名称";
			nets = doc.getElementsContainingOwnText(kw);
			
			if (nets.size() > 0)
			{
				projOrg = nets.get(0).parent().parent().text().trim();
				x = projOrg.indexOf(kw);
				if (x != -1)
					projOrg = projOrg.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projOrg = null;
			}
			
			// 建设项目依据	
			kw = "建设项目依据";
			nets = doc.getElementsContainingOwnText(kw);
			
			if (nets.size() > 0)
			{
				projResean = nets.get(0).parent().parent().text().trim();
				x = projResean.indexOf(kw);
				if (x != -1)
					projResean = projResean.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projResean = null;
			}
			
			kw = " 建设工程性质";
			nets = doc.getElementsContainingOwnText(kw);
			
			if (nets.size() > 0)
			{
				projResean = nets.get(0).parent().parent().text().trim();
				x = projResean.indexOf(kw);
				if (x != -1)
					projResean = projResean.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projResean = null;
			}
			
			// 建设项目拟选位置	佘山北大型居住社区
			kw = "建设项目拟选位置";
			nets = doc.getElementsContainingOwnText(kw);
			
			if (nets.size() > 0)
			{
				projAddr = nets.get(0).parent().parent().text().trim();
				x = projAddr.indexOf(kw);
				if (x != -1)
					projAddr = projAddr.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projAddr = null;
			}
			else {
				kw = "建设用地位置";
				nets = doc.getElementsContainingOwnText(kw);
				
				if (nets.size() > 0)
				{
					projAddr = nets.get(0).parent().parent().text().trim();
					x = projAddr.indexOf(kw);
					if (x != -1)
						projAddr = projAddr.substring(x + kw.length()).replace("&nbsp;", "").trim();
					else
						projAddr = null;
				}
			}
			
			// 拟用地面积	2438.7
			kw = "拟用地面积";
			nets = doc.getElementsContainingOwnText(kw);
			
			if (nets.size() > 0)
			{
				projArea = nets.get(0).parent().parent().text().trim();
				x = projArea.indexOf(kw);
				if (x != -1)
					projArea = projArea.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projArea = null;
			}
			else {
				kw = "建设用地面积";
				nets = doc.getElementsContainingOwnText(kw);
				
				if (nets.size() > 0)
				{
					projArea = nets.get(0).parent().parent().text().trim();
					x = projArea.indexOf(kw);
					if (x != -1)
						projArea = projArea.substring(x + kw.length()).replace("&nbsp;", "").trim();
					else
						projArea = null;
				}	
			}
			
			
			// 拟建设规模
			kw = "拟建设规模";
			nets = doc.getElementsContainingOwnText(kw);
			
			if (nets.size() > 0)
			{
				projVolumn = nets.get(0).parent().parent().text().trim();
				x = projVolumn.indexOf(kw);
				if (x != -1)
					projVolumn = projVolumn.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projVolumn = null;
			}
			String rs = null;
			if (projName != null && !projName.isEmpty())
			{
				rs = "<NAME>" + projName + "</NAME>";
			}
			
			if (projAddr != null && !projAddr.isEmpty())
			{
				if (rs == null)
					rs = "<ADDRESS>" + projAddr + "</ADDRESS>";
				else
					rs += "<ADDRESS>" + projAddr + "</ADDRESS>";
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
			if (projResean != null && !projResean.isEmpty())
			{
				if (rs == null)
					rs = "<RESEAN>" + projResean + "</RESEAN>";
				else
					rs += "<RESEAN>" + projResean + "</RESEAN>";
			}
			if (projVolumn != null && !projVolumn.isEmpty())
			{
				if (rs == null)
					rs = "<VOLUMN>" + projVolumn + "</VOLUMN>";
				else
					rs += "<VOLUMN>" + projVolumn + "</VOLUMN>";
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
			return "<ID>" + txt + "</ID>" + rs;
		}else if (type == 1) {
			/* 用 地 单 位	上海市土地储备中心、上海市奉贤区土地储备中心
			用地项目名称	土地储备（大型居住区奉贤区南桥基地14-11A-02A）
			用 地 位 置	金汇镇东至万顺路、南至百团路、西至锦墩路、北至文明东街
			用 地 性 质	
			用 地 面 积	48100.1
			建 设 规 模	0
			*/
			String kw = "用地项目名称";
			nets = doc.getElementsContainingOwnText(kw);
			
			String projName = null, projOrg = null, projResean = null, projAddr = null, projArea = null, projVolumn = null ;
			
			if (nets.size() > 0)
			{
				projName = nets.get(0).parent().text().trim();
				x = projName.indexOf(kw);
				if (x != -1)
					projName = projName.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projName = null;
			}
			
			kw = "用 地 单 位";
			nets = doc.getElementsContainingOwnText(kw);
			
			if (nets.size() > 0)
			{
				projOrg = nets.get(0).parent().text().trim();
				x = projOrg.indexOf(kw);
				if (x != -1)
					projOrg = projOrg.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projOrg = null;
			}
			
			kw = "用 地 性 质";
			nets = doc.getElementsContainingOwnText(kw);

			if (nets.size() > 0)
			{
				projResean = nets.get(0).parent().text().trim();
				x = projResean.indexOf(kw);
				if (x != -1)
					projResean = projResean.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projResean = null;
			}

			kw = "用 地 位 置";
			nets = doc.getElementsContainingOwnText(kw);

			if (nets.size() > 0)
			{
				projAddr = nets.get(0).parent().text().trim();
				x = projAddr.indexOf(kw);
				if (x != -1)
					projAddr = projAddr.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projAddr = null;
			}

			kw = "用 地 面 积";
			nets = doc.getElementsContainingOwnText(kw);

			if (nets.size() > 0)
			{
				projArea = nets.get(0).parent().text().trim();
				x = projArea.indexOf(kw);
				if (x != -1)
					projArea = projArea.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projArea = null;
			}

			kw = "建 设 规 模";
			nets = doc.getElementsContainingOwnText(kw);

			if (nets.size() > 0)
			{
				projVolumn = nets.get(0).parent().text().trim();
				x = projVolumn.indexOf(kw);
				if (x != -1)
					projVolumn = projVolumn.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projVolumn = null;
			}
			String rs = null;
			if (projName != null && !projName.isEmpty())
			{
				rs = "<NAME>" + projName + "</NAME>";
			}

			if (projAddr != null && !projAddr.isEmpty())
			{
				if (rs == null)
					rs = "<ADDRESS>" + projAddr + "</ADDRESS>";
				else
					rs += "<ADDRESS>" + projAddr + "</ADDRESS>";
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
			if (projResean != null && !projResean.isEmpty())
			{
				if (rs == null)
					rs = "<TYPE>" + projResean + "</TYPE>";
				else
					rs += "<TYPE>" + projResean + "</TYPE>";
			}
			if (projVolumn != null && !projVolumn.isEmpty())
			{
				if (rs == null)
					rs = "<VOLUMN>" + projVolumn + "</VOLUMN>";
				else
					rs += "<VOLUMN>" + projVolumn + "</VOLUMN>";
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
			
			return "<ID>" + txt + "</ID>" + rs;
		}else if (type == 2) {
			
			/* 建设单位（个人）	上海长兴岛开发建设有限公司
			建设项目名称	长兴岛潘圆公路圆南景观绿地（即圆沙公园）三期工程
			建 设 位 置	长兴镇
			建 设 规 模	487.9
			*/
			String kw = "建设项目名称";
			nets = doc.getElementsContainingOwnText(kw);
			
			String projName = null, projOrg = null, projAddr = null, projVolumn = null ;
			
			if (nets.size() > 0)
			{
				projName = nets.get(0).parent().text().trim();
				x = projName.indexOf(kw);
				if (x != -1)
					projName = projName.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projName = null;
			}
			
			kw = "建设单位（个人）";
			nets = doc.getElementsContainingOwnText(kw);
			
			if (nets.size() > 0)
			{
				projOrg = nets.get(0).parent().text().trim();
				x = projOrg.indexOf(kw);
				if (x != -1)
					projOrg = projOrg.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projOrg = null;
			}
			
			kw = "建 设 位 置";
			nets = doc.getElementsContainingOwnText(kw);

			if (nets.size() > 0)
			{
				projAddr = nets.get(0).parent().text().trim();
				x = projAddr.indexOf(kw);
				if (x != -1)
					projAddr = projAddr.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projAddr = null;
			}

			kw = "建 设 规 模";
			nets = doc.getElementsContainingOwnText(kw);

			if (nets.size() > 0)
			{
				projVolumn = nets.get(0).parent().text().trim();
				x = projVolumn.indexOf(kw);
				if (x != -1)
					projVolumn = projVolumn.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projVolumn = null;
			}
			String rs = null;
			if (projName != null && !projName.isEmpty())
			{
				rs = "<NAME>" + projName + "</NAME>";
			}

			if (projAddr != null && !projAddr.isEmpty())
			{
				if (rs == null)
					rs = "<ADDRESS>" + projAddr + "</ADDRESS>";
				else
					rs += "<ADDRESS>" + projAddr + "</ADDRESS>";
			}

			if (projOrg != null && !projOrg.isEmpty())
			{
				if (rs == null)
					rs = "<ORG>" + projOrg + "</ORG>";
				else
					rs += "<ORG>" + projOrg + "</ORG>";
			}
			if (projVolumn != null && !projVolumn.isEmpty())
			{
				if (rs == null)
					rs = "<VOLUMN>" + projVolumn + "</VOLUMN>";
				else
					rs += "<VOLUMN>" + projVolumn + "</VOLUMN>";
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
			
			return "<ID>" + txt + "</ID>" + rs;
		}
		else
		{
			/*
			 * 建设单位名称	上海轨道交通建设二期发展有限公司
			      建设项目名称	上海市轨道交通4号线蒲汇塘停车场综合楼
			      建设地点	徐汇区桂林路909号
			 */
			String kw = "建设项目名称";
			nets = doc.getElementsContainingOwnText(kw);
			
			String projName = null, projOrg = null, projAddr = null, projVolumn = null ;
			
			if (nets.size() > 0)
			{
				projName = nets.get(0).parent().parent().parent().text().trim();
				x = projName.indexOf(kw);
				if (x != -1)
					projName = projName.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projName = null;
			}
			
			kw = "建设单位名称";
			nets = doc.getElementsContainingOwnText(kw);
			
			if (nets.size() > 0)
			{
				projOrg = nets.get(0).parent().parent().parent().text().trim();
				x = projOrg.indexOf(kw);
				if (x != -1)
					projOrg = projOrg.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projOrg = null;
			}
			
			kw = "建设地点";
			nets = doc.getElementsContainingOwnText(kw);

			if (nets.size() > 0)
			{
				projAddr = nets.get(0).parent().parent().parent().text().trim();
				x = projAddr.indexOf(kw);
				if (x != -1)
					projAddr = projAddr.substring(x + kw.length()).replace("&nbsp;", "").trim();
				else
					projAddr = null;
			}
			String rs = null;
			if (projName != null && !projName.isEmpty())
			{
				rs = "<NAME>" + projName + "</NAME>";
			}

			if (projAddr != null && !projAddr.isEmpty())
			{
				if (rs == null)
					rs = "<ADDRESS>" + projAddr + "</ADDRESS>";
				else
					rs += "<ADDRESS>" + projAddr + "</ADDRESS>";
			}

			if (projOrg != null && !projOrg.isEmpty())
			{
				if (rs == null)
					rs = "<ORG>" + projOrg + "</ORG>";
				else
					rs += "<ORG>" + projOrg + "</ORG>";
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
			
			return "<ID>" + txt + "</ID>" + rs;
		}
	}
	
	public static boolean items(String value, String fileName, int pn, String key, int type)
	{
		if (value == null)
			return false;
		String url = "http://www.shgtj.gov.cn/i/ghsp/ghxk/?pn=" + pn + "&ps=50&key=" + value;
		
		String html = HTMLTool.fetchURL(url, "utf-8", "get");
		if (html == null)
			return false;
		
		// 返回为json数据
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();

		try {
			JsonElement el = parser.parse(html);

			//把JsonElement对象转换成JsonObject
			JsonObject jsonObj = null;
			if(el.isJsonObject())
			{
				jsonObj = el.getAsJsonObject();
				SHGhxk gq = gson.fromJson(jsonObj, SHGhxk.class);
				if (GLOBAL_COUNT == 9999999)
					GLOBAL_COUNT = gq.getData().getTotal();
				
				if (gq != null && gq.getData() != null && gq.getData().getList().size() > 0)
				{
					for (int n = 0; n < gq.getData().getList().size(); n ++) {
						String rts = parsePage(gq.getData().getList().get(n).getDocpuburl(), type);
						// String rts = parsePage("http://www.shgtj.gov.cn/ghsp/ghxk/xmxzyjs/200901/t20090102_229007.html", type);
	
						if (rts == null)
						{
							System.out.println("Error: " + gq.getData().getList().get(n).getDocpuburl());
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
				}
			}

		}catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	private static int GLOBAL_COUNT = 9999999;
	public static void main(String[] args) {
		
		String SPECIAL_URLS[] = {
			"http://www.shgtj.gov.cn/gsgg/bdc/bdcgg/", // 不动产登记公告 
			"http://www.shgtj.gov.cn/gsgg/bdc/bdcgg/bzsm/",	// 补证声明  "
			"http://www.shgtj.gov.cn/gsgg/dmgg/", // 地名公告
			
			"http://www.shgtj.gov.cn/ghsp/ghsp/",			// 规划审批   http://www.shgtj.gov.cn/ghsp/ghsp/index_2.html
		};
		
		String jsonURLS[] = {
			"2260",// "http://www.shgtj.gov.cn/ghsp/ghxk/xmxzyjs/",  	// 建设项目选址意见书: http://www.shgtj.gov.cn/i/ghsp/ghxk/?pn=5&ps=10&key=2260
			"2241",// "http://www.shgtj.gov.cn/ghsp/ghxk/ghxkz/",  	// 建设用地规划许可证: http://www.shgtj.gov.cn/i/ghsp/ghxk/?pn=2&ps=10&key=2241
			"2242",// "http://www.shgtj.gov.cn/ghsp/ghxk/xkzjzl/", 	// 建设工程规划许可证（建筑类） http://www.shgtj.gov.cn/i/ghsp/ghxk/?pn=2&ps=40&key=2242
			"2244",// "http://www.shgtj.gov.cn/ghsp/ghxk/ghys/", 		// 建设工程竣工规划验收合格证     http://www.shgtj.gov.cn/i/ghsp/ghxk/?pn=2&ps=10&key=2244
			"2245",// "http://www.shgtj.gov.cn/ghsp/ghxk/gcsjyq/", 	// 建设工程设计要求  http://www.shgtj.gov.cn/i/ghsp/ghxk/?pn=2&ps=10&key=2245
			"2246",// "http://www.shgtj.gov.cn/ghsp/ghxk/gcsjfa/", 	// 建设工程设计方案 http://www.shgtj.gov.cn/i/ghsp/ghxk/?pn=2&ps=10&key=2246
		};
		
		String jsonURLS_Name[] = {
			"建设项目选址意见书",
			"建设用地规划许可证",
			"建设工程规划许可证（建筑类）",
			"建设工程竣工规划验收合格证 ",
			"建设工程设计要求 ",
			"建设工程设计方案",
		};
		
		for (int n = 3; n < jsonURLS.length; n ++)
		{
			System.out.println(jsonURLS[n]);
			Vector<String> strs = FileTool.Load("/home/sinan/workspace/crawldb/plan/Shanghai_" + jsonURLS_Name[n] + "_log.txt", "UTF-8");
			String key = null;
			if (strs != null && strs.size() > 0) {
				key = strs.get(0);
			}
			int pn = 1;
			
			while (GLOBAL_COUNT > 0)
			{
				items(jsonURLS[n], "Shanghai_" + jsonURLS_Name[n], pn ++, key, n);
				GLOBAL_COUNT -= 50;
			}
			
			GLOBAL_COUNT = 9999999;
			
			File file = new File("/home/sinan/workspace/crawldb/plan/Shanghai_" + jsonURLS_Name[n] + "_log.txt");
			file.delete();
			
			if (GLOBAL_ID != null)
				FileTool.Dump(GLOBAL_ID, "/home/sinan/workspace/crawldb/plan/Shanghai_" + jsonURLS_Name[n] + "_log.txt", "UTF-8");
			else
				FileTool.Dump(key, "/home/sinan/workspace/crawldb/plan/Shanghai_" + jsonURLS_Name[n] + "_log.txt", "UTF-8");
			
			GLOBAL_ID = null;
		}
	}
}
