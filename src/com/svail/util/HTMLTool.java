package com.svail.util;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

public class HTMLTool {
	
	public static String autoDetectCharset(String url) { 
		URL source = null; 
		try { 
			source = new URL(url); 
		} catch (MalformedURLException e) { 
			e.printStackTrace();	
		} 
		
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance(); 
		detector.add(new ParsingDetector(false)); 
		detector.add(JChardetFacade.getInstance()); 
		detector.add(ASCIIDetector.getInstance()); 
		detector.add(UnicodeDetector.getInstance());
		
		Charset charset = null; 
		try { 
			charset = detector.detectCodepage(source); 
		} catch (IOException e) { 
			e.printStackTrace();	
		} 

		if (charset == null) { 
		    charset = Charset.defaultCharset(); 
		} 
		return charset.name(); 
	} 
	private static String fetchUrlHelper(String url, String charset,
			String method) throws IOException {

		/* StringBuffer的缓冲区大小 */
		int TRANSFER_SIZE = 4096;

		/* 当前平台的行分隔符 */
		String lineSep = System.getProperty("line.separator");

		URL source = null;

		source = new URL(url);

		HttpURLConnection connection;
		connection = (HttpURLConnection) source.openConnection();
		connection.setRequestProperty("User-Agent",
				"Mozilla/4.7 (compatible; MSIE 5.0; Windows NT; DigExt)");
		// connection.setRequestProperty("User-Agent", "Mozilla/4.7");

		connection.setConnectTimeout(200000);
		connection.setReadTimeout(300000);

		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		connection.setRequestProperty("Accept-Encoding", "gzip");

		/* get 方式 */
		if (method.compareToIgnoreCase("get") == 0) {
			connection.setDoOutput(false); // 设置不执行输出
			connection.setRequestMethod("GET");
		} else {
			connection.setDoOutput(true); // 设置不执行输出
			connection.setRequestMethod("POST");
		}
		connection.setDoInput(true); // 设置执行输入

		connection.setUseCaches(false); // 设置不使用缓存
		connection.connect(); // 打开到此 URL 引用的资源的通信链接
		// BufferedInputStream bis = new
		// BufferedInputStream(connection.getInputStream()); // 获取输入流

		BufferedReader reader = null;
		StringBuffer temp = new StringBuffer(TRANSFER_SIZE);
		InputStream is = connection.getInputStream();
		if (is != null) {
			byte[] header = new byte[2];
			BufferedInputStream bis = new BufferedInputStream(is);
			bis.mark(2);
			int result = bis.read(header);

			// reset输入流到开始位置
			bis.reset();

			// 判断是否是GZIP格式
			int ss = (header[0] & 0xff) | ((header[1] & 0xff) << 8);
			if (result != -1 && ss == GZIPInputStream.GZIP_MAGIC) {
				// System.out.println("为数据压缩格式...");
				reader = new BufferedReader(new InputStreamReader(
						new GZIPInputStream(bis), charset));
			} else {
				// 取前两个字节
				reader = new BufferedReader(new InputStreamReader(bis, charset));
			}
			//
			String line = new String();

			while ((line = reader.readLine()) != null) {
				temp.append(line);
				temp.append(lineSep);
			}
		}
		if (reader != null)
			reader.close();

		connection.disconnect();

		return temp.toString();
	}

	public static String fetchURL(String url, String charset, String method) { 
		int cnt = 0; 
		
		for (; cnt < 20; cnt ++)
		{
			try {
				String rs = fetchUrlHelper(url, charset, method);
				
				return rs;
			}
			catch (UnsupportedEncodingException e) { 
				System.out.println("网页访问错误:" + url);
			} 
			catch ( IOException ie)
			{		
				System.out.println("网页访问错误:" + ie.getMessage());
			}
			
			try {
				Thread.sleep(1000 * ((int) (Math
					.max(1, Math.random() * 3))));
			} catch (final InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		System.out.println("网页访问错误:" + url);
		return null;
	}
	
	
}
