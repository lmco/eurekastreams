/**
 * Get Elements by Class Name.
 */
var __eurekaConnect__getElementsByClassName=function(j,k,l){getElementsByClassName=document.getElementsByClassName?function(d,a,b){d=(b||document).getElementsByClassName(d);a=a?RegExp("\\b"+a+"\\b","i"):null;b=[];for(var e,c=0,f=d.length;c<f;c+=1)e=d[c],(!a||a.test(e.nodeName))&&b.push(e);return b}:document.evaluate?function(d,a,b){a=a||"*";b=b||document;var e=d.split(" "),c="",f=document.documentElement.namespaceURI==="http://www.w3.org/1999/xhtml"?"http://www.w3.org/1999/xhtml":null;d=[];for(var h,g=0,i=e.length;g< i;g+=1)c+="[contains(concat(' ', @class, ' '), ' "+e[g]+" ')]";try{h=document.evaluate(".//"+a+c,b,f,0,null)}catch(j){h=document.evaluate(".//"+a+c,b,null,0,null)}for(;a=h.iterateNext();)d.push(a);return d}:function(d,a,b){a=a||"*";b=b||document;var e=d.split(" ");d=[];a=a==="*"&&b.all?b.all:b.getElementsByTagName(a);b=[];var c;c=0;for(var f=e.length;c<f;c+=1)d.push(RegExp("(^|\\s)"+e[c]+"(\\s|$)"));f=0;for(var h=a.length;f<h;f+=1){e=a[f];c=!1;for(var g=0,i=d.length;g<i;g+=1)if(c=d[g].test(e.className), !c)break;c&&b.push(e)}return b};return getElementsByClassName(j,k,l)};
// END Do Not Touch

/**
 * Base URL for Eureka Instance.
 */
__eurekaConnect__baseUrl = "http://keaner.dev.smp.isgs.lmco.com";

/**
 * Eurkea Connect Activitation Class
 */
__eurekaConnect__activitionClass = "eureka-connect-widget";

if (window.postMessage) {
    window.addEventListener("message", __eurekaConnect__receiveMessage, false);
}
else {
    window.opener = {};
    window.opener.postMessage = function(msg) {
        var packet = {};
        packet.data = msg;
        __eurekaConnect__receiveMessage(packet);
    };
}

function __eurekaConnect__receiveMessage(event) {
    var payload = eval( "(" + event.data + ")" );
    document.getElementById('frameId').height = +payload.frameHeight + 2;
}

function __eurekaConnect__onLoad() {
    var hostUrl = escape(window.location.protocol + "//" + window.location.host);
	var widgets =  __eurekaConnect__getElementsByClassName(__eurekaConnect__activitionClass);

	for (i in widgets)
	{
		var widget = widgets[i];
		var widgetType = widget.getAttribute('eureka:widget');
        widget.innerHTML = "<iframe id='frameId' style='overflow: hidden' frameborder='0' width='" + widget.getAttribute('eureka:width')  + "' src='" + __eurekaConnect__baseUrl  + "/widget.html?p=" + hostUrl + "#widget-" + widgetType + "'></iframe>";

	}
}

__eurekaConnect__onLoad()
