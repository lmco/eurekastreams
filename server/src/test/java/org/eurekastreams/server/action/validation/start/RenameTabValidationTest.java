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

import static org.junit.Assert.assertEquals;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.start.RenameTabRequest;
import org.eurekastreams.server.action.validation.ValidationTestHelper;
import org.eurekastreams.server.domain.TabTemplate;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for RenameTabValidation class.
 * 
 */
@SuppressWarnings("unchecked")
public class RenameTabValidationTest
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
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link RenameTabRequest}.
     */
    private RenameTabRequest renameRequestMock = context.mock(RenameTabRequest.class);

    /**
     * System under test.
     */
    private RenameTabValidation sut = new RenameTabValidation();

    /**
     * Test.
     */
    @Test
    public void testValidateSuccess()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(renameRequestMock));

                oneOf(renameRequestMock).getTabName();
                will(returnValue(ValidationTestHelper.generateString(TabTemplate.MAX_TAB_NAME_LENGTH)));

            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();

    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testValidateTabNameToLong()
    {

        context.checking(new Expectations()
        {
            {

                oneOf(actionContext).getParams();
                will(returnValue(renameRequestMock));

                oneOf(renameRequestMock).getTabName();
                will(returnValue(ValidationTestHelper.generateString(TabTemplate.MAX_TAB_NAME_LENGTH + 1)));

            }
        });

        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException ve)
        {
            context.assertIsSatisfied();
            assertEquals(ve.getMessage(), TabTemplate.MAX_TAB_NAME_MESSAGE);
            throw ve;
        }
    }
}
