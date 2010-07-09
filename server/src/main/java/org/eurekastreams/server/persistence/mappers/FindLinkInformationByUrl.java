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

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.server.persistence.mappers.requests.UniqueStringRequest;

/**
 * Find link information by URL.
 */
public class FindLinkInformationByUrl extends ReadMapper<UniqueStringRequest, LinkInformation>
{
    /**
     * Cache expiration in milliseconds.
     */
    private long expirationInMilliseconds = 0;

    /**
     * Expiration setter.
     *
     * @param inExpirationInMilliseconds
     *            expiration time of cached links in milliseconds.
     */
    public void setExpirationInMilliseconds(final long inExpirationInMilliseconds)
    {
        expirationInMilliseconds = inExpirationInMilliseconds;
    }

    /**
     * Execute the mapper request.
     *
     * @param inRequest
     *            the request.
     * @return the link information.
     */
    @SuppressWarnings("unchecked")
    @Override
    public LinkInformation execute(final UniqueStringRequest inRequest)
    {
        Query q = getEntityManager().createQuery("from LinkInformation where url = :url").setParameter("url",
                inRequest.getUniqueId());
        List<LinkInformation> results = q.getResultList();

        LinkInformation result = null;

        if (results.size() > 0)
        {
            result = results.get(0);
            Date now = new Date();
            if (result.getCreated().getTime() < (now.getTime() - expirationInMilliseconds))
            {
                getEntityManager().remove(result);
                result = null;
            }
        }

        return result;
    }
}
