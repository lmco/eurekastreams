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
import java.util.Map;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.GetRelatedOrganizationIdsByPersonId;
import org.eurekastreams.server.persistence.strategies.PersonQueryStrategy;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * DB mapper to construct and return {@link PersonModelView} objects for ids passed in.
 * 
 */
public class GetPersonsByIds extends BaseArgDomainMapper<List<Long>, List<PersonModelView>>
{
    /**
     * Strategy for querying a person model view from the database.
     */
    private PersonQueryStrategy personQueryStrategy;

    /**
     * Mapper to get related org ids by person id.
     */
    private GetRelatedOrganizationIdsByPersonId getRelatedOrganizationIdsByPersonIdMapper;

    /**
     * Constructor.
     * 
     * @param inPersonQueryStrategy
     *            Strategy for querying a person model view from the database.
     * @param inGetRelatedOrganizationIdsByPersonIdMapper
     *            Mapper to get related org ids by person id.
     */
    public GetPersonsByIds(final PersonQueryStrategy inPersonQueryStrategy,
            final GetRelatedOrganizationIdsByPersonId inGetRelatedOrganizationIdsByPersonIdMapper)
    {
        personQueryStrategy = inPersonQueryStrategy;
        getRelatedOrganizationIdsByPersonIdMapper = inGetRelatedOrganizationIdsByPersonIdMapper;
    }

    /**
     * 
     * Construct and return {@link PersonModelView} objects for ids passed in.
     * 
     * @param inRequest
     *            the list of ids that should be found.
     * @return list of {@link PersonModelView}s.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<PersonModelView> execute(final List<Long> inRequest)
    {
        List<Long> ids = inRequest;

        // Checks to see if there's any real work to do
        if (ids == null || ids.size() == 0)
        {
            return new ArrayList<PersonModelView>();
        }

        Criteria criteria = personQueryStrategy.getCriteria(getHibernateSession());

        criteria.add(Restrictions.in("this.id", ids));

        // get all of the related organization ids for all of the people
        Map<Long, List<Long>> relatedOrgs = getRelatedOrganizationIdsByPersonIdMapper.execute(ids);

        // get the people
        List<PersonModelView> results = criteria.list();

        for (PersonModelView result : results)
        {
            // set the related org ids to the person model view
            result.setRelatedOrganizationIds(relatedOrgs.get(result.getEntityId()));
        }

        return results;
    }

}
