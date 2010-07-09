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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.strategies.DomainGroupQueryStrategy;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * Gets a list of group objects for a given list of group ids.
 */
public class GetDomainGroupsByIds extends CachedDomainMapper implements DomainMapper<List<Long>, List>
{
    /**
     * Strategy for querying a domain group model view from the database.
     */
    private DomainGroupQueryStrategy domainGroupQueryStrategy;

    /**
     * Constructor.
     *
     * @param inDomainGroupQueryStrategy
     *            the person query strategy to set.
     */
    public GetDomainGroupsByIds(final DomainGroupQueryStrategy inDomainGroupQueryStrategy)
    {
        domainGroupQueryStrategy = inDomainGroupQueryStrategy;
    }

    /**
     * Empty constructor.
     */
    protected GetDomainGroupsByIds()
    {
        // no op
    }

    /**
     * Looks in cache for the necessary DTOs and returns them if found. Otherwise, makes a database call, puts them in
     * cache, and returns them.
     *
     * @param ids
     *            the list of ids that should be found.
     * @return list of DTO objects.
     */
    @SuppressWarnings("unchecked")
    public List<DomainGroupModelView> execute(final List<Long> ids)
    {
        // Checks to see if there's any real work to do
        if (ids == null || ids.size() == 0)
        {
            return new ArrayList<DomainGroupModelView>();
        }

        List<String> stringKeys = new ArrayList<String>();
        for (long key : ids)
        {
            stringKeys.add(CacheKeys.GROUP_BY_ID + key);
        }

        // Finds items in the cache.
        Map<String, DomainGroupModelView> items = (Map<String, DomainGroupModelView>) (Map<String, ? >) getCache()
                .multiGet(stringKeys);

        // Determines if any of the items were missing from the cache
        List<Long> uncachedItemKeys = new ArrayList<Long>();
        for (long itemKey : ids)
        {
            String cacheKey = CacheKeys.GROUP_BY_ID + itemKey;
            if (!items.containsKey(cacheKey) || items.get(cacheKey) == null)
            {
                uncachedItemKeys.add(itemKey);
            }
        }

        // One or more of the items were missing in the cache so go to the database
        if (uncachedItemKeys.size() != 0)
        {
            Map<String, DomainGroupModelView> itemMap = new HashMap<String, DomainGroupModelView>();

            Criteria criteria = domainGroupQueryStrategy.getCriteria(getHibernateSession());

            // Creates the necessary "OR" clauses to get all uncached items
            Criterion restriction = null;
            for (int i = 0; i < uncachedItemKeys.size(); i++)
            {
                long key = uncachedItemKeys.get(i);
                if (restriction == null)
                {
                    restriction = Restrictions.eq("this.id", key);
                }
                else
                {
                    restriction = Restrictions.or(Restrictions.eq("this.id", key), restriction);
                }
            }

            criteria.add(restriction);

            List<DomainGroupModelView> results = criteria.list();
            for (DomainGroupModelView result : results)
            {
                itemMap.put(CacheKeys.GROUP_BY_ID + result.getEntityId(), result);
            }

            for (String key : itemMap.keySet())
            {
                getCache().set(key, itemMap.get(key));
            }

            items.putAll(itemMap);
        }

        // Return the items in the same order they were passed in, exluding items not found in cache or db.
        ArrayList<DomainGroupModelView> results = new ArrayList<DomainGroupModelView>();
        for (long id : ids)
        {
            DomainGroupModelView group = items.get(CacheKeys.GROUP_BY_ID + id);
            if (group != null)
            {
                results.add(group);
            }
        }

        return results;
    }

    /**
     * Looks in cache for the necessary DTOs and returns them if found. Otherwise, makes a database call, puts them in
     * cache, and returns them.
     *
     * @param id
     *            the id that should be found.
     * @return DTO.
     */
    public DomainGroupModelView execute(final Long id)
    {
        return execute(Collections.singletonList(id)).get(0);
    }
}
