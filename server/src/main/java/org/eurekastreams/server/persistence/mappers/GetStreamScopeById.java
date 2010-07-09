/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * Persist CompositeStream.
 *
 */
public class GetStreamScopeById extends BaseArgDomainMapper<Set<StreamScope>, Set<StreamScope>>
{

    /**
     * Return attached StreamScope entities based on ids of incoming scopes.
     * @param inRequest the list of StreamScopes to look up.
     * @return The list of attached StreamScopes.
     */
    //TODO:Was using Hibernate session.load method to try to create association 
    //to streamView without fetching streamscopes. It worked as far as successfully
    //persisting to DB, but was running into 
    //lazy loading errors when trying to return the entity. Had to back down to fetching
    //entities via this mapper, but would like to investigate further to see if this can be
    //made faster.
    @SuppressWarnings("unchecked")
    @Override
    public Set<StreamScope> execute(final Set<StreamScope> inRequest)
    {                
        if (inRequest.size() == 0)
        {
            return new HashSet<StreamScope>();
        }
        
        List<Long> ids = new ArrayList<Long>(inRequest.size());
        for (StreamScope s : inRequest)
        {
            ids.add(s.getId());
        }
        
        Criteria criteria = getHibernateSession().createCriteria(StreamScope.class); 

        criteria.add(Restrictions.in("this.id", ids));
        
        HashSet<StreamScope> results = new HashSet<StreamScope>();
        results.addAll(criteria.list());
        
        return results;
    }    
}
