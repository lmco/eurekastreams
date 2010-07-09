if (typeof eurekastreams == "undefined" || !eurekastreams) {var eurekastreams = {};}
if (typeof eurekastreams.streamplugin == "undefined" || !eurekastreams.streamplugin) {eurekastreams.streamplugin = {};}

eurekastreams.streamplugin = function()
{
    return{     
		getFormValue : function(key)
        {
    		eurekastreams.streamplugin.asyncGetFormValue = function()
    		{
    			gadgets.rpc.call(null, "getFormValue", null, key);
    		}
    		setTimeout("eurekastreams.streamplugin.asyncGetFormValue();", 0);
        },
    
		registerGetFeedCallback : function(inGetFeedCallback)
        {
        	eurekastreams.streamplugin.asyncRegisterGetFeedCallback = function()
    		{
        		gadgets.rpc.call(null, "registerGetFeedCallback", null, inGetFeedCallback);
    		}
    		setTimeout("eurekastreams.streamplugin.asyncRegisterGetFeedCallback();", 0);
        },
    
    	addUrlValidator : function(label, key, value, instructions, required, command)
	    {
        	eurekastreams.streamplugin.asyncAddUrlValidator = function()
    		{
        		gadgets.rpc.call(null, "addUrlValidator", null, label, key, value, instructions, required, command);
    		}
    		setTimeout("eurekastreams.streamplugin.asyncAddUrlValidator();", 0);
	    },
        
        addTextBox : function(size, label, key, value, instructions, required) 
        {
        	eurekastreams.streamplugin.asyncAddTextBox = function()
    		{
                gadgets.rpc.call(null, "addTextBox", null, size, label, key, value, instructions, required);
    		}
    		setTimeout("eurekastreams.streamplugin.asyncAddTextBox();", 0);
        },
    
        addCheckBox : function(label, key, value, instructions, required, checked) 
	    {
        	eurekastreams.streamplugin.asyncAddCheckBox = function()
    		{
    	        gadgets.rpc.call(null, "addCheckBox", null, label, key, value, instructions, required, checked);
    		}
    		setTimeout("eurekastreams.streamplugin.asyncAddCheckBox();", 0);
	    },
        
	    addDropDown : function(label, key, values, currentValue, instructions, required) 
        {
        	eurekastreams.streamplugin.asyncAddDropDown = function()
    		{
                gadgets.rpc.call(null, "addDropDown", null, label, key, values, currentValue, instructions, required);
    		}
    		setTimeout("eurekastreams.streamplugin.asyncAddDropDown();", 0);
        }
    };
}();