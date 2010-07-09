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
package org.eurekastreams.server.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eurekastreams.server.domain.NotificationFilterPreference.Category;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests the entity.
 */
public class NotificationFilterPreferenceTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Tests the getters and setters.
     */
    @Test
    public void testGettersSetters()
    {
        NotificationFilterPreference sut = new NotificationFilterPreference();

        sut.setNotifierType("ExpectedType");
        assertEquals("ExpectedType", sut.getNotifierType());

        Person person = context.mock(Person.class);
        sut.setPerson(person);
        assertSame(person, sut.getPerson());

        sut.setNotificationCategory(Category.POST_TO_GROUP_STREAM);
        assertEquals(Category.POST_TO_GROUP_STREAM, sut.getNotificationCategory());
    }
}
