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
package org.eurekastreams.server.service.restlets.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Request;

/**
 * Test for OAuthRequestToPrincipalTransformer.
 */
public class OAuthRequestToPrincipalTransformerTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * The mocked web request.
     */
    private final Request request = mockery.mock(Request.class);

    /** Principal from account ID DAO. */
    private final DomainMapper<String, Principal> accountIdPrincipalDao = mockery.mock(DomainMapper.class,
            "accountIdPrincipalDao");

    /** Principal from OpenSocial ID DAO. */
    private final DomainMapper<String, Principal> openSocialIdPrincipalDao = mockery.mock(DomainMapper.class,
            "openSocialIdPrincipalDao");

    /**
     * Principal mock.
     */
    private final Principal principal = mockery.mock(Principal.class);

    /**
     * System under test.
     */
    private OAuthRequestToPrincipalTransformer sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new OAuthRequestToPrincipalTransformer(accountIdPrincipalDao, openSocialIdPrincipalDao);
    }

    /**
     * Test the simple flow through getAccountId.
     */
    @Test
    public void testWithAccountId()
    {
        final Map<String, Object> requestAttributes = new HashMap<String, Object>();
        final Form headers = new Form();
        headers.add("accountid", "testaccountid");
        requestAttributes.put("org.restlet.http.headers", headers);

        mockery.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(requestAttributes));

                allowing(accountIdPrincipalDao).execute("testaccountid");
                will(returnValue(principal));

            }
        });

        assertEquals(principal, sut.transform(request));

        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testWithNullPrincipal()
    {
        final Map<String, Object> requestAttributes = new HashMap<String, Object>();
        final Form headers = new Form();
        headers.add("accountid", "testaccountid");
        requestAttributes.put("org.restlet.http.headers", headers);

        mockery.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(requestAttributes));

                oneOf(accountIdPrincipalDao).execute("testaccountid");
                will(returnValue(null));

            }
        });

        assertNull(sut.transform(request));

        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testWithOSId()
    {
        final Map<String, Object> requestAttributes = new HashMap<String, Object>();
        final Form headers = new Form();
        requestAttributes.put("org.restlet.http.headers", headers);

        final Reference origRef = mockery.mock(Reference.class);
        final Form queryForm = mockery.mock(Form.class);

        final String osId = "osid";

        mockery.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(requestAttributes));

                oneOf(request).getOriginalRef();
                will(returnValue(origRef));

                oneOf(origRef).getQueryAsForm();
                will(returnValue(queryForm));

                oneOf(queryForm).getFirstValue("opensocial_viewer_id");
                will(returnValue(osId));

                allowing(openSocialIdPrincipalDao).execute(osId);
                will(returnValue(principal));
            }
        });

        assertEquals(principal, sut.transform(request));

        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testWithNullAccountId()
    {
        final Map<String, Object> requestAttributes = new HashMap<String, Object>();
        final Form headers = new Form();
        requestAttributes.put("org.restlet.http.headers", headers);

        final Reference origRef = mockery.mock(Reference.class);
        final Form queryForm = mockery.mock(Form.class);

        mockery.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(requestAttributes));

                oneOf(request).getOriginalRef();
                will(returnValue(origRef));

                oneOf(origRef).getQueryAsForm();
                will(returnValue(queryForm));

                oneOf(queryForm).getFirstValue("opensocial_viewer_id");
                will(returnValue(null));

            }
        });

        assertNull(sut.transform(request));

        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testWithEmptyAttributes()
    {
        final Map<String, Object> requestAttributes = new HashMap<String, Object>();

        final Reference origRef = mockery.mock(Reference.class);
        final Form queryForm = mockery.mock(Form.class);

        mockery.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(requestAttributes));

                oneOf(request).getOriginalRef();
                will(returnValue(origRef));

                oneOf(origRef).getQueryAsForm();
                will(returnValue(queryForm));

                oneOf(queryForm).getFirstValue("opensocial_viewer_id");
                will(returnValue(null));
            }
        });

        assertNull(sut.transform(request));

        mockery.assertIsSatisfied();
    }
}
