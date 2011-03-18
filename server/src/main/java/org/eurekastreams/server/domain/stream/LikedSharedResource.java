/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

/**
 * Represents a liked shared resource. This is a promoted entity to make direct inserts/deletes without worrying about
 * the collections.
 */
@Entity
@Table(name = "Person_LikedSharedResources")
public class LikedSharedResource
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 7115055877144649555L;

    /**
     * Instance of LikedSharedResourcePk (Composite primary key object) for this class.
     */
    @EmbeddedId
    private LikedSharedResourcePk pk = null;

    /**
     * Constructor.
     * 
     * @param inSharedResourceId
     *            The shared resource id
     * @param inPersonId
     *            the person id
     */
    public LikedSharedResource(final long inSharedResourceId, final long inPersonId)
    {
        pk = new LikedSharedResourcePk(inSharedResourceId, inPersonId);
    }

    /**
     * Constructor (no-op for ORM).
     */
    @SuppressWarnings("unused")
    private LikedSharedResource()
    {
    }

    /**
     * @return the person id.
     */
    public long getPersonId()
    {
        return pk.getPersonId();
    }

    /**
     * @return the shared resource id.
     */
    public long getSharedResourceId()
    {
        return pk.getSharedResourceId();
    }

    /**
     * Composite primary key for follower.
     * 
     */
    @Embeddable
    public static class LikedSharedResourcePk implements Serializable
    {
        /**
         * Serial version uid.
         */
        private static final long serialVersionUID = 8812116019041521253L;

        /**
         * Shared resource id.
         */
        @Basic
        private long sharedResourceId = 0;

        /**
         * Person id.
         */
        @Basic
        private long personId = 0;

        /**
         * Constructor.
         */
        public LikedSharedResourcePk()
        {
        }

        /**
         * Constructor.
         * 
         * @param inSharedResourceId
         *            Shared resource id
         * @param inPersonId
         *            Person id
         */
        public LikedSharedResourcePk(final long inSharedResourceId, final long inPersonId)
        {
            sharedResourceId = inSharedResourceId;
            personId = inPersonId;
        }

        /**
         * @return the sharedResourceId
         */
        public long getSharedResourceId()
        {
            return sharedResourceId;
        }

        /**
         * @param inSharedResourceId
         *            the sharedResourceId to set
         */
        public void setSharedResourceId(final long inSharedResourceId)
        {
            sharedResourceId = inSharedResourceId;
        }

        /**
         * @return the personId
         */
        public long getPersonId()
        {
            return personId;
        }

        /**
         * @param inPersonId
         *            the personId to set
         */
        public void setPersonId(final long inPersonId)
        {
            personId = inPersonId;
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
            hashCode ^= (new Long(sharedResourceId)).hashCode();
            hashCode ^= (new Long(personId)).hashCode();
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
            if (!(obj instanceof LikedSharedResourcePk))
            {
                return false;
            }
            LikedSharedResourcePk target = (LikedSharedResourcePk) obj;

            return (target.sharedResourceId == sharedResourceId) && (target.personId == personId);
        }
    }

}
