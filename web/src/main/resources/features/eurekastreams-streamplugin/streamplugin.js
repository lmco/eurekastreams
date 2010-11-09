if (typeof eurekastreams == "undefined" || !eurekastreams) {var eurekastreams = {};}
if (typeof eurekastreams.streamplugin == "undefined" || !eurekastreams.streamplugin) {eurekastreams.streamplugin = {};}

eurekastreams.streamplugin = function()
{
    return{     
		getFormValue : function(key)
        {
			gadgets.rpc.call(null, "getFormValue", null, key);
        },
    
		registerGetFeedCallback : function(inGetFeedCallback)
        {
			gadgets.rpc.call(null, "registerGetFeedCallback", null, inGetFeedCallback);
        },
    
    	addUrlValidator : function(label, key, value, instructions, required, command)
	    {
			gadgets.rpc.call(null, "addUrlValidator", null, label, key, value, instructions, required, command);
	    },
        
        addTextBox : function(size, label, key, value, instructions, required) 
        {
			gadgets.rpc.call(null, "addTextBox", null, size, label, key, value, instructions, required);
        },
    
        addCheckBox : function(label, key, value, instructions, required, checked) 
	    {
			gadgets.rpc.call(null, "addCheckBox", null, label, key, value, instructions, required, checked);
	    },
        
	    addDropDown : function(label, key, values, currentValue, instructions, required) 
        {
			gadgets.rpc.call(null, "addDropDown", null, label, key, values, currentValue, instructions, required);
        }
    };
}();