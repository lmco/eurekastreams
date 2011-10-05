if (typeof(EurekaKit) === "undefined") {
    var EurekaKit = {};
}

EurekaKit.EventBusFactory =  
{		
	// single method, create event bus.
	createEventBus : function()
	{		
		 //subscribers array.
		var subscribers = [];
		
		//Return instance of EventBus
		return {			
		
			//subscribe to an event
			subscribe : function(eventKey, handler)
			{
				if (subscribers[eventKey] == null)
		        {
					subscribers[eventKey] = new Array();
		        }
				subscribers[eventKey].push(handler);	
			},			
		
			//publish an event
			publish : function(eventKey, data)
			{					
				if (subscribers[eventKey] != null)
		        {
		            for (var i = 0; i < subscribers[eventKey].length; i++)
		            {
		            	subscribers[eventKey][i](data);
		            }
		        }					
			},
			
			//clear all subscribers from event bus.
			clearSubscribers: function()
			{
				subscribers = [];
			}				
		}		
	}
};
	
