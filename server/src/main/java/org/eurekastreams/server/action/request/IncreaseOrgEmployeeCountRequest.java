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
package org.eurekastreams.server.action.request;

import java.io.Serializable;

/**
 * Request that contains org id and increment amount.
 */
public class IncreaseOrgEmployeeCountRequest implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -8972085115536110671L;

    /**
     * Id of the organization.
     */
    private long organizationId;

    /**
     * Number to increment employee count by.
     */
    private int incrementBy;

    /**
     * Empty constructor.
     */
    public IncreaseOrgEmployeeCountRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inOrganizationId
     *            the id of the org.
     * @param inIncrementBy
     *            the number of employees to increment by.
     */
    public IncreaseOrgEmployeeCountRequest(final long inOrganizationId, final int inIncrementBy)
    {
        setOrganizationId(inOrganizationId);
        setIncrementBy(inIncrementBy);
    }

    /**
     * @param inOrganizationId
     *            the organizationId to set
     */
    public void setOrganizationId(final long inOrganizationId)
    {
        organizationId = inOrganizationId;
    }

    /**
     * @return the organizationId
     */
    public long getOrganizationId()
    {
        return organizationId;
    }

    /**
     * @param inIncrementBy
     *            the incrementBy to set
     */
    public void setIncrementBy(final int inIncrementBy)
    {
        incrementBy = inIncrementBy;
    }

    /**
     * @return the incrementBy
     */
    public int getIncrementBy()
    {
        return incrementBy;
    }
}
