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
package org.eurekastreams.server.persistence.mappers.ldap.callback;

import static org.junit.Assert.assertNotNull;

import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.ContextMapper;

/**
 * Test for ContextMapperCallbackHandlerFactory.
 * 
 */
public class ContextMapperCallbackHandlerFactoryTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link ContextMapper}.
     */
    private ContextMapper mapper = context.mock(ContextMapper.class);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        ContextMapperCallbackHandlerFactory sut = new ContextMapperCallbackHandlerFactory(mapper);

        CollectingNameClassPairCallbackHandler result = sut.getCallbackHandler();
        assertNotNull(result);
    }
}
