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
package org.eurekastreams.server.search.stream;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for SearchResultSecurityScoperFactory. Note: this doesn't really
 * do much but hit lines of code - the built object doesn't expose any getters.
 */
public class SearchResultSecurityScoperFactoryTest
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
     * Test buildSearchResultSecurityScoper.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testBuildSearchResultSecurityScoper()
    {
        final int pageSize = 123;
        SearchResultListScoperFactory sut = new SearchResultListScoperFactory(
                pageSize);
        sut.buildSearchResultSecurityScoper(context
                .mock(PageFetcher.class, "A"), context.mock(PageFetcher.class,
                "B"), Long.MAX_VALUE);
    }
}
