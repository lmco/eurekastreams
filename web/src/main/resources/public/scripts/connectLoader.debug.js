// START Do Not Touch
(function(){

    var DomReady = window.DomReady = {};

	// Everything that has to do with properly supporting our document ready event. Brought over from the most awesome jQuery. 

    var userAgent = navigator.userAgent.toLowerCase();

    // Figure out what browser is being used
    var browser = {
    	version: (userAgent.match( /.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/ ) || [])[1],
    	safari: /webkit/.test(userAgent),
    	opera: /opera/.test(userAgent),
    	msie: (/msie/.test(userAgent)) && (!/opera/.test( userAgent )),
    	mozilla: (/mozilla/.test(userAgent)) && (!/(compatible|webkit)/.test(userAgent))
    };    

	var readyBound = false;	
	var isReady = false;
	var readyList = [];

	// Handle when the DOM is ready
	function domReady() {
		// Make sure that the DOM is not already loaded
		if(!isReady) {
			// Remember that the DOM is ready
			isReady = true;
        
	        if(readyList) {
	            for(var fn = 0; fn < readyList.length; fn++) {
	                readyList[fn].call(window, []);
	            }
            
	            readyList = [];
	        }
		}
	};

	// From Simon Willison. A safe way to fire onload w/o screwing up everyone else.
	function addLoadEvent(func) {
	  var oldonload = window.onload;
	  if (typeof window.onload != 'function') {
	    window.onload = func;
	  } else {
	    window.onload = function() {
	      if (oldonload) {
	        oldonload();
	      }
	      func();
	    }
	  }
	};

	// does the heavy work of working through the browsers idiosyncracies (let's call them that) to hook onload.
	function bindReady() {
		if(readyBound) {
		    return;
	    }
	
		readyBound = true;

		// Mozilla, Opera (see further below for it) and webkit nightlies currently support this event
		if (document.addEventListener && !browser.opera) {
			// Use the handy event callback
			document.addEventListener("DOMContentLoaded", domReady, false);
		}

		// If IE is used and is not in a frame
		// Continually check to see if the document is ready
		if (browser.msie && window == top) (function(){
			if (isReady) return;
			try {
				// If IE is used, use the trick by Diego Perini
				// http://javascript.nwbox.com/IEContentLoaded/
				document.documentElement.doScroll("left");
			} catch(error) {
				setTimeout(arguments.callee, 0);
				return;
			}
			// and execute any waiting functions
		    domReady();
		})();

		if(browser.opera) {
			document.addEventListener( "DOMContentLoaded", function () {
				if (isReady) return;
				for (var i = 0; i < document.styleSheets.length; i++)
					if (document.styleSheets[i].disabled) {
						setTimeout( arguments.callee, 0 );
						return;
					}
				// and execute any waiting functions
	            domReady();
			}, false);
		}

		if(browser.safari) {
		    var numStyles;
			(function(){
				if (isReady) return;
				if (document.readyState != "loaded" && document.readyState != "complete") {
					setTimeout( arguments.callee, 0 );
					return;
				}
				if (numStyles === undefined) {
	                var links = document.getElementsByTagName("link");
	                for (var i=0; i < links.length; i++) {
	                	if(links[i].getAttribute('rel') == 'stylesheet') {
	                	    numStyles++;
	                	}
	                }
	                var styles = document.getElementsByTagName("style");
	                numStyles += styles.length;
				}
				if (document.styleSheets.length != numStyles) {
					setTimeout( arguments.callee, 0 );
					return;
				}
			
				// and execute any waiting functions
				domReady();
			})();
		}

		// A fallback to window.onload, that will always work
	    addLoadEvent(domReady);
	};

	// This is the public function that people can use to hook up ready.
	DomReady.ready = function(fn, args) {
		// Attach the listeners
		bindReady();
    
		// If the DOM is already ready
		if (isReady) {
			// Execute the function immediately
			fn.call(window, []);
	    } else {
			// Add the function to the wait list
	        readyList.push( function() { return fn.call(window, []); } );
	    }
	};
    
	bindReady();
	
})();
// END Do Not Touch

function __eurekaConnect__onLoad() {
    var __eurekaConnect__baseUrl = function() {
        var myName = /(^|[\/\\])connectLoader.*\.js(\?|$)/;
        var scripts = document.getElementsByTagName("script");
        for (var i = 0; i < scripts.length; i++) {
            var src;
            if (src = scripts[i].getAttribute("src")) {
                if (src.match(myName)) {
                    return src.substring(0, src.indexOf('/',7));
                }
            }
        }
    }();

    var widgets = function(oElm, strTagName, strAttributeName){
        var arrElements = (strTagName == "*" && oElm.all)? oElm.all : oElm.getElementsByTagName(strTagName);
        var arrReturnElements = new Array();
        var oCurrent;
        var oAttribute;
        for(var i=0; i<arrElements.length; i++) {
            oCurrent = arrElements[i];
            oAttribute = oCurrent.getAttribute && oCurrent.getAttribute(strAttributeName);
            if(typeof oAttribute == "string" && oAttribute.length > 0){
                arrReturnElements.push(oCurrent);
            }
        }
        return arrReturnElements;
    }(document,"*","eureka:id");

    var __eurekaConnect__receiveMessage = function(event) {
        var payload = eval( "(" + event.data + ")" );
        var frame = document.getElementById(payload.frameId);
        if (frame != null) {
            frame.height = +payload.frameHeight + 2;
        }
        else {
            console.log("Frame is null: " + payload.frameId);
        }
    }

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

    var hostUrl = escape(window.location.protocol + "//" + window.location.host);

	for (i in widgets)
	{
		var widget = widgets[i];
		var widgetType = widget.getAttribute('eureka:widget');
        var frameId =  "__eurekaConnect__Frame-" + widget.getAttribute('eureka:id');
        widget.innerHTML = "<iframe id='" + frameId + "' style='overflow: hidden' frameborder='0' width='" + widget.getAttribute('eureka:width')  + "' src='" + __eurekaConnect__baseUrl  + "/widget.html?i=" + escape(frameId) + "&p=" + hostUrl + "#widget-" + widgetType + "'></iframe>";

	}
}

DomReady.ready(__eurekaConnect__onLoad);
