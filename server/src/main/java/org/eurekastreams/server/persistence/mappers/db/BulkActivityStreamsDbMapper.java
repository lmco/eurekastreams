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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.service.actions.strategies.activity.ListCollider;

/**
 * Bulk maps activity streams from the database.
 */
public class BulkActivityStreamsDbMapper extends BaseArgDomainMapper<List<Long>, List<Long>> implements
        DomainMapper<List<Long>, List<Long>>
{
    /**
     * Or collider, used to cross the streams.
     */
    private ListCollider orCollider = null;

    /**
     * Max items to return.
     */
    private int maxItems = 0;

    /**
     * Set the Or collider.
     * 
     * @param inOrCollider
     *            the collider.
     */
    public void setOrCollider(final ListCollider inOrCollider)
    {
        orCollider = inOrCollider;
    }

    /**
     * Set the max items to return.
     * 
     * @param inMaxItems
     *            the max items.
     */
    public void setMaxItems(final int inMaxItems)
    {
        maxItems = inMaxItems;
    }

    /**
     * Executes the mapper.
     * 
     * @param inRequest
     *            the request.
     * @return the list of activity IDs.
     */
    @SuppressWarnings("unchecked")
    public List<Long> execute(final List<Long> inRequest)
    {
        List<Long> items = new ArrayList<Long>();

        for (Long id : inRequest)
        {
            String query = "SELECT id FROM Activity WHERE recipientStreamScope.id = :streamId ORDER BY id";
            items = orCollider.collide(items, getEntityManager().createQuery(query).setParameter("streamId", id)
                    .getResultList(), maxItems);
        }

        return items;
    }

}
