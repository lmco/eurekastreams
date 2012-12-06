/*
 * Copyright (c) 2012-2012 Lockheed Martin Corporation
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
import java.util.List;

import org.eurekastreams.commons.test.EasyMatcher;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Test IsCommentAuthorLockedFilter.
 */
public class IsCommentAuthorLockedFilterTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Test.
     */
    @Test
    public void test1()
    {
        final ActivityDTO a1 = context.mock(ActivityDTO.class, "a1");
        final ActivityDTO a2 = context.mock(ActivityDTO.class, "a2");
        final ActivityDTO a3 = context.mock(ActivityDTO.class, "a3");
        final ActivityDTO a4 = context.mock(ActivityDTO.class, "a4");

        final CommentDTO c1 = context.mock(CommentDTO.class, "c1");
        final CommentDTO c2 = context.mock(CommentDTO.class, "c2");
        final CommentDTO c3 = context.mock(CommentDTO.class, "c3");
        final CommentDTO c4 = context.mock(CommentDTO.class, "c4");

        final PersonModelView p1 = context.mock(PersonModelView.class, "p1");
        final PersonModelView p3 = context.mock(PersonModelView.class, "p3");
        final PersonModelView p5 = context.mock(PersonModelView.class, "p5");

        final List<CommentDTO> a4c = Collections.singletonList(c4);

        final DomainMapper personMapper = context.mock(DomainMapper.class, "personMapper");

        context.checking(new Expectations()
        {
            {
                allowing(a1).getComments();
                will(returnValue(null));
                allowing(a1).getFirstComment();
                will(returnValue(null));
                allowing(a1).getLastComment();
                will(returnValue(null));

                allowing(a2).getComments();
                will(returnValue(null));
                allowing(a2).getFirstComment();
                will(returnValue(c1));
                allowing(a2).getLastComment();
                will(returnValue(null));

                allowing(a3).getComments();
                will(returnValue(null));
                allowing(a3).getFirstComment();
                will(returnValue(c2));
                allowing(a3).getLastComment();
                will(returnValue(c3));

                allowing(a4).getComments();
                will(returnValue(a4c));

                allowing(c1).getAuthorId();
                will(returnValue(1L));
                allowing(c2).getAuthorId();
                will(returnValue(3L));
                allowing(c3).getAuthorId();
                will(returnValue(1L));
                allowing(c4).getAuthorId();
                will(returnValue(5L));

                oneOf(personMapper).execute(with(new EasyMatcher<List<Long>>()
                {
                    @Override
                    protected boolean isMatch(final List<Long> inTestObject)
                    {
                        return inTestObject.size() == 3 && inTestObject.contains(1L) && inTestObject.contains(3L)
                                && inTestObject.contains(5L);
                    }
                }));
                will(returnValue(Arrays.asList(p1, p3, p5)));

                allowing(p1).getId();
                will(returnValue(1L));
                allowing(p3).getId();
                will(returnValue(3L));
                allowing(p5).getId();
                will(returnValue(5L));

                allowing(p1).isAccountLocked();
                will(returnValue(true));
                allowing(p3).isAccountLocked();
                will(returnValue(false));
                allowing(p5).isAccountLocked();
                will(returnValue(false));

                oneOf(c1).setAuthorActive(false);
                oneOf(c2).setAuthorActive(true);
                oneOf(c3).setAuthorActive(false);
                oneOf(c4).setAuthorActive(true);
            }
        });

        IsCommentAuthorLockedFilter sut = new IsCommentAuthorLockedFilter(personMapper);
        sut.filter(Arrays.asList(a1, a2, a3, a4), null);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void test2()
    {
        final ActivityDTO a1 = context.mock(ActivityDTO.class, "a1");
        final ActivityDTO a2 = context.mock(ActivityDTO.class, "a2");

        final DomainMapper personMapper = context.mock(DomainMapper.class, "personMapper");

        context.checking(new Expectations()
        {
            {
                allowing(a1).getComments();
                will(returnValue(null));
                allowing(a1).getFirstComment();
                will(returnValue(null));
                allowing(a1).getLastComment();
                will(returnValue(null));

                allowing(a2).getComments();
                will(returnValue(Collections.emptyList()));
                allowing(a2).getFirstComment();
                will(returnValue(null));
                allowing(a1).getLastComment();
                will(returnValue(null));
            }
        });

        IsCommentAuthorLockedFilter sut = new IsCommentAuthorLockedFilter(personMapper);
        sut.filter(Arrays.asList(a1, a2), null);

        context.assertIsSatisfied();
    }
}
