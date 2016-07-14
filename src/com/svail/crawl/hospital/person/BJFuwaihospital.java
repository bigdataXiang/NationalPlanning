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
// http://www.fuwaihospital.org
// 阜外心血管病医院
public class BJFuwaihospital {
	
	private static String HOSPITALURL = "http://www.fuwaihospital.org/Hospitals/Doctors/Overview";
	public static String parseFamorseDoctor(String url)
	{
		String content = HTMLTool.fetchURL(url, "UTF-8", "get");

		Parser parser = new Parser();
		if (content == null)
		{
			return null;
		}
		
		try {
			
			parser.setInputHTML(content);
			parser.setEncoding("UTF-8");
			// lass="article_cont"
			NodeFilter helper1 = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "article_cont"));
			
			NodeList cld = parser.extractAllNodesThatMatch(helper1);
			if(cld != null && cld.size() > 0)
			{
				TagNode parnode = (TagNode) cld.elementAt(0);
				String str = parnode.toPlainTextString();
				str = str.replace("&nbsp;", "").replace("&amp;", "").replace("\t", "").replace("\r", "").trim();
				
				return str;
			}			
		} catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		} 
		return null;
		
	}
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
			
			//<a href="#" target="_blank" class="doc_name">沈刘忠</a></li>
			NodeFilter helper1 = new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("class", "doc_name"));
			
			String poi = null;
			NodeList cld = parser.extractAllNodesThatMatch(helper1);
			if(cld != null && cld.size() > 0)
			{
				TagNode parnode = (TagNode) cld.elementAt(0);
				String name = parnode.toPlainTextString();
				name = name.replace("&nbsp;", "").replace("&amp;", "").replace("\t", "").replace("\r", "").replace("\n", "").trim();
				if (!name.isEmpty())
					poi = "<NAME>" + name + "</NAME>";
			}
			
			if (poi == null)
				return;
			
			parser.reset();
			
			// <li>职称：<span>
			// 主任医师                    </span></li>
			// helper1 = new AndFilter(new TagNameFilter("li"), new HasAttributeFilter("class", "PersonOther_proName"));
			cld = parser.extractAllNodesThatMatch(new StringFilter("职称："));
			if(cld != null && cld.size() > 0)
			{
				TagNode parnode = (TagNode) cld.elementAt(0).getParent();
				assert (poi != null);
				String pos = parnode.toPlainTextString().replace("&amp;", "").replace("职称：", "").trim();
				
				if (!pos.isEmpty())
					poi += "<POSITION>" + pos + "</POSITION>";
			}
			
			parser.reset();
			// 
			//<li>科室： <span>
			// 二十六A病区成人外科中心外科管委会移植病房                    </span></li>

			cld = parser.extractAllNodesThatMatch(new StringFilter("科室："));
			if(cld != null && cld.size() > 0)
			{
				TagNode parnode = (TagNode) cld.elementAt(0).getParent();
				assert (poi != null);
				String branch = parnode.toPlainTextString().replace("&amp;", "").replace("科室：", "").trim();
							
				if (!branch.isEmpty())
					poi += "<PARTMENT>" + branch + "," + partment + "</PARTMENT>";
				else
					poi += "<PARTMENT>" + partment + "</PARTMENT>";
			}
						
			parser.reset();
			// <p>
            // <span>专长：</span>瓣膜成形及替换、晚期心脏病、心衰、心脏移植、冠心病以及房颤射频消融</p>

			cld = parser.extractAllNodesThatMatch(new StringFilter("专长："));
			if(cld != null && cld.size() > 0)
			{
				TagNode parnode = (TagNode) cld.elementAt(0).getParent();
				assert (poi != null);
				
				String skill = parnode.toPlainTextString().replace(" ", "").replace("专长：", "").replace("\r", "").replace("\t", "").replace("\n", "").replace("&nbsp;", "").replace("&amp;", "").trim();
				assert (poi != null);
				if (!skill.isEmpty())
					poi += "<SKILL>" + skill + "</SKILL>";	
			}
			
			parser.reset();
			
			/// <a href="/Doctors/Main/Detail/4" class="details">详细介绍</a>

			cld = parser.extractAllNodesThatMatch(new StringFilter("详细介绍"));
			if(cld != null && cld.size() > 0)
			{
				TagNode parnode = (TagNode) cld.elementAt(0).getParent();
				String turl = parnode.getAttribute("href");
				
				if (turl != null)
				{
					content = HTMLTool.fetchURL("http://www.fuwaihospital.org" + turl, "UTF-8", "get");
					ins =  content.indexOf("<!---------------------content--------------------------------->");
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
					parser.reset();
					
					cld = parser.extractAllNodesThatMatch(new StringFilter("个人简介"));
					
					if(cld != null && cld.size() > 0)
					{
						parnode = (TagNode) cld.elementAt(0).getParent().getParent();
						String intr = parnode.toPlainTextString().replace(" ", "").replace("个人简介", "").replace("\r", "").replace("\t", "").replace("&nbsp;", "").replace("&amp;", "").replace("\n", "").trim();
						if (!intr.isEmpty() && intr.length() > 5)
							poi += "<INTRODUCTION>" + intr + "</INTRODUCTION>";
					}
				}
			}
			else
			{
				parser.reset();
				cld = parser.extractAllNodesThatMatch(new StringFilter("个人简介"));
				
				if(cld != null && cld.size() > 0)
				{
					TagNode parnode = (TagNode) cld.elementAt(0).getParent();
					String intr = parnode.toPlainTextString().replace(" ", "").replace("个人简介", "").replace("\r", "").replace("\t", "").replace("&nbsp;", "").replace("\n", "").replace("&amp;", "").trim();
					if (!intr.isEmpty())
						poi += "<INTRODUCTION>" + intr + "</INTRODUCTION>";
				}
			}
			
			
			if (poi != null)
			{
				poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + poi + "<URL>" + url + "</URL></DOCTOR>";
				
				FileTool.Dump(poi, "D:\\temp\\医院\\BJfuwaihospital.csv", "UTF-8");				
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
			
			content = content.replace("<strong>", "<a>").replace("</strong>", "</a>");;
			parser.setInputHTML(content);
			parser.setEncoding("UTF-8");
			// <div class="doc_cont">
			
			NodeFilter parentFilter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "doc_cont"));
			NodeList cld = parser.parse(new HasAttributeFilter("class", "doc_cont"));
			// 读取院士信息
			if(cld != null)
			{
				for(int k = 0; k < cld.size(); k++)
				{					
					TagNode parnode = (TagNode) cld.elementAt(k);
					String poi = "";
					
					
					NodeList child = parnode.getChildren();
					
					for (int m = 0; m < child.size(); m ++)
					{
						/* <div class="doc_cont">
                         <strong>高润霖</strong>
			                        <p>
			                            职称：<span>主任医师</span></p>
			                        <p>
			                            科室：<span>冠心病诊治中心</span></p>
			                        <p>
			                            专长：<span>冠心病临床</span></p>
                        <p>
                            <a href="/News/Articles/Index/57">介绍</a></p>
                    	</div>*/
						if (child.elementAt(m) instanceof TagNode)
						{
							TagNode tnd = (TagNode) child.elementAt(m);
							if (tnd.getTagName().equalsIgnoreCase("a"))
							{
								String name = tnd.toPlainTextString().replace(" ", "").replace("\r", "").replace("\t", "").replace("\n", "").replace("&nbsp;", "").replace("&amp;", "").trim();
								if (!name.isEmpty())
									poi += "<NAME>" + name + "</NAME>";
								
								else
									break;
							}
							else if (tnd.getTagName().equalsIgnoreCase("p"))
							{
								String str = tnd.toPlainTextString().replace(" ", "").replace("\r", "").replace("\t", "").replace("\n", "").replace("&nbsp;", "").replace("&amp;", "").trim();
								if (!str.isEmpty())
								{
									if (str.startsWith("职称："))
									{
										str = str.substring("职称：".length());
										if (!str.isEmpty())
											poi += "<POSITION>" + str +  ", 院士</POSITION>";
									}
									else if (str.startsWith("科室："))
									{
										str = str.substring("科室：".length());
										if (!str.isEmpty())
											poi += "<PARMENT>" + str + "</PARMENT>";
									}else if (str.startsWith("专长："))
									{
										str = str.substring("专长：".length());
										if (!str.isEmpty())
											poi += "<SKILL>" + str + "</SKILL>";
									}
									else if (str.startsWith("介绍"))
									{
										str = tnd.toHtml();
										int inx = str.indexOf("href=\"");
										
										if (inx != -1)
										{
											int ine = str.indexOf("\"", inx + "href=\"".length());
											
											if (ine != -1)
											{
												str = str.substring(inx + "href=\"".length(), ine);
												String turl = "http://www.fuwaihospital.org" + str;
												str = parseFamorseDoctor(turl);
												
												if (str != null)
												{
													poi += "<INTRODUCTION>" + str + "</INTRODUCTION><URL>" + turl + "</URL>" ; 
												}
											}
										}
									}										
								}
							}
						}
					}
					
					if (! poi.isEmpty())
					{
						poi = "<DOCTOR><HOSPITAL>" + hospital + "</HOSPTIAL>" + poi + "</DOCTOR>";
						
						FileTool.Dump(poi, "D:\\temp\\医院\\BJFuwaihospital.csv", "UTF-8");
					}
				}
			}
			parser.reset();
			// 按照 <div class="doctor_listdatal">
			NodeFilter filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "doctor_listdatal"));
			cld = parser.extractAllNodesThatMatch(filter);
			
			if (cld != null )
			{
				for (int n = 0; n < cld.size(); n ++)
				{
					TagNode par = (TagNode) cld.elementAt(n);
					
					NodeList child = par.getChildren();
					
					if (child != null)
					{
						filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "title_head_server"));
						
						NodeList scld = child.extractAllNodesThatMatch(filter);
						String branch = null;
						if (scld != null && scld.size() > 0)
						{
							branch = scld.elementAt(0).toPlainTextString();
							branch = branch.replace(" ", "").replace("\r", "").replace("\t", "").replace("\n", "").replace("&nbsp;", "").replace("&amp;", "").trim();
						}
						if (branch.equalsIgnoreCase("院士殿堂"))
							continue;
						
						filter = new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href"));
						
						scld = child.extractAllNodesThatMatch(filter, true);
						
						if (scld != null && scld.size() > 0)
						{
							for (int m = 0; m < scld.size(); m ++)
							{
								TagNode snode = (TagNode) scld.elementAt(m);
								String durl = snode.getAttribute("href");
								
								if (durl != null && durl.startsWith("/Hospitals/Doctors"))
									parseDoctor("http://www.fuwaihospital.org" + durl, hospital, branch);
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

		parsePartmentSheet(HOSPITALURL, 2);
	}
}
