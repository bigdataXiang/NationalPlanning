package com.svail.geotext;


public class GeoLocation {
	// {"status":"OK","query_string":"门头沟区永定镇","nlp_status":"地址完全匹配","location":{"matched":"永定镇","lng":116.0925780454,"lat":39.8862755751,"resolution":"snEmpty","region":"北京市门头沟区"}
	// "region":"福建省福州市晋安区","matched":"菊园","lng":119.32912,"resolution":"准确定位","lat":26.067804
    public String  status;
    public String query_string;
    public String nlp_status;
    
	public class GeoCodeInfo
	{
		public String matched;
		public double lng;
		public double lat;
		public String resolution;
		public String region;
	};
	
	public GeoCodeInfo location;
	
	
    public GeoLocation() {
        // TODO Auto-generated constructor stub
    }    
}
