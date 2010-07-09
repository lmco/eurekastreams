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

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.validator.Length;

/**
 * This class represents an instance of a job item.
 */
@SuppressWarnings("serial")
@Entity
public class Job extends DomainEntity implements Serializable
{
    /**
     * Private reference back to the person for mapper queries originating with the job.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId")
    private Person person;

    /**
     * Max characters for company name.
     */
    @Transient
    public static final int MAX_COMPANY_NAME_LENGTH = 50;

    /**
     * Max characters for company name.
     */
    @Transient
    public static final int MAX_DESCRIPTION_LENGTH = 200;

    /**
     * Max characters for title.
     */
    @Transient
    public static final int MAX_TITLE_LENGTH = 50;

    /**
     * Company length error message.
     */
    @Transient
    public static final String MAX_COMPANY_LENGTH_ERROR_MESSAGE = "Company Name must be between 1 and "
            + MAX_COMPANY_NAME_LENGTH + " characters.";

    /**
     * Title length error message.
     */
    @Transient
    public static final String MAX_TITLE_LENGTH_ERROR_MESSAGE = "Title must be between 1 and " + MAX_TITLE_LENGTH
            + " characters.";

    /**
     * Store the value of the Company Name.
     */
    @Basic(optional = false)
    @Length(min = 1, max = MAX_COMPANY_NAME_LENGTH, message = MAX_COMPANY_LENGTH_ERROR_MESSAGE)
    private String companyName;

    /**
     * Store the value of the title.
     */
    @Basic(optional = false)
    @Length(min = 1, max = MAX_TITLE_LENGTH, message = MAX_TITLE_LENGTH_ERROR_MESSAGE)
    private String title;

    /**
     * Store the value of the description.
     */
    @Basic(optional = false)
    @Lob
    private String description;

    /**
     * Store the value of the industry.
     */
    @Basic(optional = false)
    private String industry;

    /**
     * Store the value of the date from.
     */
    @Temporal(TemporalType.DATE)
    private Date dateFrom;

    /**
     * Store the value of the date to.
     */
    @Temporal(TemporalType.DATE)
    private Date dateTo;

    /**
     * Default constructor responsible for assembling the job item.
     *
     * @param inPerson
     *            the person
     * @param inCompanyName
     *            the company name
     * @param inIndustry
     *            the industry
     * @param inTitle
     *            the title
     * @param inDateFrom
     *            the start date
     * @param inDateTo
     *            the end date
     * @param inDescription
     *            the description
     */
    public Job(final Person inPerson, final String inCompanyName, final String inIndustry, final String inTitle,
            final Date inDateFrom, final Date inDateTo, final String inDescription)
    {
        person = inPerson;
        companyName = inCompanyName;
        industry = inIndustry;
        title = inTitle;
        dateFrom = inDateFrom;
        dateTo = inDateTo;
        description = inDescription;
    }

    /**
     * Default constructor for ORM.
     */
    public Job()
    {
    }

    /**
     * @return Person that had job.
     */
    public Person getPerson()
    {
        return person;
    }

    /**
     * The company name setter.
     *
     * @param inCompanyName
     *            the company name to set
     */
    public void setCompanyName(final String inCompanyName)
    {
        this.companyName = inCompanyName;
    }

    /**
     * The industry setter.
     *
     * @param inIndustry
     *            the industry to set
     */
    public void setIndustry(final String inIndustry)
    {
        this.industry = inIndustry;
    }

    /**
     * The title setter.
     *
     * @param inTitle
     *            the title to set
     */
    public void setTitle(final String inTitle)
    {
        this.title = inTitle;
    }

    /**
     * The date from setter.
     *
     * @param inDateFrom
     *            the date from to set
     */
    public void setDateFrom(final Date inDateFrom)
    {
        this.dateFrom = inDateFrom;
    }

    /**
     * The date to setter.
     *
     * @param inDateTo
     *            the date to to set
     */
    public void setDateTo(final Date inDateTo)
    {
        this.dateTo = inDateTo;
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
     * Get the owner of the job.
     *
     * @return the owner of the job.
     */
    public Person getOwner()
    {
        return person;
    }

    /**
     * Get the company name.
     *
     * @return the company name
     */
    public String getCompanyName()
    {
        return companyName;
    }

    /**
     * Get the industry.
     *
     * @return the industry
     */
    public String getIndustry()
    {
        return industry;
    }

    /**
     * Get the title.
     *
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Get the date from.
     *
     * @return the date from
     */
    public Date getDateFrom()
    {
        return dateFrom;
    }

    /**
     * Get the date to.
     *
     * @return the date to
     */
    public Date getDateTo()
    {
        return dateTo;
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

}
