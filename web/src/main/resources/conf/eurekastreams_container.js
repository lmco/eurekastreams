//Container configuration
{"gadgets.container" : ["${build.gadget.container.name}"],
	// jsUriTemplate will have %host% and %js% substituted.
	// No locked domain special cases, but jsUriTemplate must
	// never conflict with a lockedDomainSuffix.
	"gadgets.jsUriTemplate" : "${build.gadget.container.protocol}%host%/gadgets/js/%js%",

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
			"parentRelayUrl" : "${build.web.baseurl}/gadgets/files/container/rpc_relay.html"
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
		       "activity" : ["id", "title"]
			}
		},
		"osapi" : {
		    // The endpoints to query for available JSONRPC/REST services
		    "endPoints" : [ "${build.gadget.container.protocol}%host%/social/rpc", "${build.gadget.container.protocol}%host%/gadgets/api/rpc" ]                   
		},
		"eurekastreams-checklist" : {
			"parentBaseUrl" : "${build.web.baseurl}"
		}
	}
}