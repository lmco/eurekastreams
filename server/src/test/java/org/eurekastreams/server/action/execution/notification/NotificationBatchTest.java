/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.notification;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.Property;
import org.junit.Test;

/**
 * Tests NotificationBatch.
 */
public class NotificationBatchTest
{
    /** Test data. */
    private static final NotificationType TYPE = NotificationType.COMMENT_TO_COMMENTED_POST;

    /** Test data. */
    private static final String KEY = "key";

    /**
     * Tests construction.
     */
    @Test
    public void testConstructorOneRecipient()
    {
        NotificationBatch sut = new NotificationBatch(TYPE, 9L);
        Collection<Long> result = sut.getRecipients().get(TYPE);
        assertEquals(Collections.singletonList(9L), result);
    }

    /**
     * Tests construction.
     */
    @Test
    public void testConstructorRecipientList()
    {
        Collection<Long> list = Arrays.asList(1L, 3L);
        NotificationBatch sut = new NotificationBatch(TYPE, list);
        Collection<Long> result = sut.getRecipients().get(TYPE);
        assertEquals(list, result);
    }

    /**
     * Tests set recipient.
     */
    @Test
    public void testSetRecipient()
    {
        NotificationBatch sut = new NotificationBatch();
        sut.setRecipient(TYPE, 9L);
        Collection<Long> result = sut.getRecipients().get(TYPE);
        assertEquals(Collections.singletonList(9L), result);
    }

    /**
     * Tests set property.
     */
    @Test
    public void testSetPropertyActual()
    {
        Object o = new Object();
        NotificationBatch sut = new NotificationBatch();
        sut.setProperty(KEY, o);
        Property<Object> result = sut.getProperties().get(KEY);
        assertEquals(o, result.getValue());
    }

    /**
     * Tests set property.
     */
    @Test
    public void testSetPropertyPlaceholder()
    {
        NotificationBatch sut = new NotificationBatch();
        sut.setProperty(KEY, Integer.class, "id");
        Property<Object> result = sut.getProperties().get(KEY);
        assertEquals(Integer.class, result.getType());
        assertEquals("id", result.getIdentity());
    }

    /**
     * Tests create alias.
     */
    @Test
    public void testSetPropertyAlias()
    {
        Object o = new Object();
        NotificationBatch sut = new NotificationBatch();
        sut.setProperty(KEY, o);
        sut.setPropertyAlias("alias", KEY);
        Property<Object> result1 = sut.getProperties().get(KEY);
        Property<Object> result2 = sut.getProperties().get("alias");

        assertEquals(o, result2.getValue());

        result1.setValue(8L);
        assertEquals(8L, result2.getValue());
    }
}
