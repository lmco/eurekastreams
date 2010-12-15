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
package org.eurekastreams.server.action.execution.gallery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for DeleteThemeExecution.
 * 
 */
@SuppressWarnings("unchecked")
public class DeleteThemeExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Find theme by id mapper.
     */
    private final DomainMapper<FindByIdRequest, Theme> findByIdMapper = context.mock(DomainMapper.class,
            "findByIdMapper");

    /** Mapper to delete the gadget definition. */
    private final DomainMapper<Long, Void> deleteThemeMapper = context.mock(DomainMapper.class, "deleteThemeMapper");

    /** Mapper to get list of affected tab templates. */
    private final DomainMapper<Long, Collection<Long>> getPeopleIdsUsingTheme = context.mock(DomainMapper.class,
            "getPeopleIdsUsingTheme");

    /**
     * {@link Theme}.
     */
    private final Theme theme = context.mock(Theme.class);

    /**
     * Theme uuid.
     */
    private final String themeUuid = "themeUuid";

    /**
     * Theme id.
     */
    private Long themeId = 9L;

    /** Name of action to initiate. */
    private final String deleteCacheKeysActionName = "deleteCacheKeysActionName";

    /**
     * Action name.
     */
    private DeleteThemeExecution sut = new DeleteThemeExecution(findByIdMapper, deleteThemeMapper,
            getPeopleIdsUsingTheme, deleteCacheKeysActionName);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        ArrayList<UserActionRequest> queuedActions = new ArrayList<UserActionRequest>();

        final List<Long> personIdsUsingTheme = new ArrayList<Long>(Arrays.asList(4L));

        context.checking(new Expectations()
        {
            {
                oneOf(findByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(theme));

                allowing(theme).getUUID();
                will(returnValue(themeUuid));

                oneOf(getPeopleIdsUsingTheme).execute(themeId);
                will(returnValue(personIdsUsingTheme));

                oneOf(deleteThemeMapper).execute(themeId);
            }
        });

        AsyncActionContext innerContext = new AsyncActionContext(themeId);
        TaskHandlerActionContext<ActionContext> actionContext = new TaskHandlerActionContext<ActionContext>(
                innerContext, queuedActions);

        sut.execute(actionContext);
        context.assertIsSatisfied();

        assertEquals(1, queuedActions.size());
        assertTrue(deleteCacheKeysActionName.equals(queuedActions.get(0).getActionKey()));
    }
}
