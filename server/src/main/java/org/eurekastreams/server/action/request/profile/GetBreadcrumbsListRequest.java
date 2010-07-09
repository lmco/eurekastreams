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
package org.eurekastreams.server.action.request.profile;

import java.io.Serializable;

/**
 * Create breadcrumbs request.
 */
public class GetBreadcrumbsListRequest implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -2125283677247223754L;

    /**
     * The organization id to use as the starting point for lookup up the org hierarchy.
     */
    private long organizationId;

    /**
     * Constructor.
     * 
     * @param inOrganizationId
     *            the organization id to set.
     */
    public GetBreadcrumbsListRequest(final long inOrganizationId)
    {
        organizationId = inOrganizationId;
        //baseUrl = inBaseUrl;
    }
    
    /**
     * Empty constructor for serialization.
     */
    @SuppressWarnings("unused")
    private GetBreadcrumbsListRequest()
    {
        //do nothing
    }

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
}
