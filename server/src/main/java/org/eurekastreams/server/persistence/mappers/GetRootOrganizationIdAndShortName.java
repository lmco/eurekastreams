/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Mapper to get the root organization id from local memory, loading into if not yet set. We're able to store this in
 * local memory rather than Memcache because these two values never change. This doesnt' store the root org's name, just
 * the short name and id.
 */
public class GetRootOrganizationIdAndShortName extends BaseDomainMapper
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(GetRootOrganizationIdAndShortName.class);

    /**
     * Local in-memory copy of the root organization id.
     */
    private Long rootOrganizationId;

    /**
     * Local in-memory copy of the root organization's short name.
     */
    private String rootOrganizationShortName;

    /**
     * Get the root organization id from memory, or load it into memory if not already.
     * 
     * @return the root organization id
     */
    public Long getRootOrganizationId()
    {
        fetchRootOrg();
        return rootOrganizationId;
    }

    /**
     * Get the root organization short name from memory, or load it into memory if not already.
     * 
     * @return the short name of the root organization
     */
    public String getRootOrganizationShortName()
    {
        fetchRootOrg();
        return rootOrganizationShortName;
    }

    /**
     * Fetch the root org information if not already loaded.
     */
    @SuppressWarnings("unchecked")
    private void fetchRootOrg()
    {
        if (rootOrganizationId == null || rootOrganizationShortName == null)
        {
            log.info("Looking up root org in the database to store in cache.");

            // look it up
            String queryString = "SELECT id, shortName FROM Organization WHERE id = parentOrganization.id";
            Query query = getEntityManager().createQuery(queryString);
            List<Object[]> results = query.getResultList();
            if (results.size() > 0)
            {
                synchronized (this)
                {
                    rootOrganizationId = (Long) results.get(0)[0];
                    rootOrganizationShortName = (String) results.get(0)[1];
                    log.info("Retrieved " + rootOrganizationId + " as root organization id and "
                            + rootOrganizationShortName
                            + " as root organization short name and saved in global variable.");
                }
            }
        }
    }
}
