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
package org.eurekastreams.server.action.execution;

import java.util.List;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.domain.Bannerable;
import org.eurekastreams.server.domain.OrganizationChild;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.GetRecursiveParentOrgIds;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Strategy for retrieving the banner id of the first banner-configured org in the parent org hierarchy.
 * @param <T> extends DomainEntity.
 */
public class GetBannerIdByParentOrganizationStrategy<T extends DomainEntity>
{
    /**
     * Local instance of the {@link GetOrganizationsByIds} mapper.
     */
    private final GetOrganizationsByIds orgMapper;

    /**
     * Local instance of the {@link GetRecursiveParentOrgIds} mapper.
     */
    private final GetRecursiveParentOrgIds recursiveParentOrgsMapper;

    /**
     * Local instance of the FindByIdMapper.
     */
    private final FindByIdMapper<T> entityMapper;

    /**
     * Name of the entity for the FindByIdMapper.
     */
    private final String entityName;

    /**
     * Constructor.
     *
     * @param inOrgMapper
     *            - instance of the {@link GetOrganizationsByIds} mapper.
     * @param inRecursiveParentOrgsMapper
     *            - instance of the {@link GetRecursiveParentOrgIds} mapper.
     * @param inEntityMapper - FindByIdMapper instance to be used for the type of entity configured.
     * @param inEntityName - name of the entity for the find by id mapper.
     */
    public GetBannerIdByParentOrganizationStrategy(final GetOrganizationsByIds inOrgMapper,
            final GetRecursiveParentOrgIds inRecursiveParentOrgsMapper, final FindByIdMapper<T> inEntityMapper,
            final String inEntityName)
    {
        orgMapper = inOrgMapper;
        recursiveParentOrgsMapper = inRecursiveParentOrgsMapper;
        entityMapper = inEntityMapper;
        entityName = inEntityName;
    }

    /**
     * This method retrieves the first banner found in the parent org hierarchy of the supplied parent org id.
     *
     * @param inParentOrgId
     *            - org id of the parent of the entity to start searching for a banner id.
     * @param inEntity
     *            - {@link Bannerable} object that will be populated with the correct banner.
     */
    public void getBannerId(final Long inParentOrgId, final Bannerable inEntity)
    {
        OrganizationModelView currentParentOrg = orgMapper.execute(inParentOrgId);
        inEntity.setBannerId(currentParentOrg.getBannerId());
        inEntity.setBannerEntityId(currentParentOrg.getEntityId());

        if (inEntity.getBannerId() == null)
        {
            // Retrieve the recursive list of parent org for the supplied org id.
            List<OrganizationModelView> parentOrgs = orgMapper
                    .execute(recursiveParentOrgsMapper.execute(inParentOrgId));
            // Looping through the orgids in reverse because the direct parent is at the end of this list.
            for (int index = parentOrgs.size() - 1; index >= 0; index--)
            {
                inEntity.setBannerId(parentOrgs.get(index).getBannerId());
                // the first parent org with a configured banner will be returned.
                if (inEntity.getBannerId() != null)
                {
                    inEntity.setBannerEntityId(parentOrgs.get(index).getEntityId());
                    break;
                }
            }
            if (inEntity.getBannerId() == null)
            {
                inEntity.setBannerEntityId(null);
            }
        }
    }

    /**
     * This method retrieves the first banner found in the parent org hierarchy of the parent of
     * the supplied entity id.
     *
     * @param inEntity
     *            - entity for which the parent org will be looked up and the configured banner will be retrieved.
     * @param inEntityId
     *            - id of the entity for which the parent org will be looked up.
     */
    public void getBannerId(final Bannerable inEntity, final Long inEntityId)
    {
        OrganizationChild entity = (OrganizationChild) entityMapper
                .execute(new FindByIdRequest(entityName, inEntityId));
        getBannerId(entity.getParentOrgId(), inEntity);
    }
}
