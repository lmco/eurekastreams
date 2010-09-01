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

import java.util.Collection;

import org.eurekastreams.server.domain.stream.ActivitySecurityDTO;
import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Refreshes activity security information.
 */
public class BulkActivitySecurityRefresher extends CachedDomainMapper implements
        RefreshStrategy<Collection<Long>, Collection<ActivitySecurityDTO>>
{
    /**
     * @param request
     *            the list of IDs to refresh.
     * @param response
     *            the information to use to refresh the IDs.
     */
    public void refresh(final Collection<Long> request, final Collection<ActivitySecurityDTO> response)
    {
        for (ActivitySecurityDTO actSec : response)
        {
            getCache().set(CacheKeys.ACTIVITY_SECURITY_BY_ID + actSec.getId(), actSec);
        }
    }
}
