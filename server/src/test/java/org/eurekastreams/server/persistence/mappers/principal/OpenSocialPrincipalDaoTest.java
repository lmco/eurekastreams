/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.principal;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.exceptions.PrincipalPopulationException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByOpenSocialIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests OpenSocialPrincipalDao.
 */
public class OpenSocialPrincipalDaoTest
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
    private OpenSocialPrincipalDao sut;

    /**
     * Person Mapper.
     */
    private final GetPeopleByOpenSocialIds personMapper = context.mock(GetPeopleByOpenSocialIds.class);

    /**
     * Mapper to get a PersonModelView by accountId.
     */
    private final DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper = context.mock(
            DomainMapper.class, "getPersonModelViewByAccountIdMapper");

    /**
     * Person model view.
     */
    private final PersonModelView person = context.mock(PersonModelView.class);

    /**
     * Account id.
     */
    private final String accountId = "accountId";

    /**
     * Open social id.
     */
    private final String openSocialId = "openSocialId";

    /**
     * Entity id.
     */
    private final long entityId = 5L;

    /**
     * Set up.
     */
    @Before
    public void setup()
    {
        sut = new OpenSocialPrincipalDao(personMapper, getPersonModelViewByAccountIdMapper);
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecuteSuccess()
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

        Principal result = sut.execute(openSocialId);
        assertEquals(accountId, result.getAccountId());
        assertEquals(openSocialId, result.getOpenSocialId());
        assertEquals(entityId, result.getId().longValue());

        context.assertIsSatisfied();
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecuteSuccessOnNtIDResult()
    {
        context.checking(new Expectations()
        {
            {
                allowing(personMapper).fetchUniqueResult(openSocialId);
                will(returnValue(null));

                oneOf(getPersonModelViewByAccountIdMapper).execute(openSocialId);
                will(returnValue(person));

                allowing(person).getAccountId();
                will(returnValue(accountId));

                allowing(person).getOpenSocialId();
                will(returnValue(openSocialId));

                allowing(person).getEntityId();
                will(returnValue(entityId));
            }
        });

        Principal result = sut.execute(openSocialId);
        assertEquals(accountId, result.getAccountId());
        assertEquals(openSocialId, result.getOpenSocialId());
        assertEquals(entityId, result.getId().longValue());

        context.assertIsSatisfied();
    }

    /**
     * Test execute.
     */
    @Test(expected = PrincipalPopulationException.class)
    public void testExecuteFailNullResult()
    {
        context.checking(new Expectations()
        {
            {
                allowing(personMapper).fetchUniqueResult(openSocialId);
                will(returnValue(null));

                oneOf(getPersonModelViewByAccountIdMapper).execute(openSocialId);
                will(returnValue(null));
            }
        });

        Principal result = sut.execute(openSocialId);

        context.assertIsSatisfied();
    }

    /**
     * Test execute.
     */
    @Test(expected = PrincipalPopulationException.class)
    public void testExecuteFailMapperException()
    {
        context.checking(new Expectations()
        {
            {
                allowing(personMapper).fetchUniqueResult(openSocialId);
                will(throwException(new Exception()));
            }
        });

        Principal result = sut.execute(openSocialId);

        context.assertIsSatisfied();
    }
}
