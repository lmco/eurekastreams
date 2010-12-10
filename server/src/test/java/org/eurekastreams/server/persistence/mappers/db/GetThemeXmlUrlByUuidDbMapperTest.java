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

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetThemeXmlUrlByUuidDbMapper.
 * 
 */
public class GetThemeXmlUrlByUuidDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetThemeXmlUrlByUuidDbMapper sut;

    /**
     * Test.
     */
    @Test
    public void test()
    {
        assertEquals("http://www.eurekastreams.org/theme.xml", sut.execute("f81d4fae-7dec-11d0-a765-00a0c91e6bf6"));
    }
}
