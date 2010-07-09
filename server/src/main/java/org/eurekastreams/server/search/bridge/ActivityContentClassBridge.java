/*
 * Copyright (c) 2009 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.search.bridge;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.Activity;
import org.hibernate.search.bridge.StringBridge;

/**
 * Class bridge to extract the content out of an Activity.
 */
public class ActivityContentClassBridge implements StringBridge
{
    /**
     * The logger.
     */
    private Log log = LogFactory.getLog(ActivityContentClassBridge.class);

    /**
     * Extract the content out of an Activity/Message.
     * 
     * @param activityObject
     *            the message
     * @return a string containing the title and body of the input Message
     */
    @Override
    public String objectToString(final Object activityObject)
    {
        StringBuffer sb = new StringBuffer();
        Activity activity = (Activity) activityObject;
        HashMap<String, String> baseObject = activity.getBaseObject();
        if (baseObject != null)
        {
            switch (activity.getBaseObjectType())
            {
                case NOTE:
                    if (baseObject != null && baseObject.containsKey("content"))
                    {
                        sb.append(baseObject.get("content"));
                    }
                    break;
                case BOOKMARK:
                    if (baseObject.containsKey("content"))
                    {
                        sb.append(baseObject.get("content"));
                    }
                    if (baseObject.containsKey("targetTitle"))
                    {
                        sb.append(" ");
                        sb.append(baseObject.get("targetTitle"));
                    }
                    if (baseObject.containsKey("description"))
                    {
                        sb.append(" ");
                        sb.append(baseObject.get("description"));
                    }
                    
                    break;
                default:
                    log
                            .error("I don't know how to pull the content from activities of type: "
                                    + activity.getBaseObjectType().toString());
                    break;
            }
        }

        return sb != null && sb.toString().length() > 0 ? sb.toString() : null;
    }
}
