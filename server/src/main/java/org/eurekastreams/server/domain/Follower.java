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

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import net.sf.gilead.pojo.java5.LightEntity;

/**
 * Object representing the person follower/following relationship.
 *
 */
@SuppressWarnings("serial")
@Entity
public class Follower extends LightEntity implements Serializable
{
    /**
     * Instance of FollowerPk (Composite primary key object) for this class.
     */
    @EmbeddedId
    private FollowerPk pk = null;

    /**
     * Constructor.
     *
     * @param inFollowerId
     *            Follower id.
     * @param inFollowingId
     *            Following id (id of Object being followed).
     */
    public Follower(final long inFollowerId, final long inFollowingId)
    {
        pk = new FollowerPk(inFollowerId, inFollowingId);
    }

    /**
     * Constructor (no-op for ORM).
     */
    @SuppressWarnings("unused")
    private Follower()
    {
    }

    /**
     * FollowerId getter.
     *
     * @return Follower id.
     */
    public long getFollowerId()
    {
        return pk.getFollowerId();
    }

    /**
     * FollowingId getter.
     *
     * @return Following id (id of Object being followed).
     */
    public long getFollowingId()
    {
        return pk.getFollowingId();
    }

    /**
     * Composite primary key for follower.
     *
     */
    @Embeddable
    public static class FollowerPk implements Serializable
    {
        /**
         * Follower id.
         */
        @Basic
        private long followerId = 0;

        /**
         * Following id (id of Object being followed).
         */
        @Basic
        private long followingId = 0;

        /**
         * Constructor.
         *
         * @param inFollowerId
         *            Follower id.
         * @param inFollowingId
         *            Following id (id of Object being followed).
         */
        public FollowerPk(final long inFollowerId, final long inFollowingId)
        {
            followerId = inFollowerId;
            followingId = inFollowingId;
        }

        /**
         * Constructor (no-op for ORM).
         */
        @SuppressWarnings("unused")
        private FollowerPk()
        {
        }

        /**
         * FollowerId getter.
         *
         * @return Follower id.
         */
        public long getFollowerId()
        {
            return followerId;
        }

        /**
         * FollowingId getter.
         *
         * @return Following id (id of Object being followed).
         */
        public long getFollowingId()
        {
            return followingId;
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
            hashCode ^= (new Long(followerId)).hashCode();
            hashCode ^= (new Long(followingId)).hashCode();
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
            if (!(obj instanceof FollowerPk))
            {
                return false;
            }
            FollowerPk target = (FollowerPk) obj;

            return (target.followingId == this.followingId) && (target.followerId == this.followerId);
        }
    }

    /**
     * Enumeration for follower/following relationship status.
     *
     */
    public static enum FollowerStatus implements Serializable
    {
        /**
         * Follower/Following relationship is not specified.
         */
        NOTSPECIFIED,

        /**
         * Follower/Following relationship is not allowed.
         */
        DISABLED,

        /**
         * Follower/Following relationship is established.
         */
        FOLLOWING,

        /**
         * Follower/Following relationship is not established.
         */
        NOTFOLLOWING,
    }
}
