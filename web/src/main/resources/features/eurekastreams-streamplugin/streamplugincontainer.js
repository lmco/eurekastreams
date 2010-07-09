if (typeof eurekastreams == "undefined" || !eurekastreams) {var eurekastreams = {};}
if (typeof eurekastreams.streamplugin == "undefined" || !eurekastreams.streamplugin) {eurekastreams.streamplugin = {};}

eurekastreams.streamplugin.container = function()
{    
	var getFeedCallback;
    return {
    	getFormValue : function(key)
        {
    		gwt_getFormValue(key);
        },

        registerGetFeedCallback : function(inGetFeedCallback)
        {
    		gwt_registerGetFeedCallback(inGetFeedCallback);
        },

        addUrlValidator : function(label, key, value, instructions, required, command) 
        { 
        	gwt_addUrlValidator(label, key, value, instructions, required, command);
        },
        
        addTextBox : function(size, label, key, value, instructions, required) 
        { 
        	gwt_addTextBox(size, label, key, value, instructions, required);
        },
    
        addCheckBox : function(label, key, value, instructions, required, checked) 
        { 
            gwt_addCheckBox(label, key, value, instructions, required, checked);
        },
    
        addDropDown : function(label, key, values, currentValue, instructions, required) 
        { 
        	gwt_addDropDown(label, key, values, currentValue, instructions, required);
        }
    };
}();

gadgets.rpc.register('getFormValue', eurekastreams.streamplugin.container.getFormValue);
gadgets.rpc.register('registerGetFeedCallback', eurekastreams.streamplugin.container.registerGetFeedCallback);
gadgets.rpc.register('addUrlValidator', eurekastreams.streamplugin.container.addUrlValidator);
gadgets.rpc.register('addTextBox', eurekastreams.streamplugin.container.addTextBox);
gadgets.rpc.register('addCheckBox', eurekastreams.streamplugin.container.addCheckBox);
gadgets.rpc.register('addDropDown', eurekastreams.streamplugin.container.addDropDown);