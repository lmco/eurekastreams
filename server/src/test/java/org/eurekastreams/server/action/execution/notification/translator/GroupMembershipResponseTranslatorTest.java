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

import static junit.framework.Assert.assertEquals;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.junit.Test;

/**
 * Tests GroupMembershipResponseTranslator.
 */
public class GroupMembershipResponseTranslatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long GROUP_ID = 2222L;

    /** Test data. */
    private static final long FOLLOWER_ID = 150L;

    /**
     * Test creating the notification for a group membership request approval/denial.
     */
    @Test
    public void testTranslate()
    {
        GroupMembershipResponseTranslator sut = new GroupMembershipResponseTranslator(
                NotificationType.REQUEST_GROUP_ACCESS_APPROVED);

        CreateNotificationsRequest request = new CreateNotificationsRequest(null, ACTOR_ID, GROUP_ID, FOLLOWER_ID);
        NotificationBatch results = sut.translate(request);

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.REQUEST_GROUP_ACCESS_APPROVED, FOLLOWER_ID);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(2, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, ACTOR_ID);
        PropertyMapTestHelper.assertPlaceholder(props, "group", DomainGroupModelView.class, GROUP_ID);
    }
}
