/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.testing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.junit.Ignore;

/**
 * Convenience routines for creating action contexts for testing execution strategies. For ease of use does not handle
 * all features or cases, but should handle most.
 */
@Ignore
public final class TestContextCreator
{
    /**
     * Constructor to block instantiation.
     */
    private TestContextCreator()
    {
    }

    /**
     * Creates a TaskHandlerActionContext<AsyncActionContext> - used for actions invoked asynchronously which can queue
     * activities.
     *
     * @param params
     *            Action parameters.
     * @return Context.
     */
    public static TaskHandlerActionContext<ActionContext> createTaskHandlerAsyncContext(final Serializable params)
    {
        return new TaskHandlerActionContext<ActionContext>(new AsyncActionContext(params),
                new ArrayList<UserActionRequest>());
    }

    /**
     * Creates a TaskHandlerActionContext<PrincipalActionContext> - used for actions invoked directly by user action
     * which can queue activities.
     *
     * @param params
     *            Action parameters.
     * @param userAccountId
     *            User's account ID.
     * @param userId
     *            User's person ID.
     * @return Context.
     */
    public static TaskHandlerActionContext<PrincipalActionContext> createTaskHandlerContextWithPrincipal(
            final Serializable params, final String userAccountId, final long userId)
    {
        final Principal principal = createPrincipal(userAccountId, userId);
        return createTaskHandlerContextWithPrincipal(params, principal);
    }

    /**
     * Creates a TaskHandlerActionContext<PrincipalActionContext> - used for actions invoked directly by user action
     * which can queue activities.
     *
     * @param params
     *            Action parameters.
     * @param principal
     *            Principal for user requesting action.
     * @return Context.
     */
    public static TaskHandlerActionContext<PrincipalActionContext> createTaskHandlerContextWithPrincipal(
            final Serializable params, final Principal principal)
    {
        return new TaskHandlerActionContext<PrincipalActionContext>(createPrincipalActionContext(params, principal),
                new ArrayList<UserActionRequest>());
    }

    /**
     * Creates a PrincipalActionContext - used for actions invoked directly by user action which do not queue
     * activities.
     *
     * @param params
     *            Action parameters.
     * @param userAccountId
     *            User's account ID.
     * @param userId
     *            User's person ID.
     * @return Context.
     */
    public static PrincipalActionContext createPrincipalActionContext(final Serializable params,
            final String userAccountId, final long userId)
    {
        final Principal principal = createPrincipal(userAccountId, userId);
        return createPrincipalActionContext(params, principal);
    }

    /**
     * Creates a PrincipalActionContext - used for actions invoked directly by user action which do not queue
     * activities.
     *
     * @param params
     *            Action parameters.
     * @param principal
     *            Principal for user requesting action.
     * @return Context.
     */
    public static PrincipalActionContext createPrincipalActionContext(final Serializable params,
            final Principal principal)
    {
        return new PrincipalActionContext()
        {
            /** Fingerprint. */
            private static final long serialVersionUID = 8084060031869042700L;

            @Override
            public Map<String, Object> getState()
            {
                return null;
            }

            @Override
            public Serializable getParams()
            {
                return params;
            }

            @Override
            public String getActionId()
            {
                return null;
            }

            @Override
            public void setActionId(final String inActionId)
            {
            }

            @Override
            public Principal getPrincipal()
            {
                return principal;
            }
        };
    }

    /**
     * Creates a principal.
     *
     * @param userAccountId
     *            User's account ID.
     * @param userId
     *            User's person ID.
     * @return Principal.
     */
    public static Principal createPrincipal(final String userAccountId, final long userId)
    {
        return createPrincipal(userAccountId, userId, null);
    }

    /**
     * Creates a principal.
     * 
     * @param userAccountId
     *            User's account ID.
     * @param userId
     *            User's person ID.
     * @param openSocialId
     *            User's OpenSocial ID.
     * @return Principal.
     */
    public static Principal createPrincipal(final String userAccountId, final long userId, final String openSocialId)
    {
        return new Principal()
        {
            /** Fingerprint */
            private static final long serialVersionUID = -6797414788351285562L;

            @Override
            public String getAccountId()
            {
                return userAccountId;
            }

            @Override
            public Long getId()
            {
                return userId;
            }

            @Override
            public String getOpenSocialId()
            {
                return openSocialId;
            }
        };
    }
}
