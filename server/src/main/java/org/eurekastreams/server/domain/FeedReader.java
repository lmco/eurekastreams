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
package org.eurekastreams.server.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eurekastreams.commons.model.DomainEntityIdentifiable;
import org.hibernate.annotations.NaturalId;

//TODO must be brought out into the feed reader project.

/**
 * Represents a Feed Reader Gadget.
 */
@SuppressWarnings("serial")
@Entity
public class FeedReader implements Serializable, DomainEntityIdentifiable
{
    // ///////////////////////////////////////////////////////////////////
    // ATTRIBUTES
    // ///////////////////////////////////////////////////////////////////

    /**
     * The unique id of this FeedReader.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The date the user was added into the system.
     */
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded = new Date();

    /**
     * The url of the feed.
     */
    @Column(nullable = false)
    private String url;

    /**
     * The url of the feed.
     */
    @Column(nullable = false)
    private String feedTitle;

    /**
     * The instance Id of the Feed gadget.
     */
    @NaturalId
    private String moduleId;

    /**
     * The OpenSocial Id of the person.
     */
    @NaturalId
    private String openSocialId;

    /**
     * Public constructor for ORM and ResourcePersistenceStrategy.
     */
    public FeedReader()
    {
        // needs a public constructor, This one is empty because we don't need it to do anything.
    }

    /**
     * Public constructor for API.
     * 
     * @param inModuleId
     *            The application id for the instance of the gadget.
     * @param inOpenSocialId
     *            The Users unique ID.
     */
    public FeedReader(final String inModuleId, final String inOpenSocialId)
    {
        this.moduleId = inModuleId.toLowerCase();
        this.openSocialId = inOpenSocialId;
    }

    /**
     * @return the dateAdded
     */
    public Date getDateAdded()
    {
        return dateAdded;
    }

    /**
     * @param inDateAdded
     *            the dateAdded to set.
     */
    public void setDateAdded(final Date inDateAdded)
    {
        this.dateAdded = inDateAdded;
    }

    /**
     * @return the uId
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param inId
     *            the uId to set.
     */
    public void setId(final long inId)
    {
        id = inId;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param inUrl
     *            the url to set.
     */
    public void setUrl(final String inUrl)
    {
        this.url = inUrl;
    }

    /**
     * @return the moduleId
     */
    public String getModuleId()
    {
        return moduleId;
    }

    /**
     * @param inModuleId
     *            the moduleId to set.
     */
    public void setModuleId(final String inModuleId)
    {
        this.moduleId = inModuleId;
    }

    /**
     * @return the userId
     */
    public String getOpenSocialId()
    {
        return openSocialId;
    }

    /**
     * @param inOpenSocialId
     *            the os id to set.
     */
    public void setOpenSocialId(final String inOpenSocialId)
    {
        this.openSocialId = inOpenSocialId;
    }

    /**
     * @return the feedTitle
     */
    public String getFeedTitle()
    {
        return feedTitle;
    }

    /**
     * @param inFeedTitle
     *            the feedTitle to set.
     */
    public void setFeedTitle(final String inFeedTitle)
    {
        feedTitle = inFeedTitle;
    }

}
