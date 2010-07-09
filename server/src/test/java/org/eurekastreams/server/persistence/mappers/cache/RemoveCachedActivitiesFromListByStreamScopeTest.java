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
import org.eurekastreams.server.persistence.mappers.requests.RemoveCachedActivitiesFromListByStreamScopeRequest;
import org.eurekastreams.server.persistence.mappers.requests.RemoveCachedActivitiesFromListRequest;
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
 * Test class for exercising the functionality in RemoveCachedActivitiesFromListByStreamScope.
 *
 */
public class RemoveCachedActivitiesFromListByStreamScopeTest
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
     * Mock instance of the RemoveCachedActivitiesFromList mapper scoped to a person.
     */
    private RemoveCachedActivitiesFromList removeCachedPersonActivitiesMock =
            context.mock(RemoveCachedActivitiesFromList.class, "removePersonActivitiesMapperMock");

    /**
     * Mock instance of the RemoveCachedActivitiesFromList mapper scoped to a group.
     */
    private RemoveCachedActivitiesFromList removeCachedGroupActivitiesMock =
            context.mock(RemoveCachedActivitiesFromList.class, "removeGroupActivitiesMapperMock");

    /**
     * Mock instance fo the GetPeopleByAccountIds mapper.
     */
    private GetPeopleByAccountIds peopleMapperMock = context.mock(GetPeopleByAccountIds.class);

    /**
     * Mock instance of the GetDomainGroupsByShortNames mapper.
     */
    private GetDomainGroupsByShortNames groupMapperMock = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * Mock instance of the PersonModelView object.
     */
    private PersonModelView testPerson = context.mock(PersonModelView.class);

    /**
     * Mock instance of the DomainGroupModelView object.
     */
    private DomainGroupModelView testGroup = context.mock(DomainGroupModelView.class);

    /**
     * System under test.
     */
    private RemoveCachedActivitiesFromListByStreamScope sut;

    /**
     * Prep method for test suite.
     */
    @Before
    public void setup()
    {
        sut =
                new RemoveCachedActivitiesFromListByStreamScope(removeCachedPersonActivitiesMock,
                        removeCachedGroupActivitiesMock, peopleMapperMock, groupMapperMock);
    }

    /**
     * Test the successful execution of the mapper under the person context.
     */
    @Test
    public void testPersonExecute()
    {
        StreamScope testStreamScope = new StreamScope(ScopeType.PERSON, "testKey");
        RemoveCachedActivitiesFromListByStreamScopeRequest request =
                new RemoveCachedActivitiesFromListByStreamScopeRequest(1L, 2L, testStreamScope);

        context.checking(new Expectations()
        {
            {
                oneOf(peopleMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testPerson));

                oneOf(testPerson).getEntityId();

                oneOf(removeCachedPersonActivitiesMock)
                        .execute(with(any(RemoveCachedActivitiesFromListRequest.class)));
            }
        });

        sut.execute(request);

        context.assertIsSatisfied();
    }

    /**
     * Test the successful execution of the mapper under the person context.
     */
    @Test
    public void testGroupExecute()
    {
        StreamScope testStreamScope = new StreamScope(ScopeType.GROUP, "testKey");
        RemoveCachedActivitiesFromListByStreamScopeRequest request =
                new RemoveCachedActivitiesFromListByStreamScopeRequest(1L, 2L, testStreamScope);

        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));

                oneOf(testGroup).getEntityId();

                oneOf(removeCachedGroupActivitiesMock).execute(with(any(RemoveCachedActivitiesFromListRequest.class)));
            }
        });

        sut.execute(request);

        context.assertIsSatisfied();
    }

    /**
     * Test the failing execution of the mapper with an unsupported scopetype.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testExecuteFail()
    {
        StreamScope testStreamScope = new StreamScope(ScopeType.ORGANIZATION, "testKey");
        RemoveCachedActivitiesFromListByStreamScopeRequest request =
                new RemoveCachedActivitiesFromListByStreamScopeRequest(1L, 2L, testStreamScope);

        sut.execute(request);
    }
}
