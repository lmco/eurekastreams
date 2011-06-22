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
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.request.notification.TargetEntityNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests FollowGroupTranslator.
 */
public class FollowGroupTranslatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long GROUP_FOLLOWED_ID = 1L;

    /** Test data. */
    private static final long COORDINATOR1_ID = 42;

    /** Test data. */
    private static final long COORDINATOR2_ID = 98;

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** DAO to get group coordinator ids. */
    private final DomainMapper<Long, List<Long>> coordinatorDAO = context.mock(DomainMapper.class, "coordinatorDAO");

    /** System under test. */
    private NotificationTranslator<TargetEntityNotificationsRequest> sut;

    /**
     * Setup test.
     */
    @Before
    public void setup()
    {
        sut = new FollowGroupTranslator(coordinatorDAO);
    }


    /**
     * Test translating.
     */
    @Test
    public void testTranslateFollowGroup()
    {
        final List<Long> coordinators = Arrays.asList(COORDINATOR1_ID, COORDINATOR2_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(coordinatorDAO).execute(GROUP_FOLLOWED_ID);
                will(returnValue(coordinators));
            }
        });

        NotificationBatch results = sut.translate(new TargetEntityNotificationsRequest(null, ACTOR_ID,
                GROUP_FOLLOWED_ID));

        context.assertIsSatisfied();

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper
                .assertRecipients(results, NotificationType.FOLLOW_GROUP, COORDINATOR1_ID, COORDINATOR2_ID);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(3, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, ACTOR_ID);
        PropertyMapTestHelper.assertPlaceholder(props, "stream", DomainGroupModelView.class, GROUP_FOLLOWED_ID);
        PropertyMapTestHelper.assertAlias(props, "source", "stream");
    }

    /**
     * Test translating.
     */
    @Test
    public void testTranslateFollowGroupByCoordinator()
    {
        final List<Long> coordinators = Arrays.asList(COORDINATOR1_ID, ACTOR_ID, COORDINATOR2_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(coordinatorDAO).execute(GROUP_FOLLOWED_ID);
                will(returnValue(coordinators));
            }
        });

        NotificationBatch results = sut.translate(new TargetEntityNotificationsRequest(null, ACTOR_ID,
                GROUP_FOLLOWED_ID));

        context.assertIsSatisfied();
        assertNull(results);
    }
}
