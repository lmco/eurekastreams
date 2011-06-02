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
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.notification.GroupActionNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests PendingGroupApprovedTranslator.
 */
public class PendingGroupApprovedTranslatorTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Group mapper. */
    private final DomainMapper<Long, DomainGroupModelView> groupMapper = context.mock(DomainMapper.class,
            "groupMapper");

    /** Group mapper. */
    private final DomainMapper<Long, List<Long>> groupCoordinatorMapper = context.mock(DomainMapper.class,
            "groupCoordinatorMapper");

    /**
     * Tests translate.
     */
    @Test
    public void testTranslate()
    {
        final long groupId = 50L;
        GroupActionNotificationsRequest request = new GroupActionNotificationsRequest(
                RequestType.REQUEST_NEW_GROUP_APPROVED, 0L, groupId);
        NotificationTranslator sut = new PendingGroupApprovedTranslator(groupMapper,
                groupCoordinatorMapper);
        final DomainGroupModelView group = context.mock(DomainGroupModelView.class);

        context.checking(new Expectations()
        {
            {
                allowing(groupMapper).execute(groupId);
                will(returnValue(group));
                allowing(groupCoordinatorMapper).execute(groupId);
                will(returnValue(Arrays.asList(4L, 2L)));
            }
        });

        NotificationBatch results = sut.translate(request);

        context.assertIsSatisfied();

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.REQUEST_NEW_GROUP_APPROVED, 2L, 4L);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(1, props.size());
        PropertyMapTestHelper.assertValue(props, "group", group);
    }
}
