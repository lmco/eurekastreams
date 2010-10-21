if (typeof eurekastreams == "undefined" || !eurekastreams) {var eurekastreams = {};}
if (typeof eurekastreams.delegation == "undefined" || !eurekastreams.delegation) {eurekastreams.delegation = {};}

eurekastreams.delegation.container = function()
{
    var appKeys = [];
    var delegates = [];

    return {
    	getDelegateWidget : function(moduleId, ntid, displayName, avatarUrl)
    	{
    		return "<div class='delegation-item "+ntid+"'><div class='avatar'><a class='gwt-InlineHyperlink' href='#people/" + ntid + "'><img class='gwt-Image avatar-image avatar-image-Small' src='" + avatarUrl + "'></a></div><div class='connection-item-info'><div class='connection-item-name'>" + displayName + "</div><a href='javascript:eurekastreams.delegation.container.removeDelegate("+moduleId+",\""+ntid+"\")'>Remove Delegate</a></div></div>"
    	},
    	addADelegate : function(moduleId, ntid, displayName, avatarUrl)
    	{
    		if (jQuery("#gadget-zone-render-zone-"+ moduleId +" .delegate-item." + ntid).length == 0)
    		{
    			jQuery("#delegate-new-area-"+moduleId).append(this.getDelegateWidget(moduleId, ntid, displayName, avatarUrl));
    			// Call back to server.
    		}
    	},
    	removeDelegate : function(moduleId, ntid)
    	{
    		jQuery("#gadget-zone-render-zone-"+ moduleId +" .delegation-item." + ntid).remove();
    		// Call back to server.
    	},
        editButtonClicked : function(moduleId)
        {
                var container = jQuery("#gadget-zone-render-zone-"+ moduleId +" .gadgets-gadget-user-prefs-dialog");

                if (container.is(':visible') && container.text() != "")
                {
                        var html = "<div class='delegation-container'><div class='delegation-title'>Current Delegates</div>";
                        for (var i = 0; i < delegates[moduleId].length; i++)
                        {
                                html += this.getDelegateWidget(moduleId, delegates[moduleId][i][0], delegates[moduleId][i][1], delegates[moduleId][i][2]);
                        }
                        html += "<div id='delegate-new-area-"+moduleId+"'></div><div class='add-a-delegate'>+ <a href='javascript:gwt_launchEmpLookup(function(ntid, displayName, avatarUrl) { eurekastreams.delegation.container.addADelegate("+moduleId+", ntid, displayName, avatarUrl); });'>Add Delegate</a></div></div>";
                        container.prepend(html);
                }
                else
                {
                        setTimeout(function() { eurekastreams.delegation.container.editButtonClicked(moduleId); }, 20);
                }
        },
        setupDelegation : function(moduleId, appKey)
        {
                appKeys[moduleId] = appKey;

                // Call back to server.
                var dataFromServer = ["romanoa1", "sterleck"];
                gwt_bulkGetPeople(dataFromServer, function(data) { delegates[moduleId] = data; });
        }
    };
}();


gadgets.rpc.register('setupDelegation', eurekastreams.delegation.container.setupDelegation);