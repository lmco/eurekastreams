/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

/**
 * Intercepts the entire parameter list of a method invocation. Similar to AnonymousClassInterceptor. Can be reused.
 */
public class ParamInterceptor implements Action
{
    /** Holds the parameters. */
    private Invocation invocation;

    /**
     * {@inheritDoc}
     */
    @Override
    public void describeTo(final Description description)
    {
        description.appendText("Intercepts the entire parameter list of a method invocation.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(final Invocation inInvocation) throws Throwable
    {
        invocation = inInvocation;
        return null;
    }

    /**
     * Returns the requested parameter.
     *
     * @param index
     *            Parameter position (0-based from start/left).
     * @return Parameter.
     */
    public Object getParam(final int index)
    {
        return invocation.getParameter(index);
    }
}
