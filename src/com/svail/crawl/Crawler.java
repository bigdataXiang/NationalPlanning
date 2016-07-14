package com.svail.crawl;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.svail.crawl.fang.Geofang;

public class Crawler {  
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

    	ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();//newScheduledThreadPool(Geofang.regions.length * 2 + 4);  
        
    	long oneDay = 24 * 60 * 60 * 1000;  
    	long initDelay = getTimeMillis("19:28:30") - System.currentTimeMillis();  
    	initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;  

    	// 重庆 求租 抓取
        Runnable cqRentalTask = new Runnable() {  
            public void run() {  
            	Geofang.getRentalInfo();
            }  
        };  
    	// service.scheduleAtFixedRate(cqRentalTask, initDelay, 12 * 60 * 1000, TimeUnit.MILLISECONDS); // 5分钟访问频率
    	
    	// 重庆 出租 抓取
    	for (int n = 0; n < Geofang.regions.length; n ++)
    	{
    		final String region = Geofang.regions[n];
    		Runnable cqRentOutTask = new Runnable() {  
                public void run() {  
                	Geofang.getRentOutInfo(new String(region));
                }
            };  
        	service.scheduleAtFixedRate(cqRentOutTask, initDelay, 12 * 60 * 1000, TimeUnit.MILLISECONDS); // 5分钟访问频率
        	
    	}
    	
    	// 重庆 二手房 抓取
    	for (int n = 0; n < Geofang.regions.length; n ++)
    	{
    		final String region = Geofang.regions[n];
    		Runnable cqResoldApparmentTask = new Runnable() {  
                public void run() {  
                	Geofang.getResoldApartmentInfo(new String(region));
                }
            };  
        	service.scheduleAtFixedRate(cqResoldApparmentTask, initDelay, 12 * 60 * 1000, TimeUnit.MILLISECONDS); // 5分钟访问频率
        	
    	}
    	initDelay = getTimeMillis("23:50:00") - System.currentTimeMillis();  
    	initDelay = initDelay > 0 ? initDelay : oneDay + initDelay; 

    	// 搜房网 新房 按照1天更新频率
    	Runnable NewCQHouseTask = new Runnable() {  
           public void run() {  
            	Calendar cal = Calendar.getInstance();
            	Geofang.getNewBuildingInfo(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
            }  
        };  
    	service.scheduleAtFixedRate(NewCQHouseTask, initDelay, oneDay, TimeUnit.MILLISECONDS); 
    	
    }  
}  