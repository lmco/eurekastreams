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
package org.eurekastreams.server.action.authorization.profile;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * test persist authorization for education.
 *
 */
public class PersistEmploymentAuthorizationStrategyTest extends MapperTest
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
     * Job mapper.
     */
    @Autowired
    private FindByIdMapper<Job> jobFindMapper;

    /**
     * subject under test.
     */
    PersistEmploymentAuthorizationStrategy sut;

    /**
     * Test form data.
     */
    HashMap<String, Serializable> formdata;

    /**
     * Test setup.
     */
    @Before
    public void setup()
    {

        sut = new PersistEmploymentAuthorizationStrategy(jobFindMapper);

        formdata = new HashMap<String, Serializable>();
    }

    /**
     * good update input test.
     */
    @Test
    public void testexecutionupdate()
    {
        final Long testId = 42L;

        formdata.put("id", Long.valueOf("2042"));
        final Principal principalMock = context.mock(Principal.class);
        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();
                will(returnValue(testId));
            }
        });
        sut.authorize(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * good update input test.
     */
    @Test(expected = AuthorizationException.class)
    public void testexecutionupdateFail()
    {

        formdata.put("id", Long.valueOf("2042"));
        final Principal principalMock = context.mock(Principal.class);
        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();
                will(returnValue(2L));
            }
        });

        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * good update input test.
     */
    @Test
    public void testAuthorizeInsert()
    {
        final Principal principalMock = context.mock(Principal.class);
        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);
        sut.authorize(currentContext);
    }
}
