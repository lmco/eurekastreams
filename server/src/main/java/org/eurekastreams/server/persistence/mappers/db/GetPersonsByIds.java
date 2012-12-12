/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.strategies.PersonQueryStrategy;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * DB mapper to construct and return {@link PersonModelView} objects for ids passed in.
 *
 */
public class GetPersonsByIds extends BaseArgDomainMapper<Collection<Long>, List<PersonModelView>>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Strategy for querying a person model view from the database.
     */
    private final PersonQueryStrategy personQueryStrategy;

    /**
     * Mapper to get back people skills by people ids.
     */
    private final DomainMapper<Collection<Long>, Map<Long, List<String>>> getSkillsForPeopleByPeopleIdsMapper;

    /**
     * Constructor.
     *
     * @param inPersonQueryStrategy
     *            Strategy for querying a person model view from the database.
     * @param inGetSkillsForPeopleByPeopleIdsMapper
     *            mapper to get back people skills by people ids
     */
    public GetPersonsByIds(final PersonQueryStrategy inPersonQueryStrategy,
            final DomainMapper<Collection<Long>, Map<Long, List<String>>> inGetSkillsForPeopleByPeopleIdsMapper)
    {
        personQueryStrategy = inPersonQueryStrategy;
        getSkillsForPeopleByPeopleIdsMapper = inGetSkillsForPeopleByPeopleIdsMapper;
    }

    /**
     *
     * Construct and return {@link PersonModelView} objects for ids passed in.
     *
     * @param inPeopleIds
     *            the list of ids that should be found.
     * @return list of {@link PersonModelView}s.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<PersonModelView> execute(final Collection<Long> inPeopleIds)
    {
        // Checks to see if there's any real work to do
        if (inPeopleIds == null || inPeopleIds.size() == 0)
        {
            return new ArrayList<PersonModelView>();
        }

        // get the people
        Criteria criteria = personQueryStrategy.getCriteria(getHibernateSession());
        criteria.add(Restrictions.in("this.id", inPeopleIds));
        List<PersonModelView> results = criteria.list();

        // get all the skills for all of the people
        Map<Long, List<String>> skillsForPeople = getSkillsForPeopleByPeopleIdsMapper.execute(inPeopleIds);

        // set the related org ids to the person model view
        for (PersonModelView result : results)
        {
            if (skillsForPeople.containsKey(result.getEntityId()))
            {
                List<String> interests = skillsForPeople.get(result.getEntityId());
                log.debug("Found " + interests.size() + " interests for " + result.getAccountId() + ": "
                        + interests.toString());
                result.setInterests(interests);
            }
            else
            {
                log.debug("Found 0 interests for " + result.getAccountId());
                result.setInterests(new ArrayList<String>());
            }
        }
        return results;
    }
}
