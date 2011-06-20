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
package org.eurekastreams.server.action.request.transformer.notification;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.transformer.RequestTransformer;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.requests.SetUserNotificationFilterPreferencesRequest;
import org.eurekastreams.server.testing.TestContextCreator;
import org.junit.Test;

/**
 * Tests SetCurrentUserNotificationPreferencesTransformer.
 */
public class SetCurrentUserNotificationPreferencesTransformerTest
{
    /** Test data. */
    private static final long PERSON_ID = 88L;

    /** Test data. */
    private final NotificationFilterPreferenceDTO dto1 = new NotificationFilterPreferenceDTO(0, "EMAIL", "COMMENT");

    /** Test data - notifier not in map. */
    private final NotificationFilterPreferenceDTO dto2 = new NotificationFilterPreferenceDTO(0, "SMS", "COMMENT");

    /** Test data - category not in set. */
    private final NotificationFilterPreferenceDTO dto3 = new NotificationFilterPreferenceDTO(0, "EMAIL", "OLD");

    /** Fixture: notifier map. */
    private final Map<String, String> notifierTypes = new HashMap<String, String>()
    {
        {
            put("EMAIL", "Email");
            put("IM", "Instant Message");
        }
    };

    /** Fixture: category set. */
    private final Set<String> categories = Collections.singleton("COMMENT");

    /**
     * Tests transform.
     */
    @Test
    public void testTransform()
    {
        RequestTransformer sut = new SetCurrentUserNotificationPreferencesTransformer(notifierTypes, categories);
        ActionContext ctx = TestContextCreator.createPrincipalActionContext(
                (Serializable) Arrays.asList(dto1, dto2, dto3), null, PERSON_ID);
        SetUserNotificationFilterPreferencesRequest result = (SetUserNotificationFilterPreferencesRequest) sut
                .transform(ctx);

        assertEquals(PERSON_ID, result.getPersonId());
        Collection<NotificationFilterPreferenceDTO> list = result.getPrefList();
        assertEquals(1, list.size());
        NotificationFilterPreferenceDTO dto = list.iterator().next();
        assertEquals("EMAIL", dto.getNotifierType());
        assertEquals("COMMENT", dto.getNotificationCategory());
    }
}
