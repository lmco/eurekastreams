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
package org.eurekastreams.server.action.request.stream;

import java.io.Serializable;

/**
 * Request for updating activities posted to a group after group parent org has changed.
 * 
 */
public class SyncGroupActivityRecipientParentOrganizationRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 6403132560433064723L;

    /**
     * Group short name.
     */
    private String groupKey;

    /**
     * New parent org short name.
     */
    private String newOrgParentKey;

    /**
     * Old parent org short name.
     */
    private String oldOrgParentKey;

    /**
     * Constructor.
     * 
     * @param inGroupKey
     *            Group short name.
     * @param inNewOrgParentKey
     *            New parent org short name.
     * @param inOldOrgParentKey
     *            Old parent org short name.
     */
    public SyncGroupActivityRecipientParentOrganizationRequest(final String inGroupKey, final String inNewOrgParentKey,
            final String inOldOrgParentKey)
    {
        groupKey = inGroupKey;
        newOrgParentKey = inNewOrgParentKey;
        oldOrgParentKey = inOldOrgParentKey;
    }

    /**
     * @return the groupKey
     */
    public String getGroupKey()
    {
        return groupKey;
    }

    /**
     * @return the newOrgParentKey
     */
    public String getNewOrgParentKey()
    {
        return newOrgParentKey;
    }

    /**
     * @return the oldOrgParentKey
     */
    public String getOldOrgParentKey()
    {
        return oldOrgParentKey;
    }

}
