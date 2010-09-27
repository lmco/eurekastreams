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

import java.util.List;
import java.util.Set;

/**
 * Represents a restricted view on a group. Since the viewer is not allowed to see everything, this only has some of the
 * information from a full group.
 */
public class RestrictedDomainGroup implements DomainGroupEntity, AvatarEntity
{
    /**
     *
     */
    private static final long serialVersionUID = 1457482005827433651L;

    /**
     * The group's id.
     */
    private long id;

    /**
     * banner id for this group.
     */
    private String bannerId;

    /**
     * Entity id of the owner of the banner associated with this group.
     */
    private Long bannerEntityId;

    /**
     * The name of the group.
     */
    private String name;

    /**
     * The short name of the group.
     */
    private String shortName;

    /**
     * The parent organization of this group.
     */
    private Organization parentOrganization;

    /**
     * avatarCropX.
     */
    private Integer avatarCropX;

    /**
     * avatarCropY.
     */
    private Integer avatarCropY;

    /**
     * avatarCropSize.
     */
    private Integer avatarCropSize;

    /**
     * avatar id image for this user.
     */
    private String avatarId;

    /**
     * Constructor. Need for Gilead serialization.
     */
    public RestrictedDomainGroup()
    {

    }

    /**
     * A copy constructor that provides a limited version of a full group.
     *
     * @param group
     *            the full group
     */
    public RestrictedDomainGroup(final DomainGroupEntity group)
    {
        id = group.getId();
        bannerId = group.getBannerId();
        name = group.getName();
        shortName = group.getShortName();
        parentOrganization = group.getParentOrganization();
        avatarId = group.getAvatarId();
        avatarCropSize = group.getAvatarCropSize();
        avatarCropY = group.getAvatarCropY();
        avatarCropX = group.getAvatarCropX();
    }

    /**
     * @return the group's id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param inId
     *            the id of the group
     */
    public void setId(final long inId)
    {
        id = inId;
    }

    /**
     * @return the id of the group's banner
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
     * @return the group's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param inName
     *            new name
     */
    public void setName(final String inName)
    {
        name = (null == inName) ? "" : inName;
    }

    /**
     * @return the group's parent organization
     */
    public Organization getParentOrganization()
    {
        return parentOrganization;
    }

    /**
     * @param inParentOrganization
     *            the parentOrganization to set
     */
    public void setParentOrganization(final Organization inParentOrganization)
    {
        parentOrganization = inParentOrganization;
    }

    /**
     * @return always null
     */
    public List<Task> getCompletedTasks()
    {
        return null;
    }

    /**
     * @return always null
     */
    public Set<Person> getCoordinators()
    {
        return null;
    }

    /**
     * @return the short name
     */
    public String getShortName()
    {
        return shortName;
    }

    /**
     * @param inShortName
     *            the new short name for the group
     */
    public void setShortName(final String inShortName)
    {
        shortName = (null == inShortName) ? "" : inShortName;
    }

    /**
     * @param tabGroupType
     *            the type of tab group to return
     * @return always null
     */
    public List<Tab> getTabs(final TabGroupType tabGroupType)
    {
        return null;
    }

    /**
     * @param account
     *            ignored
     * @return always false
     */
    public boolean isCoordinator(final String account)
    {
        return false;
    }

    /**
     * @return always false
     */
    public boolean isPublicGroup()
    {
        return false;
    }

    /**
     * @return the avatarCropX
     */
    public Integer getAvatarCropX()
    {
        return avatarCropX;
    }

    /**
     * @param inAvatarCropX
     *            the avatarCropX to set
     */
    public void setAvatarCropX(final Integer inAvatarCropX)
    {
        // Restricted does nothing
    }

    /**
     * @return the avatarCropY
     */
    public Integer getAvatarCropY()
    {
        return avatarCropY;
    }

    /**
     * @param inAvatarCropY
     *            the avatarCropY to set
     */
    public void setAvatarCropY(final Integer inAvatarCropY)
    {
        // Restricted does nothing
    }

    /**
     * @return the avatarCropSize
     */
    public Integer getAvatarCropSize()
    {
        return avatarCropSize;
    }

    /**
     * @param inAvatarCropSize
     *            the avatarCropSize to set
     */
    public void setAvatarCropSize(final Integer inAvatarCropSize)
    {
        // Restricted does nothing
    }

    /**
     * @return the avatarId
     */
    public String getAvatarId()
    {
        return avatarId;
    }

    /**
     * @param inAvatarId
     *            the avatarId to set
     */
    public void setAvatarId(final String inAvatarId)
    {
        // Restricted does nothing
    }

    /**
     * No mission statement, this is used for help pages.
     *
     * @return empty string
     */
    @Override
    public String getDescription()
    {
        return "";
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
    }
}
