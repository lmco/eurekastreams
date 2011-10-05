if (typeof(EurekaKit) === "undefined") {
    var EurekaKit = {};
}

EurekaKit.ServiceFactory = 
{		
	//Services are just groups of EurekaKit.jsonRequests injected with correct parameters
	//to make the service call. Basically just groupings of API calls that are available.
	//Having this abstraction simplifies life for caller and 
	//encapsulates the logic for putting together different EurekaKit.jsonRequest objects.
	//TODO: this could get large quickly and turn into a mess. Investigate how to mitigate this.
	//maybe just eval a call here based on the serviceKey param?
	createService : function(inParams)
	{	
		var params = inParams || {};
		var serviceKey = params.serviceKey;
		
		//if no eventBus provided, create new one for service.
		var eventBus = params.eventBus || EurekaKit.EventBusFactory.createEventBus();
		
		switch(serviceKey)
		{
			default: //just default test stuff here for now.
			{
				var resultService = {};
				
				//build doSomething
//				var jsonRequestParams = {};
//				jsonRequestParams.eventBus = eventBus;
//				resultService.doSomething = EurekaKit.jsonRequest(jsonRequestParams);
				
				jsonRequestParams = {};
				jsonRequestParams.eventBus = eventBus;
				resultService.getSystemSettings = 
					EurekaKit.jsonRequest({url:"http://localhost:8080/api/0/read/getSystemSettings/{}"});
					
				return resultService;
			}
		}
	}
};