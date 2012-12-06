/*
 * Copyright (c) 2012-2012 Lockheed Martin Corporation
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

import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.DomainGroup;
import org.hibernate.search.bridge.StringBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class bridge to determine if a group should be shown in searches.
 */
public class IsGroupVisibleInSearchClassBridge implements StringBridge
{
    /** Log. */
    private final Logger log = LoggerFactory.getLogger(LogFactory.getClassName());

    /**
     * Convert the input object to "t" or "f" depending on whether it should be returned in searches. These return
     * values were chosen over "true"|"false" for a smaller index and to avoid stemming issues when searching.
     *
     * @param inGroupObj
     *            The DomainGroup object to check.
     * @return null if either null or the wrong type, or "t"/"f" for whether the group should be returned in searches.
     */
    @Override
    public String objectToString(final Object inGroupObj)
    {
        if (inGroupObj == null)
        {
            log.error("Object to convert is null.");
            return null;
        }
        if (!(inGroupObj instanceof DomainGroup))
        {
            log.error("Object to convert is not a DomainGroup.");
            return null;
        }

        DomainGroup group = (DomainGroup) inGroupObj;
        return group.isPending() ? "f" : "t";
    }
}
