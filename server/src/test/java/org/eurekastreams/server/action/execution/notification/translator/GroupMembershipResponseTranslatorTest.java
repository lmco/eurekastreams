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
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.notification.GroupMembershipResponseNotificationsRequest;
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
 * Tests GroupMembershipResponseTranslator.
 */
public class GroupMembershipResponseTranslatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long GROUP_ID = 2222L;

    /** Test data. */
    private static final long REQUESTOR_ID = 150L;

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** DAO to get the person's account id. */
    private final DomainMapper<Long, String> idToUniqueIdDAO = context.mock(DomainMapper.class);

    /**
     * Test creating the notification for a group membership request approval.
     */
    @Test
    public void testTranslateApproved()
    {
        NotificationTranslator<GroupMembershipResponseNotificationsRequest> sut = new GroupMembershipResponseTranslator(
                NotificationType.REQUEST_GROUP_ACCESS_APPROVED, idToUniqueIdDAO);

        context.checking(new Expectations()
        {
            {
                allowing(idToUniqueIdDAO).execute(GROUP_ID);
                will(returnValue("somegroup"));
            }
        });

        NotificationBatch results = sut.translate(new GroupMembershipResponseNotificationsRequest(
                RequestType.REQUEST_GROUP_ACCESS_APPROVED, ACTOR_ID, GROUP_ID, REQUESTOR_ID));

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.REQUEST_GROUP_ACCESS_APPROVED, REQUESTOR_ID);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(4, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "group", DomainGroupModelView.class, GROUP_ID);
        PropertyMapTestHelper.assertAlias(props, NotificationPropertyKeys.ACTOR, "group");
        PropertyMapTestHelper.assertAlias(props, NotificationPropertyKeys.SOURCE, "group");
        PropertyMapTestHelper.assertValue(props, NotificationPropertyKeys.URL, "#activity/group/somegroup");
    }

    /**
     * Test creating the notification for a group membership request denial.
     */
    @Test
    public void testTranslateDenial()
    {
        NotificationTranslator<GroupMembershipResponseNotificationsRequest> sut = new GroupMembershipResponseTranslator(
                NotificationType.REQUEST_GROUP_ACCESS_DENIED, idToUniqueIdDAO);

        context.checking(new Expectations()
        {
            {
                allowing(idToUniqueIdDAO).execute(GROUP_ID);
                will(returnValue("somegroup"));
            }
        });

        NotificationBatch results = sut.translate(new GroupMembershipResponseNotificationsRequest(
                RequestType.REQUEST_GROUP_ACCESS_DENIED, ACTOR_ID, GROUP_ID, REQUESTOR_ID));

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.REQUEST_GROUP_ACCESS_DENIED, REQUESTOR_ID);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(3, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "group", DomainGroupModelView.class, GROUP_ID);
        PropertyMapTestHelper.assertAlias(props, NotificationPropertyKeys.ACTOR, "group");
        PropertyMapTestHelper.assertAlias(props, NotificationPropertyKeys.SOURCE, "group");
    }
}
