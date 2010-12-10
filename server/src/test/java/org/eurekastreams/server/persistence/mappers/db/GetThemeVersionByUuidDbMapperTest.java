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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for GetThemeVersionByUuidDbMapper.
 * 
 */
public class GetThemeVersionByUuidDbMapperTest
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
     * Mapper for getting theme css by uuid.
     */
    private DomainMapper<String, String> getThemeCssByUuidMapper = context.mock(DomainMapper.class);

    /**
     * System under test.
     */
    private GetThemeVersionByUuidDbMapper sut = new GetThemeVersionByUuidDbMapper(getThemeCssByUuidMapper);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                allowing(getThemeCssByUuidMapper).execute("uuid1");
                will(returnValue("string 1"));

                oneOf(getThemeCssByUuidMapper).execute("uuid2");
                will(returnValue("string 2"));
            }
        });

        // assert different input creates different version.
        assertTrue(!(sut.execute("uuid1").equals(sut.execute("uuid2"))));

        // assert same input creates same version.
        assertTrue(sut.execute("uuid1").equals(sut.execute("uuid1")));

        context.assertIsSatisfied();
    }

}
