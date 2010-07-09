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
import org.eurekastreams.server.domain.Recommendation;

/**
 * This class provides the mapper functionality for Recommendation entities.
 */
public class RecommendationMapper extends DomainEntityMapper<Recommendation>
{
    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public RecommendationMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Look up recommendations by the subject.
     *
     * @param subjectOpenSocialId
     *            the Open Social Id of the person whose recommendations we're
     *            getting
     * @return list of recommendations
     */
    @SuppressWarnings("unchecked")
    public List<Recommendation> findBySubjectOpenSocialId(final String subjectOpenSocialId)
    {
        Query q = getEntityManager().createQuery(
                "from Recommendation where subjectOpenSocialId = :openSocialId order by date DESC").setParameter(
                "openSocialId", subjectOpenSocialId);

        List<Recommendation> results = q.getResultList();

        return results;
    }

    /**
     * Look up recommendations by the subject.
     *
     * @param subjectOpenSocialId
     *            the Open Social Id of the person whose recommendations we're
     *            getting
     * @param maxResults - max number of items to return.
     * @return list of recommendations
     */
    @SuppressWarnings("unchecked")
    public List<Recommendation> findBySubjectOpenSocialId(final String subjectOpenSocialId, final int maxResults)
    {
        Query q = getEntityManager().createQuery(
                "from Recommendation where subjectOpenSocialId = :openSocialId order by date DESC").setParameter(
                "openSocialId", subjectOpenSocialId);

        List<Recommendation> results = q.setMaxResults(maxResults).getResultList();

        return results;
    }

    /**
     * The name of this domain entity.
     *
     * @return the name
     */
    @Override
    protected String getDomainEntityName()
    {
        return "Recommendation";
    }

    /**
     * Delete a recommendation.
     *
     * @param id
     *            the id of the recommendation to delete.
     */
    public void delete(final long id)
    {
        getEntityManager().createQuery("DELETE FROM Recommendation where id=:id").setParameter("id", id)
                .executeUpdate();
    }

}
