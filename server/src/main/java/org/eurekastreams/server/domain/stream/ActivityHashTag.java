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

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import net.sf.gilead.pojo.java5.LightEntity;

/**
 * Join table entity for a HashTag that belongs to an Activity.
 */
@Entity
@Table(name = "Activity_HashTags")
public class ActivityHashTag extends LightEntity implements Serializable
{
    /**
     * Serial UUID.
     */
    private static final long serialVersionUID = 2291950743360988829L;

    /**
     * Instance of ActivityHashTagPk (Composite primary key object) for this class.
     */
    @EmbeddedId
    private ActivityHashTagPk pk = null;

    /**
     * Constructor.
     *
     * @param inHashTagId
     *            hashtag id.
     * @param inActivityId
     *            Activity id.
     */
    public ActivityHashTag(final long inActivityId, final long inHashTagId)
    {
        pk = new ActivityHashTagPk(inActivityId, inHashTagId);
    }

    /**
     * Constructor (no-op for ORM).
     */
    @SuppressWarnings("unused")
    private ActivityHashTag()
    {
    }

    /**
     * HashTag id getter.
     *
     * @return Person id.
     */
    public long getHashTagId()
    {
        return pk.getHashTagId();
    }

    /**
     * Activity id getter.
     *
     * @return Activity id.
     */
    public long getActivityId()
    {
        return pk.getActivityId();
    }

    /**
     * Composite primary key for ActivityHashTag.
     *
     */
    @Embeddable
    public static class ActivityHashTagPk implements Serializable
    {
        /**
         * Serial version UUID.
         */
        private static final long serialVersionUID = -1155941706694951072L;

        /**
         * hashtag id.
         */
        @Basic
        private long hashTagId = 0;

        /**
         * activity id.
         */
        @Basic
        private long activityId = 0;

        /**
         * Constructor.
         *
         * @param inHashTagId
         *            hash tag id.
         * @param inActivityId
         *            Activity id.
         */
        public ActivityHashTagPk(final long inActivityId, final long inHashTagId)
        {
            hashTagId = inHashTagId;
            activityId = inActivityId;
        }

        /**
         * Constructor (no-op for ORM).
         */
        @SuppressWarnings("unused")
        private ActivityHashTagPk()
        {
        }

        /**
         * hashtag id getter.
         *
         * @return hashtag id.
         */
        public long getHashTagId()
        {
            return hashTagId;
        }

        /**
         * Activity id getter.
         *
         * @return Activity id.
         */
        public long getActivityId()
        {
            return activityId;
        }

        /**
         * Override hashCode for comparing pk object.
         *
         * @return The generated hashcode.
         */
        @Override
        public int hashCode()
        {
            int hashCode = 0;
            hashCode ^= (new Long(hashTagId)).hashCode();
            hashCode ^= (new Long(activityId)).hashCode();
            return hashCode;
        }

        /**
         * Override equals for comparing pk object.
         *
         * @param obj
         *            The object to compare to this one.
         * @return True if obj is equal to this one, false otherwise.
         */
        @Override
        public boolean equals(final Object obj)
        {
            if (!(obj instanceof ActivityHashTagPk))
            {
                return false;
            }
            ActivityHashTagPk target = (ActivityHashTagPk) obj;

            return (target.activityId == this.activityId) && (target.hashTagId == this.hashTagId);
        }
    }
}