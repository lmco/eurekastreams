if (typeof eurekastreams == "undefined" || !eurekastreams) {var eurekastreams = {};}
if (typeof eurekastreams.checklist == "undefined" || !eurekastreams.checklist) {eurekastreams.checklist = {};}

eurekastreams.checklist = function()
{
    return{     
        registerTasks : function(jsonTaskArray)
        {
            gadgets.rpc.call(null, "registerTasks", null, jsonTaskArray);
        },
        
        registerTasks_good : function(jsonTaskArray)
        {        
            var differentSize = false;
            if (parent.gadgetCache == null
                    || parent.gadgetCache[gadgetDefId] == null)
            {
                differentSize = true;
            } else
            {
                if (parent.gadgetCache[gadgetDefId].tasks.length != jsonTaskArray.length)
                {
                    differentSize = true;
                } else
                {
                    var found = 0;
        
                    for ( var i = 0; i < jsonTaskArray.length; i++)
                    {
                        for ( var j = 0; j < parent.gadgetCache[gadgetDefId].tasks.length; j++)
                        {
                            if (parent.gadgetCache[gadgetDefId].tasks[j].name == jsonTaskArray[i].name
                                    && parent.gadgetCache[gadgetDefId].tasks[j].description == jsonTaskArray[i].description)
                            {
                                found++;
                            }
                        }
                    }
                }
            }
        
            if (differentSize || found != jsonTaskArray.length)
            {
                var params =
                {};
                params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.PUT;
                params[gadgets.io.RequestParameters.POST_DATA] = "{ tasks : "
                        + gadgets.json.stringify(jsonTaskArray) + "}";
                gadgets.io.makeRequest(parentBaseUrl + "/resources/gadgets/"
                        + gadgetDefId + "/checklist", function()
                {
                }, params);
            }
        },
        
        completeTask : function(taskName, completed)
        {
            gadgets.rpc.call(null, "completeTask", null, taskName, completed);
        },
        
        completeTask_good : function(taskName, completed)
        {
            var accountId;
            var resourcePath;
        
            if (parent.ORGNAME != null)
            {
                accountId = parent.ORGNAME;
                resourcePath = "organizations";
            } else if (parent.GROUPNAME != null)
            {
                accountId = parent.GROUPNAME;
                resourcePath = "groups";
            } else
            {
                accountId = parent.OWNER;
                resourcePath = "people";
            }
        
            if (completed)
            {
                var params =
                {};
                params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
                params[gadgets.io.RequestParameters.POST_DATA] = "{ 'name' : '"
                        + taskName + "' }";
                gadgets.io.makeRequest(parentBaseUrl + "/resources/" + resourcePath
                        + "/" + accountId + "/gadgets/" + gadgetDefId
                        + "/completedTasks", function()
                {
                    parent.gwt_refreshChecklist();
                }, params);
            } else
            {
                taskName = taskName.replace(/ /g, "%20");
                var params =
                {};
                params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.DELETE;
                gadgets.io.makeRequest(parentBaseUrl + "/resources/" + resourcePath
                        + "/" + accountId + "/gadgets/" + gadgetDefId
                        + "/completedTasks/" + taskName, function()
                {
                    parent.gwt_refreshChecklist();
                }, params);
            }
        }
    };
}();