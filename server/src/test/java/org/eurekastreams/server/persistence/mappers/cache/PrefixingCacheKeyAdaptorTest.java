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

import java.util.Set;

import org.eurekastreams.commons.test.EasyMatcher;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests PrefixingCacheKeyAdaptor.
 */
public class PrefixingCacheKeyAdaptorTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: mapper. */
    private final DomainMapper<Set<String>, Boolean> mapper = context.mock(DomainMapper.class);

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(with(new EasyMatcher<Set<String>>()
                {
                    @Override
                    protected boolean isMatch(final Set<String> inTestObject)
                    {
                        return inTestObject.size() == 1 && inTestObject.contains("Pref:9");
                    }
                }));
                will(returnValue(Boolean.TRUE));
            }
        });

        PrefixingCacheKeyAdaptor sut = new PrefixingCacheKeyAdaptor("Pref:", mapper);
        sut.execute(9L);
        context.assertIsSatisfied();
    }
}
