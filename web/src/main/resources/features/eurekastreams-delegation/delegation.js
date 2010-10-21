if (typeof eurekastreams == "undefined" || !eurekastreams) {var eurekastreams = {};}
if (typeof eurekastreams.delegation == "undefined" || !eurekastreams.delegation) {eurekastreams.delegation = {};}


eurekastreams.delegation = function()
{
    return{
        insertDropDown : function(data, container)
        {
                container.css('padding','10px');
                var html = "<strong style='font-size:12px;padding-bottom:4px;'>Choose a View:</strong><select>";
                html += "<option value='none'>Select a delegated view...</option>";

                for (var i = 0; i<data.length; i++)
                {
                        html += "<option value='" + data[i][0] + "'>" + data[i][1] + "</option>";
                }

                html += "</select>";
                container.append(html);
        },
        setup : function(appKey, ex)
        {
                var params = gadgets.util.getUrlParameters();
                var mid = params["mid"];
                gadgets.rpc.call(null, "setupDelegation", null, mid, appKey);

                // Get current delegator from server...

                // Get delegators from server.
                var dataFromServer = ["sterleck", "bhmayo"];
                var container = jQuery("<div class='delegation-dropdown'></div>");
                ex.addSection("Delegation", container, false);

                this.insertDropDown([["sterleck","Stephen Terlecki"],["bhmayo","Brian Mayo"]], container);
        }
    };
}();
