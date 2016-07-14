package com.svail.geotext;

import java.util.List;

public class Location
{
	private String matched;
	private double lng;
	private double lat;
	private String resolution;
	private Region region;
	private List<Double> box;
	private String geocode;
	
	public Location()
	{
		super();
	}
	public Location(String matched, double lng, double lat, String resolution, Region region, List<Double> box, String geocode )
	{
		super();			
		this.matched = matched;
		this.lng = lng;
		this.lat = lat;
		this.resolution = resolution;
		this.region = region;
		
		this.box = box;
		this.geocode = geocode;
	}
	
	public String toString() {
		String ct = "{matched:" + this.matched + ", lng:" + this.lng
		+ ", lat:" + this.lat + ", resolution:" + this.resolution + 
		 ", region:" + this.region;
		
		if (box != null && box.size() > 0)
		{
			ct += ",box:[" + box.get(0); 
			
			for (int n = 1; n < box.size(); n ++)
			{
				ct += "," + box.get(n);
			}
			ct += "]"; 
		}
		
		ct += ",geocode:" + geocode + "}";
		
		return ct;
	}
	
	public void setMatched( String matched ) {
		this.matched = matched;
	}

	public String getMatched() {
		return this.matched;
	}
	
	public void setResolution( String resolution ) {
		this.resolution = resolution;
	}

	public String getResolution() {
		return this.resolution;
	}
	
	public void setLng( double lng ) {
		this.lng = lng;
	}

	public double getLng() {
		return this.lng;
	}
	
	public void setLat( double lat ) {
		this.lat = lat;
	}

	public double getLat() {
		return this.lat;
	}
	
	public void setRegion( Region region ) {
		this.region = region;
	}

	public Region getRegion() {
		return this.region;
	}
	
	public void setBox( List<Double> box) {
		this.box = box;
	}

	public List<Double> getBox() {
		return this.box;
	}
	public void setGeocode(String geocode ) {
		this.geocode = geocode;
	}

	public String getGeocode() {
		return this.geocode;
	}
};
