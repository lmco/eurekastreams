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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.start.RenameTabRequest;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabMapper;

/**
 * Change name of tab.
 * 
 */
public class RenameTabExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Mapper needed to save the change to a Tab.
     */
    private TabMapper tabMapper = null;

    /**
     * Constructor.
     * 
     * @param mapper
     *            mapper for saving changes.
     */
    public RenameTabExecution(final TabMapper mapper)
    {
        tabMapper = mapper;
    }

    /**
     * Change the tab's title in the database.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return True if successful.
     */
    @Override
    public Boolean execute(final ActionContext inActionContext)
    {
        RenameTabRequest requestParams = (RenameTabRequest) inActionContext.getParams();

        Tab tab = tabMapper.findById(requestParams.getTabId());

        tab.setTabName(requestParams.getTabName());

        tabMapper.flush();

        return Boolean.TRUE;
    }

}
