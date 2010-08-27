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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.cache.UpdateDestinationStreamNameInCachedActivity;
import org.eurekastreams.server.persistence.mappers.db.GetActivityIdsPostedToStreamByUniqueKeyAndScopeType;
import org.eurekastreams.server.persistence.mappers.db.GetFieldFromTableByUniqueField;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for ActivityRecipientStreamNameCacheUpdateAsyncExecution.
 */
@SuppressWarnings("unchecked")
public class ActivityRecipientStreamNameCacheUpdateAsyncExecutionTest
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
    private ActivityRecipientStreamNameCacheUpdateAsyncExecution sut;

    /**
     * Mapper to get a Nameable by string id.
     */
    private GetFieldFromTableByUniqueField<String, String> getDisplayNameMapper = context
            .mock(GetFieldFromTableByUniqueField.class);

    /**
     * Mapper to get the activity ids posted to a group.
     */
    private GetActivityIdsPostedToStreamByUniqueKeyAndScopeType activityIdMapper = context
            .mock(GetActivityIdsPostedToStreamByUniqueKeyAndScopeType.class);

    /**
     * Cache updater.
     */
    private UpdateDestinationStreamNameInCachedActivity cacheUpdater = context
            .mock(UpdateDestinationStreamNameInCachedActivity.class);

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new ActivityRecipientStreamNameCacheUpdateAsyncExecution(ScopeType.GROUP, getDisplayNameMapper,
        // line break
                activityIdMapper, cacheUpdater);
    }

    /**
     * Test the execute method.
     */
    @Test
    public void testExecute()
    {
        final String groupShortName = "ldsjkrfsd";
        final String existingGroupName = "My Favorite Group";

        final Long activityId = 12L;
        final List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(activityId);

        ActionContext actionContext = new ActionContext()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Serializable getParams()
            {
                return groupShortName;
            }

            @Override
            public Map<String, Object> getState()
            {
                return null;
            }

            @Override
            public String getActionId()
            {
                return null;
            }

            @Override
            public void setActionId(final String inActionId)
            {

            }
        };

        context.checking(new Expectations()
        {
            {
                oneOf(getDisplayNameMapper).execute(groupShortName);
                will(returnValue(existingGroupName));

                oneOf(activityIdMapper).execute(ScopeType.GROUP, groupShortName);
                will(returnValue(activityIds));

                oneOf(cacheUpdater).execute(activityIds, existingGroupName);
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }
}
