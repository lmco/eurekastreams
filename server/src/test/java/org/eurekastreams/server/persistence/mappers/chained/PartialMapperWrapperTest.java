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
package org.eurekastreams.server.persistence.mappers.chained;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for PartialMapperWrapper.
 */
public class PartialMapperWrapperTest
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
     * Mapper to wrap with a partial mapper wrapper.
     */
    private DomainMapper<Object, Object> domainMapper = context.mock(DomainMapper.class);

    /**
     * Test execute() with a response.
     */
    @Test
    public void testExecuteWithResponse()
    {
        final Object request = new Object();
        final Object response = new Object();

        context.checking(new Expectations()
        {
            {
                oneOf(domainMapper).execute(request);
                will(returnValue(response));
            }
        });

        PartialMapperResponse<Object, Object> partialResponse = new PartialMapperWrapper<Object, Object>(domainMapper)
                .execute(request);

        assertSame(response, partialResponse.getResponse());
        assertNull(partialResponse.getUnhandledRequest());
        assertFalse(partialResponse.hasUnhandledRequest());

        context.assertIsSatisfied();
    }

    /**
     * Test execute() with a null response.
     */
    @Test
    public void testExecuteWithNullResponse()
    {
        final Object request = new Object();

        context.checking(new Expectations()
        {
            {
                oneOf(domainMapper).execute(request);
                will(returnValue(null));
            }
        });

        PartialMapperResponse<Object, Object> partialResponse = new PartialMapperWrapper<Object, Object>(domainMapper)
                .execute(request);

        assertNull(partialResponse.getResponse());
        assertSame(request, partialResponse.getUnhandledRequest());
        assertTrue(partialResponse.hasUnhandledRequest());

        context.assertIsSatisfied();
    }

}
