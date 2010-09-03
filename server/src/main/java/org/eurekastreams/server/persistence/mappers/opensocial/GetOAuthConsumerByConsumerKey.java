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

import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.persistence.mappers.ReadMapper;

/**
 * Mapper for retrieving OAuth consumer object.
 */
public class GetOAuthConsumerByConsumerKey extends ReadMapper<String, OAuthConsumer>
{
    /**
     * Execute method that retrieves the oauth consumer object.
     * 
     * @param inRequest
     *            String containing the key for the consumer being queried.
     * @return OAuthDomainConsumer - instance of the oauth consumer, if found.
     */
    @Override
    @SuppressWarnings("unchecked")
    public OAuthConsumer execute(final String inRequest)
    {
        Query q = getEntityManager().createQuery("from OAuthConsumer c where c.consumerKey = :key").setParameter("key",
                inRequest);

        List results = q.getResultList();
        return (results.size() == 0) ? null : (OAuthConsumer) results.get(0);
    }
}
