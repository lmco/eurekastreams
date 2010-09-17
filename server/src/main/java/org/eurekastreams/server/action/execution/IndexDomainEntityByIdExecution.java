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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.IndexEntity;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Submit {@link DomainEntity} to search index.
 * 
 */
public class IndexDomainEntityByIdExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Object mapper.
     */
    private FindByIdMapper<DomainEntity> objectMapper;

    /**
     * {@link IndexEntity} mapper.
     */
    private IndexEntity<DomainEntity> objectIndexer;

    /**
     * Domain entity name used by {@link FindByIdMapper}.
     */
    private String domainEntityName;

    /**
     * Constructor.
     * 
     * @param inObjectMapper
     *            Activity entity mapper.
     * @param inObjectIndexer
     *            {@link IndexEntity} mapper.
     * @param inDomainEntityName
     *            Domain entity name used by {@link FindByIdMapper}.
     */
    public IndexDomainEntityByIdExecution(final FindByIdMapper<DomainEntity> inObjectMapper,
            final IndexEntity<DomainEntity> inObjectIndexer, final String inDomainEntityName)
    {
        objectMapper = inObjectMapper;
        objectIndexer = inObjectIndexer;
        domainEntityName = inDomainEntityName;
    }

    /**
     * Submit the object to search index if found in DB.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return null.
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        DomainEntity entity = objectMapper.execute(new FindByIdRequest(domainEntityName, (Long) inActionContext
                .getParams()));
        if (entity != null)
        {
            objectIndexer.execute(entity);
        }

        return null;
    }
}
