/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.validator.Length;

/**
 * Represents a featured Stream.
 * 
 */
@Entity
public class FeaturedStream extends DomainEntity
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 382234105742156818L;

    /**
     * Used for validation.
     */
    @Transient
    public static final int MAX_DESCRIPTION_LENGTH = 250;

    /**
     * Used for validation.
     */
    @Transient
    public static final String DESCRIPTION_MESSAGE = "Description supports up to " + MAX_DESCRIPTION_LENGTH
            + " characters.";

    /**
     * FeaturedStream creation date.
     */
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    /**
     * Constructor.
     */
    public FeaturedStream()
    {
        // no-op for ORM.
    }

    /**
     * Constructor.
     * 
     * @param inDescription
     *            FeaturedStream description.
     * @param inStreamScope
     *            FeaturedStream StreamScope.
     */
    public FeaturedStream(final String inDescription, final StreamScope inStreamScope)
    {
        description = inDescription;
        streamScope = inStreamScope;
    }

    /**
     * sets the created attribute.
     */
    @PrePersist
    protected void onCreate()
    {
        created = new Date();
    }

    /**
     * Job description for person.
     */
    @Basic(optional = false)
    @Length(max = MAX_DESCRIPTION_LENGTH, message = DESCRIPTION_MESSAGE)
    private String description;

    /**
     * Stream scope representing featured stream.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "streamScopeId")
    private StreamScope streamScope;

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param inDescription
     *            the description to set
     */
    public void setDescription(final String inDescription)
    {
        description = inDescription;
    }

    /**
     * @return the streamScope
     */
    public StreamScope getStreamScope()
    {
        return streamScope;
    }

    /**
     * @param inStreamScope
     *            the streamScope to set
     */
    public void setStreamScope(final StreamScope inStreamScope)
    {
        streamScope = inStreamScope;
    }

    /**
     * @return the created
     */
    public Date getCreated()
    {
        return created;
    }

    /**
     * @param inCreated
     *            the created to set
     */
    public void setCreated(final Date inCreated)
    {
        created = inCreated;
    }

}
