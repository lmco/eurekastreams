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
package org.eurekastreams.commons.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Simple matcher base class to let unit tests do custom parameter tests easily by deriving and implementing just a
 * single method. Treats null reference and cast exceptions as a non-match (returns false) so that users can simply
 * write expressions without having to check for nulls or correct types while getting the right comparison behavior.
 *
 * @param <T>
 *            Type of object to test.
 */
public abstract class EasyMatcher<T> extends BaseMatcher<T>
{
    /**
     * Typed method to determine if an object meets the criteria.
     *
     * @param testObject
     *            Object to test.
     * @return If the object meets the criteria.
     */
    protected abstract boolean isMatch(T testObject);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final Object inArg0)
    {
        try
        {
            return isMatch((T) inArg0);
        }
        catch (NullPointerException ex)
        {
            return false;
        }
        catch (ClassCastException ex)
        {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void describeTo(final Description inArg0)
    {
    }
}
