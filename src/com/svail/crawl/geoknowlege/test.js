var ret=function GetVollueIssue1(year, baseid, curObj) {
    var oldNaivRequestDest = "n_request.aspx";
    if (year && baseid && year.length > 0 && baseid.length > 0){
    		var dest = "n_request.aspx";
        var qParam = "";
        var fieldValue = '';
        var valueStr = '';
        qParam = "&" + window.location.search.substring(1);
        re = /&baseid=[^&]*/ig;
        if (qParam.match(re))
            qParam = qParam.replace(re, "&baseid=" + baseid);
        else
            qParam += "&baseid=" + baseid;
        
        if (year && year.length > 0) 
        {
        	var url = qParam;
        	var sKey = "value";
        	var sValue = year;
        	var isEncode = true;
        	{
        		var strPattern = "[\&|\?]+" + sKey + "=[^&]*";
        		strPattern = new RegExp(strPattern, "ig");
        		var strReplacement = "";
        		if (sValue) {
        			if (isEncode) {
        				sValue = encodeURI(sValue);
        			}
        			strReplacement = sKey + "=" + sValue;
        		}
        		
        		var m = url.match(strPattern);
        		if (m && m.length > 0) {
        			if (strReplacement.length > 0) {
        				if (m[0].indexOf("?") == 0) {
        					strReplacement = "?" + strReplacement;
        				}
        				else if (m[0].indexOf("&") == 0) {
        					strReplacement = "&" + strReplacement;
        				}

        			}
        			url = url.replace(strPattern, strReplacement);
        		}
        		else {
        			if (strReplacement.length > 0) {
        				url += "&" + strReplacement;
        			}
        		}
        		qParam = url;
        	};

        	url = qParam;
        	sKey = "year";
        	sValue = year;
        	isEncode = true;

        	var strPattern = "[\&|\?]+" + sKey + "=[^&]*";
        	strPattern = new RegExp(strPattern, "ig");
        	var strReplacement = "";
        	if (sValue) {
        		if (isEncode) {
        			sValue = encodeURI(sValue);
        		}
        		strReplacement = sKey + "=" + sValue;
        	}
        	var m = url.match(strPattern);
        	if (m && m.length > 0) {
        		if (strReplacement.length > 0) {
        			if (m[0].indexOf("?") == 0) {
        				strReplacement = "?" + strReplacement;
        			}
        			else if (m[0].indexOf("&") == 0) {
        				strReplacement = "&" + strReplacement;
        			}

        		}
        		url = url.replace(strPattern, strReplacement);
        	}
        	else {
        		if (strReplacement.length > 0) {
        			url += "&" + strReplacement;
        		}
        	}
        	qParam = url;
        	
        }
       	var prentName = "yearIssueInfo";
        var childTag = "a";
        
        if (prentName && childTag) {
        	var prentObj = document.getElementById(prentName);
        	if (prentObj) {
        		var arr = prentObj.getElementsByTagName(childTag);
        		var nLen = arr.length;
        		if (arr && arr.length > 0) {
        			for (i = 0; i < nLen; i++) {
        				arr[i].className = '';
        			}
        		}
        	}
        }
        
        return dest + "?action=" + 'year' + dParam;
  	}
    return "";
};