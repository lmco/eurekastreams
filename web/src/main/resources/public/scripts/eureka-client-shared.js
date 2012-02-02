(function() {
	// Create the namespace
	if(typeof EurekaStreams === 'undefined'){
		EurekaStreams = {};
	}
})();
(function() {
	// Create the module
	EurekaStreams.Client = EurekaStreams.Client || {};
	
	
	// types of segments
	EurekaStreams.Client.TYPE_LINK = 'LINK';
	EurekaStreams.Client.TYPE_TEXT = 'TEXT';
	EurekaStreams.Client.TYPE_TAG = 'TAG';
	
	// Common behavior for regex-based filters
	EurekaStreams.Client.basicRegexFilterBase = function(node, utils, regex, matchHandler) {
		if (node.type !== EurekaStreams.Client.TYPE_TEXT) {
			return;
		}
		
		var content = node.content;
		if (typeof regex === 'string') {
			regex = new RegExp(regex, 'g');
		}
		regex.lastIndex = 0;
		
		var parts = [];
		var len = content.length;
		var start = 0;
		while(start < len) {
			var match = regex.exec(content);
			if (match) {
				if (match.index > start) {
					parts.push(utils.makeText(content.slice(start, match.index)));
				}
				var result = matchHandler(match, utils);
				if (result) {
					parts.push(result);
				}
				start = regex.lastIndex;
			} else {
				parts.push(utils.makeText(content.slice(start)));
				break;
			}
		}
		return parts;
	};

	// Filter to break text on newlines
	EurekaStreams.Client.newlineFilter = function(node, utils) {
		return EurekaStreams.Client.basicRegexFilterBase(node, utils,
				/\r\n|\n|\r/g,
				function (match, utils) {
					return utils.makeTag('br/');
				});
	};
	
	// Filter to turn URLs into links
	EurekaStreams.Client.plainUrlFilter = function(node, utils) {
		return EurekaStreams.Client.basicRegexFilterBase(node, utils, 
				/(?:((?:https?|ftp|file):\/\/)|(?:www\.))[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|]/ig,
				function (match, utils) {
					var url = match[1] ? match[0] : 'http://' + match[0];
					return utils.makeLink(match[0], url);
				});
	};
	
	// Filter to turn (a subset of) Markdown links into links
	EurekaStreams.Client.markdownLinkFilter = function(node, utils) {
		return EurekaStreams.Client.basicRegexFilterBase(node, utils, 
				/\[([^\]\[]+)\]\(([\:\.-A-Za-z0-9+&@#\/%=~_|]*)\)/g,
				function (match, utils) {
					return utils.makeLink(match[1], match[2]);
				});
	};
	
	// Filter to turn hashtags into links
	// This would use basicRegexFilterBase if JavaScript supported regex lookbehind assertions.
	EurekaStreams.Client.hashtagFilter = function(node, utils) {
		if (node.type !== EurekaStreams.Client.TYPE_TEXT) {
			return;
		}
		
		var validCharsBeforeHashtag = "-.,<>)#[]@!$'*+,;=% \t\"\n";
		
		var content = node.content;
		var regex = /#[0-9A-Za-z_-]+/g;
		regex.lastIndex = 0;
		
		var parts = [];
		var len = content.length;
		var start = 0;
		while(start < len) {
			var match = regex.exec(content);
			if (match) {
				// only a match if at line start or preceded by allowed character
				if (match.index > 0 && validCharsBeforeHashtag.indexOf(content.charAt(match.index-1)) < 0) {
					match = undefined;
				} else {
					if (match.index > start) {
						parts.push(utils.makeText(content.slice(start, match.index)));
					}
					parts.push(utils.makeLink(match[0], ''));
					start = regex.lastIndex;
				}
			}
			if (!match) {
				parts.push(utils.makeText(content.slice(start)));
				break;
			}
		}
		return parts;
	};	
	
	// Converts content text into a linked list of segments using a list of filters. 
	EurekaStreams.Client.textToList = function(text, filters) {
		
		// helpers provided to the filters
		var utils = {
			makeText : function(text) {
				return {type: EurekaStreams.Client.TYPE_TEXT, content: text }
			},
			makeTag : function(text) {
				return {type: EurekaStreams.Client.TYPE_TAG, content: text }
			},
			makeLink : function(text, url) {
				return {type: EurekaStreams.Client.TYPE_LINK, content: text, url: url }
			}
		};
		
		// create the initial list (use a head node for simplicity when changing the linked list)
		var head = {next: utils.makeText(text)};
		
		// apply each filter to the list
		for (var i=0; i < filters.length; i++) {
			// loop through all the nodes
			var last = head;
			var curr = last.next;
			while (curr) {
				var next = curr.next;
				var replacements = filters[i](curr, utils);
				if (replacements) {
					// remove old node
					last.next = next;
					// insert new nodes
					var len = replacements.length;
					for (var j=0; j < len; j++) {
						curr = replacements[j];
						last.next = curr;
						curr.next = next;
						last = curr;
					}
				} else {
					last = curr;
				}
				curr = next;
			}
		}
		
		return head.next;
	};
	
})();
