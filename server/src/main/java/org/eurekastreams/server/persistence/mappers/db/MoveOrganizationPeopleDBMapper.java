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
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MoveOrganizationPeopleRequest;

/**
 * Mapper to update all employees with given source organization to the destination organization. Note: This does NOT
 * modify the person's relatedOrganization collection.
 */
public class MoveOrganizationPeopleDBMapper extends BaseArgDomainMapper<MoveOrganizationPeopleRequest, Set<Long>>
{

    /**
     * update all people with given source organization to the destination organization. Note: This does NOT modify the
     * person's relatedOrganization collection.
     * 
     * @param inRequest
     *            the {@link MoveOrganizationEmployeesRequest}.
     * @return Set of person ids that for people that were updated.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Set<Long> execute(final MoveOrganizationPeopleRequest inRequest)
    {
        String sourceOrgKey = inRequest.getSourceOrganizationKey();
        String destOrgKey = inRequest.getDestinationOrganizationKey();

        // get list of affected persons to return.
        String q = "SELECT id FROM Person WHERE parentOrganization.shortName = :sourceOrgKey";
        List<Long> personIdsAffected = getEntityManager().createQuery(q).setParameter("sourceOrgKey", sourceOrgKey)
                .getResultList();

        // update all affected persons to have destination org as parent organization.
        q = "UPDATE VERSIONED Person SET parentOrganization = (FROM Organization WHERE shortName = :destOrgKey)"
                + " WHERE parentOrganization = (FROM Organization WHERE shortName = :sourceOrgKey)";
        getEntityManager().createQuery(q).setParameter("sourceOrgKey", sourceOrgKey).setParameter("destOrgKey",
                destOrgKey).executeUpdate();

        return new HashSet<Long>(personIdsAffected);
    }
}
