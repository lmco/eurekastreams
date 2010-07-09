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
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * 
 * Test for CreateOrganizationValidation class.
 * 
 */
public class CreateOrganizationValidationTest
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
     * {@link GetOrganizationsByShortNames}.
     */
    private GetOrganizationsByShortNames orgMapperMock = context.mock(GetOrganizationsByShortNames.class);

    /**
     * {@link CreateGroupValidation} system under test.
     */
    private CreateOrganizationValidation sut = new CreateOrganizationValidation(orgMapperMock);

    /**
     * Test validateParams() with valid inputs.
     */
    @Test
    public void validateParams()
    {

        HashSet<Person> coordinators = new HashSet<Person>();

        Person fakePerson = context.mock(Person.class);
        final Organization orgMock = context.mock(Organization.class);
        coordinators.add(fakePerson);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

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
                oneOf(orgMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(null));
                oneOf(orgMapperMock).fetchUniqueResult("isgs");
                will(returnValue(orgMock));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with bad url.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithRanges()
    {
        HashSet<Person> coordinators = new HashSet<Person>();

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

        formData.put(OrganizationModelView.NAME_KEY, ValidationTestHelper
                .generateString(Organization.MAX_NAME_LENGTH + 1));
        formData.put(OrganizationModelView.SHORT_NAME_KEY, ValidationTestHelper
                .generateString(Organization.MAX_NAME_LENGTH + 1));
        formData.put(OrganizationModelView.ORG_PARENT_KEY, null);
        formData.put(OrganizationModelView.COORDINATORS_KEY, coordinators);

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
            assertTrue(ve.getErrors().containsValue(Organization.NAME_LENGTH_MESSAGE));
            assertTrue(ve.getErrors().containsValue(Organization.SHORT_NAME_LENGTH_MESSAGE));
            assertTrue(ve.getErrors().containsValue(Organization.MIN_COORDINATORS_MESSAGE));
            assertTrue(ve.getErrors().containsValue(CreateOrganizationValidation.MUST_HAVE_PARENT_ORG_MESSAGE));
            throw ve;
        }
        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with bad url.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithRedundentShortname()
    {
        HashSet<Person> coordinators = new HashSet<Person>();

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        final Organization orgMock = context.mock(Organization.class);

        formData.put(OrganizationModelView.NAME_KEY, ValidationTestHelper.generateString(Organization.MAX_NAME_LENGTH));
        formData.put(OrganizationModelView.SHORT_NAME_KEY, "org");
        formData.put(OrganizationModelView.ORG_PARENT_KEY, "nosuchorg");
        formData.put(OrganizationModelView.COORDINATORS_KEY, coordinators);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));
                oneOf(orgMapperMock).fetchUniqueResult("org");
                will(returnValue(orgMock));
                oneOf(orgMapperMock).fetchUniqueResult("nosuchorg");
                will(returnValue(null));

            }
        });

        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException ve)
        {
            context.assertIsSatisfied();
            assertEquals(3, ve.getErrors().size());
            assertTrue(ve.getErrors().containsValue(CreateOrganizationValidation.SHORTNAME_TAKEN_MESSAGE));
            assertTrue(ve.getErrors().containsValue(Organization.MIN_COORDINATORS_MESSAGE));
            assertTrue(ve.getErrors().containsValue(CreateOrganizationValidation.NO_SUCH_PARENT_ORG));
            throw ve;
        }
        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with bad url.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithAlphanumbericShortName()
    {
        HashSet<Person> coordinators = new HashSet<Person>();

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

        formData.put(OrganizationModelView.NAME_KEY, ValidationTestHelper
                .generateString(Organization.MAX_NAME_LENGTH + 1));
        formData.put(OrganizationModelView.SHORT_NAME_KEY, "S UO|?DOG");
        formData.put(OrganizationModelView.ORG_PARENT_KEY, null);
        formData.put(OrganizationModelView.COORDINATORS_KEY, coordinators);
        formData.put(OrganizationModelView.ALLOW_GROUP_CREATION_KEY, true);

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
            assertTrue(ve.getErrors().containsValue(Organization.NAME_LENGTH_MESSAGE));
            assertTrue(ve.getErrors().containsValue(Organization.SHORT_NAME_CHARACTERS));
            assertTrue(ve.getErrors().containsValue(Organization.MIN_COORDINATORS_MESSAGE));
            assertTrue(ve.getErrors().containsValue(CreateOrganizationValidation.MUST_HAVE_PARENT_ORG_MESSAGE));
            throw ve;
        }
        context.assertIsSatisfied();
    }
}
