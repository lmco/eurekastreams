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

import org.eurekastreams.server.action.request.SetPersonLockedStatusRequest;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Mapper to set account status for a person.
 * 
 */
public class SetPersonLockedStatus extends BaseArgDomainMapper<SetPersonLockedStatusRequest, Boolean>
{
    /**
     * Set account status for a person.
     * 
     * @param inRequest
     *            {@link SetPersonLockedStatusRequest}.
     * @return true if successful.
     */
    @Override
    public Boolean execute(final SetPersonLockedStatusRequest inRequest)
    {
        getEntityManager().createQuery(
                "UPDATE Person SET accountLocked = :lockedStatus  WHERE accountId = :personAccountId").setParameter(
                "lockedStatus", inRequest.getLockedStatus()).setParameter("personAccountId",
                inRequest.getPersonAccountId()).executeUpdate();
        return true;
    }
}
