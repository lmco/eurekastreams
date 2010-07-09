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
package org.eurekastreams.server.action.principal;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.exceptions.PrincipalPopulationException;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByOpenSocialIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for OpenSocialPrincipalPopulator class.
 *
 */
public class OpenSocialPrincipalPopulatorTest
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
    private OpenSocialPrincipalPopulator sut;

    /**
     * Person Mapper.
     */
    private GetPeopleByOpenSocialIds personMapper = context.mock(GetPeopleByOpenSocialIds.class);

    /**
     * Person mapper by account ids.
     */
    private GetPeopleByAccountIds personAccountIdsMapper = context.mock(GetPeopleByAccountIds.class);

    /**
     * Person model view.
     */
    private PersonModelView person = context.mock(PersonModelView.class);

    /**
     * Account id.
     */
    private String accountId = "accountId";

    /**
     * Open social id.
     */
    private String openSocialId = "openSocialId";

    /**
     * Entity id.
     */
    private long entityId = 5L;

    /**
     * Set up.
     */
    @Before
    public void setup()
    {
        sut = new OpenSocialPrincipalPopulator(personMapper, personAccountIdsMapper);
    }

    /**
     * Test getPrincipal.
     */
    @Test
    public void testGetPrincipalSuccess()
    {
        context.checking(new Expectations()
        {
            {
                allowing(personMapper).fetchUniqueResult(openSocialId);
                will(returnValue(person));

                allowing(person).getAccountId();
                will(returnValue(accountId));

                allowing(person).getOpenSocialId();
                will(returnValue(openSocialId));

                allowing(person).getEntityId();
                will(returnValue(entityId));
            }
        });

        Principal result = sut.getPrincipal(openSocialId);
        assertEquals(accountId, result.getAccountId());
        assertEquals(openSocialId, result.getOpenSocialId());
        assertEquals(entityId, result.getId().longValue());

        context.assertIsSatisfied();
    }

    /**
     * Test getPrincipal.
     */
    @Test
    public void testGetPrincipalSuccessOnNtIDResult()
    {
        context.checking(new Expectations()
        {
            {
                allowing(personMapper).fetchUniqueResult(openSocialId);
                will(returnValue(null));

                oneOf(personAccountIdsMapper).fetchUniqueResult(openSocialId);
                will(returnValue(person));

                allowing(person).getAccountId();
                will(returnValue(accountId));

                allowing(person).getOpenSocialId();
                will(returnValue(openSocialId));

                allowing(person).getEntityId();
                will(returnValue(entityId));
            }
        });

        Principal result = sut.getPrincipal(openSocialId);
        assertEquals(accountId, result.getAccountId());
        assertEquals(openSocialId, result.getOpenSocialId());
        assertEquals(entityId, result.getId().longValue());

        context.assertIsSatisfied();
    }

    /**
     * Test getPrincipal.
     */
    @Test(expected = PrincipalPopulationException.class)
    public void testGetPrincipalFailNullResult()
    {
        context.checking(new Expectations()
        {
            {
                allowing(personMapper).fetchUniqueResult(openSocialId);
                will(returnValue(null));

                oneOf(personAccountIdsMapper).fetchUniqueResult(openSocialId);
                will(returnValue(null));
            }
        });

        Principal result = sut.getPrincipal(openSocialId);

        context.assertIsSatisfied();
    }

    /**
     * Test getPrincipal.
     */
    @Test(expected = PrincipalPopulationException.class)
    public void testGetPrincipalFailMapperException()
    {
        context.checking(new Expectations()
        {
            {
                allowing(personMapper).fetchUniqueResult(openSocialId);
                will(throwException(new Exception()));
            }
        });

        Principal result = sut.getPrincipal(openSocialId);

        context.assertIsSatisfied();
    }
}
