/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.eurekastreams.server.action.request.UpdateStickyActivityRequest;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * DAO to set which activity is sticky for a stream.
 */
public class UpdateGroupStickyActivityDbMapper extends BaseArgDomainMapper<UpdateStickyActivityRequest, Void>
{
    /**
     * Set account status for a person.
     *
     * @param inRequest
     *            {@link UpdateStickyActivityRequest}.
     * @return nothing.
     */
    @Override
    public Void execute(final UpdateStickyActivityRequest inRequest)
    {
        getEntityManager().createQuery("UPDATE DomainGroup SET stickyActivityId = :activityId WHERE id = :id")
                .setParameter("id", inRequest.getStreamEntityId())
                .setParameter("activityId", inRequest.getActivityId()).executeUpdate();
        return null;
    }
}
