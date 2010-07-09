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
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;

/**
 * This class represents an instance of a Enrollment item.
 */
@SuppressWarnings("serial")
@Entity
public class Enrollment extends DomainEntity implements Serializable
{
    /**
     * Private reference back to the person for mapper queries originating with
     * the Enrollment.
     */
    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId")
    private Person person;

    /**
     * Store the value of the school Name.
     */
    @Basic(optional = false)
    @OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinColumn(name = "schoolNameId")
    private BackgroundItem schoolName;

    /**
     * Store the value of the degree.
     */
    @Basic(optional = false)
    private String degree;

    /**
     * The areas of study that are contained in this enrollment.
     */
    @Basic(optional = false)
    @IndexColumn(name = "areasOfStudyIndex", base = 0)
    @OneToMany(fetch = FetchType.EAGER)
    @Cascade({ org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JoinTable(name = "Enrollment_AreasOfStudy",
    // inverse join column
    inverseJoinColumns = {
    // join column
    @JoinColumn(table = "BackgroundItem", name = "BackgroundItem_Id", referencedColumnName = "id") })
    private List<BackgroundItem> areasOfStudy = new LinkedList<BackgroundItem>();

    /**
     * Store the value of the grad date.
     */
    @Temporal(TemporalType.DATE)
    private Date gradDate;

    /**
     * The activities and societies that are contained in this enrollment.
     */
    @IndexColumn(name = "activitiesIndex", base = 0)
    @OneToMany(fetch = FetchType.EAGER)
    @Cascade({ org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JoinTable(name = "Enrollment_Activities",
    // inverse join columns
    inverseJoinColumns = {
    // inverse join column
    @JoinColumn(table = "BackgroundItem", name = "BackgroundItem_Id", referencedColumnName = "id") })
    private List<BackgroundItem> activities = new LinkedList<BackgroundItem>();

    /**
     * Store the value of the additional details.
     */
    @Basic()
    @Lob
    private String additionalDetails;

    /**
     * Default constructor responsible for assembling the Enrollment item.
     *
     * @param inPerson
     *            the person
     * @param inSchoolName
     *            the school name
     * @param inDegree
     *            the degree
     * @param inAreasOfStudy
     *            the area of study
     * @param inGradDate
     *            the date graduated
     * @param inActivities
     *            the activities
     * @param inAdditionalDetails
     *            the additional details
     */
    public Enrollment(final Person inPerson, final String inSchoolName, final String inDegree,
            final List<BackgroundItem> inAreasOfStudy, final Date inGradDate,
            final List<BackgroundItem> inActivities, final String inAdditionalDetails)
    {
        person = inPerson;
        schoolName = new BackgroundItem(inSchoolName, BackgroundItemType.SCHOOL_NAME);
        degree = inDegree;
        areasOfStudy = inAreasOfStudy;
        gradDate = inGradDate;
        activities = inActivities;
        additionalDetails = inAdditionalDetails;
    }

    /**
     * Default constructor for ORM.
     */
    @SuppressWarnings("unused")
    private Enrollment()
    {
    }

    /**
     * @return person who enrolled.
     */
    public Person getPerson()
    {
        return person;
    }

    /**
     * The company name setter.
     *
     * @param inSchoolName
     *            the company name to set
     */
    public void setSchoolName(final String inSchoolName)
    {
        this.schoolName = new BackgroundItem(inSchoolName, BackgroundItemType.SCHOOL_NAME);
    }

    /**
     * The degree setter.
     *
     * @param inDegree
     *            the degree to set
     */
    public void setDegree(final String inDegree)
    {
        this.degree = inDegree;
    }

    /**
     * The areas of study setter.
     *
     * @param inAreasOfStudy
     *            the areas of study to set
     */
    public void setAreasOfStudy(final List<BackgroundItem> inAreasOfStudy)
    {
        copyList(areasOfStudy, inAreasOfStudy);
    }

    /**
     * The grad date setter.
     *
     * @param inGradDate
     *            the graduate date to set
     */
    public void setGradDate(final Date inGradDate)
    {
        this.gradDate = inGradDate;
    }

    /**
     * The activities setter.
     *
     * @param inActivities
     *            the activities to set
     */
    public void setActivities(final List<BackgroundItem> inActivities)
    {
        copyList(activities, inActivities);
    }

    /**
     * The degree additionalDetails.
     *
     * @param inAdditionalDetails
     *            the additionalDetails to set
     */
    public void setAdditionalDetails(final String inAdditionalDetails)
    {
        this.additionalDetails = inAdditionalDetails;
    }

    /**
     * Get the school name.
     *
     * @return the school name
     */
    public String getSchoolName()
    {
        return schoolName == null ? null : schoolName.getName();
    }

    /**
     * Get the degree.
     *
     * @return the degree
     */
    public String getDegree()
    {
        return degree;
    }

    /**
     * Get the areas of study.
     *
     * @return the areas of study
     */
    public List<BackgroundItem> getAreasOfStudy()
    {
        return areasOfStudy;
    }

    /**
     * Get the graduation date.
     *
     * @return the graduation date
     */
    public Date getGradDate()
    {
        return gradDate;
    }

    /**
     * Get the activities.
     *
     * @return the activities
     */
    public List<BackgroundItem> getActivities()
    {
        return activities;
    }

    /**
     * Get the additionalDetails.
     *
     * @return the additionalDetails
     */
    public String getAdditionalDetails()
    {
        return additionalDetails;
    }

    /**
     * Get the list of background items for the specified background type.
     *
     * @param inEnrollmentType
     *            the enrollment type to get
     *
     * @return the list of background items
     */
    public List<BackgroundItem> getEnrollmentItems(final BackgroundItemType inEnrollmentType)

    {
        // this is assigned in the switch
        List<BackgroundItem> outBackgroundItems = null;

        switch (inEnrollmentType)
        {
        case AREA_OF_STUDY:
            outBackgroundItems = areasOfStudy;
            break;
        case ACTIVITY_OR_SOCIETY:
            outBackgroundItems = activities;
            break;
        default:
            throw new RuntimeException("Invalid enrollment type.");
        }

        return outBackgroundItems;

    }

    /**
     * Set the list of background items for the specified background type.
     *
     * @param inBackgroundItems
     *            the background items to persist.
     * @param inEnrollmentType
     *            the enrollment type
     */
    public void setEnrollmentItems(final List<BackgroundItem> inBackgroundItems,
            final BackgroundItemType inEnrollmentType)
    {
        switch (inEnrollmentType)
        {
        case AREA_OF_STUDY:
            setAreasOfStudy(inBackgroundItems);
            break;
        case ACTIVITY_OR_SOCIETY:
            setActivities(inBackgroundItems);
            break;
        default:
            throw new RuntimeException("Invalid enrollment type.");
        }
    }

    /**
     * Sets list of new BackgroundItems to an original collection of BackgroundItems.
     * Delete orpahans cascade type requires ownership of collection,
     * cannot just set new collection or hibernate gets angry so this just clears
     * collection and copys new values into original collection.
     * @param original original collection
     * @param newValues new collection
     */
    private void copyList(final List<BackgroundItem> original, final List<BackgroundItem> newValues)
    {
        //delete orpahans cascade requires ownership of collection
        //cannot just set new collection or hibernate gets angry.
        original.clear();
        for (BackgroundItem item : newValues)
        {
            original.add(item);
        }

    }
}
