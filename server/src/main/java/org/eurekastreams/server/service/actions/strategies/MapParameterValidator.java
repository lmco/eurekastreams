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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.exceptions.ValidationException;

/**
 * Validator to check for key/value sets in a map and throw ValidationException
 * upon fail.
 *
 */
public class MapParameterValidator implements MapParameterValidatorDecorator
{
    /**
     * key to look for in map.
     */
    private String key;
    
    /**
     * Class of value associated with key.
     */
    private Class< ? > clazz;
    
    /**
     * Message to display upon error.
     */
    private String message;
    
    /**
     * Decorated MapParameterValidatorDecorator.
     */
    private MapParameterValidatorDecorator decorated;
    
    /**
     * Constructor.
     * @param inKey key to look for in map.
     * @param inClazz Class of value associated with key.
     * @param inMessage Message to display upon error.
     */
    public MapParameterValidator(final String inKey, final Class< ? > inClazz, final String inMessage)
    {
       key = inKey;
       clazz = inClazz; 
       message = inMessage;
    }
    
    /**
     * Validates the key/value pair for this validator, if String, also checks for empty string.
     * 
     * @param map
     *            The map to check.
     * @param errors
     *            map of key, error messages sets from validation.
     */
    @SuppressWarnings("unchecked")
    public void validate(final Map<String, Serializable> map, final Map<String, String> errors)
    {
        if (!map.containsKey(key) || map.get(key) == null || !clazz.isAssignableFrom(map.get(key).getClass())
                || (map.get(key) instanceof String && ((String) map.get(key)).trim().isEmpty())
                || (List.class.isAssignableFrom(map.get(key).getClass()) && ((List) map.get(key)).size() == 0))
        {
            errors.put(key, message);            
        }
                       
        if (decorated != null)
        {
            decorated.validate(map, errors);
        }
        else if (!errors.isEmpty())
        {
            ValidationException ve = new ValidationException();
            for (String errorKey : errors.keySet())
            {
                ve.addError(errorKey, errors.get(errorKey));
            }
            throw ve;
        }        
    }

    /**
     * Setter for MapParameterValidatorDecorator to decorate.
     * @param inDecorated the decorated MapParameterValidatorDecorator.
     */
    public void setMapParameterValidatorDecorator(final MapParameterValidatorDecorator inDecorated)
    {
        decorated = inDecorated;
    }

}
