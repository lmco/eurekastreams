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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.start.SetTabOrderRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Move one tab to a different spot in the list of tabs belonging to a page.
 */
public class SetTabOrderExecution implements ExecutionStrategy<PrincipalActionContext>
{

    /**
     * Mapper used to retrieve and save the page that holds the tabs.
     */
    private PersonMapper personMapper = null;

    /**
     * Domain mapper to delete keys.
     */
    private DomainMapper<Set<String>, Boolean> deleteKeysMapper;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            injecting the mapper
     * @param inDeleteKeysMapper
     *            mapper to delete cache keys.
     */
    public SetTabOrderExecution(final PersonMapper inPersonMapper,
            final DomainMapper<Set<String>, Boolean> inDeleteKeysMapper)
    {
        personMapper = inPersonMapper;
        deleteKeysMapper = inDeleteKeysMapper;
    }

    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        SetTabOrderRequest currentRequest = (SetTabOrderRequest) inActionContext.getParams();

        Person person = personMapper.findByAccountId(inActionContext.getPrincipal().getAccountId());

        List<Tab> tabs = person.getTabs(currentRequest.getTabType());

        // Find the tab to be moved
        int oldIndex = findTabIndex(tabs, currentRequest.getTabId());

        Tab movingTab = tabs.get(oldIndex);

        // move the tab
        tabs.remove(oldIndex);
        tabs.add(currentRequest.getNewIndex(), movingTab);

        deleteKeysMapper.execute(Collections.singleton(CacheKeys.PERSON_PAGE_PROPERTIES_BY_ID
                + inActionContext.getPrincipal().getId()));

        personMapper.flush();

        return Boolean.TRUE;
    }

    /**
     * Find the current location of the tab.
     * 
     * @param tabs
     *            the collection to search through
     * @param tabId
     *            the id to look for
     * @return the index of the tab or -1 if not found
     */
    private int findTabIndex(final List<Tab> tabs, final long tabId)
    {
        for (int i = 0; i < tabs.size(); i++)
        {
            if (tabs.get(i).getId() == tabId)
            {
                return i;
            }
        }

        return -1;
    }

}
