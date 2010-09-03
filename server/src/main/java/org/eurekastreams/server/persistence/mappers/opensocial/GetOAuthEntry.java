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
package org.eurekastreams.server.persistence.mappers.opensocial;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.mappers.ReadMapper;

/**
 * Mapper for retrieving OAuth domain entry.
 */
public class GetOAuthEntry extends ReadMapper<String, OAuthDomainEntry>
{
    /**
     * Retrieves the OAuthDomainEntry object.
     * 
     * @param inRequest
     *            String containing the token for the oauth entry being queried.
     * @return OAuthDomainEntry - instance of the oauth entry, if found.
     */
    @Override
    @SuppressWarnings("unchecked")
    public OAuthDomainEntry execute(final String inRequest)
    {
        Query q = getEntityManager().createQuery("from OAuthDomainEntry e where e.token = :token").setParameter(
                "token", inRequest);

        List results = q.getResultList();
        return (results.size() == 0) ? null : (OAuthDomainEntry) results.get(0);
    }
}
