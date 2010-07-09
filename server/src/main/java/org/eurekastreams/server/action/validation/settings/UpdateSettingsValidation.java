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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;

/**
 * Validates settings to be saved using a list of strategies.
 */
public class UpdateSettingsValidation implements ValidationStrategy<PrincipalActionContext>
{
    /** Validation strategies. */
    private List<SettingsValidator> validators;

    /**
     * Constructor.
     *
     * @param inValidators
     *            Validation strategies.
     */
    public UpdateSettingsValidation(final List<SettingsValidator> inValidators)
    {
        validators = inValidators;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final PrincipalActionContext inActionContext) throws ValidationException
    {
        ValidationException vex = new ValidationException();
        for (SettingsValidator validator : validators)
        {
            try
            {
                validator.validate((Map<String, Object>) inActionContext.getParams(), inActionContext.getPrincipal());
            }
            catch (ValidationException ex)
            {
                for (Entry<String, String> error : ex.getErrors().entrySet())
                {
                    vex.addError(error.getKey(), error.getValue());
                }
            }
        }
        if (!vex.getErrors().isEmpty())
        {
            throw vex;
        }
    }
}
