/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.stream.Activity;
import org.hibernate.search.bridge.StringBridge;

/**
 * Class bridge to get for activity app source.
 */
public class ActivitySourceClassBridge implements StringBridge
{
    /**
     * Convert the input Message or Activity object into the name of the source app.
     * 
     * @param msgObject
     *            the Message or Activity
     * @return the input Message object with name of the source app.
     */
    @Override
    public String objectToString(final Object msgObject)
    {
        Activity activity = (Activity) msgObject;

        if (null != activity.getAppId())
        {
            return Long.toString(activity.getAppId());
        }
        else
        {
            return "0";
        }
    }
}
