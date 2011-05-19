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

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests the new group request translator.
 */
public class RequestNewGroupTranslatorTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Tests translation.
     */
    @Test
    public void testTranslate()
    {
        final long coord1id = 1001L;
        final long coord2id = 1002L;
        final String groupName = "The Name of My Group";
        final long groupId = 2000L;
        final long orgId = 3000L;
        final long actorId = 4000L;

        final List<Long> admins = new ArrayList<Long>();
        admins.add(coord1id);
        admins.add(coord2id);

        final DomainGroup group = new DomainGroup();
        group.setName(groupName);

        final DomainMapper<Serializable, List<Long>> systemAdminIdsMapper = context.mock(DomainMapper.class,
                "SystemAdminIdsMapper");

        // expectations
        final FindByIdMapper<DomainGroup> groupMapper = context.mock(FindByIdMapper.class);
        final FindByIdRequest mapperRequest = new FindByIdRequest("DomainGroup", groupId);
        context.checking(new Expectations()
        {
            {
                allowing(groupMapper).execute(with(equalInternally(mapperRequest)));
                will(returnValue(group));

                oneOf(systemAdminIdsMapper).execute(null);
                will(returnValue(admins));
            }
        });

        RequestNewGroupTranslator sut = new RequestNewGroupTranslator(groupMapper, systemAdminIdsMapper);

        CreateNotificationsRequest request = new CreateNotificationsRequest(null, actorId, 0L, groupId);
        NotificationBatch results = sut.translate(request);

        context.assertIsSatisfied();

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.REQUEST_NEW_GROUP, admins);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(2, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, actorId);
        PropertyMapTestHelper.assertValue(props, "group", group);
    }

}
