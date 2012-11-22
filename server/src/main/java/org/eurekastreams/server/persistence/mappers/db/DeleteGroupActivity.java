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
 * Given a group id, this mapper deletes all Activities and related objects (Comments and StarredActivities) from the
 * DB.
 */
public class DeleteGroupActivity extends BaseArgDomainMapper<Long, BulkActivityDeleteResponse>
{
    /**
     * Deletes all Activities (and related objects) that were posted to specified group.
     *
     * @param inRequest
     *            the group id.
     * @return {@link BulkActivityDeleteResponse} with info. needed to clean up cache and search index.
     *
     */
    @Override
    public BulkActivityDeleteResponse execute(final Long inRequest)
    {
        // get groupDTO to fetch group stream id
        Long groupStreamId = getGroupStreamId(inRequest);
        if (groupStreamId == null)
        {
            // short-circut as group is no longer present.
            return new BulkActivityDeleteResponse();
        }

        // get activity ids to return (cache and search index cleanup)
        List<Long> activityIds = getActivityIds(groupStreamId);

        // get comment ids to return (cache and search index cleanup)
        List<Long> commentIds = getCommentIds(groupStreamId);

        // get people with starred Activities map (cache clean up).
        Map<Long, Set<Long>> peopleWithStarredActivities = getPeopleWithStarredActivities(inRequest);

        // Why the following queries use sub-selects:
        // FROM HIBERNATE DOCS: No joins can be specified in a bulk HQL query. Sub-queries can be used in the
        // where-clause,
        // where the subqueries themselves may contain joins.
        getEntityManager().createQuery(
                "DELETE FROM Comment c WHERE c.target.id IN"
                        + " (SELECT a.id FROM Activity a  WHERE a.recipientStreamScope.id = :groupStreamId)")// \n
                .setParameter("groupStreamId", groupStreamId).executeUpdate();

        // delete activity from currentUser's starred activity collections in DB.
        getEntityManager().createQuery(
                "DELETE FROM StarredActivity where activityId IN"
                        + " (SELECT a.id FROM Activity a  WHERE a.recipientStreamScope.id = :groupStreamId)")// \n
                .setParameter("groupStreamId", groupStreamId).executeUpdate();

        // delete activity hashtags for this group stream
        if (!activityIds.isEmpty())
        {
            getEntityManager().createQuery("DELETE FROM StreamHashTag where activityId IN (:activityIds)")
                    .setParameter("activityIds", activityIds).executeUpdate();
        }

        // delete the activities.
        getEntityManager().createQuery("DELETE FROM Activity a WHERE a.recipientStreamScope.id = :groupStreamId")
                .setParameter("groupStreamId", groupStreamId).executeUpdate();
        
        // delete any Person's bookmark to the group that's just about to be deleted
        getEntityManager().createQuery("DELETE FROM PersonBookmark pb where pb.pk.scopeId = :groupStreamId)")
                .setParameter("groupStreamId", groupStreamId).executeUpdate();

        return new BulkActivityDeleteResponse(activityIds, commentIds, peopleWithStarredActivities);
    }

    /**
     * Get the stream id for a group.
     *
     * @param groupId
     *            Group id.
     * @return The stream id for a group.
     */
    @SuppressWarnings("unchecked")
    private Long getGroupStreamId(final Long groupId)
    {
        List<Long> result = getEntityManager().createQuery(
                "SELECT g.streamScope.id FROM DomainGroup g  WHERE g.id = :groupId").setParameter("groupId", groupId)
                .getResultList();

        return result.size() == 0 ? null : result.get(0);
    }

    /**
     * Return activity ids for a group. This only returns the number of results based on max list size in cache, no need
     * to return more than that.
     *
     * @param groupStreamId
     *            group stream id.
     * @return activity ids for a group.
     */
    @SuppressWarnings("unchecked")
    private List<Long> getActivityIds(final Long groupStreamId)
    {
        return getEntityManager().createQuery(
                "SELECT a.id FROM Activity a  WHERE a.recipientStreamScope.id = :groupStreamId ORDER BY a.id DESC")
                .setParameter("groupStreamId", groupStreamId).getResultList();
    }

    /**
     * Return comment ids for all activities in a group.
     *
     * @param groupStreamId
     *            group stream id.
     * @return Comment ids for all activities in a group.
     */
    @SuppressWarnings("unchecked")
    private List<Long> getCommentIds(final Long groupStreamId)
    {
        return getEntityManager().createQuery(
                "SELECT c.id FROM Comment c, Activity a"
                        + "  WHERE a.recipientStreamScope.id = :groupStreamId AND c.target.id = a.id") // \n
                .setParameter("groupStreamId", groupStreamId).getResultList();

    }

    /**
     * Return Map keyed by Person id that has set of activity ids that the person has starred that belong to the group
     * being deleted.
     *
     * @param groupStreamId
     *            Group stream id.
     * @return Map keyed by Person id that has set of activity ids that the person has starred that belong to the group
     *         being deleted.
     */
    @SuppressWarnings("unchecked")
    private Map<Long, Set<Long>> getPeopleWithStarredActivities(final Long groupStreamId)
    {
        Hashtable<Long, Set<Long>> results = new Hashtable<Long, Set<Long>>();
        List<Object[]> queryResults = getHibernateSession().createQuery(
                "SELECT sa.pk.personId, sa.pk.activityId from StarredActivity sa, Activity a"
                        + "  WHERE sa.pk.activityId = a.id AND a.recipientStreamScope.id = :groupStreamId")
                .setParameter("groupStreamId", groupStreamId).list();

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
