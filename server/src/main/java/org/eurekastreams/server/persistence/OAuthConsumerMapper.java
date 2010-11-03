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
package org.eurekastreams.server.persistence;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.OAuthConsumer;

/**
 * This class provides the mapper functionality for OAuthConsumer entities.
 */
public class OAuthConsumerMapper extends DomainEntityMapper<OAuthConsumer>
{
    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public OAuthConsumerMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Get the domain entity name for the generic query operations.
     *
     * @return the domain entity name for the gadget query operations.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "OAuthConsumer";
    }

    /**
     * Find a specific consumer.
     *
     * @param inServiceProviderName
     *            Nickname for the service provider.
     * @param inGadgetUrl
     *            URL for the associated gadget.
     *
     * @return the consumer that matches the parameters (if any).
     */
    @SuppressWarnings("unchecked")
    public OAuthConsumer findConsumerByServiceNameAndGadgetUrl(final String inServiceProviderName,
            final String inGadgetUrl)
    {
        Query q = getEntityManager().createQuery(
                "from OAuthConsumer c where c.serviceProviderName = :serviceName and c.gadgetUrl = :gadgetUrl")
                .setParameter("serviceName", inServiceProviderName).setParameter("gadgetUrl", inGadgetUrl);

        List results = q.getResultList();
        return (results.size() == 0) ? null : (OAuthConsumer) results.get(0);
    }

    /**
     * Find a specific consumer.
     *
     * @param inConsumerKey
     *            The key for the consumer.
     *
     * @return the consumer that matches the parameters (if any).
     */
    @SuppressWarnings("unchecked")
    public OAuthConsumer findConsumerByConsumerKey(final String inConsumerKey)
    {
        Query q = getEntityManager().createQuery(
                "from OAuthConsumer c where c.consumerKey = :key")
                .setParameter("key", inConsumerKey);

        List results = q.getResultList();
        return (results.size() == 0) ? null : (OAuthConsumer) results.get(0);
    }
}
