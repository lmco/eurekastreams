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

import java.util.List;

import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Refreshes cached org activity.
 */
public class OrgActivityIdsRefresher extends CachedDomainMapper implements RefreshStrategy<String, List<Long>>
{
    /**
     * @param inOrgShortName
     *            the org to refresh.
     * @param inActivities
     *            the updated activity.
     */
    public void refresh(final String inOrgShortName, final List<Long> inActivities)
    {
        getCache().setList(CacheKeys.ACTIVITY_IDS_FOR_ORG_BY_SHORTNAME_RECURSIVE + inOrgShortName, inActivities);
    }

}
