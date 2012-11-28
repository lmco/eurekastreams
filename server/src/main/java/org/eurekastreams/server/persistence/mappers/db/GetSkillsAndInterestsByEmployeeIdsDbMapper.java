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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * DB mapper to get all of the interests.
 */
public class GetSkillsAndInterestsByEmployeeIdsDbMapper extends
        BaseArgDomainMapper<Collection<Long>, Map<Long, List<String>>>
{

    /**
     * @see org.eurekastreams.server.persistence.mappers.DomainMapper#execute(java.lang.Object)
     * @param inPersonIds
     *            person ids to search for skills and interests
     * @return a map of PersonID -> skills & interests
     */
    @Override
    public Map<Long, List<String>> execute(final Collection<Long> inPersonIds)
    {
        Map<Long, List<String>> map = new HashMap<Long, List<String>>();

        Query q = getEntityManager().createQuery(
                "SELECT p.id, bgi.name FROM BackgroundItem bgi, Person p, PersonBackgroundItemSkill skill "
                        + "WHERE bgi MEMBER OF p.background.skills AND p.id IN (:personIds) "
                        + "and skill.pk.backgroundItemId = bgi.id and skill.pk.backgroundId = p.background.id "
                        + "ORDER BY skill.pk.sortIndex").setParameter("personIds", inPersonIds);

        List<Object[]> results = q.getResultList();
        for (Object[] interestData : results)
        {
            Long personId = (Long) interestData[0];
            String interest = (String) interestData[1];

            List<String> interests;
            if (map.containsKey(personId))
            {
                interests = map.get(personId);
            }
            else
            {
                interests = new ArrayList<String>();
                map.put(personId, interests);
            }
            interests.add(interest);
        }
        return map;
    }
}
