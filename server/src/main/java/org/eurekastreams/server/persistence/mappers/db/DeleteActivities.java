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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.BulkActivityDeleteResponse;

/**
 * Given a list of Activity ids, this mapper deletes all Activities and related objects (Comments and StarredActivities)
 * from the DB. NOTE: This mapper uses "IN" query syntax so activity id list size should NOT be unbounded.
 */
public class DeleteActivities extends BaseArgDomainMapper<List<Long>, BulkActivityDeleteResponse>
{
    /**
     * Deletes all Activities (and related objects) based on Activity ids passed in..
     *
     * @param inActivityIds
     *            List of activity ids.
     * @return {@link BulkActivityDeleteResponse} with info. needed to clean up cache and search index.
     *
     */
    @Override
    public BulkActivityDeleteResponse execute(final List<Long> inActivityIds)
    {
        // short-circut as no activities to delete.
        if (inActivityIds == null || inActivityIds.isEmpty())
        {
            return new BulkActivityDeleteResponse();
        }

        // get comment ids to return (cache and search index cleanup)
        List<Long> commentIds = getCommentIds(inActivityIds);

        // get people with starred Activities map (cache clean up).
        Map<Long, Set<Long>> peopleWithStarredActivities = getPeopleWithStarredActivities(inActivityIds);

        // delete comments.
        getHibernateSession().createQuery("DELETE FROM Comment c WHERE c.target.id IN (:activityIds)")
                .setParameterList("activityIds", inActivityIds).executeUpdate();

        // delete activity from currentUser's starred activity collections in DB.
        getHibernateSession().createQuery("DELETE FROM StarredActivity where activityId IN (:activityIds)")
                .setParameterList("activityIds", inActivityIds).executeUpdate();

        // delete any hashtags stored to streams on behalf of this activity
        getEntityManager().createQuery("DELETE FROM StreamHashTag WHERE activity.id in (:activityIds)").setParameter(
                "activityIds", inActivityIds).executeUpdate();

        // delete the activities.
        getHibernateSession().createQuery("DELETE FROM Activity a WHERE a.id IN (:activityIds)").setParameterList(
                "activityIds", inActivityIds).executeUpdate();

        return new BulkActivityDeleteResponse(inActivityIds, commentIds, peopleWithStarredActivities);
    }

    /**
     * Return comment ids for all activities in a group.
     *
     * @param inActivityIds
     *            List of activity ids.
     * @return Comment ids for all activities in a group.
     */
    @SuppressWarnings("unchecked")
    private List<Long> getCommentIds(final List<Long> inActivityIds)
    {
        return getHibernateSession().createQuery("SELECT c.id FROM Comment c WHERE c.target.id IN (:activityIds)")
                .setParameterList("activityIds", inActivityIds).list();
    }

    /**
     * Return Map keyed by Person id that has set of activity ids that the person has starred that belong to the group
     * being deleted.
     *
     * @param inActivityIds
     *            List of activity ids.
     * @return Map keyed by Person id that has set of activity ids that the person has starred that belong to the group
     *         being deleted.
     */
    @SuppressWarnings("unchecked")
    private Map<Long, Set<Long>> getPeopleWithStarredActivities(final List<Long> inActivityIds)
    {
        Hashtable<Long, Set<Long>> results = new Hashtable<Long, Set<Long>>();
        List<Object[]> queryResults = getHibernateSession().createQuery(
                "SELECT sa.pk.personId, sa.pk.activityId from StarredActivity sa"
                        + "  WHERE sa.pk.activityId IN (:activityIds)").setParameterList("activityIds", inActivityIds)
                .list();

        Long personId;
        Long activityId;
        for (Object[] row : queryResults)
        {
            personId = (Long) row[0];
            activityId = (Long) row[1];

            if (results.containsKey(personId))
            {
                results.get(personId).add(activityId);
            }
            else
            {
                HashSet<Long> set = new HashSet<Long>();
                set.add(activityId);
                results.put(personId, set);
            }
        }

        return results;
    }

}
