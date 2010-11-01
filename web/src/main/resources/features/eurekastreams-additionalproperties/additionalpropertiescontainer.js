if (typeof eurekastreams == "undefined" || !eurekastreams) {var eurekastreams = {};}
if (typeof eurekastreams.additionalproperties == "undefined" || !eurekastreams.additionalproperties) {eurekastreams.additionalproperties = {};}

eurekastreams.additionalproperties.container = function()
{    
    return {
    	get : function(key)
        {
    		return gwt_getAdditionalProperty(key);
        }
    };
}();

gadgets.rpc.register('getAdditionalProperty', eurekastreams.additionalproperties.container.get);