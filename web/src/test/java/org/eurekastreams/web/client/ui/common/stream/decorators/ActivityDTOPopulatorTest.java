/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.decorators;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * The base populator test.
 *
 */
public class ActivityDTOPopulatorTest
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
     * Verb populator mock.
     */
    private final ActivityDTOPopulatorStrategy verbPopulator = context
            .mock(ActivityDTOPopulatorStrategy.class);
    
    /**
     * The object populator mock.
     */
    private final ActivityDTOPopulatorStrategy objPopulator = context.mock(
            ActivityDTOPopulatorStrategy.class, "obj");

    /**
     * The system under test.
     */
    private ActivityDTOPopulator sut = new ActivityDTOPopulator();

    /**
     * Test with both verb and object populators injected.
     */
    @Test
    public final void testWithNothingNull()
    {
        String content = StaticResourceBundle.INSTANCE.coreCss().content();
        EntityType destinationType = EntityType.PERSON;
        String destinationId = "username1";

        context.checking(new Expectations()
        {
            {
                oneOf(verbPopulator).populate(with(any(ActivityDTO.class)));
                oneOf(objPopulator).populate(with(any(ActivityDTO.class)));
            }
        });

        ActivityDTO result = sut.getActivityDTO(content, destinationType, destinationId,
                verbPopulator, objPopulator);

        Assert.assertEquals(content, result.getBaseObjectProperties().get(StaticResourceBundle.INSTANCE.coreCss().content()));
        Assert.assertEquals(destinationType, result.getDestinationStream().getType());
        Assert.assertEquals(destinationId, result.getDestinationStream().getUniqueIdentifier());
        
        context.assertIsSatisfied();
    }
    
    /**
     * Test with just an object populator.
     */
    @Test
    public final void testWithVerbNull()
    {
        String content = StaticResourceBundle.INSTANCE.coreCss().content();
        EntityType destinationType = EntityType.PERSON;
        String destinationId = "username1";

        context.checking(new Expectations()
        {
            {
                oneOf(objPopulator).populate(with(any(ActivityDTO.class)));
            }
        });

        ActivityDTO result = sut.getActivityDTO(content, destinationType, destinationId,
                null, objPopulator);

        Assert.assertEquals(content, result.getBaseObjectProperties().get(StaticResourceBundle.INSTANCE.coreCss().content()));
        Assert.assertEquals(destinationType, result.getDestinationStream().getType());
        Assert.assertEquals(destinationId, result.getDestinationStream().getUniqueIdentifier());
        
        context.assertIsSatisfied();
    }
    
    /**
     * Test with just a verb populator.
     */
    @Test
    public final void testWithObjNull()
    {
        String content = StaticResourceBundle.INSTANCE.coreCss().content();
        EntityType destinationType = EntityType.PERSON;
        String destinationId = "username1";

        context.checking(new Expectations()
        {
            {
                oneOf(verbPopulator).populate(with(any(ActivityDTO.class)));
            }
        });

        ActivityDTO result = sut.getActivityDTO(content, destinationType, destinationId,
                verbPopulator, null);

        Assert.assertEquals(content, result.getBaseObjectProperties().get(StaticResourceBundle.INSTANCE.coreCss().content()));
        Assert.assertEquals(destinationType, result.getDestinationStream().getType());
        Assert.assertEquals(destinationId, result.getDestinationStream().getUniqueIdentifier());
        
        context.assertIsSatisfied();
    }
}
