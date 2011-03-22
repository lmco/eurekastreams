/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.requests;

import org.eurekastreams.server.domain.stream.BaseObjectType;

/**
 * A reusable request representing a shared request.
 */
public class SharedResourceRequest
{
    /**
     * Resource type.
     */
    private BaseObjectType resourceType;

    /**
     * Unique key for the resource.
     */
    private String uniqueKey;

    /**
     * Constructor.
     */
    public SharedResourceRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inResourceType
     *            the resource type
     * @param inUniqueKey
     *            the unique key for the resource
     */
    public SharedResourceRequest(final BaseObjectType inResourceType, final String inUniqueKey)
    {
        resourceType = inResourceType;
        uniqueKey = inUniqueKey;
    }

    /**
     * @return the resourceType
     */
    public BaseObjectType getResourceType()
    {
        return resourceType;
    }

    /**
     * @param inResourceType
     *            the resourceType to set
     */
    public void setResourceType(final BaseObjectType inResourceType)
    {
        resourceType = inResourceType;
    }

    /**
     * @return the uniqueKey
     */
    public String getUniqueKey()
    {
        return uniqueKey;
    }

    /**
     * @param inUniqueKey
     *            the uniqueKey to set
     */
    public void setUniqueKey(final String inUniqueKey)
    {
        uniqueKey = inUniqueKey;
    }

}
