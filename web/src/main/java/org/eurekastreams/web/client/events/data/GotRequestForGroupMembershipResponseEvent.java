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
package org.eurekastreams.web.client.events.data;

import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Event for when a list of people requesting membership in a group is received.
 */
public class GotRequestForGroupMembershipResponseEvent extends BaseDataResponseEvent<PagedSet<PersonModelView>>
{
    /** Group. */
    private long groupId;

    /**
     * Group short name.
     */
    private String groupShortName;

    /**
     * Constructor.
     * 
     * @param inGroupId
     *            Group id.
     * @param inGroupShortName
     *            Group short name.
     * @param inResponse
     *            Response.
     */
    public GotRequestForGroupMembershipResponseEvent(final long inGroupId, final String inGroupShortName,
            final PagedSet<PersonModelView> inResponse)
    {
        super(inResponse);
        groupId = inGroupId;
        groupShortName = inGroupShortName;
    }

    /**
     * @return the groupId
     */
    public long getGroupId()
    {
        return groupId;
    }

    /**
     * @param inGroupId
     *            the groupId to set
     */
    public void setGroupId(final long inGroupId)
    {
        groupId = inGroupId;
    }

    /**
     * @return the groupShortName
     */
    public String getGroupShortName()
    {
        return groupShortName;
    }

    /**
     * @param inGroupShortName
     *            the groupShortName to set
     */
    public void setGroupShortName(final String inGroupShortName)
    {
        groupShortName = inGroupShortName;
    }
}
