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

import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Mapper to update recipient parent org for all activities posted to a group. Uses the current value of recipient group
 * parent org in db for new value.
 * 
 */
public class SyncParentOrgForGroupActivities extends BaseArgDomainMapper<String, Integer>
{

    /**
     * Update recipient parent org for all activities posted to a group. Uses the current value of recipient group
     * parent org in db for new value.
     * 
     * @param inRequest
     *            Group short name.
     * @return number of activities updated.
     */
    @Override
    public Integer execute(final String inRequest)
    {
        String q = "UPDATE Activity SET recipientParentOrg="
                + "(SELECT parentOrganization FROM DomainGroup WHERE shortName = :groupKey) WHERE id IN "
                + "(SELECT id FROM Activity WHERE recipientStreamScope.scopeType = :scopeType AND "
                + "recipientStreamScope.scopeType.uniqueKey = :groupKey)";
        return getEntityManager().createQuery(q).setParameter("groupKey", inRequest).setParameter("scopeType",
                ScopeType.GROUP).executeUpdate();
    }
}
