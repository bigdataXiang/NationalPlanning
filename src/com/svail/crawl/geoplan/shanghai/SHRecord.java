package com.svail.crawl.geoplan.shanghai;

public class SHRecord {
	private String fzrq;
	private String  rn;
	private String doctitle;
	private String docpuburl;
	
	public SHRecord()
	{
		super();
	}
	public SHRecord(String fzrq, String  rn, String doctitle, String docpuburl)
	{
		super();			
		this.fzrq = fzrq;
		
		this.rn = rn;
		this.doctitle = doctitle;
		this.docpuburl = docpuburl;
	}
	
	public String toString() {
		String ct = "{fzrq:\"" + fzrq;
		
		ct += "\",rn:\"" + rn;
		
		ct += "\",doctitle:\"" + doctitle;
		
		ct += "\",docpuburl:\"" + docpuburl + "\"}";
		return ct;
	}
	
	public void setRn( String rn ) {
		this.rn = rn;
	}

	public String getRn() {
		return this.rn;
	}
	
	public void setFzrq( String fzrq ) {
		this.fzrq = fzrq;
	}

	public String getFzrq() {
		return this.fzrq;
	}
	
	public void setDoctitle( String doctitle ) {
		this.doctitle = doctitle;
	}

	public String getDoctitle() {
		return this.doctitle;
	}
	public void setDocpuburl( String docpuburl ) {
		this.docpuburl = docpuburl;
	}

	public String getDocpuburl() {
		return this.docpuburl;
	}
}
