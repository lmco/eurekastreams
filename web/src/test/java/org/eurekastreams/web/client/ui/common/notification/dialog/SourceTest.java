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
package org.eurekastreams.web.client.ui.common.notification.dialog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eurekastreams.server.domain.EntityType;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.HasText;

/**
 * Tests Source.
 */
public class SourceTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            GWTMockUtilities.disarm();
        }
    };

    /** Fixture. */
    private final Source parent = context.mock(Source.class);

    /** Fixture. */
    private final HasText widget = context.mock(HasText.class);

    /**
     * Tests getters/setters.
     */
    @Test
    public void testGettersSetters()
    {
        Source sut = new Source(EntityType.GROUP, "UID", "Display", parent);

        assertEquals("UID", sut.getUniqueId());
        assertEquals(EntityType.GROUP, sut.getEntityType());
        assertEquals("Display", sut.getDisplayName());
        assertSame(parent, sut.getParent());

        sut.setUnreadCount(9);
        assertEquals(9, sut.getUnreadCount());
        sut.decrementUnreadCount();
        assertEquals(8, sut.getUnreadCount());
        sut.incrementUnreadCount();
        assertEquals(9, sut.getUnreadCount());

        sut.setWidget(widget);
        assertSame(widget, sut.getWidget());

        sut.setDisplayName("New Name");
        assertEquals("New Name", sut.getDisplayName());
    }
}
