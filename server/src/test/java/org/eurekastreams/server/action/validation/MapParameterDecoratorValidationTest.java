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

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.service.actions.strategies.MapParameterValidatorDecorator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for MapParameterDecoratorValidation class.
 * 
 */
public class MapParameterDecoratorValidationTest
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
     * {@link MapParameterDecoratorValidation}.
     */
    private MapParameterValidatorDecorator validator = context.mock(MapParameterValidatorDecorator.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * System under test.
     */
    private MapParameterDecoratorValidation sut = new MapParameterDecoratorValidation(validator);

    /**
     * Test.
     */
    @Test
    public void testValidate()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(new HashMap()));

                oneOf(validator).validate(with(any(Map.class)), with(any(Map.class)));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();

    }
}
