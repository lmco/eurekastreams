if (typeof(EurekaKit) === "undefined") {
    var EurekaKit = {};
}

//This is the generic request. 
EurekaKit.jsonRequest = function(spec)
{
	var result = {};
	var eventBus = spec.eventBus || EurekaKit.EventBusFactory.createEventBus();
	
	//event keys
	var successEventKey = spec.successEventKey || Math.random().toString();
	var errorEventKey = spec.errorEventKey || Math.random().toString();
	var beforeSendEventKey = spec.beforeSendEventKey || Math.random().toString();
	var completeEventKey = spec.completeEventKey || Math.random().toString();
	
	// function that creates populated url from params.
	var paramsToUrlTransformer = spec.paramsToUrlTransfomer;
	
	
	function getOnSuccess(executeParams)
	{
		return function(data, textStatus, jqXHR)
		{
			if(!data.fromCache && executeParams.useCache)
			{
				jQuery.jStorage.set(executeParams.url, data);
			}
			
			eventBus.publish(successEventKey, data);
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
	
	/**
	* useCache = null: Trip to server, no caching of results
	* useCache != null: Check cache, return if possible, then trip to server
	*	to update cache and return new value. (values from cache will have fromCache=true)
	* useCache != null && useCache.noUpdate. Return from cache, or trip to server if not
	* 	in cache. No "update" trip to server will be made to get latest.
	**/
	result.execute = function(params, useCache)
	{
		var executeParams = {};
		executeParams.params = params;
		executeParams.useCache = useCache;		
		//this is the full url replaced with param data.
		executeParams.url = paramsToUrlTransformer(params);
		//executeParams.url = url;
	
		//create function closures to use on ajax call
		var onSuccess = getOnSuccess(executeParams);
		var onError = getOnError(executeParams);
		var onBeforeSend = getOnBeforeSend(executeParams);
		var onComplete = getOnComplete(executeParams);
		
		if(useCache)
		{
			//alert("use cache, checking: " + executeParams.url);
			result = jQuery.jStorage.get(executeParams.url);
			
			if(result)
			{
				result.fromCache = true;
				onSuccess(result, null, null);
				if(useCache.noUpdate)
				{
					return;
				}
			}
		}
		
		jQuery.ajax({url: executeParams.url,
			dataType: "json",
			success: onSuccess,
			error: onError,
			beforeSend: onBeforeSend,
			complete: onComplete});
	};
	
	result.observe = function(successHandler, errorHandler)
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
