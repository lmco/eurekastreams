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
package org.eurekastreams.server.action.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * 
 * Test for UpdateOrganizationValidation class.
 * 
 */
public class UpdateOrganizationValidationTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link UpdateOrganizationValidation} system under test.
     */
    private UpdateOrganizationValidation sut = new UpdateOrganizationValidation();

    /**
     * Test validateParams() with valid inputs.
     */
    @Test
    public void validateParams()
    {

        HashSet<Person> coordinators = new HashSet<Person>();

        Person fakePerson = context.mock(Person.class);
        coordinators.add(fakePerson);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

        formData.put(OrganizationModelView.ID_KEY, 2L);
        formData.put(OrganizationModelView.URL_KEY, "http://www.google.com");
        formData.put(OrganizationModelView.DESCRIPTION_KEY, ValidationTestHelper
                .generateString(Organization.MAX_DESCRIPTION_LENGTH));
        formData.put(OrganizationModelView.NAME_KEY, ValidationTestHelper.generateString(Organization.MAX_NAME_LENGTH));
        formData.put(OrganizationModelView.SHORT_NAME_KEY, ValidationTestHelper
                .generateString(Organization.MAX_NAME_LENGTH));
        formData.put(OrganizationModelView.ORG_PARENT_KEY, "isgs");
        formData.put(OrganizationModelView.ALLOW_GROUP_CREATION_KEY, true);

        formData.put(OrganizationModelView.COORDINATORS_KEY, coordinators);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with bad url.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithInvalidParams()
    {
        HashSet<Person> coordinators = new HashSet<Person>();

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

        formData.put(OrganizationModelView.ID_KEY, 2L);
        formData.put(OrganizationModelView.SHORT_NAME_KEY, "someShortName");

        formData.put(OrganizationModelView.URL_KEY, "www.google.com");
        formData.put(OrganizationModelView.DESCRIPTION_KEY, ValidationTestHelper
                .generateString(Organization.MAX_DESCRIPTION_LENGTH + 1));
        formData.put(OrganizationModelView.NAME_KEY, ValidationTestHelper
                .generateString(Organization.MAX_NAME_LENGTH + 1));
        formData.put(OrganizationModelView.COORDINATORS_KEY, coordinators);
        formData.put(OrganizationModelView.ALLOW_GROUP_CREATION_KEY, false);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));
            }
        });

        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException ve)
        {
            context.assertIsSatisfied();
            assertEquals(4, ve.getErrors().size());
            assertTrue(ve.getErrors().containsValue(Organization.WEBSITE_MESSAGE));
            assertTrue(ve.getErrors().containsValue(Organization.MIN_COORDINATORS_MESSAGE));
            assertTrue(ve.getErrors().containsValue(Organization.NAME_LENGTH_MESSAGE));
            assertTrue(ve.getErrors().containsValue(Organization.DESCRIPTION_LENGTH_MESSAGE));
            throw ve;
        }
        context.assertIsSatisfied();
    }
}
