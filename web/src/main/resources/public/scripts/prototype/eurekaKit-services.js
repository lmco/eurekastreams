if (typeof(EurekaKit) === "undefined") {
    var EurekaKit = {};
}

EurekaKit.serviceBaseUrl = "http://localhost:8080/";

EurekaKit.Services = EurekaKit.Services || {};

EurekaKit.Services.testService = function(inParams)
{
	//create params object if not passed in.
	var params = inParams || {};
	
	//if no eventBus provided, create new one for service.
	var eventBus = params.eventBus || EurekaKit.EventBusFactory.createEventBus();
	
	//create the service object to populate
	var service = {};
	
	/**
	* getSystemSettings.execute();
	**/
	service.getSystemSettings = 
		EurekaKit.jsonRequest({
			paramsToUrlTransfomer: function(params)
			{
				return EurekaKit.serviceBaseUrl + "api/0/read/getSystemSettings/{}";
			},
			eventBus: eventBus
			});
			
			
	/**
	* getActivityById.execute(5);
	**/
	service.getActivityById = 
		EurekaKit.jsonRequest({
			paramsToUrlTransfomer: function(params)
			{
				return EurekaKit.serviceBaseUrl + "api/0/read/getActivityById/{request:"+ params +"}";
			},
			eventBus: eventBus
			});
	
	//return populated service.
	return service;

};