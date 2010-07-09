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

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Test fixture for OrgChildrenGetterFactory.
 */
public class OrgChildrenGetterFactoryTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private OrgChildrenGetterFactory sut = new OrgChildrenGetterFactory();

    /**
     * The ProjectionSearchRequestBuilder to use.
     */
    private final ProjectionSearchRequestBuilder searchRequestBuilder = context
            .mock(ProjectionSearchRequestBuilder.class);

    /**
     * The sort field builder to use.
     */
    private final SortFieldBuilder sortFieldBuilder = context.mock(SortFieldBuilder.class);

    /**
     * the DirectorySearchLuceneQueryBuilder.
     */
    private final DirectorySearchLuceneQueryBuilder queryBuilder = context
            .mock(DirectorySearchLuceneQueryBuilder.class);

    /**
     * Test buildOrgChildrenGetter().
     * 
     * Note that because of the way Java implements generics, this test would succeed even if the underlying code
     * returned the wrong type.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testBuildOrgChildrenGetter()
    {
        @SuppressWarnings("unused")
        OrgChildrenGetter<DomainGroupModelView> domainGroupGetter = (OrgChildrenGetter<DomainGroupModelView>) sut
                .buildOrgChildrenGetter(EntityType.GROUP, queryBuilder, searchRequestBuilder, sortFieldBuilder, true);

        @SuppressWarnings("unused")
        OrgChildrenGetter<PersonModelView> personGetter = (OrgChildrenGetter<PersonModelView>) sut
                .buildOrgChildrenGetter(EntityType.PERSON, queryBuilder, searchRequestBuilder, sortFieldBuilder, true);

        @SuppressWarnings("unused")
        OrgChildrenGetter<OrganizationModelView> organizationGetter = (OrgChildrenGetter<OrganizationModelView>) sut
                .buildOrgChildrenGetter(EntityType.ORGANIZATION, queryBuilder, searchRequestBuilder, sortFieldBuilder,
                        false);
    }
}
