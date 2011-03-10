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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eurekastreams.commons.model.WrappedLightEntity;

/**
 * Promoted join table to fetch skills for a person, sorted by skillsIndex - this is not to be inserted, only selected.
 */
@Entity
@Table(name = "Background_Skills")
public class PersonBackgroundItemSkill extends WrappedLightEntity implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -4576164221680044566L;

    /**
     * Instance of PersonBackgroundItemSkillPk (Composite primary key object) for this class.
     */
    @EmbeddedId
    private PersonBackgroundItemSkillPk pk = null;

    /**
     * @return the background id
     * @see org.eurekastreams.server.domain.PersonBackgroundItemSkill.PersonBackgroundItemSkillPk#getBackgroundId()
     */
    public long getBackgroundId()
    {
        return pk.getBackgroundId();
    }

    /**
     * @return the background item id
     * @see org.eurekastreams.server.domain.PersonBackgroundItemSkill.PersonBackgroundItemSkillPk#getBackgroundItemId()
     */
    public long getBackgroundItemId()
    {
        return pk.getBackgroundItemId();
    }

    /**
     * @return the sort index
     * @see org.eurekastreams.server.domain.PersonBackgroundItemSkill.PersonBackgroundItemSkillPk#getSortIndex()
     */
    public int getSortIndex()
    {
        return pk.getSortIndex();
    }

    /**
     * Constructor.
     *
     * @param inBackgroundId
     *            the background id
     * @param inBackgroundItemId
     *            the background item id
     * @param inSortIndex
     *            the sort index
     */
    public PersonBackgroundItemSkill(final long inBackgroundId, final long inBackgroundItemId, final int inSortIndex)
    {
        pk = new PersonBackgroundItemSkillPk(inBackgroundId, inBackgroundItemId, inSortIndex);
    }

    /**
     * Constructor (no-op for ORM).
     */
    @SuppressWarnings("unused")
    private PersonBackgroundItemSkill()
    {
    }

    /**
     * Composite primary key for follower.
     *
     */
    @Embeddable
    public static class PersonBackgroundItemSkillPk implements Serializable
    {
        /**
         * serial version uid.
         */
        private static final long serialVersionUID = -2695672314733747006L;

        /**
         * Background id.
         */
        @Column(name = "Background_Id")
        private long backgroundId;

        /**
         * @return the backgroundId
         */
        public long getBackgroundId()
        {
            return backgroundId;
        }

        /**
         * @return the backgroundItemId
         */
        public long getBackgroundItemId()
        {
            return backgroundItemId;
        }

        /**
         * @return the sortIndex
         */
        public int getSortIndex()
        {
            return sortIndex;
        }

        /**
         * Background item id.
         */
        @Column(name = "BackgroundItem_Id")
        private long backgroundItemId;

        /**
         * Sort index.
         */
        @Column(name = "skillsIndex")
        private int sortIndex;

        /**
         * Constructor.
         *
         * @param inBackgroundId
         *            the background id
         * @param inBackgroundItemId
         *            the background item id
         * @param inSortIndex
         *            the sorting index
         */
        public PersonBackgroundItemSkillPk(final long inBackgroundId, final long inBackgroundItemId,
                final int inSortIndex)
        {
            backgroundId = inBackgroundId;
            backgroundItemId = inBackgroundItemId;
            sortIndex = inSortIndex;
        }

        /**
         * Constructor (no-op for ORM).
         */
        @SuppressWarnings("unused")
        private PersonBackgroundItemSkillPk()
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
            hashCode ^= (new Long(backgroundId)).hashCode();
            hashCode ^= (new Long(backgroundItemId)).hashCode();
            hashCode ^= (new Long(sortIndex)).hashCode();
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
            if (!(obj instanceof PersonBackgroundItemSkillPk))
            {
                return false;
            }
            PersonBackgroundItemSkillPk target = (PersonBackgroundItemSkillPk) obj;

            return (target.backgroundId == backgroundId) && (target.backgroundItemId == backgroundItemId)
                    && (target.sortIndex == sortIndex);
        }
    }

}
