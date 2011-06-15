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
package org.eurekastreams.server.action.execution.notification.inapp;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.InAppNotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.GetItemsByPointerIdsMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetItemsByPointerIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.testing.TestContextCreator;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests GetInAppNotificationsExecution.
 */
public class GetInAppNotificationsExecutionTest
{
    /** Test data. */
    private static final long USER_ID = 77L;

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mapper to get in-app notifs. */
    private final BaseArgDomainMapper alertMapper = context.mock(BaseArgDomainMapper.class, "alertMapper");

    /** Mapper to get persons. */
    private final GetItemsByPointerIdsMapper personsMapper = context.mock(GetItemsByPointerIdsMapper.class,
            "personsMapper");

    /** Mapper to get groups. */
    private final GetItemsByPointerIds groupsMapper = context.mock(GetItemsByPointerIds.class, "groupsMapper");

    /** Type-to-category mapping. */
    private final Map<NotificationType, String> notificationTypeCategories = new HashMap<NotificationType, String>();

    /** SUT. */
    private ExecutionStrategy sut;

    /**
     * Constructor.
     */
    public GetInAppNotificationsExecutionTest()
    {
        notificationTypeCategories.put(NotificationType.COMMENT_TO_PERSONAL_POST, "COMMENT");
        notificationTypeCategories.put(NotificationType.COMMENT_TO_COMMENTED_POST, "COMMENT");
        notificationTypeCategories.put(NotificationType.FOLLOW_PERSON, "FOLLOW");
        notificationTypeCategories.put(NotificationType.FOLLOW_GROUP, "FOLLOW");
    }

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetInAppNotificationsExecution(alertMapper, personsMapper, groupsMapper, notificationTypeCategories);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        final InAppNotificationDTO notif1 = new InAppNotificationDTO();
        notif1.setAvatarOwnerType(EntityType.PERSON);
        notif1.setAvatarOwnerUniqueId("knownperson");
        notif1.setNotificationType(NotificationType.COMMENT_TO_PERSONAL_POST);
        final InAppNotificationDTO notif2 = new InAppNotificationDTO();
        notif2.setAvatarOwnerType(EntityType.GROUP);
        notif2.setAvatarOwnerUniqueId("knowngroup");
        notif2.setNotificationType(NotificationType.REQUEST_GROUP_ACCESS);
        final InAppNotificationDTO notif3 = new InAppNotificationDTO();
        notif3.setAvatarOwnerType(EntityType.NOTSET);
        notif3.setNotificationType(NotificationType.FOLLOW_GROUP);
        final InAppNotificationDTO notif4 = new InAppNotificationDTO();
        notif4.setAvatarOwnerType(EntityType.PERSON);
        notif4.setAvatarOwnerUniqueId("unknownperson");
        notif4.setNotificationType(NotificationType.PASS_THROUGH);
        final InAppNotificationDTO notif5 = new InAppNotificationDTO();
        notif5.setAvatarOwnerType(EntityType.GROUP);
        notif5.setAvatarOwnerUniqueId("avatarlessgroup");
        notif5.setNotificationType(NotificationType.COMMENT_TO_COMMENTED_POST);

        final PersonModelView person = new PersonModelView();
        person.setAccountId("knownperson");
        person.setAvatarId("avatar1");
        final DomainGroupModelView group = new DomainGroupModelView();
        group.setShortName("knowngroup");
        group.setAvatarId("avatar2");
        final DomainGroupModelView group2 = new DomainGroupModelView();
        group2.setShortName("avatarlessgroup");
        group2.setAvatarId(null);

        context.checking(new Expectations()
        {
            {
                oneOf(alertMapper).execute(USER_ID);
                will(returnValue(Arrays.asList(notif1, notif2, notif3, notif4, notif5)));

                oneOf(personsMapper).execute((List<String>) with(Matchers.hasItems("knownperson", "unknownperson")));
                will(returnValue(Collections.singletonList(person)));

                oneOf(groupsMapper).execute((List<String>) with(Matchers.hasItems("knowngroup", "avatarlessgroup")));
                will(returnValue(Arrays.asList(group, group2)));
            }
        });

        PrincipalActionContext ctx = TestContextCreator.createPrincipalActionContext(null, "whomever", USER_ID);

        List<InAppNotificationDTO> result = (List<InAppNotificationDTO>) sut.execute(ctx);

        context.assertIsSatisfied();

        assertEquals(5, result.size());

        assertEquals("COMMENT", result.get(0).getFilterCategory());
        assertNull(result.get(1).getFilterCategory());
        assertEquals("FOLLOW", result.get(2).getFilterCategory());
        assertNull(result.get(3).getFilterCategory());
        assertEquals("COMMENT", result.get(4).getFilterCategory());

        assertEquals("avatar1", result.get(0).getAvatarId());
        assertEquals("avatar2", result.get(1).getAvatarId());
        assertNull(result.get(2).getAvatarId());
        assertNull(result.get(3).getAvatarId());
        assertNull(result.get(4).getAvatarId());
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteEmpty()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(alertMapper).execute(USER_ID);
                will(returnValue(Collections.EMPTY_LIST));
            }
        });
        PrincipalActionContext ctx = TestContextCreator.createPrincipalActionContext(null, "whomever", USER_ID);

        Collection<InAppNotificationDTO> result = (Collection<InAppNotificationDTO>) sut.execute(ctx);

        context.assertIsSatisfied();
        assertTrue(result.isEmpty());
    }
}
