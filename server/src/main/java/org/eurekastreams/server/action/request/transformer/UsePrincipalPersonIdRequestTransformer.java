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
package org.eurekastreams.server.action.request.transformer;

import java.io.Serializable;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;

/**
 * Transformer which returns the person ID of the principal (current user) in place of the request parameters.
 */
public class UsePrincipalPersonIdRequestTransformer implements Transformer<PrincipalActionContext, Serializable>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable transform(final PrincipalActionContext inActionContext)
    {
        return inActionContext.getPrincipal().getId();
    }
}
