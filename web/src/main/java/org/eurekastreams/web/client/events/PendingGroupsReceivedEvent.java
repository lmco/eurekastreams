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

import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Pending groups received event.
 */
public class PendingGroupsReceivedEvent
{
    /**
     * The groups.
     */
    private PagedSet<DomainGroupModelView> groups;

    /**
     * Constructor.
     * 
     * @param inGroups
     *            the groups.
     */
    public PendingGroupsReceivedEvent(final PagedSet<DomainGroupModelView> inGroups)
    {
        groups = inGroups;
    }

    /**
     * Get the groups.
     * 
     * @return the groups.
     */
    public PagedSet<DomainGroupModelView> getGroups()
    {
        return groups;
    }

}
