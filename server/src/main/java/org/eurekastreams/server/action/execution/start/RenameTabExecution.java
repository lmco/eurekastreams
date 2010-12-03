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
package org.eurekastreams.server.action.execution.start;

import java.util.Collections;
import java.util.Set;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.start.RenameTabRequest;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Change name of tab.
 * 
 */
public class RenameTabExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper needed to save the change to a Tab.
     */
    private TabMapper tabMapper = null;

    /**
     * Domain mapper to delete keys.
     */
    private DomainMapper<Set<String>, Boolean> deleteKeysMapper;

    /**
     * Constructor.
     * 
     * @param mapper
     *            mapper for saving changes.
     * @param inDeleteKeysMapper
     *            mapper to delete cache keys.
     */
    public RenameTabExecution(final TabMapper mapper, final DomainMapper<Set<String>, Boolean> inDeleteKeysMapper)
    {
        tabMapper = mapper;
        deleteKeysMapper = inDeleteKeysMapper;
    }

    /**
     * Change the tab's title in the database.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return True if successful.
     */
    @Override
    public Boolean execute(final PrincipalActionContext inActionContext)
    {
        RenameTabRequest requestParams = (RenameTabRequest) inActionContext.getParams();

        Tab tab = tabMapper.findById(requestParams.getTabId());

        tab.setTabName(requestParams.getTabName());

        tabMapper.flush();

        deleteKeysMapper.execute(Collections.singleton(CacheKeys.PERSON_PAGE_PROPERTIES_BY_ID
                + inActionContext.getPrincipal().getId()));

        return Boolean.TRUE;
    }

}
