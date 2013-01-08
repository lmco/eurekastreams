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
package org.eurekastreams.server.search.modelview;

import java.io.Serializable;

import org.eurekastreams.server.domain.EntityType;

/**
 * ModelView for Displayed Entities.
 * 
 */
public class DisplayEntityModelView implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 8218491908194565832L;

    /**
     * Display name of entity.
     */
    private String displayName;

    /**
     * Type of Entity.
     */
    private EntityType type;

    /**
     * Id for Entity (accountId or shortname).
     */
    private String uniqueKey;

    /**
     * StreamScopeId for DisplayEntity.
     */
    private Long streamScopeId;

    /**
     * Whether the User's Account is Locked.
     */
    private boolean accountLocked;

    /**
     * @return the displayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * @param inDisplayName
     *            the displayName to set
     */
    public void setDisplayName(final String inDisplayName)
    {
        this.displayName = inDisplayName;
    }

    /**
     * @return the type
     */
    public EntityType getType()
    {
        return type;
    }

    /**
     * @param inType
     *            the type to set
     */
    public void setType(final EntityType inType)
    {
        this.type = inType;
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
        this.uniqueKey = inUniqueKey;
    }

    /**
     * @return the streamScopeId
     */
    public Long getStreamScopeId()
    {
        return streamScopeId;
    }

    /**
     * @param inStreamScopeId
     *            the streamScopeId to set
     */
    public void setStreamScopeId(final Long inStreamScopeId)
    {
        this.streamScopeId = inStreamScopeId;
    }

    /**
     * @return whether the account is locked or not
     */
    public boolean isAccountLocked()
    {
        return accountLocked;
    }

    /**
     * @param inAccountLocked
     *            boolean to set whether account is locked or not
     */
    public void setAccountLocked(final boolean inAccountLocked)
    {
        accountLocked = inAccountLocked;
    }
}
