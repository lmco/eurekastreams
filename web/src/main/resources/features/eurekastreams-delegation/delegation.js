if (typeof eurekastreams == "undefined" || !eurekastreams) {var eurekastreams = {};}
if (typeof eurekastreams.delegation == "undefined" || !eurekastreams.delegation) {eurekastreams.delegation = {};}

eurekastreams.delegation = function()
{
    return{     
    	setup : function(appKey) 
        {
    		var params = gadgets.util.getUrlParameters();
    		var mid = params["mid"];
    		gadgets.rpc.call(null, "setupDelegation", null, mid, appKey);
        }
    };
}();