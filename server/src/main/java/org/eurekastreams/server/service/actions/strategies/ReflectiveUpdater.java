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
package org.eurekastreams.server.service.actions.strategies;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.commons.exceptions.ValidationException;

/**
 * uses reflection to update an object.
 */
public class ReflectiveUpdater implements UpdaterStrategy
{

    /**
     * public constructor.
     * 
     */
    public ReflectiveUpdater()
    {

    }

    /**
     * suppressing warnings here because not using generics: this is class-non-specific code.
     * 
     * @param instance
     *            the object on which to set properties
     * @param properties
     *            properties to set on the instance.
     */
    public void setProperties(final Object instance, final Map<String, Serializable> properties)
    {

        PropertyDescriptor[] descriptors;
        try
        {
            descriptors = Introspector.getBeanInfo(instance.getClass()).getPropertyDescriptors();
        }
        catch (IntrospectionException ie)
        {
            // (from the javadocs)
            // Typical causes of this exception include
            // not being able to map a string class name to a Class object,
            // not being able to resolve a string method name,
            // or specifying a method name that has the wrong type signature for its intended use
            String message = "Couldn't get bean info for " + instance.getClass();
            throw new RuntimeException(message, ie);
        }

        // if there are any cast/parse exceptions, add them to this
        ValidationException validationException = new ValidationException();

        for (PropertyDescriptor property : descriptors)
        {
            // some properties might not be passed in
            String propertyName = property.getName();
            if (!properties.containsKey(propertyName))
            {
                continue;
            }

            // some properties don't have setters
            Method setter = property.getWriteMethod();
            if (setter == null)
            {
                continue;
            }

            Serializable valueObj = properties.get(propertyName);

            // catch invocation-level exceptions at the same level
            // so we only need to do exception handling once
            try
            {
                // now you have a property, a setter, and a new value. Set it!

                // to add property types, put another block here for that type
                // cast or parse exceptions are added to the ValidationException
                // and returned as part of the validation result set

                // the reason you don't just call setter.invoke() is that
                // when the map comes in as a String/String map from a form,
                // the strings will need to be parsed before calling the setter
                // if the property type is a Long/Int/Date/etc

                if (property.getPropertyType().equals(String.class))
                {
                    String value = (String) valueObj;

                    // if empty string, should set to null for database
                    // (optional=true)
                    if ("".equals(value))
                    {
                        value = null;
                    }
                    setter.invoke(instance, value);
                    continue;
                }

                if (property.getPropertyType().equals(List.class))
                {
                    setter.invoke(instance, (List) valueObj);
                    continue;
                }
                if (property.getPropertyType().equals(HashMap.class))
                {
                    setter.invoke(instance, (HashMap) valueObj);
                    continue;
                }
                if (property.getPropertyType().equals(Set.class))
                {
                    setter.invoke(instance, (Set) valueObj);
                    continue;
                }
                if (property.getPropertyType().equals(Organization.class))
                {
                    setter.invoke(instance, (Organization) valueObj);
                    continue;
                }
                if (property.getPropertyType().equals(boolean.class)
                        || property.getPropertyType().equals(Boolean.class))
                {
                    setter.invoke(instance, ((Boolean) valueObj).booleanValue());
                    continue;
                }
                if (property.getPropertyType().equals(Integer.class) || property.getPropertyType().equals(int.class))
                {
                    setter.invoke(instance, (Integer) valueObj);
                    continue;
                }
                if (property.getPropertyType().equals(float.class))
                {
                    setter.invoke(instance, ((Float) valueObj).floatValue());
                    continue;
                }
                if (property.getPropertyType().equals(Date.class))
                {
                    setter.invoke(instance, (Date) valueObj);
                    continue;
                }
                // TODO add more property types here
                // once our domain classes have more than just strings.

                validationException.addError(propertyName, "Type not found.");

                // TODO some of these exceptions I either can't get to
                // (because they're programmatically prevented above)
                // or I just plain don't know how to cause. These should be tested as well
            }
            catch (IllegalArgumentException e)
            {
                validationException.addError(propertyName, valueObj + " had an illegal argument");
            }
            catch (IllegalAccessException e)
            {
                validationException.addError(propertyName, valueObj + " couldn't be accessed");
            }
            catch (InvocationTargetException e)
            {
                validationException.addError(propertyName, valueObj + " couldn't be invoked");
            }
        }

        // hibernate would throw an exception on writing,
        // but if we do it manually we can re-wrap hibernate's validation info
        // in a more concise and gwt-friendly way
        ClassValidator validator = new ClassValidator(instance.getClass());
        InvalidValue[] invalidValues = validator.getInvalidValues(instance);

        for (InvalidValue invalidValue : invalidValues)
        {
            validationException.addError(invalidValue.getPropertyName(), invalidValue.getMessage());
        }

        // throw if we had any parse errors or constraints errors
        if (validationException.getErrors().size() > 0)
        {
            throw validationException;
        }
    }
}
