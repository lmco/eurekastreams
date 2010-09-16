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

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Mapper for deleting an organization from datastore.
 * 
 */
public class DeleteOrganizationDbMapper extends BaseArgDomainMapper<Long, Boolean>
{
    /**
     * {@link FindByIdMapper}.
     */
    private FindByIdMapper<Organization> groupMapper;

    /**
     * Constructor.
     * 
     * @param inGroupMapper
     *            {@link FindByIdMapper}.
     */
    public DeleteOrganizationDbMapper(final FindByIdMapper<Organization> inGroupMapper)
    {
        groupMapper = inGroupMapper;
    }

    /**
     * Remove the org and adjust org statistics up the tree from delete org.
     * 
     * @param inRequest
     *            The request to delete org.
     * @return true if successful.
     */
    @Override
    public Boolean execute(final Long inRequest)
    {
        Organization org = groupMapper.execute(new FindByIdRequest("Organization", inRequest));

        // This should take care of following:
        // delete from Organization_Task where organizationId=?
        // delete from Organization_Coordinators where Organization_id=?
        // delete from StreamView_StreamScope where StreamView_id=?
        // delete from Organization_Leaders where Organization_id=?
        // delete from Person_RelatedOrganization where organizationId=?
        // delete from Organization where id=? and version=?
        // delete from StreamView where id=? and version=?
        // delete from StreamScope where id=? and version=?
        getEntityManager().remove(org);

        return true;
    }

}
