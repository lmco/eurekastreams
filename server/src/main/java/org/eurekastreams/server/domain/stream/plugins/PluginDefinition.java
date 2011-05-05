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
package org.eurekastreams.server.domain.stream.plugins;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.domain.GalleryItem;
import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.GeneralGadgetDefinition;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.hibernate.annotations.NaturalId;

/**
 * Represents a plugin to the stream.
 * 
 */
@SuppressWarnings("serial")
@Entity
public class PluginDefinition extends DomainEntity implements GalleryItem, Serializable, GeneralGadgetDefinition
{
    /**
     * The web root url - set by configuration.
     */
    private static String webRootUrl;

    /**
     * Object type optionally defined by plugin.
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = true)
    private BaseObjectType objectType;

    /**
     * If the definition is available. Set to false for soft deletes.
     */
    @Basic(optional = false)
    private Boolean showInGallery = true;

    /**
     * The number of users.
     */
    @Basic(optional = true)
    private int numberOfUsers = 0;

    /**
     * Private reference back to the person for mapper queries originating with the theme.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId")
    private Person owner;

    /**
     * UUID associated with the plugin as a string.
     */
    @NaturalId
    private String uuid;

    /**
     * Gets the object type.
     * 
     * @return the object type.
     */
    public BaseObjectType getObjectType()
    {
        return objectType;
    }

    /**
     * sets the object type.
     * 
     * @param inObjectType
     *            The type of the item to post.
     */
    public void setObjectType(final BaseObjectType inObjectType)
    {
        objectType = inObjectType;
    }

    /**
     * Storage for the url that describes the location of the plugin definition.
     */
    @Basic(optional = false)
    private String url;

    /**
     * The gadget category.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pluginCategoryId")
    @Basic(optional = false)
    private GalleryItemCategory category;

    /**
     * The creation date.
     */
    @Basic(optional = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    /*
     * Gadget Metadata
     * 
     * Meta Data contained in gadgets shouldn normally not be stored in the database This data should come from shindig
     * and should be retrieved for the DTO.
     */

    /**
     * Number of minutes we must wait before we can poll the feed again.
     * 
     * This is save in the DB because it is needed by the job that performs the feed updates. It will be updated nightly
     * by the update task for plugins.
     */
    @Basic(optional = false)
    private Long updateFrequency;

    /**
     * sets the created attribute.
     */
    @PrePersist
    protected void onCreate()
    {
        created = new Date();
    }

    /**
     * @return the numberOfUsers
     */
    public int getNumberOfUsers()
    {
        return numberOfUsers;
    }

    /**
     * @param inNumberOfUsers
     *            the numberOfUsers to set
     */
    public void setNumberOfUsers(final int inNumberOfUsers)
    {
        numberOfUsers = inNumberOfUsers;
    }

    /**
     * @return the category
     */
    public GalleryItemCategory getCategory()
    {
        return category;
    }

    /**
     * @param inCategory
     *            the category to set
     */
    public void setCategory(final GalleryItemCategory inCategory)
    {
        this.category = inCategory;
    }

    /**
     * @return the owner
     */
    public Person getOwner()
    {
        return owner;
    }

    /**
     * @param inOwner
     *            the owner to set
     */
    public void setOwner(final Person inOwner)
    {
        this.owner = inOwner;
    }

    /**
     * @return the uuid
     */
    public String getUUID()
    {
        return uuid;
    }

    /**
     * @param inUuid
     *            the uuid to set
     */
    public void setUUID(final String inUuid)
    {
        uuid = inUuid;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        if (PluginDefinition.webRootUrl != null && url != null && !url.contains("://"))
        {
            return PluginDefinition.webRootUrl + "/" + url;
        }
        else
        {
            return url;
        }
    }

    /**
     * @param inUrl
     *            the url to set
     */
    public void setUrl(final String inUrl)
    {
        url = inUrl;
    }

    /**
     * @return the created Date.
     */
    public Date getCreated()
    {
        return created;
    }

    /**
     * @param inCreated
     *            the Created Date to set.
     */
    public void setCreated(final Date inCreated)
    {
        created = inCreated;
    }

    /**
     * @return frequency this should be updated.
     */
    public Long getUpdateFrequency()
    {
        return updateFrequency;
    }

    /**
     * The fequency to runt he updates.
     * 
     * @param inUpdateFrequency
     *            The fequency to runt he updates.
     */
    public void setUpdateFrequency(final Long inUpdateFrequency)
    {
        updateFrequency = inUpdateFrequency;
    }

    /**
     * @return if plugin is active.
     */
    public Boolean getShowInGallery()
    {
        return showInGallery;
    }

    /**
     * @param inShowInGallery
     *            set if plugin is active.
     */
    public void setShowInGallery(final Boolean inShowInGallery)
    {
        showInGallery = inShowInGallery;
    }

    /**
     * @return the webRootUrl
     */
    public static String getWebRootUrl()
    {
        return webRootUrl;
    }

    /**
     * @param inWebRootUrl
     *            the webRootUrl to set
     */
    public static void setWebRootUrl(final String inWebRootUrl)
    {
        webRootUrl = inWebRootUrl;
    }
}
