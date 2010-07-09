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
package org.eurekastreams.commons.search;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Boosts a field in a Lucene query.
 */
public class LuceneFieldBooster
{
    /**
     * A list of the names of the fields allowed to be boosted.
     */
    private List<String> allowableBoostFields;

    /**
     * Setter for allowable fields.
     *
     * @param inAllowableBoostFields
     *            the fields that are allowed to be boosted.
     */
    public void setAllowableBoostFields(final List<String> inAllowableBoostFields)
    {
        allowableBoostFields = inAllowableBoostFields;
    }

    /**
     * Sets a field weight, creates the field if it doens't exist.
     *
     * @param inFormatString
     *            the format string.
     *
     * @param fieldName
     *            the field.
     * @param weight
     *            the weight.
     *
     * @return the formatted string.
     */
    public String boostField(final String inFormatString, final String fieldName, final int weight)
    {
        if (!allowableBoostFields.contains(fieldName))
        {
            throw new InvalidParameterException("The input field name is not in the list of allowed fields.");
        }

        String formatString = inFormatString;

        Pattern searchPattern = Pattern.compile(fieldName + ":\\(%1\\$s\\)(\\^[0-9]+)?");

        Matcher patternMatcher = searchPattern.matcher(formatString);

        if (patternMatcher.find())
        {
            formatString = patternMatcher.replaceFirst(fieldName + ":(%1\\$s)^" + weight);
        }
        else
        {
            formatString += " " + fieldName + ":(%1$s)^" + weight;
        }

        return formatString;
    }
}
