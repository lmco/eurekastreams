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
package org.eurekastreams.server.persistence.mappers.stream;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdateActivityFlagRequest;

/**
 * Mapper to update the flagged state of an activity.
 */
public class UpdateActivityFlag extends BaseArgDomainMapper<UpdateActivityFlagRequest, Boolean>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean execute(final UpdateActivityFlagRequest inRequest)
    {
        // Note: This approach bypasses Lucene and only updates the database. It's ok as long as 'flagged' is not
        // indexed for search.
        int rowsUpdated =
                getEntityManager().createQuery(
                "UPDATE Activity SET flagged = :flagged WHERE id = :id AND flagged <> :flagged").setParameter("id",
                inRequest.getActivityId()).setParameter("flagged", inRequest.isToFlag()).executeUpdate();

        return rowsUpdated != 0;
    }
}
