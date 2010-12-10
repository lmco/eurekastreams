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
import java.util.Set;

/**
 * Represents a group.
 */
public interface DomainGroupEntity extends Serializable, Bannerable
{
    /**
     * @return the group's id.
     */
    long getId();

    /**
     * @return the name.
     */
    String getName();

    /**
     * @param inName
     *            new name for the group
     */
    void setName(final String inName);

    /**
     * @return the parentOrganization
     */
    Organization getParentOrganization();

    /**
     * @param inParentOrganization
     *            the parentOrganization to set
     */
    void setParentOrganization(final Organization inParentOrganization);


    /**
     * @return whether this group is public
     */
    boolean isPublicGroup();

    /**
     * check to see if the specified account id is a coordinator for this group.
     *
     * @param account
     *            to check.
     * @return if they're a coordinator.
     */
    boolean isCoordinator(final String account);

    /**
     * Getter for group short name.
     *
     * @return the shortName
     */
    String getShortName();

    /**
     * @return list of coordinators.
     */
    Set<Person> getCoordinators();

    /**
     * Get avatar x coord.
     *
     * @return avatar x coord.
     */
    Integer getAvatarCropX();

    /**
     * Set avatar x coord.
     *
     * @param value
     *            x coord.
     */
    void setAvatarCropX(final Integer value);

    /**
     * Get avatar y coord.
     *
     * @return avatar y coord.
     */
    Integer getAvatarCropY();

    /**
     * Set avatar y coord.
     *
     * @param value
     *            y coord.
     */
    void setAvatarCropY(final Integer value);

    /**
     * Get avatar crop size.
     *
     * @return avatar crop size.
     */
    Integer getAvatarCropSize();

    /**
     * Set avatar crop size.
     *
     * @param value
     *            crop size.
     */
    void setAvatarCropSize(final Integer value);

    /**
     * @return the avatar Id
     */
    String getAvatarId();

    /**
     * @param inAvatarId
     *            the avatar to set
     */
    void setAvatarId(final String inAvatarId);

    /**
     * Get the mission statement of the group.
     *
     * @return the mission statement
     */
    String getDescription();
}
