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
package org.eurekastreams.server.action.validation.settings;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the validator.
 */
@SuppressWarnings("unchecked")
public class UpdateSettingsValidationTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: validator. */
    private SettingsValidator validator1 = context.mock(SettingsValidator.class, "validator1");

    /** Fixture: validator. */
    private SettingsValidator validator2 = context.mock(SettingsValidator.class, "validator2");

    /** Fixture: settings. */
    private HashMap settings = context.mock(HashMap.class, "settings");

    /** Fixture: actionContext. */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

     /** Fixture: principal. */
    private Principal principal = context.mock(Principal.class);

    /** SUT. */
    private UpdateSettingsValidation sut;

    /**
     * Per-test setup.
     */
    @Before
    public void setUp()
    {
        sut = new UpdateSettingsValidation(Arrays.asList(validator1, validator2));
        context.checking(new Expectations()
        {
            {
                 allowing(actionContext).getPrincipal();
                will(returnValue(principal));
                allowing(actionContext).getParams();
                will(returnValue(settings));
            }
        });
    }

    /**
     * Tests with no errors.
     */
    @Test
    public void testValidateNoErrors()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(validator1).validate(settings, principal);
                oneOf(validator2).validate(settings, principal);
            }
        });

        sut.validate(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Tests with one validator failing.
     */
    @Test(expected = ValidationException.class)
    public void testValidateOneFail()
    {
        final ValidationException v2ex = new ValidationException();
        v2ex.addError("f2a", "m2a");
        v2ex.addError("f2b", "m2b");

        context.checking(new Expectations()
        {
            {
                oneOf(validator1).validate(settings, principal);
                oneOf(validator2).validate(settings, principal);
                will(throwException(v2ex));
            }
        });

        // intercept the ValidationException to inspect it - insure all error messages present
        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException vex)
        {
            Map<String, String> errors = vex.getErrors();
            assertEquals(2, errors.size());
            assertEquals("m2a", errors.get("f2a"));
            assertEquals("m2b", errors.get("f2b"));
            throw vex;
        }

        context.assertIsSatisfied();
    }

    /**
     * Tests with both validators failing.
     */

    @Test(expected = ValidationException.class)
    public void testValidateBothFail()
    {
        final ValidationException v1ex = new ValidationException();
        v1ex.addError("f1a", "m1a");
        v1ex.addError("f1b", "m1b");
        final ValidationException v2ex = new ValidationException();
        v2ex.addError("f2a", "m2a");
        v2ex.addError("f2b", "m2b");

        context.checking(new Expectations()
        {
            {
                oneOf(validator1).validate(settings, principal);
                will(throwException(v1ex));
                oneOf(validator2).validate(settings, principal);
                will(throwException(v2ex));
            }
        });

        // intercept the ValidationException to inspect it - insure all error messages present
        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException vex)
        {
            Map<String, String> errors = vex.getErrors();
            assertEquals(4, errors.size());
            assertEquals("m1a", errors.get("f1a"));
            assertEquals("m1b", errors.get("f1b"));
            assertEquals("m2a", errors.get("f2a"));
            assertEquals("m2b", errors.get("f2b"));
            throw vex;
        }

        context.assertIsSatisfied();
    }
}
