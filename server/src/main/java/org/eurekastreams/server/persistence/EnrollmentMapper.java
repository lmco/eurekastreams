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
package org.eurekastreams.server.persistence;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.Enrollment;

/**
 * This class provides the mapper functionality for Enrollment entities.
 */
public class EnrollmentMapper extends DomainEntityMapper<Enrollment>
{
    /**
     * The person mapper.
     */
    private PersonMapper personMapper;

    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     *
     * @param inPersonMapper
     *            the person mapper.
     */
    public EnrollmentMapper(final QueryOptimizer inQueryOptimizer, final PersonMapper inPersonMapper)
    {
        super(inQueryOptimizer);
        personMapper = inPersonMapper;
    }

    /**
     * Get the domain entity name for the generic query operations.
     *
     * @return the domain entity name for the gadget query operations.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "Enrollment";
    }

    /**
     * Find a person's enrollments.
     *
     * @param inOpenSocialId
     *            Open Social ID of the person
     *
     * @return list of enrollments for the person
     */
    @SuppressWarnings("unchecked")
    public List<Enrollment> findPersonEnrollmentsByOpenSocialId(final String inOpenSocialId)
    {
        // TODO look into using caching to avoid join.
        Query q = getEntityManager().createQuery(
                "from Enrollment e where e.person.openSocialId = :openSocialId order by e.gradDate desc").setParameter(
                "openSocialId", inOpenSocialId);

        return q.getResultList();
    }

    /**
     * Find a person's enrollments.
     *
     * @param inId
     *            ID of the person
     *
     * @return list of enrollments for the person
     */
    @SuppressWarnings("unchecked")
    public List<Enrollment> findPersonEnrollmentsById(final Long inId)
    {
        // TODO look into using caching to avoid join.
        Query q = getEntityManager().createQuery(
                "from Enrollment e where e.person.id = :id order by e.gradDate desc").setParameter(
                "id", inId);

        return q.getResultList();
    }

    /**
     * Delete a person's enrollment .
     *
     * @param inEnrollmentId
     *            The id of the Enrollment to delete.
     */
    public void delete(final long inEnrollmentId)
    {
        Enrollment enrollment = findById(inEnrollmentId);
        getEntityManager().remove(enrollment);
    }

    /**
     * Flushes and updates the index of the person.
     *
     * @param uuid
     *            the uuid of the person to update.
     */
    public void flush(final String uuid)
    {
        flush();
        getFullTextSession().index(personMapper.findByOpenSocialId(uuid));
    }
}
