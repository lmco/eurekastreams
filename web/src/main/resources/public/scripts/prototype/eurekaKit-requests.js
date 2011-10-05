if (typeof(EurekaKit) === "undefined") {
    var EurekaKit = {};
}

//This is the generic request. 
EurekaKit.jsonRequest = function(spec)
{
	var result = {};
	var url = spec.url || null;
	var eventBus = spec.eventBus || EurekaKit.EventBusFactory.createEventBus();
	var preCallTransformer = spec.preCallTransformer;
	var postCallTransformer = spec.postCallTransformer;
	var successEventKey = spec.successEventKey || Math.random().toString();
	var errorEventKey = spec.errorEventKey || Math.random().toString();
	var beforeSendEventKey = spec.beforeSendEventKey || Math.random().toString();
	var completeEventKey = spec.completeEventKey || Math.random().toString();
	
	function getOnSuccess(executeParams)
	{
		return function(data, textStatus, jqXHR)
		{
			if(!data.fromCache && executeParams.useCache)
			{
				alert("adding value to cache");
				jQuery.jStorage.set(executeParams.url, data);
			}
			
			eventBus.publish(successEventKey, postCallTransformer ? postCallTransformer.transform(data) : data);
		};
	}
	
	function getOnError(executeParams)
	{
		return function(request, textStatus, errorThrown)
		{
			var errorData = {};
			errorData.request = request;
			errorData.textStatus = textStatus;
			errorData.errorThrown = errorThrown;
			errorData.executeParams = executeParams;
			
			eventBus.publish(errorEventKey, errorData);
		};		
	}	
	
	function getOnBeforeSend(executeParams)
	{
		return function(jqXHR, settings)
		{
			var data = {};
			data.jqXHR = jqXHR;
			data.settings = settings;
			
			eventBus.publish(beforeSendEventKey, data);
		};
	}
	
	function getOnComplete(executeParams)
	{
		return function(jqXHR, textStatus)
		{
			var data = {};
			data.jqXHR = jqXHR;
			data.textStatus = textStatus;
			
			eventBus.publish(completeEventKey, data);
		};
	}
	
	result.execute = function(params, useCache)
	{
		var executeParams = {};
		executeParams.params = params;
		executeParams.useCache = useCache;		
		//this is the full url replaced with param data.
		executeParams.url = url;
	
		//create function closures to use on ajax call
		var onSuccess = getOnSuccess(executeParams);
		var onError = getOnError(executeParams);
		var onBeforeSend = getOnBeforeSend(executeParams);
		var onComplete = getOnComplete(executeParams);
		
		if(useCache)
		{
			alert("use cache, checking: " + executeParams.url);
			result = jQuery.jStorage.get(executeParams.url);
			alert("result: " + jQuery.toJSON(result));
			
			if(result)
			{
				result.fromCache = true;
				onSuccess(result, null, null);
			}
		}
		
		jQuery.ajax({url: url,
			dataType: "json",
			success: onSuccess,
			error: onError,
			beforeSend: onBeforeSend,
			complete: onComplete});
	};
	
	result.observe = function(successHandler, errorHandler, beforeSendHandler, completeSendHandler)
	{
		if(successHandler)
		{
			eventBus.subscribe(successEventKey, successHandler);
		}
		
		if(errorHandler)
		{
			eventBus.subscribe(errorEventKey, errorHandler);
		} 
	};
	
	result.observeServerCallStatus = function(onBeforeSendHandler, onCompleteSendHandler)
	{
		if(onBeforeSendHandler)
		{
			eventBus.subscribe(beforeSendEventKey, onBeforeSendHandler);
		}
		
		if(onCompleteSendHandler)
		{
			eventBus.subscribe(completeEventKey, onCompleteSendHandler);
		}
	};
	
	return result;
};
