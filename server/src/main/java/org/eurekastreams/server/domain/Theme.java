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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.commons.search.analysis.TextStemmerAnalyzer;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

/**
 * Represents a theme.
 * 
 */
@SuppressWarnings("serial")
@Entity
@Indexed
public class Theme extends DomainEntity implements Serializable, GalleryItem
{
    /**
     * Max characters for description.
     */
    @Transient
    private final int maxDescriptionLength = 200;

    /**
     * The web root url - set by configuration.
     */
    private static String webRootUrl;

    /**
     * The unique (case-insensitive) URL to the theme XML file.
     */
    @Basic(optional = false)
    private String themeUrl;

    /**
     * Used for validation.
     */
    @Transient
    public static final String EMAIL_MESSAGE = "email address must be in a valid format";

    /**
     * Store the value of the desc.
     */
    @Basic(optional = false)
    @Length(min = 1, max = maxDescriptionLength, message = "Description supports up to " + maxDescriptionLength
            + " characters.")
    @Field(name = "description", index = Index.TOKENIZED,
    //
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class), store = Store.NO)
    private String description;

    /**
     * The unique name of this theme.
     */
    @Basic(optional = false)
    @Field(name = "name", index = Index.TOKENIZED,
    //
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class), store = Store.NO)
    private String name;

    /**
     * The URL of the generated CSS file.
     */
    @Basic(optional = false)
    private String cssFile;

    /**
     * UUID associated with the theme as a string.
     */
    @Column(nullable = false, unique = true)
    private String uuid;

    /**
     * The timestamp for when the CSS was last generated.
     */
    @SuppressWarnings("unused")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateGenerated;

    /**
     * The actual instantiations of this gadget def.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
    @JoinColumn(name = "themeId")
    private List<Person> users;

    /**
     * The number of users.
     */
    @Basic(optional = true)
    @Field(name = "numberOfUsers", index = Index.UN_TOKENIZED, store = Store.YES)
    private int numberOfUsers = 0;

    /**
     * The creation date.
     */
    @Basic(optional = false)
    @Field(name = "created", index = Index.UN_TOKENIZED, store = Store.NO)
    @Temporal(TemporalType.TIMESTAMP)
    @DateBridge(resolution = Resolution.SECOND)
    private Date created;

    /**
     * sets the created attribute.
     */
    @PrePersist
    protected void onCreate()
    {
        created = new Date();
    }

    /**
     * The gadget category.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "themeCategoryId")
    @Basic(optional = false)
    private GalleryItemCategory category;

    /**
     * banner id for this theme.
     */
    @Basic(optional = false)
    private String bannerId;

    /**
     * the author of this theme.
     */
    @Basic(optional = false)
    @Field(name = "author", index = Index.TOKENIZED,
    //
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class), store = Store.NO)
    private String authorName;

    /**
     * the email of the theme author.
     */
    @Basic(optional = false)
    @NotEmpty
    @Email(message = EMAIL_MESSAGE)
    private String authorEmail;

    /**
     * Private reference back to the person for mapper queries originating with the theme.
     */
    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId")
    private Person owner;

    /**
     * Empty Constructor.
     */
    public Theme()
    {
    }

    /**
     * Public constructor for API.
     * 
     * @param inThemeUrl
     *            the url of the theme.
     * @param inThemeName
     *            the name of the theme.
     * @param inThemeDesc
     *            the description of the theme.
     * @param inCss
     *            the generated CSS file.
     * @param inUUID
     *            the UUID.
     * @param inBannerId
     *            the banner id.
     * @param inAuthorName
     *            the name of the theme author.
     * @param inAuthorEmail
     *            the email address of the theme author.
     */
    public Theme(final String inThemeUrl, final String inThemeName, final String inThemeDesc, final String inCss,
            final String inUUID, final String inBannerId, final String inAuthorName, final String inAuthorEmail)
    {
        setUrl(inThemeUrl);
        setName(inThemeName);
        setDescription(inThemeDesc);
        setCssFile(inCss);
        setUUID(inUUID);
        setBannerId(inBannerId);
        authorName = inAuthorName;
        authorEmail = inAuthorEmail;
    }

    /**
     * The industry description.
     * 
     * @param inDescription
     *            the description to set
     */
    public void setDescription(final String inDescription)
    {
        this.description = inDescription;
    }

    /**
     * Get the description.
     * 
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Needed for serialization.
     * 
     * @param inUrl
     *            The URL to the theme.
     */
    public void setUrl(final String inUrl)
    {
        this.themeUrl = inUrl;
    }

    /**
     * Getter for the theme's XML File's URL.
     * 
     * @return the url to the theme.
     */
    public String getUrl()
    {
        if (Theme.webRootUrl != null && themeUrl != null && !themeUrl.contains("://"))
        {
            return Theme.webRootUrl + "/" + themeUrl;
        }
        else
        {
            return themeUrl;
        }
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
     * @param inThemeName
     *            The name of the theme.
     */
    public void setName(final String inThemeName)
    {
        this.name = inThemeName;
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
     * Getter for the the Theme name.
     * 
     * @return the name of the theme.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Needed for serialization.
     * 
     * @param inCssFile
     *            The CSS file of the theme.
     */
    public void setCssFile(final String inCssFile)
    {
        this.cssFile = inCssFile;
    }

    /**
     * Getter for the theme's css file's location.
     * 
     * @return the css file's location.
     */
    public String getCssFile()
    {
        return cssFile;
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
    @SuppressWarnings("unused")
    public void setCategory(final GalleryItemCategory inCategory)
    {
        this.category = inCategory;
    }

    /**
     * @return the users
     */
    public List<Person> getUsers()
    {
        return users;
    }

    /**
     * @param inNumberOfUsers
     *            the NumberOfUsers to set
     */
    public void setNumberOfUsers(final int inNumberOfUsers)
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
     * @param inCreatedDate
     *            the CreatedDate to set
     */
    public void setCreatedDate(final Date inCreatedDate)
    {
        created = inCreatedDate;
    }

    /**
     * @return the CreatedDate
     */
    public Date getCreatedDate()
    {
        return created;
    }

    /**
     * @return the banner Id
     */
    public String getBannerId()
    {
        return bannerId;
    }

    /**
     * @param inBannerId
     *            the banner to set
     */
    public void setBannerId(final String inBannerId)
    {
        bannerId = inBannerId;
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
        owner = inOwner;
    }

    /**
     * Getter for author's name address.
     * 
     * @return author's name address.
     */
    public String getAuthorName()
    {
        return authorName;
    }

    /**
     * Setter for author's name address.
     * 
     * @param inAuthorName
     *            author's name address.
     */
    public void setAuthorName(final String inAuthorName)
    {
        authorName = inAuthorName;
    }

    /**
     * Getter for author's email address.
     * 
     * @return author's email address.
     */
    public String getAuthorEmail()
    {
        return authorEmail;
    }

    /**
     * Setter for author's email address.
     * 
     * @param inAuthorEmail
     *            author's email address.
     */
    public void setAuthorEmail(final String inAuthorEmail)
    {
        authorEmail = inAuthorEmail;
    }

    /**
     * Show in gallery?
     * 
     * @return show in gallery.
     */
    @Override
    public Boolean getShowInGallery()
    {
        return true;
    }

    /**
     * Set show in gallery.
     * 
     * @param inShowInGallery
     *            show in gallery.
     */
    @Override
    public void setShowInGallery(final Boolean inShowInGallery)
    {
        // Nothing to do here for themes.
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
