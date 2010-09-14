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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * A stream.
 * 
 */
@Entity
public class Stream extends DomainEntity implements Serializable, StreamFilter
{
    /**
     * The name of the view.
     */
    @Column(nullable = false)
    private Boolean readOnly;

    /**
     * The name of the view.
     */
    @Column(nullable = false)
    @Lob
    private String request;

    /**
     * The name of the view.
     */
    @Column(nullable = false)
    private String name;

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

}
