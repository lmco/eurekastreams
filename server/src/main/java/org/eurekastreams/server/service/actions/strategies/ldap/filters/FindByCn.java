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

import org.springframework.ldap.filter.AbstractFilter;

/**
 * Creates an LDAP filter to search for a user by their CN.
 */
public class FindByCn implements FilterCreator
{
    /**
     * Local Instance of the {@link FindByAttrib} Filter Creator.
     */
    private final FindByAttrib findByAttribFilterCreator;

    /**
     * Default constructor results in the direct cn=searchtext* query.
     */
    public FindByCn()
    {
        findByAttribFilterCreator = new FindByAttrib();
    }

    /**
     * Constructor uses the supplied {@link FindByAttrib} {@link FilterCreator} to decorate the cn=searchtext* query.
     *
     * @param inFindByAttribFilterCreator
     *            - instance of the {@link FindByAttrib} {@link FilterCreator}.
     */
    public FindByCn(final FindByAttrib inFindByAttribFilterCreator)
    {
        findByAttribFilterCreator = inFindByAttribFilterCreator;
    }

    /**
     * Creates a filter.
     *
     * @param cn
     *            the filter.
     *
     * @return the filter.
     */
    public AbstractFilter getFilter(final String cn)
    {
        return findByAttribFilterCreator.getFilter("cn=" + cn + "*");
    }
}
