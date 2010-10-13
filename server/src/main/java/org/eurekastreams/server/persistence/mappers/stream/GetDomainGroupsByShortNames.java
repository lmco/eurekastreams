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

import java.util.List;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Gets a list of objects for a given list of pointer ids.
 */
public class GetDomainGroupsByShortNames extends GetItemsByPointerIds<DomainGroupModelView>
{
    /**
     * Bulk mapper.
     */
    private DomainMapper<List<Long>, List<DomainGroupModelView>> bulkDomainGroupsMapper;

    /**
     * Sets bulk mapper.
     * 
     * @param inBulkDomainGroupsMapper
     *            the bulk mapper.
     */
    public void setBulkDomainGroupsMapper(
            final DomainMapper<List<Long>, List<DomainGroupModelView>> inBulkDomainGroupsMapper)
    {
        bulkDomainGroupsMapper = inBulkDomainGroupsMapper;
    }

    /**
     * Execute bulk mapper.
     * 
     * @param ids
     *            the list of ids.
     * 
     * @return list of groups.
     */
    @Override
    protected List<DomainGroupModelView> bulkExecute(final List<Long> ids)
    {
        return bulkDomainGroupsMapper.execute(ids);
    }

    /**
     * Get prefix.
     * 
     * @return prefix.
     */
    @Override
    public String getCachePointerKeyPrefix()
    {
        return CacheKeys.GROUP_BY_SHORT_NAME;
    }

    /**
     * Get class.
     * 
     * @return class.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class getEntityClass()
    {
        return DomainGroup.class;
    }

    /**
     * Get property name.
     * 
     * @return property name.
     */
    @Override
    public String getPointerProperty()
    {
        return "shortName";
    }
}
