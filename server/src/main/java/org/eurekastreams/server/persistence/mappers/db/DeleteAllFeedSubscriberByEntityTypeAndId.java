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

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.DeleteAllFeedSubscriberByEntityTypeAndIdRequest;

/**
 * Mapper to remove all entries from FeedSubscriber table that match passed in entity type and id.
 * 
 */
public class DeleteAllFeedSubscriberByEntityTypeAndId extends
        BaseArgDomainMapper<DeleteAllFeedSubscriberByEntityTypeAndIdRequest, Boolean>
{

    /**
     * {@inheritDoc}.
     */
    @Override
    public Boolean execute(final DeleteAllFeedSubscriberByEntityTypeAndIdRequest inRequest)
    {

        getEntityManager().createQuery(
                "DELETE FROM FeedSubscriber fs WHERE fs.entityId = :entityId AND fs.type = :entityType").setParameter(
                "entityId", inRequest.getEntityId()).setParameter("entityType", inRequest.getEntityType())
                .executeUpdate();

        return Boolean.TRUE;
    }
}
