/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.test.EasyMatcher;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests IsAuthorLockedFilter.
 */
public class IsAuthorLockedFilterTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: person mapper. */
    private final DomainMapper personMapper = context.mock(DomainMapper.class, "personMapper");

    /** SUT. */
    private IsAuthorLockedFilter sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new IsAuthorLockedFilter(personMapper);
    }

    /**
     * Tests filtering.
     */
    @Test
    public void testFilterEmpty()
    {
        sut.filter(Collections.EMPTY_LIST, null);
        context.assertIsSatisfied();
    }

    /**
     * Tests filtering.
     */
    @Test
    public void testFilter()
    {
        final StreamEntityDTO e1 = context.mock(StreamEntityDTO.class, "e1");
        final StreamEntityDTO e2 = context.mock(StreamEntityDTO.class, "e2");
        final StreamEntityDTO e3 = context.mock(StreamEntityDTO.class, "e3");
        final StreamEntityDTO e1o = context.mock(StreamEntityDTO.class, "e1o");
        final StreamEntityDTO e3o = context.mock(StreamEntityDTO.class, "e3o");
        final ActivityDTO a1 = context.mock(ActivityDTO.class, "a1");
        final ActivityDTO a2 = context.mock(ActivityDTO.class, "a2");
        final ActivityDTO a3 = context.mock(ActivityDTO.class, "a3");
        final PersonModelView p7 = context.mock(PersonModelView.class, "p7");
        final PersonModelView p8 = context.mock(PersonModelView.class, "p8");
        final PersonModelView p9 = context.mock(PersonModelView.class, "p9");

        context.checking(new Expectations()
        {
            {
                allowing(a1).getActor();
                will(returnValue(e1));
                allowing(a1).getOriginalActor();
                will(returnValue(e1o));
                allowing(e1).getId();
                will(returnValue(7L));
                allowing(e1).getType();
                will(returnValue(EntityType.PERSON));
                allowing(e1o).getType();
                will(returnValue(EntityType.GROUP));

                allowing(a2).getActor();
                will(returnValue(e2));
                allowing(a2).getOriginalActor();
                will(returnValue(null));
                allowing(e2).getType();
                will(returnValue(EntityType.GROUP));

                allowing(a3).getActor();
                will(returnValue(e3));
                allowing(a3).getOriginalActor();
                will(returnValue(e3o));
                allowing(e3).getId();
                will(returnValue(8L));
                allowing(e3).getType();
                will(returnValue(EntityType.PERSON));
                allowing(e3o).getId();
                will(returnValue(9L));
                allowing(e3o).getType();
                will(returnValue(EntityType.PERSON));

                allowing(p7).getId();
                will(returnValue(7L));
                allowing(p7).isAccountLocked();
                will(returnValue(true));
                allowing(p8).getId();
                will(returnValue(8L));
                allowing(p8).isAccountLocked();
                will(returnValue(false));
                allowing(p9).getId();
                will(returnValue(9L));
                allowing(p9).isAccountLocked();
                will(returnValue(false));

                oneOf(personMapper).execute(with(new EasyMatcher<List<Long>>()
                {
                    @Override
                    protected boolean isMatch(final List<Long> inTestObject)
                    {
                        return inTestObject.size() == 3 && inTestObject.contains(7L) && inTestObject.contains(8L)
                                && inTestObject.contains(9L);
                    }
                }));
                will(returnValue(Arrays.asList(p7, p8, p9)));

                oneOf(e1).setActive(false);
                oneOf(e3).setActive(true);
                oneOf(e3o).setActive(true);
            }
        });

        sut.filter(Arrays.asList(a1, a2, a3), null);
        context.assertIsSatisfied();
    }
}
