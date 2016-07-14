package com.svail.crawl.cqplan;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChongqingPlanCrawler {
	public static String PROCLAMATION_URL = "http://www.cqupb.gov.cn/wsfw/wsfw_1S3Z_listDetails.aspx?classname=%E5%BB%BA%E8%AE%BE%E5%B7%A5%E7%A8%8B%E9%80%89%E5%9D%80%E6%84%8F%E8%A7%81%E4%B9%A6";
	
	public static void main(String[] args) {
		//-----------------------------打开火狐浏览器------------------------------------------------
		WebDriver driver = new FirefoxDriver();// 打开火狐浏览器  原生支持的浏览器，但是不支持火狐高级的版本
		//-----------------------------打开Chrome浏览器---------------------------------------------
		// File file_chrome = new File("D:/开发/Java/Crawler/lib/chromedriverchromedriver.exe");
		// System.setProperty("webdriver.ie.driver", file_chrome.getAbsolutePath());
		// WebDriver driver = new ChromeDriver();// 打开chrome浏览器
		//-----------------------------打开IE浏览器--------------------------------------------------
		// File file_ie = new File("C:\\Program Files\\Internet Explorer\\IEDriverServer.exe");
		// System.setProperty("webdriver.ie.driver", file_ie.getAbsolutePath());
    	
		WebDriverWait ww = new WebDriverWait(driver, 10);
    	
        driver.get("http://www.baidu.com/");  
        // 等价于    driver.navigate().to("http://www.baidu.com/");


        System.out.println("Page title is: " + driver.getTitle());
        
        
        try{Thread.sleep(10000);}catch(Exception e){}
        
        //Close the browser
        driver.quit();
	}

}
