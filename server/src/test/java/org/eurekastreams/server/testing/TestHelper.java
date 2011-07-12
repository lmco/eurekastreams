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

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;

/**
 * Miscellaneous methods for streamlining unit tests.
 */
@Ignore
public final class TestHelper
{
    /**
     * Forbid instantiation.
     */
    private TestHelper()
    {
    }

    /**
     * Insures that a given collection matches another where order does not matter. Does NOT handle duplicate elements.
     *
     * @param <T>
     *            Type of elements.
     * @param actual
     *            Collection to test.
     * @param expected
     *            Expected items.
     * @return If matches.
     */
    public static <T> boolean containsExactly(final Collection<T> actual, final Collection<T> expected)
    {
        assertNotNull(actual);
        return containsExactly(actual, expected, false);
    }

    /**
     * Insures that a given collection matches another where order does not matter. Does NOT handle duplicate elements.
     *
     * @param <T>
     *            Type of elements.
     * @param actual
     *            Collection to test.
     * @param expected
     *            Expected items.
     * @param nullAsEmpty
     *            If a null list should be treated like an empty list (applies to actual only; expected must not be
     *            null).
     * @return If matches.
     */
    public static <T> boolean containsExactly(final Collection<T> actual, final Collection<T> expected,
            final boolean nullAsEmpty)
    {
        assertNotNull(expected);

        if (actual == null)
        {
            return nullAsEmpty ? expected.isEmpty() : false;
        }

        if (actual.isEmpty() || expected.isEmpty())
        {
            return actual.isEmpty() != expected.isEmpty();
        }

        // compare order-insensitively
        Set<T> actualAsSet = actual instanceof Set ? (Set<T>) actual : new HashSet<T>(actual);
        Set<T> expectedAsSet = expected instanceof Set ? (Set<T>) expected : new HashSet<T>(expected);

        return expectedAsSet.equals(actualAsSet);
    }

    /**
     * Insures that a given collection matches another where order does not matter. Does NOT handle duplicate elements.
     *
     * @param <T>
     *            Type of elements.
     * @param actual
     *            Collection to test.
     * @param expected
     *            Expected items.
     * @return If matches.
     */
    public static <T> boolean containsExactly(final Collection<T> actual, final T... expected)
    {
        assertNotNull(actual);
        return containsExactly(actual, expected, false);
    }

    /**
     * Insures that a given collection matches another where order does not matter. Does NOT handle duplicate elements.
     *
     * @param <T>
     *            Type of elements.
     * @param actual
     *            Collection to test.
     * @param expected
     *            Expected items.
     * @param nullAsEmpty
     *            If a null list should be treated like an empty list (applies to actual only; expected must not be
     *            null).
     * @return If matches.
     */
    public static <T> boolean containsExactly(final Collection<T> actual, final T[] expected, final boolean nullAsEmpty)
    {
        assertNotNull(expected);

        if (actual == null)
        {
            return nullAsEmpty ? expected.length == 0 : false;
        }

        if (actual.isEmpty() || expected.length == 0)
        {
            return actual.isEmpty() != (expected.length == 0);
        }

        // compare order-insensitively
        Set<T> actualAsSet = actual instanceof Set ? (Set<T>) actual : new HashSet<T>(actual);
        Set<T> expectedAsSet = new HashSet<T>();
        for (int i = 0; i < expected.length; i++)
        {
            expectedAsSet.add(expected[i]);
        }

        return expectedAsSet.equals(actualAsSet);
    }

    /**
     * Sets the value of an object's private field.
     *
     * @param target
     *            Target object.
     * @param fieldName
     *            Name of field.
     * @param value
     *            Value to set.
     * @throws IllegalArgumentException
     *             Shouldn't.
     * @throws IllegalAccessException
     *             Shouldn't.
     * @throws NoSuchFieldException
     *             Field not found - calling unit test is probably out of date.
     */
    public static void setPrivateField(final Object target, final String fieldName, final Object value)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException
    {
        final Class< ? extends Object> type = target.getClass();
        for (Field field : type.getDeclaredFields())
        {
            if (field.getName().equals(fieldName))
            {
                boolean oldAccess = field.isAccessible();
                field.setAccessible(true);
                field.set(target, value);
                if (!oldAccess)
                {
                    field.setAccessible(false);
                }
                return;
            }
        }
        throw new NoSuchFieldException(type.getName() + " has no field named " + fieldName);
    }
}
