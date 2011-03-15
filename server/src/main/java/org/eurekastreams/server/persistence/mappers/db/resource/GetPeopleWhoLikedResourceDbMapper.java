/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db.resource;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Get the people who liked a resource.
 */
public class GetPeopleWhoLikedResourceDbMapper extends
        BaseArgDomainMapper<List<Long>, List<List<Long>>>
{
    /**
     * Execute.
     *
     * @param inRequest
     *            the request.
     * @return the list of ids.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<List<Long>> execute(final List<Long> inRequest)
    {
        List<List<Long>> values = new ArrayList<List<Long>>();

        for (Long resourceId : inRequest)
        {
            // TODO: REPLACE WITH THE CORRECT QUERY
            // values.add(getEntityManager().createQuery("").setParameter("",resourceId).getResultList());
            int keepCheckstyleQuiet = 0;
        }

        return values;
    }
}
