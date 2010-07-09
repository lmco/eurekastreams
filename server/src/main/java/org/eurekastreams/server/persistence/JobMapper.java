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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.Job;

/**
 * This class provides the mapper functionality for Job entities.
 */
public class JobMapper extends DomainEntityMapper<Job>
{
    /**
     * Local log instance.
     */
    private static Log logger = LogFactory.getLog(JobMapper.class);

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
    public JobMapper(final QueryOptimizer inQueryOptimizer, final PersonMapper inPersonMapper)
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
        return "Job";
    }

    /**
     * Find a person's jobs.
     *
     * @param inOpenSocialId
     *            Open Social ID of the person
     *
     * @return list of jobs for the person
     */
    @SuppressWarnings("unchecked")
    public List<Job> findPersonJobsByOpenSocialId(final String inOpenSocialId)
    {
        Query q = getEntityManager().createQuery(
                "from Job j where j.person.openSocialId = :openSocialId order by j.dateTo DESC, j.dateFrom DESC")
                .setParameter("openSocialId", inOpenSocialId);

        return q.getResultList();
    }

    /**
     * Find a person's jobs by person id.
     *
     * @param inId
     *            ID of the person
     *
     * @return list of jobs for the person
     */
    @SuppressWarnings("unchecked")
    public List<Job> findPersonJobsById(final Long inId)
    {
        Query q = getEntityManager().createQuery(
                "from Job j where j.person.id = :id order by j.dateTo DESC, j.dateFrom DESC").setParameter("id", inId);

        return q.getResultList();
    }

    /**
     * Delete a person's job.
     *
     * @param inJobId
     *            The Job to delete.
     */
    public void delete(final long inJobId)
    {
        Job job = findById(inJobId);
        getEntityManager().remove(job);
    }

    /**
     * Look up company names starting with the given prefix.
     *
     * @param prefix
     *            the company name prefix to search for
     * @param limit
     *            the maximum number of companies to return
     * @return the list of company names
     */
    @SuppressWarnings("unchecked")
    public List<String> findCompaniesByPrefix(final String prefix, final int limit)
    {
        String searchParam = prefix.toLowerCase() + "%";
        Query query = getEntityManager().createQuery(
                "select distinct(companyName) from Job j where lower(j.companyName) like :prefix "
                        + "order by companyName");
        query.setMaxResults(limit);
        query.setParameter("prefix", searchParam);

        return query.getResultList();
    }

    /**
     * Look up job titles starting with the given prefix.
     *
     * @param prefix
     *            the title prefix to search for
     * @param limit
     *            the maximum number of titles to return
     * @return the list of titles
     */
    @SuppressWarnings("unchecked")
    public List<String> findTitlesByPrefix(final String prefix, final int limit)
    {
        String searchParam = prefix.toLowerCase() + "%";
        Query query = getEntityManager().createQuery(
                "select distinct(title) from Job j where lower(j.title) like :prefix "
                        + "order by title");
        query.setMaxResults(limit);
        query.setParameter("prefix", searchParam);

        return query.getResultList();
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
