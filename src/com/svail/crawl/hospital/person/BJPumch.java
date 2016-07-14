package com.svail.crawl.hospital.person;

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

// 协和医院
// http://www.pumch.cn
public class BJPumch {
	private static String PARTMENTS[] = {
		"/Category_173/Index.aspx","内分泌科",	
		"/Category_172/Index.aspx","内科学系",
		"/Category_174/Index.aspx","心内科",
		"/Category_188/Index.aspx","呼吸内科",
		"/Category_189/Index.aspx","消化内科",
		"/Category_190/Index.aspx","肾内科",
		"/Category_191/Index.aspx","血液内科",
		"/Category_175/Index.aspx","风湿免疫科",
		"/Category_206/Index.aspx","感染内科",
		"/Category_207/Index.aspx","普通内科",
		"/Category_208/Index.aspx","肿瘤内科",
		"/Category_209/Index.aspx","MICU",
			
		"/Category_211/Index.aspx","肠外肠内营养科",
		"/Category_213/Index.aspx","重症医疗科（ICU）",
		"/Category_202/Index.aspx","儿科",
		"/Category_197/Index.aspx","神经科",
		"/Category_203/Index.aspx","心理医学科",
		"/Category_180/Index.aspx","皮肤科",
		"/Category_214/Index.aspx","变态反应科",
		"/Category_204/Index.aspx","急诊科",
		"/Category_181/Index.aspx","中医科",
		"/Category_1254/Index.aspx","老年医学组",
		"/Category_192/Index.aspx","外科学系",
		"/Category_193/Index.aspx","基本外科",
		"/Category_176/Index.aspx","骨科",
		"/Category_194/Index.aspx","心外科",
		"/Category_223/Index.aspx","胸外科",
		"/Category_195/Index.aspx","泌尿外科",
		"/Category_196/Index.aspx","神经外科",
		"/Category_177/Index.aspx","血管外科",
		"/Category_178/Index.aspx","整形美容外科",
		"/Category_210/Index.aspx","乳腺外科",
		"/Category_179/Index.aspx","肝脏外科",
		"/Category_212/Index.aspx","麻醉科",
		"/Category_184/Index.aspx","妇产科",
		"/Category_198/Index.aspx","眼科",
		"/Category_199/Index.aspx","耳鼻喉科",
		"/Category_200/Index.aspx","口腔科",
		"/Category_216/Index.aspx","超声诊断科",
		"/Category_183/Index.aspx","病理科",
		"/Category_201/Index.aspx","检验科",
		"/Category_205/Index.aspx","放射科",
		"/Category_182/Index.aspx","放射治疗科",
		"/Category_218/Index.aspx","物理医学康复科",
		"/Category_215/Index.aspx","核医学科",
		"/Category_219/Index.aspx","营养部",
		"/Category_221/Index.aspx","输血科",
		"/Category_217/Index.aspx","药剂科",
		"/Category_220/Index.aspx","病案科;疾病分类中心",
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
			
			// <h4 class="PersonName">严晓伟<span class="">
			NodeFilter helper1 = new AndFilter(new TagNameFilter("h4"), new HasAttributeFilter("class", "PersonName"));
			
			String poi = null;
			NodeList cld = parser.extractAllNodesThatMatch(helper1);
			if(cld != null)
			{
				TagNode parnode = (TagNode) cld.elementAt(0);
				String name = parnode.toPlainTextString();
				if (name.indexOf("function") != -1)
					name = name.substring(0, name.indexOf("function"));

				name = name.replace("&nbsp;", "").replace("&amp;", "").replace("\t", "").replace("\r", "").replace("\n", "").trim();
				poi = "<NAME>" + name + "</NAME>";
			}
			
			if (poi == null)
				return;
			
			parser.reset();
			
			// <li class="PersonOther_proName"><strong>职称：内科学系副主任 教授 主任医师 博士研究生导师</strong></li>
			helper1 = new AndFilter(new TagNameFilter("li"), new HasAttributeFilter("class", "PersonOther_proName"));
			cld = parser.extractAllNodesThatMatch(helper1);
			if(cld != null && cld.size() > 0)
			{
				TagNode parnode = (TagNode) cld.elementAt(0);
				assert (poi != null);
				String pos = parnode.toPlainTextString().replace("&amp;", "").replace("职称：", "").trim();
				
				if (!pos.isEmpty())
					poi += "<POSITION>" + pos + "</POSITION>";
			}
			
			parser.reset();
			
			//<dl class="PersonBox Persontechang">
	        // <dt><span>特长</span></dt>
	        // <dd>临床工作着重于各种心血管病危险因子（高血压、脂质代谢异常、糖尿病等）的防治、心力衰竭诊治、血栓栓塞性疾病的抗凝和抗血小板治疗、动脉粥样硬化心脑血管病的一级预防和二级预防、以及全身性疾病累及心脏的诊治等。主要研究领域包括：脂质代谢异常、动脉粥样硬化发病机制及预防和心血管病临床药理。</dd>
	    	// /dl>
	    
			
			helper1 = new AndFilter(new TagNameFilter("dl"), new HasAttributeFilter("class", "PersonBox Persontechang"));
			cld = parser.extractAllNodesThatMatch(helper1);
			if(cld != null && cld.size() > 0)
			{
				TagNode parnode = (TagNode) cld.elementAt(0);
				
				String skill = parnode.toPlainTextString().replace(" ", "").replace("特长", "").replace("\r", "").replace("\t", "").replace("\n", "").replace("&nbsp;", "").replace("&amp;", "").trim();
				assert (poi != null);
				if (!skill.isEmpty())
					poi += "<SKILL>" + skill + "</SKILL>";	
			}
			
			parser.reset();
			
			/* <dl class="PersonBox PersonIntro">
        	<dt><span>详细介绍</span></dt>
        	<dd><p>&nbsp;&nbsp;&nbsp;&nbsp; 1985年7月毕业于上海医科大学医学系。1990年6月在中国协和医科大学获医学博士学位。2002年6月晋升为北京协和医院心内科正主任医师、教授。1995年9月至1998年4月在瑞士苏黎世大学医院心内科进行博士后研究，主要从事心血管临床药理和血管内皮功能方面的研究。在国内外医学杂志共发表综述和论著120余篇，著书约80万余字,主编/主译专著5部(其中副主编2部)。</p>
				<p>&nbsp;&nbsp;&nbsp; 现任北京协和医院心内科副主任，内科学系副主任，博士研究生导师，中华医学会理事，卫生部心血管病防治研究中心专家委员会委员，中国老年学学会心脑血管病专业委员会常务理事，中华医学会心血管病分会动脉粥样硬化与冠心病学组成员，中国医师学会心血管病分会专家委员会委员，中华医学会科学普及分会第八届委员会委员，中国医师学会高血压病分会专家委员会委员，备战2012年伦敦奥运会国家队医疗专家。</p>
				<p>&nbsp;&nbsp;&nbsp; 目前是《中华心血管病杂志》、《中国循环杂志》、《中国介入心血管病杂志》、《中华高血压杂志》、《中华老年心脑血管病杂志》、《基础医学与临床》、《临床心血管病杂志》、《国际心血管病杂志－原国外医学心血管病分册》、《山东医药》、《国际循环》、《JACC中文版》、《Eur Heart J中文版》、《BMJ中文版》和《中国心血管杂志》编委,《中国医刊》特邀编委。Member of International Society of Hypertension, Member of European Society of Heart Failure。</p>
				<p>&nbsp;&nbsp;&nbsp; 临床工作着重于各种心血管病危险因子（高血压、脂质代谢异常、糖尿病等）的防治、心力衰竭诊治、血栓栓塞性疾病的抗凝和抗血小板治疗、动脉粥样硬化心脑血管病的一级预防和二级预防、以及全身性疾病累及心脏的诊治等。</p>
				<p>&nbsp;&nbsp;&nbsp; 主要研究领域包括：脂质代谢异常、动脉粥样硬化发病机制及预防和心血管病临床药理。</p>
			</dd>
    		</dl>*/

			helper1 = new AndFilter(new TagNameFilter("dl"), new HasAttributeFilter("class", "PersonBox PersonIntro"));
			cld = parser.extractAllNodesThatMatch(helper1);
			if(cld != null && cld.size() > 0)
			{
				TagNode parnode = (TagNode) cld.elementAt(0);
				String intr = parnode.toPlainTextString().replace(" ", "").replace("详细介绍", "").replace("\r", "").replace("\t", "").replace("\n", "").replace("&nbsp;", "").replace("&amp;", "").trim();
				if (!intr.isEmpty())
					poi += "<INTRODUCTION>" + intr + "</INTRODUCTION>";
			}
			
			parser.reset();
			
			if (poi != null)
			{
				poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + "<PARTMENT>" + partment + "</PARMENT>" + poi + "<URL>" + url + "</URL></DOCTOR>";
				
				FileTool.Dump(poi, "D:\\temp\\医院\\BJPumch.csv", "UTF-8");				
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
	public static void parsePartmentSheet(String url, int index,  int hospital, String partment)
	{
		/* <a href="http://www.pumch.cn/Item/1152.aspx" title="
		{Title}""
		" target="
		_blank
		">
		[详细]
		</a>*/
		
		String content = HTMLTool.fetchURL(url.replace("Index.aspx", "Index_" + index + ".aspx"), "UTF-8", "get");

		Parser parser = new Parser();
		if (content == null)
		{
			return;
		}
		
		try {
			
			parser.setInputHTML(content);
			parser.setEncoding("UTF-8");
			
			NodeList cld = parser.extractAllNodesThatMatch(new StringFilter("[详细]"));
			if(cld != null)
			{
				for(int k = 0; k < cld.size(); k++)
				{					
					TagNode parnode = (TagNode) cld.elementAt(k).getParent();
					
					parseDoctor(parnode.getAttribute("href"), hospital, partment);
				}
			}
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 
	}
	
	public static void parseFirstPartmentSheet(String url, int hospital, String partment)
	{
		// <a href="Index_2.aspx">尾页</a>
		String content = HTMLTool.fetchURL(url, "UTF-8", "get");

		Parser parser = new Parser();
		if (content == null)
		{
			return;
		}
		
		try {
			
			parser.setInputHTML(content);
			parser.setEncoding("UTF-8");
			
			// NodeFilter helper1 = new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href"));
			// NodeFilter filter = new AndFilter(new StringFilter("尾页"), helper1);
			NodeList cld = parser.extractAllNodesThatMatch(new StringFilter("尾页"));
			if(cld != null)
			{
				assert (cld.size() == 1);
				TagNode tnd = (TagNode )cld.elementAt(0).getParent();
				
				String inx = tnd.getAttribute("href");
				if (inx.equals("Index.aspx"))
				{
					parsePartmentSheet(url, 1,  hospital, partment);
				}
				else
				{	
					inx = inx.substring("Index_".length());
					inx = inx.substring(0, inx.indexOf(".aspx"));
					
					if (inx.isEmpty())
					{
						
					}
					else{
						int pages = Integer.parseInt(inx);
						
						for (int n = 1; n <= pages; n ++)
						{
							parsePartmentSheet(url, n,  hospital, partment);
						}	
					}
				}								
			}
			else
				parsePartmentSheet(url, 1,  hospital, partment);

		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 
	}
	
	public static void main(String[] args) throws Exception {

		for (int n = 0; n < PARTMENTS.length; n += 2)
		{			
			parseFirstPartmentSheet("http://www.pumch.cn" + PARTMENTS[n], 0, PARTMENTS[n + 1]);
		}
	}
}
