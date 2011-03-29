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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertSame;

import java.io.Serializable;

import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for DomainMapperTransformer.
 */
public class DomainMapperTransformerTest
{
    /**
     * mock context.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * The mapper to wrap.
     */
    private DomainMapper<Serializable, Serializable> domainMapper = context.mock(DomainMapper.class, "domainMapper");

    /**
     * The transformer to transform the mapper results.
     */
    private Transformer<Serializable, Serializable> transformer = context.mock(Transformer.class, "transformer");

    /**
     * System under test.
     */
    private DomainMapperTransformer sut = new DomainMapperTransformer(domainMapper, transformer);

    /**
     * Test execute().
     */
    @Test
    public void testExecute()
    {
        final String request = "aaaaaa";
        final String mapperResponse = "bbbbbbb";
        final String transformResponse = "ccccccc";
        context.checking(new Expectations()
        {
            {
                oneOf(domainMapper).execute(request);
                will(returnValue(mapperResponse));

                oneOf(transformer).transform(mapperResponse);
                will(returnValue(transformResponse));
            }
        });

        assertSame(transformResponse, sut.execute(request));

        context.assertIsSatisfied();
    }
}
