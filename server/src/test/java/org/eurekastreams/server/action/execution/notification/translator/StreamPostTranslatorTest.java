/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.notification.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.request.notification.ActivityNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.junit.Test;

/**
 * Tests the stream post notification translator.
 *
 */
public class StreamPostTranslatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long STREAM_OWNER_ID = 2222L;

    /** Test data. */
    private static final long ACTIVITY_ID = 3333L;

    /**
     * Test creating the notification for the event of posting to a personal stream.
     */
    @Test
    public void testTranslatePersonalStreamPost()
    {
        NotificationTranslator sut = new StreamPostTranslator();

        CreateNotificationsRequest request = new ActivityNotificationsRequest(null, ACTOR_ID, STREAM_OWNER_ID,
                ACTIVITY_ID);
        NotificationBatch results = sut.translate(request);

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.POST_TO_PERSONAL_STREAM, STREAM_OWNER_ID);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(4, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, ACTOR_ID);
        PropertyMapTestHelper.assertPlaceholder(props, "stream", PersonModelView.class, STREAM_OWNER_ID);
        PropertyMapTestHelper.assertPlaceholder(props, "activity", ActivityDTO.class, ACTIVITY_ID);
        PropertyMapTestHelper.assertAlias(props, "source", "stream");
    }

    /**
     * Test that the notification is not sent for a person posting to his own personal stream.
     */
    @Test
    public void testTranslateOwnPersonalStreamPost()
    {
        NotificationTranslator sut = new StreamPostTranslator();

        CreateNotificationsRequest request = new ActivityNotificationsRequest(null, ACTOR_ID, ACTOR_ID, ACTIVITY_ID);
        NotificationBatch results = sut.translate(request);

        assertNull(results);
    }
}
