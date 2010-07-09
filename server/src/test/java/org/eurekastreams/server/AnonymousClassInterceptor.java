/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

/**
 * Intercepts an anonymous classes when passed to a 'will' statement.
 * 
 * @param <T> type of class to intercept.
 * 
 */
public class AnonymousClassInterceptor<T> implements Action
{
    /**
     * The intercepted object.
     */
    private T anonObject;

    /**
     * Description for jMock.
     * 
     * @param description
     *            the description.
     */
    public void describeTo(final Description description)
    {
        description.appendText("Intercepts an anon. class.");
    }

    /**
     * Get the intercepted object.
     * 
     * @return the intercepted object.
     */
    public T getObject()
    {
        return anonObject;
    }

    /**
     * Method invocation.
     * 
     * @param invocation
     *            the invocation.
     * @throws Throwable an
     *             exception.
     * @return null in this case.
     */
    @SuppressWarnings("unchecked")
    public Object invoke(final Invocation invocation) throws Throwable
    {
        anonObject = (T) invocation
                .getParameter(invocation.getParameterCount() - 1);
        return null;
    }
}
