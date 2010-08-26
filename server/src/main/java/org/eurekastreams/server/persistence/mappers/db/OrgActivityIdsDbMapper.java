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
public class OrgActivityIdsDbMapper extends BaseArgDomainMapper<Long, List<Long>>
{
    private DescendantOrganizationStrategy descendantOrganizationStrategy = null;

    /**
     * @param inOrgId
     *            the ord ID.
     * @return the activity associated with that org.
     */
    public List<Long> execute(final Long inOrgId)
    {
        final String descendentString = descendantOrganizationStrategy.getDescendantOrganizationIdsForJpql(inOrgId,
                new HashMap<String, String>());
        final List<Long> descendents = new ArrayList<Long>();

        for (String idStr : Arrays.asList(descendentString.split(",")))
        {
            if (idStr.trim().length() > 0)
            {
                descendents.add(Long.parseLong(idStr));
            }
        }

        descendents.add(inOrgId);

        String query = "SELECT id FROM Activity WHERE recipientParentOrg.id in (:orgIds) ORDER BY id DESC";

        return getEntityManager().createQuery(query).setParameter("orgIds", descendents).getResultList();
    }

    /**
     * @param descendantOrganizationStrategy
     *            the descendantOrganizationStrategy to set
     */
    public void setDescendantOrganizationStrategy(DescendantOrganizationStrategy descendantOrganizationStrategy)
    {
        this.descendantOrganizationStrategy = descendantOrganizationStrategy;
    }
}
