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
package org.eurekastreams.server.action.validation.stream;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.stream.Stream;

/**
 * Validates the action to modify a current users streams.
 */
public class ModifyStreamForCurrentUserValidation implements ValidationStrategy<PrincipalActionContext>
{
    /**
     * Validates modifying the current user's streams.
     * 
     * @param inActionContext
     *            the action context.
     * @throws ValidationException
     *             on validation error.
     */
    @Override
    public void validate(final PrincipalActionContext inActionContext) throws ValidationException
    {
        Stream stream = (Stream) inActionContext.getParams();

        if (stream.getName() == null || stream.getName().length() == 0)
        {
            throw new ValidationException("Stream must have a name.");
        }
    }

}
