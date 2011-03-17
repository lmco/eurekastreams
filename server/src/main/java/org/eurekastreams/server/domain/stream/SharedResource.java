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

package org.eurekastreams.server.domain.stream;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.domain.Person;
import org.hibernate.validator.Length;

/**
 * Entity representing a resource that people can like and share.
 */
@Entity
public class SharedResource extends DomainEntity implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -6240583797748449044L;

    /**
     * Used for validation.
     */
    @Transient
    public static final int MAX_UNIQUE_KEY_LENGTH = 2000;

    /**
     * The type of resource.
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private BaseObjectType resourceType;

    /**
     * Case-insensitive unique key for this resource.
     */
    @Basic(optional = false)
    @Length(min = 1, max = MAX_UNIQUE_KEY_LENGTH, // 
    message = "A resource identifier must be no more than " + MAX_UNIQUE_KEY_LENGTH + " characters")
    private String uniqueKey;

    /**
     * List of people that liked this - used only for querying - do not load this list.
     */
    @Basic(optional = false)
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
    @JoinTable(name = "Person_LikedSharedResources",
    // join columns
    joinColumns = { @JoinColumn(table = "Person", name = "personId") },
    // inverse join columns
    inverseJoinColumns = { @JoinColumn(table = "SharedResource", name = "sharedResourceId") },
    // unique constraints
    uniqueConstraints = { @UniqueConstraint(columnNames = { "personId", "sharedResourceId" }) })
    private List<Person> likedBy;

    /**
     * Constructor.
     */
    public SharedResource()
    {
    }

    /**
     * Constructor.
     * 
     * @param inResourceType
     *            the type of resource
     * @param inUniqueKey
     *            the unique key
     */
    public SharedResource(final BaseObjectType inResourceType, final String inUniqueKey)
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

    /**
     * @return the likedBy
     */
    public List<Person> getLikedBy()
    {
        return likedBy;
    }

    /**
     * @param inLikedBy
     *            the likedBy to set
     */
    public void setLikedBy(final List<Person> inLikedBy)
    {
        likedBy = inLikedBy;
    }

}
