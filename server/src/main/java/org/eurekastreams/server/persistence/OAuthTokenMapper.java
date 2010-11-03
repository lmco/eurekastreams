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

import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.domain.OAuthToken;

/**
 * This class provides the mapper functionality for OAuthToken entities.
 */
public class OAuthTokenMapper extends DomainEntityMapper<OAuthToken>
{
    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public OAuthTokenMapper(final QueryOptimizer inQueryOptimizer)
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
        return "OAuthToken";
    }

    /**
     * Find a specific token.
     *
     * @param inConsumer
     *            OAuthConsumer for the token.
     * @param inViewerId
     *            viewer id of the token.
     * @param inOwnerId
     *            owner id of the token.
     *
     * @return the token that matches the parameters (if any).
     */
    @SuppressWarnings("unchecked")
    public OAuthToken findToken(final OAuthConsumer inConsumer, final String inViewerId, final String inOwnerId)
    {
        Query q = getEntityManager().createQuery(
                "from OAuthToken t where t.consumer.serviceProviderName = :serviceName"
                        + " and t.consumer.gadgetUrl = :gadgetUrl and t.viewerId = :viewerId and t.ownerId = :ownerId")
                .setParameter("serviceName", inConsumer.getServiceProviderName()).setParameter("gadgetUrl",
                        inConsumer.getGadgetUrl()).setParameter("viewerId", inViewerId).setParameter("ownerId",
                        inOwnerId);

        List results = q.getResultList();
        if (results.size() == 0)
        {
            return null;
        }
        OAuthToken token = (OAuthToken) results.get(0);

        Long ms = token.getTokenExpireMillis();

        // Checks for token expiration;
        if (ms == null || ms == 0 || ms > Calendar.getInstance().getTimeInMillis())
        {
            return token;
        }
        else
        {
            delete(token.getId());
            return null;
        }
    }

    /**
     * Delete a token.
     *
     * @param inTokenId
     *            The id of the OAuthToken to delete.
     */
    public void delete(final long inTokenId)
    {
        OAuthToken token = findById(inTokenId);
        if (token != null)
        {
            getEntityManager().remove(token);
        }
    }

    /**
     * Delete a token.
     *
     * @param inConsumer
     *            The consumer of the token to delete.
     * @param inViewerId
     *            The gadget viewer id for the token to delete.
     * @param inOwnerId
     *            The gadget owner id for the token to delete.
     */
    public void delete(final OAuthConsumer inConsumer, final String inViewerId, final String inOwnerId)
    {
        OAuthToken token = findToken(inConsumer, inViewerId, inOwnerId);
        if (token != null)
        {
            delete(token.getId());
        }
    }
}
