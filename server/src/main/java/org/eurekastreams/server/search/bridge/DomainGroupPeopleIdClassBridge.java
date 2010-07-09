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
package org.eurekastreams.server.search.bridge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.hibernate.search.bridge.StringBridge;

/**
 * Class bridge that uses an injected DomainGroupMapper to return a String containing all ids of the people that are
 * coordinators or followers of a DomainGroup.
 */
//TODO: this doesn't allow org coordinators - hit cache for this operation
public class DomainGroupPeopleIdClassBridge implements StringBridge
{
    /**
     * Instance of the logger.
     */
    private static Log log = LogFactory.getLog(DomainGroupPeopleIdClassBridge.class);

    /**
     * The static domain group mapper, injected by spring.
     */
    private static DomainGroupMapper domainGroupMapper;

    /**
     * Inject the domainGroupMapper to use for all lookups.
     * 
     * @param inDomainGroupMapper
     *            the domainGroupMapper to use for all lookups
     */
    public static synchronized void setDomainGroupMapper(final DomainGroupMapper inDomainGroupMapper)
    {
        log.info("DomainGroupMapper injected.");
        domainGroupMapper = inDomainGroupMapper;
    }

    /**
     * Convert the input DomainGroup into a space-separated list of contributor and follower person ids.
     * 
     * @param domainGroupObj
     *            a DomainGroup
     * @return the input DomainGroup into a space-separated list of contributor and follower person ids, or null if not
     *         a DomainGroup
     */
    @Override
    public String objectToString(final Object domainGroupObj)
    {
        if (domainGroupObj == null || !(domainGroupObj instanceof DomainGroup))
        {
            return null;
        }
        DomainGroup domainGroup = (DomainGroup) domainGroupObj;

        if (domainGroupMapper == null)
        {
            throw new IllegalStateException("static domainGroupMapper must be injected into this class bridge");
        }

        Long[] peopleIds = domainGroupMapper.getFollowerAndCoordinatorPersonIds(domainGroup);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < peopleIds.length; i++)
        {
            if (i > 0)
            {
                sb.append(" ");
            }
            sb.append(peopleIds[i]);
        }
        return sb.toString();
    }
}
