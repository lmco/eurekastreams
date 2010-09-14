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
package org.eurekastreams.server.action.execution.feed;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.actions.Action;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.feed.DeleteFeedSubscriptionRequest;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.db.GetFeedByUrlOrCreateMapper;
import org.eurekastreams.server.persistence.mappers.db.GetFeedSubscriberOrCreateMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedByUrlRequest;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedSubscriberRequest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.GetEntityIdForFeedSubscription;

/**
 * Adds a feed to a user or group. Also handles updates, since the conf settings are cleared and reset regardless and
 * the mappers handle finding and creating for me. Booya.
 *
 */
public class AddFeedToEntityExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Update mapper.
     */
    private UpdateMapper<Feed> updateMapper;
    /**
     * Get feed mapper.
     */
    private GetFeedByUrlOrCreateMapper getMapper;

    /**
     * Person mapper.
     */
    private GetEntityIdForFeedSubscription getEntityId;

    /**
     * Get feed subscriber mapper.
     */
    private GetFeedSubscriberOrCreateMapper getFeedSubscriberMapper;

    /**
     * Get the title for a feed.
     */
    private ExecutionStrategy getTitleFromFeed;

    /**
     * Delete the existing feed sub is this is an edit.
     */
    private Action deleteFeedSub;

    /**
     * The entity type.
     */
    private EntityType type;

    /**
     * The get activity executor.
     */
    private TaskHandlerExecutionStrategy postActivityExecutor;

    /**
     * Default constructor.
     *
     * @param inUpdateMapper
     *            update mapper.
     * @param inGetMapper
     *            mapper to get the feed or create it if it doesnt exist.
     * @param inGetEntityId
     *            get entity mapper.
     * @param inGetFeedSubscriberMapper
     *            get feed sub mapper.
     * @param inGetTitleFromFeed
     *            get the title from a feed.
     * @param inDeleteFeedSub
     *            delete feed sub mapper.
     * @param inType
     *            entity type.
     * @param inPostActivityExecutor
     *            post executor.
     */
    @SuppressWarnings("unchecked")
    public AddFeedToEntityExecution(final UpdateMapper<Feed> inUpdateMapper,
            final GetFeedByUrlOrCreateMapper inGetMapper, final GetEntityIdForFeedSubscription inGetEntityId,
            final GetFeedSubscriberOrCreateMapper inGetFeedSubscriberMapper,
            final ExecutionStrategy inGetTitleFromFeed, final Action inDeleteFeedSub, final EntityType inType,
            final TaskHandlerExecutionStrategy inPostActivityExecutor)
    {
        updateMapper = inUpdateMapper;
        getMapper = inGetMapper;
        getEntityId = inGetEntityId;
        getFeedSubscriberMapper = inGetFeedSubscriberMapper;
        getTitleFromFeed = inGetTitleFromFeed;
        deleteFeedSub = inDeleteFeedSub;
        type = inType;
        postActivityExecutor = inPostActivityExecutor;
    }

    /**
     * Perform action.
     *
     * @param context
     *            The ActionContext for this execution
     * @return Serializable result
     */
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> context)
    {
        HashMap<String, Serializable> values = (HashMap<String, Serializable>) context.getActionContext().getParams();

        ValidationException exception = new ValidationException();

        // Loop through and see if any key/values are required through the use of the
        // REQUIRED: prefix.
        for (String key : values.keySet())
        {
            if (key.contains("REQUIRED:"))
            {
                String keyLookup = key.split("REQUIRED:")[1];
                if (!values.containsKey(keyLookup) || values.get(keyLookup) == null
                        || (values.get(keyLookup) instanceof String && ((String) values.get(keyLookup)).equals("")))
                {
                    exception.addError(keyLookup, (String) values.get(key));
                }
            }
        }

        // Make sure they checked the TOS.
        if (!(Boolean) values.get("EUREKA:TOS"))
        {
            exception.addError("EUREKA:TOS", "In order to use this plugin you must agree to the terms of use.");
        }

        // If you found any errors, error.
        if (exception.getErrors().size() > 0)
        {
            throw exception;
        }

        String group = "";
        if (values.containsKey("EUREKA:GROUP"))
        {
            group = (String) values.get("EUREKA:GROUP");
        }
        Principal principal = context.getActionContext().getPrincipal();
        if (values.containsKey("EUREKA:FEEDSUBID"))
        {
            ServiceActionContext deleteAC =
                    new ServiceActionContext(new DeleteFeedSubscriptionRequest((Long) values.get("EUREKA:FEEDSUBID"),
                            group), principal);
            deleteFeedSub.getExecutionStrategy().execute(deleteAC);
        }

        // Put the user in the values hash map so it's on equal footing for the strategies.
        values.put("EUREKA:USER", principal.getAccountId());

        // TODO BUG: context does not contain the right parameters for this execution strategy.
        // Perhaps a new request object needs to be created? Moving on for now.
        ServiceActionContext getFeedContext = new ServiceActionContext(values.get("EUREKA:FEEDURL"), principal);
        String title = (String) getTitleFromFeed.execute(getFeedContext);

        // Find or create the feed.
        GetFeedByUrlRequest request =
                new GetFeedByUrlRequest((Long) values.get("EUREKA:PLUGINID"), (String) values.get("EUREKA:FEEDURL"));
        Feed feed = getMapper.execute(request);
        feed.setTitle(title);

        // And now find or create the subscriber.
        FeedSubscriber feedSubscriber =
                getFeedSubscriberMapper.execute(new GetFeedSubscriberRequest(feed.getId(), getEntityId
                        .getEntityId(values), type, principal.getId()));

        // Add any non system key/value pairs to the feed subscribers conf settings.
        feedSubscriber.getConfSettings().clear();

        for (String key : values.keySet())
        {
            if (!key.contains("REQUIRED:") && !key.contains("EUREKA:"))
            {
                feedSubscriber.getConfSettings().put(key, values.get(key));
            }
        }

        ActivityDTO activity = new ActivityDTO();
        HashMap<String, String> props = new HashMap<String, String>();
        activity.setBaseObjectProperties(props);
        String content = "%EUREKA:ACTORNAME% configured the " + values.get("EUREKA:PLUGINTITLE") + " stream plugin";
        activity.getBaseObjectProperties().put("content", content);

        StreamEntityDTO destination = new StreamEntityDTO();
        if (type.equals(EntityType.PERSON))
        {
            destination.setUniqueIdentifier(principal.getAccountId());
        }
        else
        {
            destination.setUniqueIdentifier(group);
        }
        destination.setType(type);
        activity.setDestinationStream(destination);
        activity.setBaseObjectType(BaseObjectType.NOTE);
        activity.setVerb(ActivityVerb.POST);

        postActivityExecutor.execute(new TaskHandlerActionContext<PrincipalActionContext>(new ServiceActionContext(
                new PostActivityRequest(activity), principal), context.getUserActionRequests()));

        updateMapper.execute(new PersistenceRequest<Feed>(feed));
        return Boolean.TRUE;
    }
}
