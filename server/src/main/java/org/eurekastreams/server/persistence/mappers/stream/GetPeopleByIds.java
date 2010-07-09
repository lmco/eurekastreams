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
import org.eurekastreams.server.persistence.mappers.GetRelatedOrganizationIdsByPersonId;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.strategies.PersonQueryStrategy;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * Gets a list of Person objects for a given list of person ids.
 */
public class GetPeopleByIds extends CachedDomainMapper implements DomainMapper<List<Long>, List>
{
    /**
     * Strategy for querying a person model view from the database.
     */
    private PersonQueryStrategy personQueryStrategy;

    /**
     * Mapper to get related org ids by person id.
     */
    private GetRelatedOrganizationIdsByPersonId getRelatedOrganizationIdsByPersonIdMapper;

    /**
     * Constructor.
     *
     * @param inPersonQueryStrategy
     *            the person query strategy to set.
     * @param inGetRelatedOrganizationIdsByPersonIdMapper
     *            the strategy to get related orgs by people
     */
    public GetPeopleByIds(final PersonQueryStrategy inPersonQueryStrategy,
            final GetRelatedOrganizationIdsByPersonId inGetRelatedOrganizationIdsByPersonIdMapper)
    {
        personQueryStrategy = inPersonQueryStrategy;
        getRelatedOrganizationIdsByPersonIdMapper = inGetRelatedOrganizationIdsByPersonIdMapper;
    }

    /**
     * Empty constructor.
     */
    protected GetPeopleByIds()
    {
        // not used.
    }

    /**
     * Fetch a person by their ID.
     *
     * @param id
     *            the id of the person to fetch
     * @return the PersonModelView with the id passed in
     */
    public PersonModelView execute(final Long id)
    {
        return execute(Collections.singletonList(id)).get(0);
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
    public List<PersonModelView> execute(final List<Long> ids)
    {
        // Checks to see if there's any real work to do
        if (ids == null || ids.size() == 0)
        {
            return new ArrayList<PersonModelView>();
        }

        List<String> stringKeys = new ArrayList<String>();
        for (long key : ids)
        {
            stringKeys.add(CacheKeys.PERSON_BY_ID + key);
        }

        // Finds items in the cache.
        Map<String, PersonModelView> items = (Map<String, PersonModelView>) (Map<String, ? >) getCache().multiGet(
                stringKeys);

        // Determines if any of the items were missing from the cache
        List<Long> uncachedItemKeys = new ArrayList<Long>();
        for (long itemKey : ids)
        {
            String cacheKey = CacheKeys.PERSON_BY_ID + itemKey;
            if (!items.containsKey(cacheKey) || items.get(cacheKey) == null)
            {
                uncachedItemKeys.add(itemKey);
            }
        }

        // One or more of the items were missing in the cache so go to the database
        if (uncachedItemKeys.size() != 0)
        {
            Map<String, PersonModelView> itemMap = new HashMap<String, PersonModelView>();
            Criteria criteria = personQueryStrategy.getCriteria(getHibernateSession());

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

            // get all of the related organization ids for all of the people
            Map<Long, List<Long>> relatedOrgs = getRelatedOrganizationIdsByPersonIdMapper.execute(uncachedItemKeys);

            // get the people
            List<PersonModelView> results = criteria.list();

            for (PersonModelView result : results)
            {
                // set the related org ids to the person model view
                result.setRelatedOrganizationIds(relatedOrgs.get(result.getEntityId()));

                // store the person person modelview in cache
                itemMap.put(CacheKeys.PERSON_BY_ID + result.getEntityId(), result);
            }

            for (String key : itemMap.keySet())
            {
                getCache().set(key, itemMap.get(key));
            }

            items.putAll(itemMap);
        }

        return new ArrayList<PersonModelView>(items.values());
    }
}
