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
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

import net.sf.gilead.pojo.gwt.LightEntity;

import org.eurekastreams.server.domain.EntityCacheUpdater;

/**
 * Object representing the person "liked" activities for users.
 *
 */
@SuppressWarnings("serial")
@Entity
public class LikedActivity extends LightEntity implements Serializable
{
    /**
     * Instance of LikedActivityPk (Composite primary key object) for this class.
     */
    @EmbeddedId
    private LikedActivityPk pk = null;

    /**
     * Constructor.
     *
     * @param inPersonId
     *            Person id.
     * @param inActivityId
     *            Activity id.
     */
    public LikedActivity(final long inPersonId, final long inActivityId)
    {
        pk = new LikedActivityPk(inPersonId, inActivityId);
    }

    /**
     * Constructor (no-op for ORM).
     */
    @SuppressWarnings("unused")
    private LikedActivity()
    {
    }

    /**
     * Person id getter.
     *
     * @return Person id.
     */
    public long getPersonId()
    {
        return pk.getPersonId();
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

    // ----------------------------------------------------
    // ------------------ CACHE UPDATING ------------------

    /**
     * The entity cache updater.
     */
    private static transient EntityCacheUpdater<LikedActivity> entityCacheUpdater;

    /**
     * Setter for the static EntityCacheUpdater.
     *
     * @param inEntityCacheUpdater
     *            the entityCacheUpdater to set
     */
    public static void setEntityCacheUpdater(final EntityCacheUpdater<LikedActivity> inEntityCacheUpdater)
    {
        entityCacheUpdater = inEntityCacheUpdater;
    }

    /**
     * Call-back after a LikedActivity entity has been updated. This tells the static cacheUpdater if set.
     */
    @SuppressWarnings("unused")
    @PostUpdate
    private void onPostUpdate()
    {
        if (entityCacheUpdater != null)
        {
            entityCacheUpdater.onPostUpdate(this);
        }
    }

    /**
     * Call-back after the entity has been persisted. This tells the static cacheUpdater if set.
     */
    @SuppressWarnings("unused")
    @PostPersist
    private void onPostPersist()
    {
        if (entityCacheUpdater != null)
        {
            entityCacheUpdater.onPostPersist(this);
        }
    }

    // ---------------- END CACHE UPDATING ----------------
    // ----------------------------------------------------

    /**
     * Composite primary key for LikedActivity.
     *
     */
    @Embeddable
    public static class LikedActivityPk implements Serializable
    {
        /**
         * person id.
         */
        @Basic
        private long personId = 0;

        /**
         * activity id.
         */
        @Basic
        private long activityId = 0;

        /**
         * Constructor.
         *
         * @param inPersonId
         *            Person id.
         * @param inActivityId
         *            Activity id.
         */
        public LikedActivityPk(final long inPersonId, final long inActivityId)
        {
            personId = inPersonId;
            activityId = inActivityId;
        }

        /**
         * Constructor (no-op for ORM).
         */
        @SuppressWarnings("unused")
        private LikedActivityPk()
        {
        }

        /**
         * Person id getter.
         *
         * @return person id.
         */
        public long getPersonId()
        {
            return personId;
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
            hashCode ^= (new Long(personId)).hashCode();
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
            if (!(obj instanceof LikedActivityPk))
            {
                return false;
            }
            LikedActivityPk target = (LikedActivityPk) obj;

            return (target.activityId == this.activityId) && (target.personId == this.personId);
        }
    }

}
