/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import org.apache.log4j.Logger;
import org.eurekastreams.server.domain.stream.Activity;
import org.hibernate.search.bridge.StringBridge;

/**
 * Class bridge to determine if an Activity is public.
 */
public class IsActivityPublicClassBridge implements StringBridge
{
    /**
     * Logger.
     */
    private static Logger log = Logger
            .getLogger(IsActivityPublicClassBridge.class);

    /**
     * Convert the input Activity object to "t" or "f" depending on whether it's
     * publicly visible. These return values were chosen over "true"|"false" for
     * a smaller index and to avoid stemming issues when searching.
     * 
     * @param inActivityObj
     *            the Activity object to check for public/private access
     * @return null if either not an Activity or null, or "true"/"false" for
     *         whether or not the activity is publicly visible
     */
    @Override
    public String objectToString(final Object inActivityObj)
    {
        if (inActivityObj == null || !(inActivityObj instanceof Activity))
        {
            log.error("Found either null Activity or wrong type.");
            return null;
        }

        Activity activity = (Activity) inActivityObj;
        return activity.getIsDestinationStreamPublic() ? "t" : "f";
    }

}
