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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.service.actions.strategies.MapParameterValidatorDecorator;

/**
 * Validation strategy that uses injected MapParameterValidatorDecorator to validate.
 * 
 */
public class MapParameterDecoratorValidation implements ValidationStrategy<ActionContext>
{
    /**
     * {@link MapParameterValidatorDecorator}.
     */
    private MapParameterValidatorDecorator validator;

    /**
     * Constructor.
     * 
     * @param inValidator
     *            the {@link MapParameterValidatorDecorator} to use.
     */
    public MapParameterDecoratorValidation(final MapParameterValidatorDecorator inValidator)
    {
        validator = inValidator;
    }

    /**
     * Validate parameters via injected {@link MapParameterValidatorDecorator}.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void validate(final ActionContext inActionContext)
    {
        validator.validate((Map<String, Serializable>) inActionContext.getParams(), new HashMap<String, String>());
    }

}
