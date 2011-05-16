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
package org.eurekastreams.server.domain.stream;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.Length;

/**
 * Scope of an activity/message stream.
 */
@Entity
public class StreamScope extends DomainEntity implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -8038373583543778383L;

    /**
     * Max unique key lenght.
     */
    private static final int MAX_UNIQUEKEY_LENGTH = 2000;

    /**
     * Display name for the scope.
     */
    @Transient
    private String displayName;

    /**
     * The scope type.
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @NaturalId
    private ScopeType scopeType;

    /**
     * The unique key for the scope.
     */
    @Basic(optional = false)
    @NaturalId
    @Length(min = 1, max = MAX_UNIQUEKEY_LENGTH)
    private String uniqueKey;

    /**
     * The destination (group or person) entity's id.
     */
    @Basic(optional = true)
    private Long destinationEntityId;

    /**
     * Empty constructor for serialization.
     */
    public StreamScope()
    {
        // no-op
    }

    /**
     * Constructor.
     * 
     * @param inScopeType
     *            the scope type
     * @param inUniqueKey
     *            the unique key
     */
    // TODO: This constructor should go away after memcached refactor.
    public StreamScope(final ScopeType inScopeType, final String inUniqueKey)
    {
        scopeType = inScopeType;
        setUniqueKey(inUniqueKey);
    }

    /**
     * Constructor.
     * 
     * @param inScopeType
     *            the scope type
     * @param inUniqueKey
     *            the unique key
     * @param inStreamScopeId
     *            the id.
     */
    public StreamScope(final ScopeType inScopeType, final String inUniqueKey, final long inStreamScopeId)
    {
        scopeType = inScopeType;
        setUniqueKey(inUniqueKey);
        setId(inStreamScopeId);
    }

    /**
     * Constructor.
     * 
     * @param inDisplayName
     *            the display name for the scope.
     * @param inScopeType
     *            the scope type
     * @param inUniqueKey
     *            the unique key
     * @param inStreamScopeId
     *            the id.
     */
    public StreamScope(final String inDisplayName, final ScopeType inScopeType, final String inUniqueKey,
            final long inStreamScopeId)
    {
        this(inScopeType, inUniqueKey, inStreamScopeId);
        displayName = inDisplayName;
    }

    /**
     * Constructor.
     * 
     * @param inDisplayName
     *            the display name for the scope.
     * @param inScopeType
     *            the scope type
     * @param inUniqueKey
     *            the unique key
     * @param inStreamScopeId
     *            the id.
     * @param inDestinationEntityId
     *            the id of the person or group that is the recipient of this stream
     */
    public StreamScope(final String inDisplayName, final ScopeType inScopeType, final String inUniqueKey,
            final long inStreamScopeId, final long inDestinationEntityId)
    {
        this(inScopeType, inUniqueKey, inStreamScopeId);
        setDestinationEntityId(inDestinationEntityId);
        displayName = inDisplayName;
    }

    /**
     * Set the scope.
     * 
     * @param inScopeType
     *            the scope to set
     */
    public void setScopeType(final ScopeType inScopeType)
    {
        this.scopeType = inScopeType;
    }

    /**
     * Get the scope.
     * 
     * @return the scope
     */
    public ScopeType getScopeType()
    {
        return scopeType;
    }

    /**
     * Set the unique key.
     * 
     * @param inUniqueKey
     *            the uniqueKey to set
     */
    public void setUniqueKey(final String inUniqueKey)
    {
        this.uniqueKey = inUniqueKey == null ? "" : inUniqueKey.toLowerCase();
    }

    /**
     * Get the unique key.
     * 
     * @return the uniqueKey
     */
    public String getUniqueKey()
    {
        return uniqueKey;
    }

    /**
     * An enum describing the scope of a stream view.
     */
    public enum ScopeType
    {
        /**
         * Scoped for all.
         */
        ALL,

        /**
         * Scoped to a specific person.
         */
        PERSON,

        /**
         * Scoped to a specific group.
         */
        GROUP,

        /**
         * Scoped to all streams followed by a person - key refers to the person's unique key.
         */
        PERSONS_FOLLOWED_STREAMS,

        /**
         * Scoped to starred activities for current user.
         */
        STARRED,

        /**
         * Represents a resource.
         */
        RESOURCE
    }

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
     * Equals method comparing object ids.
     * 
     * @param obj
     *            The object to compare to this one.
     * @return true if objects are equal, false otherwise.
     */
    @Override
    public boolean equals(final Object obj)
    {
        return (obj instanceof StreamScope) ? (((StreamScope) obj).getId() == this.getId()) : false;

    }

    /**
     * Needed for checkstyle.
     * 
     * @return the hashcode.
     */
    @Override
    public int hashCode()
    {
        return (new Long(this.getId())).hashCode();
    }

    /**
     * Get the destination (group or person) entity's id.
     * 
     * @return the destination (group or person) entity's id.
     */
    public Long getDestinationEntityId()
    {
        return destinationEntityId;
    }

    /**
     * Set the destination (group or person) entity's id.
     * 
     * @param inDestinationEntityId
     *            the destination (group or person) entity's id.
     */
    public void setDestinationEntityId(final Long inDestinationEntityId)
    {
        this.destinationEntityId = inDestinationEntityId;
    }
}
