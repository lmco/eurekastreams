/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
import javax.persistence.Table;

import org.eurekastreams.commons.model.WrappedLightEntity;

/**
 * Object representing the person following a group relationship.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "Person_Bookmark")
public class PersonBookmark extends WrappedLightEntity implements Serializable
{
    /**
     * Instance of FollowerPk (Composite primary key object) for this class.
     */
    @EmbeddedId
    private PersonBookmarkPk pk = null;

    /**
     * Constructor.
     * 
     * @param inScopeId
     *            Stream scope id.
     * @param inPersonId
     *            Person id.
     */
    public PersonBookmark(final long inScopeId, final long inPersonId)
    {
        pk = new PersonBookmarkPk(inScopeId, inPersonId);
    }

    /**
     * Constructor (no-op for ORM).
     */
    @SuppressWarnings("unused")
    private PersonBookmark()
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
     * Stream scope id getter.
     * 
     * @return stream scope id.
     */
    public long getScopeId()
    {
        return pk.getScopeId();
    }

    /**
     * Composite primary key for bookmark..
     * 
     */
    @Embeddable
    public static class PersonBookmarkPk implements Serializable
    {
        /**
         * Stream scope id.
         */
        @Basic
        private long scopeId = 0;

        /**
         * Person id.
         */
        @Basic
        private long personId = 0;

        /**
         * Constructor.
         * 
         * @param inScopeId
         *            Stream scope id.
         * @param inPersonId
         *            Person id.
         */
        public PersonBookmarkPk(final long inScopeId, final long inPersonId)
        {
            scopeId = inScopeId;
            personId = inPersonId;
        }

        /**
         * Get the stream scope id.
         * 
         * @return the stream scope id.
         */
        public long getScopeId()
        {
            return scopeId;
        }

        /**
         * Get the person id.
         * 
         * @return the person id.
         */
        public long getPersonId()
        {
            return personId;
        }

        /**
         * Constructor (no-op for ORM).
         */
        @SuppressWarnings("unused")
        private PersonBookmarkPk()
        {
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
            hashCode ^= (new Long(scopeId)).hashCode();
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
            if (!(obj instanceof PersonBookmarkPk))
            {
                return false;
            }
            PersonBookmarkPk target = (PersonBookmarkPk) obj;

            return (target.personId == personId) && (target.scopeId == scopeId);
        }
    }
}
