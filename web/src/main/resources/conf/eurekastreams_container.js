//Container configuration
{"gadgets.container" : ["eureka"],
	// jsUriTemplate will have %host% and %js% substituted.
	// No locked domain special cases, but jsUriTemplate must
	// never conflict with a lockedDomainSuffix.
	"gadgets.jsUriTemplate" : "http://%host%/gadgets/js/%js%",

	// Config param to load Opensocial data for social
	// preloads in data pipelining.  %host% will be
	// substituted with the current host.
	"gadgets.osDataUri" : "http://%host%/social/rpc",
	
	"gadgets.features" : {
		"core.io" : {
		    // Note: /proxy is an open proxy. Be careful how you expose this!
		    "proxyUrl" : "http://%host%/gadgets/proxy?refresh=%refresh%&url=%url%%rewriteMime%",
		    "jsonProxyUrl" : "http://%host%/gadgets/makeRequest"
	  	},
		"rpc" : {
			"parentRelayUrl" : "http://localhost:8080/gadgets/files/container/rpc_relay.html"
		},
		"opensocial" : {
		    // Path to fetch opensocial data from
		    // Must be on the same domain as the gadget rendering server
		    "path" : "http://%host%/social/rpc",
		    // Path to issue invalidate calls
		    "invalidatePath" : "http://%host%/gadgets/api/rpc",
		    "domain" : "shindig",
		    "enableCaja" : false,
		    "supportedFields" : {
		       "person" : ["id", {"name" : ["familyName", "givenName", "unstructured"]}, "thumbnailUrl", "profileUrl"],
		       "activity" : ["id", "title"]
			}
		},
		"osapi" : {
		    // The endpoints to query for available JSONRPC/REST services
		    "endPoints" : [ "http://%host%/social/rpc", "http://%host%/gadgets/api/rpc" ]                   
		},
		"eurekastreams-checklist" : {
			"parentBaseUrl" : "http://localhost:8080"
		}
	}
}