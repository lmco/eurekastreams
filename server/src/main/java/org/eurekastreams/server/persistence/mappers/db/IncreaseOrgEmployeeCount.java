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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.IncreaseOrgEmployeeCountRequest;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Increases a given organization's employee count by a certain amount.
 */
public class IncreaseOrgEmployeeCount extends BaseArgDomainMapper<IncreaseOrgEmployeeCountRequest, Boolean>
{
    /**
     * Logger.
     */
    Log log = LogFactory.make();

    /**
     * Increase the denormalized count of employees in an organization by a given amount.
     * 
     * @param inRequest
     *            The request.
     * @return True.
     */
    @Override
    public Boolean execute(final IncreaseOrgEmployeeCountRequest inRequest)
    {
        int currentCount = (Integer) getEntityManager().createQuery(
                "select descendantEmployeeCount from Organization where id = :orgId").setParameter("orgId",
                inRequest.getOrganizationId()).getSingleResult();

        if (log.isInfoEnabled())
        {
            log.info("Org id #" + inRequest.getOrganizationId() + " has " + currentCount
                    + " descendant employees; incrementing this by " + inRequest.getIncrementBy());
        }

        getEntityManager().createQuery("update Organization set descendantEmployeeCount = :newCount where id = :orgId")
                .setParameter("newCount", currentCount + inRequest.getIncrementBy()).setParameter("orgId",
                        inRequest.getOrganizationId()).executeUpdate();
        return true;
    }
}
