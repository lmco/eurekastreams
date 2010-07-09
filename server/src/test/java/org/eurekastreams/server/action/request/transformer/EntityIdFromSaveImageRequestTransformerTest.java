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
package org.eurekastreams.server.action.request.transformer;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.profile.SaveImageRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for EntityIdFromSaveImageRequestTransformer.
 * 
 */
public class EntityIdFromSaveImageRequestTransformerTest
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
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link SaveImageRequest}.
     */
    private SaveImageRequest request = context.mock(SaveImageRequest.class);

    /**
     * System under test.
     */
    private EntityIdFromSaveImageRequestTransformer sut = new EntityIdFromSaveImageRequestTransformer();

    /**
     * Test.
     */
    @Test
    public void testTransformLongResult()
    {

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getEntityId();
                will(returnValue(5L));
            }
        });

        assertEquals(5L, sut.transform(actionContext));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testTransformStringResult()
    {
        sut.setReturnValueAsString(true);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getEntityId();
                will(returnValue(5L));
            }
        });

        assertEquals("5", sut.transform(actionContext));
        context.assertIsSatisfied();
    }

}
