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
import javax.persistence.Column;
import javax.persistence.Entity;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Represents a recommendation made by an author about a subject.
 */
@Entity
public class Recommendation extends DomainEntity implements Serializable
{
    /**
     * Generated serial version id.
     */
    private static final long serialVersionUID = 7542884684818308400L;

    /**
     * The OpenSocial Id of the subject of the recommendation.
     */
    @Basic(optional = false)
    private String subjectOpenSocialId;

    /**
     * The OpenSocial Id of the author.
     */
    @Basic(optional = false)
    private String authorOpenSocialId;

    /**
     * Max size for recommendation text.
     */
    private static final int MAX_SIZE = 500;

    /**
     * The text of the recommendation.
     */
    @Column(nullable = false, length = MAX_SIZE)
    private String text;

    /**
     * The creation date/time of the recommendation.
     */
    @Basic(optional = false)
    private Date date;

    /**
     * Public constructor for ORM and ResourcePersistenceStrategy.
     */
    public Recommendation()
    {
    }

    /**
     * Constructor for API.
     *
     * @param inSubjectOSId
     *            the subject's OpenSocialId
     * @param inAuthorOSId
     *            the author's OpenSocialId
     * @param recoText
     *            the text of the recommendation
     */
    public Recommendation(final String inSubjectOSId, final String inAuthorOSId, final String recoText)
    {
        subjectOpenSocialId = inSubjectOSId;
        authorOpenSocialId = inAuthorOSId;
        text = recoText;
        date = new Date();
    }

    /**
     * Getter.
     *
     * @return the subjectOpenSocialId
     */
    public String getSubjectOpenSocialId()
    {
        return subjectOpenSocialId;
    }

    /**
     * Setter.
     *
     * @param subjectOSId
     *            the subjectOpenSocialId to set
     */
    public void setSubjectOpenSocialId(final String subjectOSId)
    {
        this.subjectOpenSocialId = subjectOSId;
    }

    /**
     * Getter.
     *
     * @return the authorOpenSocialId
     */
    public String getAuthorOpenSocialId()
    {
        return authorOpenSocialId;
    }

    /**
     * Setter.
     *
     * @param authorOSId
     *            the authorOpenSocialId to set
     */
    public void setAuthorOpenSocialId(final String authorOSId)
    {
        this.authorOpenSocialId = authorOSId;
    }

    /**
     * Getter.
     *
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * Setter.
     *
     * @param inText
     *            the text to set
     */
    public void setText(final String inText)
    {
        this.text = inText;
    }

    /**
     * Getter.
     *
     * @return the date
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * Setter.
     *
     * @param inDate
     *            the date to set
     */
    public void setDate(final Date inDate)
    {
        this.date = inDate;
    }
}
