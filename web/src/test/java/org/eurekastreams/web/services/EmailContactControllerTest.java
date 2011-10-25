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
package org.eurekastreams.web.services;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.commons.test.EasyMatcher;
import org.eurekastreams.server.domain.EntityIdentifier;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

/**
 * Test EmailContactController.
 */
public class EmailContactControllerTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Test data. */
    private static final String ADDRESS = "system+AAABBBAAACCC@eurekastreams.org";

    /** Test data. */
    private static final long PERSON_ID = 800L;

    /** For executing actions. */
    private final ActionController serviceActionController = mockery.mock(ActionController.class,
            "serviceActionController");

    /** Principal populator. */
    private final PrincipalPopulator principalPopulator = mockery.mock(PrincipalPopulator.class, "principalPopulator");

    /** Fixture: action. */
    private final ServiceAction getPersonAction = mockery.mock(ServiceAction.class, "action");

    /** For validating requests and selecting the right lookup action. */
    private final Map<EntityType, ServiceAction> typeToFetchActionIndex = Collections.unmodifiableMap(Collections
            .singletonMap(EntityType.PERSON, getPersonAction));

    /** Action to get stream token. */
    private final ServiceAction getTokenForStreamAction = mockery.mock(ServiceAction.class, "getTokenForStreamAction");

    /** Fixture: response. */
    private final HttpServletResponse response = mockery.mock(HttpServletResponse.class, "response");

    /** Fixture: principal. */
    private final Principal principal = mockery.mock(Principal.class, "principal");

    /** Fixture: person. */
    private final PersonModelView person = mockery.mock(PersonModelView.class, "person");

    /** SUT. */
    private EmailContactController sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new EmailContactController(serviceActionController, principalPopulator, typeToFetchActionIndex,
                getTokenForStreamAction);
    }

    /**
     * Test.
     */
    @Test
    public void test()
    {
        mockery.checking(new Expectations()
        {
            {
                allowing(principalPopulator).getPrincipal(with(any(String.class)), with(any(String.class)));
                will(returnValue(principal));

                allowing(serviceActionController).execute(with(new EasyMatcher<PrincipalActionContext>()
                {
                    @Override
                    protected boolean isMatch(final PrincipalActionContext inTestObject)
                    {
                        return (Long) inTestObject.getParams() == PERSON_ID
                                && inTestObject.getPrincipal() == principal;
                    }
                }), with(same(getPersonAction)));
                will(returnValue(person));

                allowing(serviceActionController).execute(with(new EasyMatcher<PrincipalActionContext>()
                {
                    @Override
                    protected boolean isMatch(final PrincipalActionContext inTestObject)
                    {
                        if (inTestObject.getParams() instanceof EntityIdentifier)
                        {
                            EntityIdentifier rqst = (EntityIdentifier) inTestObject.getParams();
                            return rqst.getType() == EntityType.PERSON && rqst.getId() == PERSON_ID
                                    && inTestObject.getPrincipal() == principal;
                        }

                        return false;
                    }
                }), with(same(getTokenForStreamAction)));
                will(returnValue(ADDRESS));

                oneOf(response).setHeader("Content-Disposition", "attachment");
                oneOf(response).setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                oneOf(response).addHeader("Pragma", "no-cache");

            }
        });

        ModelAndView result = sut.getStreamContact(EntityType.PERSON, PERSON_ID, response);
        mockery.assertIsSatisfied();

        assertEquals("vcardView", result.getViewName());
        assertSame(person, result.getModel().get("streamEntity"));
        assertEquals(ADDRESS, result.getModel().get("email"));
    }

    /**
     * Test.
     */
    @Test(expected = ExecutionException.class)
    public void testUnsupportedType()
    {
        mockery.checking(new Expectations()
        {
            {
                allowing(principalPopulator).getPrincipal(with(any(String.class)), with(any(String.class)));
                will(returnValue(principal));
            }
        });

        sut.getStreamContact(EntityType.APPLICATION, 1L, response);
        mockery.assertIsSatisfied();
    }
}
