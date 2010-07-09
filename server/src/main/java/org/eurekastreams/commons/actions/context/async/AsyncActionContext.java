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
package org.eurekastreams.commons.actions.context.async;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;

/**
 * ServiceAction implementation of the ActionContext this class implements ActionContext, but also adds the Principal as
 * well.
 *
 */
public class AsyncActionContext implements ActionContext
{

    /**
     * Serialization id.
     */
    private static final long serialVersionUID = 5174601781547895849L;

    /**
     * Parameters for this context.
     */
    private final Serializable params;

    /**
     * State to be shared amongst execution blocks within this action context.
     */
    private Map<String, Object> state;

    /**
     * Constructor.
     *
     * @param inParams
     *            - instance of the params to execute the action with.
     */
    public AsyncActionContext(final Serializable inParams)
    {
        params = inParams;
        state = new HashMap<String, Object>();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Serializable getParams()
    {
        return params;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Map<String, Object> getState()
    {
        return state;
    }
}
