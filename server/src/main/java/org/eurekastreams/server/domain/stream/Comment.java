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
package org.eurekastreams.server.domain.stream;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.domain.Person;
import org.hibernate.validator.Length;

/**
 * Entity representing a comment on an Activity.
 * 
 */
@Entity
public class Comment extends DomainEntity implements Serializable
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 7132022993005124352L;

    /**
     * Max character count for comment body.
     */
    private static final int MAX_BODY_LENGTH = 250;

    /**
     * Max body length error message.
     */
    static final String MAX_BODY_LENGTH_MESSAGE = "Comment body must be between 0 and " + MAX_BODY_LENGTH
            + " characters.";

    /**
     * Author of comment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Basic(optional = false)
    @JoinColumn(name = "authorPersonId", nullable = false)
    private Person author;

    /**
     * Target activity of comment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Basic(optional = false)
    @JoinColumn(name = "activityId", nullable = false)
    private Activity target;

    /**
     * Time comment was made.
     */
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeSent = new Date();

    /**
     * Body of the comment.
     */
    @Length(min = 0, max = MAX_BODY_LENGTH, message = MAX_BODY_LENGTH_MESSAGE)
    @Basic(optional = false)
    private String body;
    
    /**
     * Private constructor for ORM.
     */
    private Comment()
    {
        //no-op.
    }

    /**
     * Constructor.
     * 
     * @param inAuthor
     *            Author of the comment.
     * @param inTarget
     *            Target Activity of the comment.
     * @param inBody
     *            Body of the comment.
     */
    public Comment(final Person inAuthor, final Activity inTarget, final String inBody)
    {
        setAuthor(inAuthor);
        setTarget(inTarget);
        setBody(inBody);
    }

    /**
     * @return the author
     */
    public Person getAuthor()
    {
        return author;
    }

    /**
     * @param inAuthor
     *            the author to set
     */
    public void setAuthor(final Person inAuthor)
    {
        this.author = inAuthor;
    }

    /**
     * @return the target Activity
     */
    public Activity getTarget()
    {
        return target;
    }

    /**
     * @param inTarget
     *            the target to set
     */
    public void setTarget(final Activity inTarget)
    {
        this.target = inTarget;
    }

    /**
     * @return the timeSent
     */
    public Date getTimeSent()
    {
        return timeSent;
    }

    /**
     * @param inTimeSent
     *            the timeSent to set
     */
    public void setTimeSent(final Date inTimeSent)
    {
        this.timeSent = inTimeSent;
    }

    /**
     * @return the body
     */
    public String getBody()
    {
        return body;
    }

    /**
     * @param inBody
     *            the body to set
     */
    public void setBody(final String inBody)
    {
        this.body = inBody;
    }

}
