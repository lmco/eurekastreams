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

import java.util.Collection;

import junit.framework.Assert;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.testing.TestHelper;
import org.junit.Ignore;

/**
 * Helpers for testing translators.
 */
@Ignore
public final class TranslatorTestHelper
{
    /**
     * Forbid creation.
     */
    private TranslatorTestHelper()
    {
    }

    /**
     * Asserts that the notification batch has the expected set of recipients for the given notification type.
     *
     * @param batch
     *            Notification batch to check.
     * @param type
     *            Given notification type.
     * @param recipients
     *            Expected set of recipients.
     */
    public static void assertRecipients(final NotificationBatch batch, final NotificationType type,
            final Long... recipients)
    {
        Assert.assertTrue(TestHelper.containsExactly(batch.getRecipients().get(type), recipients, true));
    }

    /**
     * Asserts that the notification batch has the expected set of recipients for the given notification type.
     *
     * @param batch
     *            Notification batch to check.
     * @param type
     *            Given notification type.
     * @param recipients
     *            Expected set of recipients.
     */
    public static void assertRecipients(final NotificationBatch batch, final NotificationType type,
            final Collection<Long> recipients)
    {
        Assert.assertTrue(TestHelper.containsExactly(batch.getRecipients().get(type), recipients, true));
    }
}
