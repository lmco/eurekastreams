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
package org.eurekastreams.server.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import net.sf.gilead.pojo.java5.LightEntity;

/**
 * A request for membership in a (private) group.
 */
@Entity(name = "GroupMembershipRequests")
public class RequestedGroupMembership extends LightEntity implements Serializable
{
    /** Fingerprint. */
    private static final long serialVersionUID = 1380361014138832647L;

    /**
     * Composite primary key for this class.
     */
    @EmbeddedId
    private JoinPk pk = null;

    /**
     * Constructor.
     *
     * @param inGroupId
     *            Group membership requested in.
     * @param inPersonId
     *            Person requesting membership.
     */
    public RequestedGroupMembership(final long inGroupId, final long inPersonId)
    {
        pk = new JoinPk(inGroupId, inPersonId);
    }

    /**
     * Constructor (no-op for ORM).
     */
    @SuppressWarnings("unused")
    private RequestedGroupMembership()
    {
    }

    /**
     * @return Group id.
     */
    public long getGroupId()
    {
        return pk.getGroupId();
    }

    /**
     * @return Person id.
     */
    public long getPersonId()
    {
        return pk.getPersonId();
    }

    /**
     * A composite key for join tables.
     */
    @Embeddable
    private static class JoinPk implements Serializable
    {
        /**
         * Group id.
         */
        @Basic
        private long groupId = 0;

        /**
         * Person id.
         */
        @Basic
        private long personId = 0;


        /**
         * Constructor (no-op for ORM).
         */
        @SuppressWarnings("unused")
        private JoinPk()
        {
        }

        /**
         * Constructor.
         *
         * @param inGroupId
         *            ID of one entity/table.
         * @param inPersonId
         *            ID of other entity/table.
         */
        public JoinPk(final long inGroupId, final long inPersonId)
        {
            groupId = inGroupId;
            personId = inPersonId;
        }

        /**
         * @return group id.
         */
        public long getGroupId()
        {
            return groupId;
        }

        /**
         * @return person id.
         */
        public long getPersonId()
        {
            return personId;
        }

        /**
         * Override hashCode for comparing pk object.
         *
         * @return The generated hashcode.
         */
        public int hashCode()
        {
            int hashCode = 0;
            hashCode ^= (new Long(groupId)).hashCode();
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
            if (!(obj instanceof JoinPk))
            {
                return false;
            }
            JoinPk target = (JoinPk) obj;

            return (target.groupId == this.groupId) && (target.personId == this.personId);
        }
    }
}
