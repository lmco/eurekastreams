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
package org.eurekastreams.server.service.actions.strategies.directory;

import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Factory to return an organization child getter strategy based on child type.
 */
public final class OrgChildrenGetterFactory
{
    /**
     * Build an OrgChildrenGetter strategy for the type of children described with the input OrgChildType.
     * 
     * @param childType
     *            value
     * @param inQueryBuilder
     *            the strategy to build a Lucene query string for searching the directory
     * @param inSearchRequestBuilder
     *            the search request builder to build our query
     * @param inSortFieldBuilder
     *            the query sort builder
     * @param inIsRecursive
     *            whether we're searching for entities recursively from the org with the input shortName
     * @return an OrgChildrenGetter strategy
     */
    public OrgChildrenGetter< ? extends ModelView> buildOrgChildrenGetter(final EntityType childType,
            final DirectorySearchLuceneQueryBuilder inQueryBuilder,
            final ProjectionSearchRequestBuilder inSearchRequestBuilder, final SortFieldBuilder inSortFieldBuilder,
            final boolean inIsRecursive)
    {
        switch (childType)
        {
        case GROUP:
            return new OrgChildrenGetter<DomainGroupModelView>(inQueryBuilder, inSearchRequestBuilder,
                    inSortFieldBuilder, inIsRecursive);

        case PERSON:
            return new OrgChildrenGetter<PersonModelView>(inQueryBuilder, inSearchRequestBuilder, inSortFieldBuilder,
                    inIsRecursive);

        case ORGANIZATION:
            return new OrgChildrenGetter<OrganizationModelView>(inQueryBuilder, inSearchRequestBuilder,
                    inSortFieldBuilder, inIsRecursive);

        default:
            throw new RuntimeException("Unhandled OrgChildType: " + childType.toString());
        }
    }
}
