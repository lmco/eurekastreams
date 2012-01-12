/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;


/**
 * Test fixture for SharedResourceRequestToUniqueKeyTransformer.
 */
public class SharedResourceRequestToCacheKeySuffixTransformerTest
{
    /** Used for mocking objects. */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: nested transformer. */
    private final Transformer<String, String> nested = mockery.mock(Transformer.class);

    /**
     * System under test.
     */
    private Transformer<SharedResourceRequest, String> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new SharedResourceRequestToCacheKeySuffixTransformer(nested);
    }

    /**
     * Test transforming a null request.
     */
    @Test
    public void testWithNullRequest()
    {
        Assert.assertNull(sut.transform(null));
    }

    /**
     * Test transforming a null unique key.
     */
    @Test
    public void testWithNullUniqueKey()
    {
        SharedResourceRequest request = new SharedResourceRequest();
        request.setUniqueKey(null);

        Assert.assertNull(sut.transform(request));
    }

    /**
     * Test transforming a valid request with a valid unique key.
     */
    @Test
    public void testWithSuccess()
    {
        SharedResourceRequest request = new SharedResourceRequest();
        request.setUniqueKey("HI");

        mockery.checking(new Expectations()
        {
            {
                oneOf(nested).transform("HI");
                will(returnValue("BYE"));
            }
        });

        assertEquals("BYE", sut.transform(request));
    }
}
