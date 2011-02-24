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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.cache.PopulateOrgChildWithSkeletonParentOrgsCacheMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for GetDomainGroupModelViewByShortNameExectution.
 * 
 */
@SuppressWarnings("unchecked")
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
     * Get ids for direct group coordinators.
     */
    private DomainMapper<Long, List<Long>> groupCoordinatorIdsByGroupIdMapper = context.mock(DomainMapper.class,
            "groupCoordinatorIdsByGroupIdMapper");

    /**
     * Get PersonModelViews by id.
     */
    private DomainMapper<List<Long>, List<PersonModelView>> personModelViewsByIdMapper = context.mock(
            DomainMapper.class, "personModelViewsByIdMapper");

    /**
     * Group short name.
     */
    private final String shortname = "shortName";

    /**
     * Group Id.
     */
    private final Long groupId = 5L;

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext pac = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * System under test.
     */
    private GetDomainGroupModelViewByShortNameExectution sut = new GetDomainGroupModelViewByShortNameExectution(
            groupByShortNameMapper, populateOrgChildWithSkeletonParentOrgsCacheMapper, groupCoordinatorIdsDAO,
            getBannerIdStrategy, groupFollowerIdsMapper, groupCoordinatorIdsByGroupIdMapper, //
            personModelViewsByIdMapper);

    // TODO: Minimal pass testing. Beef this up a bit for non public and restricted groups.
    /**
     * Test.
     */
    @Test
    public void test()
    {
        final ArrayList<Long> coordIds = new ArrayList<Long>(Arrays.asList(1L));
        final ArrayList<PersonModelView> coords = new ArrayList<PersonModelView>(Arrays.asList(new PersonModelView()));

        context.checking(new Expectations()
        {
            {
                allowing(pac).getParams();
                will(returnValue(shortname));

                allowing(pac).getPrincipal();
                will(returnValue(principal));

                oneOf(groupByShortNameMapper).fetchUniqueResult(shortname);
                will(returnValue(dgmv));

                allowing(dgmv).getId();
                will(returnValue(groupId));

                oneOf(dgmv).setBannerEntityId(groupId);

                oneOf(dgmv).getBannerId();
                will(returnValue("bannerId"));

                oneOf(dgmv).isPublic();
                will(returnValue(true));

                oneOf(dgmv).setRestricted(false);

                oneOf(groupCoordinatorIdsByGroupIdMapper).execute(groupId);
                will(returnValue(coordIds));

                oneOf(personModelViewsByIdMapper).execute(coordIds);
                will(returnValue(coords));

                oneOf(dgmv).setCoordinators(coords);
            }
        });

        sut.execute(pac);
        context.assertIsSatisfied();
    }
}
