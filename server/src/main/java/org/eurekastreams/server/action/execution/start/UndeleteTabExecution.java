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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabGroupMapper;
import org.eurekastreams.server.persistence.exceptions.TabUndeletionException;

/**
 * Undeletes a Tab, with the ID provided by parameter.
 */
public class UndeleteTabExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private static Log logger = LogFactory.make();

    /**
     * The TabGroupMapper that deletes the Tab.
     */
    private TabGroupMapper tabGroupMapper = null;

    /**
     * Constructor.
     * 
     * @param inTabGroupMapper
     *            the TabGroupMapper to undelete from
     */
    public UndeleteTabExecution(final TabGroupMapper inTabGroupMapper)
    {
        tabGroupMapper = inTabGroupMapper;
    }

    /**
     * Deletes a Tab given the ID.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return Tab object that was undeleted.
     */
    @Override
    public Tab execute(final ActionContext inActionContext)
    {
        try
        {
            Long tabId = (Long) inActionContext.getParams();
            logger.debug("Undelete tabId = " + tabId);
            return tabGroupMapper.undeleteTab(tabId);
        }
        catch (TabUndeletionException tue)
        {
            throw new ExecutionException(tue);
        }
    }

}
