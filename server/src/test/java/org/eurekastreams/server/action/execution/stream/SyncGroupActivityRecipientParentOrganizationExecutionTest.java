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
package org.eurekastreams.server.action.execution.stream;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.stream.SyncGroupActivityRecipientParentOrganizationRequest;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.GetActivityIdsPostedToStreamByUniqueKeyAndScopeType;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for SyncGroupActivityRecipientParentOrganizationExecution.
 *
 */
@SuppressWarnings("unchecked")
public class SyncGroupActivityRecipientParentOrganizationExecutionTest
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
     * Mapper to update recipient parent org id for all activites for a group.
     */
    private DomainMapper<String, Integer> syncActivityRecipientParentOrg = context.mock(DomainMapper.class,
            "syncActivityRecipientParentOrg");

    /**
     * Mapper to get Orgs by short name.
     */
    private GetOrganizationsByShortNames getOrgByShortNameMapper = context.mock(GetOrganizationsByShortNames.class);

    /**
     * Mapper to get Orgs by id.
     */
    private DomainMapper<List<Long>, List<OrganizationModelView>> getOrgByIdMapper = context.mock(DomainMapper.class);

    /**
     * mapper to get all parent org ids for an org id.
     */
    private DomainMapper<Long, List<Long>> getRecursiveOrgParentMapper = context.mock(DomainMapper.class,
            "getRecursiveOrgParentMapper");

    /**
     * Mapper to get activity ids posted to a group.
     */
    private GetActivityIdsPostedToStreamByUniqueKeyAndScopeType activityIdMapper = context
            .mock(GetActivityIdsPostedToStreamByUniqueKeyAndScopeType.class);

    /**
     * Request.
     */
    private SyncGroupActivityRecipientParentOrganizationRequest request = context
            .mock(SyncGroupActivityRecipientParentOrganizationRequest.class);

    /**
     * Group key.
     */
    private String groupUniqueKey = "foo";

    /**
     * System under test.
     */
    private SyncGroupActivityRecipientParentOrganizationExecution sut = // \n
    new SyncGroupActivityRecipientParentOrganizationExecution(syncActivityRecipientParentOrg, activityIdMapper,
            getOrgByShortNameMapper, getOrgByIdMapper, getRecursiveOrgParentMapper);

    /**
     * TaskHandlerActionContext.
     */
    private TaskHandlerActionContext taskHandlerActionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * ActionContext.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * OrganizationModelView.
     */
    private OrganizationModelView oldParent = context.mock(OrganizationModelView.class, "oldParent");

    /**
     * OrganizationModelView.
     */
    private OrganizationModelView newParent = context.mock(OrganizationModelView.class, "newParent");

    /**
     * OrganizationModelView.
     */
    private OrganizationModelView rootOrg = context.mock(OrganizationModelView.class, "rootOrg");

    /**
     * List of new/old parent org model views.
     */
    private List<OrganizationModelView> parentOrgList = new ArrayList<OrganizationModelView>(Arrays.asList(oldParent,
            newParent));

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final List<Long> activityIds = new ArrayList<Long>(Arrays.asList(5L));
        final String oldParentKey = "oldParentKey";
        final String newParentKey = "newParentKey";

        final List<UserActionRequest> tasks = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getGroupKey();
                will(returnValue(groupUniqueKey));

                oneOf(syncActivityRecipientParentOrg).execute(groupUniqueKey);

                allowing(activityIdMapper).execute(ScopeType.GROUP, groupUniqueKey);
                will(returnValue(activityIds));

                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(tasks));

                allowing(request).getNewOrgParentKey();
                will(returnValue(newParentKey));

                allowing(request).getOldOrgParentKey();
                will(returnValue(oldParentKey));

                allowing(getOrgByShortNameMapper).execute(Arrays.asList(newParentKey, oldParentKey));
                will(returnValue(parentOrgList));

                allowing(oldParent).getEntityId();
                will(returnValue(1L));

                allowing(newParent).getEntityId();
                will(returnValue(2L));

                allowing(getRecursiveOrgParentMapper).execute(with(any(Long.class)));
                will(returnValue(new ArrayList<Long>(Arrays.asList(9L))));

                allowing(getOrgByIdMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<OrganizationModelView>(Arrays.asList(rootOrg, oldParent, newParent))));

                allowing(rootOrg).getShortName();
                will(returnValue("rootorg"));

                allowing(oldParent).getShortName();
                will(returnValue("oldParent"));

                allowing(newParent).getShortName();
                will(returnValue("newParent"));
            }
        });

        sut.execute(taskHandlerActionContext);

        // verify correct number of tasks generated.
        assertEquals(5, tasks.size());

        context.assertIsSatisfied();
    }
}
