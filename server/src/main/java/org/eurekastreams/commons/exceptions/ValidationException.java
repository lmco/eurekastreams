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
package org.eurekastreams.commons.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * This is an exception that holds information about which fields in a single object failed validation.
 */
public class ValidationException extends RuntimeException
{

    /**
     * Serialization version id.
     */
    private static final long serialVersionUID = -1972954267138214118L;

    /**
     * this is the format to pass back: map of property->message.
     */
    private HashMap<String, String> errors = new HashMap<String, String>();

    /**
     * public constructor.
     */
    public ValidationException()
    {
        super("validation failed");
    }

    /**
     * Constructs new ValidationException with specified detailed message.
     *
     * @param message
     *            Detailed message.
     */
    public ValidationException(final String message)
    {
        super(message);
    }

    /**
     *
     * @return errors found in validation.
     */
    public Map<String, String> getErrors()
    {
        return errors;
    }

    /**
     *
     * @param map
     *            errors to set.
     */
    public void setErrors(final HashMap<String, String> map)
    {
        errors = map;
    }

    /**
     *
     * @param property
     *            that had the error.
     * @param message
     *            what's wrong with that property.
     */
    public void addError(final String property, final String message)
    {
        errors.put(property, message);
    }

    /**
     * @return If any errors have been added to the exception.
     */
    public boolean hasErrors()
    {
        return errors != null && !errors.isEmpty();
    }
}
