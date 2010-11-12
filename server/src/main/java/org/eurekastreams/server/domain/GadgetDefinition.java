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
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.commons.search.analysis.TextStemmerAnalyzer;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;

/**
 * This class represents the Gadget domain object.
 *
 */
@SuppressWarnings("serial")
@Entity
@Indexed
public class GadgetDefinition extends DomainEntity implements Serializable, GalleryItem, GeneralGadgetDefinition
{
    /**
     * Storage for the url that describes the location of the gadget definition.
     */
    @Basic(optional = false)
    private String url;

    /**
     * Private reference back to appData for cascading of deletes.
     */
    @SuppressWarnings("unused")
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "gadgetDefinitionId")
    private List<AppData> appData;

    /**
     * The gadget tasks.
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "gadgetDefinitionId")
    @Cascade({ org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private List<Task> tasks;

    /**
     * UUID associated with the theme as a string.
     */
    @NaturalId
    private String uuid;

    /**
     * The gadget category.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gadgetCategoryId")
    @Basic(optional = false)
    private GalleryItemCategory category;

    /**
     * The actual instantiations of this gadget def.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "gadgetDefinitionId")
    @Where(clause = "deleted='false'")
    private List<Gadget> gadgets;

    /**
     * The creation date.
     */
    @Basic(optional = false)
    @Field(name = "created", index = Index.UN_TOKENIZED, store = Store.NO)
    @Temporal(TemporalType.TIMESTAMP)
    @DateBridge(resolution = Resolution.SECOND)
    private Date created;

    /**
     * Whether to show this Gadget in the gallery.
     */
    @Basic
    private Boolean showInGallery = true;

    /**
     * The number of users.
     */
    @Basic
    @Field(name = "numberOfUsers", index = Index.UN_TOKENIZED, store = Store.YES)
    private Integer numberOfUsers = 0;

    /**
     * Private reference back to the person for mapper queries originating with the theme.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId")
    private Person owner;


    /*
     * Gadget Metadata
     *
     * Meta Data contained in gadgets shouldn normally not be stored in the database
     * This data should come from shindig.
     *
     */

    /**
     * The gadget title.
     *
     * This is not transient because we need it to set a gadgets title.
     */
    @Column(nullable = true)
    @Field(name = "title", index = Index.TOKENIZED,
    //
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class), store = Store.NO)
    private String gadgetTitle = "";

    /**
     * The gadget description.
     *
     * This appears to be in here for search purposes.
     */
    @Transient
    @Field(name = "description", index = Index.TOKENIZED,
    //
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class), store = Store.NO)
    private String gadgetDescription = "";

    /**
     * The gadget author.
     *
     * This appears to be in here for search purposes.
     */
    @Transient
    @Field(name = "author", index = Index.TOKENIZED,
    //
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class), store = Store.NO)
    private String gadgetAuthor = "";

    /**
     * Empty Constructor.
     */
    public GadgetDefinition()
    {
        // Currently no implementation need for a default constructor
        // but this is needed because an overloaded constructor
        // was provided.
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
     * Constructor with passing in the url to find the gadget definition.
     *
     * @param inUrl
     *            - url of the gadget definition.
     * @param inUuid
     *            - location of the gadget definition.
     * @param inCategory
     *            - category of the gadget definition.
     */
    public GadgetDefinition(final String inUrl, final String inUuid, final GalleryItemCategory inCategory)
    {
        this.url = inUrl;
        setUUID(inUuid);
        this.category = inCategory;
    }

    /**
     * Constructor with passing in the url to find the gadget definition.
     *
     * @param inUrl
     *            - url of the gadget definition.
     * @param inUuid
     *            - location of the gadget definition.
     */
    public GadgetDefinition(final String inUrl, final String inUuid)
    {
        this.url = inUrl;
        setUUID(inUuid);
    }

    /**
     * This method returns the url of the gadget definition.
     *
     * @return Returns the url of the gadget definition.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Getter for the theme's UUID.
     *
     * @return the UUID of the theme.
     */
    public String getUUID()
    {
        return uuid;
    }

    /**
     * Needed for serialization.
     *
     * @param inUUID
     *            UUID to use.
     */
    public void setUUID(final String inUUID)
    {
        this.uuid = inUUID;
    }

    /**
     * Getter for the theme's Category.
     *
     * @return the Category of the theme.
     */
    public GalleryItemCategory getCategory()
    {
        return category;
    }

    /**
     * Needed for serialization.
     *
     * @param inCategory
     *            Category to use.
     */
    public void setCategory(final GalleryItemCategory inCategory)
    {
        this.category = inCategory;
    }

    /**
     * Needed for serialization.
     *
     * @param inUrl
     *            The URL of the gadget def.
     */
    public void setUrl(final String inUrl)
    {
        this.url = inUrl;
    }

    /**
     * Returns the tasks for a gadget.
     *
     * @return the tasks.
     */
    public List<Task> getTasks()
    {
        return tasks;
    }

    /**
     * Private setting for serialization purposes.
     *
     * @param inTasks
     *            the tasks.
     */
    @SuppressWarnings("unused")
    private void setTasks(final List<Task> inTasks)
    {
        tasks = inTasks;
    }

    /**
     * @param inGadgets
     *            the gadgets to set
     */
    public void setGadgets(final List<Gadget> inGadgets)
    {
        gadgets = inGadgets;
    }

    /**
     * @return the gadgets
     */
    public List<Gadget> getGadgets()
    {
        return gadgets;
    }

    /**
     * @param inNumberOfUsers
     *            the NumberOfUsers to set
     */
    public void setNumberOfUsers(final Integer inNumberOfUsers)
    {
        numberOfUsers = inNumberOfUsers;
    }

    /**
     * @return the NumberOfUsers
     */
    public int getNumberOfUsers()
    {
        return numberOfUsers;
    }

    /**
     * @param inOwner
     *            the banner to set
     */
    public void setOwner(final Person inOwner)
    {
        owner = inOwner;
    }

    /**
     * @return the owner
     */
    public Person getOwner()
    {
        return owner;
    }

    /**
     * @return the showInGallery
     */
    public Boolean getShowInGallery()
    {
        return showInGallery;
    }

    /**
     * @param inShowInGallery
     *            the showInGallery to set
     */
    public void setShowInGallery(final Boolean inShowInGallery)
    {
        this.showInGallery = inShowInGallery;
    }

    /**
     * @param inGadgetTitle
     *            the gadgetTitle to set
     */
    public void setGadgetTitle(final String inGadgetTitle)
    {
        this.gadgetTitle = inGadgetTitle;
    }

    /**
     * @return the gadgetTitle
     */
    public String getGadgetTitle()
    {
        return gadgetTitle;
    }

    /**
     * @param inGadgetDescription
     *            the gadgetDesciption to set
     */
    public void setGadgetDescription(final String inGadgetDescription)
    {
        this.gadgetDescription = inGadgetDescription;
    }

    /**
     * @return the gadgetDesciption
     */
    public String getGadgetDescription()
    {
        return gadgetDescription;
    }

    /**
     * @param inGadgetAuthor
     *            the gadgetAuthor to set
     */
    public void setGadgetAuthor(final String inGadgetAuthor)
    {
        this.gadgetAuthor = inGadgetAuthor;
    }

    /**
     * @return the gadgetAuthor
     */
    public String getGadgetAuthor()
    {
        return gadgetAuthor;
    }

    /**
     * @param inCreated
     *            the Created Date to set
     */
    public void setCreated(final Date inCreated)
    {
        created = inCreated;
    }

    /**
     * @return the Created Date
     */
    public Date getCreated()
    {
        return created;
    }


    /**
     * Sets the id.
     * @param inId the id.
     */
    @Override
    public void setId(final long inId)
    {
        super.setId(inId);
    }

}
