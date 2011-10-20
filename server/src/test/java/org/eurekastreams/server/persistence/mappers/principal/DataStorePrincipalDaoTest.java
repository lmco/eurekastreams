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
package org.eurekastreams.server.persistence.mappers.principal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests DataStorePrincipalDao.
 */
public class DataStorePrincipalDaoTest
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
     * Person mapper.
     */
    private final DomainMapper<String, PersonModelView> getPersonMVByAccountId = context.mock(DomainMapper.class);

    /**
     * {@link ActionController}.
     */
    private final ActionController serviceActionController = context.mock(ActionController.class);

    /**
     * Action to create user from LDAP.
     */
    private final TaskHandlerServiceAction createUserfromLdapAction = context.mock(TaskHandlerServiceAction.class);

    /**
     * Test person mock.
     */
    private final PersonModelView personModelViewMock = context.mock(PersonModelView.class);

    /**
     * Test person mock.
     */
    private final Person personMock = context.mock(Person.class);

    /**
     * System under test.
     */
    private DataStorePrincipalDao sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new DataStorePrincipalDao(getPersonMVByAccountId, serviceActionController, //
                createUserfromLdapAction);
    }

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(getPersonMVByAccountId).execute("accountId");
                will(returnValue(personModelViewMock));

                oneOf(personModelViewMock).getAccountId();
                will(returnValue("accountId"));

                oneOf(personModelViewMock).getOpenSocialId();
                will(returnValue("osid"));

                oneOf(personModelViewMock).getId();
                will(returnValue(5L));
            }
        });

        assertNotNull(sut.execute("accountId"));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testNullPmv()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(getPersonMVByAccountId).execute("accountId");
                will(returnValue(null));

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(any(TaskHandlerServiceAction.class)));
                will(returnValue(personMock));

                oneOf(personMock).getAccountId();
                will(returnValue("accountId"));

                oneOf(personMock).getOpenSocialId();
                will(returnValue("osid"));

                oneOf(personMock).getId();
                will(returnValue(5L));
            }
        });

        assertNotNull(sut.execute("accountId"));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testExceptionCatch()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(getPersonMVByAccountId).execute("accountId");
                will(throwException(new Exception()));
            }
        });

        assertNull(sut.execute("accountId"));
        context.assertIsSatisfied();
    }
}
