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
package org.eurekastreams.server.action.execution.profile;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverserBuilder;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.MoveOrganizationPeopleRequest;
import org.eurekastreams.server.persistence.mappers.requests.MoveOrganizationPeopleResponse;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for DeleteOrganizationExecution.
 *
 */
@SuppressWarnings("unchecked")
public class DeleteOrganizationExecutionTest
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
     * Mapper to move People out of organization.
     */
    private DomainMapper<MoveOrganizationPeopleRequest, MoveOrganizationPeopleResponse> movePeopleMapper = context
            .mock(DomainMapper.class, "movePeopleMapper");

    /**
     * Mapper for getting organization DTOs.
     */

    private DomainMapper<List<Long>, List<OrganizationModelView>> orgDTOByIdMapper = context.mock(DomainMapper.class);

    /**
     * {@link FindByIdMapper}.
     */
    private FindByIdMapper<Organization> orgByIdMapper = context.mock(FindByIdMapper.class, "orgByIdMapper");

    /**
     * Mapper to delete org and related objects.
     */
    private DomainMapper<Long, Boolean> deleteOrgMapper = context.mock(DomainMapper.class, "deleteOrgMapper");

    /**
     * Mapper to get person ids for those that have given org as related org.
     */
    private DomainMapper<Long, Set<Long>> relatedOrgPersonIdsMapper = context.mock(DomainMapper.class,
            "relatedOrgPersonIdMapper");

    /**
     * {@link OrganizationMapper}. This is used for updating org stats only.
     */
    @SuppressWarnings("deprecation")
    private OrganizationMapper organizationMapper = context.mock(OrganizationMapper.class);

    /**
     * The organization hierarchy traverser builder - needed because this class is reused by all threads, we can't share
     * OrganizationHierarchyTraversers.
     */
    private OrganizationHierarchyTraverserBuilder orgTraverserBuilder = context
            .mock(OrganizationHierarchyTraverserBuilder.class);

    /**
     * {@link OrganizationHierarchyTraverser}.
     */
    private OrganizationHierarchyTraverser orgTraverser = context.mock(OrganizationHierarchyTraverser.class);

    /**
     * {@link TaskHandlerActionContext}.
     */
    private TaskHandlerActionContext taskHandlerConext = context.mock(TaskHandlerActionContext.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link OrganizationModelView}.
     */
    private OrganizationModelView orgDto = context.mock(OrganizationModelView.class);

    /**
     * Parent {@link Organization} of org being deleted.
     */
    private Organization parentOrg = context.mock(Organization.class);

    /**
     * Organization id.
     */
    private final Long orgId = 1L;

    /**
     * Parent org id.
     */
    private final Long orgParentId = 2L;

    /**
     * Org compositeStream id.
     */
    private Long compositeStreamId = 3L;

    /**
     * Moved activity id.
     */
    private Long activityId = 4L;

    /**
     * System under test.
     */
    private DeleteOrganizationExecution sut = new DeleteOrganizationExecution(movePeopleMapper, orgDTOByIdMapper,
            orgByIdMapper, deleteOrgMapper, relatedOrgPersonIdsMapper, organizationMapper, orgTraverserBuilder);

    /**
     * Test.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void test()
    {
        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        final Set<Long> movedPeopleIds = new HashSet<Long>(Arrays.asList(9L));
        final Set<Long> movedActivityIds = new HashSet<Long>(Arrays.asList(9L));

        final MoveOrganizationPeopleResponse response = new MoveOrganizationPeopleResponse(movedPeopleIds,
                movedActivityIds);

        final List<OrganizationModelView> orgs = new ArrayList<OrganizationModelView>();
        orgs.add(orgDto);

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerConext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(orgId));

                allowing(orgDTOByIdMapper).execute(Collections.singletonList(orgId));
                will(returnValue(orgs));

                allowing(orgDto).getParentOrganizationId();
                will(returnValue(orgParentId));

                allowing(orgByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(parentOrg));

                allowing(parentOrg).getShortName();
                will(returnValue("parentOrgShortName"));

                allowing(parentOrg).getId();
                will(returnValue(orgParentId));

                allowing(orgDto).getShortName();
                will(returnValue("orgShortName"));

                allowing(movePeopleMapper).execute(with(any(MoveOrganizationPeopleRequest.class)));
                will(returnValue(response));

                allowing(relatedOrgPersonIdsMapper).execute(orgId);
                will(returnValue(movedPeopleIds));

                allowing(deleteOrgMapper).execute(orgId);

                allowing(orgTraverserBuilder).getOrganizationHierarchyTraverser();
                will(returnValue(orgTraverser));

                allowing(orgTraverser).traverseHierarchy(parentOrg);

                allowing(organizationMapper).updateOrganizationStatistics(orgTraverser);

                allowing(orgTraverser).getOrganizations();
                will(returnValue(new HashSet<Organization>(Arrays.asList(parentOrg))));

                allowing(taskHandlerConext).getUserActionRequests();
                will(returnValue(requests));
            }
        });

        // run sut.
        sut.execute(taskHandlerConext);
        context.assertIsSatisfied();

        // verify queued tasks should be 4
        // * reindex single moved person
        // * reindex parent org.
        // * reindex moved activity
        // * remove deleted org from search index.
        // * delete cache keys.
        assertEquals(5, requests.size());
    }
}
