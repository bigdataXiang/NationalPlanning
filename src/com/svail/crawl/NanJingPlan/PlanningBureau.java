package com.svail.crawl.NanJingPlan;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.svail.util.FileTool;
import com.svail.util.HTMLTool;
import com.svail.util.Tool;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by ZhouXiang on 2016/7/14.
 */
public class PlanningBureau {
    public static void main(String[] args) {
        parserContent("http://www.njghj.gov.cn/NGWeb/Project/ProjectSearch.aspx?ProjectType=%u9879%u76ee%u9009%u5740%u610f%u89c1%u4e66");
    }
    public static void parserContent(String url){
        String content="";
        try
        {
            //content = Tool.fetchURL(url);
            content = HTMLTool.fetchURL(url,"utf-8","get");
            System.out.println(content);

        }catch (FailingHttpStatusCodeException e1)
        {}


    }
}
