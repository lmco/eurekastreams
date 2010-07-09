if (typeof eurekastreams == "undefined" || !eurekastreams) {var eurekastreams = {};}
if (typeof eurekastreams.checklist == "undefined" || !eurekastreams.checklist) {eurekastreams.checklist = {};}

eurekastreams.checklist.container = function()
{    
    return {
        
        registerTasks : function(jsonTaskArray)
        {
            var id = gadgets.container.gadgetService.getGadgetIdFromModuleId(this.f);
            var gadget = gadgets.container.getGadget(id);
            var gadgetDefId = gadget.appId;
            var differentSize = false;
            if (gadgetCache == null
                    || gadgetCache[gadgetDefId] == null)
            {
                differentSize = true;
            } else
            {
                if (gadgetCache[gadgetDefId].tasks.length != jsonTaskArray.length)
                {
                    differentSize = true;
                } else
                {
                    var found = 0;
        
                    for ( var i = 0; i < jsonTaskArray.length; i++)
                    {
                        for ( var j = 0; j < gadgetCache[gadgetDefId].tasks.length; j++)
                        {
                            if (gadgetCache[gadgetDefId].tasks[j].name == jsonTaskArray[i].name
                                    && gadgetCache[gadgetDefId].tasks[j].description == jsonTaskArray[i].description)
                            {
                                found++;
                            }
                        }
                    }
                }
            }
        
            if (differentSize || found != jsonTaskArray.length)
            {
                var postData = "{ tasks : "
                        + gadgets.json.stringify(jsonTaskArray) + "}";
                eurekastreams.container.sendRequestToServer("/resources/gadgets/"
                        + gadgetDefId + "/checklist", "PUT", postData, null, true);
            }
        },
        
        completeTask : function(taskName, completed)
        { 
            var id = gadgets.container.gadgetService.getGadgetIdFromModuleId(this.f);
            var gadget = gadgets.container.getGadget(id);
            var gadgetDefId = gadget.appId;
            //alert("Completing " + completed + " a task for app id " + gadget.appId);
            var accountId;
            var resourcePath;
        
            if (!(typeof ORGNAME == "undefined") && ORGNAME != null)
            {
                accountId = ORGNAME;
                resourcePath = "organizations";
            } 
            else if (!(typeof GROUPNAME == "undefined") && GROUPNAME != null)
            {
                accountId = GROUPNAME;
                resourcePath = "groups";
            } 
            else
            {
                accountId = OWNER;
                resourcePath = "people";
            }
        
            if (completed)
            {
                var postData = "{ 'name' : '"
                        + taskName + "' }";
                eurekastreams.container.sendRequestToServer("/resources/" + resourcePath
                        + "/" + accountId + "/gadgets/" + gadgetDefId
                        + "/completedTasks", "POST",  postData, function()
                {
                    gwt_refreshChecklist();
                }, true);
            } 
            else
            {
                taskName = taskName.replace(/ /g, "%20");
                eurekastreams.container.sendRequestToServer("/resources/" + resourcePath
                        + "/" + accountId + "/gadgets/" + gadgetDefId
                        + "/completedTasks/" + taskName, "DELETE", null, function()
                {
                    gwt_refreshChecklist();
                }, true);
            }
        }
    };
}();

gadgets.rpc.register('registerTasks', eurekastreams.checklist.container.registerTasks);
gadgets.rpc.register('completeTask', eurekastreams.checklist.container.completeTask);