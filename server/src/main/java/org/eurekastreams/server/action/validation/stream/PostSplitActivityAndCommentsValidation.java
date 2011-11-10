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
package org.eurekastreams.server.action.validation.stream;

import org.apache.commons.lang.StringUtils;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.stream.PostSplitActivityAndCommentsRequest;

/**
 * Validates PostSplitActivityAndCommentsRequest requests for PostSplitActivityAndCommentsExecution.
 */
public class PostSplitActivityAndCommentsValidation implements ValidationStrategy<ActionContext>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final ActionContext inActionContext) throws ValidationException
    {
        PostSplitActivityAndCommentsRequest request = (PostSplitActivityAndCommentsRequest) inActionContext
                .getParams();

        if (request == null)
        {
            throw new ValidationException("A request must be provided.");
        }

        if (StringUtils.isBlank(request.getText()))
        {
            throw new ValidationException("Request must provide text content.");
        }
    }
}
