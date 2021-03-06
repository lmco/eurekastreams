/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetStreamScopeIdForScopeTypeByUniqueKeyDbMapper.
 */
public class GetStreamScopeIdForScopeTypeByUniqueKeyDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetStreamScopeIdForScopeTypeByUniqueKeyDbMapper sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetStreamScopeIdForScopeTypeByUniqueKeyDbMapper(ScopeType.PERSON);
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute() when it's found.
     */
    @Test
    public void testExecuteWhenFound()
    {
        Assert.assertEquals(new Long(4L), sut.execute("mrburns"));
    }

    /**
     * Test execute() when it's not found.
     */
    @Test
    public void testExecuteWhenNotFound()
    {
        Assert.assertNull(sut.execute("heythere"));
    }

    /**
     * Test execute() when it's not found and wrong type.
     */
    @Test
    public void testExecuteWhenNotFoundForWrongType()
    {
        sut = new GetStreamScopeIdForScopeTypeByUniqueKeyDbMapper(ScopeType.RESOURCE);
        sut.setEntityManager(getEntityManager());
        Assert.assertNull(sut.execute("mrburns"));
    }
}
