if (typeof eurekastreams == "undefined" || !eurekastreams) {var eurekastreams = {};}
if (typeof eurekastreams.delegation == "undefined" || !eurekastreams.delegation) {eurekastreams.delegation = {};}


eurekastreams.delegation = function()
{
	var section;
    return{
    	clearDelegate : function()
    	{
        	var params = gadgets.util.getUrlParameters();
        	var mid = params["mid"];
        	
       		var url = "${build.app.baseurl}/resources/delegation/delegator";
       		var params={};
       		params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.DELETE;
       		params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
       		params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.NONE;
       		params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
       		gadgets.io.makeRequest(url, function(results) { gadgets.rpc.call(null, "refreshGadget", null, mid); }, params);
    	},
    	displayCurrentDelegator : function(ntid)
    	{
    		var currDel = jQuery("<div class='currently-delegating'>Delegating For: " + ntid + "</div>")
    		currDel.css("background","url('/style/images/delegating-for-bg.png')");
    		currDel.css("color","white");
    		currDel.css("font-weight","bold");
    		currDel.css("padding","6px");
    		currDel.css("height","18px");
    		currDel.css("font-size","13px");
    		
    		var clearDelLink = jQuery("<a href='javascript:eurekastreams.delegation.clearDelegate()'>X</a>");
    		clearDelLink.css("background","url('/style/images/delegating-for-x.png')");
    		clearDelLink.css("float", "right");
    		clearDelLink.css("height", "21px");
    		clearDelLink.css("width", "20px");
    		clearDelLink.css("text-indent", "-1000em");
    		clearDelLink.css("overflow", "hidden");
    		
    		currDel.append(clearDelLink);
    		jQuery("body").prepend(currDel);
    		Eureka.resize();
    	},
    	setDelegate : function(ntid)
    	{
            var params = gadgets.util.getUrlParameters();
            var mid = params["mid"];
            
           	var url = "${build.app.baseurl}/resources/delegation/delegator/" + ntid;
        	var params={};
        	params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.PUT;
            params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
            params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.NONE;
            params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
            gadgets.io.makeRequest(url, function(results) { gadgets.rpc.call(null, "refreshGadget", null, mid); }, params);
    	},
        insertDropDown : function(data, container)
        {
                container.css('padding','10px');
                var html = "<strong style='display:block;font-size:12px;padding-bottom:4px;'>Choose a View:</strong><select onchange='eurekastreams.delegation.setDelegate(this.options[this.selectedIndex].value)'>";
                html += "<option value='none'>Select a delegated view...</option>";

                if (data.length > 0) section.show();
                
                for (var i = 0; i<data.length; i++)
                {
                        html += "<option value='" + data[i] + "'>" + data[i] + "</option>";
                }

                html += "</select>";
                container.append(html);
                Eureka.resize();
        },
        setup : function(appKey, ex)
        {
                var params = gadgets.util.getUrlParameters();
                var mid = params["mid"];
                
            	var url = "${build.app.baseurl}/resources/delegation/delegates";
            	var params={};
            	params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.GET;
                params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
                params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.NONE;
                params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
                gadgets.io.makeRequest(url, function(results) { gadgets.rpc.call(null, "setupDelegation", null, mid, appKey, results.data.delegates); }, params);
             
                this.spin();

            	var url = "${build.app.baseurl}/resources/delegation/delegators";
            	var params={};
            	params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.GET;
                params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
                params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.NONE;
                params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
                gadgets.io.makeRequest(url, function(results) { eurekastreams.delegation.insertDropDown(results.data.delegators, container); }, params);
             
                
            	var url = "${build.app.baseurl}/resources/delegation/delegator/current";
            	var params={};
            	params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.GET;
                params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
                params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.NONE;
                params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
                gadgets.io.makeRequest(url, function(results) { if (results.data.currentDelegator != null && results.data.currentDelegator != "null" && results.data.currentDelegator != "" ) 
                	{ eurekastreams.delegation.displayCurrentDelegator(results.data.currentDelegator); }}, params);
             

                var dataFromServer = ["sterleck", "bhmayo"];
                var container = jQuery("<div class='delegation-dropdown'></div>");
                section = ex.addSection("Delegation", container, false);
                section.hide();
                
        },
        makeRequest : function(request)
        {

        	var requestArr = request.split(":");
        	var url = "${build.app.baseurl}/resources/delegation/delegate/" + requestArr[1];
        	var params={};
        	if (requestArr[0] == "PUT")
        	{
        		params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.PUT;
        	}
        	else if (requestArr[0] == "DELETE")
        	{
        		params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.DELETE;
        	}
            
            params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
            params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.NONE;
            params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 0;
            gadgets.io.makeRequest(url, function(results) {  }, params);

        },
        spin : function()
        {
            var params = gadgets.util.getUrlParameters();
            var mid = params["mid"];
            gadgets.rpc.call(null, "spinOnUserAction", function(data) { if (data != null) { eurekastreams.delegation.makeRequest(data); } setTimeout(function() { eurekastreams.delegation.spin();}, 1000); }, mid)
        }

    };
}();
