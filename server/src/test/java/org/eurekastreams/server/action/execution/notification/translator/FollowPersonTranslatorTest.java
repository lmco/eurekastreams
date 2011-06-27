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

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.TargetEntityNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;


/**
 * Tests the follower notification translator.
 */
public class FollowPersonTranslatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long FOLLOWED_ID = 2222L;

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
     * Test creating the notification for the event of following a person.
     */
    @Test
    public void testTranslateFollowPerson()
    {
        NotificationTranslator<TargetEntityNotificationsRequest> sut = new FollowPersonTranslator(idToUniqueIdDAO);

        context.checking(new Expectations()
        {
            {
                allowing(idToUniqueIdDAO).execute(ACTOR_ID);
                will(returnValue("somebody"));
            }
        });

        NotificationBatch results = sut.translate(new TargetEntityNotificationsRequest(null, ACTOR_ID, FOLLOWED_ID));

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.FOLLOW_PERSON, FOLLOWED_ID);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(4, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, ACTOR_ID);
        PropertyMapTestHelper.assertPlaceholder(props, "stream", PersonModelView.class, FOLLOWED_ID);
        PropertyMapTestHelper.assertAlias(props, "source", "stream");
        PropertyMapTestHelper.assertValue(props, NotificationPropertyKeys.URL, "#activity/person/somebody");
    }
}
