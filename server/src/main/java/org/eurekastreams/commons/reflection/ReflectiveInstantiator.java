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
package org.eurekastreams.commons.reflection;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class to aid in object instantiation via reflection.
 */
public class ReflectiveInstantiator
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(ReflectiveInstantiator.class);

    /**
     * Instantiate an object of the input class type with the empty constructor.
     * Private constructors are handled.
     *
     * @param objType
     *            the type of class to instantiate.
     *
     * @return an object of the input class type.
     */
    public Object instantiateObject(final Class< ? > objType)
    {
        Constructor< ? > emptyConstructor = null;
        for (Constructor< ? > constructor : objType.getDeclaredConstructors())
        {
            if (constructor.getParameterTypes().length == 0)
            {
                emptyConstructor = constructor;
                break;
            }
        }
        if (emptyConstructor == null)
        {
            String message = "Cannot find empty constructor for " + objType.getName();
            log.error(message);
            throw new RuntimeException(message);
        }

        // set it accessible, just in case
        emptyConstructor.setAccessible(true);

        Object obj = null;
        try
        {
            obj = emptyConstructor.newInstance(new Object[0]);
        }
        catch (Exception e)
        {
            String message = "Couldn't instantiate: " + objType.getName();
            log.error(message, e);
            throw new RuntimeException(message);
        }

        return obj;
    }
}
