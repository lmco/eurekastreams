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
package org.eurekastreams.commons.search.modelview;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Container for matched keywords in fields.
 */
public class FieldMatch implements Serializable
{
    /**
     * Version ID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * A Map of indexedField -> Set(matched keywords).
     */
    private Map<String, Set<String>> matchedFields = new HashMap<String, Set<String>>();

    /**
     * Check whether the input field has matching keywords.
     *
     * @param fieldName
     *            the field name to check
     * @return whether the field has matching keywords
     */
    public boolean isMatch(final String fieldName)
    {
        return matchedFields.containsKey(fieldName);
    }

    /**
     * Gets the keys for the matched fields.
     *
     * @return the keys for the matches fields.
     */
    public Set<String> getMatchedFieldKeys()
    {
        return matchedFields.keySet();
    }

    /**
     * Add a matched keyword for a field.
     *
     * @param fieldName
     *            the name of the field that matches
     * @param matchedKeyword
     *            the keyword that matched the field
     */
    public void addMatch(final String fieldName, final String matchedKeyword)
    {
        Set<String> matches;
        if (!isMatch(fieldName))
        {
            matches = new HashSet<String>();
            matchedFields.put(fieldName, matches);
        }
        else
        {
            matches = matchedFields.get(fieldName);
        }
        matches.add(matchedKeyword);
    }

    /**
     * Return a set of keywords that matched a field.
     *
     * @param fieldName
     *            the name of the field to check
     * @return a sorted set of the matching keywords for the input field name
     */
    public Set<String> getMatchingKeywords(final String fieldName)
    {
        if (isMatch(fieldName))
        {
            return matchedFields.get(fieldName);
        }
        else
        {
            return new HashSet<String>();
        }
    }

    /**
     * Get the map of fieldName->sorted list of keywords - for serialization only.
     *
     * @return the map of fieldName->sorted list of keywords
     */
    protected Map<String, Set<String>> getMatchedFields()
    {
        return matchedFields;
    }

    /**
     * Set the map of fieldName->sorted list of keywords - for serialization only.
     *
     * @param inMatchedFields
     *            the map of fieldName->sorted list of keywords to set
     */
    protected void setMatchedFields(final Map<String, Set<String>> inMatchedFields)
    {
        matchedFields = inMatchedFields;
    }
}
