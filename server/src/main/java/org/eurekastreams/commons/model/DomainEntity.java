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
package org.eurekastreams.commons.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import net.sf.gilead.pojo.gwt.LightEntity;

/**
 * The parent class for all domain entities. Contains the unique id key and
 * version property.
 */
@MappedSuperclass
public abstract class DomainEntity extends LightEntity
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 123942387L;

    /**
     * Base text used for toString.
     */
    @Transient
    private String toStringBase;

    /**
     * Primary key ID field for ORM.
     *
     * Where you set the @Id on entities tells the ORM if you're using field or
     * property-based entity mapping. if you set it on a private variable, then
     * the ORM will not use getters/setters at all. If you set it on getId(),
     * then you need to have getters/setters on everything.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Version column - used for ORM for optimistic locking.
     */
    @Version
    private long version;

    /**
     * returns the version.
     *
     * @return version
     */
    public long getVersion()
    {
        return version;
    }

    /**
     * sets the version.
     *
     * @param newVersion
     *            version of the entity.
     */
    protected void setVersion(final long newVersion)
    {
        this.version = newVersion;
    }

    /**
     * Gets the id of entity.
     *
     * @return the id of the entity
     */
    public long getId()
    {
        return id;
    }

    /**
     * Sets the id of entity.
     *
     * Only used by persistence mechanism. Not testable because it's protected.
     *
     * @param newId
     *            the id of the entity
     */
    protected void setId(final long newId)
    {
        this.id = newId;
    }

    /**
     * Base toString implementation - class name and ID.
     *
     * @return the string representation of the entity, with
     *         [EntityName]#[Entity ID]
     */
    @Override
    public String toString()
    {
        if (toStringBase == null)
        {
            toStringBase = this.getClass().getName().substring(
                    this.getClass().getName().lastIndexOf('.') + 1)
                    + "#";
        }
        return toStringBase + this.id;
    }
}
