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

import org.apache.log4j.Logger;
import org.eurekastreams.server.domain.Organization;
import org.hibernate.search.bridge.StringBridge;

/**
 * Class bridge to determine if an organization is the root org.
 */
public class IsRootOrganizationClassBridge implements StringBridge
{
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(IsRootOrganizationClassBridge.class);

    /**
     * Return "true" if the input organization is its own parent, thus the root org.
     * 
     * @param orgObj
     *            the Organization to parse
     * @return "true" if the input Organization is the root org, else null
     */
    @Override
    public String objectToString(final Object orgObj)
    {
        if (orgObj == null || !(orgObj instanceof Organization))
        {
            // null isn't indexed
            log.debug("No org to parse - orgObj was null or wrong type");
            return null;
        }

        Organization org = (Organization) orgObj;
        if (org.getParentOrganization().getId() == org.getId())
        {
            log.info("Found the root org with short name: " + org.getShortName());
            return "true";
        }
        return null;
    }

}
