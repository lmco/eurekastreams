/*
 * Copyright (c) 2009-2013 Lockheed Martin Corporation
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.commons.search.analysis.HtmlStemmerAnalyzer;
import org.eurekastreams.commons.search.analysis.TextStemmerAnalyzer;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.search.bridge.BackgroundStringBridge;
import org.eurekastreams.server.search.bridge.IsPersonVisibleInSearchClassBridge;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OrderBy;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;

/**
 * Represents a person using the system, owning a start page.
 */
@Entity
@Indexed
@ClassBridge(name = "isVisibleInSearch", index = Index.UN_TOKENIZED, store = Store.NO, // \n
impl = IsPersonVisibleInSearchClassBridge.class)
public class Person extends DomainEntity implements Serializable, AvatarEntity, Followable, HasEmail, Bannerable,
        Identifiable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -2941774908815671062L;

    /**
     * used for validation.
     */
    @Transient
    public static final int MAX_FIRST_NAME_LENGTH = 50;

    /**
     * Used for validation.
     */
    @Transient
    public static final int MAX_LAST_NAME_LENGTH = 50;

    /**
     * used for validation.
     */
    @Transient
    public static final int MAX_TITLE_LENGTH = 150;

    /**
     * Used for validation.
     */
    @Transient
    public static final int MAX_JOB_DESCRIPTION_LENGTH = 250;

    /**
     * Used for validation.
     */
    @Transient
    public static final int MAX_PHONE_NUMBER_LENGTH = 250;

    /**
     * The number of tabs each person is limited to ont he start page.
     */
    @Transient
    public static final int TAB_LIMIT = 6;

    /**
     * Message to return when limit is reached.
     */
    @Transient
    public static final String TAB_LIMIT_MESSAGE = "Start page tabs are limited to " + TAB_LIMIT + ".";

    /**
     * Used for validation.
     */
    @Transient
    public static final String PHONE_NUMBER_MESSAGE = "Phone numbers can be no more than " + MAX_PHONE_NUMBER_LENGTH
            + " characters.";

    /**
     * Used for validation.
     */
    @Transient
    public static final String FAX_NUMBER_MESSAGE = "Fax number can be no more than " + MAX_PHONE_NUMBER_LENGTH
            + " characters.";

    /**
     * Used for validation.
     */
    @Transient
    public static final String JOB_DESCRIPTION_MESSAGE = "Job Description supports up to " + MAX_JOB_DESCRIPTION_LENGTH
            + " characters.";

    /**
     * Used for validation.
     */
    @Transient
    public static final String TITLE_MESSAGE = "Title supports up to " + MAX_TITLE_LENGTH + " characters.";

    /**
     * Used for validation.
     */
    @Transient
    public static final String FIRST_NAME_MESSAGE = "First name must be between 1 and " + MAX_FIRST_NAME_LENGTH
            + " characters.";

    /**
     * Used for validation.
     */
    @Transient
    public static final String MIDDLE_NAME_MESSAGE = "Middle name must be between 1 and " + MAX_LAST_NAME_LENGTH
            + " characters.";

    /**
     * Used for validation.
     */
    @Transient
    public static final String COMPANY_NAME_MESSAGE = "Company name must be between 1 and " + MAX_LAST_NAME_LENGTH
            + " characters.";

    /**
     * Used for validation.
     */
    @Transient
    public static final String LAST_NAME_MESSAGE = "Last name must be between 1 and " + MAX_LAST_NAME_LENGTH
            + " characters.";

    /**
     * Used for validation.
     */
    @Transient
    public static final String EMAIL_MESSAGE = "Please enter a properly formatted email address.";

    /**
     * Used for validation.
     */
    @Transient
    private static final int MAX_OVERVIEW_LENGTH = 10000;

    /**
     * Used for validation.
     */
    @Transient
    public static final String OVERVIEW_MESSAGE = "Overview can be no more than " + MAX_OVERVIEW_LENGTH
            + " characters.";

    /**
     * Transient 'isPublic' field used only for searching - helps speed up certain queries that contain
     * permission-scoped entities.
     */
    @Transient
    @Field(name = "isPublic", index = Index.UN_TOKENIZED, store = Store.NO)
    @SuppressWarnings("unused")
    private final boolean publicGroup = true;

    /**
     * Transient field used only for displaying a banner on profile pages. This is needed so that a common strategy can
     * be used across groups, orgs, and people to display banners. When profiles support DTO's, this can be moved there.
     */
    @Transient
    private String bannerId = null;

    /**
     * Transient field used only for displaying a banner on profile pages. This is needed so that a common strategy can
     * be used across groups, orgs, and people to display banners. When profiles support DTO's, this can be moved there.
     */
    @Transient
    private Long bannerEntityId = null;

    /**
     * List of Streams for this person.
     */
    @OrderBy(clause = "name")
    // Don't cascade on delete
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(name = "Person_Stream",
    // join columns
    joinColumns = { @JoinColumn(table = "Person", name = "personId") },
    // inverse join columns
    inverseJoinColumns = { @JoinColumn(table = "Stream", name = "streamId") },
    // unique constraints
    uniqueConstraints = { @UniqueConstraint(columnNames = { "personId", "streamId" }) })
    private List<Stream> streams;

    /**
     * List of Bookmarks for this person.
     */
    @OrderBy(clause = "scopeId")
    // Don't cascade on delete
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(name = "Person_Bookmark",
    // join columns
    joinColumns = { @JoinColumn(table = "Person", name = "personId") },
    // inverse join columns
    inverseJoinColumns = { @JoinColumn(table = "StreamScope", name = "scopeId") },
    // unique constraints
    uniqueConstraints = { @UniqueConstraint(columnNames = { "personId", "scopeId" }) })
    private List<StreamScope> bookmarks;

    // ///////////////////////////////////////////////////////////////////
    // ATTRIBUTES
    // ///////////////////////////////////////////////////////////////////

    /**
     * The date the user was added into the system, defaults to the current time, indexed into search engine. Note, for
     * the date to be sortable, it needs to be either Index.UN_TOKENIZED or Index.NO_NORMS.
     */
    @Column(nullable = false)
    @Field(name = "dateAdded", index = Index.UN_TOKENIZED, store = Store.NO)
    @Temporal(TemporalType.TIMESTAMP)
    @DateBridge(resolution = Resolution.SECOND)
    private Date dateAdded = new Date();

    /**
     * The unique (case-insensitive) account id for this Person.
     */
    @Column(nullable = false, unique = true)
    @Field(name = "accountId", index = Index.UN_TOKENIZED, store = Store.NO)
    private String accountId;

    /**
     * The first name of this Person.
     */
    @Basic(optional = false)
    @Length(min = 1, max = MAX_FIRST_NAME_LENGTH, message = FIRST_NAME_MESSAGE)
    private String firstName;

    /**
     * The middle name of this Person.
     */
    @Length(min = 1, max = MAX_FIRST_NAME_LENGTH, message = MIDDLE_NAME_MESSAGE)
    private String middleName;

    /**
     * The last name of this Person.
     */
    @Basic(optional = false)
    @Length(min = 1, max = MAX_FIRST_NAME_LENGTH, message = LAST_NAME_MESSAGE)
    @Field(name = "lastName", index = Index.TOKENIZED,
    // search is using text stemmer, so we need to index searchable fields with it
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class), store = Store.NO)
    private String lastName;

    /**
     * The suffix to append to the person's display name - defaults to "".
     */
    @Basic(optional = false)
    private String displayNameSuffix = "";

    /**
     * The preferred name of this Person.
     */
    @Basic(optional = false)
    @Field(name = "preferredName", index = Index.TOKENIZED, store = Store.NO,
    // analyzer
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class))
    private String preferredName;

    /**
     * Email for person.
     */
    @Basic(optional = true)
    @Email(message = EMAIL_MESSAGE)
    private String email;

    /**
     * Work phone number.
     */
    @Length(max = MAX_PHONE_NUMBER_LENGTH, message = PHONE_NUMBER_MESSAGE)
    private String workPhone;

    /**
     * Home phone number.
     */
    @Basic(optional = true)
    @Length(max = MAX_PHONE_NUMBER_LENGTH, message = PHONE_NUMBER_MESSAGE)
    private String cellPhone;

    /**
     * Fax number.
     */
    @Basic(optional = true)
    @Length(max = MAX_PHONE_NUMBER_LENGTH, message = FAX_NUMBER_MESSAGE)
    private String fax;

    /**
     * Profile title for person.
     */
    @Length(max = MAX_TITLE_LENGTH, message = TITLE_MESSAGE)
    @Field(name = "title", index = Index.TOKENIZED,
    //
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class), store = Store.NO)
    private String title;

    /**
     * Job description for person.
     */
    @Basic(optional = true)
    @Length(max = MAX_JOB_DESCRIPTION_LENGTH, message = JOB_DESCRIPTION_MESSAGE)
    @Field(name = "description", index = Index.TOKENIZED,
    // search is using text stemmer, so we need to index searchable fields with it
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class), store = Store.NO)
    private String jobDescription;

    /**
     * Background information - not managed through Person, here for search indexing.
     */
    @OneToOne(mappedBy = "person", fetch = FetchType.LAZY)
    @Field(name = "background", bridge = @FieldBridge(impl = BackgroundStringBridge.class), index = Index.TOKENIZED,
    // line break
    store = Store.NO, analyzer = @Analyzer(impl = TextStemmerAnalyzer.class))
    private Background background;

    /**
     * Profile overview for person. This is a short version of the biography.
     */
    @Basic(optional = true)
    @Length(max = MAX_OVERVIEW_LENGTH, message = OVERVIEW_MESSAGE)
    @Field(name = "overview", index = Index.TOKENIZED, store = Store.NO,
    // html-stemmer analyzer will be used for indexing and, text-stemmer for searching
    analyzer = @Analyzer(impl = HtmlStemmerAnalyzer.class))
    private String overview;

    /**
     * A ; delimited list of video ids a person has optedOut of seeing.
     */
    @Basic
    private String optOutVideoIds = "";

    /**
     * Open social ID.
     */
    @Basic
    private String openSocialId;

    /**
     *
     */
    @Basic
    private Integer avatarCropX;

    /**
     *
     */
    @Basic
    private Integer avatarCropY;

    /**
     *
     */
    @Basic
    private Integer avatarCropSize;

    /**
     * The date the user last accepted the terms of service.
     */
    @Basic(optional = true)
    private Date lastAcceptedTermsOfService = null;

    /**
     * Count of people following this user.
     */
    @Basic(optional = false)
    @Field(name = "followersCount", index = Index.UN_TOKENIZED, store = Store.NO)
    private int followersCount = 0;
    /**
     * The number of updates for this person.
     */
    @Basic(optional = false)
    @Field(name = "updatesCount", index = Index.UN_TOKENIZED, store = Store.NO)
    private int updatesCount = 0;
    /**
     * Count of people this user is following.
     */
    @Basic(optional = false)
    private int followingCount = 0;
    /**
     * Count of groups this user is following.
     */
    @Basic(optional = false)
    private int groupsCount = 0;
    /**
     * avatar id image for this user.
     */
    @Basic
    private String avatarId;

    /**
     * Whether the entity allows comments on their post.
     */
    @Basic(optional = false)
    @Field(name = "isCommentable", index = Index.UN_TOKENIZED, store = Store.NO)
    private boolean commentable = true;

    /**
     * Whether the entity allows people to post to their stream.
     */
    @Basic(optional = false)
    @Field(name = "isStreamPostable", index = Index.UN_TOKENIZED, store = Store.NO)
    private boolean streamPostable = true;

    /**
     * Only used for query reference, don't load this.
     */
    @SuppressWarnings("unused")
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinTable(name = "Follower",
    // join columns
    joinColumns = { @JoinColumn(table = "Person", name = "followingId") },
    // inverse join columns
    inverseJoinColumns = { @JoinColumn(table = "Person", name = "followerId") })
    private List<Person> followers;

    /**
     * List of people this user is following.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinTable(name = "Follower",
    // join columns
    joinColumns = { @JoinColumn(table = "Person", name = "followerId") },
    // inverse join columns
    inverseJoinColumns = { @JoinColumn(table = "Person", name = "followingId") })
    private List<Person> following;

    /**
     * List of groups this user is following.
     */
    @OrderBy(clause = "name")
    @SuppressWarnings("unused")
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinTable(name = "GroupFollower",
    // join columns
    joinColumns = { @JoinColumn(table = "Person", name = "followerId") },
    // inverse join columns
    inverseJoinColumns = { @JoinColumn(table = "DomainGroup", name = "followingId") })
    private List<DomainGroup> followingGroup;

    /**
     * Start page TabGroup.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "startTabGroupId", nullable = false)
    private TabGroup startTabGroup = new TabGroup();

    /**
     * The application data for this person.
     */
    @SuppressWarnings("unused")
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "personId")
    private List<AppData> appData;

    /**
     * This field will maintain a link to the corresponding theme for this person.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "themeId")
    private Theme theme;

    /**
     * Stream scope representing this person.
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
    @JoinColumn(name = "streamScopeId")
    private StreamScope streamScope;

    /**
     * StreamView hidden line index for user.
     */
    @Basic(optional = false)
    private Integer streamViewHiddenLineIndex = 0;

    /**
     * GroupStream hidden line index for user.
     */
    @Basic(optional = false)
    private Integer groupStreamHiddenLineIndex = 0;

    /**
     * Flag indicating if user's account is locked.
     */
    @Basic(optional = false)
    private boolean accountLocked = false;

    /**
     * Flag indicating if user's account is deactivated (forced locked).
     */
    @Basic(optional = false)
    private boolean accountDeactivated = false;

    /**
     * Optional list of sources that were traversed to locate this employee.
     */
    @Transient
    private ArrayList<String> sourceList = new ArrayList<String>();

    /**
     * Public constructor for ORM and ResourcePersistenceStrategy.
     */
    public Person()
    {
    }

    /**
     * Gets the user's name in the standard format for display.
     * 
     * @return Displayed name.
     */
    @Override
    public String getDisplayName()
    {
        if (displayName == null || displayName.isEmpty())
        {
            displayName = preferredName + " " + lastName + displayNameSuffix;
        }
        return displayName;
    }

    /**
     * Set the display name - for de/serialization only.
     * 
     * @param inDisplayName
     *            the display name to set
     */
    @SuppressWarnings("unused")
    private void setDisplayName(final String inDisplayName)
    {
        displayName = inDisplayName;
    }

    /**
     * Allows for getting displayName in a property projection.
     */
    @Formula("preferredName || ' ' || lastName || '' || displayNameSuffix")
    private String displayName;

    /**
     * Optional map of additional properties.
     */
    @Basic(optional = true)
    private HashMap<String, String> additionalProperties;

    /**
     * Company name.
     */
    @Length(min = 1, max = MAX_FIRST_NAME_LENGTH, message = COMPANY_NAME_MESSAGE)
    private String companyName;

    /**
     * Whether this user is a system administrator.
     */
    private boolean isAdministrator;

    /**
     * TODO: This is a patch until we stop sending entities over the line
     * 
     * Construct a person from a person model view.
     * 
     * @param personModelView
     *            the person model view.
     */
    public Person(final PersonModelView personModelView)
    {
        setId(personModelView.getEntityId());
        avatarId = personModelView.getAvatarId();
        setAccountId(personModelView.getAccountId());
        openSocialId = personModelView.getOpenSocialId();
        setOptOutVideos(personModelView.getOptOutVideos());
        followersCount = personModelView.getFollowersCount();
        title = personModelView.getTitle();
        email = personModelView.getEmail();
        dateAdded = personModelView.getDateAdded();
        additionalProperties = personModelView.getAdditionalProperties();
        lastName = personModelView.getLastName();
        preferredName = personModelView.getPreferredName();
        displayNameSuffix = personModelView.getDisplayNameSuffix();

        if (personModelView.getDisplayName() != null)
        {
            // override with the display name in the modelview
            displayName = personModelView.getDisplayName();
        }
    }

    /**
     * Public constructor for API.
     * 
     * @param inAccountId
     *            unique key for person - lower-cased for uniqueness
     * @param inFirstName
     *            person's first name
     * @param inMiddleName
     *            person's middle name
     * @param inLastName
     *            person's last name
     * @param inPreferredName
     *            name the user prefers to be called
     */
    public Person(final String inAccountId, final String inFirstName, final String inMiddleName,
            final String inLastName, final String inPreferredName)
    {
        setAccountId(inAccountId);
        firstName = inFirstName;
        middleName = inMiddleName;
        lastName = inLastName;
        preferredName = (inPreferredName == null || inPreferredName.trim().length() == 0) ? inFirstName
                : inPreferredName;
        displayName = preferredName + " " + lastName;
    }

    /**
     * Get the person's unique key.
     * 
     * @return the unique case-insensitive account id of this Person.
     */
    public String getAccountId()
    {
        return accountId;
    }

    /**
     * Gets the opensocial ID.
     * 
     * @return the open social id.
     */
    public String getOpenSocialId()
    {
        return openSocialId;
    }

    /**
     * Sets the open social id.
     * 
     * @param inOpenSocialId
     *            the open social id.
     */
    public void setOpenSocialId(final String inOpenSocialId)
    {
        openSocialId = inOpenSocialId;
    }

    /**
     * Returns list of tabs of correct TabGroupType or empty list if none exist.
     * 
     * @param tabGroupType
     *            The tab type enum value.
     * @return list of tabs of correct TabGroupType.
     */
    public List<Tab> getTabs(final TabGroupType tabGroupType)
    {
        switch (tabGroupType)
        {
        case START:
            return startTabGroup != null ? startTabGroup.getTabs() : new ArrayList<Tab>();
        default:
            return new ArrayList<Tab>();
        }
    }

    /**
     * Adds the given tab to the specified tab group type.
     * 
     * @param newTab
     *            The new tab.
     * @param tabGroupType
     *            The new tab type.
     */
    public void addTab(final Tab newTab, final TabGroupType tabGroupType)
    {
        switch (tabGroupType)
        {
        case START:
            startTabGroup.addTab(newTab);
            break;
        default:
        }
    }

    /**
     * @return the startTabGroup
     */
    public TabGroup getStartTabGroup()
    {
        return startTabGroup;
    }

    /**
     * @param inStartTabGroup
     *            the startTabGroup to set
     */
    public void setStartTabGroup(final TabGroup inStartTabGroup)
    {
        startTabGroup = inStartTabGroup;
    }

    /**
     * Set the Person's individual theme.
     * 
     * @param inTheme
     *            the new theme.
     */
    public void setTheme(final Theme inTheme)
    {
        theme = inTheme;
    }

    /**
     * Get the Person's individual theme.
     * 
     * @return the theme
     */
    public Theme getTheme()
    {
        return theme;
    }

    /**
     * Get the person's first name.
     * 
     * @return the first name
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * Get the person's middle name.
     * 
     * @return the middle name
     */
    public String getMiddleName()
    {
        return middleName;
    }

    /**
     * Get the person's last name.
     * 
     * @return the last name
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * Get the name the user prefers to be called.
     * 
     * Method is non-final to facilitate mocking.
     * 
     * @return the name the user prefers to be called
     */
    public String getPreferredName()
    {
        return preferredName;
    }

    /**
     * Getter for person's email address.
     * 
     * @return person's email address.
     */
    @Override
    public String getEmail()
    {
        return email;
    }

    /**
     * Setter for person's email address.
     * 
     * Hibernate validation on this object requires the attribute to be not null and to be a valid email address.
     * (name@domain.com, etc).
     * 
     * @param inEmail
     *            person's email address.
     */
    public void setEmail(final String inEmail)
    {
        email = inEmail;
    }

    /**
     * @return the workPhone
     */
    public String getWorkPhone()
    {
        return workPhone;
    }

    /**
     * Setter for work phone number.
     * 
     * @param phoneNumber
     *            The phone number.
     */
    public void setWorkPhone(final String phoneNumber)
    {
        workPhone = phoneNumber;
    }

    /**
     * @return the cellPhone
     */
    public String getCellPhone()
    {
        return cellPhone;
    }

    /**
     * Setter for cell phone number.
     * 
     * @param phoneNumber
     *            The phone number.
     */
    public void setCellPhone(final String phoneNumber)
    {
        cellPhone = phoneNumber;
    }

    /**
     * @return optout video ids.
     */
    @SuppressWarnings("unused")
    private String getOptOutVideoIds()
    {
        return optOutVideoIds;
    }

    /**
     * private methods.
     * 
     * @param inOptOutVideoIds
     *            the String of opt videos to set.
     */
    @SuppressWarnings("unused")
    private void setOptOutVideoIds(final String inOptOutVideoIds)
    {
        optOutVideoIds = inOptOutVideoIds;
    }

    /**
     * NOTE: this method abstracts the way the db stores this data. This returns a copy of the set. Adding to this copy
     * will not save to the entity. You must use the set method to modify these values.
     * 
     * @return a COPY of the set of videos a person has opted out of.
     */
    @SuppressWarnings("unchecked")
    public HashSet<Long> getOptOutVideos()
    {
        HashSet<Long> videoIdSet = new HashSet();

        if (optOutVideoIds != null && !(optOutVideoIds.isEmpty()))
        {
            String[] videoIdsArray = optOutVideoIds.split(";");

            for (String vidId : videoIdsArray)
            {
                // if it tokenized the ending ; then don't set a blank long.
                if (!vidId.isEmpty())
                {
                    videoIdSet.add(Long.valueOf(vidId.trim()));
                }
            }
        }

        return videoIdSet;
    }

    /**
     * set the set of videos a person has opted out of.
     * 
     * @param inOptOutVideos
     *            the set of videos.
     * 
     */
    public void setOptOutVideos(final HashSet<Long> inOptOutVideos)
    {
        // this tmp variable is being used in case there is a exception then we won't loss data.
        String tmpOptOutVideoIds = "";
        for (Long vidId : inOptOutVideos)
        {
            tmpOptOutVideoIds = tmpOptOutVideoIds + vidId + ";";
        }

        optOutVideoIds = tmpOptOutVideoIds;
    }

    /**
     * @return the fax
     */
    public String getFax()
    {
        return fax;
    }

    /**
     * Setter for fax number.
     * 
     * @param faxNumber
     *            The phone number.
     */
    public void setFax(final String faxNumber)
    {
        fax = faxNumber;
    }

    /**
     * Getter for job description.
     * 
     * @return job description.
     */
    public String getJobDescription()
    {
        return jobDescription;
    }

    /**
     * Setter for job description.
     * 
     * @param inJobDescription
     *            person's job description.
     */
    public void setJobDescription(final String inJobDescription)
    {
        jobDescription = inJobDescription;
    }

    /**
     * Getter for person's title.
     * 
     * @return person's title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Setter for person's title.
     * 
     * @param inTitle
     *            person's title.
     */
    public void setTitle(final String inTitle)
    {
        title = inTitle;
    }

    /**
     * @return the followersCount
     */
    @Override
    public int getFollowersCount()
    {
        return followersCount;
    }

    /**
     * Private setter for followers count.
     * 
     * @param inFollowersCount
     *            The count.
     */
    @SuppressWarnings("unused")
    private void setFollowersCount(final int inFollowersCount)
    {
        followersCount = inFollowersCount;
    }

    /**
     * @return the followingCount
     */
    public int getFollowingCount()
    {
        return followingCount;
    }

    /**
     * Private setter for followingCount.
     * 
     * @param inFollowingCount
     *            The count.
     */
    @SuppressWarnings("unused")
    private void setFollowingCount(final int inFollowingCount)
    {
        followingCount = inFollowingCount;
    }

    /**
     * @return following
     */
    public List<Person> getFollowing()
    {
        return following;
    }

    /**
     * Get avatar x coord.
     * 
     * @return avatar x coord.
     */
    @Override
    public Integer getAvatarCropX()
    {
        return avatarCropX;
    }

    /**
     * Set avatar x coord.
     * 
     * @param value
     *            x coord.
     */
    @Override
    public void setAvatarCropX(final Integer value)
    {
        avatarCropX = value;
    }

    /**
     * Get avatar y coord.
     * 
     * @return avatar y coord.
     */
    @Override
    public Integer getAvatarCropY()
    {
        return avatarCropY;
    }

    /**
     * Set avatar y coord.
     * 
     * @param value
     *            y coord.
     */
    @Override
    public void setAvatarCropY(final Integer value)
    {
        avatarCropY = value;
    }

    /**
     * Get avatar crop size.
     * 
     * @return avatar crop size.
     */
    @Override
    public Integer getAvatarCropSize()
    {
        return avatarCropSize;
    }

    /**
     * Set avatar crop size.
     * 
     * @param value
     *            crop size.
     */
    @Override
    public void setAvatarCropSize(final Integer value)
    {
        avatarCropSize = value;
    }

    /**
     * @return the avatar Id
     */
    @Override
    public String getAvatarId()
    {
        return avatarId;
    }

    /**
     * @param inAvatarId
     *            the avatar to set
     */
    @Override
    public void setAvatarId(final String inAvatarId)
    {
        avatarId = inAvatarId;
    }

    /**
     * Adding private setter to make serialization work.
     * 
     * Hibernate validation on this object requires the first name to be not null, and of length greater than 1 and less
     * than MAX_FIRST_NAME_LENGTH
     * 
     * @param inFirstName
     *            the firstName to set
     */
    @SuppressWarnings("unused")
    private void setFirstName(final String inFirstName)
    {
        firstName = inFirstName;
    }

    /**
     * Adding private setter to make serialization work.
     * 
     * @param inMiddleName
     *            the middleName to set
     */
    @SuppressWarnings("unused")
    private void setMiddleName(final String inMiddleName)
    {
        middleName = inMiddleName;
    }

    /**
     * Set the person's last name.
     * 
     * Hibernate validation on this object requires the last name to be not null, and of length greater than 1 and less
     * than MAX_LAST_NAME_LENGTH
     * 
     * @param inLastName
     *            the lastName to set
     */
    public void setLastName(final String inLastName)
    {
        lastName = inLastName;
    }

    /**
     * Set the person's preferred name.
     * 
     * @param inPreferredName
     *            the preferredName to set
     */
    public void setPreferredName(final String inPreferredName)
    {
        preferredName = inPreferredName;

        // reset the display name
        displayName = null;
    }

    /**
     * Set the person's unique key, needed for serialization.
     * 
     * @param inAccountId
     *            the account ID.
     */
    private void setAccountId(final String inAccountId)
    {
        accountId = inAccountId == null ? null : inAccountId.toLowerCase();
    }

    /**
     * Returns properties of person, this is NOT an all inclusive list, this is used by create and update functionality.
     * 
     * @return properties of person.
     */
    public HashMap<String, Serializable> getProperties()
    {
        HashMap<String, Serializable> personData = new HashMap<String, Serializable>();
        addNonNullProperty("accountId", getAccountId(), personData);
        addNonNullProperty("firstName", getFirstName(), personData);
        addNonNullProperty("middleName", getMiddleName(), personData);
        addNonNullProperty("lastName", getLastName(), personData);
        addNonNullProperty("preferredName", getPreferredName(), personData);
        addNonNullProperty("displayNameSuffix", getDisplayNameSuffix(), personData);
        addNonNullProperty("email", getEmail(), personData);
        addNonNullProperty("workPhone", getWorkPhone(), personData);
        addNonNullProperty("fax", getFax(), personData);
        addNonNullProperty("cellPhone", getCellPhone(), personData);
        addNonNullProperty("jobDescription", getJobDescription(), personData);
        addNonNullProperty("title", getTitle(), personData);
        addNonNullProperty("accountLocked", isAccountLocked(), personData);
        addNonNullProperty("accountDeactivated", isAccountDeactivated(), personData);
        addNonNullProperty("companyName", getCompanyName(), personData);

        if (getAdditionalProperties() != null)
        {
            personData.put("additionalProperties", getAdditionalProperties());
        }
        if (getSourceList() != null && !getSourceList().isEmpty())
        {
            personData.put("sourceList", getSourceList());
        }
        return personData;
    }

    /**
     * Adds key/value pairs to a map if non-null.
     * 
     * @param key
     *            Key to use.
     * @param value
     *            Value to use.
     * @param map
     *            Map to use.
     */
    private void addNonNullProperty(final String key, final Serializable value, final Map<String, Serializable> map)
    {
        // if null or empty string, return instantly.
        if (value == null || (value instanceof String && ((String) value).length() == 0))
        {
            return;
        }
        map.put(key, value);
    }

    /**
     * @return the overview
     */
    public String getOverview()
    {
        return overview;
    }

    /**
     * @param inOverview
     *            the overview to set
     */
    public void setOverview(final String inOverview)
    {
        overview = inOverview;
    }

    /**
     * @param inGroupCount
     *            the groupCount to set
     */
    public void setGroupCount(final int inGroupCount)
    {
        groupsCount = inGroupCount;
    }

    /**
     * @return the groupCount
     */
    public int getGroupCount()
    {
        return groupsCount;
    }

    /**
     * Override equality to be based on the person's id.
     * 
     * @param rhs
     *            target object
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(final Object rhs)
    {
        return (rhs instanceof Person && getId() == ((Person) rhs).getId() && accountId.equals(((Person) rhs)
                .getAccountId()));
    }

    /**
     * set the id - useful for unit testing.
     * 
     * @param newId
     *            the new id
     */
    @Override
    protected void setId(final long newId)
    {
        super.setId(newId);
    }

    /**
     * HashCode override.
     * 
     * @see java.lang.Object#hashCode()
     * @return hashcode for object.
     */
    @Override
    public int hashCode()
    {
        // NOTE: unable to use HashCodeBuilder here due to GWT limitation.
        int hashCode = 0;
        hashCode ^= (new Long(getId())).hashCode();
        hashCode ^= null != accountId ? accountId.hashCode() : "".hashCode();
        return hashCode;
    }

    /**
     * Set the date the user was added to the system.
     * 
     * @param inDateAdded
     *            the dateAdded to set
     */
    protected void setDateAdded(final Date inDateAdded)
    {
        dateAdded = inDateAdded;
    }

    /**
     * Get the date the person was added to the system.
     * 
     * @return the dateAdded
     */
    public Date getDateAdded()
    {
        return dateAdded;
    }

    /**
     * Get the number of updates for this person.
     * 
     * @return the updatesCount
     */
    public int getUpdatesCount()
    {
        return updatesCount;
    }

    /**
     * Set the number of updates for this person.
     * 
     * @param inUpdatesCount
     *            the updatesCount to set
     */
    protected void setUpdatesCount(final int inUpdatesCount)
    {
        updatesCount = inUpdatesCount;
    }

    /**
     * Get the Person's background.
     * 
     * @return the Person's background.
     */
    public Background getBackground()
    {
        return background;
    }

    /**
     * Setter for serialization.
     * 
     * @param inBackground
     *            the background to set
     */
    @SuppressWarnings("unused")
    private void setBackground(final Background inBackground)
    {
        background = inBackground;
    }

    /**
     * @return if the profile is set to allow post comments.
     */
    public boolean isCommentable()
    {
        return commentable;
    }

    /**
     * @param inCommentable
     *            if the profile is set to allow post comments.
     */
    public void setCommentable(final boolean inCommentable)
    {
        commentable = inCommentable;
    }

    /**
     * @return if the profile is set to allow wall post.
     */
    public boolean isStreamPostable()
    {
        return streamPostable;
    }

    /**
     * @param inStreamPostable
     *            set the steam postable property.
     */
    public void setStreamPostable(final boolean inStreamPostable)
    {
        streamPostable = inStreamPostable;
    }

    /**
     * @return the streamViewHiddenLineIndex
     */
    public Integer getStreamViewHiddenLineIndex()
    {
        return streamViewHiddenLineIndex;
    }

    /**
     * @param inStreamViewHiddenLineIndex
     *            the streamViewHiddenLineIndex to set
     */
    public void setStreamViewHiddenLineIndex(final Integer inStreamViewHiddenLineIndex)
    {
        streamViewHiddenLineIndex = inStreamViewHiddenLineIndex;
    }

    /**
     * @return the groupStreamHiddenLineIndex
     */
    public Integer getGroupStreamHiddenLineIndex()
    {
        return groupStreamHiddenLineIndex;
    }

    /**
     * @param inGroupStreamHiddenLineIndex
     *            the groupStreamHiddenLineIndex to set
     */
    public void setGroupStreamHiddenLineIndex(final Integer inGroupStreamHiddenLineIndex)
    {
        groupStreamHiddenLineIndex = inGroupStreamHiddenLineIndex;
    }

    // ----------------------------------------------------
    // ------------------ CACHE UPDATING ------------------

    /**
     * Call-back after a Person entity has been updated. This tells the static cacheUpdater if set.
     */
    @SuppressWarnings("unused")
    @PostUpdate
    private void onPostUpdate()
    {
        if (entityCacheUpdater != null)
        {
            entityCacheUpdater.onPostUpdate(this);
        }
    }

    /**
     * Call-back after the entity has been persisted. This tells the static cacheUpdater if set.
     */
    @SuppressWarnings("unused")
    @PostPersist
    private void onPostPersist()
    {
        if (entityCacheUpdater != null)
        {
            entityCacheUpdater.onPostPersist(this);
        }
    }

    /**
     * The entity cache updater.
     */
    private static transient EntityCacheUpdater<Person> entityCacheUpdater;

    /**
     * Setter for the static PersonUpdater.
     * 
     * @param inEntityCacheUpdater
     *            the PersonUpdater to set
     */
    public static void setEntityCacheUpdater(final EntityCacheUpdater<Person> inEntityCacheUpdater)
    {
        entityCacheUpdater = inEntityCacheUpdater;
    }

    // ---------------- END CACHE UPDATING ----------------
    // ----------------------------------------------------
    /**
     * @param inLastAcceptedTermsOfService
     *            the lastAcceptedTermsOfService to set
     */
    public void setLastAcceptedTermsOfService(final Date inLastAcceptedTermsOfService)
    {
        lastAcceptedTermsOfService = inLastAcceptedTermsOfService;
    }

    /**
     * @return the lastAcceptedTermsOfService
     */
    public Date getLastAcceptedTermsOfService()
    {
        return lastAcceptedTermsOfService;
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
     * @return if the account is locked.
     */
    public boolean isAccountLocked()
    {
        return accountLocked;
    }

    /**
     * @param inAccountLocked
     *            if the account is locked.
     */
    public void setAccountLocked(final boolean inAccountLocked)
    {
        accountLocked = inAccountLocked;
    }

    /**
     * Get the unique id for as implemented for Followable.
     * 
     * @return uniqueId of the person - accountid.
     */
    @Override
    public String getUniqueId()
    {
        return getAccountId();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getBannerId()
    {
        return bannerId;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setBannerId(final String inBannerId)
    {
        bannerId = inBannerId;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Long getBannerEntityId()
    {
        return bannerEntityId;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setBannerEntityId(final Long inBannerEntityId)
    {
        bannerEntityId = inBannerEntityId;
    }

    /**
     * Get the streams.
     * 
     * @return the streams
     */
    public List<Stream> getStreams()
    {
        return streams;
    }

    /**
     * Set the streams.
     * 
     * @param inStreams
     *            the streams to set
     */
    public void setStreams(final List<Stream> inStreams)
    {
        streams = inStreams;
    }

    /**
     * The additionalProperties setter.
     * 
     * @param inAdditionalProperties
     *            the properties hashmap to set
     */
    public void setAdditionalProperties(final HashMap<String, String> inAdditionalProperties)
    {
        additionalProperties = inAdditionalProperties;
    }

    /**
     * The additionalProperties getter.
     * 
     * @return additionalProperties hashmap.
     */
    public HashMap<String, String> getAdditionalProperties()
    {
        return additionalProperties;
    }

    /**
     * TODO: This is a patch until we stop sending entities over the line
     * 
     * Creates a PersonModelView from the person.
     * 
     * @return PersonModelView.
     */
    public PersonModelView toPersonModelView()
    {
        PersonModelView p = new PersonModelView();
        p.setEntityId(getId());
        p.setAvatarId(avatarId);
        p.setAccountId(accountId);
        p.setOpenSocialId(openSocialId);
        p.setOptOutVideos(getOptOutVideos());
        p.setDisplayName(getDisplayName());
        p.setFollowersCount(followersCount);
        p.setFollowingCount(followingCount);
        p.setGroupsCount(groupsCount);
        p.setTitle(title);
        p.setEmail(email);
        p.setDateAdded(dateAdded);
        p.setAdditionalProperties(getAdditionalProperties());
        p.setLastName(lastName);
        p.setPreferredName(preferredName);
        p.setJobDescription(getJobDescription());
        p.setCompanyName(getCompanyName());
        p.setAvatarCropSize(avatarCropSize);
        p.setAvatarCropX(avatarCropX);
        p.setAvatarCropY(avatarCropY);
        p.setCellPhone(cellPhone);
        p.setWorkPhone(workPhone);
        p.setFax(fax);
        p.setBannerId(bannerId);

        if (background != null)
        {
            List<String> interests = new ArrayList<String>();
            for (BackgroundItem item : background.getBackgroundItems(BackgroundItemType.SKILL))
            {
                interests.add(item.getName());
            }
            p.setInterests(interests);
        }
        return p;
    }

    /**
     * Get the company name.
     * 
     * @return the company name.
     */
    public String getCompanyName()
    {
        return companyName;
    }

    /**
     * Set the company name.
     * 
     * @param inCompanyName
     *            the company name.
     */
    public void setCompanyName(final String inCompanyName)
    {
        companyName = inCompanyName;
    }

    /**
     * @return the isAdministrator
     */
    public boolean isAdministrator()
    {
        return isAdministrator;
    }

    /**
     * @param inIsAdministrator
     *            the isAdministrator to set
     */
    public void setAdministrator(final boolean inIsAdministrator)
    {
        isAdministrator = inIsAdministrator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityType getEntityType()
    {
        return EntityType.PERSON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getEntityId()
    {
        return getId();
    }

    /**
     * @return the sourceList
     */
    public ArrayList<String> getSourceList()
    {
        return sourceList;
    }

    /**
     * @param inSourceList
     *            the sourceList to set
     */
    public void setSourceList(final ArrayList<String> inSourceList)
    {
        sourceList = inSourceList;
    }

    /**
     * @param inBookmarks
     *            the bookmarks to set.
     */
    public void setBookmarks(final List<StreamScope> inBookmarks)
    {
        bookmarks = inBookmarks;
    }

    /**
     * @return the bookmarks.
     */
    public List<StreamScope> getBookmarks()
    {
        return bookmarks;
    }

    /**
     * @return Current deactivated status
     */
    public boolean isAccountDeactivated()
    {
        return accountDeactivated;
    }

    /**
     * @param inAccountDeactivated
     *            New deactivated status.
     */
    public void setAccountDeactivated(final boolean inAccountDeactivated)
    {
        accountDeactivated = inAccountDeactivated;
    }

    /**
     * Set the displayNameSuffix.
     * 
     * @param inDisplayNameSuffix
     *            The display name suffix to set.
     */
    public void setDisplayNameSuffix(final String inDisplayNameSuffix)
    {
        displayNameSuffix = inDisplayNameSuffix;

        // reset the display name
        displayName = null;
    }

    /**
     * Get the displayNameSuffix.
     * 
     * @return the display name suffix
     */
    public String getDisplayNameSuffix()
    {
        return displayNameSuffix;
    }
}
