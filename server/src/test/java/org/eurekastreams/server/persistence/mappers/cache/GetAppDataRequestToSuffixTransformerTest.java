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

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.action.request.opensocial.GetAppDataRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for GetAppDataRequestToSuffixTransformer.
 * 
 */
public class GetAppDataRequestToSuffixTransformerTest
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
     * {@link GetAppDataRequest}.
     */
    private GetAppDataRequest request = context.mock(GetAppDataRequest.class);

    /**
     * System under test.
     */
    private GetAppDataRequestToSuffixTransformer sut = new GetAppDataRequestToSuffixTransformer();

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(request).getApplicationId();
                will(returnValue(5L));

                oneOf(request).getOpenSocialId();
                will(returnValue("9"));
            }
        });

        assertEquals("5_9", sut.transform(request));
        context.assertIsSatisfied();
    }
}
