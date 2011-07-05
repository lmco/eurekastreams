/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.validator.Length;

/**
 * A stream.
 * 
 */
@Entity
public class Stream extends DomainEntity implements Serializable, StreamFilter
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 7278197745679222759L;

    /** Used for validation. */
    public static final int MAX_NAME_LENGTH = 50;

    /** Used for validation. */
    public static final String NAME_LENGTH_MESSAGE = "Name may not be longer than " + MAX_NAME_LENGTH + " characters.";

    /**
     * The name of the view.
     */
    @Column(nullable = false)
    private Boolean readOnly;

    /**
     * The request.
     */
    @Column(nullable = false)
    @Lob
    private String request;

    /**
     * The name of the view.
     */
    @Basic(optional = false)
    @Length(min = 1, max = MAX_NAME_LENGTH, message = NAME_LENGTH_MESSAGE)
    private String name;

    /**
     * Owner entity ID.
     */
    @Transient
    private Long ownerEntityId;

    /**
     * Owner avatar ID.
     */
    @Transient
    private String ownerAvatarId;

    /**
     * Sets the name of the view.
     * 
     * @param inName
     *            the name.
     */
    public void setName(final String inName)
    {
        this.name = inName;
    }

    /**
     * Gets the name.
     * 
     * @return the name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the name of the view.
     * 
     * @param inRequest
     *            the name.
     */
    public void setRequest(final String inRequest)
    {
        this.request = inRequest;
    }

    /**
     * Gets the name.
     * 
     * @return the name.
     */
    public String getRequest()
    {
        return this.request;
    }

    /**
     * Sets the name of the view.
     * 
     * @param inReadOnly
     *            the name.
     */
    public void setReadOnly(final Boolean inReadOnly)
    {
        this.readOnly = inReadOnly;
    }

    /**
     * Gets the name.
     * 
     * @return the name.
     */
    public Boolean getReadOnly()
    {
        return this.readOnly;
    }

    /**
     * Dont really need this.
     * 
     * @param inId
     *            id.
     */
    @Override
    public void setId(final long inId)
    {
        super.setId(inId);
    }

    /**
     * Get the owner avatar ID.
     * 
     * @return the owner avatar ID.
     */
    public String getOwnerAvatarId()
    {
        return ownerAvatarId;
    }

    /**
     * Get the owner entity ID.
     * 
     * @return the owner entity ID.
     */
    public Long getOwnerEntityId()
    {
        return ownerEntityId;
    }

    /**
     * Set the owner avatar id.
     * 
     * @param inOwnerAvatarId
     *            the owner avatar id.
     */
    public void setOwnerAvatarId(final String inOwnerAvatarId)
    {
        ownerAvatarId = inOwnerAvatarId;
    }

    /**
     * Set the owner entity id.
     * 
     * @param inEntityId
     *            the owner entity id.
     */
    public void setOwnerEntityId(final long inEntityId)
    {
        ownerEntityId = inEntityId;
    }

}
