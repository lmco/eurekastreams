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
                                html += this.getDelegateWidget(moduleId, delegates[moduleId][i].ntid, delegates[moduleId][i].displayName, delegates[moduleId][i].avatarUrl);
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

                // Go to Eureka Apps to get list of current delegates
                delegates[moduleId] = [{ ntid : "ronanoa1", displayName : "Anthony Romano", avatarUrl : "https://eureka.isgs.lmco.com/eurekastreams/photos?img=nd1d4a5ae2de3af79a93aeb2cd33ce53" }, { ntid : "keohanec", displayName : "Chris Keohane", avatarUrl : "https://eureka.isgs.lmco.com/eurekastreams/photos?img=s79e29fa9db25cf2a7808e1a77f4b1c2"}];
        }
    };
}();


gadgets.rpc.register('setupDelegation', eurekastreams.delegation.container.setupDelegation);