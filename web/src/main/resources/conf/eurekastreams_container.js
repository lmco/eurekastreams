//Container configuration for Eureka Streams.
{"gadgets.container" : ["${build.gadget.container.name}"],
	// jsUriTemplate will have %host% and %js% substituted.
	// No locked domain special cases, but jsUriTemplate must
	// never conflict with a lockedDomainSuffix.
	"gadgets.jsUriTemplate" : "${build.gadget.container.protocol}%host%/gadgets/js/%js%",

	"gadgets.uri.js.host": "${build.web.baseurl}",
	
	// Default concat Uri config; used for testing.
	"gadgets.uri.concat.host" : "${build.web.host}",
	"gadgets.uri.concat.path" : "/gadgets/concat",
	"gadgets.uri.concat.js.splitToken" : "false",

	// Default proxy Uri config; used for testing.
	"gadgets.uri.proxy.host" : "${build.web.host}",
	"gadgets.uri.proxy.path" : "/gadgets/proxy",
	
	// Config param to load Opensocial data for social
	// preloads in data pipelining.  %host% will be
	// substituted with the current host.
	"gadgets.osDataUri" : "${build.gadget.container.protocol}%host%/social/rpc",
	
	"gadgets.features" : {
		"core.io" : {
		    // Note: /proxy is an open proxy. Be careful how you expose this!
		    "proxyUrl" : "${build.gadget.container.protocol}%host%/gadgets/proxy?refresh=%refresh%&url=%url%%rewriteMime%",
		    "jsonProxyUrl" : "${build.gadget.container.protocol}%host%/gadgets/makeRequest"
	  	},
		"rpc" : {
			"parentRelayUrl" : "${build.web.baseurl}/container/rpc_relay.html"
		},
		"opensocial" : {
		    // Path to fetch opensocial data from
		    // Must be on the same domain as the gadget rendering server
		    "path" : "${build.gadget.container.protocol}%host%/social/rpc",
		    // Path to issue invalidate calls
		    "invalidatePath" : "${build.gadget.container.protocol}%host%/gadgets/api/rpc",
		    "domain" : "shindig",
		    "enableCaja" : false,
		    "supportedFields" : {
		        "person" : ["id", {"name" : ["familyName", "givenName", "unstructured"]}, "thumbnailUrl", "profileUrl"],
		        "activity" : ["appId", "body", "bodyId", "externalId", "id", "mediaItems", "postedTime", "priority", 
		                      "streamFaviconUrl", "streamSourceUrl", "streamTitle", "streamUrl", "templateParams", "title",
		                      "url", "userId"],
		        "album" : ["id", "thumbnailUrl", "title", "description", "location", "ownerId"],
		        "mediaItem" : ["album_id", "created", "description", "duration", "file_size", "id", "language", "last_updated",
		                       "location", "mime_type", "num_comments", "num_views", "num_votes", "rating", "start_time",
		                       "tagged_people", "tags", "thumbnail_url", "title", "type", "url"]
		     }
		},
		"osapi.services" : {
		    // Specifying a binding to "container.listMethods" instructs osapi to dynamicaly introspect the services
		    // provided by the container and delay the gadget onLoad handler until that introspection is
		    // complete.
		    // Alternatively a container can directly configure services here rather than having them
		    // introspected. Simply list out the available servies and omit "container.listMethods" to
		    // avoid the initialization delay caused by gadgets.rpc
		    // E.g. "gadgets.rpc" : ["activities.requestCreate", "messages.requestSend", "requestShareApp", "requestPermission"]
		    // Updated with the methods available from Eureka Streams.
		    "gadgets.rpc" : ["activities.delete","gadgets.metadata","activities.update","activities.supportedFields",
		                     "activities.get","http.put","messages.modify","appdata.get","messages.get",
		                     "system.listMethods","cache.invalidate","people.supportedFields","http.head",
		                     "http.delete","messages.create","people.get","messages.delete",
		                     "appdata.update","gadgets.tokenSupportedFields","http.post","activities.create",
		                     "http.get","appdata.delete","gadgets.token","appdata.create","gadgets.supportedFields"]
		  },
		"osapi" : {
		    // The endpoints to query for available JSONRPC/REST services
		    "endPoints" : [ "${build.web.grbaseurl}/rpc" ]                   
		},
		"eurekastreams-checklist" : {
			"parentBaseUrl" : "${build.web.baseurl}"
		}
	}
}