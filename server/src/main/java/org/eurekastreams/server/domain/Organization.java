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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.commons.search.analysis.HtmlStemmerAnalyzer;
import org.eurekastreams.commons.search.analysis.TextStemmerAnalyzer;
import org.eurekastreams.commons.search.bridge.StandardAnalyzerSortFieldBridge;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.search.bridge.BackgroundItemListStringBridge;
import org.eurekastreams.server.search.bridge.IsRootOrganizationClassBridge;
import org.eurekastreams.server.search.bridge.OrgIdHierarchyFieldBridge;
import org.eurekastreams.server.search.bridge.OrganizationToShortNameFieldBridge;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.Length;
import org.hibernate.validator.Pattern;
import org.hibernate.validator.Size;

/**
 * Represents an organization, which holds people.
 */
@SuppressWarnings("serial")
@Entity
@Indexed
@ClassBridge(name = "isRootOrganization", index = Index.UN_TOKENIZED, store = Store.NO,
// (line length)
impl = IsRootOrganizationClassBridge.class)
public class Organization extends DomainEntity implements OrganizationChild, AvatarEntity, CompositeEntity, Bannerable
{
    /** Used for validation. */
    public static final int MAX_NAME_LENGTH = 50;

    /** Used for validation. */
    public static final int MAX_DESCRIPTION_LENGTH = 250;

    /** Used for validation. */
    public static final int MAX_SHORT_NAME_LENGTH = 20;

    // TODO Messages should be moved into the ui model.
    /** Used for validation. */
    public static final String NAME_LENGTH_MESSAGE = "Organization Name must supports up to " + MAX_NAME_LENGTH
            + " characters.";

    /** Used for validation. */
    public static final String SHORT_NAME_LENGTH_MESSAGE = "Organization Web Address supports up to "
            + MAX_SHORT_NAME_LENGTH + " characters.";

    /** Used for validation. */
    @Transient
    public static final String NAME_REQUIRED = "Organization Name is required.";

    /** Used for validation. */
    @Transient
    public static final String SHORTNAME_REQUIRED = "Organization Web Address is required.";

    /** Used for validation. */
    public static final String MIN_COORDINATORS_MESSAGE = "Organizations must have at least one coordinator.";

    /** Used for validation. */
    public static final String DESCRIPTION_LENGTH_MESSAGE = "Description supports up to " + MAX_DESCRIPTION_LENGTH
            + " characters.";

    /** Used for validation. */
    public static final String ALPHA_NUMERIC_PATTERN = "[A-Za-z0-9]*";

    /** Used for validation. */
    public static final String SHORT_NAME_CHARACTERS = "A short name can only contain alphanumeric "
            + "characters and no spaces.";

    /**
     * Transient 'isPublic' field used only for searching - helps speed up certain queries that contain
     * permission-scoped entities.
     */
    @Transient
    @Field(name = "isPublic", index = Index.UN_TOKENIZED, store = Store.NO)
    @SuppressWarnings("unused")
    private final boolean publicGroup = true;
    // TODO why is this called publicgroup?

    /**
     * The name of the organization.
     */
    @Basic(optional = false)
    @Length(min = 1, max = MAX_NAME_LENGTH, message = NAME_LENGTH_MESSAGE)
    @Fields(value = { @Field(name = "name", index = Index.TOKENIZED, store = Store.NO,
    // use Text stemmer analyzer
            analyzer = @Analyzer(impl = TextStemmerAnalyzer.class)),
            // sort field - needs to be indexed and we need the StandardAnalyzer to convert to lowercase, and to pull
            // out common words and punctuation. This way "The XYZ" will show up after "XYZ"
            @Field(name = "byName", index = Index.UN_TOKENIZED, store = Store.NO,
            // bridge tokenizes then joins
            bridge = @FieldBridge(impl = StandardAnalyzerSortFieldBridge.class)) })
    private String name;

    /**
     * The skills that are contained in this background.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinTable(name = "Organization_Capability",
    // join columns
    joinColumns = { @JoinColumn(table = "Organization", name = "organizationId") },
    // inverse join columns
    inverseJoinColumns = { @JoinColumn(table = "BackgroundItem", name = "capabilityId") },
    // unique constraints
    uniqueConstraints = { @UniqueConstraint(columnNames = { "organizationId", "capabilityId" }) })
    @Field(name = "capabilities", bridge = @FieldBridge(impl = BackgroundItemListStringBridge.class),
    // line break
    index = Index.TOKENIZED, store = Store.NO, analyzer = @Analyzer(impl = TextStemmerAnalyzer.class))
    private List<BackgroundItem> capabilities;

    /**
     * The short version of organization name.
     */
    @Column(nullable = false, unique = true)
    @Length(min = 1, max = MAX_NAME_LENGTH, message = SHORT_NAME_LENGTH_MESSAGE)
    @Pattern(regex = ALPHA_NUMERIC_PATTERN, message = SHORT_NAME_CHARACTERS)
    @Field(name = "shortName", index = Index.UN_TOKENIZED, store = Store.NO)
    private String shortName;

    /**
     * The overview of the organization.
     */
    @Basic
    @Lob
    @Field(name = "overview", index = Index.TOKENIZED, store = Store.NO,
    // html-stemmer analyzer will be used for indexing and, text-stemmer for searching
    analyzer = @Analyzer(impl = HtmlStemmerAnalyzer.class))
    private String overview;

    /**
     * The url of the organization.
     */
    @Basic
    @Pattern(regex = URL_REGEX_PATTERN, message = WEBSITE_MESSAGE)
    private String url;

    /**
     * if all users can create groups under this org.
     */
    @Basic
    private Boolean allUsersCanCreateGroups = true;

    /**
     * The X coordinate of the upper left corner of the crop.
     */
    @Basic
    private Integer avatarCropX;

    /**
     * The Y coordinate of the upper left corner of the crop.
     */
    @Basic
    private Integer avatarCropY;

    /**
     * The width of the crop.
     */
    @Basic
    private Integer avatarCropSize;

    /**
     * avatar id image for this user.
     */
    @Basic
    private String avatarId;

    /**
     * The description of the organization.
     */
    @Basic
    @Length(min = 1, max = MAX_DESCRIPTION_LENGTH, message = DESCRIPTION_LENGTH_MESSAGE)
    @Field(name = "description", index = Index.TOKENIZED, store = Store.NO,
    // text-stemmer analyzer will be used for indexing and, text-stemmer for searching
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class))
    private String description;

    /**
     * List of coordinators for this organization.
     */
    @Size(min = 1, message = MIN_COORDINATORS_MESSAGE)
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    // LazyCollectionOption.EXTRA is only being used here as a workaround for
    // hibernate bug where lazy collection collide with validators. This should
    // NOT be used in any other situation without understanding full ramifications
    // as it does potentially involve extra queries.
    @LazyCollection(LazyCollectionOption.EXTRA)
    @JoinTable(name = "Organization_Coordinators")
    private Set<Person> coordinators;

    /**
     * Get the parent org id w/o loading the org.
     */
    @Formula("parentOrganizationId")
    private Long parentOrgId;

    /**
     * Private collection mapped for JPA queries.
     */
    @SuppressWarnings("unused")
    @OneToMany(fetch = FetchType.LAZY)
    @Where(clause = "(id <> parentOrganizationId)")
    @JoinColumn(name = "parentOrganizationId")
    private Set<Organization> childOrganizations;

    /**
     * The persons that have this org as realated org. NOTE: This is a private lazy collection that allow
     * Person_RelatedOrganization to be cleaned up when an org is deleted. It is not for external use.
     */
    @SuppressWarnings("unused")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Person_RelatedOrganization",
    // join columns
    inverseJoinColumns = { @JoinColumn(table = "Person", name = "personId") },
    // inverse join columns
    joinColumns = { @JoinColumn(table = "Organization", name = "organizationId") },
    // unique constraints
    uniqueConstraints = { @UniqueConstraint(columnNames = { "personId", "organizationId" }) })
    private final List<Person> relatedPersons = new ArrayList<Person>();

    /**
     * The de-normalized child (non-recursive) organization count.
     */
    @Basic(optional = false)
    private int childOrganizationCount = 0;

    /**
     * The de-normalized descendant (recursive) group count.
     */
    @Basic(optional = false)
    private int descendantGroupCount = 0;

    /**
     * The number of updates for this org.
     */
    @Basic(optional = false)
    private int updatesCount = 0;

    /**
     * The de-normalized count (recursive) of employees in this organization.
     */
    @Basic(optional = false)
    private int descendantEmployeeCount = 0;

    /**
     * The de-normalized count of employees following this organization.
     */
    @Basic(optional = false)
    private int employeeFollowerCount = 0;

    /**
     * banner id for this org.
     */
    @Basic
    private String bannerId;

    /**
     * Transient field used only for displaying a banner on profile pages. This is needed so that a common strategy can
     * be used across groups, orgs, and people to display banners. When profiles support DTO's, this can be moved there.
     */
    @Transient
    private Long bannerEntityId;

    /**
     * Stream scope representing this organization.
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinColumn(name = "streamScopeId")
    private StreamScope streamScope;

    /**
     * Default constructor.
     */
    public Organization()
    {
        // no-op
    }

    /**
     * Constructor that creates a skeleton org from the input OrganizationModelView, populating the fields that the
     * front-end typically needs.
     *
     * - orgId, shortName, name, bannerId
     *
     * @param inOrgModelView
     *            - the organization modelview to pull values from
     */
    public Organization(final OrganizationModelView inOrgModelView)
    {
        setId(inOrgModelView.getEntityId());
        setShortName(inOrgModelView.getShortName());
        setName(inOrgModelView.getName());
        setBannerId(inOrgModelView.getBannerId());
        setParentOrgId(inOrgModelView.getParentOrganizationId());
    }

    /**
     * Override equality to be based on the org's id.
     *
     * @param rhs
     *            target object
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(final Object rhs)
    {
        return (rhs instanceof Organization && this.getId() == ((Organization) rhs).getId());
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
        hashCode ^= shortName.hashCode();
        return hashCode;
    }

    /**
     * List of leaders for this organization.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Organization_Leaders")
    private Set<Person> leaders;

    /**
     * Parent organization - note: this is indexed as a class-level bridge.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentOrganizationId")
    @Fields(value = {
    // "parentOrganizationShortName"
            @Field(name = "parentOrganizationShortName", index = Index.UN_TOKENIZED, store = Store.NO,
            // field bridge
            bridge = @FieldBridge(impl = OrganizationToShortNameFieldBridge.class)),

            // "parentOrganizationShortNameHierarchy" - a space-separated list of all short names up the tree
            @Field(name = "parentOrganizationIdHierarchy", index = Index.TOKENIZED, store = Store.NO,
            // WhitespaceAnalyzer to split on spaces, not lowercase, and not use stop words - necessary to mention
            // since we're tokenizing
            analyzer = @Analyzer(impl = WhitespaceAnalyzer.class),
            // field bridge
            bridge = @FieldBridge(impl = OrgIdHierarchyFieldBridge.class)) })
    private Organization parentOrganization;

    /**
     * Constructor. This should have every non-null parameter included.
     *
     * @param inName
     *            - Full name of org.
     * @param inShortName
     *            Short name of org.
     */
    public Organization(final String inName, final String inShortName)
    {
        name = inName;
        setShortName(inShortName);
    }

    /**
     * Add coordinator to org.
     *
     * @param person
     *            The Person to add.
     */
    public void addCoordinator(final Person person)
    {
        if (coordinators == null)
        {
            // doesn't *need* to be TreeSet, I just picked it.
            coordinators = new TreeSet<Person>();
        }
        coordinators.add(person);
    }

    /**
     * Remove coordinator from org.
     *
     * @param person
     *            The Person to remove.
     */
    public void removeCoordinator(final Person person)
    {
        for (Person p : coordinators)
        {
            if (person.getId() == p.getId())
            {
                coordinators.remove(p);
                break;
            }
        }
    }

    /**
     * Getter for list of coordinators.
     *
     * @return list of coordinators.
     */
    @Override
    public Set<Person> getCoordinators()
    {
        return coordinators;
    }

    /**
     * Setter for list of coordinators.
     *
     * @param inCoordinators
     *            list of coordinators.
     */
    public void setCoordinators(final Set<Person> inCoordinators)
    {
        coordinators = inCoordinators;
    }

    /**
     * Add leaders to org.
     *
     * @param person
     *            The Person to add.
     */
    public void addLeader(final Person person)
    {
        leaders.add(person);
    }

    /**
     * Remove leader from org.
     *
     * @param person
     *            The Person to remove.
     */
    public void removeLeader(final Person person)
    {
        for (Person p : leaders)
        {
            if (person.getId() == p.getId())
            {
                leaders.remove(p);
                break;
            }
        }
    }

    /**
     * Getter for list of leaders.
     *
     * @return list of leaders.
     */
    public Set<Person> getLeaders()
    {
        return leaders;
    }

    /**
     * Setter for list of leaders.
     *
     * @param inLeaders
     *            list of coordinators.
     */
    public void setLeaders(final Set<Person> inLeaders)
    {
        leaders = inLeaders;
    }

    /**
     * Getter for Organization name.
     *
     * @return the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Setter for Organization name.
     *
     * @param inName
     *            new name
     */
    public void setName(final String inName)
    {
        name = (null == inName) ? "" : inName;
    }

    /**
     * Getter for Organization short name.
     *
     * @return the shortName
     */
    public String getShortName()
    {
        return shortName;
    }

    /**
     * Setter for Organization short name.
     *
     * @param inShortName
     *            the shortName to set.
     */
    public void setShortName(final String inShortName)
    {
        shortName = (null == inShortName) ? "" : inShortName.toLowerCase();
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
     *            the url to set
     */
    public void setUrl(final String inUrl)
    {
        url = inUrl;
    }

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
     * @return the parentOrganization
     */
    @Override
    public Organization getParentOrganization()
    {
        return parentOrganization;
    }

    /**
     * Parent Organization - used for serialization and unit testing only - setting a parent organization must be done
     * through the mapper.
     *
     * @param inParentOrganization
     *            the parentOrganization to set
     */
    @Override
    public void setParentOrganization(final Organization inParentOrganization)
    {
        parentOrganization = inParentOrganization;
    }

    /**
     * @return If this organization is known to be the root org.
     */
    public boolean isRootOrganization()
    {
        return parentOrganization != null && parentOrganization.getId() == getId();
    }

    /**
     * check to see if the specified account id is a coordinator for this Organization.
     *
     * @param account
     *            to check.
     * @return if they're a coordinator.
     */
    public boolean isCoordinator(final String account)
    {
        for (Person p : coordinators)
        {
            if (p.getAccountId().equals(account))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Getter.
     *
     * @return the overview
     */
    @Override
    public String getOverview()
    {
        return overview;
    }

    /**
     * Setter.
     *
     * @param inOverview
     *            the overview to set
     */
    @Override
    public void setOverview(final String inOverview)
    {
        overview = inOverview;
    }

    /**
     * @return the capabilities
     */
    @Override
    public List<BackgroundItem> getCapabilities()
    {
        return (capabilities == null) ? new ArrayList<BackgroundItem>(0) : capabilities;
    }

    /**
     * @param inCapabilities
     *            the capabilities to set
     */
    @Override
    public void setCapabilities(final List<BackgroundItem> inCapabilities)
    {
        capabilities = inCapabilities;
    }

    /**
     * Set the number of child (non-recursive) organizations.
     *
     * @param inCount
     *            the count to set
     */
    public void setChildOrganizationCount(final int inCount)
    {
        childOrganizationCount = inCount;
    }

    /**
     * Get the number of child (non-recursive) organizations - de-normalized.
     *
     * @return the count
     */
    public int getChildOrganizationCount()
    {
        return childOrganizationCount;
    }

    /**
     * Get the de-normalized count of employees in this organization.
     *
     * @return the employeeCount
     */
    public int getDescendantEmployeeCount()
    {
        return descendantEmployeeCount;
    }

    /**
     * Set the employee count.
     *
     * @param inDescendantEmployeeCount
     *            the employeeCount to set
     */
    public void setDescendantEmployeeCount(final int inDescendantEmployeeCount)
    {
        descendantEmployeeCount = inDescendantEmployeeCount;
    }

    /**
     * Set the de-normalized number of employees following this organization - for unit testing and serialization.
     *
     * @param inEmployeeFollowerCount
     *            the employeeFollowerCount to set
     */
    protected void setEmployeeFollowerCount(final int inEmployeeFollowerCount)
    {
        employeeFollowerCount = inEmployeeFollowerCount;
    }

    /**
     * Get the de-normalized number of employees following this organization.
     *
     * @return the employeeFollowerCount the number of employees following this organization
     */
    public int getEmployeeFollowerCount()
    {
        return employeeFollowerCount;
    }

    /**
     * Set the de-normalized group count - for serialization and unit testing.
     *
     * @param inDescendantGroupCount
     *            the groupCount to set
     */
    public void setDescendantGroupCount(final int inDescendantGroupCount)
    {
        descendantGroupCount = inDescendantGroupCount;
    }

    /**
     * Get the de-normalized group count.
     *
     * @return the groupCount
     */
    public int getDescendantGroupCount()
    {
        return descendantGroupCount;
    }

    /**
     * @return the banner Id
     */
    @Override
    public String getBannerId()
    {
        return bannerId;
    }

    /**
     * @param inBannerId
     *            the banner to set
     */
    @Override
    public void setBannerId(final String inBannerId)
    {
        bannerId = inBannerId;
    }

    /**
     * Set the number of updates for the org.
     *
     * @param inUpdatesCount
     *            the updatesCount to set
     */
    public void setUpdatesCount(final int inUpdatesCount)
    {
        updatesCount = inUpdatesCount;
    }

    /**
     * Get the number of updates for the org.
     *
     * @return the updatesCount
     */
    public int getUpdatesCount()
    {
        return updatesCount;
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
     * @param inAllUsersCanCreateGroups
     *            the allUsersCanCreateGroups to set.
     */
    public void setAllUsersCanCreateGroups(final Boolean inAllUsersCanCreateGroups)
    {
        allUsersCanCreateGroups = inAllUsersCanCreateGroups;
    }

    /**
     * @return the allUsersCanCreateGroups.
     */
    public Boolean getAllUsersCanCreateGroups()
    {
        return allUsersCanCreateGroups;
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
    private static transient EntityCacheUpdater<Organization> entityCacheUpdater;

    /**
     * Setter for the static PersonUpdater.
     *
     * @param inEntityCacheUpdater
     *            the PersonUpdater to set
     */
    public static void setEntityCacheUpdater(final EntityCacheUpdater<Organization> inEntityCacheUpdater)
    {
        entityCacheUpdater = inEntityCacheUpdater;
    }

    // ---------------- END CACHE UPDATING ----------------
    // ----------------------------------------------------

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
     * Get the parent org id without loading the parent organization.
     *
     * @return the parent org id without loading the parent organization
     */
    @Override
    public Long getParentOrgId()
    {
        return parentOrgId;
    }

    /**
     * Set the parent org id.
     *
     * @param inParentOrgId
     *            the parent org id
     */
    protected void setParentOrgId(final Long inParentOrgId)
    {
        parentOrgId = inParentOrgId;
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
}
