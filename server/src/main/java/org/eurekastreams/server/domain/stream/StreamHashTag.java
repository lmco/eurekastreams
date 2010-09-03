/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;

/**
 * Record of an activity's hashtag in a stream.
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "streamEntityUniqueKey", "activityId", "hashTagId" }) })
public class StreamHashTag extends DomainEntity implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1389589224766011071L;

    /**
     * Hash tag.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashTagId")
    private HashTag hashTag;

    /**
     * Activity responsible for this hashtag.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activityId")
    private Activity activity;

    /**
     * Unique key of the stream entity that the hashtag was posted under.
     */
    private String streamEntityUniqueKey;

    /**
     * Type of stream that the hashtag is applied to.
     */
    @Enumerated(EnumType.STRING)
    private ScopeType streamScopeType;

    /**
     * Date of the activity.
     */
    private Date activityDate;

    /**
     * Empty constructor for ORM.
     */
    @SuppressWarnings("unused")
    private StreamHashTag()
    {
    }

    /**
     * Constructor.
     *
     * @param inHashTag
     *            the hash tag
     * @param inActivity
     *            the activity
     * @param inStreamEntityUniqueKey
     *            unique key of the stream entity
     * @param inStreamScopeType
     *            the stream entity type
     */
    public StreamHashTag(final HashTag inHashTag, final Activity inActivity, final String inStreamEntityUniqueKey,
            final ScopeType inStreamScopeType)
    {
        hashTag = inHashTag;
        activity = inActivity;
        setStreamEntityUniqueKey(inStreamEntityUniqueKey);
        streamScopeType = inStreamScopeType;
        activityDate = inActivity.getPostedTime();
    }

    /**
     * Get the hashtag.
     *
     * @return the hashtag
     */
    public HashTag getHashTag()
    {
        return hashTag;
    }

    /**
     * Set the hashtag.
     *
     * @param inHashTag
     *            the hashtag to set
     */
    @SuppressWarnings("unused")
    private void setHashTag(final HashTag inHashTag)
    {
        hashTag = inHashTag;
    }

    /**
     * Get the activity.
     *
     * @return the activity
     */
    public Activity getActivity()
    {
        return activity;
    }

    /**
     * Set the activity.
     *
     * @param inActivity
     *            the activity
     */
    @SuppressWarnings("unused")
    private void setActivity(final Activity inActivity)
    {
        activity = inActivity;
    }

    /**
     * Get the stream scope type.
     *
     * @return the stream scope type
     */
    public ScopeType getStreamScopeType()
    {
        return streamScopeType;
    }

    /**
     * Set th stream scope type.
     *
     * @param inStreamScopeType
     *            the stream entity type
     */
    @SuppressWarnings("unused")
    private void setStreamEntityType(final ScopeType inStreamScopeType)
    {
        streamScopeType = inStreamScopeType;
    }

    /**
     * Get the activity date.
     *
     * @return the activity date
     */
    public Date getActivityDate()
    {
        return activityDate;
    }

    /**
     * Set the activity date.
     *
     * @param inActivityDate
     *            the activity date
     */
    @SuppressWarnings("unused")
    private void setActivityDate(final Date inActivityDate)
    {
        activityDate = inActivityDate;
    }

    /**
     * Set the unique key of the stream entity that the hashtag was posted under.
     *
     * @param inStreamEntityUniqueKey
     *            the streamEntityUniqueKey to set
     */
    private void setStreamEntityUniqueKey(final String inStreamEntityUniqueKey)
    {
        streamEntityUniqueKey = inStreamEntityUniqueKey;
    }

    /**
     * Get the unique key of the stream entity that the hashtag was posted under.
     *
     * @return the streamEntityUniqueKey
     */
    public String getStreamEntityUniqueKey()
    {
        return streamEntityUniqueKey;
    }
}
