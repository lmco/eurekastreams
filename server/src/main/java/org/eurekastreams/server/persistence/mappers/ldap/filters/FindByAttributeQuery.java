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
package org.eurekastreams.server.persistence.mappers.ldap.filters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ldap.filter.AbstractFilter;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;

/**
 * Creates an LDAP filter to search for a user by an attribute.
 */
public class FindByAttributeQuery implements FilterCreator
{
    /**
     * Map of the Equals filter for this Filter Creator. This allows the caller to configure as many required filters
     * with every call of this {@link FilterCreator}.
     */
    private final List<Filter> filterList;

    /**
     * Default constructor override.
     */
    public FindByAttributeQuery()
    {
        filterList = new ArrayList<Filter>();
    }

    /**
     * Constructor.
     * 
     * @param inFilterList
     *            - list of filters for this Filter Creator. This allows the caller to configure as many required
     *            filters with every call to this {@link FilterCreator}.
     */
    public FindByAttributeQuery(final List<Filter> inFilterList)
    {
        filterList = inFilterList;
    }

    /**
     * Creates a filter.
     * 
     * @param searchString
     *            the string to search for.
     * @return the filter object.
     */
    public AbstractFilter getFilter(final String searchString)
    {
        int index = searchString.indexOf("=");
        if (index == -1)
        {
            throw new IllegalArgumentException("Malformed arg");
        }

        AndFilter filter;

        filter = new AndFilter();
        for (Filter filterObject : filterList)
        {
            filter.and(filterObject);
        }
        filter.and(new LikeFilter(searchString.substring(0, index), searchString.substring(index + 1, searchString
                .length())));

        return filter;
    }

}
