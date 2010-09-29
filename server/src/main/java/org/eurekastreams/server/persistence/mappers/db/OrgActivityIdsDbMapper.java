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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.strategies.DescendantOrganizationStrategy;

/**
 * Find all activity associated with an organization from the database.
 */
public class OrgActivityIdsDbMapper extends BaseArgDomainMapper<String, List<Long>>
{
    /**
     * Strategy for finding descentents.
     */
    private DescendantOrganizationStrategy descendantOrganizationStrategy = null;

    /**
     * @param inOrgShortName
     *            the org's short name.
     * @return the activity associated with that org.
     */
    public List<Long> execute(final String inOrgShortName)
    {
        long orgId = descendantOrganizationStrategy.getOrgIdByShortName(inOrgShortName);
        if (orgId == 0)
        {
            // no results - missing org
            return new ArrayList<Long>();
        }
        final String descendentString = descendantOrganizationStrategy.getDescendantOrganizationIdsForJpql(orgId,
                new HashMap<String, String>());
        final List<Long> descendents = new ArrayList<Long>();

        for (String idStr : Arrays.asList(descendentString.split(",")))
        {
            if (idStr.trim().length() > 0)
            {
                descendents.add(Long.parseLong(idStr));
            }
        }

        descendents.add(orgId);

        String query = "SELECT id FROM Activity WHERE recipientParentOrg.id in (:orgIds) ORDER BY id DESC";

        return getEntityManager().createQuery(query).setParameter("orgIds", descendents).getResultList();
    }

    /**
     * @param inDescendantOrganizationStrategy
     *            the descendantOrganizationStrategy to set
     */
    public void setDescendantOrganizationStrategy(final DescendantOrganizationStrategy inDescendantOrganizationStrategy)
    {
        this.descendantOrganizationStrategy = inDescendantOrganizationStrategy;
    }
}
