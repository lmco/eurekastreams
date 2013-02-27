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

/**
 * Test for LdapGroupDnLdapTemplateRetriever.
 * 
 */
public class LdapGroupDnLdapTemplateRetrieverTest
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
     * Test with null key.
     */
    @Test
    public void testNullKey()
    {
        LdapGroupDnLdapTemplateRetriever sut = new LdapGroupDnLdapTemplateRetriever(ldapTemplates, defaultLdapTemplate);

        context.checking(new Expectations()
        {
            {
                allowing(ldapLookupRequest).getTemplateKey();
                will(returnValue(null));
                
                allowing(ldapLookupRequest).getQueryString();
                will(returnValue(""));
            }
        });

        LdapTemplate result = sut.getLdapTemplate(ldapLookupRequest);
        assertEquals(defaultLdapTemplate, result);
        context.assertIsSatisfied();
    }

    /**
     * Test with empty key.
     */
    @Test
    public void testEmptyKey()
    {
        LdapGroupDnLdapTemplateRetriever sut = new LdapGroupDnLdapTemplateRetriever(ldapTemplates, defaultLdapTemplate);

        context.checking(new Expectations()
        {
            {
                allowing(ldapLookupRequest).getTemplateKey();
                will(returnValue(""));
            }
        });

        LdapTemplate result = sut.getLdapTemplate(ldapLookupRequest);
        assertEquals(defaultLdapTemplate, result);
        context.assertIsSatisfied();
    }

    /**
     * Test with key found.
     */
    @Test
    public void testKeyFound()
    {
        ldapTemplates.put("key", mappedLdapTemplate1);
        LdapGroupDnLdapTemplateRetriever sut = new LdapGroupDnLdapTemplateRetriever(ldapTemplates, defaultLdapTemplate);

        context.checking(new Expectations()
        {
            {
                allowing(ldapLookupRequest).getTemplateKey();
                will(returnValue("cn=blah,dc=key"));
            }
        });

        LdapTemplate result = sut.getLdapTemplate(ldapLookupRequest);
        assertEquals(mappedLdapTemplate1, result);
        context.assertIsSatisfied();
    }

    /**
     * Test with key not found.
     */
    @Test
    public void testKeyNotFound()
    {
        LdapGroupDnLdapTemplateRetriever sut = new LdapGroupDnLdapTemplateRetriever(ldapTemplates, defaultLdapTemplate);

        context.checking(new Expectations()
        {
            {
                allowing(ldapLookupRequest).getTemplateKey();
                will(returnValue("cn=blah,dc=key"));
            }
        });

        LdapTemplate result = sut.getLdapTemplate(ldapLookupRequest);
        assertEquals(defaultLdapTemplate, result);
        context.assertIsSatisfied();
    }

}
