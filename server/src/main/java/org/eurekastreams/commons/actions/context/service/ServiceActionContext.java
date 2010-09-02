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
package org.eurekastreams.commons.actions.context.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;

/**
 * ServiceAction implementation of the ActionContext this class implements ActionContext, but also adds the Principal as
 * well.
 * 
 */
public class ServiceActionContext implements PrincipalActionContext
{
    /**
     * serialized id.
     */
    private static final long serialVersionUID = -3624676030057226351L;

    /**
     * Parameter object for the current ServiceActionContext.
     */
    private final Serializable params;

    /**
     * Instance of the Principal object for the current context.
     */
    private final Principal principal;

    /**
     * Instance of the state of the current execution of the action.
     */
    private Map<String, Object> state;

    /**
     * Action id.
     */
    private String actionId;

    /**
     * Constructor.
     * 
     * @param inParams
     *            - instance of the Params for the current action context.
     * @param inPrincipal
     *            - instance of the {@link Principal} for the current action context.
     */
    public ServiceActionContext(final Serializable inParams, final Principal inPrincipal)
    {
        params = inParams;
        principal = inPrincipal;
        state = new HashMap<String, Object>();
    }

    /**
     * {@inheritDoc}.
     */
    public Serializable getParams()
    {
        return params;
    }

    /**
     * {@inheritDoc}.
     */
    public Principal getPrincipal()
    {
        return principal;
    }

    /**
     * {@inheritDoc}.
     */
    public Map<String, Object> getState()
    {
        return state;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getActionId()
    {
        return actionId;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setActionId(final String inActionId)
    {
        actionId = inActionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return this.getClass().getSimpleName() + " actionId: ***" + actionId + "*** "
                + ((principal == null) ? "null" : principal.getAccountId()) + " | "
                + ((params == null) ? "null" : params.toString());
    }

}
