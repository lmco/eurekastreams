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
package org.eurekastreams.server.persistence.mappers.ldap;


import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests LdapSingleValueLookupMapper.
 */
public class LdapSingleValueLookupMapperTest
{
    /** Test data. */
    private static final String NTID = "jdoe";

    /** Test data. */
    private static final String UPN = "jdoe@REALM";

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture. */
    private DomainMapper<LdapLookupRequest, List<String>> ldapQueryMapper = context.mock(DomainMapper.class);

    /** SUT. */
    private LdapSingleValueLookupMapper sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new LdapSingleValueLookupMapper(ldapQueryMapper);
    }

    /**
     * Tests mapping.
     */
    @Test
    public void testExecuteOneResult()
    {
        final LdapLookupRequest rqst = new LdapLookupRequest(NTID);

        context.checking(new Expectations()
        {
            {
                allowing(ldapQueryMapper).execute(with(equalInternally(rqst)));
                will(returnValue(Collections.singletonList(UPN)));
            }
        });

        String result = sut.execute(NTID);
        context.assertIsSatisfied();
        assertEquals(UPN, result);
    }

    /**
     * Tests mapping.
     */
    @Test
    public void testExecuteNoResults()
    {
        final LdapLookupRequest rqst = new LdapLookupRequest(NTID);

        context.checking(new Expectations()
        {
            {
                allowing(ldapQueryMapper).execute(with(equalInternally(rqst)));
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        String result = sut.execute(NTID);
        context.assertIsSatisfied();
        assertNull(result);
    }
}
