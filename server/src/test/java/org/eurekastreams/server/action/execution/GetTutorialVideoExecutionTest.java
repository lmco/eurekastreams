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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.domain.TutorialVideoDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for this GetTutorialVideoExecution.
 * 
 */
public class GetTutorialVideoExecutionTest
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
     * Mapper mock to look up a tutorial video.
     */
    private DomainMapper<Long, Set<TutorialVideoDTO>> tutorialVideoMapper = context.mock(DomainMapper.class);

    /**
     * test that it executes correctly.
     */
    @Test
    public void testExecution()
    {
        GetTutorialVideoExecution sut = new GetTutorialVideoExecution(tutorialVideoMapper);

        context.checking(new Expectations()
        {
            {
                oneOf(tutorialVideoMapper).execute(null);
                will(returnValue(new HashSet<TutorialVideoDTO>()));
            }
        });

        assertNotNull(sut.execute(null));
        context.assertIsSatisfied();
    }

}
