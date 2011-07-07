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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.TargetEntityNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the group access request notification translator.
 *
 */
public class RequestGroupAccessTranslatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long GROUP_ID = 102L;

    /** Test data. */
    private static final long COORDINATOR1_ID = 42;

    /** Test data. */
    private static final long COORDINATOR2_ID = ACTOR_ID;

    /** Test data. */
    private static final long COORDINATOR3_ID = 98;

    /** System under test. */
    private NotificationTranslator<TargetEntityNotificationsRequest> sut;

    /** Context for building mock objects. */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mock coordinator mapper. */
    private final DomainMapper<Long, List<Long>> coordinatorsDAO = context.mock(DomainMapper.class, "coordinatorsDAO");

    /** DAO to get the group's unique id. */
    private final DomainMapper<Long, String> idToUniqueIdDAO = context.mock(DomainMapper.class, "idToUniqueIdDAO");

    /**
     * Setup test.
     */
    @Before
    public void setup()
    {
        sut = new RequestGroupAccessTranslator(coordinatorsDAO, idToUniqueIdDAO);
    }

    /**
     * Test creating the notification for the event of following a person.
     */
    @Test
    public void testTranslate()
    {
        final List<Long> coordinators = new ArrayList<Long>();
        coordinators.add(COORDINATOR1_ID);
        coordinators.add(COORDINATOR3_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(coordinatorsDAO).execute(GROUP_ID);
                will(returnValue(coordinators));

                allowing(idToUniqueIdDAO).execute(GROUP_ID);
                will(returnValue("somegroup"));
            }
        });

        NotificationBatch results = sut.translate(new TargetEntityNotificationsRequest(null, ACTOR_ID, GROUP_ID));

        context.assertIsSatisfied();

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.REQUEST_GROUP_ACCESS, COORDINATOR1_ID,
                COORDINATOR3_ID);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(5, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, ACTOR_ID);
        PropertyMapTestHelper.assertPlaceholder(props, "group", DomainGroupModelView.class, GROUP_ID);
        PropertyMapTestHelper.assertValue(props, NotificationPropertyKeys.HIGH_PRIORITY, true);
        PropertyMapTestHelper.assertAlias(props, NotificationPropertyKeys.SOURCE, "group");
        PropertyMapTestHelper.assertSetNonNull(props, NotificationPropertyKeys.URL);
    }
}
