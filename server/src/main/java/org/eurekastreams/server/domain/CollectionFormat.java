/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

// TODO merge this with uinnovate's TagCollectionFormat and use from commons

/**
 * Similar in functionality to Date and DateFormat class, this handles the
 * parsing and un-parsing of lists of tags in a string.
 */
public class CollectionFormat
{

    /**
     * delimit a list of tags in a string.
     */
    public static final String DELIMITER = ",";

    /**
     * Constructor.
     */
    public CollectionFormat()
    {

    }

    /**
     * parses the comma-delimited string into a collection of strings. (more
     * appropriate for our purposes than StringTokenizer)
     * 
     * @param toParse
     *            the string to parse
     * @return a collection of strings parsed from toParse
     */
    public Collection<String> parse(final String toParse)
    {
        String[] stringArray = toParse.split(DELIMITER);
        ArrayList<String> strings = new ArrayList<String>();
        for (String stringName : stringArray)
        {
            String name = stringName.trim();
            if (name.length() > 0)
            {
                strings.add(name);
            }
        }
        return strings;
    }

    /**
     * turns a list of T into a displayable string. uses the toString() method
     * of each element.
     * 
     * If an element is null, it is not returned in the string.
     * 
     * generics warnings are suppressed because we intend to take any collection
     * type.
     * 
     * @param elements the elements to format
     * @return formatted string
     */
    @SuppressWarnings("unchecked")
    public String format(final Collection elements)
    {
        if (elements.size() == 0)
        {
            return "";
        }

        final String seperator = DELIMITER + " ";
        StringBuilder builder = new StringBuilder();
        Iterator iterator = elements.iterator();
        while (iterator.hasNext())
        {
            Object next = iterator.next();
            if (next != null)
            {
                String name = next.toString().trim();
                builder.append(name + seperator);
            }

        }

        String formattedString = builder.substring(0, builder.lastIndexOf(seperator)).trim();

        return formattedString;
    }

}
