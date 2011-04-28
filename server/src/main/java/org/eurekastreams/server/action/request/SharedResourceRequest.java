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
package org.eurekastreams.server.action.request;

import java.io.Serializable;

/**
 * A reusable request representing a shared resource.
 */
public class SharedResourceRequest implements Serializable
{
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -2235934647925339155L;

    /**
     * Unique key for the resource.
     */
    private String uniqueKey;

    /**
     * If set, this will be used to query instead of the unique key - will produce faster queries.
     */
    private Long sharedResourceId;

    /**
     * The person id.
     */
    private Long personId;

    /**
     * Constructor.
     */
    public SharedResourceRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inUniqueKey
     *            the unique key for the resource
     * @param inPersonId
     *            the person Id
     */
    public SharedResourceRequest(final String inUniqueKey, final Long inPersonId)
    {
        uniqueKey = inUniqueKey;
        personId = inPersonId;
    }

    /**
     * Constructor - using shared resource id instead of unique key - faster, if you can use it.
     * 
     * @param inSharedResourceId
     *            the shared resource id
     * @param inPersonId
     *            the person Id
     */
    public SharedResourceRequest(final Long inSharedResourceId, final Long inPersonId)
    {
        sharedResourceId = inSharedResourceId;
        personId = inPersonId;
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

    /**
     * Get the person ID.
     * 
     * @return the person Id.
     */
    public Long getPersonId()
    {
        return personId;
    }

    /**
     * Set the person id.
     * 
     * @param inPersonId
     *            the person id.
     */
    public void setPersonId(final Long inPersonId)
    {
        personId = inPersonId;
    }

    /**
     * Get the shared resource id - if not null, it'll be used for a faster query.
     * 
     * @return the sharedResourceId
     */
    public Long getSharedResourceId()
    {
        return sharedResourceId;
    }

    /**
     * Set the shared resource id - if not null, will be used instead of unique key, and will be much faster.
     * 
     * @param inSharedResourceId
     *            the sharedResourceId to set
     */
    public void setSharedResourceId(final Long inSharedResourceId)
    {
        sharedResourceId = inSharedResourceId;
    }

}
