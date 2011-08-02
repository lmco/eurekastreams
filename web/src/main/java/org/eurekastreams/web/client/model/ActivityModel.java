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
package org.eurekastreams.web.client.model;

import java.util.HashMap;

import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.DeletedActivityResponseEvent;
import org.eurekastreams.web.client.events.data.GotActivityResponseEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;

/**
 * Activity Model.
 * 
 */
public class ActivityModel extends BaseModel implements Fetchable<Long>, Deletable<Long>,
        Insertable<PostActivityRequest>
{
    /** Singleton. */
    private static ActivityModel model = new ActivityModel();

    /** Action keys used to post an activity per recipient type. */
    private final HashMap<EntityType, String> postActivityActionKeysByType = new HashMap<EntityType, String>();

    /** Event bus. */
    private final EventBus eventBus;

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static ActivityModel getInstance()
    {
        return model;
    }

    /**
     * Constructor.
     */
    public ActivityModel()
    {
        eventBus = Session.getInstance().getEventBus();
        postActivityActionKeysByType.put(EntityType.GROUP, "postGroupActivityServiceActionTaskHandler");
        postActivityActionKeysByType.put(EntityType.PERSON, "postPersonActivityServiceActionTaskHandler");
        postActivityActionKeysByType.put(EntityType.RESOURCE, "postResourceActivityServiceActionTaskHandler");
    }

    /**
     * Retrieves a list of activities for the org.
     * 
     * @param inRequest
     *            Request.
     * @param inUseClientCacheIfAvailable
     *            If ok to return cached results.
     */
    public void fetch(final Long inRequest, final boolean inUseClientCacheIfAvailable)
    {
        super.callReadAction("getActivityById", inRequest, new OnSuccessCommand<ActivityDTO>()
        {
            public void onSuccess(final ActivityDTO response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotActivityResponseEvent(response));
            }
        }, new OnFailureCommand()
        {
            public void onFailure(final Throwable inEx)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotActivityResponseEvent(null));
            }
        }, inUseClientCacheIfAvailable);
    }

    /**
     * Deletes an activity.
     * 
     * @param request
     *            Activity id.
     */
    public void delete(final Long request)
    {
        super.callWriteAction("deleteActivityAction", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                eventBus.notifyObservers(new DeletedActivityResponseEvent(request));
            }
        });
    }

    /**
     * Hides an activity.
     * 
     * @param request
     *            Activity id.
     */
    public void hide(final Long request)
    {
        super.callWriteAction("hideResourceActivity", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                eventBus.notifyObservers(new DeletedActivityResponseEvent(request));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final PostActivityRequest inRequest)
    {
        super.callWriteAction(postActivityActionKeysByType.get(inRequest.getActivityDTO().getDestinationStream()
                .getType()), inRequest, new OnSuccessCommand<ActivityDTO>()
        {
            public void onSuccess(final ActivityDTO result)
            {
                eventBus.notifyObservers(new MessageStreamAppendEvent(result));
            }
        }, new OnFailureCommand()
        {
            public void onFailure(final Throwable inEx)
            {
                eventBus.notifyObservers(new ShowNotificationEvent(new Notification(
                        inEx instanceof AuthorizationException ? "Not allowed to post to this stream."
                                : "Error posting to stream.")));
            }
        });
    }
}
