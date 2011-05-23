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

import java.util.Arrays;
import java.util.Collection;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.notification.GroupRemovedNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.junit.Test;

/**
 * Tests PendingGroupDeniedTranslator.
 */
public class PendingGroupDeniedTranslatorTest
{
    /**
     * Tests translate.
     */
    @Test
    public void testTranslate()
    {
        final String groupName = "Group Name";
        final Collection<Long> coordinators = Arrays.asList(1L, 3L);

        PendingGroupDeniedTranslator sut = new PendingGroupDeniedTranslator();
        GroupRemovedNotificationsRequest request = new GroupRemovedNotificationsRequest(
                RequestType.REQUEST_NEW_GROUP_DENIED, 0, groupName, coordinators);

        NotificationBatch results = sut.translate(request);

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.REQUEST_NEW_GROUP_DENIED, coordinators);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(1, props.size());
        PropertyMapTestHelper.assertValue(props, "groupName", groupName);
    }
}
