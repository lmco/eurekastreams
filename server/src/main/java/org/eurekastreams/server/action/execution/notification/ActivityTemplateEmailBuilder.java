/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.notification;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Notification email builder specifically for emails regarding activities which contain the text of the post. Looks up
 * the activity and selects a template email builder based on the activity base object type. (Author's note: I don't
 * like this design. I'd rather use Apache Velocity to allow the template itself to contain conditionals.)
 */
public class ActivityTemplateEmailBuilder implements NotificationEmailBuilder
{
    /** Key for the content (requires filtering). */
    private static final String CONTENT_KEY = "content";

    /** Tag to replace. */
    private static final String REPLACEMENT_TAG = "%EUREKA:ACTORNAME%";

    /** List of builders to choose from. */
    private Map<BaseObjectType, TemplateEmailBuilder> builders;

    /** For getting activity info. */
    private DomainMapper<List<Long>, List<ActivityDTO>> activitiesMapper;

    /**
     * Constructor.
     *
     * @param inActivitiesMapper
     *            For getting activity info.
     * @param inBuilders
     *            List of builders per activity type.
     */
    public ActivityTemplateEmailBuilder(final DomainMapper<List<Long>, List<ActivityDTO>> inActivitiesMapper,
            final Map<BaseObjectType, TemplateEmailBuilder> inBuilders)
    {
        super();
        activitiesMapper = inActivitiesMapper;
        builders = inBuilders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void build(final NotificationDTO inNotification, final MimeMessage inMessage) throws Exception
    {
        TemplateEmailBuilder builder = builders.get(inNotification.getActivityType());
        if (builder == null)
        {
            throw new Exception("No email builder for activity type " + inNotification.getActivityType()
                    + " (encountered on activity " + inNotification.getActivityId() + ")");
        }

        // retrieve activity
        ActivityDTO activity =
                activitiesMapper.execute(Collections.singletonList(inNotification.getActivityId())).get(0);
        if (activity == null)
        {
            throw new Exception("Cannot retrieve activity " + inNotification.getActivityId());
        }

        // invoke builder with activity details
        Map<String, String> properties = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : activity.getBaseObjectProperties().entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();
            if (CONTENT_KEY.equals(key))
            {
                value = value.replace(REPLACEMENT_TAG, activity.getActor().getDisplayName());
            }
            properties.put("activity." + key, value);
        }
        builder.build(inNotification, properties, inMessage);
    }
}
