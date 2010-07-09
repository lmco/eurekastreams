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
package org.eurekastreams.web.client.events;

import org.eurekastreams.server.domain.OrganizationTreeDTO;

/**
 * Org children found event -- used by org lookup modal.
 */
public class OrgLookupFindChildrenEvent
{
    /**
     * The result org tree.
     */
    private OrganizationTreeDTO results;

    /**
     * Constructor.
     * @param inResults  The result org tree.
     */
    public OrgLookupFindChildrenEvent(final OrganizationTreeDTO inResults)
    {
        results = inResults;
    }

    /**
     * @return The result org tree.
     */
    public OrganizationTreeDTO getResults()
    {
        return results;
    }

}
