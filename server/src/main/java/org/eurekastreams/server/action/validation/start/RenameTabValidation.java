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

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.start.RenameTabRequest;
import org.eurekastreams.server.domain.TabTemplate;

/**
 * Validation for RenameTabValidation Execution.
 * 
 */
public class RenameTabValidation implements ValidationStrategy<ActionContext>
{

    /**
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @throws ValidationException
     *             if inputs don't meet validation standards.
     */
    @Override
    public void validate(final ActionContext inActionContext) throws ValidationException
    {
        if (((RenameTabRequest) inActionContext.getParams()).getTabName().length() > TabTemplate.MAX_TAB_NAME_LENGTH)
        {
            throw new ValidationException(TabTemplate.MAX_TAB_NAME_MESSAGE);
        }
    }

}
