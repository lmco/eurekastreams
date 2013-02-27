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
package org.eurekastreams.server.persistence.mappers.ldap.templateretrievers;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Test for SearchStringLdapTemplateRetriever.
 * 
 */
public class SearchStringLdapTemplateRetrieverTest
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
     * Map of ldap templates.
     */
    private HashMap<String, LdapTemplate> ldapTemplates = new HashMap<String, LdapTemplate>();

    /**
     * Default {@link LdapTemplate}.
     */
    private LdapTemplate defaultLdapTemplate = context.mock(LdapTemplate.class, "defaultTemplate");

    /**
     * {@link LdapContextSource}.
     */
    private final LdapContextSource contextSource = context.mock(LdapContextSource.class);

    /**
     * ContextSource urls.
     */
    private String[] urls = new String[] { "url" };

    /**
     * {@link LdapTemplate}.
     */
    private LdapTemplate mappedLdapTemplate1 = context.mock(LdapTemplate.class, "mappedLdapTemplate1");

    /**
     * {@link LdapLookupRequest}.
     */
    private LdapLookupRequest ldapLookupRequest = context.mock(LdapLookupRequest.class);

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        ldapTemplates.clear();
    }

    /**
     *Test.
     */
    @Test
    public void testNullStringAndKey()
    {
        SearchStringLdapTemplateRetriever sut = new SearchStringLdapTemplateRetriever(ldapTemplates,
                defaultLdapTemplate);

        context.checking(new Expectations()
        {
            {
                allowing(ldapLookupRequest).getQueryString();
                will(returnValue(null));

                allowing(ldapLookupRequest).getTemplateKey();
                will(returnValue(null));

                allowing(defaultLdapTemplate).getContextSource();
                will(returnValue(contextSource));

                allowing(contextSource).getUrls();
                will(returnValue(urls));

                allowing(contextSource).getBaseLdapPathAsString();
                will(returnValue("dc=blah"));
            }
        });

        LdapTemplate result = sut.getLdapTemplate(ldapLookupRequest);
        assertEquals(defaultLdapTemplate, result);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testStringDomainFound()
    {
        ldapTemplates.put("key", mappedLdapTemplate1);
        SearchStringLdapTemplateRetriever sut = new SearchStringLdapTemplateRetriever(ldapTemplates,
                defaultLdapTemplate);

        context.checking(new Expectations()
        {
            {
                allowing(ldapLookupRequest).getQueryString();
                will(returnValue("key\\blah"));

                allowing(ldapLookupRequest).getTemplateKey();
                will(returnValue(null));

                allowing(ldapLookupRequest).setQueryString("blah");

                allowing(mappedLdapTemplate1).getContextSource();
                will(returnValue(contextSource));

                allowing(contextSource).getUrls();
                will(returnValue(urls));

                allowing(contextSource).getBaseLdapPathAsString();
                will(returnValue("dc=blah"));
            }
        });

        LdapTemplate result = sut.getLdapTemplate(ldapLookupRequest);
        assertEquals(mappedLdapTemplate1, result);
        context.assertIsSatisfied();
    }

    /**
     *Test.
     */
    @Test
    public void testStringDomainNotFoundKeyFound()
    {
        ldapTemplates.put("key", mappedLdapTemplate1);
        SearchStringLdapTemplateRetriever sut = new SearchStringLdapTemplateRetriever(ldapTemplates,
                defaultLdapTemplate);

        context.checking(new Expectations()
        {
            {
                allowing(ldapLookupRequest).getQueryString();
                will(returnValue("notFoundKey\\blah"));

                allowing(ldapLookupRequest).getTemplateKey();
                will(returnValue("key"));

                allowing(ldapLookupRequest).setQueryString("blah");

                allowing(mappedLdapTemplate1).getContextSource();
                will(returnValue(contextSource));

                allowing(contextSource).getUrls();
                will(returnValue(urls));
                
                allowing(contextSource).getBaseLdapPathAsString();
                will(returnValue("dc=blah"));
            }
        });

        LdapTemplate result = sut.getLdapTemplate(ldapLookupRequest);
        assertEquals(mappedLdapTemplate1, result);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testStringDomainNotFoundKeyNotFound()
    {
        SearchStringLdapTemplateRetriever sut = new SearchStringLdapTemplateRetriever(ldapTemplates,
                defaultLdapTemplate);

        context.checking(new Expectations()
        {
            {
                allowing(ldapLookupRequest).getQueryString();
                will(returnValue("notFoundKey\\blah"));

                allowing(ldapLookupRequest).getTemplateKey();
                will(returnValue("key"));

                allowing(ldapLookupRequest).setQueryString("blah");

                allowing(defaultLdapTemplate).getContextSource();
                will(returnValue(contextSource));

                allowing(contextSource).getUrls();
                will(returnValue(urls));

                allowing(contextSource).getBaseLdapPathAsString();
                will(returnValue("dc=blah"));
            }
        });

        LdapTemplate result = sut.getLdapTemplate(ldapLookupRequest);
        assertEquals(defaultLdapTemplate, result);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testNoStringDomainFoundKeyFound()
    {
        ldapTemplates.put("key", mappedLdapTemplate1);
        SearchStringLdapTemplateRetriever sut = new SearchStringLdapTemplateRetriever(ldapTemplates,
                defaultLdapTemplate);

        context.checking(new Expectations()
        {
            {
                allowing(ldapLookupRequest).getQueryString();
                will(returnValue("blah"));

                allowing(ldapLookupRequest).getTemplateKey();
                will(returnValue("key"));

                allowing(mappedLdapTemplate1).getContextSource();
                will(returnValue(contextSource));

                allowing(contextSource).getUrls();
                will(returnValue(urls));
                
                allowing(contextSource).getBaseLdapPathAsString();
                will(returnValue("dc=blah"));
            }
        });

        LdapTemplate result = sut.getLdapTemplate(ldapLookupRequest);
        assertEquals(mappedLdapTemplate1, result);
        context.assertIsSatisfied();
    }
}
