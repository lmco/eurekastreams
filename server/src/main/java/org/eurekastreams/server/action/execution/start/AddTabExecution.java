/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.annotations.RequiresCredentials;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Creates and returns a new Tab, with the name provided by parameter.
 */
@RequiresCredentials
public class AddTabExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(AddTabExecution.class);

    /**
     * The DataMapper that lets us build a new tab.
     */
    private PersonMapper personMapper = null;

    /**
     * The TabMapper that lets us query for the newly created tab.
     */
    private TabMapper tabMapper = null;

    /**
     * Domain mapper to delete keys.
     */
    private DomainMapper<Set<String>, Boolean> deleteKeysMapper;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            for looking up the Person who will get the new tab
     * @param inTabMapper
     *            the TabMapper to insert against
     * @param inDeleteKeysMapper
     *            mapper to delete cache keys.
     */
    public AddTabExecution(final PersonMapper inPersonMapper, final TabMapper inTabMapper,
            final DomainMapper<Set<String>, Boolean> inDeleteKeysMapper)
    {
        personMapper = inPersonMapper;
        tabMapper = inTabMapper;
        deleteKeysMapper = inDeleteKeysMapper;
    }

    /**
     * Create and return a new Tab.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return the new tab
     * @throws ExecutionException
     *             can result from bad arguments, the user not being logged in, or not finding the user in the database
     */
    public final Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        String tabName = (String) inActionContext.getParams();

        Tab tab = new Tab(tabName, Layout.THREECOLUMN);

        Person person = personMapper.findByAccountId(inActionContext.getPrincipal().getAccountId());

        person.addTab(tab, TabGroupType.START);

        // because the caller relies on tabIndex being set properly, we have to
        // flush, then clear the entity manager so we can reget the updated
        // Tab's new tabIndex
        personMapper.flush();
        personMapper.clear();

        deleteKeysMapper.execute(Collections.singleton(CacheKeys.PERSON_PAGE_PROPERTIES_BY_ID + person.getId()));

        if (log.isDebugEnabled())
        {
            log.debug("Saved tab for " + person.getDisplayName());
        }

        return tabMapper.findById(tab.getId());
    }
}
