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

/**
 * Join-table entity promotion for bulk updates of a Person's relatedOrganizations collection. This shouldn't be used in
 * the domain model, just JPA queries.
 */
@Entity
@Table(name = "Person_RelatedOrganization")
public class PersonRelatedOrganization
{
    /**
     * Instance of PersonRelatedOrganizationPk (Composite primary key object) for this class.
     */
    @EmbeddedId
    private PersonRelatedOrganizationPk pk;

    /**
     * Default constructor for serialization.
     */
    @SuppressWarnings("unused")
    private PersonRelatedOrganization()
    {
    }

    /**
     * Constructor.
     * 
     * @param inPersonId
     *            the person id
     * @param inOrganizationId
     *            the organization id
     * @param inKey
     */
    public PersonRelatedOrganization(final long inPersonId, final long inOrganizationId)
    {
        pk = new PersonRelatedOrganizationPk(inPersonId, inOrganizationId);
    }

    /**
     * Get the PK for serialization.
     * 
     * @return the PK
     */
    @SuppressWarnings("unused")
    private PersonRelatedOrganizationPk getPk()
    {
        return pk;
    }

    /**
     * Set the PK for serialization.
     * 
     * @param inPk
     *            the primary key to set
     */
    @SuppressWarnings("unused")
    private void setPk(final PersonRelatedOrganizationPk inPk)
    {
        pk = inPk;
    }

    /**
     * Get the person id.
     * 
     * @return the person id
     */
    public long getPersonId()
    {
        return pk.getPersonId();
    }

    /**
     * Get the organization id.
     * 
     * @return the organization id
     */
    public long getOrganizationId()
    {
        return pk.getOrganizationId();
    }

    /**
     * Composite primary key for follower.
     * 
     */
    @Embeddable
    public static class PersonRelatedOrganizationPk implements Serializable
    {
        /**
         * 
         */
        private static final long serialVersionUID = -773128801901609735L;

        /**
         * Default constructor for deserialization.
         */
        @SuppressWarnings("unused")
        private PersonRelatedOrganizationPk()
        {
        }

        /**
         * Person id.
         */
        @Basic
        private long personId = 0;

        /**
         * Organization id.
         */
        @Basic
        private long organizationId = 0;

        /**
         * Constructor (no-op for ORM).
         * 
         * @param inPersonId
         *            the person id
         * @param inOrganizationId
         *            the organization id
         */
        public PersonRelatedOrganizationPk(final long inPersonId, final long inOrganizationId)
        {
            personId = inPersonId;
            organizationId = inOrganizationId;
        }

        /**
         * Override hashCode for comparing pk object.
         * 
         * @return The generated hashcode.
         */
        public int hashCode()
        {
            int hashCode = 0;
            hashCode ^= (new Long(personId)).hashCode();
            hashCode ^= (new Long(organizationId)).hashCode();
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
            if (!(obj instanceof PersonRelatedOrganizationPk))
            {
                return false;
            }
            PersonRelatedOrganizationPk target = (PersonRelatedOrganizationPk) obj;

            return (target.personId == this.personId) && (target.organizationId == this.organizationId);
        }

        /**
         * Private setter for deserialization.
         * 
         * @param inPersonId
         *            the personId to set
         */
        @SuppressWarnings("unused")
        private void setPersonId(final Long inPersonId)
        {
            personId = inPersonId;
        }

        /**
         * Get the person id.
         * 
         * @return the person id
         */
        public long getPersonId()
        {
            return personId;
        }

        /**
         * Set the organization id.
         * 
         * @param inOrganizationId
         *            the organization id to set
         */
        public void setOrganizationId(final Long inOrganizationId)
        {
            organizationId = inOrganizationId;
        }

        /**
         * Get the organization id.
         * 
         * @return the organization id
         */
        public long getOrganizationId()
        {
            return organizationId;
        }
    }
}
