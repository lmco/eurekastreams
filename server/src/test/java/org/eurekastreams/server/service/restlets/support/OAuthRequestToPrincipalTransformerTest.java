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
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.server.action.principal.OpenSocialPrincipalPopulator;
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
 * 
 */
public class OAuthRequestToPrincipalTransformerTest
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
     * The mocked web request.
     */
    private Request request = context.mock(Request.class);

    /**
     * PrincipalPopulator mock.
     */
    private PrincipalPopulator pp = context.mock(PrincipalPopulator.class);

    /**
     * OpenSocialPrincipalPopulator mock.
     */
    private OpenSocialPrincipalPopulator opp = context.mock(OpenSocialPrincipalPopulator.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

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
        sut = new OAuthRequestToPrincipalTransformer(pp, opp);
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

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(requestAttributes));

                allowing(pp).getPrincipal("testaccountid", "");
                will(returnValue(principal));

            }
        });

        assertEquals(principal, sut.transform(request));

        context.assertIsSatisfied();
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

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(requestAttributes));

                oneOf(pp).getPrincipal("testaccountid", "");
                will(returnValue(null));

            }
        });

        assertNull(sut.transform(request));

        context.assertIsSatisfied();
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

        final Reference origRef = context.mock(Reference.class);
        final Form queryForm = context.mock(Form.class);
        
        final String osId = "osid";

        context.checking(new Expectations()
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
                
                allowing(opp).getPrincipal(osId);
                will(returnValue(principal));
            }
        });

        assertEquals(principal, sut.transform(request));

        context.assertIsSatisfied();
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

        final Reference origRef = context.mock(Reference.class);
        final Form queryForm = context.mock(Form.class);

        context.checking(new Expectations()
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

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testWithEmptyAttributes()
    {
        final Map<String, Object> requestAttributes = new HashMap<String, Object>();

        final Reference origRef = context.mock(Reference.class);
        final Form queryForm = context.mock(Form.class);

        context.checking(new Expectations()
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

        context.assertIsSatisfied();
    }

}
