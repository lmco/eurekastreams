if (typeof eurekastreams == "undefined" || !eurekastreams) {var eurekastreams = {};}
if (typeof eurekastreams.additionalproperties == "undefined" || !eurekastreams.additionalproperties) {eurekastreams.additionalproperties = {};}

eurekastreams.additionalproperties = function()
{
    return{     
		get : function(key, callback)
        {
    		gadgets.rpc.call(null, "getAdditionalProperty", callback, key);
        }
    };
}();