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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdateCachedBannerMapperRequest;
import org.eurekastreams.server.persistence.mappers.stream.BaseArgCachedDomainMapper;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * This mapper updates the banner id in the cached organization model view object based on inputs.
 *
 */
public class UpdateCachedOrgBannerIdMapper extends BaseArgCachedDomainMapper<UpdateCachedBannerMapperRequest, Object>
{
    /**
     * Local instance of the {@link GetOrganizationsByIds} mapper.
     */
    private final DomainMapper<List<Long>, List<OrganizationModelView>> cachedOrgMapper;

    /**
     * Constructor.
     *
     * @param inCachedOrgMapper
     *            instance of the {@link GetOrganizationsByIds} mapper.
     */
    public UpdateCachedOrgBannerIdMapper(final DomainMapper<List<Long>, List<OrganizationModelView>> inCachedOrgMapper)
    {
        cachedOrgMapper = inCachedOrgMapper;
    }

    /**
     * Retrieve the requested {@link OrganizationModelView}, set the new banner id and save it back to cache.
     *
     * @param inRequest
     *            - instance of {@link UpdateCachedBannerMapperRequest} that contains the entity id and banner id to
     *            update.
     * @return - nothing to return.
     */
    @Override
    public Object execute(final UpdateCachedBannerMapperRequest inRequest)
    {
        List<Long> orgIdLists = new ArrayList<Long>();
        orgIdLists.add(inRequest.getEntityId());
        OrganizationModelView currentOrgDTO = cachedOrgMapper.execute(orgIdLists).get(0);
        currentOrgDTO.setBannerId(inRequest.getBannerId());
        getCache().set(CacheKeys.ORGANIZATION_BY_ID + inRequest.getEntityId(), currentOrgDTO);
        return null;
    }
}
