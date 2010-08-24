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
package org.eurekastreams.server.persistence.mappers.ldap.filters;

import org.springframework.ldap.filter.AbstractFilter;

/**
 * Creates an LDAP filter to search via configured attribute.
 */
public class FindByAttribute implements FilterCreator
{
    /**
     * Local Instance of the {@link FindByAttrib} Filter Creator.
     */
    private final FindByAttributeQuery findByAttribFilterCreator;

    /**
     * Attribute.
     */
    private String attribute;

    /**
     * flag if wildcard should be added to query or not.
     */
    private boolean exactMatch;

    /**
     * Constructor.
     * 
     * @param inAttribute
     *            Attribute.
     * @param inExactMatch
     *            true if attribute should be exact match, false to append wildcard to value.
     */
    public FindByAttribute(final String inAttribute, final boolean inExactMatch)
    {
        this(inAttribute, new FindByAttributeQuery(), inExactMatch);

    }

    /**
     * Constructor.
     * 
     * @param inAttribute
     *            Attribute.
     * @param inFindByAttributeQueryFilterCreator
     *            - instance of the {@link FindByAttrib} {@link FilterCreator}.
     * @param inExactMatch
     *            true if attribute should be exact match, false to append wildcard to value.
     */
    public FindByAttribute(final String inAttribute, final FindByAttributeQuery inFindByAttributeQueryFilterCreator,
            final boolean inExactMatch)
    {
        attribute = inAttribute + "=";
        findByAttribFilterCreator = inFindByAttributeQueryFilterCreator;
        exactMatch = inExactMatch;
    }

    /**
     * Creates a filter.
     * 
     * @param inAttributeValue
     *            The attribute value to match on.
     * 
     * @return the filter.
     */
    public AbstractFilter getFilter(final String inAttributeValue)
    {
        return findByAttribFilterCreator.getFilter((exactMatch ? attribute + inAttributeValue : attribute
                + inAttributeValue + "*"));
    }
}
