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
package org.eurekastreams.server.action.validation.start;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.start.GadgetUserPrefActionRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for UpdateGadgetUserPrefByIdValidation class.
 * 
 */
public class UpdateGadgetUserPrefByIdValidationTest
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
     * Default value for user preferences to use during testing.
     */
    private static final String USER_PREFS = "{userPref1:'value1',userPref2:'value2'}";

    /**
     * Value for user preferences to use during testing JSON validation.
     */
    private static final String BAD_USER_PREFS = "{userPref1:value1,userPref2:value2}";

    /**
     * System under test.
     */
    private UpdateGadgetUserPrefByIdValidation sut = new UpdateGadgetUserPrefByIdValidation();

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link GadgetUserPrefActionRequest}.
     */
    private GadgetUserPrefActionRequest request = context.mock(GadgetUserPrefActionRequest.class);

    /**
     * Test.
     */
    @Test
    public void testValidatePassZeroLength()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                oneOf(request).getGadgetUserPref();
                will(returnValue(""));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testValidatePass()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getGadgetUserPref();
                will(returnValue(USER_PREFS));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();

    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testValidateFail()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getGadgetUserPref();
                will(returnValue(BAD_USER_PREFS));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();

    }
}
