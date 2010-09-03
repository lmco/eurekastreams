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

import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.OAuthToken;
import org.eurekastreams.server.persistence.mappers.ReadMapper;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthTokenRequest;

/**
 * Mapper for retrieving an OAuth token.
 */
public class GetOAuthToken extends ReadMapper<OAuthTokenRequest, OAuthToken>
{
    /**
     * Retrieves the OAuthToken object.
     * 
     * @param inRequest
     *            Object containing the data necessary to query for oauth token.
     * @return OAuthToken - instance of the oauth token, if found.
     */
    @Override
    @SuppressWarnings("unchecked")
    public OAuthToken execute(final OAuthTokenRequest inRequest)
    {
        Query q = getEntityManager().createQuery(
                "from OAuthToken t where t.consumer.serviceProviderName = :serviceName"
                        + " and t.consumer.gadgetUrl = :gadgetUrl and t.viewerId = :viewerId and t.ownerId = :ownerId")
                .setParameter("serviceName", inRequest.getConsumer().getServiceProviderName()).setParameter(
                        "gadgetUrl", inRequest.getConsumer().getGadgetUrl()).setParameter("viewerId",
                        inRequest.getViewerId()).setParameter("ownerId", inRequest.getOwnerId());

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
            getEntityManager().remove(token);
            return null;
        }
    }
}
