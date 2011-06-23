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
package org.eurekastreams.server.action.execution.notification.translator;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.notification.PrebuiltNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.junit.Test;


/**
 * Tests PreBuiltNotificationTranslator.
 */
public class PreBuiltNotificationTranslatorTest
{
    /**
     * Tests translating.
     */
    @Test
    public void testTranslate()
    {
        NotificationTranslator<PrebuiltNotificationsRequest> sut = new PreBuiltNotificationTranslator();

        final String clientId = "CLIENT_ID";
        final long recipientId = 80L;
        final String message = "MESSAGE";
        final String url = "http://www.eurekastreams.org";

        PrebuiltNotificationsRequest request = new PrebuiltNotificationsRequest(RequestType.EXTERNAL_PRE_BUILT, true,
                clientId, recipientId, message, url);

        NotificationBatch results = sut.translate(request);

        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.PASS_THROUGH, recipientId);

        PropertyMap<Object> props = results.getProperties();
        assertEquals(5, props.size());
        PropertyMapTestHelper.assertValue(props, NotificationPropertyKeys.URL, url);
        PropertyMapTestHelper.assertValue(props, NotificationPropertyKeys.HIGH_PRIORITY, true);
        PropertyMapTestHelper.assertValue(props, "message", message);
        PropertyMapTestHelper.assertPlaceholder(props, NotificationPropertyKeys.SOURCE, OAuthConsumer.class, clientId);
        PropertyMapTestHelper.assertAlias(props, NotificationPropertyKeys.ACTOR, NotificationPropertyKeys.SOURCE);
    }
}
