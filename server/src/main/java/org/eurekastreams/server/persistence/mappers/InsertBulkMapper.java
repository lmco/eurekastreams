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
package org.eurekastreams.server.persistence.mappers;

import java.util.List;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceListRequest;
import org.hibernate.Session;

/**
 * Inserts a bunch of things using the preferred hibernate way of bulk updating.
 *
 * @param <TDomainEntityType> the domain entity.
 */
@SuppressWarnings("unchecked")
public class InsertBulkMapper<TDomainEntityType extends DomainEntity> extends
        BaseArgDomainMapper<PersistenceListRequest, Boolean>
{
    /**
     * The number of items to do per flush and clear.
     */
    private static final int BULKNUM = 20;
    
    // TODO: verify that these things get indexed.
    /**
     * Inserts the DomainEntity.
     * 
     * @param inRequest
     *            The MapperRequest.
     * @return true if inserted.
     */
    public Boolean execute(final PersistenceListRequest inRequest)
    {
        List entities = inRequest.getDomainEnities();
        Session session = getHibernateSession();

        for (int i = 0; i < entities.size(); i++)
        {
            session.save(entities.get(i));
            if (i % BULKNUM == 0)
            {
                session.flush();
                session.clear();
            }
        }

        getEntityManager().getTransaction().commit();
        session.close();
        return true;
    }

}
