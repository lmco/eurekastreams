/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;

/**
 * Executes a configured {@link DomainMapper} with provided params.
 */
public class ExecuteDomainMapperExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * {@link DomainMapper}.
     */
    private final DomainMapper<Serializable, Serializable> domainMapper;

    /** Strategy to supply mapper parameters from the action context. */
    private final Transformer<ActionContext, Serializable> parameterSupplier;

    /**
     * Constructor.
     *
     * @param inParameterSupplier
     *            Strategy to supply mapper parameters from the action context.
     * @param inDomainMapper
     *            {@link DomainMapper}.
     */
    public ExecuteDomainMapperExecution(final Transformer<ActionContext, Serializable> inParameterSupplier,
            final DomainMapper<Serializable, Serializable> inDomainMapper)
    {
        parameterSupplier = inParameterSupplier;
        domainMapper = inDomainMapper;
    }

    /**
     * Executes a configured {@link DomainMapper} with provided params.
     *
     * @param inActionContext
     *            {@link ActionContext}.
     * @return {@link DomainMapper} results.
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        Serializable param = parameterSupplier.transform(inActionContext);
        return domainMapper.execute(param);
    }
}
