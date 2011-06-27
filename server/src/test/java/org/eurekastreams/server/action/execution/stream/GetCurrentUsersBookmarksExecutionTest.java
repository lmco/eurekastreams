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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests getting a users bookmarks.
 */
public class GetCurrentUsersBookmarksExecutionTest
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
    private GetCurrentUsersBookmarksExecution sut = null;

    /**
     * ActionContext mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Bookmarks mapper.
     */
    private DomainMapper<Long, List<StreamScope>> bookmarksMapper = context.mock(DomainMapper.class);

    /**
     * Bookmarks transformer.
     */
    private Transformer<List<StreamScope>, List<StreamFilter>> bookmarksTransformer = context.mock(Transformer.class);

    /**
     * Setup Fixtures.
     */
    @Before
    public final void setup()
    {
        sut = new GetCurrentUsersBookmarksExecution(bookmarksMapper, bookmarksTransformer);
    }

    /**
     * Test executing the action.
     */
    @Test
    public final void executeTest()
    {
        final Long personId = 2L;

        final List<StreamScope> bookmarksList = new ArrayList<StreamScope>();

        final List<StreamFilter> filterList = new ArrayList<StreamFilter>();

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(personId));

                oneOf(bookmarksMapper).execute(personId);
                will(returnValue(bookmarksList));

                oneOf(bookmarksTransformer).transform(bookmarksList);
                will(returnValue(filterList));
            }
        });

        Assert.assertEquals(filterList, sut.execute(actionContext));

        context.assertIsSatisfied();
    }
}
