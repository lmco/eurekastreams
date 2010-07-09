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
package org.eurekastreams.server.service.actions.requests;

import java.io.Serializable;

/**
 * Request parameters for updating an organization name.
 */
public class UpdateOrganizationNameRequest implements Serializable
{
    /**
     * Constructor.
     *
     * @param inOrganizationId
     *            the organization id.
     * @param inNewOrganizationName
     *            the new organization name.
     */
    public UpdateOrganizationNameRequest(final long inOrganizationId, final String inNewOrganizationName)
    {
        organizationId = inOrganizationId;
        newOrganizationName = inNewOrganizationName;
    }

    /**
     * The serial version id.
     */
    private static final long serialVersionUID = -2977201424343368822L;

    /**
     * The organization id.
     */
    private long organizationId;

    /**
     * The new organization name.
     */
    private String newOrganizationName;

    /**
     * @return the organizationId
     */
    public long getOrganizationId()
    {
        return organizationId;
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
     * @return the newOrganizationName
     */
    public String getNewOrganizationName()
    {
        return newOrganizationName;
    }

    /**
     * @param inNewOrganizationName
     *            the newOrganizationName to set
     */
    public void setNewOrganizationName(final String inNewOrganizationName)
    {
        newOrganizationName = inNewOrganizationName;
    }

}
