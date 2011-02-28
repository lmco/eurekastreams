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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.annotations.IndexColumn;

/**
 * Represents the background of a person using the system.
 */
@SuppressWarnings("serial")
@Entity
public class Background extends DomainEntity implements Serializable
{
    /**
     * Private reference back to the person for mapper queries originating with the background.
     */
    @SuppressWarnings("unused")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId")
    private Person person;

    /**
     * The skills that are contained in this background.
     */
    @IndexColumn(name = "skillsIndex", base = 0)
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinTable(name = "Background_Skills", inverseJoinColumns = {
    // inverse join column
    @JoinColumn(table = "BackgroundItem", name = "BackgroundItem_Id", referencedColumnName = "id") })
    private List<BackgroundItem> skills;

    /**
     * The interests that are contained in this background.
     */
    @IndexColumn(name = "interestIndex", base = 0)
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinTable(name = "Background_Interests", inverseJoinColumns = {
    // inverse join column
    @JoinColumn(table = "BackgroundItem", name = "BackgroundItem_Id", referencedColumnName = "id") })
    private List<BackgroundItem> interests;

    /**
     * The affiliations that are contained in this background.
     */
    @IndexColumn(name = "affiliationIndex", base = 0)
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinTable(name = "Background_Affiliations", inverseJoinColumns = {
    // inverse join column
    @JoinColumn(table = "BackgroundItem", name = "BackgroundItem_Id", referencedColumnName = "id") })
    private List<BackgroundItem> affiliations;

    /**
     * The honors that are contained in this background.
     */
    @IndexColumn(name = "honorsIndex", base = 0)
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinTable(name = "Background_Honors", inverseJoinColumns = {
    // inverse join column
    @JoinColumn(table = "BackgroundItem", name = "BackgroundItem_Id", referencedColumnName = "id") })
    private List<BackgroundItem> honors;

    /**
     * Private constructor.
     * 
     * unused warning is suppressed because it actually is used in reflection.
     */
    @SuppressWarnings("unused")
    private Background()
    {
    }

    /**
     * Public constructor.
     * 
     * @param inPerson
     *            to create the background for.
     */
    public Background(final Person inPerson)
    {
        person = inPerson;
    }

    /**
     * Get the list of background items for the specified background type.
     * 
     * @param inBackgroundType
     *            get all items of this type.
     * @return the list of background items
     */
    public List<BackgroundItem> getBackgroundItems(final BackgroundItemType inBackgroundType)

    {
        // this is assigned in the switch
        List<BackgroundItem> outBackgroundItems;

        switch (inBackgroundType)
        {
        case SKILL:
            outBackgroundItems = skills;
            break;
        case INTEREST:
            outBackgroundItems = interests;
            break;
        case AFFILIATION:
            outBackgroundItems = affiliations;
            break;
        case HONOR:
            outBackgroundItems = honors;
            break;
        default:
            String msg = inBackgroundType + " is not handled";
            throw new IllegalArgumentException(msg);
        }

        return outBackgroundItems;

    }

    /**
     * Set the list of background items for the specified background type.
     * 
     * @param inBackgroundItems
     *            the background items to persist.
     * 
     * @param inBackgroundType
     *            the items are of this type.
     */
    public void setBackgroundItems(final List<BackgroundItem> inBackgroundItems,
            final BackgroundItemType inBackgroundType)
    {
        switch (inBackgroundType)
        {
        case SKILL:
            skills = inBackgroundItems;
            break;
        case INTEREST:
            interests = inBackgroundItems;
            break;
        case AFFILIATION:
            affiliations = inBackgroundItems;
            break;
        case HONOR:
            honors = inBackgroundItems;
            break;
        default:
            String msg = inBackgroundType + " is not handled";
            throw new IllegalArgumentException(msg);
        }
    }

    // private getters and setters for serialization.
    /**
     * @return the skills
     */
    private List<BackgroundItem> getSkills()
    {
        return skills;
    }

    /**
     * @param inSkills
     *            the skills to set
     */
    private void setSkills(final List<BackgroundItem> inSkills)
    {
        skills = inSkills;
    }

    /**
     * @return the interests
     */
    private List<BackgroundItem> getInterests()
    {
        return interests;
    }

    /**
     * @param inInterests
     *            the interests to set
     */
    private void setInterests(final List<BackgroundItem> inInterests)
    {
        interests = inInterests;
    }

    /**
     * @return the affiliations
     */
    private List<BackgroundItem> getAffiliations()
    {
        return affiliations;
    }

    /**
     * @param inAffiliations
     *            the affiliations to set
     */
    private void setAffiliations(final List<BackgroundItem> inAffiliations)
    {
        affiliations = inAffiliations;
    }

    /**
     * @return the honors
     */
    private List<BackgroundItem> getHonors()
    {
        return honors;
    }

    /**
     * @param inHonors
     *            the honors to set
     */
    private void setHonors(final List<BackgroundItem> inHonors)
    {
        honors = inHonors;
    }
}
