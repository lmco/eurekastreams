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
package org.eurekastreams.server.service.actions.strategies.ldap.filters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ldap.filter.AbstractFilter;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

/**
 * Creates a group filter.
 */
public class FindGroup implements FilterCreator
{
    /**
     * Local instance of the filters used for the ldap search query.
     */
    private final List<Filter> filterList;

    /**
     * Local instance of the attribute for matching a group name.
     */
    private final String groupNameMatchAttribute;

    /**
     * Default constructor that sets the group name match attribute to "cn".
     */
    public FindGroup()
    {
        filterList = new ArrayList<Filter>();
        groupNameMatchAttribute = "cn";
    }

    /**
     * Constructor.
     *
     * @param inFilterList
     *            - List of filter objects to build the ldap query.
     * @param inGroupNameMatchAttribute
     *            - attribute to match the full group name on.
     */
    public FindGroup(final List<Filter> inFilterList, final String inGroupNameMatchAttribute)
    {
        filterList = inFilterList;
        groupNameMatchAttribute = inGroupNameMatchAttribute;
    }

    /**
     * Gets the group filter.
     *
     * @param queryString
     *            the query.
     * @return the filter.
     */
    public AbstractFilter getFilter(final String queryString)
    {
        AndFilter filter = new AndFilter();

        for (Filter filterObject : filterList)
        {
            filter.and(filterObject);
        }
        filter.and(new EqualsFilter(groupNameMatchAttribute, queryString));

        return filter;
    }

}
