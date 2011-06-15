/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.validation.notification;

import java.util.Set;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;

/**
 * Insures only valid categories are disabled.
 */
public class DisableNotificationCategoryValidation implements ValidationStrategy<ActionContext>
{
    /** List of allowed notification categories. */
    private final Set<String> categories;

    /**
     * Constructor.
     * 
     * @param inCategories
     *            List of allowed notification categories.
     */
    public DisableNotificationCategoryValidation(final Set<String> inCategories)
    {
        categories = inCategories;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final ActionContext inActionContext) throws ValidationException
    {
        String category = (String) inActionContext.getParams();
        if (!categories.contains(category))
        {
            throw new ValidationException("Invalid notification category: " + category);
        }
    }
}
