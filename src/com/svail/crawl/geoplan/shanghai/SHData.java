package com.svail.crawl.geoplan.shanghai;

import java.util.List;

public class SHData {
	
	private List<SHRecord> list;
	private int total;
	    
	public SHData()
	{
		super();
	}
	public SHData(int total, List<SHRecord> rds)
	{
		super();			
		this.total = total;
		
		this.list = rds;
	}
	
	public String toString() {
		String ct = "{list:[" + this.list.get(0).toString();
		
		for (int n = 1; n < this.list.size(); n ++)
		{
			ct += "," + this.list.get(n).toString();
		}
		ct += "],total:" + this.total + "}";
		
		return ct;
	}
	
	public void setTotal( int total ) {
		this.total = total;
	}

	public int getTotal() {
		return this.total;
	}
	
	public void setList( List<SHRecord> rds ) {
		this.list = rds;
	}

	public List<SHRecord> getList() {
		return this.list;
	}
}
