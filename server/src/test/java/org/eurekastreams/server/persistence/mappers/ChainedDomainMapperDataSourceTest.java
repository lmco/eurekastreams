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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for ChainedDomainMapperDataSource.
 */
public class ChainedDomainMapperDataSourceTest
{
    /**
     * Context for mocking.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked DomainMapper.
     */
    final DomainMapper<Object, Object> domainMapper = context.mock(DomainMapper.class);

    /**
     * Mocked RefreshDataSourceMapper.
     */
    final RefreshDataSourceMapper<Object> refreshMapper = context.mock(RefreshDataSourceMapper.class);

    /**
     * Test constructor with 2 args.
     */
    @Test
    public void testConstructorWithTwoArgs()
    {
        ChainedDomainMapperDataSource<Object, Object> sut = new ChainedDomainMapperDataSource<Object, Object>(
                domainMapper, refreshMapper);

        // test constructor
        assertSame(domainMapper, sut.getDomainMapper());
        assertSame(refreshMapper, sut.getRefreshMapper());

        // test setter & getter
        sut.setDomainMapper(null);
        sut.setRefreshMapper(null);
        assertNull(sut.getDomainMapper());
        assertNull(sut.getRefreshMapper());

        sut.setDomainMapper(domainMapper);
        sut.setRefreshMapper(refreshMapper);
        assertSame(domainMapper, sut.getDomainMapper());
        assertSame(refreshMapper, sut.getRefreshMapper());
    }

    /**
     * Test constructor with 1 arg.
     */
    public void testConstructorWithOneArgs()
    {
        ChainedDomainMapperDataSource<Object, Object> sut = new ChainedDomainMapperDataSource<Object, Object>(
                domainMapper);

        // test constructor
        assertSame(domainMapper, sut.getDomainMapper());
        assertNull(refreshMapper);

        // test setter & getter
        sut.setDomainMapper(null);
        assertNull(sut.getDomainMapper());

        sut.setDomainMapper(domainMapper);
        sut.setRefreshMapper(refreshMapper);
        assertSame(domainMapper, sut.getDomainMapper());
        assertSame(refreshMapper, sut.getRefreshMapper());
    }
}
