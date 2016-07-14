package com.svail.crawl.geoplan;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlanTasksExcutor {
	private static long getTimeMillis(String time) {  
	    try {  
	        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");  
	        DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");  
	        Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);  
	        return curDate.getTime();  
	    } catch (ParseException e) {  
	        e.printStackTrace();  
	    }  
	    return 0;  
	} 
	
	public static void main(String[] args) {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();  
        
    	long oneDay = 24 * 60 * 60 * 1000;  
    	long initDelay = getTimeMillis("18:0:0") - System.currentTimeMillis();  
    	initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;  

    	// 北京规划抓取
        Runnable BJPlanTask = new Runnable() {  
            public void run() {  
            	BeijingPlanCrawler.BJPlanTask();
            }  
        };  
        service.scheduleAtFixedRate(BJPlanTask, initDelay, oneDay, TimeUnit.MILLISECONDS); 
	}
}
