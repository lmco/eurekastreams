/*
 * This is a core set of features for the Eureka Streams Container.
 */

var eurekastreams = eurekastreams || {};

eurekastreams.core = function() {
    
    return {
        getAppId: function(callback)
        {
        	gadgets.rpc.call(null, "getAppId", callback);
        },
        
        getModuleId: function()
        {
            return gadgets.util.getUrlParameters()['mid'];
        },
        
        refreshCurrentGadget : function()
        {
        	gadgets.rpc.call(null, "refreshCurrentGadget");
        },
        
        //This method tests a json dataset to see if it is empty.
        //This method tests a json dataset to see if it is empty.
        emptyHash: function(hash)
        {
            for(var i in hash)
            {
                return false;
            }
            return true;
        },
        
        //This method generates an event to have the container show a UI notification.
        triggerShowNotificationEvent : function(inNotification)
        {
        	gadgets.rpc.call(null, "triggerShowNotificationEvent", null, inNotification);
        },
        
        //This method navigates to a relative eureaka streams url
        navigate : function(relativeUrl)
        {
        	gadgets.rpc.call(null, "eurekaNavigate", null, relativeUrl);
        },
        
        //This method
        getTimeAgo : function(theDate)
        {
        	if(theDate==null)
        	{
        	    return "";
        	}

        	try
        	{
        	    var nowDate = new Date();
        	    theDate = new Date(theDate);
        	    
        	    if(theDate == "Invalid Date")
        	    {
        	        return "";
        	    }
        	    
        	    var seconds = 1000;
        	    var minutes = 60 * 1000;
        	    var hours = 60 * 60 * 1000;
        	    var days = (24 * 60 * 60 * 1000);
        	    
        	    var months = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        	    var daysOfWeek = new Array("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
        	    
        	    var timeOfDay = "";
        	    var halfDay = 12;
        	    
        	    var hour = theDate.getHours();
        	    var pm = false;
        	    if (hour == 0)
        	    {
        	        hour = halfDay;
        	    }
        	    else if (hour >= halfDay)
        	    {
        	        pm = true;
        	        if (hour > halfDay)
        	        {
        	            hour -= halfDay;
        	        }
        	    }

        	    timeOfDay += hour;
        	    timeOfDay += ":";
        	    
        	    var minute = theDate.getMinutes();
        	    if (minute <= 9)
        	    {
        	        timeOfDay += '0';
        	    }
        	    
        	    timeOfDay += minute;
        	    
        	    if (pm)
        	       timeOfDay += "pm";
        	    else
        	       timeOfDay += "am";      
        	    
        	    var deltaMilliseconds = (nowDate.getTime() - theDate.getTime());
        	    
        	    if (deltaMilliseconds < 2 * days)
                {
                    // CANNOT just use a delta! Need to actually make sure the day of the event is within the day prior to
                    // today. Consider: if it is 8:30AM now, yesterday = everything from 8.5 to 32.5 hours ago. Using 24-48
                    // hours ago means that some of the day before yesterday will be labeled as yesterday which is just plain
                    // wrong.
                    var dayOfWeekDelta = nowDate.getDay() - theDate.getDay();
                    if (dayOfWeekDelta == 1 || dayOfWeekDelta == 1 - 7)
                    {
                        return "Yesterday at " + timeOfDay;
                    }
                }
        	    
        	    if (deltaMilliseconds < minutes)
                {
                    return "Less than 1 minute ago";
                }
                else if (deltaMilliseconds < 2 * minutes)
                {
                    return "1 minute ago";
                }
                else if (deltaMilliseconds < hours)
                {
                    return Math.floor(deltaMilliseconds/minutes)+" minutes ago";
                }
                else if (deltaMilliseconds < 2 * hours)
                {
                    return "1 hour ago";
                }
                else if (deltaMilliseconds < days)
                {
                    return Math.floor(deltaMilliseconds/hours)+" hours ago";
                }
                else
                {
                    // for older than yesterday

                    // For the past week, use the day of week name + time. But cut it off such that if today is Tuesday, that
                    // last Tuesday doesn't fall under this rule. That way there's no confusion about which Tuesday it's talking
                    // about.
                    if (deltaMilliseconds < 7 * days && nowDate.getDay() != theDate.getDay())
                    {
                        return daysOfWeek[theDate.getDay()] + " at " + timeOfDay;
                    }
                    else if (deltaMilliseconds < (365*days))
                    {
                        return months[theDate.getMonth()] + " " + theDate.getDate() + " at " + timeOfDay;
                    }
                    else
                    {
                    	return months[theDate.getMonth()] + " " + theDate.getDate() + "," + theDate.getFullYear() + " at " + timeOfDay;
                    }
                }
        	}
        	catch(err)
        	{
        	    // When an invalid date or other exception occurs return a blank string 
        	    // so the ui is not messy.
        	    return err.message;
        	}
        }
    };
}();
