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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.eurekastreams.server.persistence.mappers.ldap.callback.CallbackHandlerFactory;
import org.eurekastreams.server.persistence.mappers.ldap.filters.FilterCreator;
import org.eurekastreams.server.persistence.mappers.ldap.templateretrievers.LdapTemplateRetriever;
import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AbstractFilter;

/**
 * Test for LdapLookup.
 * 
 */
public class LdapLookupTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link LdapTemplateRetriever}.
     */
    private LdapTemplateRetriever ldapTemplateRetriever = context.mock(LdapTemplateRetriever.class);

    /**
     * LDAP filter creator for finding group by name.
     */
    private FilterCreator filterCreator = context.mock(FilterCreator.class);

    /**
     * The {@link CallbackHandlerFactory}.
     */
    private CallbackHandlerFactory handlerFactory = context.mock(CallbackHandlerFactory.class);

    /**
     * Strategy for performing the search against ldap.
     */
    private LdapSearchStrategy ldapSearchStrategy = context.mock(LdapSearchStrategy.class);

    /**
     * {@link LdapLookupRequest}.
     */
    private LdapLookupRequest ldapLookupRequest = context.mock(LdapLookupRequest.class);

    /**
     * {@link LdapTemplate).
     */
    private LdapTemplate template = context.mock(LdapTemplate.class);

    /**
     * {@link AbstractFilter}.
     */
    private AbstractFilter filter = context.mock(AbstractFilter.class);

    /**
     * {@link CollectingNameClassPairCallbackHandler}.
     */
    private CollectingNameClassPairCallbackHandler handler = context.mock(CollectingNameClassPairCallbackHandler.class);

    /**
     * String to use in request for query.
     */
    private String queryString = "queryString";

    /**
     * System under test.
     */
    private LdapLookup<Object> sut = new LdapLookup<Object>(ldapTemplateRetriever, filterCreator, handlerFactory,
            ldapSearchStrategy);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final ArrayList<Object> rawResults = new ArrayList<Object>(Arrays.asList(new Object(), null, new Object()));

        context.checking(new Expectations()
        {
            {
                allowing(ldapLookupRequest).getSearchUpperBound();
                will(returnValue(Integer.MAX_VALUE));

                allowing(ldapLookupRequest).getQueryString();
                will(returnValue(queryString));

                allowing(ldapTemplateRetriever).getLdapTemplate(ldapLookupRequest);
                will(returnValue(template));

                allowing(filterCreator).getFilter(queryString);
                will(returnValue(filter));

                allowing(handlerFactory).getCallbackHandler();
                will(returnValue(handler));

                allowing(filter).encode();
                will(returnValue("filter"));

                allowing(ldapSearchStrategy).searchLdap(with(any(LdapTemplate.class)), with(any(String.class)),
                        with(any(SearchControls.class)), with(any(CollectingNameClassPairCallbackHandler.class)));

                allowing(handler).getList();
                will(returnValue(rawResults));
            }
        });

        assertEquals(3, rawResults.size());

        List<Object> results = sut.execute(ldapLookupRequest);

        assertEquals(2, results.size());

        context.assertIsSatisfied();
    }
}
