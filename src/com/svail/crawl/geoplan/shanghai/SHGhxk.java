package com.svail.crawl.geoplan.shanghai;

public class SHGhxk {
	private String flag;
	private SHData  data;
	private String msg;
	
	public SHGhxk()
	{
		super();
	}
	
	public SHGhxk(String flag, SHData  data, String msg)
	{
		super();			
		this.flag = flag;
		
		this.data = data;
		this.msg = msg;
	}
	
	public String toString() {
		
		String ct = "{flag:" + flag;
		
		ct += ",data:" + data.toString();
		
		ct += ",msg:\"" + msg;
		
		ct += "\"}";
		
		return ct;
	}
	
	public void setFlag( String flag ) {
		this.flag = flag;
	}

	public String getFlag() {
		return this.flag;
	}
	
	public void setData( SHData data ) {
		this.data = data;
	}

	public SHData getData() {
		return this.data;
	}
	public void setMsg( String msg ) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
