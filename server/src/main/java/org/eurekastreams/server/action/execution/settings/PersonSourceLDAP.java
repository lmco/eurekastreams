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
package org.eurekastreams.server.action.execution.settings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.dto.MembershipCriteriaDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.service.actions.strategies.PersonLookupStrategy;

/**
 * LDAP source for user information.
 * 
 */
public class PersonSourceLDAP implements PersonSource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * The membership criteria mapper.
     */
    private DomainMapper<MapperRequest, List<MembershipCriteriaDTO>> membershipCriteriaDAO;

    /**
     * Group lookup strategy.
     */
    private PersonLookupStrategy groupLookupStrategy;

    /**
     * Attribute-based lookup strategy.
     */
    private PersonLookupStrategy attributeLookupStrategy;

    /**
     * Constructor.
     * 
     * @param inMembershipCriteriaDAO
     *            mapper to get membership criteria.
     * @param inGroupLookupStrategy
     *            group lookup mapper.
     * @param inAttributeLookupStrategy
     *            person lookup mapper.
     */
    public PersonSourceLDAP(final DomainMapper<MapperRequest, List<MembershipCriteriaDTO>> inMembershipCriteriaDAO,
            final PersonLookupStrategy inGroupLookupStrategy, final PersonLookupStrategy inAttributeLookupStrategy)
    {
        membershipCriteriaDAO = inMembershipCriteriaDAO;
        groupLookupStrategy = inGroupLookupStrategy;
        attributeLookupStrategy = inAttributeLookupStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Person> getPeople()
    {
        List<MembershipCriteriaDTO> membershipCriteria = membershipCriteriaDAO.execute(null);

        HashSet<Person> results = new HashSet<Person>();

        PersonLookupStrategy lookupStrategy = null;
        for (MembershipCriteriaDTO criterion : membershipCriteria)
        {
            String ldapQuery = criterion.getCriteria();
            log.info("Processing criteria: " + ldapQuery);

            if (ldapQuery.contains("="))
            {
                lookupStrategy = attributeLookupStrategy;
            }
            else
            {
                lookupStrategy = groupLookupStrategy;
            }

            List<Person> people = lookupStrategy.findPeople(ldapQuery, new Integer(Integer.MAX_VALUE));
            log.info(people.size() + " people found for criteria.");
            results.addAll(people);
        }
        return results;
    }

}
