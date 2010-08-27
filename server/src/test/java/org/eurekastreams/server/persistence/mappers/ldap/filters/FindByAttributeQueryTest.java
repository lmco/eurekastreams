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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.ldap.filter.AbstractFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

/**
 * Tests LDAP attribute filter.
 */
public class FindByAttributeQueryTest
{
    /**
     * System under test.
     */
    private FindByAttributeQuery sut;

    /**
     * Tests creating the filter.
     */
    @Test
    public final void testCreateFilterWithoutEqualFilterAttribute()
    {
        sut = new FindByAttributeQuery();
        AbstractFilter filter = sut.getFilter("attrib=something");

        assertEquals("(attrib=something)", filter.encode());
    }

    /**
     * Tests creating the filter.
     */
    @Test
    public final void testCreateFilter()
    {
        List<Filter> filterList = new ArrayList<Filter>();
        filterList.add(new EqualsFilter("objectclass", "person"));
        filterList.add(new EqualsFilter("objectclass", "user"));
        sut = new FindByAttributeQuery(filterList);
        AbstractFilter filter = sut.getFilter("attrib=something");

        assertEquals("(&(objectclass=person)(objectclass=user)(attrib=something))", filter.encode());
    }

    /**
     * Tests creating the filter with a malformed search string.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testCreateFilterMalformed()
    {
        sut = new FindByAttributeQuery();
        sut.getFilter("attrib745something");
    }
}
