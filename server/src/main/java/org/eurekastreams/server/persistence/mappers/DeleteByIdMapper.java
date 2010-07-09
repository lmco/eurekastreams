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
package org.eurekastreams.server.persistence.mappers;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Delete an entity by id.
 *
 */
public class DeleteByIdMapper extends BaseArgDomainMapper<FindByIdRequest, Boolean>
{
    /**
     * Deletes a DomainEntity by ID.
     * 
     * @param inRequest
     *            The MapperRequest.
     * @return the requested domain entity. Returns null if no results.
     */
    public Boolean execute(final FindByIdRequest inRequest)
    {
    	Query q = getEntityManager().createQuery(
         "DELETE from " + inRequest.getEntityName() + " where id = :domainEntityId");
    	q.setParameter("domainEntityId", inRequest.getEntityId());
 		q.executeUpdate();

 		return Boolean.TRUE;
    }
}
