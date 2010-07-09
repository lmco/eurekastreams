/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.StreamView;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * Base class for CompositeStreamLoaders.
 * 
 */
public abstract class BaseCompositeStreamLoader extends CachedDomainMapper implements CompositeStreamLoader
{
    /**
     * Maximum number of items to return.
     */
    private static final int MAX_LIST_SIZE = 10000;

    /**
     * Get hashtable of restrictions used to query for activity ids.
     * 
     * @param compositeStream
     *            The compositeStream
     * @param inUserId
     *            The user id.
     * @return hashtable of restrictions used to query for activity ids.
     */
    protected abstract Hashtable<RestrictionType, HashSet> getActivityRestrictions(StreamView compositeStream,
            long inUserId);

    /**
     * Get list of activity ids for given compositeStream and user from cache, if present, or null if not.
     * 
     * @param compositeStream
     *            The CompositeStream.
     * @param inUserId
     *            The user id.
     * @return List of activity ids for given compositeStream and user from cache, if present, or null if not.
     */
    protected abstract List<Long> getIdListFromCache(StreamView compositeStream, long inUserId);

    /**
     * Sets the list of activity ids to cache for given CompositeStream and user.
     * 
     * @param inActivityIds
     *            The list of activity ids.
     * @param inCompositeStream
     *            The CompositeStream.
     * @param inUserId
     *            The user id.
     */
    protected abstract void setIdListToCache(List<Long> inActivityIds, StreamView inCompositeStream, long inUserId);

    /**
     * Gets list of activityIds for given CompositeStream.
     * @param inCompositeStream the composite stream.
     * @param inUserId the user id.
     * @return list of activityIds for given CompositeStream.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getActivityIds(final StreamView inCompositeStream, final long inUserId)
    {
        // check cache for ids and return if found.
        List<Long> activityKeys = getIdListFromCache(inCompositeStream, inUserId);

        if (activityKeys != null)
        {
            return activityKeys;
        }

        // build critera for getting id list from datastore
        Criteria criteria = getHibernateSession().createCriteria(Activity.class);
        ProjectionList fields = Projections.projectionList();
        fields.add(getColumn("id"));
        criteria.setProjection(fields);

        // Get restrictions from loader and add them to criteria. This is basically the
        // "where" clause for the query.
        Hashtable restrictionParams = getActivityRestrictions(inCompositeStream, inUserId);
        if (restrictionParams != null)
        {
            // if false, abort query and just return no results.
            if (!addRestrictions(criteria, restrictionParams))
            {
                activityKeys = new ArrayList<Long>(0);
                setIdListToCache(activityKeys, inCompositeStream, inUserId);
                return activityKeys;
            }
        }

        // set the max results to return.
        criteria.setMaxResults(MAX_LIST_SIZE);

        // set the order by date descending
        criteria.addOrder(Order.desc("id"));

        // get the list of ids from datastore
        activityKeys = criteria.list();

        setIdListToCache(activityKeys, inCompositeStream, inUserId);

        return activityKeys;
    }

    /**
     * This method takes the hashTable of parameters, generates the appropriate Criterion objects, and adds the
     * Restrictions to the passed in Critera object. This essentially generates the "where" clause for the query. This
     * method assumes that if a key is present, the set contains values.
     * 
     * @param inCriteria
     *            The Critera (query) to add the restrictions to.
     * @param scopeParams
     *            The hashtable of parameters, keyed by ScopeType, that will be used to generate the where clause
     *            restrictions.
     * @return true if query is to continue to execute, false if there is a condition that will allow empty set to be
     *         returned and avoid the query all together.
     */
    @SuppressWarnings("unchecked")
    private boolean addRestrictions(final Criteria inCriteria, final Hashtable<RestrictionType, HashSet> scopeParams)
    {
        // if scope params is empty, that means that restrictions were specified, but did not have
        // any content (e.g. followed people, but user is not following anyone). Short circut.
        if (scopeParams.keySet().size() == 0)
        {
            return false;
        }

        // At this point all we have to handle is ActivityId|OrgId|StreamId restrictions
        ArrayList<Criterion> restrictions = new ArrayList<Criterion>(2);

        if (scopeParams.containsKey(RestrictionType.ORG_IDS))
        {
            Criterion orgRestriction = Restrictions.in("this.recipientParentOrg.id", scopeParams
                    .get(RestrictionType.ORG_IDS));
            restrictions.add(orgRestriction);
        }

        if (scopeParams.containsKey(RestrictionType.STREAM_IDS))
        {
            Criterion streamRestriction = Restrictions.in("this.recipientStreamScope.id", scopeParams
                    .get(RestrictionType.STREAM_IDS));
            restrictions.add(streamRestriction);
        }

        // take all the restrictions and "OR" them together.
        restrictions.trimToSize();
        Criterion tmpRestriction = null;
        for (Criterion c : restrictions)
        {
            if (tmpRestriction == null)
            {
                tmpRestriction = c;
            }
            else
            {
                tmpRestriction = Restrictions.or(c, tmpRestriction);
            }
        }
        inCriteria.add(tmpRestriction);
        return true;
    }

    /**
     * An enum describing the restriction types for querying for activities.
     */
    public enum RestrictionType
    {
        /**
         * Key for list of org ids.
         */
        ORG_IDS,

        /**
         * Key for list of activity ids.
         */
        ACTIVITY_IDS,

        /**
         * key for list of steam ids.
         */
        STREAM_IDS
    }

}
