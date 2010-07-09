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

import java.util.List;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Gets a list of objects for a given list of pointer ids.
 */
public class GetOrganizationsByShortNames extends GetItemsByPointerIds<OrganizationModelView>
{
    /**
     * Bulk mapper.
     */
    private GetOrganizationsByIds bulkOrganizationsMapper;

    /**
     * Sets bulk mapper.
     * 
     * @param inBulkOrganizationsMapper
     *            the bulk mapper to set.
     */
    public void setBulkOrganizationsMapper(final GetOrganizationsByIds inBulkOrganizationsMapper)
    {
        bulkOrganizationsMapper = inBulkOrganizationsMapper;
    }

    /**
     * Execute bulk mapper.
     * 
     * @param ids
     *           the list of ids.
     * @return list of orgs.
     */
    @Override
    protected List<OrganizationModelView> bulkExecute(final List<Long> ids)
    {
        return bulkOrganizationsMapper.execute(ids);
    }

    /**
     * Get prefix.
     * 
     * @return prefix.
     */
    @Override
    public String getCachePointerKeyPrefix()
    {
        return CacheKeys.ORGANIZATION_BY_SHORT_NAME;
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
        return Organization.class;
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
