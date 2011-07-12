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
 * @param <T>
 *            type of class to intercept.
 *
 */
public class AnonymousClassInterceptor<T> implements Action
{
    /** Index of parameter to intercept. 0-n = from start of list; negative is from end of list. */
    private int parameterIndex = -1;

    /** The intercepted object. */
    private T anonObject;

    /** Return value of the intercepted call. */
    private Object returnValue = null;

    /**
     * Default constructor; intercepts last parameter, returns null.
     */
    public AnonymousClassInterceptor()
    {
    }

    /**
     * Constructor; intercepts specified parameter, returns null.
     *
     * @param inParameterIndex
     *            Index of parameter to intercept: 0-n = from start of list; negative is from end of list.
     */
    public AnonymousClassInterceptor(final int inParameterIndex)
    {
        parameterIndex = inParameterIndex;
    }

    /**
     * Constructor; intercepts last parameter, returns specified value.
     *
     * @param inReturnValue
     *            Return value of the intercepted call.
     */
    public AnonymousClassInterceptor(final Object inReturnValue)
    {
        returnValue = inReturnValue;
    }

    /**
     * Constructor; intercepts specified parameter, returns specified value.
     *
     * @param inParameterIndex
     *            Index of parameter to intercept: 0-n = from start of list; negative is from end of list.
     * @param inReturnValue
     *            Return value of the intercepted call.
     */
    public AnonymousClassInterceptor(final int inParameterIndex, final Object inReturnValue)
    {
        parameterIndex = inParameterIndex;
        returnValue = inReturnValue;
    }

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
     * @throws Throwable
     *             an exception.
     * @return Configured return value of the method call (generally null).
     */
    @SuppressWarnings("unchecked")
    public Object invoke(final Invocation invocation) throws Throwable
    {
        int paramCount = invocation.getParameterCount();
        int zeroBasedIndex = parameterIndex >= 0 ? parameterIndex : paramCount + parameterIndex;
        if (zeroBasedIndex < 0 || zeroBasedIndex >= paramCount)
        {
            throw new Exception("Desired parameter index (" + parameterIndex
                    + ") is out of bounds for the call (with " + paramCount + " parameters).");
        }

        anonObject = (T) invocation.getParameter(zeroBasedIndex);
        return returnValue;
    }
}
