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
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * 
 * Test for CreateGroupValidation class.
 * 
 */
public class CreateGroupValidationTest
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
     * {@link DomainGroupMapper}.
     */
    private GetDomainGroupsByShortNames groupMapperMock = context.mock(GetDomainGroupsByShortNames.class);
    
    /**
     * {@link OrganizationMapper}.
     */
    private GetOrganizationsByShortNames orgMapperMock = context.mock(GetOrganizationsByShortNames.class);

    /**
     * {@link CreateGroupValidation} system under test.
     */
    private CreateGroupValidation sut = new CreateGroupValidation(groupMapperMock, orgMapperMock);

    /**
     * Test validateParams() with valid inputs.
     */
    @Test
    public void validateParams()
    {

        HashSet<Person> coordinators = new HashSet<Person>();

        final Organization orgMock = context.mock(Organization.class);
        Person personMock = context.mock(Person.class);
        coordinators.add(personMock);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

        formData.put(DomainGroupModelView.NAME_KEY, ValidationTestHelper.generateString(DomainGroup.MAX_NAME_LENGTH));
        formData.put(DomainGroupModelView.SHORT_NAME_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_SHORT_NAME_LENGTH));
        formData.put(DomainGroupModelView.ORG_PARENT_KEY, "isgs");
        formData.put(DomainGroupModelView.PRIVACY_KEY, true);
        formData.put(DomainGroupModelView.COORDINATORS_KEY, coordinators);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));
                oneOf(groupMapperMock).fetchUniqueResult(with(any(String.class)));
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

        formData.put(DomainGroupModelView.NAME_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_NAME_LENGTH + 1));
        formData.put(DomainGroupModelView.SHORT_NAME_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_SHORT_NAME_LENGTH + 1));
        formData.put(DomainGroupModelView.ORG_PARENT_KEY, null);
        formData.put(DomainGroupModelView.COORDINATORS_KEY, coordinators);

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
            assertEquals(5, ve.getErrors().size());
            assertTrue(ve.getErrors().containsValue(DomainGroup.SHORT_NAME_LENGTH_MESSAGE));
            assertTrue(ve.getErrors().containsValue(DomainGroup.NAME_LENGTH_MESSAGE));
            assertTrue(ve.getErrors().containsValue(DomainGroup.MIN_COORDINATORS_MESSAGE));
            assertTrue(ve.getErrors().containsValue(CreateGroupValidation.MUST_HAVE_PARENT_ORG_MESSAGE));
            assertTrue(ve.getErrors().containsValue(ValidationHelper.UNEXPECTED_DATA_ERROR_MESSAGE));
            
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

        formData.put(DomainGroupModelView.NAME_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_NAME_LENGTH + 1));
        formData.put(DomainGroupModelView.SHORT_NAME_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_SHORT_NAME_LENGTH));
        formData.put(DomainGroupModelView.ORG_PARENT_KEY, "nonehere");
        formData.put(DomainGroupModelView.COORDINATORS_KEY, coordinators);
        formData.put(DomainGroupModelView.PRIVACY_KEY, true);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));
                oneOf(groupMapperMock).fetchUniqueResult(with(any(String.class)));
                oneOf(orgMapperMock).fetchUniqueResult("nonehere");
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
            assertEquals(4, ve.getErrors().size());
            assertTrue(ve.getErrors().containsValue(CreateGroupValidation.SHORTNAME_TAKEN_MESSAGE));
            assertTrue(ve.getErrors().containsValue(CreateGroupValidation.NO_SUCH_PARENT_ORG));
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

        formData.put(DomainGroupModelView.NAME_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_NAME_LENGTH + 1));
        formData.put(DomainGroupModelView.SHORT_NAME_KEY, "S UO|?DOG");
        formData.put(DomainGroupModelView.ORG_PARENT_KEY, null);
        formData.put(DomainGroupModelView.COORDINATORS_KEY, coordinators);
        formData.put(DomainGroupModelView.PRIVACY_KEY, true);

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
            assertTrue(ve.getErrors().containsValue(DomainGroup.SHORT_NAME_CHARACTERS));
            throw ve;
        }
        context.assertIsSatisfied();
    }
}
