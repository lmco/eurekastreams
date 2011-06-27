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
package org.eurekastreams.server.domain.strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the bookmark to stream filter transformer.
 */
public class BookmarkStreamFilterTransformerTest
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
    private BookmarkStreamFilterTransformer sut = null;

    /**
     * Person mapper.
     */
    private DomainMapper<List<String>, List<PersonModelView>> getPersonModelViewsByAccountIdsMapper = context
            .mock(DomainMapper.class);

    /**
     * Groups mapper.
     */
    private GetDomainGroupsByShortNames groupMapper = context.mock(GetDomainGroupsByShortNames.class);;

    /**
     * Person stream scope.
     */
    private StreamScope personScope = new StreamScope();

    /**
     * Group stream scope.
     */
    private StreamScope groupScope = new StreamScope();

    /**
     * Unknown type stream scope.
     */
    private StreamScope otherScope = new StreamScope();

    /**
     * Person object.
     */
    private PersonModelView person = new PersonModelView();

    /**
     * Group object.
     */
    private DomainGroupModelView group = new DomainGroupModelView();

    /**
     * Setup.
     */
    @Before
    public final void setup()
    {
        sut = new BookmarkStreamFilterTransformer(getPersonModelViewsByAccountIdsMapper, groupMapper);
        personScope.setScopeType(ScopeType.PERSON);
        groupScope.setScopeType(ScopeType.GROUP);
        otherScope.setScopeType(ScopeType.ALL);
    }

    /**
     * Test with empty lists.
     */
    @Test
    public final void testEmpty()
    {
        List<StreamScope> bookmarks = new ArrayList<StreamScope>();

        Assert.assertEquals(0, sut.transform(bookmarks).size());
    }
    
    /**
     * Test with groups and people.
     */
    @Test
    public final void testGroupsAndPeople()
    {
        List<StreamScope> bookmarks = new ArrayList<StreamScope>();
        bookmarks.add(personScope);
        bookmarks.add(groupScope);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonModelViewsByAccountIdsMapper).execute(with(any(List.class)));
                will(returnValue(Collections.singletonList(person)));

                oneOf(groupMapper).execute(with(any(List.class)));
                will(returnValue(Collections.singletonList(group)));
            }
        });

        Assert.assertEquals(2, sut.transform(bookmarks).size());

        context.assertIsSatisfied();
    }
    

    /**
     * Test with groups, people, and unknown type.
     */
    @Test(expected=RuntimeException.class)
    public final void testGroupsPeopleAndUnknown()
    {
        List<StreamScope> bookmarks = new ArrayList<StreamScope>();
        bookmarks.add(personScope);
        bookmarks.add(groupScope);
        bookmarks.add(otherScope);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonModelViewsByAccountIdsMapper).execute(with(any(List.class)));
                will(returnValue(Collections.singletonList(person)));

                oneOf(groupMapper).execute(with(any(List.class)));
                will(returnValue(Collections.singletonList(group)));
            }
        });

        sut.transform(bookmarks).size();

        context.assertIsSatisfied();
    }

}
