package com.svail.crawl.hospital.person;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

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

// 医科院肿瘤医院
// http://www.cicams.ac.cn
public class BJCicams {

	public static String PARTMENTS[] = {
		"Index_208","头颈外科",
		"Index_204","神经外科",
		"Index_525","胸外科",
		"Index_563","腹部肝胆外科",		// http://www.cicams.ac.cn/Html/Departments/MainIndex_564.html
		"Index_564","腹部结直肠一科", 	// http://www.cicams.ac.cn/Html/Departments/Main/Index_564.html
		"Index_562","腹部结直肠二科",
		"Index_565","腹部胰胃外科",
		"Index_505","乳腺外科",
		"Index_189","骨科",
		"Index_266","妇科",
		"Index_523","泌尿外科",
		"Index_203","内科",
		"Index_522","放射治疗科",
		"Index_521","麻醉科",
		"Index_520","重症加强治疗科",
		"Index_526","特需医疗部",
		"Index_212","综合科",
		"Index_211","中医科",
		"Index_566","防癌科",
		"Index_190","核医学科",
		"Index_217","影像诊断科",
		"Index_176","病理科",
		"Index_191","检验科",
		"Index_519","内镜科",
		"Index_514","口腔科",
		"Index_567","PET-CT中心",
		"Index_205","手术室",
		"Index_209","药剂科",
		"Index_207","输血科",
		"Index_175","病案室",
		"Index_210","营养室",
		"Index_188","中心供应室"
	};
	
	//访问获取科室专家列表 /Html/Departments/Main/DoctorTeam
	public static void parseDoctor(String url, int hospital, String partment)
	{
		String content = HTMLTool.fetchURL(url, "UTF-8", "get");

		if (content == null)
		{
			return;
		}
		
		/*<strong>姓名：</strong><span>唐平章</span> 
		 </dt>
		 <dd>
		 <strong>职称：</strong>主任医师</dd>
		 */
		
		int inx = content.indexOf("<strong>姓名");
		if (inx == -1)
			return;
		
		int ine = content.indexOf("</span>", inx);
		
		if (ine == -1)
			return;
		inx = content.indexOf("<span>", inx);
		
		if (inx == -1 || inx > ine)
			return;
		
		String name = content.substring(inx + "<span>".length(), ine);
		String poi = null;
		name = name.replace("\t", "").replace("\r", "").replace("\n", "").trim();
		if (!name.isEmpty())
			poi = "<NAME>" + name + "</NAME>";
		
		if (poi == null)
			return;
			
		inx = content.indexOf("<strong>职称");
		if (inx != -1)
		{
			ine = content.indexOf("</dd>", inx);
		
			if (ine != -1)
			{
				inx = content.indexOf("</strong>", inx);
		
				if (!(inx == -1 || inx > ine))
				{
					String pos = content.substring(inx + "</strong>".length(), ine).replace("&nbsp;", "").replace("&amp;", "").replace("\t", "").replace("\r", "").replace("\n", "");
								
					if (!pos.isEmpty())
						poi += "<POSITION>" + pos + "</POSITION>";
				}
			}
		}
		// id="Descri_all"><strong>专家介绍：</strong><p><span style="color: rgb(255, 255, 255);"></span>
		inx = content.indexOf("id=\"Descri_all");
		if (inx != -1)
		{
			inx = content.indexOf("<strong>专家介绍：</strong>", inx + "id=\"Descri_all".length());
			
			if (inx != -1)
			{
				content = content.substring(inx + "<strong>专家介绍：</strong>".length());
				
				if (content.startsWith("<p>"))
				{
					inx = content.indexOf("</p>", 0);
					if (inx != -1)
					{
						content = content.substring("<p>".length(), inx);
					}
					else
					{
						poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + "<PARTMENT>" + partment + "</PARMENT>" + poi + "</DOCTOR>";
						
						FileTool.Dump(poi, "D:\\temp\\医院\\BJCicams.csv", "UTF-8");
					}
				}
				inx = content.indexOf("<span");
				
				if (inx != -1)
				{
					ine = content.indexOf(";\">", inx);
					if (ine != -1)
					{
						content = content.replace(content.substring(inx, ine + ";\">".length()), "");
					}
				}
	
				content = content.replace("</span>", "").replace("<span style=\"color:#ffffff\">", "").replace("<span style=\"color:#000000\">", "").replace("<br/>", "").replace("&nbsp;", "").replace("&amp;", "").replace("\t", "").replace("\r", "").replace("\n", "");
				
				content = content.trim();
						
				if (!content.isEmpty())
					poi += "<INTRODUCTION>" + content + "</INTRODUCTION>";
			}
		}
		if (poi != null)
		{
			poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + "<PARTMENT>" + partment + "</PARMENT>" + poi + "<URL>" + url + "</URL></DOCTOR>";
			
			FileTool.Dump(poi, "D:\\temp\\医院\\BJCicams.csv", "UTF-8");
			
		}
		
		try {
			Thread.sleep(500 * ((int) (Math
				.max(1, Math.random() * 3))));
		} catch (final InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public static void parsePartmentSheet(String url, int hospital, String partment)
	{
		// http://www.cicams.ac.cn/Html/Departments/Main/ + PARTMENTS[n] + ".html"
		String content = HTMLTool.fetchURL(url, "UTF-8", "get");
		
		if (content == null)
		{
			return;
		}
		
		int inx = content.indexOf("Html/Departments/Main/DoctorTeam");
		if (inx != -1)
		{
			int ine = content.indexOf("\">", inx + "Html/Departments/Main/DoctorTeam".length());
			String turl = content.substring(inx, ine);

			content = HTMLTool.fetchURL("http://www.cicams.ac.cn/" + turl, "UTF-8", "get");
			// 网页内容存在错误，使用HTMLParser解析不出，硬解析
			inx = content.indexOf("/Html/Doctors/Main/Index");
			Set<String> docs = new TreeSet<String>();
			
			while (inx != -1)
			{
				ine = content.indexOf(".html", inx + "/Html/Doctors/Main/Index".length());
				if (ine != -1)
				{
					turl = content.substring(inx, ine);
					docs.add(turl);
					inx = ine + ".html".length();
				}
				else
				{
					inx = inx + "/Html/Doctors/Main/Index".length();
				}

				inx = content.indexOf("/Html/Doctors/Main/Index", inx);
			}
			
			Iterator<String> itr = docs.iterator();
			
			while(itr.hasNext())
			{
				turl = itr.next();
				parseDoctor("http://www.cicams.ac.cn" + turl + ".html", hospital, partment);
				
			}
			/*parser.setInputHTML(content);
				parser.setEncoding("UTF-8");
				System.out.println(content);		

				// NodeFilter filter = new AndFilter(new TagNameFilter("ul"), new HasAttributeFilter("class", "doctorList"));
				NodeFilter filter = new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href"));

				NodeList cld = parser.extractAllNodesThatMatch(filter);
				if (cld == null)
					return;

				for(int n = 0; n < cld.size(); n ++)				
				{				
					String href = ((TagNode)cld.elementAt(n)).getAttribute("href");
					if (href.startsWith("/Html/Doctors/Main/Index_"))
					{
						parseDoctor("http://www.cicams.ac.cn" + href, hospital, partment);
					}
					 NodeList ncld = cld.elementAt(n).getChildren();

					for (int m = 0; m < ncld.size(); m ++)
					{
						if (ncld.elementAt(m) instanceof TagNode)
						{
							TagNode ntnd = (TagNode) ncld.elementAt(m);

							if (ntnd.getTagName().equalsIgnoreCase("li"))
							{
								NodeList tcld = ntnd.getChildren();	

								if (tcld != null)
								{
									for (int k = 0; k < tcld.size(); k ++)
									{
										if (tcld.elementAt(k) instanceof TagNode)
										{
											TagNode ftnd = (TagNode) tcld.elementAt(k);

											if (ftnd.getTagName().equalsIgnoreCase("a"))
											{
												parseDoctor("http://www.cicams.ac.cn" + ftnd.getAttribute("href"), hospital, partment);
												break;												
											}
										}
									}
								}								
							}
						}
					}
				}*/
		}
		else
		{
			inx = content.indexOf("/Html/Departments/Main/DoctorTeam");
			while (inx != -1)
			{
				int ine = content.indexOf(".html", inx + "href=\"/Html/Doctors/Main/Index".length());
				if (ine != -1)
				{
					String turl = content.substring(inx, ine);
					parseDoctor("http://www.cicams.ac.cn" + turl, hospital, partment);
					inx = ine + ".html".length();
				}
				else
				{
					inx = inx + "href=\"/Html/Doctors/Main/Index".length();
				}

				inx = content.indexOf("href=\"/Html/Doctors/Main/Index", inx);
			}
		}
	}
	
		
	public static void main(String[] args) throws Exception {

		for (int n = 0; n < PARTMENTS.length; n += 2)
		{	
			parsePartmentSheet("http://www.cicams.ac.cn/Html/Departments/Main/" + PARTMENTS[n] + ".html", 1, PARTMENTS[n + 1]);
			
		}
	}
	
}
