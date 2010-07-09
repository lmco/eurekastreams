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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.start.SetTabLayoutRequest;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.mappers.db.UpdateGadgetsWithNewTabLayoutMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdateGadgetsWithNewTabLayoutRequest;

/**
 * Lets the client change a PortalPage's layout.
 */
public class SetTabLayoutExecution implements ExecutionStrategy<PrincipalActionContext>
{

    /**
     * Mapper used to look up the tab we're modifying.
     */
    private final TabMapper tabMapper;

    /**
     * Mapper that performs the updates on gadgets as a results of the layout change.
     */
    private final UpdateGadgetsWithNewTabLayoutMapper updateMapper;

    /**
     * Constructor.
     *
     * @param mapper
     *            injecting the TabMapper
     * @param inUpdateMapper
     *            mapper that performs the updates on gadgets as a result of the layout change.
     */
    public SetTabLayoutExecution(final TabMapper mapper, final UpdateGadgetsWithNewTabLayoutMapper inUpdateMapper)
    {
        tabMapper = mapper;
        updateMapper = inUpdateMapper;
    }

    /**
     * {@inheritDoc}.
     *
     * Perform the layout change. If the new layout contains less columns than the old layout, move any gadgets
     * that are in the truncated columns to the last column in the new layout.
     *
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        SetTabLayoutRequest currentRequest = (SetTabLayoutRequest) inActionContext.getParams();

        Tab tab = tabMapper.findById(currentRequest.getTabId());
        Layout oldTabLayout = tab.getTabLayout();
        tab.setTabLayout(currentRequest.getLayout());

        tabMapper.flush();

        // Call mapper to update gadgets for a shrinking layout.
        if (oldTabLayout.getNumberOfZones() > currentRequest.getLayout().getNumberOfZones())
        {
            updateMapper.execute(new UpdateGadgetsWithNewTabLayoutRequest(tab.getTemplate().getId(), currentRequest
                    .getLayout()));
        }

        // Need to retrieve the updated tab and touch the gadgets to return to the caller.
        Tab updatedTab = tabMapper.findById(currentRequest.getTabId());

        return updatedTab;
    }
}
