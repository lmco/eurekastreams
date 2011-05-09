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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for person update validation.
 * 
 */
public class UpdatePersonValidationTest
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
     * Mocked principal object for test.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * email address validator.
     */
    private EmailAddressValidator emailValidator = new EmailAddressValidator(".*@.*", "");

    /**
     * subject under test.
     */
    private UpdatePersonValidation sut;

    /**
     * Test setup.
     */
    @Before
    public void setup()
    {
        sut = new UpdatePersonValidation(emailValidator);

    }

    /**
     * good input test.
     */
    @Test
    public void testgoodvalidation()
    {

        HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put(PersonModelView.TITILE_KEY, ValidationTestHelper.generateString(Person.MAX_TITLE_LENGTH));
        formData.put(PersonModelView.PREFERREDNAME_KEY, ValidationTestHelper
                .generateString(UpdatePersonValidation.DEFAULT_MAX_STRING_LENGTH));
        formData.put(PersonModelView.DESCRIPTION_KEY, ValidationTestHelper
                .generateString(Person.MAX_JOB_DESCRIPTION_LENGTH));
        formData.put(PersonModelView.WORKPHONE_KEY,
        // line break
                ValidationTestHelper.generateString(Person.MAX_PHONE_NUMBER_LENGTH));
        formData.put(PersonModelView.CELLPHONE_KEY,
        // line break
                ValidationTestHelper.generateString(Person.MAX_PHONE_NUMBER_LENGTH));
        formData.put(PersonModelView.FAX_KEY, ValidationTestHelper.generateString(Person.MAX_PHONE_NUMBER_LENGTH));
        formData.put(PersonModelView.EMAIL_KEY, "email@email.com");
        formData.put(PersonModelView.SKILLS_KEY, ValidationTestHelper
                .generateString(BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH));

        final ServiceActionContext currentContext = new ServiceActionContext(formData, principalMock);

        sut.validate(currentContext);
    }

    /**
     * test required fields.
     */
    @Test(expected = ValidationException.class)
    public void testbadvalidationEmpty()
    {
        HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put(PersonModelView.TITILE_KEY, "");
        formData.put(PersonModelView.PREFERREDNAME_KEY, "");
        formData.put(PersonModelView.DESCRIPTION_KEY, "");
        formData.put(PersonModelView.WORKPHONE_KEY, "");
        formData.put(PersonModelView.CELLPHONE_KEY, "");
        formData.put(PersonModelView.FAX_KEY, "");
        formData.put(PersonModelView.EMAIL_KEY, "");
        formData.put(PersonModelView.SKILLS_KEY, "");

        final ServiceActionContext currentContext = new ServiceActionContext(formData, principalMock);
        try
        {
            sut.validate(currentContext);

        }
        catch (ValidationException e)
        {
            assertEquals(3, e.getErrors().size());
            assertTrue(e.getErrors().containsKey(PersonModelView.TITILE_KEY));
            assertTrue(e.getErrors().containsKey(PersonModelView.PREFERREDNAME_KEY));
            assertTrue(e.getErrors().containsKey(PersonModelView.EMAIL_KEY));
            throw e;
        }
    }

    /**
     * test required fields.
     */
    @Test(expected = ValidationException.class)
    public void testbadvalidationNofieldsSent()
    {
        HashMap<String, Serializable> formdata = new HashMap<String, Serializable>();
        final int errorSize = 8;
        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);
        try
        {
            sut.validate(currentContext);

        }
        catch (ValidationException e)
        {
            assertEquals(errorSize, e.getErrors().size());
            assertTrue(e.getErrors().containsValue(ValidationHelper.UNEXPECTED_DATA_ERROR_MESSAGE));
            throw e;
        }
    }

    /**
     * This email will fail while it is a valid RFC format we require a full address.
     */
    @Test(expected = ValidationException.class)
    public void testbadInsertValidationNeedFullAddress()
    {
        HashMap<String, Serializable> formdata = new HashMap<String, Serializable>();

        formdata.put(PersonModelView.TITILE_KEY, ValidationTestHelper.generateString(Person.MAX_TITLE_LENGTH + 1));
        formdata.put(PersonModelView.PREFERREDNAME_KEY, ValidationTestHelper
                .generateString(UpdatePersonValidation.DEFAULT_MAX_STRING_LENGTH + 1));
        formdata.put(PersonModelView.DESCRIPTION_KEY, ValidationTestHelper
                .generateString(Person.MAX_JOB_DESCRIPTION_LENGTH + 1));
        formdata.put(PersonModelView.WORKPHONE_KEY, ValidationTestHelper
                .generateString(Person.MAX_PHONE_NUMBER_LENGTH + 1));
        formdata.put(PersonModelView.CELLPHONE_KEY, ValidationTestHelper
                .generateString(Person.MAX_PHONE_NUMBER_LENGTH + 1));
        formdata.put(PersonModelView.FAX_KEY, ValidationTestHelper.generateString(Person.MAX_PHONE_NUMBER_LENGTH + 1));
        formdata.put(PersonModelView.EMAIL_KEY, "notanemail");
        formdata.put(PersonModelView.SKILLS_KEY, ValidationTestHelper
                .generateString(BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH + 1));

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);

        try
        {
            sut.validate(currentContext);

        }
        catch (ValidationException e)
        {
            context.assertIsSatisfied();
            assertEquals(8, e.getErrors().size());
            assertTrue(e.getErrors().containsValue(Person.EMAIL_MESSAGE));
            assertTrue(e.getErrors().containsValue(Person.TITLE_MESSAGE));
            assertTrue(e.getErrors().containsValue(UpdatePersonValidation.PREFERREDNAME_MESSAGE));
            assertTrue(e.getErrors().containsValue(Person.JOB_DESCRIPTION_MESSAGE));
            assertTrue(e.getErrors().containsKey(PersonModelView.CELLPHONE_KEY));
            assertTrue(e.getErrors().containsKey(PersonModelView.WORKPHONE_KEY));
            assertTrue(e.getErrors().containsValue(Person.FAX_NUMBER_MESSAGE));
            assertTrue(e.getErrors().containsValue(PersonModelView.SKILLS_MESSAGE));

            throw e;
        }
    }

    /**
     * This test will fail because of an invalid format of the email.
     */
    @Test(expected = ValidationException.class)
    public void testbadInsertValidationForBadFormatedEmail()
    {
        HashMap<String, Serializable> formdata = new HashMap<String, Serializable>();

        formdata.put(PersonModelView.TITILE_KEY, ValidationTestHelper.generateString(Person.MAX_TITLE_LENGTH + 1));
        formdata.put(PersonModelView.PREFERREDNAME_KEY, ValidationTestHelper
                .generateString(UpdatePersonValidation.DEFAULT_MAX_STRING_LENGTH + 1));
        formdata.put(PersonModelView.DESCRIPTION_KEY, ValidationTestHelper
                .generateString(Person.MAX_JOB_DESCRIPTION_LENGTH + 1));
        formdata.put(PersonModelView.WORKPHONE_KEY, ValidationTestHelper
                .generateString(Person.MAX_PHONE_NUMBER_LENGTH + 1));
        formdata.put(PersonModelView.CELLPHONE_KEY, ValidationTestHelper
                .generateString(Person.MAX_PHONE_NUMBER_LENGTH + 1));
        formdata.put(PersonModelView.FAX_KEY, ValidationTestHelper.generateString(Person.MAX_PHONE_NUMBER_LENGTH + 1));
        formdata.put(PersonModelView.EMAIL_KEY, "notanemail@ss..das");
        formdata.put(PersonModelView.SKILLS_KEY, "");

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);

        try
        {
            sut.validate(currentContext);

        }
        catch (ValidationException e)
        {
            context.assertIsSatisfied();
            assertEquals(7, e.getErrors().size());
            assertTrue(e.getErrors().containsValue(Person.TITLE_MESSAGE));
            assertTrue(e.getErrors().containsValue(UpdatePersonValidation.PREFERREDNAME_MESSAGE));
            assertTrue(e.getErrors().containsValue(Person.JOB_DESCRIPTION_MESSAGE));
            assertTrue(e.getErrors().containsKey(PersonModelView.CELLPHONE_KEY));
            assertTrue(e.getErrors().containsKey(PersonModelView.WORKPHONE_KEY));
            assertTrue(e.getErrors().containsValue(Person.FAX_NUMBER_MESSAGE));

            throw e;
        }
    }
}
