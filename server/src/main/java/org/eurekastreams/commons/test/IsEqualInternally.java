/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * A matcher to use in junit tests to determine if two objects contain the same state.
 * 
 * This is useful for comparing objects for value equality instead of reference equality when the class does not have an
 * appropriate implementation of equals(), such as ClickListeners and AsyncCallbacks.
 * 
 * @param <T>
 *            Type to compare.
 */
public class IsEqualInternally<T> extends BaseMatcher<T>
{
    /** Value to compare result against. */
    private Object compareAgainst;

    /** Logger. */
    private static Log log = LogFactory.getLog(IsEqualInternally.class);

    /**
     * Create matcher with a given value.
     * 
     * @param inCompareAgainst
     *            Value to compare result against.
     */
    public IsEqualInternally(final T inCompareAgainst)
    {
        compareAgainst = inCompareAgainst;
    }

    /**
     * Compares the values.
     * 
     * @param item
     *            Item to compare.
     * @return If objects match.
     */
    public boolean matches(final Object item)
    {
        return areEqualInternally(item, compareAgainst);
    }

    /**
     * Determines if two objects are equal internally, i.e. based on values of all fields.
     * 
     * @param object1
     *            First object.
     * @param object2
     *            Second object.
     * @return If objects match.
     */
    public static boolean areEqualInternally(final Object object1, final Object object2)
    {
        if (object1 == null)
        {
            return object2 == null;
        }
        else if (object2 == null)
        {
            return false;
        }

        // both must be the same type (not just compatible types)
        Class type = object1.getClass();
        if (!object2.getClass().equals(type))
        {
            if (log.isDebugEnabled())
            {
                log.debug("equalInternally:  types do not match. '" + type.getName() + "' vs. '"
                        + object2.getClass().getName() + "'");
            }
            return false;
        }

        // compare values of all fields
        for (Field field : type.getDeclaredFields())
        {
            try
            {
                field.setAccessible(true);
                Object value1 = field.get(object1);
                Object value2 = field.get(object2);
                if (!areEqual(value1, value2))
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("equalInternally:  field '" + field.getName() + "' doesn't match.  '" + value1
                                + "' vs. '" + value2 + "'");
                    }
                    return false;
                }
            }
            catch (Throwable ex)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("equalInternally:  exception:  " + ex);
                }
                return false;
            }
        }

        return true;
    }

    /**
     * Describes the object. Needed for a matcher.
     * 
     * @param description
     *            THe description to add to.
     */
    public void describeTo(final Description description)
    {
        description.appendValue(compareAgainst);
    }

    /**
     * Test whether the two objects are equal.
     * 
     * @param obj1
     *            obj1
     * @param obj2
     *            obj2
     * @return whether the two objects are effectively equal
     */
    private static boolean areEqual(final Object obj1, final Object obj2)
    {
        if (obj1 == null)
        {
            if (obj2 == null)
            {
                return true;
            }
            return false;
        }

        if (obj1 instanceof Object[] && obj2 instanceof Object[])
        {
            Object[] l1 = (Object[]) obj1;
            Object[] l2 = (Object[]) obj2;
            if (l1.length != l2.length)
            {
                return false;
            }
            for (int i = 0; i < l1.length; i++)
            {
                if (!areEqual(l1[i], l2[i]))
                {
                    return false;
                }
            }
            return true;
        }
        return obj1.equals(obj2);
    }

    /**
     * Creates an instance of the matcher.
     * 
     * @param <T>
     *            Type of object to match.
     * @param value
     *            Specific value to check against.
     * @return Matcher.
     */
    public static <T> Matcher<T> equalInternally(final T value)
    {
        return new IsEqualInternally<T>(value);
    }

}
