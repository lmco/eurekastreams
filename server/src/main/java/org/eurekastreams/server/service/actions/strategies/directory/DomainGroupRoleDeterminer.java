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
package org.eurekastreams.server.service.actions.strategies.directory;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.server.domain.DomainGroup;
import org.hibernate.search.jpa.FullTextQuery;

/**
 * Strategy to determine if a person is either a coordinator or follower of a DomainGroup.
 */
public class DomainGroupRoleDeterminer
{
    /**
     * SearchRequestBuilder to use to look up whether the user is either a follower or coordinator.
     */
    private ProjectionSearchRequestBuilder searchRequestBuilder;

    /**
     * Constructor.
     *
     * @param inSearchRequestBuilder
     *            the search request builder, mostly setup in this constructor but still required as a parameter because
     *            Spring injects the entity manager into it. An empty ProjectionSearchRequestBuilder can be passed into
     *            this constructor.
     */
    public DomainGroupRoleDeterminer(final ProjectionSearchRequestBuilder inSearchRequestBuilder)
    {
        searchRequestBuilder = inSearchRequestBuilder;

        // setup the fields - we might not need any fields, but just in case, specify id
        List<String> fields = new ArrayList<String>();
        fields.add("id");
        searchRequestBuilder.setResultFields(fields);

        // set the entity types - just DomainGroups
        Class< ? >[] entityTypes = { DomainGroup.class };
        searchRequestBuilder.setResultTypes(entityTypes);
    }

    /**
     * Check if the input user is a follower or coordinator for a DomainGroup.
     *
     * @param personId
     *            the personId to check
     * @param domainGroupId
     *            the domainGroup to check
     * @return whether the input user is a follower or coordinator of a DomainGroup
     */
    public boolean isGroupCoordinatorOrFollower(final long personId, final long domainGroupId)
    {
        if (personId <= 0)
        {
            return false;
        }

        // search for groups with the input
        String luceneQuery = String.format("+id:(%d) +followerAndCoordinatorIds:(%d)", domainGroupId, personId);
        FullTextQuery query = searchRequestBuilder.buildQueryFromNativeSearchString(luceneQuery);

        // if we receive one result, the user is a coordinator or follower of the group
        return query.getResultSize() == 1;
    }
}
