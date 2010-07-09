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

import org.eurekastreams.commons.hibernate.ModelViewResultTransformer;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.search.factories.OrganizationModelViewFactory;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * Gets a list of organization objects for a given list of org ids.
 */
public class GetOrganizationsByIds extends CachedDomainMapper
{
    /**
     * Refresh all Organizations in cache from database. Should be called during cache warming.
     *
     * @return list of stream objects.
     */
    public List<OrganizationModelView> execute()
    {
        Map<String, OrganizationModelView> streams = refresh(null);
        return new ArrayList<OrganizationModelView>(streams.values());
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
    public List<OrganizationModelView> execute(final List<Long> ids)
    {
        // Checks to see if there's any real work to do
        if (ids == null || ids.size() == 0)
        {
            return new ArrayList<OrganizationModelView>();
        }

        List<String> stringKeys = new ArrayList<String>();
        for (long key : ids)
        {
            stringKeys.add(CacheKeys.ORGANIZATION_BY_ID + key);
        }

        // Finds items in the cache.
        Map<String, OrganizationModelView> items = (Map<String, OrganizationModelView>) (Map<String, ? >) getCache()
                .multiGet(stringKeys);

        // Determines if any of the items were missing from the cache
        List<Long> uncachedItemKeys = new ArrayList<Long>();
        for (long itemKey : ids)
        {
            String cacheKey = CacheKeys.ORGANIZATION_BY_ID + itemKey;
            if (!items.containsKey(cacheKey) || items.get(cacheKey) == null)
            {
                uncachedItemKeys.add(itemKey);
            }
        }

        // One or more of the items were missing in the cache so go to the database
        if (uncachedItemKeys.size() != 0)
        {
            items.putAll(refresh(uncachedItemKeys));
        }

        List<OrganizationModelView> returnOrgs = new ArrayList<OrganizationModelView>();
        for (Long orgId : ids)
        {
            returnOrgs.add(items.get(CacheKeys.ORGANIZATION_BY_ID + orgId));
        }

        return returnOrgs;
    }

    /**
     * Looks in cache for the necessary DTO and returns it if found. Otherwise, makes a database call, puts it in cache,
     * and returns it.
     *
     * @param id
     *            id that should be found.
     * @return DTO object.
     */
    public OrganizationModelView execute(final Long id)
    {
        return execute(Collections.singletonList(id)).get(0);
    }

    /**
     * Gets items from database and stores them in cache.
     *
     * @param uncached
     *            list of items to be refreshed. If this is null, all items will be refreshed from database.
     * @return the map of refreshed items.
     */
    @SuppressWarnings("unchecked")
    private Map<String, OrganizationModelView> refresh(final List<Long> uncached)
    {
        Map<String, OrganizationModelView> itemMap = new HashMap<String, OrganizationModelView>();
        Criteria criteria = getHibernateSession().createCriteria(Organization.class);
        ProjectionList fields = Projections.projectionList();
        fields.add(getColumn("id"));
        fields.add(Projections.property("parentOrganization.id").as("parentOrganizationId"));
        fields.add(getColumn("description"));
        fields.add(getColumn("name"));
        fields.add(getColumn("shortName"));
        fields.add(getColumn("childOrganizationCount"));
        fields.add(getColumn("descendantGroupCount"));
        fields.add(getColumn("descendantEmployeeCount"));
        fields.add(Projections.property("employeeFollowerCount").as("followersCount"));
        fields.add(getColumn("updatesCount"));
        fields.add(getColumn("avatarId"));
        fields.add(getColumn("overview"));
        fields.add(Projections.property("entityStreamView.id").as("compositeStreamId"));
        fields.add(Projections.property("stream.id").as("streamId"));
        fields.add(getColumn("bannerId"));
        criteria.setProjection(fields);

        criteria.createAlias("streamScope", "stream");

        // Creates the necessary "OR" clauses to get all uncached items
        if (uncached != null)
        {
            Criterion restriction = null;
            for (int i = 0; i < uncached.size(); i++)
            {
                long key = uncached.get(i);
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
        }

        ModelViewResultTransformer<OrganizationModelView> resultTransformer =
            new ModelViewResultTransformer<OrganizationModelView>(new OrganizationModelViewFactory());
        criteria.setResultTransformer(resultTransformer);

        List<OrganizationModelView> results = criteria.list();
        for (OrganizationModelView result : results)
        {
            itemMap.put(CacheKeys.ORGANIZATION_BY_ID + result.getEntityId(), result);
        }

        for (String key : itemMap.keySet())
        {
            getCache().set(key, itemMap.get(key));
        }

        return itemMap;
    }
}
