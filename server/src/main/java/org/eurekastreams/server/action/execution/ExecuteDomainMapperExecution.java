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
import java.util.ArrayList;
import java.util.Arrays;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
* Executes a configured {@link DomainMapper} with provided params.
*
*/
public class ExecuteDomainMapperExecution implements ExecutionStrategy<ActionContext>
{
    /**
* {@link DomainMapper}.
*/
    private DomainMapper<Serializable, Serializable> domainMapper;

    /**
* Flag indicating if param should be put in list before calling domain mapper.
*/
    private boolean putParamInList;

    /**
* Constructor.
*
* @param inDomainMapper
* {@link DomainMapper}.
* @param inPutParamInList
* flag to indicate if parameter should be inserted into a list before being sent to DomainMapper.
*/
    public ExecuteDomainMapperExecution(final DomainMapper<Serializable, Serializable> inDomainMapper,
            final boolean inPutParamInList)
    {
        domainMapper = inDomainMapper;
        putParamInList = inPutParamInList;
    }

    /**
* Executes a configured {@link DomainMapper} with provided params.
*
* @param inActionContext
* {@link ActionContext}.
* @return {@link DomainMapper} results.
*/
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {

        return putParamInList ? domainMapper.execute(new ArrayList<Serializable>(Arrays.asList(inActionContext
                .getParams()))) : domainMapper.execute(inActionContext.getParams());
    }

}