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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;

/**
 * This class is the execution strategy for the KeepSessionAlive action.  This action
 * is an endpoint that is pinged by the client to keep the current session alive.
 *
 */
public class KeepSessionAliveExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local log instance.
     */
    private final Log log = LogFactory.make();

    /**
     * {@inheritDoc}.
     */
    @Override
    public Boolean execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        if (log.isInfoEnabled())
        {
            log.info("Session keep-alive request");
        }
        return true;
    }

}
