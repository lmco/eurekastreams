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
package org.eurekastreams.server.action.request.transformer;

import org.eurekastreams.commons.actions.context.ActionContext;

/**
 * Transformer that converts parameter to String.
 * 
 */
public class ToStringTransformer implements RequestTransformer
{

    /**
     * Call toString() on parameter object and return.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return toString() value of parameter object.
     * 
     */
    @Override
    public String transform(final ActionContext inActionContext)
    {
        return inActionContext.getParams().toString();
    }

}
