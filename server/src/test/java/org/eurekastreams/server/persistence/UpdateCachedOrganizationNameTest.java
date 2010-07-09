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
package org.eurekastreams.server.persistence;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for updating cached parent organization names in groups and people.
 */
public class UpdateCachedOrganizationNameTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private UpdateCachedOrganizationName sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        ((SimpleMemoryCache) sut.getCache()).clear();
    }

    /**
     * Organization id for testing.
     */
    private static final long ORG_ID = 5;

    /**
     * Test original name for org.
     */
    private static final String OLD_NAME = "original org name";

    /**
     * Test new name for org.
     */
    private static final String NEW_NAME = "new org name";

    /**
     * Person id for testing.
     */
    private static final long PERSON_ID = 42;

    /**
     * Group id for testing.
     */
    private static final long GROUP_ID = 5;

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        PersonModelView person = new PersonModelView();
        person.setParentOrganizationName(OLD_NAME);
        sut.getCache().set(CacheKeys.PERSON_BY_ID + PERSON_ID, person);
        PersonModelView cachedPerson = (PersonModelView) sut.getCache().get(CacheKeys.PERSON_BY_ID + PERSON_ID);
        assertEquals(OLD_NAME, cachedPerson.getParentOrganizationName());

        DomainGroupModelView group = new DomainGroupModelView();
        group.setParentOrganizationName(OLD_NAME);
        sut.getCache().set(CacheKeys.GROUP_BY_ID + GROUP_ID, group);
        DomainGroupModelView cachedGroup = (DomainGroupModelView) sut.getCache().get(CacheKeys.GROUP_BY_ID + GROUP_ID);
        assertEquals(OLD_NAME, cachedGroup.getParentOrganizationName());

        sut.execute(ORG_ID, NEW_NAME);

        cachedPerson = (PersonModelView) sut.getCache().get(CacheKeys.PERSON_BY_ID + PERSON_ID);
        assertEquals(NEW_NAME, cachedPerson.getParentOrganizationName());

        cachedGroup = (DomainGroupModelView) sut.getCache().get(CacheKeys.GROUP_BY_ID + GROUP_ID);
        assertEquals(NEW_NAME, cachedGroup.getParentOrganizationName());
    }
}
