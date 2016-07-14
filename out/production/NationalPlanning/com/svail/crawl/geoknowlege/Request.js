//异步提交
//处理脚本
function _getXmlHttp()
{
/*@cc_on @*/
/*@if (@_jscript_version >= 5)
	var progids=["Msxml2.XMLHTTP", "Microsoft.XMLHTTP"]
	for (i in progids) {
		try { return new ActiveXObject(progids[i]) }
		catch (e) {}
	}
@end @*/
	try { return new XMLHttpRequest();}
	catch (e2) {return null; }
}

function CachedResponse(response) {
	this.readyState = ReadyState.Complete
	this.status = HttpStatus.OK
	this.responseText = response
}

ReadyState = {
	Uninitialized: 0,
	Loading: 1,
	Loaded:2,
	Interactive:3,
	Complete: 4
	}
	
HttpStatus = {
	OK: 200,
	NotFound: 404
	}

function Request_from_cache(url, f_change) {
	var result = this._cache[url];
	
	if (result != null) {
		var response = new CachedResponse(result)
		f_change(response)
		return true
	}
	else
		return false
}

function Request_cached_get(url, f_change) {
	if (!this.FromCache(url, f_change)){
		var request = this
		this.Get(url,
			/* Cache results if request completed */
			function(x){
				if ((x.readyState==ReadyState.Complete)&&(x.status==HttpStatus.OK))
				{request._cache[url]=x.responseText}
				f_change(x)
			},
			"GET")
	}
}

function Request_get(url, f_change, method) 
{
	if (!this._get) return;
	
	if (method == null) method="GET"
	if (this._get.readyState != ReadyState.Uninitialized)
		this._get.abort() 
			
	if(method && method == "POST")
	{	    
        var sArr = url.split('?');
        if(sArr)
        {        
	        this._get.open(method, sArr[0], true);
	    }
	    else
	    {
	        this._get.open(method, url, true);
	    }
	    this._get.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	    if (f_change != null) 
        {
		    var _get = this._get;
		    this._get.onreadystatechange = function(){f_change(_get);}
		}
		if(sArr)
        { 
		    this._get.send(sArr[1]);
		}
		else
	    {
	        this._get.send(url);
	    }
	}       
    
    else
    {
        this._get.open(method, url, true);
        if (f_change != null) 
        {
		    var _get = this._get;
		    this._get.onreadystatechange = function(){f_change(_get);}
		}
		this._get.send(null);
	}
	    
	
}

function Request_get_no_cache(url, f_change, method){
	var sep = (-1 < url.indexOf("?")) ? "&" : "?"	
	var newurl = url + sep + "__=" + encodeURIComponent((new Date()).toString());
	return this.Get(newurl, f_change, method);
}

function Request() {
	this.Get = Request_get
	this.GetNoCache = Request_get_no_cache
	this.CachedGet = Request_cached_get
	this.FromCache = Request_from_cache
	
	this.Use = function(){return this._get!=null}
	this.Cancel = function(){if (this._get) this._get.abort();}
	this._cache = new Object();
	
	this._get = _getXmlHttp();
	if (this._get == null) return;
}

var waitDiv;
var waitElement;
var scrollX, scrollY = -1;
function MoveWaitElement(position) {
	var scrollYT, scrollXT;
	if (!waitElement)
		CreateWaitElement();
	if (window.pageYOffset && typeof(window.pageYOffset) == "number") { 
		scrollYT = window.pageYOffset; 
		scrollXT = window.pageXOffset; 
	} 
	else if (document.body && document.documentElement && document.documentElement.scrollTop) { 
		scrollYT = document.documentElement.scrollTop; 
		scrollXT = document.body.scrollLeft;
	}
	else if (document.body && typeof(document.body.scrollTop) == "number") { 
		scrollYT = document.body.scrollTop; 
		scrollXT = document.body.scrollLeft; 
	} 
	if (scrollX != scrollXT || scrollY != scrollYT) {
		scrollX = scrollXT;
		scrollY = scrollYT;
		if(position=='center')
		{
		var width = document.body.clientWidth;
		 waitElement.style.top=280+document.documentElement.scrollTop+"px";
		//waitElement.style.top = (document.body.offsetHeight+160)/2 + "px";
		waitElement.style.left = (document.body.offsetWidth-200)/2 +  "px";
		}
		else
		{
		
		//var width = document.body.clientWidth;
		  waitElement.style.top = scrollYT + "px";
		  waitElement.style.right = -scrollXT +  "px";
		}
	}
}

 function CreateWaitElement() {
    var elem = document.getElementById('Ajax__Waiting');
    if (!elem) {
			
        elem = document.createElement("div");
        elem.id = 'Ajax__Waiting';

        elem.style.position = 'absolute';
        elem.style.height = 16;
        elem.style.width=150;
        elem.style.paddingLeft = "3px";
        elem.style.paddingRight = "3px";
        elem.style.fontSize = "10px";
        elem.style.fontFamily = 'Arial, Verdana, Tahoma';
        elem.style.border = "#000000 1px solid";
        elem.style.backgroundColor = "#000000";
        elem.style.color = "#ffffff";
        
        elem.innerHTML = '<img src=../images/' + LanguageEncode + '/loading.gif  width=16 height=16 border=0 align=absMiddle> '+message['searchingLoad'];
        elem.style.visibility = 'hidden';
        document.body.insertBefore(elem, document.body.firstChild);
    }
   
    waitElement = elem;
  }
  
function MovewaitDiv(position) {
	var scrollYT, scrollXT;
	if (!waitDiv)
		CreatewaitDiv();
	if(document.all)
	{
	    if (window.pageYOffset && typeof(window.pageYOffset) == "number") { 
		    scrollYT = window.pageYOffset; 
		    scrollXT = window.pageXOffset; 
	    } 
	    else if (document.body && document.documentElement && document.documentElement.scrollTop) { 
		    scrollYT = document.documentElement.scrollTop; 
		    scrollXT = document.body.scrollLeft;
	    }
	    else if (document.body && typeof(document.body.scrollTop) == "number") { 
		    scrollYT = document.body.scrollTop; 
		    scrollXT = document.body.scrollLeft; 
	    } 
	}
	if (scrollX != scrollXT || scrollY != scrollYT) {
		scrollX = scrollXT;
		scrollY = scrollYT;
		if(position=='center')
		{
		var width = document.body.clientWidth;
		 waitDiv.style.top=280+document.documentElement.scrollTop+"px";
		//waitDiv.style.top = (document.body.offsetHeight+160)/2 + "px";
		waitDiv.style.left = (document.body.offsetWidth-200)/2 +  "px";
		}
		else
		{
		
		//var width = document.body.clientWidth;
		  waitDiv.style.top = scrollYT + "px";
		  waitDiv.style.right = -scrollXT +  "px";
		}
	}
}

 function CreatewaitDiv(s) {
    var elem = document.getElementById('Search__Waiting');
    if (!elem) {
			
        elem = document.createElement("div");
        elem.id = 'Search__Waiting';
        elem.style.position = 'absolute';
				elem.style.height = 20;
				elem.style.width=180;
				elem.style.paddingLeft = '3px';
				elem.style.paddingRight = '3px';
				elem.style.fontSize = '12px';
				elem.style.fontFamily = 'Arial, Verdana, Tahoma';
				//elem.style.border = '#fff 1px solid';
				elem.style.backgroundColor = '';
				elem.style.color = '#ffffff';
				elem.style.zIndex = 300;
        elem.innerHTML = '<img src=../images/' + LanguageEncode + '/loading.gif  width=30 height=30 border=0 align=absMiddle><b> ' + s + '</b>';
        elem.style.display = 'none';
        document.body.insertBefore(elem, document.body.firstChild);
    }
   
    waitDiv = elem;
  }

  function HideWaitDiv() {

      if (waitDiv) {
          if (waitDiv.style.display == '') {
              waitDiv.style.display = 'none';
          }
      }
      if (typeof ispost != "undefined") {
          if (ispost)
              ispost = false;
      }
  }

  function ShowWaitDiv() {
      if (waitDiv) {
          waitDiv.style.display = '';
          MovewaitDiv('center');
      }
      setTimeout("HideWaitDiv()", 15000);
  }

 //***********************************************
 //通用提交处理
 //参数:action		操作类型 search检 //		destpage	目标页面名称
 //     param		提交参数
 //		func		结果处理函数
 //		waiting		显示的等待文
 //************************************************
 function CommonDeal(action,destpage,param,func,waiting,method)
 {
    CreatewaitDiv(waiting);
    if (waitDiv)
    {
        if(waiting != null && waiting != "")
        {
            var objWaitTip = document.getElementById("divsearchtip");
            if(objWaitTip)
            {
                objWaitTip.innerHTML = waiting;
            }
        }
        //waitDiv.style.display = '';
        //MovewaitDiv('center');
        ShowWaitDiv();
    }	
	
    var request = new Request();

	var url = destpage + "?action=" + action + param;
    request.GetNoCache(url,func,method)
}

//add by liupei 
//提交的时候不出现等待的div 用于跨库保存tab状态
 function CommonDealNoDiv1(action,destpage,param,func,waiting,method)
 {
    //debugger		
	
    var request = new Request();

	  var url = destpage + "?action=" + action + param;
	  alert("GOOD")
	  $("#txtValue").val("me");
    request.GetNoCache(url,func,method)
}

