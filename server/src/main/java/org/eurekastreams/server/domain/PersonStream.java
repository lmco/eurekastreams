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
import javax.persistence.Table;

import net.sf.gilead.pojo.java5.LightEntity;

/**
 * Object representing the person following a group relationship.
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "Person_Stream")
public class PersonStream extends LightEntity implements Serializable
{
    /**
     * Instance of FollowerPk (Composite primary key object) for this class.
     */
    @EmbeddedId
    private PersonStreamPk pk = null;

    /**
     * Index of the followed group in the list of followed groups for the follower.
     */
    @Basic
    private int streamIndex = 0;

    /**
     * Constructor.
     * 
     * @param inStreamId
     *            Stream id.
     * @param inPersonId
     *            Person id.
     */
    public PersonStream(final long inStreamId, final long inPersonId)
    {
        pk = new PersonStreamPk(inStreamId, inPersonId);
    }

    /**
     * Constructor (no-op for ORM).
     */
    @SuppressWarnings("unused")
    private PersonStream()
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
     * Stream id getter.
     * 
     * @return stream id.
     */
    public long getStreamId()
    {
        return pk.getStreamId();
    }

    /**
     * @param inStreamIndex the stream index to set
     */
    public void setStreamIndex(final int inStreamIndex)
    {
        streamIndex = inStreamIndex;
    }

    /**
     * @return the stream index
     */
    public int getStreamIndex()
    {
        return streamIndex;
    }

    /**
     * Composite primary key for follower.
     * 
     */
    @Embeddable
    public static class PersonStreamPk implements Serializable
    {
        /**
         * Stream id.
         */
        @Basic
        private long streamId = 0;

        /**
         * Person id.
         */
        @Basic
        private long personId = 0;

        /**
         * Constructor.
         * 
         * @param inStreamId
         *            Stream id.
         * @param inPersonId
         *            Person id.
         */
        public PersonStreamPk(final long inStreamId, final long inPersonId)
        {
            streamId = inStreamId;
            personId = inPersonId;
        }

        public long getStreamId()
        {
            return streamId;
        }

        public long getPersonId()
        {
            return personId;
        }

        /**
         * Constructor (no-op for ORM).
         */
        @SuppressWarnings("unused")
        private PersonStreamPk()
        {
        }

        /**
         * Override hashCode for comparing pk object.
         * 
         * @return The generated hashcode.
         */
        public int hashCode()
        {
            int hashCode = 0;
            hashCode ^= (new Long(streamId)).hashCode();
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
        public boolean equals(final Object obj)
        {
            if (!(obj instanceof PersonStreamPk))
            {
                return false;
            }
            PersonStreamPk target = (PersonStreamPk) obj;

            return (target.personId == this.personId) && (target.streamId == this.streamId);
        }
    }
}
