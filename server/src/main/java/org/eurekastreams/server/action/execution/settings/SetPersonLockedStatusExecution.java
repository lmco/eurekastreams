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
package org.eurekastreams.server.action.execution.settings;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.server.action.request.SetPersonLockedStatusRequest;
import org.eurekastreams.server.persistence.mappers.db.SetPersonLockedStatus;

/**
 * Action for setting a person's account locked status.
 * 
 */
public class SetPersonLockedStatusExecution implements ExecutionStrategy<AsyncActionContext>
{
    /**
     * {@link SetPersonLockedStatus}.
     */
    private SetPersonLockedStatus setLockedStatusDAO;

    /**
     * Constructor.
     * 
     * @param inSetLockedStatusDAO
     *            {@link SetPersonLockedStatus}.
     */
    public SetPersonLockedStatusExecution(final SetPersonLockedStatus inSetLockedStatusDAO)
    {
        setLockedStatusDAO = inSetLockedStatusDAO;
    }

    /**
     * Set a person's locked status.
     * 
     * @param inActionContext
     *            The action context.
     * @return null.
     */
    @Override
    public Serializable execute(final AsyncActionContext inActionContext)
    {
        setLockedStatusDAO.execute((SetPersonLockedStatusRequest) inActionContext.getParams());
        return null;
    }

}
