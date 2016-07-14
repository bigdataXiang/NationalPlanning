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

// <POI><RID>10</RID><NAME>北京大学第一医院</NAME><URL>http://www.bddyyy.com.cn/</URL><TELEPHONE>010-83572211、010-66551056(院办)</TELEPHONE></POI>
public class BJBddyyy {
	public static String DEPARTMENTS[] = {
		"http://www.bddyyy.com.cn/ksyl/nkxt/sjnk/20100209/10725.shtml","神经内科",
		"http://www.bddyyy.com.cn/ksyl/nkxt/xnk/20091126/2588.shtml","心内科",
		"http://www.bddyyy.com.cn/ksyl/nkxt/hxnk/20091126/2594.shtml","呼吸内科",
		"http://www.bddyyy.com.cn/ksyl/nkxt/xhnk/20091126/2600.shtml","消化内科",
		"http://www.bddyyy.com.cn/ksyl/nkxt/nfmnk/20091126/2603.shtml","内分泌内科",
		"http://www.bddyyy.com.cn/ksyl/nkxt/xynk/20101124/14691.shtml","血液内科",
		"http://www.bddyyy.com.cn/ksyl/nkxt/sznk/20091127/2627.shtml","肾脏内科",
		"http://www.bddyyy.com.cn/ksyl/nkxt/lnb/20091127/2630.shtml","老年病内科",
		"http://www.bddyyy.com.cn/ksyl/nkxt/fsmy/20091127/2645.shtml","风湿免疫科",
		"http://www.bddyyy.com.cn/ksyl/nkxt/dnk/20091127/2647.shtml","大内科",
		"http://www.bddyyy.com.cn/ksyl/wkxt/pw/20091127/2657.shtml","普通外科",
		"http://www.bddyyy.com.cn/ksyl/wkxt/gk/20091127/2668.shtml","骨科",
		"http://www.bddyyy.com.cn/ksyl/wkxt/mnw/20091127/2672.shtml","泌尿外科",
		"http://www.bddyyy.com.cn/ksyl/wkxt/xwk/20091130/2689.shtml","胸外科",
		"http://www.bddyyy.com.cn/ksyl/wkxt/xzwk/20091130/2684.shtml","心脏外科",
		"http://www.bddyyy.com.cn/ksyl/wkxt/zxss/20091130/2683.shtml","整形烧伤外科",
		"http://www.bddyyy.com.cn/ksyl/wkxt/sjwk/20091130/2681.shtml","神经外科",
		"http://www.bddyyy.com.cn/ksyl/wkxt/mzk/20091127/2656.shtml","麻醉科",
		"http://www.bddyyy.com.cn/ksyl/wkxt/jrxg/20091127/2655.shtml","介入血管外科",
		"http://www.bddyyy.com.cn/ksyl/wkxt/nk/20091127/2642.shtml","男科中心",
		"http://www.bddyyy.com.cn/ksyl/fe/fck/20091127/2635.shtml","妇产科",
		"http://www.bddyyy.com.cn/ksyl/fe/ek/20091127/2628.shtml","儿科",
		"http://www.bddyyy.com.cn/ksyl/fe/xewk/20091127/2626.shtml","小儿外科",
		"http://www.bddyyy.com.cn/ksyl/wgk/yk/20091126/2619.shtml","眼科",
		"http://www.bddyyy.com.cn/ksyl/wgk/xeyk/20091126/2617.shtml","小儿眼科",
		"http://www.bddyyy.com.cn/ksyl/wgk/ebhtj/20091126/2616.shtml","耳鼻咽喉-头颈外科",
		"http://www.bddyyy.com.cn/ksyl/wgk/kqk/20091126/2609.shtml","口腔科",
		"http://www.bddyyy.com.cn/ksyl/yjks/blk/20091130/2697.shtml","病理科",
		"http://www.bddyyy.com.cn/ksyl/yjks/kfk/20091130/2699.shtml","康复医学科",
		"http://www.bddyyy.com.cn/ksyl/yjks/cszd/20091130/2703.shtml","超声诊断中心",
		"http://www.bddyyy.com.cn/ksyl/yjks/jyk/20091130/2709.shtml","检验科",
		"http://www.bddyyy.com.cn/ksyl/yjks/yjk/20091130/2713.shtml","药剂科",
		"http://www.bddyyy.com.cn/ksyl/yjks/sxk/20100316/11081.shtml","输血科",
		"http://www.bddyyy.com.cn/ksyl/yjks/djs/20091202/2789.shtml","电镜室",
		"http://www.bddyyy.com.cn/ksyl/yjks/yxyx/20091202/2781.shtml","医学影像科",
		"http://www.bddyyy.com.cn/ksyl/yjks/hyx/20091202/2780.shtml","核医学科",
		"http://www.bddyyy.com.cn/ksyl/qtks/pfxb/20091207/3416.shtml","皮肤性病科",
		"http://www.bddyyy.com.cn/ksyl/qtks/grjb/20091202/2778.shtml","感染疾病科",
		"http://www.bddyyy.com.cn/ksyl/qtks/zy/20091202/2777.shtml","中医、中西医结合科",
		"http://www.bddyyy.com.cn/ksyl/qtks/jzk/20091202/2775.shtml","急诊科",
		"http://www.bddyyy.com.cn/ksyl/qtks/fszl/20091202/2774.shtml","放射治疗科",
		"http://www.bddyyy.com.cn/ksyl/qtks/zlhl/20091202/2770.shtml","肿瘤化疗科",
		"http://www.bddyyy.com.cn/ksyl/qtks/yfbj/20091202/2768.shtml","预防保健科",
		"http://www.bddyyy.com.cn/ksyl/qtks/xlmz/20091202/2767.shtml","心理门诊",
		"http://www.bddyyy.com.cn/ksyl/qtks/kgrbf/20091202/2766.shtml","抗感染病房",
		"http://www.bddyyy.com.cn/ksyl/yjs/lcyl/20091202/2738.shtml","北京大学临床药理研究所"
	};
	
	public static void parseDoctor(String url, int hospital, String partment)
	{
		String content = HTMLTool.fetchURL(url, "gb2312", "get");

		Parser parser = new Parser();
		if (content == null)
		{
			return;
		}
		
		/* 姓名：黄一宁</TD>
		<TD width="50%">性别：男</TD></TR>
		<TR>
		<TD height=22>行政职务：神经内科主任</TD>
		<TD>技术职称：主任医师、教授、博士生导师</TD></TR>
		<TR>
		<TD height=22>专业：内科-神经内科</TD>
		*/
		String poi = null;
		int ins = content.indexOf("姓名：");
		if (ins != -1)
		{
			int ine = content.indexOf("<", ins);
			if (ine != -1)
			{
				String name = content.substring(ins + "姓名：".length(), ine);
				if (!name.isEmpty())
					poi = "<NAME>" + name + "</NAME>";
			}
		}
		
		if (poi == null)
			return;
		ins = content.indexOf("行政职务：");
		String pos1 = null, pos2 = null;
		if (ins != -1)
		{
			int ine = content.indexOf("<", ins);
			if (ine != -1)
			{
				pos2 = content.substring(ins + "行政职务：".length(), ine);				
			}
		}
		ins = content.indexOf("技术职称：");
		if (ins != -1)
		{
			int ine = content.indexOf("<", ins);
			if (ine != -1)
			{
				pos1 = content.substring(ins + "技术职称：".length(), ine);				
			}
		}
		if (pos1 != null && !pos1.isEmpty())
		{
			if (pos2 != null && !pos2.isEmpty())
				pos1 += "," + pos2;
		}
		else
		{
			if (pos2 != null && !pos2.isEmpty())
				pos1 = pos2;
		}
		
		if (pos1 != null && !pos1.isEmpty())
			poi += "<POSITION>" + pos1.replace("、", ",") + "</POSITION>";
		
		int zhuangchang = content.indexOf("专业特长");
		int gerenjianli = content.indexOf("个人简历");
		int shijian = content.indexOf("出诊时间");
		if (zhuangchang != -1)
		{
			ins = content.indexOf("<TD class=bol>");
			if (ins != -1 && ins > zhuangchang && ins < gerenjianli)
			{
				int ine = content.indexOf("<", ins + "<TD class=bol>".length());
				
				if (ine != -1)
				{
					String str = content.substring(ins + "<TD class=bol>".length(), ine).replace("&nbsp;", "").replace("&amp;", "").replace("\t", "").replace("\r", "").replace("\n", "").trim();;
					if (!str.isEmpty())
						poi += "<SKILL>" + str + "</SKILL>";
				}
			}
		}
		
		if (gerenjianli != -1)
		{
			ins = content.indexOf("<TD class=bol>", gerenjianli);
			if (ins != -1 && (ins < shijian && shijian != -1 || shijian == -1))
			{
				int ine = content.indexOf("<", ins + "<TD class=bol>".length());
				if (ine != -1)
				{
					String str = content.substring(ins + "<TD class=bol>".length(), ine).replace("　", "").replace("&nbsp;", "").replace("&amp;", "").replace("\t", "").replace("\r", "").replace("\n", "").trim();;
					if (!str.isEmpty())
						poi += "<INTRODUCTION>" + str + "</INTRODUCTION>";
				}
			}
		}
			
		if (poi != null)
		{
			poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + poi + "<PARTMENT>" + partment + "</PARMENT>" + "<URL>" + url + "</URL></DOCTOR>";
				
			FileTool.Dump(poi, "D:\\temp\\医院\\BJBddyyy.csv", "UTF-8");				
		}
	}
	public static void parsePartmentSheet(String url, int hospital, String partment)
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
			// class="b14"
			NodeList cld = parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("class", "b14")));
			if(cld != null)
			{
				for(int k = 0; k < cld.size(); k++)
				{					
					TagNode parnode = (TagNode) cld.elementAt(k);
					
					String turl = parnode.getAttribute("href");
					
					if (turl != null && turl.indexOf("/zj/") != -1)
						parseDoctor("http://www.bddyyy.com.cn" + turl, hospital, partment);
				}
			}
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 
	}
	
	public static void main(String[] args) throws Exception {

		for (int n = 0; n < DEPARTMENTS.length; n += 2)
		{
			parsePartmentSheet(DEPARTMENTS[n], 10, DEPARTMENTS[n + 1]);
		}
	}
}
