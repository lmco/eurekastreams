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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.GroupLookupRequest;
import org.eurekastreams.server.persistence.mappers.ldap.LdapGroup;
import org.eurekastreams.server.persistence.mappers.ldap.LdapLookup;
import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for VerifyLdapGroupExecution class.
 */
public class VerifyLdapGroupExecutionTest
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
    private VerifyLdapGroupExecution sut;

    /**
     * {@link LdapLookup} mock.
     */
    @SuppressWarnings("unchecked")
    private LdapLookup lookup = context.mock(LdapLookup.class);

    /**
     * {@link PrincipalActionContext} mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link GroupLookupRequest} mock.
     */
    private GroupLookupRequest params = context.mock(GroupLookupRequest.class);

    /**
     * List used for ldapGroup results.
     */
    private List<LdapGroup> results = new ArrayList<LdapGroup>();

    /**
     * Query string used for tests.
     */
    private static final String QUERY_STRING = "some.group";

    /**
     * Setup.
     */
    @SuppressWarnings("unchecked")
    @Before
    public final void setUp()
    {
        results.clear();
        sut = new VerifyLdapGroupExecution(lookup);
    }

    /**
     * Test performing the action.
     */
    @Test
    public final void testPerformActionWithGroup()
    {
        LdapGroup group = context.mock(LdapGroup.class);
        results.add(group);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(params));

                allowing(params).getQueryString();
                will(returnValue(QUERY_STRING));

                oneOf(lookup).execute(with(any(LdapLookupRequest.class)));
                will(returnValue(results));
            }
        });

        assertEquals(true, sut.execute(actionContext));
        context.assertIsSatisfied();
    }

    /**
     * Test performing the action.
     */
    @Test
    public final void testPerformActionNoGroup()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(params));

                allowing(params).getQueryString();
                will(returnValue(QUERY_STRING));

                oneOf(lookup).execute(with(any(LdapLookupRequest.class)));
                will(returnValue(results));
            }
        });

        assertEquals(false, sut.execute(actionContext));
        context.assertIsSatisfied();
    }
}
