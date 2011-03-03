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
package org.eurekastreams.server.action.request.profile;

import java.io.Serializable;

/**
 * Request class for the {@link DomainGroupCacheUpdaterAsyncExecution}.
 * 
 */
public class DomainGroupCacheUpdaterRequest implements Serializable
{
    /**
     * Serialization version id.
     */
    private static final long serialVersionUID = -4780527099952462926L;

    /**
     * Id of the group context for the request.
     */
    private Long domainGroupId;

    /**
     * Whether or not the request context was called as an update.
     */
    private boolean update;

    /**
     * For serialization only.
     */
    @SuppressWarnings("unused")
    private DomainGroupCacheUpdaterRequest()
    {
        // no op.
    }

    /**
     * Constructor.
     * 
     * @param inDomainGroupId
     *            - value of the group id to use for this request.
     * @param inUpdate
     *            - value of the update flag to indicate whether or not the action should be performed in the context of
     *            an update.
     */
    public DomainGroupCacheUpdaterRequest(final Long inDomainGroupId, final boolean inUpdate)
    {
        domainGroupId = inDomainGroupId;
        update = inUpdate;
    }

    /**
     * Retrieve the group id for this request.
     * 
     * @return - long value of the group id.
     */
    public Long getDomainGroupId()
    {
        return domainGroupId;
    }

    /**
     * Set the group id for this request.
     * 
     * @param inDomainGroupId
     *            the group id
     */
    public void setDomainGroupId(final Long inDomainGroupId)
    {
        this.domainGroupId = inDomainGroupId;
    }

    /**
     * Boolean indicating whether or not this request is in the context of an update or not.
     * 
     * @return - boolean value to indicate update context.
     */
    public boolean getIsUpdate()
    {
        return update;
    }

    /**
     * Indicating whether or not this request is in the context of an update or not.
     * 
     * @param inUpdate
     *            boolean value to indicate update context.
     */
    public void setIsUpdate(final boolean inUpdate)
    {
        this.update = inUpdate;
    }
}
