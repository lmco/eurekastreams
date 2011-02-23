/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.cache.PopulateOrgChildWithSkeletonParentOrgsCacheMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for GetDomainGroupModelViewByShortNameExectution.
 * 
 */
public class GetDomainGroupModelViewByShortNameExectutionTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mapper used to look up the group.
     */
    private GetDomainGroupsByShortNames groupByShortNameMapper = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * Mapper to populate the parent org of people with skeleton orgs from cache.
     */
    private PopulateOrgChildWithSkeletonParentOrgsCacheMapper populateOrgChildWithSkeletonParentOrgsCacheMapper //
    = context.mock(PopulateOrgChildWithSkeletonParentOrgsCacheMapper.class);

    /**
     * Mapper to get all person ids that have group coordinator access for a given group.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordinatorIdsDAO = context
            .mock(GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class);

    /**
     * Strategy to retrieve the banner id if it is not directly configured.
     */
    @SuppressWarnings("unchecked")
    private GetBannerIdByParentOrganizationStrategy getBannerIdStrategy = context
            .mock(GetBannerIdByParentOrganizationStrategy.class);

    /**
     * Mapper to get followers for a group.
     */
    private DomainMapper<Long, List<Long>> groupFollowerIdsMapper = context.mock(DomainMapper.class);

    /**
     * DomainGroupModelView.
     */
    private DomainGroupModelView dgmv = context.mock(DomainGroupModelView.class);

    /**
     * GroupId.
     */
    private final String groupId = "shortName";

    /**
     * System under test.
     */
    private GetDomainGroupModelViewByShortNameExectution sut = new GetDomainGroupModelViewByShortNameExectution(
            groupByShortNameMapper, populateOrgChildWithSkeletonParentOrgsCacheMapper, groupCoordinatorIdsDAO,
            getBannerIdStrategy, groupFollowerIdsMapper);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(groupByShortNameMapper).fetchUniqueResult(groupId);
                will(returnValue(dgmv));
            }
        });

    }
}
