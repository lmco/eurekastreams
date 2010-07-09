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
package org.eurekastreams.server.action.validation;

import java.io.Serializable;
import java.util.Map;

import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.DomainFormatUtility;

/**
 * Helper Class for validation methods.
 * 
 */
@Deprecated
// TODO: Replace this class with spring based validator objects that take params as constructor args. This
// pulls params and error messages out of code and into config files and then validation classes can be generic
// and just execute configed lists of validators.
public class ValidationHelper
{

    /**
     * Message to display for all unexpected data errors.
     */
    public static final String UNEXPECTED_DATA_ERROR_MESSAGE = "Unexpected data error.";

    /**
     * 
     * @param fields
     *            The Map of fields to check.
     * @param fieldName
     *            The field Name to find.
     * @param alwaysExpected
     *            Is this element expected to be in the fields Map?
     * @param ve
     *            the validation exception to add exceptions onto.
     * @return null if the element is null or not in the list, otherwise return element.
     */
    public Serializable getAndCheckStringFieldExist(final Map<String, Serializable> fields, final String fieldName,
            final boolean alwaysExpected, final ValidationException ve)
    {

        if (!fields.containsKey(fieldName))
        {
            if (alwaysExpected)
            {
                ve.addError(fieldName, UNEXPECTED_DATA_ERROR_MESSAGE);
            }
            return null;
        }

        return fields.get(fieldName);

    }

    /**
     * @param fieldName
     *            The name of the field incase of a validation exception.
     * @param field
     *            The String field to check.
     * @param ve
     *            the validation exception object to add exceptions too.
     * @param nonNullMessage
     *            The message to display if field is null or empty, set to Null to bypass check.
     * @param maxLength
     *            The max length of an element, set to null to bypass check.
     * @param lengthMessage
     *            The message to add if there is a length validation exception.
     * @param pattern
     *            the regexpr pattern that the String must meet. Set to null to bypass check.
     * @param patternMatchMessage
     *            The message to add if there is a pattern match validation exception.
     * @return true if all requirements are meet.
     */
    public boolean stringMeetsRequirments(final String fieldName, final String field, final ValidationException ve,
            final String nonNullMessage, final Integer maxLength, final String lengthMessage, final String pattern,
            final String patternMatchMessage)
    {

        if (field == null || field.isEmpty())
        {
            if (nonNullMessage != null)
            {
                ve.addError(fieldName, nonNullMessage);
                return false;
            }
            return true;
        }

        if (maxLength != null)
        {
            if (field.length() > maxLength)
            {
                ve.addError(fieldName, lengthMessage);
                return false;
            }
        }
        if (pattern != null)
        {
            if (!field.matches(pattern))
            {
                ve.addError(fieldName, patternMatchMessage);
                return false;
            }
        }

        return true;
    }
    
    /**
     * checks if keywords are valid.
     * 
     * @param inBackgroundItems
     *            background list list.
     * @return true if all keywords are of valid length.
     */
    boolean validBackgroundItems(final String inBackgroundItems)
    {
        for (BackgroundItem item : DomainFormatUtility.splitCapabilitiesString(inBackgroundItems))
        {
            if (item.getName().length() > BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH)
            {
                return false;
            }
        }
        return true;
    }
}
