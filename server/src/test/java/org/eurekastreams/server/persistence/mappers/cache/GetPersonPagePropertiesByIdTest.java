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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetPersonPagePropertiesById.
 * 
 */
public class GetPersonPagePropertiesByIdTest extends CachedMapperTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * An arbitrary person id to use for testing.
     */
    private static final long PERSON_ID = 123L;

    /**
     * mocked tab for testing results.
     */
    private Person person = context.mock(Person.class);

    /**
     * {@link PersonPagePropertiesDTO}.
     */
    private PersonPagePropertiesDTO ppp = context.mock(PersonPagePropertiesDTO.class);

    /**
     * Transformer to convert person to PersonPageProperties.
     */
    private Transformer<Person, PersonPagePropertiesDTO> transformer = context.mock(Transformer.class);

    /**
     * {@link FindByIdMapper}.
     */
    private FindByIdMapper<Person> personByIdMapper = context.mock(FindByIdMapper.class);

    /**
     * Cache mock.
     */
    private Cache cache = context.mock(Cache.class);

    /**
     * System under test.
     */
    private GetPersonPagePropertiesById sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new GetPersonPagePropertiesById(personByIdMapper, transformer, null);
        sut.setCache(cache);
    }

    /**
     * Test.
     */
    @Test
    public void testCachedValue()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(cache).get(CacheKeys.PERSON_PAGE_PROPERTIES_BY_ID + PERSON_ID);
                will(returnValue(ppp));
            }
        });

        assertEquals(ppp, sut.execute(PERSON_ID));

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testWithoutCachedValue()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(cache).get(CacheKeys.PERSON_PAGE_PROPERTIES_BY_ID + PERSON_ID);
                will(returnValue(null));

                oneOf(personByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));

                oneOf(transformer).transform(person);
                will(returnValue(ppp));

                oneOf(cache).set(CacheKeys.PERSON_PAGE_PROPERTIES_BY_ID + PERSON_ID, ppp);
            }
        });

        assertEquals(ppp, sut.execute(PERSON_ID));

        context.assertIsSatisfied();
    }

}
