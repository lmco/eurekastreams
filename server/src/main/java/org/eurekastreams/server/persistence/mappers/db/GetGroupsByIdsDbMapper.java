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
import org.eurekastreams.server.persistence.strategies.DomainGroupQueryStrategy;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * Return list of DomainGroupModelViews from DB for ids passed in.
 * 
 */
public class GetGroupsByIdsDbMapper extends BaseArgDomainMapper<List<Long>, List<DomainGroupModelView>>
{
    /**
     * Strategy for querying a domain group model view from the database.
     */
    private DomainGroupQueryStrategy domainGroupQueryStrategy;

    /**
     * Constructor.
     * 
     * @param inDomainGroupQueryStrategy
     *            query strategy for loading {@link DomainGroupModelView}s.
     */
    public GetGroupsByIdsDbMapper(final DomainGroupQueryStrategy inDomainGroupQueryStrategy)
    {
        domainGroupQueryStrategy = inDomainGroupQueryStrategy;
    }

    /**
     * Return list of DomainGroupModelViews from DB for ids passed in.
     * 
     * @param inRequest
     *            list of group ids.
     * @return list of DomainGroupModelViews from DB for ids passed in.
     */
    @Override
    public List<DomainGroupModelView> execute(final List<Long> inRequest)
    {
        List<Long> ids = inRequest;

        // Checks to see if there's any real work to do
        if (ids == null || ids.size() == 0)
        {
            return new ArrayList<DomainGroupModelView>();
        }

        Criteria criteria = domainGroupQueryStrategy.getCriteria(getHibernateSession());

        criteria.add(Restrictions.in("this.id", ids));

        return criteria.list();
    }
}
