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
package org.eurekastreams.server.search.modelview;

import java.io.Serializable;
import java.util.Map;

import org.eurekastreams.commons.search.modelview.ModelView;

/**
 * A lightweight view of an Organization containing everything needed for display of a search result of an Organization.
 */
public class OrganizationModelView extends ModelView implements Serializable
{
    /** Fingerprint. */
    private static final long serialVersionUID = -802463140572809635L;

    /**
     * form Key.
     */
    public static final String ID_KEY = "id";
    /**
     * form Key.
     */
    public static final String NAME_KEY = "name";
    /**
     * form Key.
     */
    public static final String SHORT_NAME_KEY = "shortName";
    /**
     * form Key.
     */
    public static final String ORG_PARENT_KEY = "orgParent";
    /**
     * form Key.
     */
    public static final String COORDINATORS_KEY = "coordinators";
    /**
     * form Key.
     */
    public static final String LEADERSHIP_KEY = "leaders";
    /**
     * form Key.
     */
    public static final String DESCRIPTION_KEY = "description";
    /**
     * form Key.
     */
    public static final String URL_KEY = "url";
    /**
     * form Key.
     */
    public static final String ALLOW_GROUP_CREATION_KEY = "allUsersCanCreateGroups";

    /**
     * Parent organization id.
     */
    private long parentOrganizationId = UNINITIALIZED_LONG_VALUE;

    /**
     * The organization name.
     */
    private String name = UNINITIALIZED_STRING_VALUE;

    /**
     * The organization description.
     */
    private String description = UNINITIALIZED_STRING_VALUE;

    /**
     * The number of followers for this organization.
     */
    private int followersCount = UNINITIALIZED_INTEGER_VALUE;

    /**
     * The number of employees in this organization (recursively).
     */
    private int descendantEmployeeCount = UNINITIALIZED_INTEGER_VALUE;

    /**
     * The number of child groups in this organization (recursively).
     */
    private int descendantGroupCount = UNINITIALIZED_INTEGER_VALUE;

    /**
     * The number of updates for this org.
     */
    private int updatesCount = UNINITIALIZED_INTEGER_VALUE;

    /**
     * The number of child organizations in this organization (directly, non-recursively).
     */
    private int childOrganizationCount = UNINITIALIZED_INTEGER_VALUE;

    /**
     * The shortname of the organization.
     */
    private String shortName = UNINITIALIZED_STRING_VALUE;

    /**
     * The avatar id.
     */
    private String avatarId = UNINITIALIZED_STRING_VALUE;

    /**
     * The org's overview.
     */
    private String overview;

    /**
     * Stream id for this group.
     */
    private long streamId = UNINITIALIZED_LONG_VALUE;

    /**
     * Banner id.
     */
    private String bannerId = UNINITIALIZED_STRING_VALUE;

    /**
     * Get the name of this entity.
     *
     * @return the name of this entity
     */
    @Override
    protected String getEntityName()
    {
        return "Organization";
    }

    /**
     * Load this object's properties from the input Map.
     *
     * @param properties
     *            the Map of the properties to load
     */
    @Override
    public void loadProperties(final Map<String, Object> properties)
    {
        // let the parent class get its properties first
        super.loadProperties(properties);

        if (properties.containsKey("name"))
        {
            setName((String) properties.get("name"));
        }
        if (properties.containsKey("description"))
        {
            setDescription((String) properties.get("description"));
        }
        if (properties.containsKey("followersCount"))
        {
            setFollowersCount((Integer) properties.get("followersCount"));
        }
        if (properties.containsKey("descendantEmployeeCount"))
        {
            setDescendantEmployeeCount((Integer) properties.get("descendantEmployeeCount"));
        }
        if (properties.containsKey("updatesCount"))
        {
            setUpdatesCount((Integer) properties.get("updatesCount"));
        }
        if (properties.containsKey("descendantGroupCount"))
        {
            setDescendantGroupCount((Integer) properties.get("descendantGroupCount"));
        }
        if (properties.containsKey("childOrganizationCount"))
        {
            setChildOrganizationCount((Integer) properties.get("childOrganizationCount"));
        }
        if (properties.containsKey("shortName"))
        {
            setShortName(((String) properties.get("shortName")));
        }
        if (properties.containsKey("avatarId"))
        {
            setAvatarId(((String) properties.get("avatarId")));
        }
        if (properties.containsKey("overview"))
        {
            setOverview((String) properties.get("overview"));
        }
        if (properties.containsKey("streamId"))
        {
            setStreamId((Long) properties.get("streamId"));
        }
        if (properties.containsKey("parentOrganizationId"))
        {
            setParentOrganizationId((Long) properties.get("parentOrganizationId"));
        }
        if (properties.containsKey("bannerId"))
        {
            setBannerId((String) properties.get("bannerId"));
        }
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
     *            the overview
     */
    public void setOverview(final String inOverview)
    {
        overview = inOverview;
    }

    /**
     * Get the name of the organization.
     *
     * @return the name of the organization
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of the organization.
     *
     * @param inName
     *            the name to set
     */
    public void setName(final String inName)
    {
        name = inName;
    }

    /**
     * Get the description of the organization.
     *
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set the description of the organization.
     *
     * @param inDescription
     *            the description to set
     */
    public void setDescription(final String inDescription)
    {
        description = inDescription;
    }

    /**
     * Get the follower count.
     *
     * @return the followersCount
     */
    public int getFollowersCount()
    {
        return followersCount;
    }

    /**
     * Set the follower count.
     *
     * @param inFollowersCount
     *            the followersCount to set
     */
    public void setFollowersCount(final int inFollowersCount)
    {
        followersCount = inFollowersCount;
    }

    /**
     * Get the shortname.
     *
     * @return the shortname.
     */
    public String getShortName()
    {
        return shortName;
    }

    /**
     * Set the shortname.
     *
     * @param inShortName
     *            the shortname.
     */
    public void setShortName(final String inShortName)
    {
        shortName = inShortName;
    }

    /**
     * Get the recursive employee count.
     *
     * @return the employeeCount
     */
    public int getDescendantEmployeeCount()
    {
        return descendantEmployeeCount;
    }

    /**
     * Set the recursive employee count.
     *
     * @param inEmployeesCount
     *            the employeeCount to set
     */
    public void setDescendantEmployeeCount(final int inEmployeesCount)
    {
        descendantEmployeeCount = inEmployeesCount;
    }

    /**
     * Get the recursive group count.
     *
     * @return the groupCount
     */
    public int getDescendantGroupCount()
    {
        return descendantGroupCount;
    }

    /**
     * Set the recursive group count.
     *
     * @param inGroupsCount
     *            the groupCount to set
     */
    public void setDescendantGroupCount(final int inGroupsCount)
    {
        descendantGroupCount = inGroupsCount;
    }

    /**
     * Get the organization count (non-recursive).
     *
     * @return the organizationCount
     */
    public int getChildOrganizationCount()
    {
        return childOrganizationCount;
    }

    /**
     * Set the organization count (non-recursive).
     *
     * @param inOrganizationsCount
     *            the organizationCount to set
     */
    public void setChildOrganizationCount(final int inOrganizationsCount)
    {
        childOrganizationCount = inOrganizationsCount;
    }

    /**
     * Get the org's avatar id.
     *
     * @return the avatarId
     */
    public String getAvatarId()
    {
        return avatarId;
    }

    /**
     * Set the org's avatar id.
     *
     * @param inAvatarId
     *            the avatarId to set
     */
    public void setAvatarId(final String inAvatarId)
    {
        avatarId = inAvatarId;
    }

    /**
     * Get the number of updates for this org.
     *
     * @return the updatesCount
     */
    public int getUpdatesCount()
    {
        return updatesCount;
    }

    /**
     * Set the number of updates for this org.
     *
     * @param inUpdatesCount
     *            the updatesCount to set
     */
    public void setUpdatesCount(final int inUpdatesCount)
    {
        updatesCount = inUpdatesCount;
    }

    /**
     * Set the entity id.
     *
     * @param inEntityId
     *            the entity id of the organization.
     */
    // TODO: pull this out - this is temporary
    @Override
    public void setEntityId(final long inEntityId)
    {
        super.setEntityId(inEntityId);
    }

    /**
     * @return the streamId
     */
    public long getStreamId()
    {
        return streamId;
    }

    /**
     * @param inStreamId
     *            the streamId to set
     */
    public void setStreamId(final long inStreamId)
    {
        streamId = inStreamId;
    }

    /**
     * @return the parentOrganizationId
     */
    public long getParentOrganizationId()
    {
        return parentOrganizationId;
    }

    /**
     * @param inParentOrganizationId
     *            the inParentOrganizationId to set
     */
    public void setParentOrganizationId(final long inParentOrganizationId)
    {
        parentOrganizationId = inParentOrganizationId;
    }

    /**
     * Get the organization banner id.
     *
     * @return the organization banner id
     */
    public String getBannerId()
    {
        return bannerId;
    }

    /**
     * Set the organization banner id.
     *
     * @param inBannerId
     *            the banner id to set
     */
    public void setBannerId(final String inBannerId)
    {
        bannerId = inBannerId;
    }
}
