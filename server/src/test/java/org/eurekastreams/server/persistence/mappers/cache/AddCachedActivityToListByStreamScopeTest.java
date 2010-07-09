/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.requests.AddCachedActivityToListByStreamScopeRequest;
import org.eurekastreams.server.persistence.mappers.requests.AddCachedActivityToListRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Class to test the AddCachedActivityToListByStreamScope class.
 *
 */
public class AddCachedActivityToListByStreamScopeTest
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
     * Mock instance of the person scoped AddCachedActivityToList mapper.
     */
    private AddCachedActivityToList addCachedPersonActivityToListMock =
            context.mock(AddCachedActivityToList.class, "addCachedPersonActivityMapper");

    /**
     * Mock instance of the group scoped AddCachedActivityToList mapper.
     */
    private AddCachedActivityToList addCachedGroupActivityToListMock =
            context.mock(AddCachedActivityToList.class, "addCachedGroupActivityMapper");

    /**
     * Mock instance of the GetPeopleByAccountIds mapper.
     */
    private GetPeopleByAccountIds peopleMapperMock = context.mock(GetPeopleByAccountIds.class);

    /**
     * Mock instance of the GetDomainGroupsByShortNames mapper.
     */
    private GetDomainGroupsByShortNames groupMapperMock = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * Mock instance of a PersonModelView object.
     */
    private PersonModelView testPerson = context.mock(PersonModelView.class);

    /**
     * Mock instance of a DomainGroupModelView object.
     */
    private DomainGroupModelView testGroup = context.mock(DomainGroupModelView.class);

    /**
     * System under test.
     */
    private AddCachedActivityToListByStreamScope sut;

    /**
     * Method to prep the test suite.
     */
    @Before
    public void setup()
    {
        sut =
                new AddCachedActivityToListByStreamScope(addCachedPersonActivityToListMock,
                        addCachedGroupActivityToListMock, peopleMapperMock, groupMapperMock);
    }

    /**
     * Test a successful execution when adding activities for a person to a list.
     */
    @Test
    public void testPersonExecute()
    {
        StreamScope testStreamScope = new StreamScope(ScopeType.PERSON, "testkey");
        AddCachedActivityToListByStreamScopeRequest request =
                new AddCachedActivityToListByStreamScopeRequest(1L, 2L, testStreamScope);
        context.checking(new Expectations()
        {
            {
                oneOf(peopleMapperMock).fetchId(with(any(String.class)));

                oneOf(addCachedPersonActivityToListMock).execute(with(any(AddCachedActivityToListRequest.class)));
            }
        });

        sut.execute(request);

        context.assertIsSatisfied();
    }

    /**
     * Test a successful execution when adding activities for a group to a list.
     */
    @Test
    public void testGroupExecute()
    {
        StreamScope testStreamScope = new StreamScope(ScopeType.GROUP, "testkey");
        AddCachedActivityToListByStreamScopeRequest request =
                new AddCachedActivityToListByStreamScopeRequest(1L, 2L, testStreamScope);
        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).fetchId(with(any(String.class)));

                oneOf(addCachedGroupActivityToListMock).execute(with(any(AddCachedActivityToListRequest.class)));
            }
        });

        sut.execute(request);

        context.assertIsSatisfied();
    }

    /**
     * Test a failing execution when using and unsupported ScopeType.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testExecuteFail()
    {
        StreamScope testStreamScope = new StreamScope(ScopeType.ORGANIZATION, "testkey");
        AddCachedActivityToListByStreamScopeRequest request =
                new AddCachedActivityToListByStreamScopeRequest(1L, 2L, testStreamScope);

        sut.execute(request);
    }
}
