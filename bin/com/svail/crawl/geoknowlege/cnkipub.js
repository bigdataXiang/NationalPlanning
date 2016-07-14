var RefreshFrame=function(issueValue, curObj, baseid, year,pubType) {
    var iframeObj = document.getElementById("issue");
    if (iframeObj) {
        var frameSrc = iframeObj.src;
        frameSrc = "n_CNKIPubInItem.aspx" + frameSrc.substring(frameSrc.indexOf("?"));
        var tmpStr = "";
        removeObjAClass("issueHTML", "a");
        if (curObj) {
            tmpStr = curObj.innerHTML;
            curObj.className = "active";
        }
        if (pubType) {
            if (pubType == "2") {
                if (tmpStr == "全部") {
                    frameSrc = SetQueryStringValue(frameSrc, "showClass", "all", false);
                    issueValue = "";
                    year = "";
                }
                else if (tmpStr == "尚未编期") {
                    frameSrc = SetQueryStringValue(frameSrc, "showClass", "noissue", false);
                    issueValue = "";
                    year = "";
                }
            }
        }
        else
            pubType = "";
        frameSrc = SetQueryStringValue(frameSrc, "pubtype", pubType, false);
        frameSrc = SetQueryStringValue(frameSrc, "issue", issueValue, false);         
        frameSrc = SetQueryStringValue(frameSrc, "value", year, false); 
        frameSrc = SetQueryStringValue(frameSrc, "year", year, false); 
        iframeObj.src = frameSrc;
        alert(frameSrc);
        if (!year) {
            var yearSelect = document.getElementById("YearSelect");
            if (yearSelect)
                year = yearSelect.value;
        }
        if (baseid && year && issueValue)
            RefeshPic(baseid, year, issueValue);
    }
}