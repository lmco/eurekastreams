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
package org.eurekastreams.server.persistence.strategies;

import static org.junit.Assert.assertNotNull;

import org.eurekastreams.server.domain.DomainGroup;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for domain group query strategy.
 *
 */
public class DomainGroupQueryStrategyTest
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
     * Mapper to get domain group info.
     */
    private Session hibernateSession = context.mock(Session.class);

    /**
     * System under test.
     */
    private DomainGroupQueryStrategy sut;
    
    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new DomainGroupQueryStrategy();
    }
    
    /**
     * Test.
     */
    @Test
    public void testGetCriteria()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(hibernateSession).createCriteria(DomainGroup.class);
            }
        });

        Criteria result = sut.getCriteria(hibernateSession);
        assertNotNull(result);
        
        context.assertIsSatisfied();
    }
}
