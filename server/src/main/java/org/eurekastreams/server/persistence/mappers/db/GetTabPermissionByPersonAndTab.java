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

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;

/**
 * Mapper that retrieves true or false whether or not the provided user and tab match in ownership.
 * 
 */
public class GetTabPermissionByPersonAndTab extends BaseDomainMapper
{
    /**
     * Local logging instance.
     */
    private final Log logger = LogFactory.getLog(GetTabPermissionByPersonAndTab.class);

    /**
     * Determine if the id of the person passed in matches as an owner to the tab supplied. 
     * This is scoped only to start page tabs, not profile tabs.
     * 
     * @param inPersonAccountId
     *            - account id of the person to test permissions for.
     * @param inTabId
     *            - id of the tab to verify is within the start page tabgroup of the person passed in.
     * @return true if the user owns the tab, otherwise false.
     */
    public boolean execute(final String inPersonAccountId, final Long inTabId)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Determinging permissions for tab id: " + inTabId 
                    + " and person id: " + inPersonAccountId);
        }
        try
        {
            Query q = getEntityManager().createQuery(
                    "Select count(*) from Person p, Tab t " 
                        + "where t.id =:tabId and p.accountId =:personAccountId and p.startTabGroup.id = t.tabGroup.id")
                        .setParameter("tabId", inTabId)
                        .setParameter("personAccountId", inPersonAccountId);
            Long count = (Long) q.getSingleResult();

            if (logger.isDebugEnabled())
            {
                logger.trace("Determined permissions for tab id: " + inTabId 
                        + " and person id: " + inPersonAccountId
                        + " value: " + (count > 0));
            }

            return count > 0;
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving permissions, fail over to denying permission.", ex);
        }
        return false;
    }
}
